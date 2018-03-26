package com.mindnotix.mnxchats;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.activeandroid.SqliteDatabaseManager;
import com.mindnotix.mnxchats.adapter.ChatConversationAdapter;
import com.mindnotix.mnxchats.adapter.MyContactsAdapter;
import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;
import com.mindnotix.mnxchats.listerner.XMPPConnectionListener;
import com.mindnotix.mnxchats.model.BasicResponse;
import com.mindnotix.mnxchats.model.User;
import com.mindnotix.mnxchats.network.ApiClient;
import com.mindnotix.mnxchats.network.ChatMindAPI;
import com.mindnotix.mnxchats.openfire.OpenfireService;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.Pref;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sridharan on 11/24/2017.
 */

public class ContactActivity extends BaseActivity {

    private static final String TAG = "ContactActivity";
    SwipeRefreshLayout swipelayout;
    TextView txtEmpty;
    RecyclerView recyclerView;
    ArrayList<User> userArrayList = new ArrayList<User>();
    ArrayList<MyContacts> myContactsArrayList = new ArrayList<MyContacts>();//db
    ArrayList<MyContacts> myContactsArrayListSwife = new ArrayList<MyContacts>();//db
    MyContactsAdapter myContactsAdapter;
    Toolbar toolbar;
    SqliteDatabaseManager databaseManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        GlobalBus.getBus().register(this);
        UiInitialization();

        myContactsArrayList.clear();
        myContactsArrayList = (ArrayList<MyContacts>) databaseManager.getALLContacts();

        Log.d(TAG, "onCreate: mycontacts size" + myContactsArrayList.size());
        if (myContactsArrayList.size() == 0) {
            /*GetMyContacts task = new GetMyContacts();
            task.execute();*/
            getContacts();
        } else {

            txtEmpty.setVisibility(View.INVISIBLE);
            myContactsAdapter = new MyContactsAdapter(ContactActivity.this, myContactsArrayList, "0");
            recyclerView.setAdapter(myContactsAdapter);
            swipelayout.setRefreshing(false);
        }

        swipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onRefresh: safdsfasdfsd");
                        /*GetMyContacts task = new GetMyContacts();
                        task.execute();*/
                        getContacts();
                    }
                }, 2500);
            }
        });

    }

    private void UiInitialization() {

        databaseManager = new SqliteDatabaseManager();
        swipelayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        txtEmpty = (TextView) findViewById(R.id.txtEmpty);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Contacts");
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


    private class GetMyContacts extends AsyncTask<Integer, Void, String> {


        @Override
        protected String doInBackground(Integer... params) {

            getContacts();
            return null;
        }

        @Override
        protected void onPostExecute(String bitmap) {

        }

    }
    public void getContacts() {

        myContactsArrayListSwife.clear();
        //  myContactsArrayList.clear();
        userArrayList.clear();
        // databaseManager.delContacts();

        try {

            ChatMindAPI chatMindAPI = ApiClient.getClient(Constant.BASE_URL).create(ChatMindAPI.class);
            Call<BasicResponse> call = chatMindAPI.getusers();

            System.out.println("all_answer_url= " + call.request().url());

            call.enqueue(new Callback<BasicResponse>() {

                @Override
                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {

                    Log.d(TAG, "onResponse:getUserArrayList " + response.body().getUser().size());
                    userArrayList.addAll(response.body().getUser());
                    swipelayout.setRefreshing(false);
                    Log.d(TAG, "onResponse: userArrayList" + userArrayList.size());

                    MyContacts myContacts;
                    boolean iscount =false;
                    for (int i=0;i<userArrayList.size();i++){

                        iscount = false;
                        for (int j=0;j<myContactsArrayList.size();j++){

                            if(myContactsArrayList.get(j).getJid().equals(userArrayList.get(i).getUsername())){

                                iscount = true;
                                break;
                            }
                        }
                        if(!iscount){


                            if (!Pref.getPreferenceUser().equals(userArrayList.get(i).getUsername()) &&
                                    !userArrayList.get(i).getUsername().equals("admin")) {

                                myContacts = new MyContacts();
                                myContacts.setJid(userArrayList.get(i).getUsername());
                                myContacts.setEmail(userArrayList.get(i).getEmail());
                                myContacts.setUname(userArrayList.get(i).getName());
                                myContacts.setType("Friend");
                                myContacts.setBlock_status("0");
                                myContacts.setMute_status("0");
                                myContacts.setContact_status("1");
                                myContacts.setRoomLeaveStatus("0");
                                myContacts.setStatus(OpenfireService.GetUserStatus(userArrayList.get(i).getUsername()));
                                myContacts.save();
                            }
                        }

                    }

                    myContactsArrayList.clear();
                    myContactsArrayList = (ArrayList<MyContacts>) databaseManager.getALLContacts();
                    if (myContactsArrayList.size() == 0) {
                       // getContacts();
                    } else {

                        txtEmpty.setVisibility(View.INVISIBLE);
                        myContactsAdapter = new MyContactsAdapter(ContactActivity.this, myContactsArrayList, "0");
                        recyclerView.setAdapter(myContactsAdapter);
                        swipelayout.setRefreshing(false);
                    }

                }

                @Override
                public void onFailure(Call<BasicResponse> call, Throwable t) {
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Event bus event trigger here..


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ActivityUserStatusChange userStatusChange) {

        Log.d(TAG, "getMessage:GroupName " + userStatusChange.getStatus());

        for (int i = 0; i < myContactsArrayList.size(); i++) {
            if (myContactsArrayList.get(i).getJid().equals(userStatusChange.getFromname())) {

                MyContacts myContacts = myContactsArrayList.get(i);
                myContacts.setStatus(userStatusChange.getStatus());
                myContactsAdapter.notifyItemChanged(i);

            }
        }

    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ActivityUserProfileChange activityUserProfileChange) {

        Log.d(TAG, "getMessage:getProfileImg " + activityUserProfileChange.getProfileImg());

        for (int i = 0; i < myContactsArrayList.size(); i++) {
            if (myContactsArrayList.get(i).getJid().equals(activityUserProfileChange.getFromname())) {

                MyContacts myContacts = myContactsArrayList.get(i);
                myContacts.setImage_path(activityUserProfileChange.getProfileImg());
                myContactsAdapter.notifyItemChanged(i);

            }
        }

    }

    //Event bus
    //Block/Unblock status append in array list
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ChatActivityBlockUnblockStatus chatActivityBlockUnblockStatus) {

        Log.d(TAG, "getMessage:chatActivityBlockUnblockStatus " + chatActivityBlockUnblockStatus.getBlock_status());
        Log.d(TAG, "getMessage:chatActivityBlockUnblockStatus " + chatActivityBlockUnblockStatus.getItem_position());
        if (chatActivityBlockUnblockStatus.getBlock_status() != null) {

            myContactsArrayList.get(chatActivityBlockUnblockStatus.getItem_position()).setBlock_status(chatActivityBlockUnblockStatus.getBlock_status());
            if (myContactsAdapter != null)
                myContactsAdapter.notifyDataSetChanged();
        }

    }


    //Event bus
    //Block/Unblock status append in array list
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ChatActivityMuteImmuteStatus chatActivityMuteImmuteStatus) {

        Log.d(TAG, "getMessage:chatActivityMuteImmuteStatus " + chatActivityMuteImmuteStatus.getMute_status());
        Log.d(TAG, "getMessage:chatActivityMuteImmuteStatus " + chatActivityMuteImmuteStatus.getItem_position());
        if (chatActivityMuteImmuteStatus.getMute_status() != null) {

            myContactsArrayList.get(chatActivityMuteImmuteStatus.getItem_position()).setBlock_status(chatActivityMuteImmuteStatus.getMute_status());
            if (myContactsAdapter != null)
                myContactsAdapter.notifyDataSetChanged();
        }

    }
}
