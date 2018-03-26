package com.mindnotix.mnxchats.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.mindnotix.mnxchats.ChatActivity;
import com.mindnotix.mnxchats.reciever.ConnectivityReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 22-11-2017.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks{

    public static List<Activity> activities= new ArrayList<>() ;

    private static final String TAG = "MyApplication";
    private static MyApplication mInstance;
    public static SharedPreferences sharedpreferences;
    public int activityCount = 0;
    public ChatActivity activityChats;
    @Override
    public void onCreate() {
        super.onCreate();
       // MultiDex.install(this);
        sharedpreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mInstance = this;
        ActiveAndroid.initialize(this);
        registerActivityLifecycleCallbacks(this);

    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

        activities.add(activity);

        Log.d(TAG, "onActivityStarted: p "+activity.getPackageName());
        Log.d(TAG, "onActivityStarted: c "+activity.getClass());
        activityCount++;

        if(activity instanceof ChatActivity)
            activityChats = (ChatActivity)activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public void finish(){


        for (Activity activity : new ArrayList<Activity>(activities)) {

            activity.finish();
            activities.remove(activity);
        }
    }
}
