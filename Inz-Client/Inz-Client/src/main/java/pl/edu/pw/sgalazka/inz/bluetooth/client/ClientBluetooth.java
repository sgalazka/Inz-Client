package pl.edu.pw.sgalazka.inz.bluetooth.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pl.edu.pw.sgalazka.inz.activities.BeginPanel;
import pl.edu.pw.sgalazka.inz.InzApplication;
import pl.edu.pw.sgalazka.inz.activities.ScannerResultCallback;
import pl.edu.pw.sgalazka.inz.devicesList.ConnectedCallback;

/**
 * Created by ga��zka on 2015-09-04.
 */
public class ClientBluetooth extends Thread {

    private final static String TAG = "ClientBluetooth";
    public static BlockingQueue<String> toSend = new LinkedBlockingQueue<String>();

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private PrintWriter out;
    private ConnectedCallback connectedCallback;

    public ClientBluetooth(BluetoothDevice device, Context context, ConnectedCallback connectedCallback) {

        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mmSocket = tmp;
        this.connectedCallback = connectedCallback;
    }
    @Override
    public void run() {
        try{
            Log.d(TAG, "Lacze z serwerem");
            mmSocket.connect();

            out = new PrintWriter(mmSocket.getOutputStream(), true);
            InzApplication.startServer(connectedCallback);

            out.println(InzApplication.START_BLUETOOTH);
            Log.d(TAG, "Sent star message");
        }
        catch (Exception ce) {
            try {
                Log.d(TAG, ce + "");
                mmSocket.close();
                return;
            } catch (Exception cle) {
                return;
            }
        }
        while(true){
            if (!toSend.isEmpty()){
                try {
                    String send = toSend.take();
                    if(send.equals(InzApplication.STOP_CLIENT)){
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
    }

    public void StopRunning(){
        out.close();
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
