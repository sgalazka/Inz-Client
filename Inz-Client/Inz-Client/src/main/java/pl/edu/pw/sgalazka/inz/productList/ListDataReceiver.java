package pl.edu.pw.sgalazka.inz.productList;

import java.util.List;

/**
 * Created by gałązka on 2016-01-12.
 */
public interface ListDataReceiver {

    public static final String LIST_DATA_CODE = "GPL";

    void onListDataReceive(String data);
}
