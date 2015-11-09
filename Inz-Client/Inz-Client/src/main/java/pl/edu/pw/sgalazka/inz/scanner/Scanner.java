package pl.edu.pw.sgalazka.inz.scanner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.Random;

import pl.edu.pw.sgalazka.inz.R;


public class Scanner extends Activity implements SurfaceHolder.Callback{

    private Camera mCamera;
    private CameraPreview mPreview;
    private SurfaceView topSurface;
    private Handler autoFocusHandler;

    private Button scanButton;
    private ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    private String results[];

    private int resultNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initControls();
    }

    private void initControls() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        results = new String[3];
        resultNumber = 1;

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);


        mPreview = new CameraPreview(Scanner.this, mCamera, previewCb,
                autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.scanner_bottom_surface);
        topSurface = (SurfaceView) findViewById(R.id.scanner_top_surface);
        topSurface.getHolder().addCallback(this);

        topSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);

        preview.addView(mPreview);

        scanButton = (Button) findViewById(R.id.button_capture);

        //releaseCamera();

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (barcodeScanned) {
                    startScanning();
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // SCAdminTapToScanScreen.isFromAssetDetail = false;
            releaseCamera();
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            Image barcodeToCrop = new Image(size.width, size.height, "Y800");
            barcode.setData(data);
            barcodeToCrop.setData(data);
            int width = size.width - 300;
            int height = size.height - 300;
            barcodeToCrop.setCrop(150, 150, width, height);

            int result = scanner.scanImage(barcodeToCrop);

            if (result != 0) {
                int result2 = scanner.scanImage(barcode);
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {

                    Log.i("<<<<<<Asset Code>>>>> ",
                            "<<<<Bar Code>>> " + sym.getData());
                    String scanResult = sym.getData().trim();

                    Toast.makeText(Scanner.this, scanResult,
                            Toast.LENGTH_SHORT).show();
                    Intent resultData = new Intent();
                    resultData.putExtra("barcode", scanResult+":"+result2);
                    setResult(Activity.RESULT_OK, resultData);
                    Scanner.this.finish();

                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
    private synchronized void startScanning(){
        barcodeScanned = false;
        mCamera.setPreviewCallback(previewCb);
        mCamera.startPreview();
        previewing = true;
        mCamera.autoFocus(autoFocusCB);
    }

    private int getResultNumber() {
        return resultNumber;
    }

    private void setResultNumber(int resultNumber) {
        this.resultNumber = resultNumber;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        tryDrawing(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        tryDrawing(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private class Timer implements Runnable{
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            while(true) {
                long currentTime = System.currentTimeMillis() - startTime;
                if(currentTime>100){
                    startScanning();
                    break;
                }
            }
        }
    }

    public boolean checkEan13(String barCode){
        // TODO: 2015-10-01 napisać kod sprawdzający parzystość
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void tryDrawing(SurfaceHolder holder) {
        Log.i("INFO", "Trying to draw...");

        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            Log.e("INFO", "Cannot draw onto the canvas as it's null");
        } else {
            drawMyStuff(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawMyStuff(final Canvas canvas) {
        Random random = new Random();
        Log.i("INFO", "Drawing...");
        //canvas.drawRGB(255, 128, 128);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        Rect rect = new Rect();
        rect.set(150, 150, canvas.getWidth() - 150, canvas.getHeight() - 150);

        canvas.drawRect(rect, paint);
    }


}
