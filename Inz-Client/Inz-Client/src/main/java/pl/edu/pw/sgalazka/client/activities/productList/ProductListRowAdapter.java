package pl.edu.pw.sgalazka.client.activities.productList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pl.edu.pw.sgalazka.client.R;

/**
 * Created by gałązka on 2016-01-12.
 */
public class ProductListRowAdapter extends ArrayAdapter<ProductListRow> {

    Context context;
    int layoutResourceId;
    ProductListRow data[] = null;

    public ProductListRowAdapter(Context context, int layoutResourceId, ProductListRow[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowBeanHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RowBeanHolder();
            holder.productName = (TextView) row.findViewById(R.id.plname);
            holder.productBarcode = (TextView) row.findViewById(R.id.plbarcode);
            holder.productQuantity = (TextView) row.findViewById(R.id.plquantity);
            holder.productName.setFocusable(false);
            holder.productBarcode.setFocusable(false);
            holder.productQuantity.setFocusable(false);
            row.setTag(holder);
        } else {
            holder = (RowBeanHolder) row.getTag();
        }

        ProductListRow object = data[position];
        holder.productName.setText(object.getName());
        holder.productBarcode.setText(object.getBarcode());
        holder.productQuantity.setText(object.getQuantity());
        return row;
    }

    static class RowBeanHolder {
        TextView productName;
        TextView productBarcode;
        TextView productQuantity;
    }
}
