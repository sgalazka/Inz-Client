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

public class AddToDatabase extends Activity {

    private EditText name;
    private EditText code;
    private EditText amount;
    private EditText barcode;
    private Button addBarCode;
    private Button add;
    public final int ADD_TO_DATABASE_REQUEST_CODE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_database);

        name = (EditText) findViewById(R.id.addToDatabaseName);
        code = (EditText) findViewById(R.id.addToDatabase_code);
        amount = (EditText) findViewById(R.id.addToDatabaseAmount);
        barcode = (EditText) findViewById(R.id.addToDatabaseBarcode);
        add = (Button) findViewById(R.id.addToDatabase_add);
        addBarCode = (Button) findViewById(R.id.addToDatabaseAddBarcode);


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
                String dataToSend = "D:" + name.getText() + ":" + barcode.getText() + ":" + code.getText()
                        + ":" + amount.getText();
                SendBuffer.toSend.add(dataToSend);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ADD_TO_DATABASE_REQUEST_CODE){
            String barCodeData = data.getStringExtra("barcode");
            barcode.setText(barCodeData);
            AddToDatabase.this.finish();
        }
    }
}
