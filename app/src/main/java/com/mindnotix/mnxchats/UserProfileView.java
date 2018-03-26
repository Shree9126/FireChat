package com.mindnotix.mnxchats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.utils.Pref;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sridharan on 12/12/2017.
 */

public class UserProfileView extends BaseActivity {


    TextView title3;
    View v1,v2;
    TextView mynumber,my_name;
    TextView title2,status,title;
    CircleImageView ivprofile;
    //This is our toolbar
    Toolbar toolbar;

    private String name =null;
    private String presence_status =null;
    private String mobile =null;
    private String image_path =null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UiInitialization();

        if(getIntent() != null){

            name = getIntent().getStringExtra("name");
            mobile = getIntent().getStringExtra("mobile");
            presence_status = getIntent().getStringExtra("presence_status");
            image_path = getIntent().getStringExtra("image_path");

            my_name.setText(name);
            mynumber.setText(presence_status);
            status.setText(mobile);

            Picasso.with(this)
                    .load(image_path)
                    .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(ivprofile);
        }
    }


    public void profileView(View view) {

        Intent intent = new Intent(UserProfileView.this,ProfileImageUpdateActivity.class);
        intent.putExtra("userType",0);
        intent.putExtra("image_path",image_path);
        startActivity(intent);
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

        title = (TextView)  findViewById(R.id.title);
        ivprofile = (CircleImageView)  findViewById(R.id.iv_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

             default:
                onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
