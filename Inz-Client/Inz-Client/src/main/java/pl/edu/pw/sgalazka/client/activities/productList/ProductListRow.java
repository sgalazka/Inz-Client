package pl.edu.pw.sgalazka.client.activities.productList;

import android.content.Context;
import android.util.Log;

/**
 * Created by gałązka on 2016-01-12.
 */
public class ProductListRow {

    private Context context;

    private String name = "";
    private String barcode = "";
    private String quantity = "";

    public ProductListRow(Context context, String name, String bardcode, String quantity){
        this.context = context;
        this.name = name;
        this.barcode = bardcode;
        this.quantity = quantity;
    }

    public String getName() {
        Log.d("ProductListRow", "Get name: "+name);
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getQuantity() {
        return quantity;
    }

}
