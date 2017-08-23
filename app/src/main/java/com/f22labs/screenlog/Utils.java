package com.f22labs.screenlog;

/**
 * Created by f22labs on 18/08/17.
 */




import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

    /**
     * Created by chandrasekar on 26/07/16.
     */

    public class Utils {


        private static final char[] hexDigits = "0123456789abcdef".toCharArray();

        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        public static boolean isAppAlreadyInstalled(String packageName, Context context) {
            PackageManager pm = context.getPackageManager();
            boolean installed;
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                installed = false;
            }
            return installed;
        }

        public static boolean isPackageSystemApp(Context context, String packageName) {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(packageName, 0);
                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
            return false;
        }

        public static String md5(File fileToCheck) {
            String md5 = "";

            try {
                byte[] bytes = new byte[4096];
                int read;
                MessageDigest digest = MessageDigest.getInstance("MD5");

                FileInputStream is = new FileInputStream(fileToCheck);
                while ((read = is.read(bytes)) != -1) {
                    digest.update(bytes, 0, read);
                }

                byte[] messageDigest = digest.digest();

                StringBuilder sb = new StringBuilder(32);

                for (byte b : messageDigest) {
                    sb.append(hexDigits[(b >> 4) & 0x0f]);
                    sb.append(hexDigits[b & 0x0f]);
                }

                md5 = sb.toString();
                is.close();
            } catch (Exception e) {
            }
            return md5;
        }




        public static boolean isFolderExists(String folderPath) {


            File folderDirectory = new File(folderPath);

            return folderDirectory.exists();
        }

        public static void makeDirectory(String folderPath) {
            new File(folderPath).mkdirs();
        }


        public static void copyDirectory(File sourceLocation, File targetLocation)
                throws IOException {

            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                    throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
                }

                String[] children = sourceLocation.list();
                for (int i = 0; i < children.length; i++) {
                    copyDirectory(new File(sourceLocation, children[i]),
                            new File(targetLocation, children[i]));

                }
            } else {

                // make sure the directory we plan to store the recording in exists
                File directory = targetLocation.getParentFile();
                if (directory != null && !directory.exists() && !directory.mkdirs()) {
                    throw new IOException("Cannot create dir " + directory.getAbsolutePath());
                }

                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }

        }



        /**
         * Method to get an icon based on the package name. * @param - passing the context of Activity or fragment. * @param packageName * @return
         */
        public static Bitmap getIconFromPackageName(Context context, String packageName) {
            Drawable icon = null;
            try {
                icon = context.getPackageManager().getApplicationIcon(packageName);


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return convertDrawableToBitmap(icon);
        }


        /**
         * Method to get an icon based on the package name. * @param - passing the context of Activity or fragment. * @param packageName * @return
         */
        public static Drawable getIconDrawableFromPackageName(Context context, String packageName) {
            Drawable icon = null;
            try {
                icon = context.getPackageManager().getApplicationIcon(packageName);


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return icon;
        }



        /**
         * Method to convert Drawable into Bitmap * @param drawable - passing the Drawable that should be converted into Bitmap * @return final bitmap processed
         */
        public static Bitmap convertDrawableToBitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        public static String getAppNameFromPackage(Context context, String packageName) {


            try {
                ApplicationInfo app = context.getPackageManager().getApplicationInfo(packageName, 0);

                String appName = String.valueOf(context.getPackageManager().getApplicationLabel(app));

                return !TextUtils.isEmpty(appName) ? appName : "Application";

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return "Application";
        }


        public static void copyToClipboard(Context context, String text) {

            if (!TextUtils.isEmpty(text)) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(text, text);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(context, text + " has been copied", Toast.LENGTH_SHORT).show();
            }


        }



        public static void closeNotificationBar(Context context) {
            Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(intent);
        }



        public static <T>boolean isCheckServiceRunning(Context context,Class<T> serviceClass) {
            ActivityManager manager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager
                    .getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(
                        service.service.getClassName())) {
                    return true;
                }
            }

            return false;
        }


        public static boolean isAccessibilityEnabled(Context context, String id) {

            AccessibilityManager am = (AccessibilityManager) context
                    .getSystemService(Context.ACCESSIBILITY_SERVICE);

            List<AccessibilityServiceInfo> runningServices = am
                    .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
            for (AccessibilityServiceInfo service : runningServices) {
                if (id.equals(service.getId())) {
                    return true;
                }
            }

            return false;
        }


        public static void logInstalledAccessiblityServices(Context context) {

            AccessibilityManager am = (AccessibilityManager) context
                    .getSystemService(Context.ACCESSIBILITY_SERVICE);

            List<AccessibilityServiceInfo> runningServices = am
                    .getInstalledAccessibilityServiceList();
            for (AccessibilityServiceInfo service : runningServices) {
                Log.i("InstalledAccessService", service.getId());
            }
        }


        public static void openAccessibilitySettings(Context context){

            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent);
        }


        public static void showToastShort(Context context,String message){

            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }


        public static void showToastLong(Context context,String message){

            Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }
    }

