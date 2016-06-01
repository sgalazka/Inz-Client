package pl.edu.pw.sgalazka.client.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import pl.edu.pw.sgalazka.client.InzApplication;
import pl.edu.pw.sgalazka.client.activities.AddingResultCallback;
import pl.edu.pw.sgalazka.client.activities.ScannerResultCallback;
import pl.edu.pw.sgalazka.client.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.client.activities.devicesList.ConnectedCallback;
import pl.edu.pw.sgalazka.client.activities.productList.ListDataReceiver;

/**
 * Created by ga��zka on 2015-09-04.
 */
public class ServerBluetooth extends Thread {

    private final static String TAG = "ServerBluetooth";

    private final BluetoothServerSocket mmServerSocket;
    private  BufferedReader in;
    private boolean running;
    private ConnectedCallback connectedCallback;
    private long watchDogTimer;
    private final Object watchDogLock = new Object();
    private static final Object receiverLock = new Object();
    private final Object lock = new Object();
    private static final String HEART_BEAT = "HeartBeat";
    private boolean initialized;
    private static ListDataReceiver listDataReceiver = null;
    private static ScannerResultCallback scannerResultCallback = null;
    private static AddingResultCallback addingResultCallback = null;

    public ServerBluetooth(ConnectedCallback connectedCallback) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;
        Log.d(TAG, "Creating Server");
        try {
            UUID uuid = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66");
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("PC to Android", uuid);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Created");
        setRunning(true);
        mmServerSocket = tmp;
        initialized = false;
        this.connectedCallback = connectedCallback;
    }

    @Override
    public void run() {
        Log.d(TAG, "Uruchamiam serwer");

        setWatchDogTimer(40000);
        Thread watchdog = new Thread(watchDog);
        watchdog.start();

        BluetoothSocket socket = null;
        InputStream inputStream = null;
        Log.d(TAG, "Czekam na polaczenie od clienta");
        try {
            socket = mmServerSocket.accept();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "znaleziono klienta");

        in = new BufferedReader(new InputStreamReader(inputStream));

        String tmp = null;
        try {
            tmp = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            connectedCallback.onConnected(false);
            InzApplication.setConnected(false);
            Log.w(TAG, "Connection failed");
            return;
        }
        Log.d(TAG, "BT Server got: " + tmp);
        if (tmp.equals(InzApplication.START_BLUETOOTH)) {
            if (isWatchDogError()) {
                return;
            } else if(connectedCallback!=null) {
                InzApplication.setConnected(true);
                connectedCallback.onConnected(true);
                connectedCallback = null;
            }
        } else if (!tmp.contains("start")) {
            connectedCallback.onConnected(false);
            InzApplication.setConnected(false);
            Log.w(TAG, "Connection failed");
            return;
        }
        initialized = true;
        Thread thread = new Thread(heartBeat);
        thread.start();

        while (isRunning()) {
            try {
                tmp = in.readLine();
                String tab[] = tmp.split(":");
                Log.d(TAG, "BT Server got: " + tmp);
                switch (tab[0]) {
                    case HEART_BEAT:
                        thread = new Thread(heartBeat);
                        thread.start();
                        break;
                    case "ENF":
                    case "BSS":
                    case "EZQ":
                        if (getScannerResultCallback() != null) {
                            getScannerResultCallback().onScannerResult(tmp);
                            setScannerResultCallback(null);
                        }
                        break;
                    case "EEX":
                    case "DAS":
                        if (getAddingResultCallback() != null) {
                            getAddingResultCallback().onAddingResult(tmp);
                            setAddingResultCallback(null);
                        }
                        break;
                    case ListDataReceiver.LIST_DATA_CODE:
                        Log.d(TAG, tmp);
                        if (getListDataReceiver() != null) {
                            Log.d(TAG, "List of products recived");
                            getListDataReceiver().onListDataReceive(tmp);
                            setListDataReciver(null);
                        }
                        break;
                }
            } catch (IOException e) {
                break;
            }
        }

//        InzApplication.setConnected(false);
//        try {
//            in.close();
//            mmServerSocket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void StopRunning() {
        //stopRunningCallback.run();
        setRunning(false);
    }

//    private Runnable stopRunningCallback = new Runnable() {
//        @Override
//        public void run() {
//            running = false;
//        }
//    };

    private Runnable heartBeat = new Runnable() {
        @Override
        public void run() {
            try {
                sleep(2000, 0);
                ClientBluetooth.toSend.add(HEART_BEAT);
                setWatchDogTimer(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable watchDog = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "watchdog start");
            while (!isWatchDogError()) {
                watchDogTick();
                 //Log.d(TAG, "watchdog tick");
                try {
                    sleep(500, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            setRunning(false);
            Log.d(TAG, "watchdog counted to 0, the connection is failed");
            if (!isInitialized()){
                InzApplication.setConnected(false);
                connectedCallback.onConnected(false);
            }
            if (getScannerResultCallback() != null) {
                getScannerResultCallback().onScannerResult("STOP");
            }
            if (getAddingResultCallback() != null) {
                getAddingResultCallback().onAddingResult("STOP");
            }
            if (getListDataReceiver()!=null){
                getListDataReceiver().onListDataReceive("STOP");
            }
            InzApplication.setConnected(false);
            try {
                Log.d(TAG, "close socket");
                if(in!=null)
                    in.close();
                if(mmServerSocket!=null)
                    mmServerSocket.close();
                ServerBluetooth.this.interrupt();
                Log.d(TAG, "closed socket");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void setWatchDogTimer(long watchDogTimerVal) {
        synchronized (watchDogLock) {
            Log.d(TAG, "setWatchDogTimer: " + watchDogTimerVal);
            watchDogTimer = watchDogTimerVal;
        }
    }

    private void watchDogTick() {
        synchronized (watchDogLock) {
            watchDogTimer = watchDogTimer - 500;
        }
    }

    private boolean isWatchDogError() {
        synchronized (watchDogLock) {
            return watchDogTimer <= 0;
        }
    }

    private boolean isInitialized() {
        synchronized (watchDogLock) {
            return initialized;
        }
    }

    private boolean isRunning() {
        synchronized (lock) {
            return running;
        }
    }

    private void setRunning(boolean val) {
        synchronized (lock) {
            running = val;
        }
    }

    public static void setListDataReciver(ListDataReceiver receiver) {
        synchronized (receiverLock) {
            listDataReceiver = receiver;
        }
    }

    public static ListDataReceiver getListDataReceiver() {
        synchronized (receiverLock) {
            return listDataReceiver;
        }
    }

    public static void setScannerResultCallback(ScannerResultCallback callback) {
        synchronized (receiverLock) {
            scannerResultCallback = callback;
        }
    }

    public static ScannerResultCallback getScannerResultCallback() {
        synchronized (receiverLock) {
            return scannerResultCallback;
        }
    }

    public static void setAddingResultCallback(AddingResultCallback callback) {
        synchronized (receiverLock) {
            addingResultCallback = callback;
        }
    }

    public static AddingResultCallback getAddingResultCallback() {
        synchronized (receiverLock) {
            return addingResultCallback;
        }
    }
}
