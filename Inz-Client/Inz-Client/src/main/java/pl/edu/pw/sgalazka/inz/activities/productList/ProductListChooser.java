package pl.edu.pw.sgalazka.inz.activities.productList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pl.edu.pw.sgalazka.inz.InzApplication;
import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.activities.ServerChooseDialog;
import pl.edu.pw.sgalazka.inz.utils.Utils;

public class ProductListChooser extends Activity {

    private EditText quantity;
    private Button showSelected;
    private Button showAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_chooser);

        quantity = (EditText) findViewById(R.id.plchooser_quantity);
        showAll = (Button) findViewById(R.id.plchooser_show_all);
        showSelected = (Button) findViewById(R.id.plchoooser_show_selected);

        showSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!InzApplication.isConnected())
                    new ServerChooseDialog(ProductListChooser.this).show();
                else if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(i, 1);
                } else if (Utils.checkQuantityProductList(quantity, ProductListChooser.this)) {
                    Intent intent = new Intent(getApplicationContext(), ProductList.class);
                    intent.putExtra("quantity", Integer.parseInt(quantity.getText().toString()));
                    startActivity(intent);
                }
            }
        });
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!InzApplication.isConnected())
                    new ServerChooseDialog(ProductListChooser.this).show();
                else if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(i, 1);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ProductList.class);
                    intent.putExtra("quantity", 0);
                    startActivity(intent);
                }
            }
        });
    }
}
