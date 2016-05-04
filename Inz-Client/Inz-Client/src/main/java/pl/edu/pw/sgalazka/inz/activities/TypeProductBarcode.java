package pl.edu.pw.sgalazka.inz.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.edu.pw.sgalazka.inz.InzApplication;
import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.bluetooth.server.ServerBluetooth;
import pl.edu.pw.sgalazka.inz.scanner.EAN13CheckDigit;

public class TypeProductBarcode extends Activity implements ScannerResultCallback {

    private EditText amount;
    private EditText barcode;
    private Button ok;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_product_barcode);

        amount = (EditText) findViewById(R.id.typeProductBarcodeAmount);
        barcode = (EditText) findViewById(R.id.typeProductBarcode_barcode);
        ok = (Button) findViewById(R.id.typeProductBarcodeOk);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!InzApplication.isConnected())
                    new ServerChooseDialog(TypeProductBarcode.this).show();
                else if(checkAmount() && checkBarcode()){
                    String dataToSend = "B:" + barcode.getText() + ":" + amount.getText();
                    ServerBluetooth.setScannerResultCallback(TypeProductBarcode.this);
                    ClientBluetooth.toSend.add(dataToSend);
                    dialog = ProgressDialog.show(TypeProductBarcode.this, "Wysyłanie skanu", "Proszę czekać...", true);
                }
            }
        });
    }

    private boolean checkBarcode() {
        if (barcode.getText().length() == 0) {
            Toast.makeText(TypeProductBarcode.this, "Wprowadź kod kreskowy", Toast.LENGTH_SHORT).show();
            return false;
        } else if (barcode.getText().length() != 13) {
            Toast.makeText(TypeProductBarcode.this, "Kod kreskowy musi mieć 13 znaków", Toast.LENGTH_SHORT).show();
            return false;
        } else if(EAN13CheckDigit.checkBarcode(barcode.getText().toString())){
            Toast.makeText(TypeProductBarcode.this, "Kod kreskowy jest niepoprawny", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkAmount() {
        if (amount.getText().length() == 0) {
            Toast.makeText(TypeProductBarcode.this, "Wprowadź ilość", Toast.LENGTH_SHORT).show();
            return false;
        } else if (amount.getText().length() > 2) {
            Toast.makeText(TypeProductBarcode.this, "Ilość musi być mniejsza niż 100", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
            returnBack = true;
        }
        showInformDialog(message, returnBack);
    }

    private void showInformDialog(final String finalMessage, final boolean finalReturnBack) {
        Runnable dialogShow = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(TypeProductBarcode.this);
                builder.setMessage(finalMessage)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (finalReturnBack)
                                    TypeProductBarcode.this.finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        };
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        TypeProductBarcode.this.runOnUiThread(dialogShow);
    }
}
