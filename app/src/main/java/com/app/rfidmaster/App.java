package com.app.rfidmaster;

import android.app.Application;
import android.content.Context;

import com.app.rfidmaster.rfid.RFIDManager;
import com.tencent.bugly.crashreport.CrashReport;


public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        CrashReport.initCrashReport(getApplicationContext(), "04c37ba196", false);

        RFIDManager.getInstance();
    }

    @Override
    public void onTerminate() {
        RFIDManager.getInstance().destroy();
        super.onTerminate();
    }

    public static Context getContext() {
        return mContext;
    }
}
