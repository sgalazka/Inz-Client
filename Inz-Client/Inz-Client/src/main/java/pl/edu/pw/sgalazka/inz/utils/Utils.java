package pl.edu.pw.sgalazka.inz.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import pl.edu.pw.sgalazka.inz.scanner.Scanner;

/**
 * Created by gałązka on 2016-05-04.
 */
public class Utils {

    public static boolean checkName(EditText name, Context context) {
        if (name.getText().length() == 0) {
            Toast.makeText(context, "Wprowadź nazwę", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name.getText().length() > 18) {
            Toast.makeText(context, "Nazwa musi miec mniej niż 19 znaków", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean checkBarcode(EditText barcode, Context context) {
        if (barcode.getText().length() == 0) {
            Toast.makeText(context, "Wprowadź kod kreskowy", Toast.LENGTH_SHORT).show();
            return false;
        } else if (barcode.getText().length() != 13) {
            Toast.makeText(context, "Kod kreskowy musi mieć 13 znaków", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!EAN13CheckDigit.checkBarcode(barcode.getText().toString())) {
            Toast.makeText(context, "Kod kreskowy jest niepoprawny", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean checkPrice(EditText price, Context context) {
        if (price.getText().length() == 0) {
            Toast.makeText(context, "Wprowadź cenę", Toast.LENGTH_SHORT).show();
            return false;
        } else if (price.getText().length() > 8) {
            Toast.makeText(context, "Za duża cena", Toast.LENGTH_SHORT).show();
            return false;
        } else if (price.getText().toString().contains(".")) {
            String tmp = price.getText().toString();
            if (tmp.length() - tmp.indexOf(".") > 3) {
                Toast.makeText(context, "Cena może mieć dwa miejsca po przecinku", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public static boolean checkAmount(EditText amount, Context context) {
        if (amount.getText().length() == 0) {
            Toast.makeText(context, "Wprowadź ilość", Toast.LENGTH_SHORT).show();
            return false;
        } else if (amount.getText().length() > 2) {
            Toast.makeText(context, "Ilość musi być mniejsza niż 100", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean checkVat(Spinner vatGroup, Context context) {
        if (vatGroup.getSelectedItem() == null) {
            Toast.makeText(context, "Wybierz grupę VAT", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean checkQuantity(EditText amount, Context context) {
        if (amount.getText().length() == 0) {
            Toast.makeText(context, "Wprowadź ilość", Toast.LENGTH_SHORT).show();
            return false;
        } else if (amount.getText().length() > 2) {
            Toast.makeText(context, "Ilość musi być mniejsza od 100", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean checkQuantityProductList(EditText quantity, Context context) {
        if (quantity.getText().length() == 0) {
            Toast.makeText(context, "Wprowadź ilość", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static void showInformDialog(final String finalMessage, final boolean finalReturnBack, final Activity activity, Dialog dialog) {
        Runnable dialogShow = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(finalMessage)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (finalReturnBack)
                                    activity.finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        };
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        activity.runOnUiThread(dialogShow);
    }

    public static void showRetryDialog(final Activity activity, final int requestCode) {
        Runnable dialogShow = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Błąd odczytu! Spróbuj jeszcze raz.")
                        .setPositiveButton("Skanuj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(activity.getApplicationContext(), Scanner.class);
                                activity.startActivityForResult(intent, requestCode);
                            }
                        })
                        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        };
        activity.runOnUiThread(dialogShow);
    }
}
