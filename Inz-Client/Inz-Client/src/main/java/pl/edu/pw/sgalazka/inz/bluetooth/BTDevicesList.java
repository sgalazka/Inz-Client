package pl.edu.pw.sgalazka.inz.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import pl.edu.pw.sgalazka.inz.BeginPanel;
import pl.edu.pw.sgalazka.inz.R;


public class BTDevicesList extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ArrayList<BTDeviceRow> deviceList = new ArrayList<>();
    ArrayList<String> adresses = new ArrayList<>();
    ListView listView;
    Context context;
    BluetoothAdapter ba;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_btdevices_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTDeviceRow.deviceBonded bonded;
                if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    bonded = BTDeviceRow.deviceBonded.not_bonded;
                }
                else{
                    bonded = BTDeviceRow.deviceBonded.bonded;
                }

                BTDeviceRow row = new BTDeviceRow(getApplicationContext(), device.getName(),bonded);
                deviceList.add(row);
                adresses.add(device.getAddress());


                BTDeviceRow[] arr = new BTDeviceRow[deviceList.size()];
                for(int i=0;i<deviceList.size();i++){
                    arr[i] = deviceList.get(i);
                }

                final RowAdapter adapter = new RowAdapter(getApplicationContext(), R.layout.row, arr);
                listView.setAdapter(adapter);

            }
            /*else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                Log.d("INFO", "POLACZONO");
                BeginPanel.setStart_panel(false);
            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action) ||
                    BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){

                Log.e("INFO", "BRAK POLACZENIA!");
                BeginPanel.setStart_panel(true);
            }*/
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BTDeviceRow row = (BTDeviceRow)listView.getItemAtPosition(position);
        Toast.makeText(context, adresses.get(position), Toast.LENGTH_LONG).show();

        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        ba.getProfileProxy(context, new ConnectionListener(), BluetoothProfile.A2DP);
        BluetoothDevice serwer = ba.getRemoteDevice(adresses.get(position));
        if(BeginPanel.getConnectedSocket()!=null){
            try {
                BeginPanel.getConnectedSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new ClientBluetooth(serwer, context).start();


        ba.cancelDiscovery();
        this.unregisterReceiver(broadcastReceiver);
        this.finish();
    }
}

class ConnectionListener implements BluetoothProfile.ServiceListener{

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        Log.d("INFO", "POLACZONO");
        BeginPanel.setStart_panel(false);
    }

    @Override
    public void onServiceDisconnected(int profile) {
        Log.e("INFO", "BRAK POLACZENIA!");
        BeginPanel.setStart_panel(true);
    }
}