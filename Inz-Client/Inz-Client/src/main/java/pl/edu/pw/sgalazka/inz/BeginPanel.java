package pl.edu.pw.sgalazka.inz;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pl.edu.pw.sgalazka.inz.bluetooth.BTDevicesList;
import pl.edu.pw.sgalazka.inz.bluetooth.BluetoothActivity;
import pl.edu.pw.sgalazka.inz.bluetooth.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.scanner.Scanner;


public class BeginPanel extends Activity {

    private Button startScanProduct;
    private Button startBluetoothSettings;
    private Button startAddToDatabase;
    private Button startTypeProductBarcode;
    private static BluetoothSocket connectedSocket = null;
    private static boolean start_panel = true;
    private Button connect;
    private Button another;
    private TextView name_address;

    public final static String SETTINGS = "bluetooth_settings";

    public static boolean isStart_panel() {
        return start_panel;
    }

    public static void setStart_panel(boolean start_panel) {
        BeginPanel.start_panel = start_panel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /* this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       */ super.onCreate(savedInstanceState);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(!adapter.isEnabled()) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, 1);
        }
        /*if(start_panel)
            setStartView();
        else
            setWorkView();*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_begin_panel, menu);
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

    /*@Override
    public void onRestart(){
        super.onRestart();
        if(start_panel)
            setStartView();
        else
            setWorkView();
    }

    @Override
    public void onStart(){
        super.onStart();
        if(start_panel)
            setStartView();
        else
            setWorkView();
    }*/

    @Override
    public void onResume(){
        super.onResume();

        if(start_panel)
            setStartView();
        else
            setWorkView();
    }

    private void initializeBluetooth(){
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS,0);
        String server_name = sharedPreferences.getString("server_name", "");
        String server_address = sharedPreferences.getString("server_address","");

        if(TextUtils.isEmpty(server_address)) {
            Intent intent = new Intent(getApplicationContext(), BTDevicesList.class);
            startActivity(intent);
        }
        else {
            Bundle bundle = new Bundle();
            bundle.putString("name", server_name);
            bundle.putString("address", server_address);
            BluetoothSettingsDialog settingsDialog = new BluetoothSettingsDialog();
            settingsDialog.onAttach(BeginPanel.this);
            Dialog dialog = settingsDialog.onCreateDialog(bundle);
            dialog.show();
        }
    }

    public static BluetoothSocket getConnectedSocket() {
        return connectedSocket;
    }

    public static void setConnectedSocket(BluetoothSocket socket) {
        connectedSocket = socket;
    }

    private void setStartView(){
        setContentView(R.layout.activity_begin_panel_start);
        connect = (Button) findViewById(R.id.start_connect);
        another = (Button) findViewById(R.id.start_another_devices);
        name_address = (TextView) findViewById(R.id.start_name_address);

        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS,0);
        String server_name = sharedPreferences.getString("server_name", "");
        final String server_address = sharedPreferences.getString("server_address","");

        name_address.setText(server_name+"\n"+server_address);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice serwer = ba.getRemoteDevice(server_address);
                new ClientBluetooth(serwer, BeginPanel.this.getApplicationContext()).start();
                setStart_panel(false);
                setWorkView();
            }
        });

        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeginPanel.this.getApplicationContext(), BTDevicesList.class);
                startActivity(intent);
            }
        });
    }

    private void setWorkView(){
        setContentView(R.layout.activity_begin_panel);

        startScanProduct = (Button) findViewById(R.id.scannerStart);
        startBluetoothSettings = (Button) findViewById(R.id.bluetooth_btn);
        startAddToDatabase = (Button) findViewById(R.id.begin_panel_addToDatabase);
        startTypeProductBarcode= (Button) findViewById(R.id.begin_panel_typeName);



        startScanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanProduct.class);
                startActivity(intent);
            }
        });

        startBluetoothSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivity(intent);
            }
        });
        startAddToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddToDatabase.class);
                startActivity(intent);
            }
        });
        startTypeProductBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TypeProductBarcode.class);
                startActivity(intent);
            }
        });
        /*if(getConnectedSocket()==null) {
            initializeBluetooth();
        }*/
    }
}

