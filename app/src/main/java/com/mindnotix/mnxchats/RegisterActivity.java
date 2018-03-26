package com.mindnotix.mnxchats;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.model.BasicResponse;
import com.mindnotix.mnxchats.model.RegisterRequest;
import com.mindnotix.mnxchats.model.User;
import com.mindnotix.mnxchats.network.ApiClient;
import com.mindnotix.mnxchats.network.ChatMindAPI;
import com.mindnotix.mnxchats.openfire.OpenfireService;
import com.mindnotix.mnxchats.service.MyService;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.Pref;
import com.mindnotix.mnxchats.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sridharan on 11/23/2017.
 */

public class RegisterActivity extends BaseActivity {


    private static final String TAG ="RegisterActivity" ;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = { android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE,  android.Manifest.permission.INTERNET};

    FloatingActionButton fbdone;
    LinearLayout laymain;
    EditText etMnum;
    EditText etUname;
    ImageView ivcam;
    MyService mMyService;
    public static Intent mServiceIntent;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UiInitializations();


        mMyService = new MyService();
    }


    private void UiInitializations() {


        fbdone = (FloatingActionButton) findViewById(R.id.fb_done);
        laymain = (LinearLayout) findViewById(R.id.lay_main);
        etMnum = (EditText) findViewById(R.id.et_Mnum);
        etUname = (EditText) findViewById(R.id.et_Uname);
        ivcam = (ImageView) findViewById(R.id.iv_cam);
     


    }
    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);

        if (!Utils.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }



    }



    public void fabClicked(View view) {
        chkValidation();
    }

    public void uploadPhotoClicked(View view) {
    }

    private void chkValidation() {


        if(!Utils.hasText(etUname)){
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
        }
        else if(!Utils.hasText(etMnum)){
            Toast.makeText(this, "Enter your mobile number", Toast.LENGTH_SHORT).show();
        }else if(!Utils.isValidMobile(etMnum.getText().toString())){
            Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
        }else {
            
            register();
        }
    }

    private void register() {


      final String uname =  etMnum.getText().toString();
      final String name  =  etUname.getText().toString();
            String email =  etMnum.getText().toString() + Constant.MAIL;

        Log.d(TAG, "register: uname "+uname);
        Log.d(TAG, "register: name "+name);
        Log.d(TAG, "register: email "+email);
        Log.d(TAG, "register: pass "+Constant.PASSWORD_DEFUALT);

        ChatMindAPI chatMindAPI = ApiClient.getClient(Constant.BASE_URL).create(ChatMindAPI.class);
        Call<Void> call = chatMindAPI.postusers(new RegisterRequest(uname,name,email,Constant.PASSWORD_DEFUALT));

        Log.d(TAG, "registerProcess: "+call.request().url());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

              //  Toast.makeText(RegisterActivity.this, "Register successfully..!", Toast.LENGTH_SHORT).show();

                Boolean isLoginSuccess = true;

                final Boolean result = isLoginSuccess;

                startcommandservice(uname,name);



            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Toast.makeText(RegisterActivity.this, "Register failed. Try again..!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void startcommandservice(String uname, String name) {
        Log.d(TAG, "register_startcommandservice: "+uname);
        mServiceIntent = new Intent(RegisterActivity.this, MyService.class);
        mServiceIntent.putExtra("uname", uname);
        mServiceIntent.putExtra("pass", Constant.PASSWORD_DEFUALT);
        mServiceIntent.putExtra("name", name);

        if (!isMyServiceRunning(mMyService.getClass())) {
            startService(mServiceIntent);
        }else{
            startService(mServiceIntent);
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(TAG, "isMyServiceRunning?" + true + "");
                return true;
            }
        }
        Log.i(TAG, "isMyServiceRunning?" + false + "");
        startService(mServiceIntent);
        return false;
    }
}
