package com.f22labs.screenlog;

import android.Manifest;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.f22labs.screenlog.models.AccessbilityCheckEvent;
import com.f22labs.screenlog.storage.Storage;
import com.f22labs.screenlog.utils.Constants;
import com.f22labs.screenlog.utils.SharedPrefsUtils;
import com.f22labs.screenlog.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by f22labs on 18/08/17.
 */

@RuntimePermissions
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
            Utils.openAccessibilitySettings(this, Constants.RESULT_CODE.ACCESSIBILITY_SERVICE);
            Utils.showToastLong(getApplicationContext(),"Please enable accessibility service access to ScreenLog");

        }else{

            MainActivityPermissionsDispatcher.onWriteStoragePermissionEnabledWithCheck(this);


        }




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == Constants.RESULT_CODE.ACCESSIBILITY_SERVICE) {

                MainActivityPermissionsDispatcher.onWriteStoragePermissionEnabledWithCheck(this);


            }else  if (requestCode == Constants.RESULT_CODE.SCREENSHOT){

                startAccessibilityService(resultCode, data);

            }



        }else if (resultCode == RESULT_CANCELED) {


            if (requestCode == Constants.RESULT_CODE.SCREENSHOT){


                finish();
            }


        }



    }

    private void startAccessibilityService(int resultCode, Intent data) {
        Intent intent = new Intent(this, WindowChangeDetectingService.class);
        intent.putExtra(Constants.INTENT_KEY.SCREENSHOT, resultCode);
        intent.putExtra(Constants.INTENT_KEY.SCREENSHOT_DATA, data);
        startService(intent);
    }

    private void startScreenCapture() {
        mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        startActivityForResult(mgr.createScreenCaptureIntent(),
                Constants.RESULT_CODE.SCREENSHOT);
    }


    @Override
    protected void onResume() {
        super.onResume();

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
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPrefsUtils.putBoolean(Constants.SHARED_PREFS.IS_SCREENSHOT_PERMISSION_ENABLED,false, this);

    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onWriteStoragePermissionEnabled() {

        setAdapter();

        startScreenCapture();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onWriteStoragePermissionDisabled() {


        finish();
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Subscribe
//    public void onAccessibilityServiceEnabled(AccessbilityCheckEvent event) {
//        /* Do something */
//
//        if(event != null){
//
//            if(event.getStatus() == Constants.RESULT_CODE.SUCCESS){
//
//
//            }
//        }
//    };






}
