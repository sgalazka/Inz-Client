package pl.edu.pw.sgalazka.inz.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.bluetooth.server.ServerBluetooth;
import pl.edu.pw.sgalazka.inz.scanner.EAN13CheckDigit;
import pl.edu.pw.sgalazka.inz.scanner.Scanner;

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
                if (checkQuantity()) {
                    Intent intent = new Intent(getApplicationContext(), Scanner.class);
                    startActivityForResult(intent, SCAN_PRODUCT_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_PRODUCT_REQUEST_CODE) {
            try {
                String barCode = data.getStringExtra("barcode");
                if (!EAN13CheckDigit.checkBarcode(barCode)) {
                    showRetryDialog();
                    return;
                }
                String dataToSend = "B:" + barCode + ":" + amount.getText();
                ServerBluetooth.setScannerResultCallback(this);
                ClientBluetooth.toSend.add(dataToSend);
                dialog = ProgressDialog.show(ScanProduct.this, "Wysyłanie skanu", "Proszę czekać...", true);
            } catch (Exception e) {
                showRetryDialog();
                e.printStackTrace();
            }
        }
    }

    private boolean checkQuantity() {
        if (amount.getText().length() == 0) {
            Toast.makeText(ScanProduct.this, "Wprowadź ilość", Toast.LENGTH_SHORT).show();
            return false;
        } else if (amount.getText().length() > 2) {
            Toast.makeText(ScanProduct.this, "Ilość musi być mniejsza od 100", Toast.LENGTH_SHORT).show();
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

    private void showRetryDialog() {
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
    }

    private void showInformDialog(final String finalMessage, final boolean finalReturnBack) {
        Runnable dialogShow = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanProduct.this);
                builder.setMessage(finalMessage)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (finalReturnBack)
                                    ScanProduct.this.finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        };
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        ScanProduct.this.runOnUiThread(dialogShow);
    }
}
