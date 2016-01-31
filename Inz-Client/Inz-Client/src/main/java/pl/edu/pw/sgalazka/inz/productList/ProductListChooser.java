package pl.edu.pw.sgalazka.inz.productList;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pl.edu.pw.sgalazka.inz.R;

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
        showSelected= (Button) findViewById(R.id.plchoooser_show_selected);

        showSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!quantity.getText().toString().isEmpty()){

                    Intent intent = new Intent(getApplicationContext(), ProductList.class);
                    intent.putExtra("quantity", Integer.parseInt(quantity.getText().toString()));
                    startActivity(intent);
                }
            }
        });
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductList.class);
                intent.putExtra("quantity", 0);
                startActivity(intent);
            }
        });
    }
}
