package pl.edu.pw.sgalazka.client.activities.productList;

/**
 * Created by gałązka on 2016-01-12.
 */
public interface ListDataReceiver {

    public static final String LIST_DATA_CODE = "GPL";

    void onListDataReceive(String data);
}
