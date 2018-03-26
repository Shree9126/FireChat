package com.mindnotix.mnxchats;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;

import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.service.MyService;
import com.mindnotix.mnxchats.utils.Pref;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);


        CheckSession();
    }

    private void CheckSession() {

        if (Pref.getPreferenceUser() != null) {

            Intent myServiceIntent = new Intent(SplashActivity.this, MyService.class);
            myServiceIntent.putExtra("uname", Pref.getPreferenceUser());
            myServiceIntent.putExtra("pass", Pref.getPreferencePass());
            myServiceIntent.putExtra("name", Pref.getPreferenceUserProfileName());
            startService(myServiceIntent);


            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {

                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();


            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        } else {
            Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }
}
