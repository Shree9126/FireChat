package com.mindnotix.mnxchats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;
import com.mindnotix.mnxchats.model.UploadImage;
import com.mindnotix.mnxchats.network.ChatMindAPI;
import com.mindnotix.mnxchats.network.MyServerAPI;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.ImageFilePath;
import com.mindnotix.mnxchats.utils.Pref;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sridharan on 11/27/2017.
 */

public class UserProfileActivity extends BaseActivity {

    private static final String TAG ="UserProfileActivity" ;
    ListView liststatus;
    TextView title3;
    View v1,v2;
    TextView mynumber,my_name;
    TextView title2,status,title;
    CircleImageView ivprofile;
    //This is our toolbar
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        GlobalBus.getBus().register(this);
        UiInitialization();



    }

    private void UiInitialization() {

       // liststatus =  findViewById(R.id.list_status);
        title3 = (TextView) findViewById(R.id.title3);
        v2 = findViewById(R.id.v2);
        mynumber = (TextView)  findViewById(R.id.mynumber);
        my_name = (TextView)  findViewById(R.id.my_name);
        if(Pref.getPreferenceUserProfileName() != null)
        my_name.setText(Pref.getPreferenceUserProfileName());

        title2 = (TextView)  findViewById(R.id.title2);
        v1 =  findViewById(R.id.v1);
        status = (TextView) findViewById(R.id.status);
        if(Pref.getPreferenceUser() != null)
            status.setText(Pref.getPreferenceUser());
        if(Pref.getPreferenceStatus() != null)
            mynumber.setText(Pref.getPreferenceStatus());
        title = (TextView)  findViewById(R.id.title);
        ivprofile = (CircleImageView)  findViewById(R.id.iv_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(Pref.getProfileImg() != null)
            Picasso.with(this)
                    .load(Pref.getProfileImg())
                    .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(ivprofile);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            default:

                onBackPressed();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




    public void profileView(View view) {

        Intent intent = new Intent(UserProfileActivity.this,ProfileImageUpdateActivity.class);
        intent.putExtra("userType",1);
        intent.putExtra("image_path",Pref.getProfileImg());
        startActivity(intent);
    }

    //Event bus
    //Profile image refreshment
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.MyProfilePictureChange myProfilePictureChange) {

        Log.d(TAG, "getMessage:getProfileImg " + myProfilePictureChange.getProfileImg());

//        if(myProfilePictureChange.getProfileImg() != null){

            Picasso.with(this)
                    .load(myProfilePictureChange.getProfileImg())
                    .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(ivprofile);

        }

}
