package pl.edu.pw.sgalazka.client.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import pl.edu.pw.sgalazka.client.R;
import pl.edu.pw.sgalazka.client.scanner.Scanner;

/**
 * Created by gałązka on 2016-05-04.
 */
public class Utils {

    public static boolean checkName(EditText name, Context context) {
        if (name.getText().length() == 0) {
            Toast.makeText(context, context.getString(R.string.toast_name_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (name.getText().length() > 18) {
            Toast.makeText(context, context.getString(R.string.toast_name_too_much_cases), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean checkBarcode(EditText barcode, Context context) {
        if (barcode.getText().length() == 0) {
            Toast.makeText(context, context.getString(R.string.toast_barcode_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (barcode.getText().toString().length() != 13) {
            Toast.makeText(context, context.getString(R.string.toast_barcode_wrong_length), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!EAN13CheckDigit.checkBarcode(barcode.getText().toString())) {
            Toast.makeText(context, context.getString(R.string.toast_barcode_wrong), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean checkPrice(EditText price, Context context) {
        if (price.getText().length() == 0) {
            Toast.makeText(context, context.getString(R.string.toast_price_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        Double bigDecimal;
        try {
            bigDecimal = Double.parseDouble(price.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        if (bigDecimal > 999999.99) {
            Toast.makeText(context, context.getString(R.string.toast_price_too_high), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (price.getText().toString().contains(".")) {
            String tmp = price.getText().toString();
            if (tmp.length() - tmp.indexOf(".") > 3) {
                Toast.makeText(context, context.getString(R.string.toast_price_coma_places), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    /*public static boolean checkPrice(EditText price, Context context) {
        if (price.getText().length() == 0) {
            Toast.makeText(context, context.getString(R.string.toast_price_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (price.getText().length() > 8) {
            Toast.makeText(context, context.getString(R.string.toast_price_too_high), Toast.LENGTH_SHORT).show();
            return false;
        } else if (price.getText().toString().contains(".")) {
            String tmp = price.getText().toString();
            if (tmp.length() - tmp.indexOf(".") > 3) {
                Toast.makeText(context, context.getString(R.string.toast_price_coma_places), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }*/

    public static boolean checkAmount(EditText amount, Context context) {
        if (amount.getText().length() == 0) {
            Toast.makeText(context, context.getString(R.string.toast_amount_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (amount.getText().length() > 2) {
            Toast.makeText(context, context.getString(R.string.toast_amount_too_high), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean checkVat(Spinner vatGroup, Context context) {
        if (vatGroup.getSelectedItem() == null) {
            Toast.makeText(context, context.getString(R.string.toast_vat_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean checkQuantity(EditText amount, Context context) {
        if (amount.getText().length() == 0) {
            Toast.makeText(context, context.getString(R.string.toast_quantity_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (amount.getText().length() > 2) {
            Toast.makeText(context, context.getString(R.string.toast_quantity_too_high), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean checkQuantityProductList(EditText quantity, Context context) {
        if (quantity.getText().length() == 0) {
            Toast.makeText(context, context.getString(R.string.toast_amount_empty), Toast.LENGTH_SHORT).show();
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
