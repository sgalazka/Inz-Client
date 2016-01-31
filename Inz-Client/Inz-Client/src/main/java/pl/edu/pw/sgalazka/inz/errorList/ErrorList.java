package pl.edu.pw.sgalazka.inz.errorList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pl.edu.pw.sgalazka.inz.R;
import pl.edu.pw.sgalazka.inz.productList.ProductListRow;
import pl.edu.pw.sgalazka.inz.productList.ProductListRowAdapter;

public class ErrorList extends Activity implements Runnable, AdapterView.OnItemClickListener {

    public static BlockingQueue<String> errorQueue = new LinkedBlockingQueue<>();
    private static List<String> errorList = new LinkedList<>();
    private ListView listView;
    private EditText title;
    private final static Object lock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_list);

        Thread thread = new Thread(this);
        thread.start();

        listView = (ListView) findViewById(R.id.errorList);
        title = (EditText) findViewById(R.id.errorList_title);

        runOnUiThread(showList);
    }

    public static void addError(String msg) {
        synchronized (lock) {
            errorList.add(msg);
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                String errorMsg = errorQueue.take();
                String tmp[] = errorMsg.split(":");
                if(tmp[0].equals("N")){
                    Log.d("SS", errorMsg);
                    errorMsg = "Brak towaru o kodzie: \n"+tmp[1];
                }
                addError(errorMsg);
                runOnUiThread(showList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private Runnable showList = new Runnable() {
        @Override
        public void run() {
            synchronized (lock) {
                ErrorListRow errors[] = new ErrorListRow[errorList.size()];

                for (int i = 0; i < errorList.size(); i++) {
                    errors[i] = new ErrorListRow(getApplicationContext(), errorList.get(i));
                }

                ErrorListRowAdapter adapter = new ErrorListRowAdapter(getApplicationContext(),
                        R.layout.error_list_row, errors);

                listView.setAdapter(adapter);
            }
        }
    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final ErrorListRow row = (ErrorListRow) listView.getItemAtPosition(position);



        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        errorList.remove(row);
                        ErrorList.this.runOnUiThread(showList);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ErrorList.this);
        builder.setMessage("Chcesz usunąć element?").setPositiveButton("Tak", dialogClickListener)
                .setNegativeButton("Nie", dialogClickListener).show();
    }
}
