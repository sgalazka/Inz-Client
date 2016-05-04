package pl.edu.pw.sgalazka.inz.activities.devicesList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import pl.edu.pw.sgalazka.inz.InzApplication;
import pl.edu.pw.sgalazka.inz.R;

public class BTDevicesList extends Activity implements AdapterView.OnItemClickListener, ConnectedCallback {

    private ArrayList<BTDeviceRow> deviceList = new ArrayList<>();
    private ArrayList<String> addresses = new ArrayList<>();
    private ListView listView;
    private Context context;
    private BluetoothAdapter ba;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btdevices_list);

        listView = (ListView) findViewById(R.id.listView);

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(broadcastReceiver, filter1);
        this.registerReceiver(broadcastReceiver, filter2);
        this.registerReceiver(broadcastReceiver, filter3);
        this.registerReceiver(broadcastReceiver, filter4);
        ba = BluetoothAdapter.getDefaultAdapter();
        ba.startDiscovery();

        listView.setOnItemClickListener(this);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTDeviceRow.deviceBonded bonded;
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    bonded = BTDeviceRow.deviceBonded.not_bonded;
                } else {
                    bonded = BTDeviceRow.deviceBonded.bonded;
                }

                BTDeviceRow row = new BTDeviceRow(getApplicationContext(), device.getName(), bonded);
                deviceList.add(row);
                addresses.add(device.getAddress());

                BTDeviceRow[] arr = new BTDeviceRow[deviceList.size()];
                for (int i = 0; i < deviceList.size(); i++) {
                    arr[i] = deviceList.get(i);
                }

                final BTRowAdapter adapter = new BTRowAdapter(getApplicationContext(), R.layout.bt_row, arr);
                listView.setAdapter(adapter);
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BTDeviceRow row = (BTDeviceRow) listView.getItemAtPosition(position);
        Toast.makeText(context, addresses.get(position), Toast.LENGTH_LONG).show();

        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice server = ba.getRemoteDevice(addresses.get(position));

        InzApplication.startClient(server, context, this);
        ba.cancelDiscovery();
        dialog = ProgressDialog.show(BTDevicesList.this, "Łączenie z serwerem", "Proszę czekać...", true);
    }

    @Override
    public void onConnected(final boolean result) {
        dialog.dismiss();

        Runnable dialogShow = new Runnable() {
            @Override
            public void run() {
                if (result) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BTDevicesList.this);
                    builder.setMessage("Połączono!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    BTDevicesList.this.unregisterReceiver(broadcastReceiver);
                                    BTDevicesList.this.finish();
                                }
                            });
                    AlertDialog alert = builder.create();

                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BTDevicesList.this);
                    builder.setMessage("Nie nawiązano połączenia")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    BTDevicesList.this.unregisterReceiver(broadcastReceiver);
                                    BTDevicesList.this.finish();
                                }
                            });
                    AlertDialog alert = builder.create();

                    alert.show();
                }
            }
        };
        this.runOnUiThread(dialogShow);
    }
}

