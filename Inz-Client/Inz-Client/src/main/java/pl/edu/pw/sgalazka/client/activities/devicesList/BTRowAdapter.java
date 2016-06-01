package pl.edu.pw.sgalazka.client.activities.devicesList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pl.edu.pw.sgalazka.client.R;

/**
 * Created by ga��zka on 2015-09-06.
 */
public class BTRowAdapter extends ArrayAdapter<BTDeviceRow> {
    Context context;
    int layoutResourceId;
    BTDeviceRow data[] = null;

    public BTRowAdapter(Context context, int layoutResourceId, BTDeviceRow[] data) {
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
            holder.btdevice_name = (TextView)row.findViewById(R.id.btdevice_name);
            holder.btdevice_bonded = (TextView)row.findViewById(R.id.btdevice_bonded);
            holder.btdevice_name.setFocusable(false);
            holder.btdevice_bonded.setFocusable(false);
            row.setTag(holder);
        }
        else
        {
            holder = (RowBeanHolder)row.getTag();
        }

        BTDeviceRow object = data[position];
        holder.btdevice_name.setText(object.getName());
        holder.btdevice_bonded.setText(context.getString(object.getBonded().getId()));

        return row;
    }

    static class RowBeanHolder
    {
        TextView btdevice_name;
        TextView btdevice_bonded;
    }
}
