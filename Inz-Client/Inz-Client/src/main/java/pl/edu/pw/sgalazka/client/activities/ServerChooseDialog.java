package pl.edu.pw.sgalazka.client.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import pl.edu.pw.sgalazka.client.R;
import pl.edu.pw.sgalazka.client.activities.devicesList.BTDevicesList;

/**
 * Created by gałązka on 2016-04-25.
 */
public class ServerChooseDialog {

    private Context context;

    public ServerChooseDialog(Context context) {
        this.context = context;
    }

    public void show() {
        DialogButtonListener listener = new DialogButtonListener(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.begin_panel_question_1))
                .setNegativeButton(context.getString(R.string.cancel), listener)
                .setPositiveButton(context.getString(R.string.bt_dialog_another), listener)
                .show();
    }

    private class DialogButtonListener implements DialogInterface.OnClickListener {

        private Context context;

        public DialogButtonListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(context.getApplicationContext(), BTDevicesList.class);
                    context.startActivity(intent);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    }
}
