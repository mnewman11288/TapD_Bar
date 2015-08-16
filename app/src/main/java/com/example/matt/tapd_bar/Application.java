package com.example.matt.tapd_bar;

/**
 * Created by Matt on 8/12/2015.
 */

/**
 * Created by Matt on 6/2/2015.
 */
import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Application extends android.app.Application {
    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "AnyWall";

    // Used to pass location from MainActivity to PostActivity
    public static final String INTENT_EXTRA_LOCATION = "Location";

    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";

    private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;

    private static SharedPreferences preferences;

    //private static ConfigHelper configHelper;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();


      //  ParseObject.registerSubclass(ItemMenu.class);
      //  ParseObject.registerSubclass(TapBar.class);



        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "752SAORshXYz5DMFJ1uJtkw8wQkHAa1ha7Msj0KY",
                "eFMmP1RLvnhmOQSYeR7WkrZUgOV8vgWyZQ1Hdlbf");


        // ParseUser.enableAutomaticUser();

		/*
		 * For more information on app security and Parse ACL:
		 * https://www.parse.com/docs/android_guide#security-recommendations
		 */
        ParseACL defaultACL = new ParseACL();

		/*
		 * If you would like all objects to be private by default, remove this
		 * line
		 */
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
        preferences = getSharedPreferences("com.example.matt.myapplication", Context.MODE_PRIVATE);

        //configHelper = new ConfigHelper();
       // configHelper.fetchConfigIfNeeded();
    }

    public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }

   /* public static ConfigHelper getConfigHelper() {
        return configHelper;
    }*/

    public static void setSearchDistance(float value) {
        preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
    }

}

