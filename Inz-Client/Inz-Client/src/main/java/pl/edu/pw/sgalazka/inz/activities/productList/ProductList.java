package pl.edu.pw.sgalazka.inz.activities.productList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.bluetooth.server.ServerBluetooth;
import pl.edu.pw.sgalazka.inz.utils.Utils;

public class ProductList extends Activity implements ListDataReceiver {

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
    }

    @Override
    public void onListDataReceive(String data) {
        String tmp[] = data.split(":");
        String message = "";
        if (tmp[0].equals(ListDataReceiver.LIST_DATA_CODE)) {
            String tab[] = tmp[2].split(";");
            ProductListRow products[] = new ProductListRow[tab.length];
            for (int i = 0; i < tab.length; i++) {
                String tmpData[] = tab[i].split("#");
                products[i] = new ProductListRow(getApplicationContext(), tmpData[0], tmpData[1], tmpData[2]);
            }
            ProductListRowAdapter adapter = new ProductListRowAdapter(getApplicationContext(),
                    R.layout.product_list_row, products);
            listView.setAdapter(adapter);
            dialog.dismiss();
            return;
        } else if (tmp[0].equals("STOP")) {
            message = "Połączenie zostało zerwane";
        } else if (tmp[0].equals("ERR")) {
            message = "Baza jest pusta";
        }
        Utils.showInformDialog(message, true, ProductList.this, dialog);
    }
}
