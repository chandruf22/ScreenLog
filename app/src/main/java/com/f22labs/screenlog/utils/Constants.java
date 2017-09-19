package com.f22labs.screenlog.utils;

import com.f22labs.screenlog.BuildConfig;

/**
 * Created by Chandru on 14/09/17.
 */

public class Constants {


    public interface INTENT_KEY {


        String PACKAGE = BuildConfig.APPLICATION_ID + ".INTENT_KEY" + "PACKAGE";


        String ACTIVITY_NAME = BuildConfig.APPLICATION_ID + ".INTENT_KEY" + "ACTIVITY_NAME";

        String SCREENSHOT = BuildConfig.APPLICATION_ID + ".INTENT_KEY" + "SCREENSHOT";


        String SCREENSHOT_DATA = BuildConfig.APPLICATION_ID + ".INTENT_KEY" + "SCREENSHOT_DATA";

    }


    public interface RESULT_CODE {


        int SCREENSHOT = 0;

        int SUCCESS = 1;


        int FAILURE = 2;



    }


    public interface FILE {


        String FOLDER_SCREENSHOTS = "ScreenLogs";

        String DELIMITER = "-";

        String PNG = ".png";


    }

    public interface SHARED_PREFS {


        String IS_SCREENSHOT_PERMISSION_ENABLED = BuildConfig.APPLICATION_ID + ".SHARED_PREFS"+ "IS_SCREENSHOT_PERMISSION_ENABLED";


    }
}
