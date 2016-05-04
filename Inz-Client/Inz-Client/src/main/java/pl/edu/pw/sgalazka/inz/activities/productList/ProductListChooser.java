package pl.edu.pw.sgalazka.inz.activities.productList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pl.edu.pw.sgalazka.inz.InzApplication;
import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.activities.ServerChooseDialog;

public class ProductListChooser extends Activity {

    private TextView quantity;
    private Button showSelected;
    private Button showAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_chooser);

        quantity = (TextView) findViewById(R.id.plchooser_quantity);
        showAll = (Button) findViewById(R.id.plchooser_show_all);
        showSelected = (Button) findViewById(R.id.plchoooser_show_selected);

        showSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!InzApplication.isConnected())
                    new ServerChooseDialog(ProductListChooser.this).show();
                else if (checkQuantity()) {
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
                else {
                    Intent intent = new Intent(getApplicationContext(), ProductList.class);
                    intent.putExtra("quantity", 0);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkQuantity() {
        if (quantity.getText().length() == 0) {
            Toast.makeText(ProductListChooser.this, "Wprowadź ilość", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
