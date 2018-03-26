package com.mindnotix.mnxchats.openfire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.activeandroid.query.Update;
import com.mindnotix.mnxchats.MainActivity;
import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.activeandroid.MyProfileStatus;
import com.mindnotix.mnxchats.connection.MyXMPP;
import com.mindnotix.mnxchats.model.UserProfile;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.Pref;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.privacy.PrivacyList;
import org.jivesoftware.smackx.privacy.PrivacyListManager;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.util.XmppStringUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sridharan on 11/24/2017.
 */

public class OpenfireService extends MyXMPP {
    private SmackVCardHelper vCardHelper;


    private static final String TAG = "OpenfireService";

    public String GetUserProfileImage(String userJID){

        Log.d(TAG, "GetUserProfileImage: "+userJID);
        String Img_path=null;
        VCardManager vCardManager = VCardManager.getInstanceFor(MyXMPP.getConnection());
        boolean isSupported = false;
        try {
            Log.d(TAG, "onBindViewHolder: username " + userJID+ Constant.DOMAIN);
            isSupported = vCardManager.isSupported(userJID + Constant.DOMAIN);
            if (isSupported) {
                Log.d(TAG, "onBindViewHolder: username " + userJID);
                VCard vCard = vCardManager.loadVCard(userJID + Constant.DOMAIN);
                //get the image path from EMAILHOME here
                Img_path = vCard.getEmailHome();
                return  Img_path;
            } else {
                return null;
            }

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return Img_path;
    }


    public String GetUserProfileImageBitmap(String userJID){

        String Img_path=null;
        VCardManager vCardManager = VCardManager.getInstanceFor(MyXMPP.getConnection());
        boolean isSupported = false;
        try {
            Log.d(TAG, "onBindViewHolder: username " + userJID+ Constant.DOMAIN);
            isSupported = vCardManager.isSupported(userJID + Constant.DOMAIN);
            if (isSupported) {
                Log.d(TAG, "onBindViewHolder: username " + userJID);
                VCard vCard = vCardManager.loadVCard(userJID + Constant.DOMAIN);
                //get the image path from EMAILHOME here
                Img_path =vCard.getEmailHome();

            }
            return  Img_path;

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return Img_path;
    }


    public static Date getStanzaDelay(Stanza packet) {
        ExtensionElement _delay = packet.getExtension("delay", "urn:xmpp:delay");
        if (_delay == null)
            _delay = packet.getExtension("x", "jabber:x:delay");

        Date stamp = null;
        if (_delay != null) {
            if (_delay instanceof DelayInformation) {
                stamp = ((DelayInformation) _delay).getStamp();
            }
        }

        return stamp;
    }
    public static String GetFriendName(String userJID) {

        Log.d(TAG, "GetFriendName: "+userJID);

        String myfriendName = "";

        try {
            try {

                String key = userJID.contains("@") ? userJID.split("@")[0] : userJID;

                UserSearchManager userSearchManager = new UserSearchManager(
                        connection);
                Form searchForm = userSearchManager.getSearchForm("search."
                        + connection.getServiceName());
                Form answerForm = searchForm.createAnswerForm();
                ReportedData resultData;

                int count = 0;

                answerForm = searchForm.createAnswerForm();
                answerForm.setAnswer("Username", true);
                answerForm.setAnswer("Name", true);
                answerForm.setAnswer("Email", true);

                answerForm.setAnswer("search", "*" + key.replaceFirst("^0+(?!$)", "").replace(" ", "").replace("-", "").replace("+", "").replace(".", "") + "*");

                resultData = userSearchManager.getSearchResults(
                        answerForm,
                        "search."
                                + connection
                                .getServiceName());

                List<ReportedData.Row> it = resultData.getRows();
                if (it.size() > 0) {
                    for (ReportedData.Row row : it) {
                        List<String> jids = row.getValues("Username");

                        int pos = 0;
                        for (String jid : jids) {
                            myfriendName = row.getValues("Name").get(pos);
                            break;
                        }
                    }
                } else {

                 /*   bus.post(new ContactSearchResponse(key, key,
                            ContactSearchResponse.Status.Invite, ContactSearchResponse.SearchInitiatedBy.Manual));*/
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        } catch (Exception e) {
        }

        return myfriendName;
    }



    public static UserProfile search(String username)  {
        Log.d(TAG, "search: username (JID ) - "+username);
        String name = username;
        String jid = null;
        if (name == null || name.trim().length() == 0) {
            jid = username + "@" + MyXMPP.getConnection().getServiceName();
        } else {
            jid = XmppStringUtils.parseBareJid(username);
        }

        VCard vCard = loadVCard(jid);
        String nickname = vCard.getNickName();
        String ProfilePic = vCard.getEmailHome();

        Log.d(TAG, "search: email as profile pic "+ProfilePic);
        Log.d(TAG, "search: email as profile pic "+jid);


        String jid_array[] = jid.split("@");
        Log.d(TAG, "search: email as profile pic jid_array "+jid_array[0]);

        String updateSet = " image_path = ? ";

        new Update(MyContacts.class)
                .set(updateSet,ProfilePic)
                .where("Jid = ?", jid_array[0])
                .execute();

        return nickname == null ? null : new UserProfile(jid, vCard);
    }

    public static VCard loadVCard(String jid) {
        VCard vCard = new VCard();
        try {
            vCard.load(MyXMPP.getConnection(), jid);

            return vCard;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vCard;
    }
    public static String GetUserStatus(String jid)
    {

        Log.d(TAG, "GetUserStatus:jid "+jid);
        Roster roster = Roster.getInstanceFor(connection);

        String status ="";
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
        String my_uname = Pref.getPreferenceUser() + Constant.DOMAIN;
        String userName = jid + Constant.DOMAIN;
        try {
            roster.createEntry(userName, my_uname, null);
            Presence presence = new Presence(Presence.Type.available);
            connection.sendStanza(presence);
        } catch (SmackException.NotLoggedInException e) {
            Log.e(TAG, "onBindViewHolder: ERROR " + e.getLocalizedMessage());
        } catch (SmackException.NoResponseException e) {
            Log.e(TAG, "onBindViewHolder: ERROR " + e.getLocalizedMessage());
        } catch (XMPPException.XMPPErrorException e) {
            Log.e(TAG, "onBindViewHolder: ERROR " + e.getLocalizedMessage());
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "onBindViewHolder: ERROR " + e.getLocalizedMessage());
        }

        Presence presence = roster.getPresence(jid+Constant.DOMAIN);

        if(presence.getStatus()!=null){

            status =presence.getStatus();
            Log.d(TAG, "GetUserStatus: "+status);
        }else{
            status = "Hey!i Am Using Chat Here";
        }


        return  status;
    }

    public static void setStatusContents(MainActivity context) {

        Log.d(TAG, "setStatusContents: " + Pref.getPreferenceUser());
        Roster roster = Roster.getInstanceFor(connection);


        Presence entryPresence = roster.getPresence(Pref.getPreferenceUser() + Constant.DOMAIN);
        String status = entryPresence.getStatus();

        Log.d(TAG, "setStatusContents_status: " + status);

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.status_prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);


        userInput.setText(Pref.getPreferenceStatus());

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public static final String TAG = "CHATfcgchvbvbnvbn";

                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                try {

                                    MyProfileStatus myProfile = new MyProfileStatus();
                                    myProfile.setJid(Pref.getPreferenceUser());
                                    myProfile.setStatus(userInput.getText().toString());
                                    myProfile.save();
                                    Log.d(TAG, "onClick: " + userInput.getText().toString());
                                    Pref.saveSecondUserPresenceStatus(userInput.getText().toString());

                                    if( MyXMPP.getConnection() != null){
                                        Log.d(TAG, "onClick: true");
                                        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
                                        final Presence presence = new Presence(Presence.Type.available);
                                        presence.setStatus(userInput.getText().toString());
                                        MyXMPP.getConnection().sendStanza(presence);

                                    }else{
                                        Log.d(TAG, "onClick: false");
                                    }


                                    Log.d(TAG, "onClick: f" + userInput.getText().toString());
                                } catch (SmackException.NotConnectedException e) {
                                    Log.d(TAG, "onClick: c" + userInput.getText().toString());
                                    e.printStackTrace();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void changeProfile(String path) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            Log.d(TAG, "changeProfile: "+Constant.IMG_PATH+path);

            VCardManager vCardManager = VCardManager.getInstanceFor(MyXMPP.getConnection());
            VCard vCard;
            vCard = vCardManager.loadVCard();
            vCard.setNickName(Pref.getPreferenceUser()+Constant.DOMAIN);
            URL urldefault = new URL(Constant.IMG_PATH+path);
            InputStream stream = urldefault.openStream();
            byte[] avatar1 = readBytes(stream);
            vCard.setAvatar(avatar1, "avatar1/jpg");
            vCard.setPhoneHome("mobile", Pref.getPreferenceUser());
            vCard.setEmailHome(Constant.IMG_PATH+path);
            vCardManager.saveVCard(vCard);

            Pref.setProfileImg(Constant.IMG_PATH+path);

/*            VCardTempXUpdatePresenceExtension x = new VCardTempXUpdatePresenceExtension(avatar1);
            //   presence.addExtension(x);*/
            //Here presence language play for profile picture role.

            Presence presence = new Presence(Presence.Type.available);
            presence.setLanguage(Constant.IMG_PATH+path);
            connection.sendStanza(presence);


        } catch (Exception e) {
            e.printStackTrace();
            //FileLog.e("Yahala create Account", e.toString());
            Log.e("Yahala create Account", e.toString());

        }
    }
    private byte[] readBytes(InputStream stream) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }



    public static void UnBlockUser(String userJID) {
        Log.d(TAG, "UnBlockUser: "+userJID);
        String listName = "userBlockList";
        PrivacyListManager privacyManager = PrivacyListManager
                .getInstanceFor(connection);

        try {
            List<PrivacyItem> list = privacyManager.getPrivacyList(listName)
                    .getItems();

            for (PrivacyItem pi : list) {
                if (pi.getValue().equalsIgnoreCase(userJID)) {
                    list.remove(pi);
                    break;
                }
            }
            privacyManager.updatePrivacyList(listName, list);
            privacyManager.setActiveListName(listName);
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }


        Presence newp = new Presence(Presence.Type.available);
        newp.setMode(Presence.Mode.available);
        //the value 25 for unblock the user
        newp.setPriority(25);
        newp.setTo(userJID);
        try {
            connection.sendStanza(newp);
        } catch (SmackException.NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public static boolean BlockUser(String userJID) {

        Log.d(TAG, "BlockUser: "+userJID);
        try {

            PrivacyListManager privacyManager = PrivacyListManager
                    .getInstanceFor(connection);
            PrivacyList privacyList = null;


            List<PrivacyList> privacylists = privacyManager.getPrivacyLists();
            if (privacylists.size() > 0) {
                privacyList = privacyManager.getActiveList();
            }

            String listName = "userBlockList";
            PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid, userJID,
                    false, privacyManager.getPrivacyLists().size() + 1);
            item.setFilterIQ(false);
            item.setFilterMessage(false);
            item.setFilterPresenceIn(false);
            item.setFilterPresenceOut(false);

            List<PrivacyItem> list = new ArrayList<PrivacyItem>();
            if (privacyList != null) {
                list = privacyList.getItems();
            }
            list.add(item);

            if (privacyList == null) {
                privacyManager.createPrivacyList(listName, list);
            } else {
                privacyManager.updatePrivacyList(listName, list);
            }

            privacyManager.setActiveListName(listName);
            privacyManager.setDefaultListName(listName);

            Presence newp = new Presence(Presence.Type.unavailable);
            newp.setMode(Presence.Mode.xa);
            //the value 24 for block the user
            newp.setPriority(24);
            newp.setTo(userJID);
            try {
                connection.sendStanza(newp);
            } catch (SmackException.NotConnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

         /*   bus.post(new FriendsPropertyChanged());*/

            return true;
        } catch (SmackException.NoResponseException
                | XMPPException.XMPPErrorException
                | SmackException.NotConnectedException e) {
            e.printStackTrace();

            return false;
        }
    }

}
