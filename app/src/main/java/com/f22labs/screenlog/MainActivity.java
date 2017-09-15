package com.f22labs.screenlog;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by f22labs on 18/08/17.
 */

public class MainActivity extends Activity {


    private MediaProjectionManager mgr;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(!Utils.isAccessibilityEnabled(this,getPackageName()+"/.WindowChangeDetectingService")){
            Utils.openAccessibilitySettings(this);
            Utils.showToastLong(getApplicationContext(),"Please enable accessibility service access to ScreenLog");
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if(Utils.isAccessibilityEnabled(this,getPackageName()+"/.WindowChangeDetectingService")) {

            mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

            startActivityForResult(mgr.createScreenCaptureIntent(),
                    Constants.RESULT_CODE.SCREENSHOT);

        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.RESULT_CODE.SCREENSHOT) {

            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, WindowChangeDetectingService.class);
                intent.putExtra(Constants.INTENT_KEY.SCREENSHOT, resultCode);
                intent.putExtra(Constants.INTENT_KEY.SCREENSHOT_DATA, data);
                startService(intent);
                finish();
            }
        }

    }



}
