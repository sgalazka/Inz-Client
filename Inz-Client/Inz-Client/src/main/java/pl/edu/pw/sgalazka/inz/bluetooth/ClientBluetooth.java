package pl.edu.pw.sgalazka.inz.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import pl.edu.pw.sgalazka.inz.BeginPanel;

/**
 * Created by ga��zka on 2015-09-04.
 */
public class ClientBluetooth extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BlockingQueue<String> toSend;

    public  ClientBluetooth(BluetoothDevice device, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(BeginPanel.SETTINGS, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("server_name",device.getName());
        editor.putString("server_address", device.getAddress());
        editor.apply();

        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            //UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        }
        catch (Exception e) {

        }
        mmSocket = tmp;
        toSend = SendBuffer.toSend;


    }

    public void run() {
        PrintWriter out;
        try{
            Log.d("INFO", "Lacze z serwerem");
            mmSocket.connect();
            Log.d("INFO", "Po��czono z serwerem");
            out = new PrintWriter(mmSocket.getOutputStream(), true);

        }
        catch (Exception ce) {
            try {
                mmSocket.close();
                return;
            }
            catch (Exception cle){
                return;
            }
        }
        BeginPanel.setConnectedSocket(mmSocket);
        while(true){
            if (!toSend.isEmpty()){
                try {
                    String send = toSend.take();
                    out.println(send);
                    Log.d("INFO","wysylam wiadomosc:"+send);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
