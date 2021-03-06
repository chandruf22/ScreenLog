package com.f22labs.screenlog;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

import com.f22labs.screenlog.activities.MainActivity;
import com.f22labs.screenlog.utils.Constants;
import com.f22labs.screenlog.utils.Utils;

public class WindowChangeDetectingService extends AccessibilityService {


    private int resultCode;
    private Intent resultData;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        resultCode = intent.getIntExtra(Constants.INTENT_KEY.SCREENSHOT, 1337);
        resultData = intent.getParcelableExtra(Constants.INTENT_KEY.SCREENSHOT_DATA);


        return START_STICKY;

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();


//        EventBus.getDefault().postSticky(new AccessbilityCheckEvent(Constants.RESULT_CODE.SUCCESS));

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(),
                    event.getClassName().toString()

                );

                AccessibilityServiceInfo config = new AccessibilityServiceInfo();



                Log.i("AccessPackagename",  event.getPackageName().toString());
                Log.i("AccessgetClassName",  event.getClassName().toString());

//                NotificationBuilder.cancel(this,1);

                if(!event.getPackageName().toString().equals("com.android.systemui")) {
                    showNotification(event.getPackageName().toString(),event.getClassName().toString());

                }




            }


        }
    }



    private void showNotification(String packageName, String activityName) {


        RemoteViews remoteViewsExpand = new RemoteViews(this.getPackageName(), R.layout.notification_expand_layout);

        RemoteViews remoteViewsCollapse = new RemoteViews(getPackageName(),
                R.layout.notification_collapse_layout);


//        PendingIntent bannerSingleTypeAction = RichMediaBuilder.getPendingIntent(this, Constants.ACTION.CALL_TO_ACTION_BANNER_TYPE, RichMediaNotificationService.class);

        PendingIntent openAppIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);




        remoteViewsCollapse.setImageViewBitmap(R.id.img_noti_icon_small, Utils.getIconFromPackageName(this,packageName));
        remoteViewsExpand.setImageViewBitmap(R.id.img_noti_icon_small, Utils.getIconFromPackageName(this,packageName));




//        remoteViewsExpand.setOnClickPendingIntent(R.id.txt_action_button, bannerSingleTypeAction);

        if (resultCode == Constants.RESULT_CODE.RESULT_OK && resultData != null) {

            Intent screenshotIntent = new Intent(this, ScreenshotService.class);
            screenshotIntent.putExtra(Constants.INTENT_KEY.SCREENSHOT, resultCode);
            screenshotIntent.putExtra(Constants.INTENT_KEY.SCREENSHOT_DATA, resultData);
            screenshotIntent.putExtra(Constants.INTENT_KEY.PACKAGE, packageName);
            screenshotIntent.putExtra(Constants.INTENT_KEY.ACTIVITY_NAME, activityName);


            screenshotIntent.setData(Uri.parse(screenshotIntent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent screenshotPendingIntent = PendingIntent.getService(this, 0,
                    screenshotIntent, 0);

            remoteViewsExpand.setOnClickPendingIntent(R.id.img_screen_capture, screenshotPendingIntent);

        }else{

            remoteViewsExpand.setOnClickPendingIntent(R.id.img_screen_capture, openAppIntent);


        }


        remoteViewsCollapse.setTextViewText(R.id.txt_noti_title, Utils.getAppNameFromPackage(this,packageName));
        remoteViewsCollapse.setTextViewText(R.id.txt_noti_activity_name, activityName);

        remoteViewsExpand.setTextViewText(R.id.txt_noti_title, Utils.getAppNameFromPackage(this,packageName));
        remoteViewsExpand.setTextViewText(R.id.txt_noti_activity_name, activityName);
        remoteViewsExpand.setTextViewText(R.id.txt_noti_package, packageName);

        NotificationBuilder.with().initCustomLayout(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOpenAppIntent(openAppIntent)
                .buildCollapseExpandLayout(remoteViewsCollapse, remoteViewsExpand, 1, true);


    }



    void logContentView(View parent, String indent) {
        Log.i("test", indent + parent.getClass().getName());
        if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)parent;
            for (int i = 0; i < group.getChildCount(); i++)
                logContentView(group.getChildAt(i), indent + " ");
        }
    }


    public static void logViewHierarchy(AccessibilityNodeInfo nodeInfo, final int depth) {

        if (nodeInfo == null) return;

        String spacerString = "";

        for (int i = 0; i < depth; ++i) {
            spacerString += '-';
        }
        //Log the info you care about here... I choce classname and view resource name, because they are simple, but interesting.
        Log.i("Access", spacerString + nodeInfo.getClassName() + " " + nodeInfo.getLabelFor().getParent() + " "+nodeInfo.getViewIdResourceName());

        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            logViewHierarchy(nodeInfo.getChild(i), depth+1);
        }
    }



    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {}
}