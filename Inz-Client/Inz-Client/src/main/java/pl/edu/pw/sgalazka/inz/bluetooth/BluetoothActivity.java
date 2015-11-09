package pl.edu.pw.sgalazka.inz.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Set;

import pl.edu.pw.sgalazka.inz.R;


public class BluetoothActivity extends Activity {

    Button b;
    Button b2;
    Button b3;
    Button b4;
    Button b5;
    TextView t1;
    TextView t2;
    EditText et1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        b = (Button) findViewById(R.id.let_show);
        b2 = (Button) findViewById(R.id.find_others);
        b3 = (Button) findViewById(R.id.show_paired);
        b4 = (Button) findViewById(R.id.be_server);
        b5 = (Button) findViewById(R.id.be_client);
        t1 = (TextView) findViewById(R.id.textView);
        t2 = (TextView) findViewById(R.id.tv_mac);
        et1 = (EditText) findViewById(R.id.mac_adress);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dajSieWykryc();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wykryjInne();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pokazSparowane();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t2.setText("Aplikacja jest serwerem");
                new ServerBluetooth().start();
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t2.setText("Aplikacja jest klientem");
                BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice serwer = ba.getRemoteDevice(et1.getText().toString());
                //new ClientBluetooth(serwer).start();
            }
        });
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        t1.setText("Tw�j MAC: "+ ba.getAddress());
        if(!ba.isEnabled()) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, 1);
        }
        et1.setText("DC:85:DE:06:12:E5");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
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

    public void dajSieWykryc() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(intent);
    }

    public void wykryjInne() {
        /*IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(odbiorca, filter);
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        ba.startDiscovery();*/
        Intent intent = new Intent(getApplicationContext(), BTDevicesList.class);
        startActivity(intent);
    }

//    private final BroadcastReceiver odbiorca = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            String akcja = intent.getAction();
//            if(BluetoothDevice.ACTION_FOUND.equals(akcja)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String status = "";
//                if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                    status = "niesparowane";
//                }
//                else{
//                    status = "sparowane";
//                }
//                Log.d("INFO", "znalezione urzadzenie: " + device.getName()+ " - " +  device.getAddress());
//            }
//        }
//    };

    public void pokazSparowane() {
        BluetoothAdapter mbBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mbBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice bluetoothDevice : pairedDevices) {
                Log.d("INFO", bluetoothDevice.getName()+" - "+bluetoothDevice.getAddress());
            }
        }
    }
}
