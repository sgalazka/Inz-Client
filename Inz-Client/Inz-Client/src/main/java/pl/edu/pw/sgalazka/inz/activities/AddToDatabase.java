package pl.edu.pw.sgalazka.inz.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.scanner.Scanner;

public class AddToDatabase extends Activity {

    private EditText name;
    private EditText price;
    private EditText amount;
    private EditText barcode;
    private Spinner vatGroup;
    private CheckBox isPackaging;
    private Button addBarCode;
    private Button add;
    public final int ADD_TO_DATABASE_REQUEST_CODE = 3;
    private int count = -1;

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

        //addTextListeners();

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
                if (checkName() && checkBarcode() && checkPrice() && checkAmount() && checkVat()) {
                    StringBuilder stringBuilder = new StringBuilder("");
                    stringBuilder.append("D:");
                    stringBuilder.append(name.getText()).append(":");
                    stringBuilder.append(barcode.getText()).append(":");
                    stringBuilder.append(price.getText()).append(":");
                    stringBuilder.append(amount.getText()).append(":");
                    stringBuilder.append(vatGroup.getSelectedItem().toString()).append(":");
                    stringBuilder.append(isPackaging.isSelected());

                    ClientBluetooth.toSend.add(stringBuilder.toString());
                    AddToDatabase.this.finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TO_DATABASE_REQUEST_CODE) {
            try {
                String barCodeData = data.getStringExtra("barcode");
                barcode.setText(barCodeData);
            } catch (Exception e) {

            }
        }
    }

    private boolean checkName() {
        if (name.getText().length() == 0) {
            Toast.makeText(AddToDatabase.this, "Wprowadź nazwę", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name.getText().length() > 18) {
            Toast.makeText(AddToDatabase.this, "Nazwa musi miec mniej niż 19 znaków", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkBarcode() {
        if (barcode.getText().length() == 0) {
            Toast.makeText(AddToDatabase.this, "Wprowadź kod kreskowy", Toast.LENGTH_SHORT).show();
            return false;
        } else if (barcode.getText().length() != 13) {
            Toast.makeText(AddToDatabase.this, "Kod kreskowy musi mieć 13 znaków", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkPrice() {
        if (price.getText().length() == 0) {
            Toast.makeText(AddToDatabase.this, "Wprowadź cenę", Toast.LENGTH_SHORT).show();
            return false;
        } else if (price.getText().toString().contains(".")) {
            String tmp = price.getText().toString();
            if (tmp.length() - tmp.indexOf(".") > 3) {
                Toast.makeText(AddToDatabase.this, "Cena może mieć dwa miejsca po przecinku", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (price.getText().length() > 8) {
            Toast.makeText(AddToDatabase.this, "Za duża cena", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkAmount() {
        if (amount.getText().length() == 0) {
            Toast.makeText(AddToDatabase.this, "Wprowadź ilość", Toast.LENGTH_SHORT).show();
            return false;
        } else if (amount.getText().length() > 2) {
            Toast.makeText(AddToDatabase.this, "Ilość musi być mniejsza niż 100", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkVat() {
        if (vatGroup.getSelectedItem() == null) {
            Toast.makeText(AddToDatabase.this, "Wybierz grupę VAT", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
