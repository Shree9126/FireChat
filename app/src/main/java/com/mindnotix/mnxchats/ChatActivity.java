package com.mindnotix.mnxchats;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Update;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.mindnotix.mnxchats.activeandroid.ChatMessages;
import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.activeandroid.SqliteDatabaseManager;
import com.mindnotix.mnxchats.adapter.SingleChatAdapter;
import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.bitmapcache.FileCompressUtils;
import com.mindnotix.mnxchats.connection.MyXMPP;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;
import com.mindnotix.mnxchats.openfire.OpenfireService;
import com.mindnotix.mnxchats.task.Response;
import com.mindnotix.mnxchats.task.SendImageTask;
import com.mindnotix.mnxchats.utils.CommonMethods;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.FileUtils;
import com.mindnotix.mnxchats.utils.ImageFilePath;
import com.mindnotix.mnxchats.utils.Pref;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

//import id.zelory.compressor.Compressor;

/**
 * Created by Sridharan on 11/24/2017.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener, Response.Listener<Boolean> {

    private static final int MENU_CLEAR = Menu.FIRST;
    private static final int MENU_MUTE = Menu.FIRST + 1;
    private static final int MENU_BLOCK = Menu.FIRST + 2;
    private static final int MENU_WALLPAPER = Menu.FIRST + 3;
    private static final int MENU_DEFAULT_WALLPAPER = Menu.FIRST + 4;

    private static final String TAG = "ChatActivity";
    private static final int REQUEST_PLACE_PICKER = 111;
    private static final int REQUEST_IMAGE_PICKER = 222;
    private static final int PICK_WALLPAPER_REQUEST_CODE = 777;
    int fragment_position;
     String To_JID = null, TO_JID_NAME = null, TO_JID_PROFILE_IMAGE = null;
    String NavigateFrom = null;
    String block_status = null;
     String image_path = null;
     String mute_status = null;
    String presence_status = null;
    int item_position;
    static ChatActivity chatActivity;
    public TextView userStatus;
    CircleImageView imgProfilePicture;
    LinearLayout chatlayout, toolbar_linear;
    ImageView enterchat1;
    EmojiconEditText chatedittext1;
    ImageView emojiButton;
    LinearLayout root_view;
    RecyclerView recycleindividual;
    TextView txtusername;
    Toolbar toolbar;
    MyXMPP xmpp;
    Timer timer;
    TimerTask tasknew;
    SingleChatAdapter singleChatAdapter;
    ArrayList<ChatMessages> messagesArrayList = new ArrayList<>();
    EmojIconActions emojIcon;
    DisplayMetrics displaymetrics = new DisplayMetrics();
    Drawable wallpaperDraw;
    private File file;
    private String LastMessage = null;
    private boolean isTyping = false;
    private Handler mHandler = new Handler();
    private String wallpaperImage;
    private Response.Listener<Boolean> sendImageListener;
    private File actualImage;
    private File compressedImage;

    public static synchronized ChatActivity getInstance() {
        return chatActivity;
    }


   /* public static void navigate(String Jid, String jid_name, int fragment_positions, int item_positions, String block_statuss,
                                String mute_statuss, String image_paths, String presence_statuss, String navigateFrom, Activity activity) {

        To_JID = Jid;
        TO_JID_NAME = jid_name;
        fragment_position = fragment_positions;
        item_position = item_positions;
        block_status = block_statuss;
        mute_status = mute_statuss;
        presence_status = presence_statuss;
        NavigateFrom = navigateFrom;
        image_path = image_paths;

        activity.startActivity(new Intent(activity, ChatActivity.class));

    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        GlobalBus.getBus().register(this);
        Constant.ACTIVTIY_CHAT = true;
        UiInitialization();


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Chat");
        }

        if (getIntent() != null) {

            To_JID = getIntent().getStringExtra("TO_JID");
            TO_JID_NAME = getIntent().getStringExtra("TO_JID_NAME");

            fragment_position = getIntent().getIntExtra("fragment_position", 0);
            NavigateFrom = getIntent().getStringExtra("NavigateFrom");
            block_status = getIntent().getStringExtra("block_status");
            TO_JID_PROFILE_IMAGE = getIntent().getStringExtra("image_path");
            mute_status = getIntent().getStringExtra("mute_status");
            presence_status = getIntent().getStringExtra("presence_status");
            item_position = getIntent().getIntExtra("item_position", 0);
            txtusername.setText(TO_JID_NAME);
            Log.d(TAG, "onCreate: jid " + To_JID);
            Log.d(TAG, "onCreate: fragment_position " + fragment_position);
            Log.d(TAG, "onCreate: NavigateFrom " + NavigateFrom);
            Log.d(TAG, "onCreate: TO_JID_NAME " + TO_JID_NAME);
            Log.d(TAG, "onCreate: block_status " + block_status);
            Log.d(TAG, "onCreate: mute_status " + block_status);
            Log.d(TAG, "onCreate: TO_JID_PROFILE_IMAGE " + TO_JID_PROFILE_IMAGE);
        }
        block_mute_status_check();


        //  TO_JID_PROFILE_IMAGE = openfireService.GetUserProfileImage(To_JID);
        if (TO_JID_PROFILE_IMAGE != null) {
            Log.d(TAG, "onCreate: T  " + TO_JID_PROFILE_IMAGE);
            Picasso.with(this)
                    .load(TO_JID_PROFILE_IMAGE)
                    .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(imgProfilePicture);
        } else
            Log.d(TAG, "onCreate: F  " + TO_JID_PROFILE_IMAGE);


        getOnlineStatus(To_JID);

        wallpaperImage = Pref.getChatScreenWallPaper();

        SetWallpaper(wallpaperImage);
        chatedittext1.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                MyXMPP.sendTypingStatus(ChatState.active, To_JID);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MyXMPP.sendTypingStatus(ChatState.composing, To_JID);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!isTyping) {
                    MyXMPP.sendTypingStatus(ChatState.composing, To_JID);
                    isTyping = true;
                }

                // creating timer task, timer
                tasknew = new TimerSchedulePeriod();
                timer = new Timer();

                // scheduling the task at interval

                timer.schedule(new TimerSchedulePeriod(), 3000);
            }

            class TimerSchedulePeriod extends TimerTask {
                @Override
                public void run() {

                    MyXMPP.sendTypingStatus(ChatState.active, To_JID);
                    timer.cancel();
                    isTyping = false;

                }
            }
        });

        getExistMessage();
    }

    private void block_mute_status_check() {

        if (block_status.equals("1") && block_status != null) {
            Pref.saveChatBlock("Unblock");

        } else {
            Pref.saveChatBlock("Block");
        }
        if (mute_status.equals("1") && mute_status != null) {
            Pref.saveChatMute("Immute");
        } else {
            Pref.saveChatMute("Mute");
        }

    }

    private void getExistMessage() {

        messagesArrayList.clear();
        new Update(MyContacts.class)
                .set("un_read = ?", "0")
                .where("Jid = ?", To_JID)
                .execute();


        List<ChatMessages> inventories = SqliteDatabaseManager.getAllMessageFromChatMessages(To_JID);
        messagesArrayList.addAll(inventories);

        Log.d(TAG, "getExistMessage: size "+messagesArrayList.size());
        singleChatAdapter = new SingleChatAdapter(messagesArrayList, this);
        recycleindividual.setAdapter(singleChatAdapter);
        onResultscrollBottom(true);


    }

    private void UiInitialization() {

        chatlayout = (LinearLayout) findViewById(R.id.chat_layout);
        toolbar_linear = (LinearLayout) findViewById(R.id.toolbar_linear);
        toolbar_linear.setOnClickListener(this);
        //bottomlayout =(LinearLayout) findViewById(R.id.bottomlayout);
        enterchat1 = (ImageView) findViewById(R.id.enter_chat1);
        enterchat1.setOnClickListener(this);
        userStatus = (TextView) findViewById(R.id.userStatus);
        txtusername = (TextView) findViewById(R.id.txtusername);
        chatedittext1 = (EmojiconEditText) findViewById(R.id.chat_edit_text1);
        imgProfilePicture = (CircleImageView) findViewById(R.id.imgProfilePicture);
        imgProfilePicture.setOnClickListener(this);
        emojiButton = (ImageView) findViewById(R.id.emojiButton);
        recycleindividual = (RecyclerView) findViewById(R.id.recycle_individual);


        recycleindividual.setItemAnimator(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //  layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recycleindividual.setLayoutManager(layoutManager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        root_view = (LinearLayout) findViewById(R.id.root_view);

        emojIcon = new EmojIconActions(this, root_view, chatedittext1, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
            }
        });

        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);


        sendImageListener = new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean result) {
            }

            @Override
            public void onErrorResponse(Exception exception) {
                Toast.makeText(ChatActivity.this, R.string.send_image_error, Toast.LENGTH_SHORT).show();
            }
        };
    }


    private void getOnlineStatus(String JID) {

        if (MyXMPP.getInstance() != null) {

            if (MyXMPP.getInstance().getLastSeenMessage(JID + Constant.DOMAIN).equals("Online")) {
                userStatus.setText("Online");
                userStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_online, 0, 0, 0);

            } else {
                userStatus.setText(MyXMPP.getInstance().getLastSeenMessage(JID + Constant.DOMAIN));
                userStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_offline, 0, 0, 0);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onBackPressed() {

        Constant.ACTIVTIY_CHAT = false;

        if (NavigateFrom.equals("1")) {

            Intent mIntent = new Intent(ChatActivity.this, MainActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mIntent);
        } else {

            Log.d(TAG, "onBackPressed: fragment_position " + fragment_position);
            MainActivity.getInstance().setPage(fragment_position, item_position, To_JID, LastMessage);
            finish();
            super.onBackPressed();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_one_to_one_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        menu.add(0, MENU_BLOCK, Menu.NONE, Pref.getChatMenuBlock());
        menu.add(0, MENU_CLEAR, Menu.NONE, Pref.getChatMenuClear());
        menu.add(0, MENU_MUTE, Menu.NONE, Pref.getChatMenuMute());
        menu.add(0, MENU_WALLPAPER, Menu.NONE, Pref.getChatMenuWallPaper());
        menu.add(0, MENU_DEFAULT_WALLPAPER, Menu.NONE, Pref.getChatMenudefaultWallPaper());

        return super.onPrepareOptionsMenu(menu);
    }


    public void attachImage(View view) {
        /*if (Build.VERSION.SDK_INT < 19) {
            Intent intentWall = new Intent();
            intentWall.setType("image*//*");
            intentWall.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intentWall, REQUEST_IMAGE_PICKER);
        } else {

            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image*//*");
            startActivityForResult(i, REQUEST_IMAGE_PICKER);

        }*/
        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*");

        startActivityForResult(pickIntent, REQUEST_IMAGE_PICKER);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.pick_image:
                attachImage(item.getActionView());
                break;

            case MENU_BLOCK:


                Log.d(TAG, "onOptionsItemSelected: " + block_status);
                if (block_status.equals("1")) {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle());
                    block_status = "0";
                    item.setTitle("Block");
                    Pref.saveChatBlock("Block");

                    String updateSet = " blockStatus = ? ";


                    new Update(MyContacts.class)
                            .set(updateSet, "0")
                            .where("Jid = ?", To_JID)
                            .execute();

                    block_unblockEvent(block_status, item_position);

                    OpenfireService.UnBlockUser(To_JID.concat(Constant.DOMAIN));


                } else {
                    block_status = "1";
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle());
                    item.setTitle("Unblock");
                    Pref.saveChatBlock("Unblock");
                    String updateSet = " blockStatus = ? ";
                    new Update(MyContacts.class)
                            .set(updateSet, "1")
                            .where("Jid = ?", To_JID)
                            .execute();
                    block_unblockEvent(block_status, item_position);

                    OpenfireService.BlockUser(To_JID.concat(Constant.DOMAIN));
                }

                break;

            case MENU_CLEAR:

                new Delete().from(ChatMessages.class).where("Jid = ?", To_JID).execute();
                messagesArrayList.clear();
                if (singleChatAdapter != null)
                    singleChatAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Clear", Toast.LENGTH_SHORT).show();


                break;

            case MENU_MUTE:

                if (mute_status.equals("0")) {

                    Pref.saveChatMute("Immute");
                    mute_status = "1";
                    item.setTitle("Immute");
                    String updateSet = " mute_status = ? ";


                    new Update(MyContacts.class)
                            .set(updateSet, "1")
                            .where("Jid = ?", To_JID)
                            .execute();

                    Mute_ImmuteEvent(mute_status, item_position);
                } else {

                    mute_status = "0";
                    item.setTitle("Mute");
                    Pref.saveChatMute("Mute");
                    String updateSet = " mute_status = ? ";


                    new Update(MyContacts.class)
                            .set(updateSet, "0")
                            .where("Jid = ?", To_JID)
                            .execute();
                    Mute_ImmuteEvent(mute_status, item_position);
                }
                break;

            case MENU_WALLPAPER:

                if (Build.VERSION.SDK_INT < 19) {
                    Intent intentWall = new Intent();
                    intentWall.setType("image/*");
                    intentWall.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intentWall, PICK_WALLPAPER_REQUEST_CODE);
                } else {

                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    startActivityForResult(i, PICK_WALLPAPER_REQUEST_CODE);

                }
                break;

            case MENU_DEFAULT_WALLPAPER:

                Pref.setChatScreenWallPaper("");
                int sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    chatlayout.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.chat_listview_bg));
                } else {
                    chatlayout.setBackground(getResources().getDrawable(
                            R.drawable.chat_listview_bg));
                }
                break;

            default:
                onBackPressed();
        }
        return false;
    }

    private void block_unblockEvent(String block_status, int item_position) {

        Events.ChatActivityBlockUnblockStatus activityActivityMessageEvent =
                new Events.ChatActivityBlockUnblockStatus(block_status, item_position);

        GlobalBus.getBus().postSticky(activityActivityMessageEvent);
    }

    private void Mute_ImmuteEvent(String block_status, int item_position) {

        Events.ChatActivityBlockUnblockStatus activityActivityMessageEvent =
                new Events.ChatActivityBlockUnblockStatus(block_status, item_position);

        GlobalBus.getBus().postSticky(activityActivityMessageEvent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        GlobalBus.getBus().unregister(this);
        wallpaperDraw = null;

        To_JID = null;
        TO_JID_NAME = null;
        TO_JID_PROFILE_IMAGE = null;
        NavigateFrom = null;
        block_status = null;
        image_path = null;
        mute_status = null;
        presence_status = null;
        item_position = 0;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.enter_chat1:
                sendTextMessage();
                break;
            case R.id.toolbar_linear:

                Intent mIntent = new Intent(ChatActivity.this, UserProfileView.class);
                mIntent.putExtra("presence_status", presence_status);
                mIntent.putExtra("mobile", To_JID);
                mIntent.putExtra("name", TO_JID_NAME);
                mIntent.putExtra("image_path", TO_JID_PROFILE_IMAGE);

                startActivity(mIntent);

                break;
            case R.id.imgProfilePicture:
                Intent intent = new Intent(ChatActivity.this, ProfileImageUpdateActivity.class);
                intent.putExtra("userType", 0);
                intent.putExtra("image_path", TO_JID_PROFILE_IMAGE);
                startActivity(intent);
                break;
        }
    }

    public void onResultscrollBottom(boolean result) {
        if (result) {
            if (singleChatAdapter != null) {

                singleChatAdapter.notifyDataSetChanged();
                if (singleChatAdapter.getItemCount() > 1)
                    recycleindividual.getLayoutManager().smoothScrollToPosition(recycleindividual, null, singleChatAdapter.getItemCount() - 1);

            }

        }

    }

    public void onResultGetAllMessage(boolean result) {

        if (result) {
            singleChatAdapter = new SingleChatAdapter(messagesArrayList, this);
            recycleindividual.setAdapter(singleChatAdapter);
            //  onResultscrollBottom(true);
        }

    }

    private void sendTextMessage() {

        ChatMessages chatmessage;
        long serverTimestamp = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        Date resultdate = new Date(serverTimestamp);
        String timestamp = sdf.format(resultdate);

        List<MyContacts> myContactsList = new ArrayList<>();
        myContactsList.clear();
        myContactsList = SqliteDatabaseManager.getMyContactRandom(To_JID);
        String messages_str = chatedittext1.getText().toString();
        LastMessage = messages_str;
        if (!messages_str.equalsIgnoreCase("")) {


            Message message = new Message(To_JID + Constant.DOMAIN, Message.Type.chat);

            //final String myJID = XmppStringUtils.parseBareJid(MyXMPP.getConnection().getUser());

            final String myJID = Pref.getPreferenceUser();
            Log.d(TAG, "sendTextMessage: " + myJID);
            if (myContactsList.size() == 0) {

                message.setBody(messages_str);
                message.setType(Message.Type.chat);
                message.setTo(To_JID + Constant.DOMAIN);
                message.setFrom(myJID);
                ChatStateExtension extension = new ChatStateExtension(ChatState.active);
                message.addExtension(extension);
                message.addExtension(new DeliveryReceiptRequest());
                DeliveryReceiptRequest.addTo(message);

                Log.d("MyXMPP", "sendMessage: " + message.getStanzaId());
                MyContacts myContacts = new MyContacts();
                myContacts.setLastMessage(messages_str);
                myContacts.setUn_read("0");
                myContacts.setContact_status("1");
                myContacts.save();

                chatmessage = new ChatMessages();
                chatmessage.setBody(messages_str);
                chatmessage.setMsgID(message.getStanzaId());
                chatmessage.setUname(TO_JID_NAME);
                chatmessage.setIsUser("1");//user type who send the message
                chatmessage.setStatus("1");//sended message
                chatmessage.setJid(To_JID);
                chatmessage.setMyJid(Pref.getPreferenceUser() + Constant.DOMAIN);
                chatmessage.setDate(CommonMethods.getCurrentDate());
                chatmessage.setTime(timestamp);
                chatmessage.save();

                messagesArrayList.add(new ChatMessages(messages_str, "1", "Friend", CommonMethods.getCurrentDate(), timestamp, "1", message.getStanzaId()));
                chatedittext1.setText("");

                singleChatAdapter.notifyDataSetChanged();
                if (block_status.equals("0"))
                    MyXMPP.getInstance().sendMessage(message);

            } else {


                message.setBody(messages_str);
                message.setType(Message.Type.chat);
                message.setTo(To_JID + Constant.DOMAIN);
                message.setFrom(MyXMPP.getConnection().getUser() + Constant.DOMAIN);
                ChatStateExtension extension = new ChatStateExtension(ChatState.active);
                message.addExtension(extension);
                message.addExtension(new DeliveryReceiptRequest());
                DeliveryReceiptRequest.addTo(message);

                Log.d("MyXMPP", "sendMessage: " + message.getStanzaId());

                String un_read = "0";
                String updateSet = " un_read = ? ," +
                        " lastMessage = ? ," +
                        " conversation_status = ? ";

                new Update(MyContacts.class)
                        .set(updateSet, un_read, messages_str, "1")
                        .where("Jid = ?", To_JID)
                        .execute();

                chatmessage = new ChatMessages();
                chatmessage.setBody(messages_str);
                chatmessage.setMsgID(message.getStanzaId());
                chatmessage.setUname(TO_JID_NAME);
                chatmessage.setType("Friend");//user type who send the message
                chatmessage.setIsUser("1");//user type who send the message
                chatmessage.setStatus("1");//sended message
                chatmessage.setJid(To_JID);
                chatmessage.setMyJid(Pref.getPreferenceUser() + Constant.DOMAIN);
                chatmessage.setDate(CommonMethods.getCurrentDate());
                chatmessage.setTime(timestamp);
                chatmessage.save();

                messagesArrayList.add(new ChatMessages(messages_str, "1", "Friend", CommonMethods.getCurrentDate(), timestamp, "1", message.getStanzaId()));
                chatedittext1.setText("");

                singleChatAdapter.notifyDataSetChanged();

                if (block_status.equals("0"))
                    MyXMPP.getInstance().sendMessage(message);
            }

        }
        onResultscrollBottom(true);
    }


    @Override
    public synchronized void onActivityResult(final int requestCode,
                                              int resultCode, final Intent data) {


        if (requestCode == PICK_WALLPAPER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {

                Log.d(TAG, "onActivityResult: selectedImageUri " + selectedImageUri);


                //    actualImage = FileCompressUtils.from(this, data.getData());

                String realPath = ImageFilePath.getPath(ChatActivity.this, selectedImageUri);
                Log.i(TAG, "onActivityResult: Image Path_s_realPath : " + realPath);

                Pref.setChatScreenWallPaper(realPath);

                SetWallpaper(realPath);

            }


        }

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {

            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {

                Log.d(TAG, "onActivityResult: selectedImageUri " + selectedImageUri);

                try {
                    actualImage = FileCompressUtils.from(this, selectedImageUri);
                    // compressImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String realPath = ImageFilePath.getPath(ChatActivity.this, selectedImageUri);
                Log.i(TAG, "onActivityResult: Image Path_s_realPath : " + realPath);

                String fileName = String.valueOf(System.currentTimeMillis());

                new SendImageTask(sendImageListener, this, To_JID, TO_JID_NAME, getString(R.string.image_message_body), data.getData(), fileName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            }


        }

    }

    private File createSentImageFile(String fileName, Bitmap bitmap) throws IOException {

        file = new File(FileUtils.getSentImagesDir(this), fileName + FileUtils.IMAGE_EXTENSION);
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        return file;
    }

    public void SetWallpaper(String wallpaperImagePath) {

        Log.d(TAG, "SetWallpaper: " + wallpaperImagePath);
        try {
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            Log.d(TAG, "SetWallpaper: width " + width);
            Log.d(TAG, "SetWallpaper: height " + height);

            if (wallpaperImage != null && wallpaperImage.trim() != "") {


                Glide.with(getApplicationContext()).load(new File(wallpaperImagePath)).asBitmap().toBytes().signature(new StringSignature(UUID.randomUUID().toString()))
                        .into(new SimpleTarget<byte[]>(width, height) {
                            @SuppressLint("NewApi")
                            @Override
                            public void onResourceReady(byte[] data,
                                                        GlideAnimation anim) {

                                try {

                                    wallpaperDraw = new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
                                    int sdk = Build.VERSION.SDK_INT;
                                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                                        chatlayout
                                                .setBackgroundDrawable(wallpaperDraw);
                                    } else {
                                        chatlayout.setBackground(wallpaperDraw);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                }
                            }
                        });
            } else {

                int sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    chatlayout.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.chat_listview_bg));
                } else {
                    chatlayout.setBackground(getResources().getDrawable(
                            R.drawable.chat_listview_bg));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    //Event Bus subscribe events here
    //User chat state change event while typing...

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ChatActivityChangeChatState chatActivityChangeChatState) {

        Log.d(TAG, "getMessage:Chat state " + chatActivityChangeChatState.getState());

        if (chatActivityChangeChatState.getState() != null && userStatus != null)
            userStatus.setText(chatActivityChangeChatState.getState());

    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ChatActivityReferesh chatActivityReferesh) {

        Log.d(TAG, "getMessage:Chat state " + chatActivityReferesh.getReceipt_id());

        for (int i = 0; i < messagesArrayList.size(); i++) {

            Log.d(TAG, "getRefresh: message_id" + messagesArrayList.get(i).getMsgID());

            if (messagesArrayList.get(i).getMsgID() != null) {
                if (messagesArrayList.get(i).getMsgID().equals(chatActivityReferesh.getReceipt_id())) {
                    Log.d(TAG, "getRefresh: message_id_in" + messagesArrayList.get(i).getMsgID());

                    Log.d("MyXMPP", "onReceiptReceived: " + messagesArrayList.get(i));
                    ChatMessages messageInfo = messagesArrayList.get(i);
                    messageInfo.setStatus("2");
                    if (singleChatAdapter != null)
                        singleChatAdapter.notifyItemChanged(i);

                }
            } else {
                Log.d(TAG, "getRefresh: message_id_else" + messagesArrayList.get(i).getMsgID());
            }

        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                if (singleChatAdapter != null)
                    singleChatAdapter.notifyDataSetChanged();
            }
        });
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ChatActvityReceiveImages chatActvityReceiveImages) {

        long serverTimestamp = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        Date resultdate = new Date(serverTimestamp);

        String timestamp = sdf.format(resultdate);


        String jid = chatActvityReceiveImages.getJid();
        String body = chatActvityReceiveImages.getBody();
        String mediaUrl = chatActvityReceiveImages.getPath();
        String time = String.valueOf(chatActvityReceiveImages.getTimeMillis());
        String status = chatActvityReceiveImages.getStatus();
        String userType = chatActvityReceiveImages.getUserType();


        if (chatActvityReceiveImages.getUserType().equals("3")) {


            messagesArrayList.add(new ChatMessages(body, userType, "Friend", CommonMethods.getCurrentDate(),
                    timestamp, status, "", mediaUrl));

            if(singleChatAdapter != null)
            singleChatAdapter.notifyDataSetChanged();
        } else if (chatActvityReceiveImages.getUserType().equals("4")) {


            messagesArrayList.add(new ChatMessages(body, userType, "Friend", CommonMethods.getCurrentDate(),
                    timestamp, status, "", mediaUrl));

            if(singleChatAdapter != null)
            singleChatAdapter.notifyDataSetChanged();
        }


    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ActivityUserProfileChange activityUserProfileChange) {

        Log.d(TAG, "getMessage:getProfileImg " + activityUserProfileChange.getProfileImg());
        if (activityUserProfileChange.getProfileImg() != null && imgProfilePicture != null) {

            Picasso.with(this)
                    .load(activityUserProfileChange.getProfileImg())
                    .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(imgProfilePicture);

        }

    }

    // receive message here from MMessageListener service
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ChatActivityReceiveMessage chatActivityReceiveMessage) {

        Log.d(TAG, "getMessage:ChatActivityReceiveMessage  getMessage_body " + chatActivityReceiveMessage.getMessage_body());
        Log.d(TAG, "getMessage:ChatActivityReceiveMessage  getRead_count " + chatActivityReceiveMessage.getRead_count());
        Log.d(TAG, "getMessage:ChatActivityReceiveMessage  getTimestamp " + chatActivityReceiveMessage.getTimestamp());
        Log.d(TAG, "getMessage:ChatActivityReceiveMessage  getType " + chatActivityReceiveMessage.getType());
        Log.d(TAG, "getMessage:ChatActivityReceiveMessage  getUser_jid " + chatActivityReceiveMessage.getUser_jid());

        ChatMessages messageInfo;

        if (chatActivityReceiveMessage.getMessage_body() != null) {

            if (To_JID.equalsIgnoreCase(chatActivityReceiveMessage.getUser_jid())) {


                new Update(MyContacts.class)
                        .set("un_read = ?", "0")
                        .where("Jid = ?", chatActivityReceiveMessage.getUser_jid())
                        .execute();


                messagesArrayList.add(new ChatMessages(chatActivityReceiveMessage.getMessage_body(), "2",
                        chatActivityReceiveMessage.getType(), CommonMethods.getCurrentDate(),
                        chatActivityReceiveMessage.getTimestamp(), "2", "null"));

                messageInfo = new ChatMessages();

                messageInfo.setBody(chatActivityReceiveMessage.getMessage_body());
                //    messageInfo.setUname(uname);
                messageInfo.setJid(chatActivityReceiveMessage.getUser_jid());
                messageInfo.setMyJid(Pref.getPreferenceUser() + Constant.DOMAIN);
                messageInfo.setTime(chatActivityReceiveMessage.getTimestamp());
                messageInfo.setDate(CommonMethods.getCurrentDate());
                messageInfo.setType(chatActivityReceiveMessage.getType());
                messageInfo.setIsUser("2");
                messageInfo.setStatus("2");
                messageInfo.save();

                // Add the incoming message to the list view
                mHandler.post(new Runnable() {
                    public void run() {
                        onResultscrollBottom(true);
                        //singleChatAdapter.notifyDataSetChanged();
                    }
                });

            } else {
                //TODO: Save data to database
                int unread = Integer.parseInt(chatActivityReceiveMessage.getRead_count());
                unread = unread + 1;
                Log.v("contect_request_read_c", String.valueOf(unread));
                new Update(MyContacts.class)
                        .set("un_read = ?", String.valueOf(unread))
                        .where("Jid = ?", chatActivityReceiveMessage.getUser_jid())
                        .execute();


                messageInfo = new ChatMessages();

                messageInfo.setBody(chatActivityReceiveMessage.getMessage_body());
                messageInfo.setJid(chatActivityReceiveMessage.getUser_jid());
                messageInfo.setMyJid(Pref.getPreferenceUser() + Constant.DOMAIN);
                messageInfo.setTime(chatActivityReceiveMessage.getTimestamp());
                messageInfo.setDate(CommonMethods.getCurrentDate());
                messageInfo.setType(chatActivityReceiveMessage.getType());
                messageInfo.setIsUser("2");
                messageInfo.setStatus("2");
                messageInfo.save();
            }
        }
    }

    @Override
    public void onResponse(Boolean result) {

    }

    @Override
    public void onErrorResponse(Exception exception) {

    }


}
