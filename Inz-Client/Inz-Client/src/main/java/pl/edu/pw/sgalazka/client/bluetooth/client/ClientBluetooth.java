package pl.edu.pw.sgalazka.client.bluetooth.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pl.edu.pw.sgalazka.client.InzApplication;
import pl.edu.pw.sgalazka.client.activities.devicesList.ConnectedCallback;

/**
 * Created by ga��zka on 2015-09-04.
 */
public class ClientBluetooth extends Thread {

    private final static String TAG = "ClientBluetooth";
    public static BlockingQueue<String> toSend = new LinkedBlockingQueue<String>();

    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private PrintWriter out;
    private ConnectedCallback connectedCallback;

    public ClientBluetooth(BluetoothDevice device, Context context, ConnectedCallback connectedCallback) {

        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "create socket failed");
        }
        this.connectedCallback = connectedCallback;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "Lacze z serwerem");
            mmSocket.connect();
            out = new PrintWriter(mmSocket.getOutputStream(), true);
        } catch (Exception ce) {
            Log.d(TAG, ce + "");
            connectedCallback.onConnected(false);
            try {
                ce.printStackTrace();
                mmSocket.close();
                mmSocket = null;
                out.close();
                out = null;
            } catch (Exception cle) {
                cle.printStackTrace();

            }
            finally {
                return;
            }
        }

        InzApplication.startServer(connectedCallback);
        out.println(InzApplication.START_BLUETOOTH);
        Log.d(TAG, "Sent star message");

        while (true) {
            try {
                String send = toSend.take();
                if (send.equals(InzApplication.STOP_CLIENT)) {
                    StopRunning();
                    Log.d(TAG, "Client stopped running");
                    break;
                }
                Log.d(TAG, "Sending: " + send);
                out.println(send);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void StopRunning() {
        try {
            mmSocket.close();
            mmSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
        out = null;
    }
}
