package com.mindnotix.mnxchats.listerner;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Update;
import com.mindnotix.mnxchats.MainActivity;
import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.activeandroid.SqliteDatabaseManager;
import com.mindnotix.mnxchats.adapter.ChatConversationAdapter;
import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.connection.MyXMPP;
import com.mindnotix.mnxchats.model.BasicResponse;
import com.mindnotix.mnxchats.model.User;
import com.mindnotix.mnxchats.network.ApiClient;
import com.mindnotix.mnxchats.network.ChatMindAPI;
import com.mindnotix.mnxchats.openfire.OpenfireService;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.Pref;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.Async;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 11/23/2017.
 */

public class XMPPConnectionListener extends MyXMPP implements ConnectionListener {

    private static final String TAG = XMPPConnectionListener.class.getSimpleName();
    ArrayList<User> userArrayList = new ArrayList<User>();
    ArrayList<MyContacts> myContactsArrayList = new ArrayList<MyContacts>();//db
    MyXMPP context;
    SqliteDatabaseManager sqliteDatabaseManager;

    public XMPPConnectionListener(MyXMPP myXMPP, SqliteDatabaseManager sqliteDatabaseManager) {
        this.context = myXMPP;
        this.sqliteDatabaseManager = sqliteDatabaseManager;
    }

    @Override
    public void connected(final XMPPConnection connection) {
        Log.d("xmpp", "Connected!");

        connected = true;


        if (!connection.isAuthenticated()) {
            Log.d("Login_Xmpp_auth_", "false");
            login();
        } else {
            Log.d("Login_Xmpp_auth_", "true");
            loggedin = true;
        }
    }

    @Override
    public void connectionClosed() {

        Log.d("xmpp", "ConnectionCLosed!");
        connected = false;
        chat_created = false;
        loggedin = false;
    }

    @Override
    public void connectionClosedOnError(Exception arg0) {


        // Set this up in the UI thread.
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub


                Toast.makeText(MyApplication.getInstance(), "Server Disconnected/Error", Toast.LENGTH_SHORT).show();

            }
        });



        if (isToasted)
            Log.d("xmpp", "ConnectionClosedOn Error!");

        connected = false;

        chat_created = false;
        loggedin = false;

        CanConnect = true;
        String ss = "test";
        ss = ss + "test";
        Log.d("smack:ClosedOnError", arg0.toString());


    }

    @Override
    public void reconnectingIn(int arg0) {

        Log.d("xmpp", "Reconnectingin " + arg0);

        loggedin = false;
        connected = true;
        if (connection.isAuthenticated()) {
            login();
        } else {

        }

        if (!CanConnect) {
            ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
            reconnectionManager.disableAutomaticReconnection();
        }

        String ss = "test";
        ss = ss + "test";
        Log.d("smack:reconnectingIn", String.valueOf(arg0));

    }

    @Override
    public void reconnectionFailed(Exception arg0) {
        if (isToasted)


            Log.d("xmpp", "ReconnectionFailed!");
        connected = false;

        chat_created = false;
        loggedin = false;
    }

    @Override
    public void reconnectionSuccessful() {
        if (isToasted)
            Log.d("xmpp", "ReconnectionSuccessful");


        connected = true;

        chat_created = false;
        loggedin = false;


        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            if (!connection.isAuthenticated()) {
                connection.login();
            }

            Presence presence = new Presence(Presence.Type.available);
            presence.setMode(Presence.Mode.available);
            connection.sendStanza(presence);
            IsLoggedIn = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void authenticated(XMPPConnection arg0, boolean arg1) {
        Log.d("xmpp", "Authenticated!");


        chat_created = false;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    Thread.sleep(5000);
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

                if (Pref.getPreferenceUser() != null) {

                } else {
                    Pref.savePreference(loginUser, passwordUser);
                    Log.d("asdfdsfsa", "TRUE");
                    Log.d("asdfdsfsa", "TRUE" + MyApplication.getInstance().activityCount);

                    myContactsArrayList.clear();
                    myContactsArrayList = (ArrayList<MyContacts>) sqliteDatabaseManager.getALLContacts();

                    Log.d(TAG, "onCreate: mycontacts size" + myContactsArrayList.size());
                    if (myContactsArrayList.size() == 0) {

                        GetMyContacts task = new GetMyContacts();
                        task.execute();

                    }
                    loggedin = true;

                    Intent mIntent = new Intent(MyApplication.getInstance(), MainActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getInstance().startActivity(mIntent);
                    MyApplication.getInstance().finish();
                }


            }
        });
    }

    public void getContacts() {

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

                    Log.d(TAG, "onResponse: userArrayList" + userArrayList.size());
                    Log.d(TAG, "onResponse: userArrayList myContactsArrayList" + myContactsArrayList.size());


                    Log.d(TAG, "onResponse: userArrayList" + userArrayList.size());

                    MyContacts myContacts;

                    for (User contacts : userArrayList) {

                        if (!Pref.getPreferenceUser().equals(contacts.getUsername()) &&
                                !contacts.getUsername().equals("admin")) {

                            Log.d(TAG, "onResponse: jid user "+contacts.getUsername());


                            myContacts = new MyContacts();
                            myContacts.setJid(contacts.getUsername());
                            myContacts.setEmail(contacts.getEmail());
                            myContacts.setUname(contacts.getName());
                            myContacts.setType("Friend");
                            myContacts.setBlock_status("0");
                            myContacts.setMute_status("0");
                            myContacts.setContact_status("1");
                            myContacts.setRoomLeaveStatus("0");
                            myContacts.setStatus(OpenfireService.GetUserStatus(contacts.getUsername()));
                            myContacts.save();


                        }

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
}
