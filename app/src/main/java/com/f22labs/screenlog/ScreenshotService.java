package com.f22labs.screenlog;

import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.ToneGenerator;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.f22labs.screenlog.screenshot.ImageTransmogrifier;
import com.f22labs.screenlog.storage.Storage;
import com.f22labs.screenlog.utils.Constants;
import com.f22labs.screenlog.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;

public class ScreenshotService extends Service {


    static final int VIRT_DISPLAY_FLAGS =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

    private MediaProjection projection;
    private VirtualDisplay vdisplay;
    final private HandlerThread handlerThread =
            new HandlerThread(getClass().getSimpleName(),
                    android.os.Process.THREAD_PRIORITY_BACKGROUND);
    private Handler handler;
    private MediaProjectionManager mgr;
    private WindowManager wmgr;
    private ImageTransmogrifier it;
    final private ToneGenerator beeper =
            new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);


    private int resultCode;
    private Intent resultData;
    private String packageName;

    private String activityName;


    private Storage storage;


    private long currentMillis = System.currentTimeMillis();

    @Override
    public void onCreate() {
        super.onCreate();

        mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        wmgr = (WindowManager) getSystemService(WINDOW_SERVICE);

        storage = new Storage(getApplicationContext());

        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        resultCode = intent.getIntExtra(Constants.INTENT_KEY.SCREENSHOT, 1337);
        resultData = intent.getParcelableExtra(Constants.INTENT_KEY.SCREENSHOT_DATA);

        packageName = intent.getStringExtra(Constants.INTENT_KEY.PACKAGE);
        activityName = intent.getStringExtra(Constants.INTENT_KEY.ACTIVITY_NAME);


        NotificationBuilder.closeNotificationBar(this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startCapture();
            }
        },1000);
        return (START_NOT_STICKY);
    }

    private void stopService() {
        beeper.startTone(ToneGenerator.TONE_PROP_NACK);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        stopCapture();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("Binding not supported. Go away.");
    }

    public WindowManager getWindowManager() {
        return (wmgr);
    }

    public Handler getHandler() {
        return (handler);
    }

    public void processImage(final byte[] png) {


        new Thread() {
            @Override
            public void run() {



                try {


                    storage.createDirectory(storage.getExternalStorageDirectory()+File.separator+Constants.FILE.FOLDER_SCREENSHOTS);


//                    File output = new File(storage.getExternalStorageDirectory()+File.separator+Constants.FILE.FOLDER_SCREENSHOTS
//                            +File.separator
//
//                            , Utils.getAppNameFromPackage(ScreenshotService.this,packageName)
//                            +Constants.FILE.DELIMITER+activityName+Constants.FILE.DELIMITER+currentMillis+Constants.FILE.PNG);

                    File output =  storage.buildScreenShotsFileName(ScreenshotService.this, packageName,activityName,currentMillis);

                    FileOutputStream fos = new FileOutputStream(output);

                    fos.write(png);
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();


                    MediaScannerConnection.scanFile(ScreenshotService.this,
                            new String[]{output.getAbsolutePath()},
                            new String[]{"image/png"},
                            null);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Exception writing out screenshot", e);
                }
            }
        }.start();


        beeper.startTone(ToneGenerator.TONE_PROP_ACK);
        stopCapture();

        stopService();
    }

    private void stopCapture() {
        if (projection != null) {
            projection.stop();
            vdisplay.release();
            projection = null;
        }
    }

    private void startCapture() {
        projection = mgr.getMediaProjection(resultCode, resultData);
        it = new ImageTransmogrifier(this);

        MediaProjection.Callback cb = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                vdisplay.release();
            }
        };

        vdisplay = projection.createVirtualDisplay("andshooter",
                it.getWidth(), it.getHeight(),
                getResources().getDisplayMetrics().densityDpi,
                VIRT_DISPLAY_FLAGS, it.getSurface(), null, handler);
        projection.registerCallback(cb, handler);
    }



}