package pl.edu.pw.sgalazka.inz.bluetooth;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ga³¹zka on 2015-09-07.
 */
public class SendBuffer {
    public static BlockingQueue<String> toSend = new LinkedBlockingQueue<String>();
}
