package pl.edu.pw.sgalazka.inz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.edu.pw.sgalazka.inz.bluetooth.SendBuffer;
import pl.edu.pw.sgalazka.inz.scanner.Scanner;

public class TypeProductBarcode extends Activity {

    private EditText amount;
    private EditText barcode;
    private Button ok;
    public final int TYPE_PRODUCT_BARCODE_REQUEST_CODE = 2;
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
                if(barcode.getText().toString().matches("")){
                    Toast.makeText(TypeProductBarcode.this, "Wprowadź kod kreskowy!", Toast.LENGTH_LONG).show();
                }
                else{
                    String dataToSend = "B" + barcode + ":" + amount.getText();
                    SendBuffer.toSend.add(dataToSend);
                    TypeProductBarcode.this.finish();
                }
            }
        });
    }
}
