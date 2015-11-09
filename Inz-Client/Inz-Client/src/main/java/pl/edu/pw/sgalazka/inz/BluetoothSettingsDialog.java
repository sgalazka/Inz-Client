package pl.edu.pw.sgalazka.inz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import pl.edu.pw.sgalazka.inz.bluetooth.BTDevicesList;
import pl.edu.pw.sgalazka.inz.bluetooth.ClientBluetooth;

/**
 * Created by ga³¹zka on 2015-09-24.
 */
public class BluetoothSettingsDialog extends DialogFragment {

    private Context context;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = activity;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String name = savedInstanceState.getString("name");
        final String address = savedInstanceState.getString("address");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.bt_dialog_question)
                + "\nNazwa:\t" + name + "\nAdres:\t" + address)
                .setPositiveButton(R.string.bt_dialog_connect, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice serwer = ba.getRemoteDevice(address);
                        new ClientBluetooth(serwer, context).start();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.bt_dialog_another, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, BTDevicesList.class);
                        context.startActivity(intent);
                        dialog.dismiss();
                    }
                });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }
}
