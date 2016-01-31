package pl.edu.pw.sgalazka.inz.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pl.edu.pw.sgalazka.inz.InzApplication;
import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.devicesList.BTDevicesList;
import pl.edu.pw.sgalazka.inz.errorList.ErrorList;
import pl.edu.pw.sgalazka.inz.productList.ProductListChooser;


public class BeginPanel extends Activity {

    private Button startScanProduct;
    private Button startBluetoothSettings;
    private Button startAddToDatabase;
    private Button startTypeProductBarcode;
    private Button startGetProducts;
    private Button showErrors;
    private static BluetoothSocket connectedSocket = null;
    private static boolean start_panel = true;

    private Button another;


    public final static String SETTINGS = "bluetooth_settings";

    public static boolean isStart_panel() {
        return start_panel;
    }

    public static void setStart_panel(boolean start_panel) {
        BeginPanel.start_panel = start_panel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, 1);
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, 1);
        }

        if (!InzApplication.isConnected())
            setStartView();
        else
            setWorkView();
    }


    public static BluetoothSocket getConnectedSocket() {
        return connectedSocket;
    }

    public static void setConnectedSocket(BluetoothSocket socket) {
        connectedSocket = socket;
    }

    private void setStartView() {
        setContentView(R.layout.activity_begin_panel_start);
        another = (Button) findViewById(R.id.start_another_devices);

        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeginPanel.this.getApplicationContext(), BTDevicesList.class);
                startActivity(intent);
            }
        });
    }

    private void setWorkView() {
        setContentView(R.layout.activity_begin_panel);

        startScanProduct = (Button) findViewById(R.id.scannerStart);
        startBluetoothSettings = (Button) findViewById(R.id.bluetooth_btn);
        startAddToDatabase = (Button) findViewById(R.id.begin_panel_addToDatabase);
        startTypeProductBarcode = (Button) findViewById(R.id.begin_panel_typeName);
        startGetProducts = (Button) findViewById(R.id.begin_panel_get_products);
        showErrors = (Button) findViewById(R.id.begin_panel_show_errors);


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
                Intent intent = new Intent(getApplicationContext(), BTDevicesList.class);
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
        startGetProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductListChooser.class);
                startActivity(intent);
            }
        });
        showErrors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ErrorList.class);
                startActivity(intent);
            }
        });

    }
}

