package pl.edu.pw.sgalazka.inz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pl.edu.pw.sgalazka.inz.bluetooth.SendBuffer;
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
                Intent intent = new Intent(getApplicationContext(), Scanner.class);
                startActivityForResult(intent, SCAN_PRODUCT_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SCAN_PRODUCT_REQUEST_CODE){
            String barCode = data.getStringExtra("barcode");
            String dataToSend = "B:"+ barCode + ":" + amount.getText();
            SendBuffer.toSend.add(dataToSend);
            ScanProduct.this.finish();
            // TODO: 2015-10-02 wysłać ilość przez bluetooth
        }
    }
}
