package pl.edu.pw.sgalazka.inz.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;

public class TypeProductBarcode extends Activity {

    private EditText amount;
    private EditText barcode;
    private Button ok;

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
                if(checkAmount() && checkBarcode()){
                    String dataToSend = "B:" + barcode.getText() + ":" + amount.getText();
                    ClientBluetooth.toSend.add(dataToSend);
                    TypeProductBarcode.this.finish();
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
}
