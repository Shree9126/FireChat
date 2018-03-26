package com.mindnotix.mnxchats;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mindnotix.mnxchats.activeandroid.SqliteDatabaseManager;
import com.mindnotix.mnxchats.openfire.OpenfireService;
import com.mindnotix.mnxchats.reciever.ConnectivityReceiver;
import com.mindnotix.mnxchats.service.MyService;
import com.mindnotix.mnxchats.utils.Pref;
import com.mindnotix.mnxchats.utils.Utils;

/**
 * Created by Admin on 22-11-2017.
 */


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    OpenfireService openfireService;
    private static final String TAG ="BaseActivity" ;
    public SqliteDatabaseManager databaseManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = new SqliteDatabaseManager();
        openfireService = new OpenfireService();
        checkConnection();
    }

    // Method to manually check connection status
    public void checkConnection() {

        boolean isConnected = ConnectivityReceiver.isConnected();
        ShowDialog(isConnected);
    }

    // Showing the status in Snackbar
    private void ShowDialog(boolean isConnected) {
        String message = null;
        int color;
        if (isConnected) {
           // message = "Good! Connected to Internet";
            color = Color.WHITE;
            ConnectXMPPAsyn task = new ConnectXMPPAsyn();
            task.execute();


        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
            Utils.showDialogDetails("Internet Connection",message,this);
        }
      //  Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ShowDialog(isConnected);
    }

    class ConnectXMPPAsyn extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... voids) {


            Log.d(TAG, "doInBackground: baseactivity call service");

            if(Pref.getPreferenceUser() != null){

                Intent myServiceIntent = new Intent(BaseActivity.this, MyService.class);
                myServiceIntent.putExtra("uname", Pref.getPreferenceUser());
                myServiceIntent.putExtra("pass", Pref.getPreferencePass());
                myServiceIntent.putExtra("name", Pref.getPreferenceUserProfileName());
                startService(myServiceIntent);
            }

            
            return null;
        }
    }

}
