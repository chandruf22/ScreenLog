package com.f22labs.screenlog;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.f22labs.screenlog.storage.Storage;
import com.f22labs.screenlog.utils.Constants;
import com.f22labs.screenlog.utils.SharedPrefsUtils;
import com.f22labs.screenlog.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f22labs on 18/08/17.
 */

public class MainActivity extends AppCompatActivity {


    private MediaProjectionManager mgr;

    private Storage storage;


    private RecyclerView recyclerView;

    private Toolbar toolbar;


    private SwipeRefreshLayout swipeRefreshRecyclerList;
    private FloatingActionButton fab;
    private RecyclerViewAdapter mAdapter;

    private List<File> allFilesList ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // ButterKnife.bind(this);
        findViews();
        initToolbar("ScreenLog");

        swipeRefreshRecyclerList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Do your stuff on refresh
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (swipeRefreshRecyclerList.isRefreshing())
                            swipeRefreshRecyclerList.setRefreshing(false);
                    }
                }, 5000);

            }
        });

        storage = new Storage(this);




        if(!Utils.isAccessibilityEnabled(this,getPackageName()+"/.WindowChangeDetectingService")){
            Utils.openAccessibilitySettings(this);
            Utils.showToastLong(getApplicationContext(),"Please enable accessibility service access to ScreenLog");
        }else{

            setAdapter();

        }




    }


    @Override
    protected void onResume() {
        super.onResume();

        boolean isScreenShotPermissionEnabled = SharedPrefsUtils.getBoolean(Constants.SHARED_PREFS.IS_SCREENSHOT_PERMISSION_ENABLED, false, this);

        if(Utils.isAccessibilityEnabled(this,getPackageName()+"/.WindowChangeDetectingService") && !isScreenShotPermissionEnabled) {


                mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

                startActivityForResult(mgr.createScreenCaptureIntent(),
                        Constants.RESULT_CODE.SCREENSHOT);

        }else{


        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.RESULT_CODE.SCREENSHOT) {

            SharedPrefsUtils.putBoolean(Constants.SHARED_PREFS.IS_SCREENSHOT_PERMISSION_ENABLED,true, this);

            if (resultCode == RESULT_OK) {

                Intent intent = new Intent(this, WindowChangeDetectingService.class);
                intent.putExtra(Constants.INTENT_KEY.SCREENSHOT, resultCode);
                intent.putExtra(Constants.INTENT_KEY.SCREENSHOT_DATA, data);
                startService(intent);

                setAdapter();

            }
        }



    }


    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshRecyclerList = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_recycler_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ;
    }


    public void initToolbar(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
    }


    private void setAdapter() {


        allFilesList = storage.getNestedFiles(storage.getScrenshotsFolder());


        if(allFilesList != null && allFilesList.size() > 0) {

            mAdapter = new RecyclerViewAdapter(MainActivity.this, allFilesList);

            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);


            recyclerView.setAdapter(mAdapter);


            mAdapter.SetOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, File model) {

                    //handle item click events here
//                    Toast.makeText(MainActivity.this, "Hey " + model.getTitle(), Toast.LENGTH_SHORT).show();


                }
            });

        }else{

            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPrefsUtils.putBoolean(Constants.SHARED_PREFS.IS_SCREENSHOT_PERMISSION_ENABLED,false, this);

    }
}
