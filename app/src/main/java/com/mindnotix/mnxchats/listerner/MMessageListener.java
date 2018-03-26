package com.mindnotix.mnxchats.listerner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.activeandroid.query.Update;
import com.mindnotix.mnxchats.MainActivity;
import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.activeandroid.ChatMessages;
import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.activeandroid.SqliteDatabaseManager;
import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.connection.MyXMPP;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;
import com.mindnotix.mnxchats.openfire.OpenfireService;
import com.mindnotix.mnxchats.service.MyService;
import com.mindnotix.mnxchats.utils.CommonMethods;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.FileUtils;
import com.mindnotix.mnxchats.utils.Pref;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jxmpp.util.XmppStringUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Sridharan on 11/23/2017.
 */

public class MMessageListener implements ChatStateListener, StanzaListener ,FileTransferListener {

    private static final String TAG = "MMessageListener";
    public static Notification.Builder builder;
    public static NotificationManager notificationManager;
    public String userJID = "";
    List<MyContacts> myContactsList;
    ChatMessages chatMessages;
    MyService context;
    private DelayInformation delayInformation;


    public static NotificationManager getNotificationManager() {
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
        return notificationManager;
    }

    public static Notification.Builder getBuilder() {
        Log.d(TAG, "getBuilder: asdfsadfasdf");

        return builder.setAutoCancel(true);
    }

    public MMessageListener(MyService context) {
        this.context = context;
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

        String timestamp;
        Message message = (Message) packet;
        userJID = XmppStringUtils.parseBareJid(message.getFrom());
        final String fromJID = XmppStringUtils.parseBareJid(message.getFrom());
        final String myJID = XmppStringUtils.parseBareJid(MyXMPP.getConnection().getUser());
        Log.i(TAG, "Got Single Chat Message ANd ID : [" + message.getBody() + "] from [" + fromJID + "] Current UserJID [ " + myJID + "]");
        if (message.getBody() != null) {
            Log.d(TAG, "processPacket: processPacket: new packet received in service");

            String from_jid[] = fromJID.split("@");
            Log.d(TAG, "processPacket: from_jid 0 pos " + from_jid[0]);
            Log.d(TAG, "processPacket: from_jid 1 pos " + from_jid[1]);
            Date date;

            //My time getting code
            Date stamp = OpenfireService.getStanzaDelay(message);

            long serverTimestamp;
            if (stamp != null) {
                serverTimestamp = stamp.getTime();
                Log.d(TAG, "processPacket_server: " + serverTimestamp);
                DateFormat.getInstance().format(serverTimestamp);
                Log.d(TAG, "processPacket_server: " + DateFormat.getDateInstance().format(new Date(0)));
                Log.d(TAG, "processPacket_server: " + DateFormat.getDateTimeInstance().format(new Date(0)));
                Log.d(TAG, "processPacket_server: " + DateFormat.getTimeInstance().format(new Date(0)));
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                Date resultdate = new Date(serverTimestamp);
                System.out.println("processPacket_server_timestamp" + sdf.format(resultdate));
                timestamp = sdf.format(resultdate);
            } else {
                serverTimestamp = System.currentTimeMillis();
                Log.d(TAG, "processPacket_system: " + serverTimestamp);
                DateFormat.getInstance().format(serverTimestamp);
                Log.d(TAG, "processPacket_system: " + DateFormat.getDateInstance().format(new Date(0)));
                Log.d(TAG, "processPacket_system: " + DateFormat.getDateTimeInstance().format(new Date(0)));
                Log.d(TAG, "processPacket_system: " + DateFormat.getTimeInstance().format(new Date(0)));
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                Date resultdate = new Date(serverTimestamp);
                System.out.println("processPacket_system_timestamp" + sdf.format(resultdate));
                timestamp = sdf.format(resultdate);
            }
            // Message receive here

            myContactsList = SqliteDatabaseManager.getMyContactRandom(fromJID);

            Log.d(TAG, "processPacket:getPreferenceSecondUser " + Pref.getPreferenceSecondUser());
            Log.d(TAG, "processPacket:fromJID " + fromJID);

            Log.d(TAG, "processPacket: Chat activity first" + Constant.ACTIVTIY_CHAT);
            if (Constant.ACTIVTIY_CHAT && Pref.getPreferenceSecondUser().equals(fromJID)) {


                Log.d(TAG, "processPacket: Chat activity first"+MyApplication.getInstance().activityChats);

                if (MyApplication.getInstance().activityChats != null) {

                    if (myContactsList.size() == 0) {

                        Log.d(TAG, "processPacket: In chat activity T jid" + from_jid[0]);

                        MyContacts myContacts = new MyContacts();
                        myContacts.setLastMessage(message.getBody());
                        myContacts.setConversation_status("1");
                        myContacts.setUname(from_jid[0]);
                        myContacts.setJid(from_jid[0]);
                        myContacts.save();

/*                        AppController.getInstance().activityChats.recieveMessage(message.getBody(), fromName,
                                "0", timestamp,"Friend", fromName);*/

                        Events.ChatActivityReceiveMessage chatActivityReceiveMessage =
                                new Events.ChatActivityReceiveMessage(message.getBody(),from_jid[0],"0",timestamp,"Friend");

                        GlobalBus.getBus().postSticky(chatActivityReceiveMessage);


                    } else {

                        Log.d(TAG, "processPacket: In chat activity F jid :" + from_jid[0]);
                        Log.d(TAG, "processPacket: In chat activity F message :" + message.getBody());

                        String updateSet = " conversation_status = ? ," +
                                " lastMessage = ? " ;


                        new Update(MyContacts.class)
                                .set(updateSet, "1", message.getBody())
                                .where("Jid = ?", from_jid[0])
                                .execute();

                        Events.ChatActivityReceiveMessage chatActivityReceiveMessage =
                                new Events.ChatActivityReceiveMessage(message.getBody(),from_jid[0],myContactsList.get(0).getUn_read(),timestamp,"Friend");

                        GlobalBus.getBus().postSticky(chatActivityReceiveMessage);
                    }

                }
            } else {


                Log.d(TAG, "processPacket:Not in chat activity");


                if (myContactsList.size() == 0) {

                    Log.d(TAG, "processPacket:Not in chat activity T jid" + from_jid[0]);
                    int unread = 1;
            /*        String updateSet = " conversation_status = ? ," + " lastMessage = ? ";


                    new Update(MyContacts.class)
                            .set(updateSet, "1", message.getBody())
                            .where("Jid = ?", from_jid[0])
                            .execute();*/


                    MyContacts myContacts = new MyContacts();
                    myContacts.setJid(from_jid[0]);
                    myContacts.setEmail(from_jid[0]+Constant.DOMAIN);
                    myContacts.setUname(OpenfireService.GetFriendName(from_jid[0]));
                    myContacts.setType("Friend");
                    myContacts.setBlock_status("0");
                    myContacts.setMute_status("0");
                    myContacts.setContact_status("1");
                    myContacts.setLastMessage(message.getBody());
                    myContacts.setConversation_status("1");
                    myContacts.setRoomLeaveStatus("0");
                    myContacts.setStatus(OpenfireService.GetUserStatus(from_jid[0]));
                    myContacts.save();

                    chatMessages = new ChatMessages();
                    chatMessages.setBody(message.getBody());
                    chatMessages.setJid(from_jid[0]);
                    chatMessages.setMyJid(Pref.getPreferenceUser() + Constant.DOMAIN);
                    chatMessages.setTime(timestamp);
                    chatMessages.setDate(CommonMethods.getCurrentDate());
                    chatMessages.setType("Friend");
                    chatMessages.setIsUser("2");
                    chatMessages.setStatus("1");
                    chatMessages.save();


                    sendStickycountMsg(String.valueOf(unread), from_jid[0], message.getBody());

                    myContactsList = SqliteDatabaseManager.getMyContactRandom(fromJID);
                    if (myContactsList.get(0).getMute_status().equals("0"))
                        generateNotification(context, message.getBody(), myContactsList.get(0).getUname(), 1, myContactsList.get(0).getId());

                    MainActivity.getInstance().triggerCrap();

                } else {

                    Log.d(TAG, "processPacket:Not in chat activity F jid" + from_jid[0]);
                    int unread = 0;
                    if (myContactsList.get(0).getUn_read() != null) {
                        unread = Integer.parseInt(myContactsList.get(0).getUn_read());
                    }


                    unread = unread + 1;
                    String unreadstring = String.valueOf(unread);

                    String updateSet = " conversation_status = ? ," + " un_read = ? ," + " lastMessage = ? ";


                    new Update(MyContacts.class)
                            .set(updateSet, "1",unreadstring, message.getBody())
                            .where("Jid = ?", from_jid[0])
                            .execute();

                    chatMessages = new ChatMessages();
                    chatMessages.setBody(message.getBody());
                    chatMessages.setJid(from_jid[0]);
                    chatMessages.setMyJid(Pref.getPreferenceUser() + Constant.DOMAIN);
                    chatMessages.setTime(timestamp);
                    chatMessages.setDate(CommonMethods.getCurrentDate());
                    chatMessages.setType("Friend");
                    chatMessages.setIsUser("2");
                    chatMessages.setStatus("1");
                    chatMessages.save();

                    sendStickycountMsg(unreadstring, from_jid[0], message.getBody());
                    if (myContactsList.get(0).getMute_status().equals("0"))
                        generateNotification(context, message.getBody(), myContactsList.get(0).getUname(), unread, myContactsList.get(0).getId());

                }

            }

        }

    }

    @Override
    public void stateChanged(Chat chat, ChatState state) {

        switch (state) {

            case active:

                Log.d("state", "active");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Events.ChatActivityChangeChatState activityActivityMessageEvent =
                                new Events.ChatActivityChangeChatState("Online", userJID);

                        GlobalBus.getBus().postSticky(activityActivityMessageEvent);


                    }
                });

                break;
            case composing:
                Log.d("state", "composing");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Events.ChatActivityChangeChatState activityActivityMessageEvent =
                                new Events.ChatActivityChangeChatState("Typing...", userJID);

                        GlobalBus.getBus().postSticky(activityActivityMessageEvent);


                    }
                });

                break;
            case paused:
                Log.d("state", "paused");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Events.ChatActivityChangeChatState activityActivityMessageEvent =
                                new Events.ChatActivityChangeChatState("Paused", userJID);

                        GlobalBus.getBus().postSticky(activityActivityMessageEvent);


                    }
                });

                break;
            case inactive:
                Log.d("state", "inactive");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Events.ChatActivityChangeChatState activityActivityMessageEvent =
                                new Events.ChatActivityChangeChatState("Paused", userJID);

                        GlobalBus.getBus().postSticky(activityActivityMessageEvent);

                    }
                });

                break;
            case gone:
                Log.d("state", "gone");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Events.ChatActivityChangeChatState activityActivityMessageEvent =
                                new Events.ChatActivityChangeChatState("Offline", userJID);

                        GlobalBus.getBus().postSticky(activityActivityMessageEvent);


                    }
                });

                break;
        }

    }

    @Override
    public void processMessage(Chat chat, Message message) {

        Log.d(TAG, "processMessage: "+message.getBody());

    }


    private void generateNotification(MyService context, String body, String fromName, int i, Long _ids) {


        int _id = Integer.parseInt(String.valueOf(_ids));

        Log.d(TAG, "generateNotification:user_two_f " + fromName);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("frag_pos", 0);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //  resultIntent.putExtra("user_two", fromName);
        PendingIntent piResult = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_logo_small)
                .setContentTitle(fromName)
                .setContentText(body)
                .setSound(alarmsound)
                .setContentIntent(piResult)
                .setAutoCancel(true);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.InboxStyle(builder)
                    .addLine(body)
                    .setBigContentTitle(fromName)
                    .setSummaryText(i + "more...")
                    .build();
        }


        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(_id, notification);

    }
    public void sendStickycountMsg(String count, String fromname, String message) {
        // Post an Sticky event before starting an activity to show the message,
        // sent by the MainActivity, in SecondActivity.
        Events.ActivityActivityMessage activityActivityMessageEvent =
                new Events.ActivityActivityMessage(message, fromname, count);

        GlobalBus.getBus().postSticky(activityActivityMessageEvent);

    }

    @Override
    public void fileTransferRequest(final FileTransferRequest request) {

        new Thread() {
            @Override
            public void run() {
                IncomingFileTransfer transfer = request.accept();

                String fileName = String.valueOf(System.currentTimeMillis());
                File file = new File(FileUtils.getReceivedImagesDir(context), fileName + FileUtils.IMAGE_EXTENSION);
               /* File mf = Environment.getExternalStorageDirectory();
                File file = new File(mf.getAbsoluteFile() + "/DCIM/Camera/" + transfer.getFileName());*/
                try {
                    Log.d(TAG, "run: receiving file" + file.getAbsolutePath());
                    transfer.recieveFile(file);

                    Thread.sleep(2000);

              //      MyApplication.getInstance().sendBroadcast(new Intent(Constant.IMAGE_ATTACH).putExtra(Constant.IMAGE_ATTACH, "https://www.android.com/static/2016/img/versions/oreo/hero/oreo-top-mobile.png"));
                    Log.d(TAG, "run: custom broadcast send!");

                    while (!transfer.isDone()) {
                        try {

                            Log.d(TAG, "run: getStatus" + transfer.getStatus());

                            Log.d(TAG, "run: getProgress" + transfer.getProgress());

                            Thread.sleep(1000);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }


                        Log.d(TAG, "run: " + transfer.getProgress());

                        if (transfer.getStatus().equals(FileTransfer.Status.error)) {
                            Log.e(TAG, "ERROR!!!" + transfer.getError());
                        }
                        if (transfer.getException() != null) {
                            transfer.getException().printStackTrace();
                        }
                    }

                    if (transfer.isDone()) {
                     //   MyApplication.getInstance().sendBroadcast(new Intent(Constant.IMAGE_ATTACH).putExtra(Constant.IMAGE_ATTACH, Uri.fromFile(file).toString()));
                        Log.d(TAG, "run: broadcast send");
                        Log.d(TAG, "run: transfer completed");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }
        }.
                start();


      /*  new Thread() {
            @Override
            public void run() {
                IncomingFileTransfer transfer = request.accept();
                String fileName = String.valueOf(System.currentTimeMillis());
                File file = new File(FileUtils.getReceivedImagesDir(context), fileName + FileUtils.IMAGE_EXTENSION);
                try {
                    Log.d(TAG, "run: recieve file getAbsolutePath is :"+file.getAbsolutePath());
                    Log.d(TAG, "run: recieve file getCanonicalPath is :"+file.getCanonicalPath());

                    transfer.recieveFile(file);
                } catch (SmackException e) {

                    Log.d(TAG, "run: recieve file error");
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (!transfer.isDone()) {
                    if(transfer.getStatus().equals(FileTransfer.Status.refused) || transfer.getStatus().equals(FileTransfer.Status.error)
                            || transfer.getStatus().equals(FileTransfer.Status.cancelled)){

                        Log.d(TAG, "run: recieve file error "+ transfer.getError());
                        return;
                    }
                }

                // start service to save the image to sqlite
                if (transfer.getStatus().equals(FileTransfer.Status.complete)) {

                    Log.d(TAG, "run: write code for save image in sqlite here...");
                }

            }
        }.start();*/
    }


   /* getFileTransferManager().addFileTransferListener(
                new FileTransferListener() {
        @Override
        public void fileTransferRequest(final FileTransferRequest request) {

            new Thread() {
                @Override
                public void run() {
                    IncomingFileTransfer transfer = request.accept();
                    File mf = Environment.getExternalStorageDirectory();
                    File file = new File(mf.getAbsoluteFile() + "/DCIM/Camera/" + transfer.getFileName());
                    try {
                        Log.d(TAG, "run: receiving file" + file.getAbsolutePath());
                        transfer.recieveFile(file);

                        Thread.sleep(2000);

                        MyApplication.getInstance().sendBroadcast(new Intent(Constant.IMAGE_ATTACH).putExtra(Constant.IMAGE_ATTACH, "https://www.android.com/static/2016/img/versions/oreo/hero/oreo-top-mobile.png"));
                        Log.d(TAG, "run: custom broadcast send!");

                        while (!transfer.isDone()) {
                            try {

                                Log.d(TAG, "run: getStatus" + transfer.getStatus());

                                Log.d(TAG, "run: getProgress" + transfer.getProgress());

                                Thread.sleep(1000);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }


                            Log.d(TAG, "run: " + transfer.getProgress());

                            if (transfer.getStatus().equals(FileTransfer.Status.error)) {
                                Log.e(TAG, "ERROR!!!" + transfer.getError());
                            }
                            if (transfer.getException() != null) {
                                transfer.getException().printStackTrace();
                            }
                        }

                        if (transfer.isDone()) {
                            MyApplication.getInstance().sendBroadcast(new Intent(Constant.IMAGE_ATTACH).putExtra(Constant.IMAGE_ATTACH, Uri.fromFile(file).toString()));
                            Log.d(TAG, "run: broadcast send");
                            Log.d(TAG, "run: transfer completed");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }

                }
            }.
                    start();

        }
    }
        );*/
}
