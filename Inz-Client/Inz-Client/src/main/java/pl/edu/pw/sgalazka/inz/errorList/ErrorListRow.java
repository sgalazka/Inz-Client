package pl.edu.pw.sgalazka.inz.errorList;

import android.content.Context;
import android.util.Log;

/**
 * Created by gałązka on 2016-01-14.
 */
public class ErrorListRow {
    private Context context;

    private String message1 = "";
    private String message2 = "";

    public ErrorListRow(Context context, String message1, String message2){
        this.context = context;
        this.message1 = message1;
        this.message2 = message2;
    }

    public String getMessage1() {
        //Log.d("ProductListRow", "Get name: " + message);
        return message1;
    }
    public String getMessage2() {
        //Log.d("ProductListRow", "Get name: " + message);
        return message2;
    }


}
