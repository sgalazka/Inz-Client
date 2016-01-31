package pl.edu.pw.sgalazka.inz.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import pl.edu.pw.sgalazka.inz.InzApplication;
import pl.edu.pw.sgalazka.inz.bluetooth.client.ClientBluetooth;
import pl.edu.pw.sgalazka.inz.devicesList.OnConnectedCallback;
import pl.edu.pw.sgalazka.inz.errorList.ErrorList;
import pl.edu.pw.sgalazka.inz.productList.ListDataReceiver;

/**
 * Created by ga��zka on 2015-09-04.
 */
public class ServerBluetooth extends Thread {

    private final static String TAG = "ServerBluetooth";

    private final BluetoothServerSocket mmServerSocket;
    private boolean running;
    private OnConnectedCallback callback;
    private long watchDogTimer;
    private final Object watchDogLock = new Object();
    private static final Object reciverLock = new Object();
    private final Object lock = new Object();
    private static final String HEART_BEAT = "HeartBeat";
    private boolean initialized;
    private static ListDataReceiver listDataReceiver = null;
    private BlockingQueue<String> toErrorList;

    public ServerBluetooth(OnConnectedCallback callback) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;
        Log.d(TAG, "Creating Server");
        try {
            UUID uuid = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66");
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("PC to Android", uuid);

        } catch (IOException e) {
        }


        Log.d(TAG, "Created");
        running = true;
        mmServerSocket = tmp;
        initialized = false;
        this.callback = callback;
        toErrorList = ErrorList.errorQueue;
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

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String tmp = null;
        try {
            tmp = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, "BT Server got: " + tmp);
        if (tmp.equals(InzApplication.START_BLUETOOTH)) {
            if (isWatchDogError()) {
                InzApplication.setConnected(false);
                callback.onConnected(false);
            } else {
                InzApplication.setConnected(true);
                callback.onConnected(true);
            }
        } else if (!tmp.contains("start")) {
            callback.onConnected(false);
            Log.w(TAG, "Connection failed");
            return;
        }
        initialized = true;
        Thread thread = new Thread(heartBeat);
        thread.start();


        while (running) {
            try {

                tmp = in.readLine();
                String tab[] = tmp.split(":");
                //Log.d(TAG, "BT Server got: " + tmp);
                if (tmp.equals(HEART_BEAT)) {
                    setWatchDogTimer(2500);
                    thread = new Thread(heartBeat);
                    thread.start();
                } else if (tmp.charAt(0) == 'N') {
                    ErrorList.errorQueue.add(tmp);
                }
                else if (tab[0].equals(ListDataReceiver.MSG_CODE)){
                    Log.d(TAG,tmp);
                    if(getListDataReciver() != null){
                        Log.d(TAG, "List of products recived");
                        listDataReceiver.onListDataReceive(tmp);
                        setListDataReciver(null);
                    }
                }

            } catch (IOException e) {
                break;
            }
        }

        InzApplication.setConnected(false);

        try {
            in.close();
            mmServerSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void StopRunning() {

        stopRunningCallback.run();
    }

    private Runnable stopRunningCallback = new Runnable() {
        @Override
        public void run() {
            running = false;
        }
    };

    private Runnable heartBeat = new Runnable() {
        @Override
        public void run() {

            try {
                sleep(2000, 0);
                ClientBluetooth.toSend.add(HEART_BEAT);
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
            running = false;
            Log.d(TAG, "watchdog counted to 0, the connection is failed");
            if (!isInitialized())
                callback.onConnected(false);

        }
    };

    private void setWatchDogTimer(long watchDogTimerVal) {
        synchronized (watchDogLock) {
            //Log.d(TAG, "setWatchDogTimer: " + watchDogTimerVal);
            watchDogTimer = watchDogTimerVal;
        }
    }

    private void watchDogTick() {
        synchronized (watchDogLock) {

            watchDogTimer = watchDogTimer - 500;
            //Log.d(TAG, "watchDogTick:" + watchDogTimer);
        }
    }

    private boolean isWatchDogError() {
        synchronized (watchDogLock) {
            if (watchDogTimer <= 0)
                return true;
            else
                return false;
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

    public static void setListDataReciver(ListDataReceiver receiver){
        synchronized (reciverLock){
            listDataReceiver = receiver;
        }
    }

    public static ListDataReceiver getListDataReciver(){
        synchronized (reciverLock){
            return listDataReceiver;
        }
    }

}
