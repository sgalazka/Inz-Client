package pl.edu.pw.sgalazka.inz.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.scanner.Scanner;

public class ScanProduct extends Activity {

    private EditText amount;
    private Button scan;
    private final int SCAN_PRODUCT_REQUEST_CODE = 1;

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
                String dataToSend = "B:" + barCode + ":" + amount.getText();
                ClientBluetooth.toSend.add(dataToSend);
            } catch (Exception e) {

                Runnable dialogShow = new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ScanProduct.this);
                        builder.setMessage("Błąd odczytu! Spróbuj jeszcze raz.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ScanProduct.this.finish();
                                    }
                                });
                        AlertDialog alert = builder.create();

                        alert.show();
                    }
                };
                ScanProduct.this.runOnUiThread(dialogShow);
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
}
