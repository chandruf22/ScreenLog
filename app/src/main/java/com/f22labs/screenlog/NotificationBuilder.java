package com.f22labs.screenlog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;


/**
 * Created by chandrasekar on 14/01/16.
 */
public class NotificationBuilder {


    private Context mContext;
    private NotificationCompat.Builder mBuilder;
    private NotificationCompat.BigPictureStyle mBigPictureStyle;

    public static NotificationBuilder with() {
      //  if (mInstance == null)
            return new NotificationBuilder();

    }

    public NotificationBuilder init(Context context) {
        this.mContext = context;
        mBuilder = new NotificationCompat.Builder(mContext);
        return this;
    }

    public NotificationBuilder initCustomLayout(Context context) {
        init(context);
        mBigPictureStyle = new NotificationCompat.BigPictureStyle();
        return this;
    }

    public NotificationBuilder setTitle(String title) {
        mBuilder.setContentTitle(title);
        return this;
    }

    public NotificationBuilder setMessage(String message) {
        mBuilder.setContentText(message);
        return this;
    }

    public NotificationBuilder setSmallIcon(int icon) {
        mBuilder.setSmallIcon(icon);
        return this;
    }

    public NotificationBuilder setBigICon(Bitmap icon) {
        mBuilder.setLargeIcon(icon);
        return this;
    }

    public NotificationBuilder setAutoCancel(boolean isAutoCancel) {
        mBuilder.setAutoCancel(isAutoCancel);
        return this;
    }

    public NotificationBuilder setPriority(int priority) {
        mBuilder.setPriority(priority);
        return this;
    }

    public NotificationBuilder setBigTitle(String bigTitle) {
        mBigPictureStyle.setBigContentTitle(bigTitle);
        return this;
    }


    public NotificationBuilder setBigMessage(String summaryText) {
        mBigPictureStyle.setSummaryText(summaryText);
        return this;
    }

    public NotificationBuilder setBigPicture(Bitmap bigPicture) {
        mBigPictureStyle.bigPicture(bigPicture);
        mBuilder.setStyle(mBigPictureStyle);
        return this;
    }

    public NotificationBuilder setSwipeToDismiss(boolean dismiss){
        mBuilder.setOngoing(dismiss);

        return this;
    }

    public NotificationBuilder setDeleteIntent(PendingIntent swipeDismissIntent){
        mBuilder.setDeleteIntent(swipeDismissIntent);
        return this;
    }

    public Notification build() {
        Notification notification = mBuilder.build();
        return notification;
    }

    public Notification buildExpandLayout(RemoteViews expandViews,int notificationID,boolean isSwipeable) {
        Notification notification = mBuilder.build();
        notification.bigContentView = expandViews;
        if(!isSwipeable)
            notification.flags = Notification.FLAG_ONGOING_EVENT;
        show(mContext,notificationID,notification);

        return notification;
    }

    public Notification buildCollapseLayout(RemoteViews collapseViews,int notificationID,boolean isSwipeable) {
        Notification notification = mBuilder.build();
        notification.contentView = collapseViews;
        if(!isSwipeable)
            notification.flags = Notification.FLAG_ONGOING_EVENT;

        show(mContext,notificationID,notification);
        return notification;
    }

    public Notification buildCollapseExpandLayout(RemoteViews collapseViews,RemoteViews expandViews,int notificationID,boolean isSwipeable) {
        Notification notification = mBuilder.build();

            notification.bigContentView = expandViews;
            notification.contentView = collapseViews;

        if(!isSwipeable)
            notification.flags = Notification.FLAG_ONGOING_EVENT;

        show(mContext,notificationID,notification);

        return notification;
    }




    public static void notify(int id, NotificationManager notificationManager, Notification notification){
        notificationManager.notify(id,notification);
    }

    public static void cancel(Context context,int id) {
        NotificationManagerCompat.from(context).cancel(id);
    }

    private static void show(Context context,int id, Notification notification) {
        NotificationManagerCompat.from(context).notify(id, notification);
    }

    private static <T>Intent getIntent(Context context, String action, Class<T> toClass) {
        Intent intent = new Intent(context, toClass);
        intent.setAction(action);
        return intent;
    }

    private static <T>Intent getIntent(Context context, String action, Class<T> toClass, int position) {

        Intent intent = new Intent(context, toClass);
        intent.setAction(action);
        intent.putExtra("position",position);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))); // embed extras so they don't get ignored

        return intent;
    }

    public static <T>PendingIntent getPendingIntent(Context context,String action, Class<T> toClass,int position){
        return PendingIntent.getService(context, 0,
                getIntent(context,action,toClass,position), 0);
    }

    public static <T>PendingIntent getPendingIntent(Context context,String action, Class<T> toClass){
        return PendingIntent.getService(context, 0,
                getIntent(context,action,toClass), 0);
    }

    public static <T> void startActivity(Context context, Class<T> toClass) {

        Intent videoIntent = new Intent(context, toClass);
        videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(videoIntent);

    }


//    public static void loadImagesIntoNotification(Context context, String url, RemoteViews remoteViews, int imageViewID, Notification notification, int notificationID) {
//
//
//        NotificationTarget notificationTarget = new NotificationTarget(
//                context,
//                remoteViews,
//                imageViewID,
//                notification,
//                notificationID);
//
//        if (!TextUtils.isEmpty(url)) {
//            Glide
//                    .with(context)
//                    .load(url)
//                    .asBitmap()
//                    .placeholder(R.drawable.ic_placeholder_notification)
//                    .fitCenter()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(notificationTarget);
//        } else {
//            Glide
//                    .with(context)
//                    .load(R.drawable.ic_placeholder_notification)
//                    .asBitmap()
//                    .placeholder(R.drawable.ic_placeholder_notification)
//                    .fitCenter()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(notificationTarget);
//        }
//    }
//
//
//    public static void loadIconsIntoNotification(Context context, String url, RemoteViews remoteViews, int imageViewID, Notification notification, int notificationID) {
//
//
//        NotificationTarget notificationTarget = new NotificationTarget(
//                context,
//                remoteViews,
//                imageViewID,
//                notification,
//                notificationID);
//
//        if (!TextUtils.isEmpty(url)) {
//
//            Glide
//                    .with(context)
//                    .load(url)
//                    .asBitmap()
//                    .placeholder(R.drawable.ic_launcher)
//                    .fitCenter()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(notificationTarget);
//
//        } else {
//            Glide
//                    .with(context)
//                    .load(R.drawable.ic_launcher)
//                    .asBitmap()
//                    .placeholder(R.drawable.ic_launcher)
//                    .fitCenter()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(notificationTarget);
//
//        }
//    }





}
