package com.f22labs.screenlog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by f22labs on 18/08/17.
 */

public class MainActivity extends Activity {


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

        Intent intent = new Intent(this, WindowChangeDetectingService.class);
        startService(intent);
        finish();

    }
}
