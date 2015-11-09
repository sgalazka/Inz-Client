package pl.edu.pw.sgalazka.inz.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * Created by ga³¹zka on 2015-09-04.
 */
public class ServerBluetooth extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    public ServerBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp =  null;
        try{
            UUID uuid=UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Us³uga witaj¹ca", uuid);

        }
        catch (IOException e){}
        mmServerSocket = tmp;
    }

    public void run() {

        Log.d("INFO", "Uruchamiam serwer");
        BluetoothSocket socket = null;
        while(true) {
            try {
                Log.d("INFO", "Czekam na polaczenie od clienta");
                socket = mmServerSocket.accept();
                Log.d("INFO","znaleziono klienta");
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Witaj");
            }
            catch (IOException e){
                break;
            }
            if (socket != null) {
                try {
                    mmServerSocket.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
}
