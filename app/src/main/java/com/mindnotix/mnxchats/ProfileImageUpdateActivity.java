package com.mindnotix.mnxchats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mindnotix.mnxchats.adapter.ProfileImageBottomSheetAdapter;
import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;
import com.mindnotix.mnxchats.model.ProfileImageEditModel;
import com.mindnotix.mnxchats.model.UploadImage;
import com.mindnotix.mnxchats.network.ChatMindAPI;
import com.mindnotix.mnxchats.network.MyServerAPI;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.ImageFilePath;
import com.mindnotix.mnxchats.utils.Pref;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sridharan on 12/12/2017.
 */

public class ProfileImageUpdateActivity extends BaseActivity {

    private static final String TAG = "ProfileImgUpload";
    private static final int PICK_GALLERY_IMAGE_REQUEST_CODE = 200;
    private final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 300;
    CoordinatorLayout coordinatorLayout;
    String image_path;
    int userType;
    private ImageView profileimage;
    private Toolbar toolbar;
    private BottomSheetBehavior<View> behavior;
    private BottomSheetDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);


        UiInitialization();

        if (getIntent() != null) {
            userType = getIntent().getIntExtra("userType", 0);
            image_path = getIntent().getStringExtra("image_path");
            Log.d(TAG, "onCreate: userType " + userType);
            Log.d(TAG, "onCreate: image_path " + image_path);

            Picasso.with(this)
                    .load(image_path)
                    .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(this.profileimage);

        }


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Profile Image");
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    private void UiInitialization() {

        this.profileimage = (ImageView) findViewById(R.id.profile_image);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        MenuItem edit_Image = menu.findItem(R.id.edit_profile_image);

        // show the button when some condition is true
        if (userType == 0) {
            edit_Image.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.edit_profile_image:
                createDialog();
                break;

            default:
                onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return true;
        }

        return false;
    }

    private void createDialog() {
        if (dismissDialog()) {
            return;
        }

        List<ProfileImageEditModel> list = new ArrayList<>();
        list.add(new ProfileImageEditModel(R.string.gallery, R.drawable.gallery));
        list.add(new ProfileImageEditModel(R.string.remove, R.drawable.delete));

        ProfileImageBottomSheetAdapter adapter = new ProfileImageBottomSheetAdapter(list);
        adapter.setOnItemClickListener(new ProfileImageBottomSheetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProfileImageBottomSheetAdapter.ItemHolder item, int position) {
                //dismissDialog();

                switch (position) {
                    case 0:
                        profileClicked();
                        break;
                    case 1:
                        Pref.setProfileImg(null);

                        Events.MyProfilePictureChange activityUserProfileChange =
                                new Events.MyProfilePictureChange(Pref.getProfileImg());

                        GlobalBus.getBus().postSticky(activityUserProfileChange);

                        Picasso.with(ProfileImageUpdateActivity.this)
                                .load(Pref.getProfileImg())
                                .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                                .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                                .into(profileimage);

                        dismissDialog();
                        break;

                    default:

                }
            }
        });

        View view = getLayoutInflater().inflate(R.layout.bottomsheetlayout, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }


    public void profileClicked() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Pic"), PICK_GALLERY_IMAGE_REQUEST_CODE);
        dismissDialog();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PICK_GALLERY_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    Uri selectedImageUri = data.getData();
                    if (null != selectedImageUri) {

                        Log.d(TAG, "onActivityResult: selectedImageUri " + selectedImageUri);


                        String realPath = ImageFilePath.getPath(ProfileImageUpdateActivity.this, selectedImageUri);
                        Log.i(TAG, "onActivityResult: Image Path_s_realPath : " + realPath);


                        Picasso.with(this)
                                .load(selectedImageUri)
                                .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                                .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                                .into(this.profileimage);


                        uploadProfile(realPath);


                    }
                    break;
                }
        }

    }

    public void uploadProfile(String mediaPath) {
        if (!mediaPath.equals("") && !mediaPath.equals(null)) {

            Map<String, RequestBody> map = new HashMap<>();

            Log.v("befyebfgeuhfguyh", mediaPath);

            File file = new File(mediaPath);


            Log.v("befyebfgeuhfguyh", file.getName());
            ChatMindAPI kspotAPI = MyServerAPI.getClient(Constant.BASE_URL_MY_SERVER).create(ChatMindAPI.class);
            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            Call call = kspotAPI.uploadChannelPost(fileToUpload, filename);

            Log.d(TAG, "uploadProfile_URL: " + call.request().url());


            call.enqueue(new Callback<UploadImage>() {
                @Override
                public void onResponse(Call<UploadImage> call, Response<UploadImage> response) {

                    try {

                        final UploadImage basicResponse = response.body();
                        Log.d(TAG, "onResponse:getSuccess_s " + basicResponse.getSuccess());
                        Log.d(TAG, "onResponse:getSuccess_m " + basicResponse.getMessage());
                        Log.d(TAG, "onResponse:getSuccess_n " + basicResponse.getPhotoname());

                        if (basicResponse.getSuccess()) {

                            openfireService.changeProfile(basicResponse.getPhotoname());
                            //   Utils.changeProfile(basicResponse.getPhotoname());
                            Toast.makeText(ProfileImageUpdateActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Pref.setProfileImg(Constant.IMG_PATH + basicResponse.getPhotoname());

                            Events.MyProfilePictureChange activityUserProfileChange =
                                    new Events.MyProfilePictureChange(Pref.getProfileImg());

                            GlobalBus.getBus().postSticky(activityUserProfileChange);

                        } else {
                            Toast.makeText(ProfileImageUpdateActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                    }

                }

                @Override
                public void onFailure(Call<UploadImage> call, Throwable error) {
                    Log.d(TAG, "MyServerAPI:" + error.getMessage());
                }

            });
        } else {

            Toast.makeText(ProfileImageUpdateActivity.this, "Please select cover photo", Toast.LENGTH_SHORT).show();
        }
    }

}
