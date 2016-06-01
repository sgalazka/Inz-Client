package pl.edu.pw.sgalazka.client.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pl.edu.pw.sgalazka.client.InzApplication;
import pl.edu.pw.sgalazka.client.R;
import pl.edu.pw.sgalazka.client.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.client.bluetooth.server.ServerBluetooth;
import pl.edu.pw.sgalazka.client.utils.EAN13CheckDigit;
import pl.edu.pw.sgalazka.client.scanner.Scanner;
import pl.edu.pw.sgalazka.client.utils.Utils;

public class ScanProduct extends Activity implements ScannerResultCallback {

    private EditText amount;
    private Button scan;
    private final int SCAN_PRODUCT_REQUEST_CODE = 1;
    private Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_product);

        scan = (Button) findViewById(R.id.scan_product_scan);
        amount = (EditText) findViewById(R.id.scan_product_amount);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!InzApplication.isConnected())
                    new ServerChooseDialog(ScanProduct.this).show();
                else if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(i, 1);
                } else if (Utils.checkQuantity(amount, ScanProduct.this)) {
                    Intent intent = new Intent(getApplicationContext(), Scanner.class);
                    startActivityForResult(intent, SCAN_PRODUCT_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_PRODUCT_REQUEST_CODE) {
            if (!InzApplication.isConnected())
                new ServerChooseDialog(ScanProduct.this).show();
            else {
                Log.d("Scan Product", "state:" + InzApplication.isConnected());
                try {
                    String barCode = data.getStringExtra("barcode");
                    if (!EAN13CheckDigit.checkBarcode(barCode)) {
                        Utils.showRetryDialog(ScanProduct.this, SCAN_PRODUCT_REQUEST_CODE);
                        return;
                    }
                    String dataToSend = "B:" + barCode + ":" + amount.getText();
                    ServerBluetooth.setScannerResultCallback(this);
                    ClientBluetooth.toSend.add(dataToSend);
                    dialog = ProgressDialog.show(ScanProduct.this, "Wysyłanie skanu", "Proszę czekać...", true);
                } catch (Exception e) {
                    Utils.showRetryDialog(ScanProduct.this, SCAN_PRODUCT_REQUEST_CODE);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onScannerResult(String result) {
        String tmp[] = result.split(":");
        String message = null;
        boolean returnBack = false;
        if (tmp[0].equals("ENF")) {
            message = "W bazie nie istnieje towar o kodzie " + tmp[1];
        } else if (tmp[0].equals("EZQ")) {
            message = "Sprzedano ostatnią sztukę towaru o kodzie " + tmp[1];
            returnBack = true;
        } else if (tmp[0].equals("BSS")) {
            message = "Przesłano pomyślnie";
            returnBack = true;
        } else if (tmp[0].equals("STOP")) {
            message = "Połączenie zostało zerwane";
        }
        Utils.showInformDialog(message, returnBack, ScanProduct.this, dialog);
    }

    /*private void showRetryDialog() {
        Runnable dialogShow = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanProduct.this);
                builder.setMessage("Błąd odczytu! Spróbuj jeszcze raz.")
                        .setPositiveButton("Skanuj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(), Scanner.class);
                                startActivityForResult(intent, SCAN_PRODUCT_REQUEST_CODE);
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
        ScanProduct.this.runOnUiThread(dialogShow);
    }*/
}
