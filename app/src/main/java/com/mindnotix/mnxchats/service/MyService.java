package com.mindnotix.mnxchats.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mindnotix.mnxchats.connection.MyXMPP;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.Pref;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sridharan on 11/23/2017.
 */

public class MyService extends Service {

    /**
     * Broadcasted when a last activity iq is received.
     * Send this intent to request a last activity.
     */
    public static final String ACTION_LAST_ACTIVITY = "mindnotix.action.LAST_ACTIVITY";
    // common parameters
    public static final String EXTRA_PACKET_ID = "mindnotix.packet.id";
    public static final String EXTRA_TYPE = "mindnotix.packet.type";
    public static final String EXTRA_ERROR_CONDITION = "mindnotix.packet.error.condition";

    // use with mindnotix.action.PRESENCE/SUBSCRIBED
    public static final String EXTRA_FROM = "mindnotix.stanza.from";
    public static final String EXTRA_TO = "mindnotix.stanza.to";
    public static final String EXTRA_STATUS = "mindnotix.presence.status";
    public static final String EXTRA_SHOW = "mindnotix.presence.show";
    public static final String EXTRA_PRIORITY = "mindnotix.presence.priority";
    public static final String EXTRA_PRIVACY = "mindnotix.presence.privacy";
    public static final String EXTRA_FINGERPRINT = "mindnotix.presence.fingerprint";
    public static final String EXTRA_SUBSCRIBED_FROM = "mindnotix.presence.subscribed.from";
    public static final String EXTRA_SUBSCRIBED_TO = "mindnotix.presence.subscribed.to";
    public static final String EXTRA_STAMP = "mindnotix.packet.delay";

    // use with mindnotix.action.ROSTER(_MATCH)
    public static final String EXTRA_JIDLIST = "mindnotix.roster.JIDList";
    public static final String EXTRA_ROSTER_NAME = "mindnotix.roster.name";

    // use with mindnotix.action.LAST_ACTIVITY
    public static final String EXTRA_SECONDS = "mindnotix.last.seconds";

    // use with mindnotix.action.VCARD
    public static final String EXTRA_PUBLIC_KEY = "mindnotix.vcard.publicKey";

    // used with mindnotix.action.BLOCKLIST
    public static final String EXTRA_BLOCKLIST = "mindnotix.blocklist";

    // used with mindnotix.action.IMPORT_KEYPAIR
    public static final String EXTRA_KEYPACK = "mindnotix.keypack";
    public static final String EXTRA_PASSPHRASE = "mindnotix.passphrase";

    // used with mindnotix.action.VERSION
    public static final String EXTRA_VERSION_NAME = "mindnotix.version.name";
    public static final String EXTRA_VERSION_NUMBER = "mindnotix.version.number";

    // created in onCreate
    private PowerManager.WakeLock mWakeLock;
    private PowerManager.WakeLock mPingLock;
    public LocalBroadcastManager mLocalBroadcastManager;
    private AlarmManager mAlarmManager;

    public static final String TAG ="MyService" ;
    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;

    MyXMPP myXMPP;
    public MyService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public boolean isStarted() {
        return mLocalBroadcastManager != null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent != null){
            final String uname = intent.getStringExtra("uname");
            final String passs = intent.getStringExtra("pass");
            final String name = intent.getStringExtra("name");
            Log.d("Login_mys_t",""+uname);
            Log.d("uname_is",""+uname);
            Log.d("uname_is",""+passs);

            myXMPP = MyXMPP.getInstance(MyService.this, Constant.HOST, uname, passs,name);
            myXMPP.connect("onCreate");
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ondestroy!");

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Log.d(TAG, "onTaskRemoved");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.putExtra("uname", Pref.getPreferenceUser());
        restartServiceIntent.putExtra("pass", Pref.getPreferencePass());
        restartServiceIntent.putExtra("name", Pref.getPreferenceUserProfileName());

        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
