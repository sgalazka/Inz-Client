package pl.edu.pw.sgalazka.inz.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import pl.edu.pw.sgalazka.inz.InzApplication;
import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.bluetooth.server.ServerBluetooth;
import pl.edu.pw.sgalazka.inz.utils.EAN13CheckDigit;
import pl.edu.pw.sgalazka.inz.scanner.Scanner;
import pl.edu.pw.sgalazka.inz.utils.Utils;

public class AddToDatabase extends Activity implements AddingResultCallback {

    private EditText name;
    private EditText price;
    private EditText amount;
    private EditText barcode;
    private Spinner vatGroup;
    private CheckBox isPackaging;
    private Button addBarCode;
    private Button add;
    private Dialog dialog = null;
    public final int ADD_TO_DATABASE_REQUEST_CODE = 3;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_database);

        name = (EditText) findViewById(R.id.addToDatabaseName);
        price = (EditText) findViewById(R.id.addToDatabase_price);
        amount = (EditText) findViewById(R.id.addToDatabaseAmount);
        barcode = (EditText) findViewById(R.id.addToDatabaseBarcode);
        add = (Button) findViewById(R.id.addToDatabase_add);
        addBarCode = (Button) findViewById(R.id.addToDatabaseAddBarcode);
        vatGroup = (Spinner) findViewById(R.id.addToDatabase_vat_spinner);
        isPackaging = (CheckBox) findViewById(R.id.is_packaging_checkbox);
        addButtonListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TO_DATABASE_REQUEST_CODE) {
            try {
                String barCodeData = data.getStringExtra("barcode");
                if (!EAN13CheckDigit.checkBarcode(barCodeData)) {
                    Utils.showRetryDialog(AddToDatabase.this, ADD_TO_DATABASE_REQUEST_CODE);
                    return;
                }
                barcode.setText(barCodeData);
            } catch (Exception e) {
                Utils.showRetryDialog(AddToDatabase.this, ADD_TO_DATABASE_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onAddingResult(String result) {
        String tmp[] = result.split(":");
        String message = null;
        boolean returnBack = false;
        if (tmp[0].equals("EEX")) {
            message = "W bazie już istnieje towar o kodzie " + tmp[2];
        } else if (tmp[0].equals("DAS")) {
            message = "Przesłano pomyślnie";
            returnBack = true;
        } else if (tmp[0].equals("STOP")) {
            message = "Połączenie zostało zerwane";
        }
        Utils.showInformDialog(message, returnBack, AddToDatabase.this, dialog);
    }

    /*private void showRetryDialog() {
        Runnable dialogShow = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddToDatabase.this);
                builder.setMessage("Błąd odczytu! Spróbuj jeszcze raz.")
                        .setPositiveButton("Skanuj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(), Scanner.class);
                                startActivityForResult(intent, ADD_TO_DATABASE_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        };
        AddToDatabase.this.runOnUiThread(dialogShow);
    }*/

    private void addButtonListeners() {
        addBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Scanner.class);
                startActivityForResult(intent, ADD_TO_DATABASE_REQUEST_CODE);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!InzApplication.isConnected())
                    new ServerChooseDialog(AddToDatabase.this).show();
                else if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(i, 1);
                } else if (Utils.checkName(name, AddToDatabase.this) && Utils.checkPrice(price, AddToDatabase.this)
                        && Utils.checkAmount(amount, AddToDatabase.this) && Utils.checkVat(vatGroup, AddToDatabase.this)
                        && Utils.checkBarcode(barcode, AddToDatabase.this)) {
                    StringBuilder stringBuilder = new StringBuilder("");
                    stringBuilder.append("D:");
                    stringBuilder.append(name.getText()).append(":");
                    stringBuilder.append(barcode.getText()).append(":");
                    stringBuilder.append(price.getText()).append(":");
                    stringBuilder.append(amount.getText()).append(":");
                    stringBuilder.append(vatGroup.getSelectedItem().toString()).append(":");
                    stringBuilder.append(isPackaging.isSelected());
                    dialog = ProgressDialog.show(AddToDatabase.this, "Wysyłanie danych", "Proszę czekać");
                    ServerBluetooth.setAddingResultCallback(AddToDatabase.this);
                    ClientBluetooth.toSend.add(stringBuilder.toString());
                }
            }
        });
    }
}
