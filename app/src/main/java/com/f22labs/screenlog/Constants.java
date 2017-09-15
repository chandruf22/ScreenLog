package com.f22labs.screenlog;

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
}
