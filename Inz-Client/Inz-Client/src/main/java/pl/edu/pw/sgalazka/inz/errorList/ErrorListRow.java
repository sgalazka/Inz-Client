package pl.edu.pw.sgalazka.inz.errorList;

import android.content.Context;
import android.util.Log;

/**
 * Created by gałązka on 2016-01-14.
 */
public class ErrorListRow {
    private Context context;

    private String message = "";

    public ErrorListRow(Context context, String message){
        this.context = context;
        this.message = message;
    }

    public String getMessage() {
        Log.d("ProductListRow", "Get name: " + message);
        return message;
    }


}
