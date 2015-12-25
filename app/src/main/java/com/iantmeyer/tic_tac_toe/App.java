package com.iantmeyer.tic_tac_toe;

import android.app.Application;
import android.content.Context;

/**
 * Created by ianmeyer on 12/19/15.
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getBaseContext();

    }

    public static Context getAppContext() {
        return mContext;
    }
}
