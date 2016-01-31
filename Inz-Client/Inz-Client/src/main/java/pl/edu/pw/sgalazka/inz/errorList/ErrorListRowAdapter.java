package pl.edu.pw.sgalazka.inz.errorList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pl.edu.pw.sgalazka.inz.R;

/**
 * Created by gałązka on 2016-01-14.
 */
public class ErrorListRowAdapter extends ArrayAdapter<ErrorListRow> {

    Context context;
    int layoutResourceId;
    ErrorListRow data[] = null;

    public ErrorListRowAdapter(Context context, int layoutResourceId, ErrorListRow[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowBeanHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RowBeanHolder();
            holder.message = (TextView)row.findViewById(R.id.err_message);
            holder.message.setFocusable(false);

            row.setTag(holder);
        }
        else
        {
            holder = (RowBeanHolder)row.getTag();
        }

        ErrorListRow object = data[position];
        holder.message.setText(object.getMessage());

        return row;
    }

    static class RowBeanHolder
    {
        TextView message;
    }
}
