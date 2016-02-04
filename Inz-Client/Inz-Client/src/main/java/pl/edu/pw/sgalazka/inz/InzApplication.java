package pl.edu.pw.sgalazka.inz;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.devicesList.ConnectedCallback;
import pl.edu.pw.sgalazka.inz.bluetooth.server.ServerBluetooth;

/**
 * Created by gałązka on 2015-12-30.
 */
public class InzApplication extends Application {

    private final static String TAG = "InzApplication";

    public static final String STOP_CLIENT = "stopClientRunning";
    public static final String START_BLUETOOTH = "start";

    private static InzApplication app = null;
    private static boolean connected;
    private static ClientBluetooth clientBluetooth = null;
    private static ServerBluetooth serverBluetooth = null;
    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();
        connected = false;
        app = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ClientBluetooth.toSend.add(STOP_CLIENT);
        serverBluetooth.StopRunning();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static InzApplication getAppInstance() {
        return (app != null) ? app : null;
    }

    public static boolean isConnected() {
        synchronized (lock) {
            return connected;
        }
    }

    public static void setConnected(boolean isConnected) {
        synchronized (lock) {
            connected = isConnected;
        }
    }



    public static void startClient(BluetoothDevice bluetoothDevice, Context context,
                                   ConnectedCallback callback) {

        if (clientBluetooth != null) {
            ClientBluetooth.toSend.add(STOP_CLIENT);
        }
        clientBluetooth = new ClientBluetooth(bluetoothDevice, context, callback);
        clientBluetooth.start();
    }

    public static void startServer(final ConnectedCallback callback) {
        if (serverBluetooth != null) {
            Log.d(TAG, "server is not null");
            serverBluetooth.StopRunning();
        }
        Log.d(TAG, "server is being created");
        Runnable startServerCallback = new Runnable() {
            @Override
            public void run() {
                serverBluetooth = new ServerBluetooth(callback);
                serverBluetooth.start();
            }
        };
        Thread thread = new Thread(startServerCallback);
        thread.start();

    }

    /*private static Runnable startServerCallback = new Runnable() {
        @Override
        public void run() {
            serverBluetooth = new ServerBluetooth();
            serverBluetooth.start();
        }
    };*/


}
