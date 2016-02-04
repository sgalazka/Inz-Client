package pl.edu.pw.sgalazka.inz.productList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.bluetooth.server.ServerBluetooth;

public class ProductList extends Activity implements ListDataReceiver{

    private ListView listView;
    private Dialog dialog;
    private EditText title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        listView = (ListView) findViewById(R.id.productList);
        title = (EditText) findViewById(R.id.productList_title);

        Intent intent = getIntent();
        int quantity = intent.getIntExtra("quantity", 0);

        dialog = ProgressDialog.show(ProductList.this, "Łączenie z serwerem", "Proszę czekać...", true);
        ServerBluetooth.setListDataReciver(this);
        ClientBluetooth.toSend.add(ListDataReceiver.LIST_DATA_CODE + ":" + quantity);

        /*List<ProductListRow> list = new LinkedList<>();


        list.add(new ProductListRow(getApplicationContext(), "NazwaNazwaNa1", "5900000000008", "12"));
        list.add(new ProductListRow(getApplicationContext(), "NazwaNazwaNa2", "5900000000009", "122"));
        list.add(new ProductListRow(getApplicationContext(), "NazwaNazwaNa3", "5900000000010", "45"));

        ProductListRow tab[] = new ProductListRow[list.size()];

        for (int i = 0; i < list.size(); i++) {
            tab[i] = list.get(i);
        }

        ProductListRowAdapter adapter = new ProductListRowAdapter(getApplicationContext(),
                R.layout.product_list_row, tab);

        listView.setAdapter(adapter);*/
    }


    @Override
    public void onListDataReceive(String data) {
        String tmp[] = data.split(":");
        String tab[] = tmp[2].split(";");
        ProductListRow products[] = new ProductListRow[tab.length];

        for (int i=0; i<tab.length; i++){
            String tmpData[] = tab[i].split("#");
            products[i] = new ProductListRow(getApplicationContext(), tmpData[0], tmpData[1], tmpData[2]);
        }

        ProductListRowAdapter adapter = new ProductListRowAdapter(getApplicationContext(),
                R.layout.product_list_row, products);

        listView.setAdapter(adapter);

        /*String titleTmp = title.getText().toString()+" "+tmp[1];
        title.setText(titleTmp);*/

        dialog.dismiss();
    }
}
