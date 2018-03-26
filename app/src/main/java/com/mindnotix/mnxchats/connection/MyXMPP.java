package com.mindnotix.mnxchats.connection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.activeandroid.query.Update;
import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.activeandroid.ChatMessages;
import com.mindnotix.mnxchats.activeandroid.SqliteDatabaseManager;
import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;
import com.mindnotix.mnxchats.listerner.CChatStateListener;
import com.mindnotix.mnxchats.listerner.DDeliveryReceiptListener;
import com.mindnotix.mnxchats.listerner.MMessageListener;
import com.mindnotix.mnxchats.listerner.RRosterListener;
import com.mindnotix.mnxchats.listerner.StanzaAcknowledgementListener;
import com.mindnotix.mnxchats.listerner.XMPPConnectionListener;
import com.mindnotix.mnxchats.service.MyService;
import com.mindnotix.mnxchats.task.SendImageTask;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.FileUtils;
import com.mindnotix.mnxchats.utils.LocaleController;
import com.mindnotix.mnxchats.utils.Pref;
import com.mindnotix.mnxchats.utils.ReadReceipt;
import com.mindnotix.mnxchats.utils.Utilities;
import com.mindnotix.mnxchats.utils.Utils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.attention.packet.AttentionExtension;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.delay.provider.LegacyDelayInformationProvider;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.iqlast.LastActivityManager;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.nick.packet.Nick;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.privacy.PrivacyListListener;
import org.jivesoftware.smackx.privacy.PrivacyListManager;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider;
import org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.pubsub.provider.FormNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.PubSubProvider;
import org.jivesoftware.smackx.pubsub.provider.RetractEventProvider;
import org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.shim.provider.HeaderProvider;
import org.jivesoftware.smackx.shim.provider.HeadersProvider;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;
import org.jxmpp.util.XmppStringUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import static com.mindnotix.mnxchats.task.SendImageTask.STATUS_SUCCESS;

/**
 * Created by Sridharan on 11/23/2017.
 */

public class MyXMPP {


    public static final int REPLY_TIMEOUT = 5000;
    private static final String TAG = "MyXMPP";
    private static final boolean XMPP_DEBUG_MODE = true;
    private static final String XMPP_TAG = "XMPP";
    public static ArrayList<HashMap<String, String>> usersList = new ArrayList<HashMap<String, String>>();
    public static boolean connected = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;
    public static XMPPTCPConnection connection;
    public static String loginName;
    public static String loginUser;
    public static String passwordUser;
    public static MyXMPP instance = null;
    public static boolean instanceCreated = false;
    public static boolean loggedin = false;
    public static boolean IsLoggedIn = false;
    public static VCardManager vCardManager;
    public static String delivery_receiptID = "";
    public static boolean CanConnect = true;
    private static BookmarkManager bMgr;
    public static final String RESOURCE_PART = "Smack";

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
        }
    }

    public Chat Mychat;
    public boolean chat_created = false;
    ProviderManager providerManager;
    ChatManager chatmanager;
    MyService context;
    XMPPConnectionListener connectionListener;
    MMessageListener mMessageListener;
    CChatStateListener cChatStateListener;
    StanzaAcknowledgementListener acknowledgementListener;
    DDeliveryReceiptListener deliveryReceiptListener;
    Roster roster;
    private XMPPConnection xmppConnection;
    private FileTransferManager fileTransferManager;
    private String serverAddress;

    //  MyXMPPPresence myXMPPPresence;
    public MyXMPP(MyService context, String serverAdress, String logiUser,
                  String passwordser, String name) {
        this.serverAddress = serverAdress;
        this.loginUser = logiUser;
        this.passwordUser = passwordser;
        this.loginName = name;
        this.context = context;


        Log.d("uname_is_xmpp", "" + loginUser);
        Log.d("uname_is_xmpp", "" + passwordUser);
        Log.d("Login_mys_constr", "" + loginUser);
        Log.d("Login_mys_constr", "" + name);
        init();

    }

    public MyXMPP() {
    }


    public static MyXMPP getInstance() {
        return instance;
    }


    public static MyXMPP getInstance(MyService context, String server,
                                     String user, String pass, String name) {
        Log.d("Login_mys_getins", "" + user);
        if (instance == null) {
            instance = new MyXMPP(context, server, user, pass, name);
            instanceCreated = true;
        } else {
            Log.d("Login_mys_getins_else", "" + user);
        }
        return instance;

    }

    public static void sendTypingStatus(ChatState state, String To) {


        try {

            Message statusPacket = new Message();
            statusPacket.setBody(null);
            statusPacket.setTo(To + Constant.DOMAIN);
            statusPacket.setType(Message.Type.chat);
            statusPacket.setSubject(null);
            ChatStateExtension extension = new ChatStateExtension(state);
            statusPacket.addExtension(extension);
            connection.sendStanza(statusPacket);

        } catch (Exception e) {

            e.printStackTrace();
            Log.d("sendTypingStatus: ", "" + e.getMessage());
        }

    }


    public static XMPPTCPConnection getConnection() {
        return connection;
    }

    private static void configureProviderManager(XMPPConnection connection) {

        // Delayed Delivery
        ProviderManager.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());
        ProviderManager.addExtensionProvider("delay", "urn:xmpp:delay",
                new DelayInformationProvider());


        ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
        ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, new DeliveryReceiptRequest().getNamespace(), new DeliveryReceiptRequest.Provider());


        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());

        ProviderManager.addExtensionProvider(ReadReceipt.ELEMENT, ReadReceipt.NAMESPACE, new ReadReceipt.Provider());


        ServiceDiscoveryManager sdm = ServiceDiscoveryManager
                .getInstanceFor(connection);
        sdm.addFeature("http://jabber.org/protocol/disco#info");
        sdm.addFeature("http://jabber.org/protocol/disco#item");
        sdm.addFeature("http://jabber.org/protocol/muc#rooms");
        sdm.addFeature("jabber:iq:privacy");

        // The order is the same as in the smack.providers file

        // Private Data Storage
        ProviderManager.addIQProvider("query", "jabber:iq:private",
                new PrivateDataManager.PrivateDataIQProvider());
        // Time
        try {
            ProviderManager.addIQProvider("query", "jabber:iq:time",
                    Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            System.err
                    .println("Can't load class for org.jivesoftware.smackx.packet.Time");
        }

      /*  // Roster Exchange
        ProviderManager.addExtensionProvider("x", "jabber:x:roster",
        		new RosterExchangeProvider());
        // Message Events
        ProviderManager.addExtensionProvider("x", "jabber:x:event",
        		new MessageEventProvider());*/
        // Chat State
        ProviderManager.addExtensionProvider("active",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("composing",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("paused",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("inactive",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        ProviderManager.addExtensionProvider("gone",
                "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());

        // XHTML
        ProviderManager.addExtensionProvider("html",
                "http://jabber.org/protocol/xhtml-im",
                new XHTMLExtensionProvider());

        // Group Chat Invitations
        ProviderManager.addExtensionProvider("x", "jabber:x:conference",
                new GroupChatInvitation.Provider());
        // Service Discovery # Items
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/disco#items",
                new DiscoverItemsProvider());
        // Service Discovery # Info
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/disco#info",
                new DiscoverInfoProvider());
        // Data Forms
        ProviderManager.addExtensionProvider("x", "jabber:x:data",
                new DataFormProvider());

        // MUC User
        ProviderManager.addExtensionProvider("x",
                "http://jabber.org/protocol/muc#user", new MUCUserProvider());
        // MUC Admin
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        // MUC Owner
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());


        // Delayed Delivery
        ProviderManager.addExtensionProvider("x", "jabber:x:delay",
                new DelayInformationProvider());
        ProviderManager.addExtensionProvider("delay", "urn:xmpp:delay",
                new DelayInformationProvider());

        ProviderManager.addExtensionProvider("x", "jabber:x:delay", new LegacyDelayInformationProvider());
        // Version
        try {
            ProviderManager.addIQProvider("query", "jabber:iq:version",
                    Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            System.err
                    .println("Can't load class for org.jivesoftware.smackx.packet.Version");
        }
        // VCard
        ProviderManager.addIQProvider("vCard", "vcard-temp:x:update",
                new VCardProvider());
        // Offline Message Requests
        ProviderManager.addIQProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        ProviderManager.addExtensionProvider("offline",
                "http://jabber.org/protocol/offline",
                new OfflineMessageInfo.Provider());
        // Last Activity
        ProviderManager.addIQProvider("query", "jabber:iq:last",
                new LastActivity.Provider());
        // User Search
        ProviderManager.addIQProvider("query", "jabber:iq:search",
                new UserSearch.Provider());
        // SharedGroupsInfo
        ProviderManager.addIQProvider("sharedgroup",
                "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        ProviderManager.addExtensionProvider("addresses",
                "http://jabber.org/protocol/address",
                new MultipleAddressesProvider());

        // FileTransfer
        ProviderManager.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());
        // ProviderManager.addIQProvider("open",
        // "http://jabber.org/protocol/ibb",
        // new OpenIQProvider());
        // ProviderManager.addIQProvider("data",
        // "http://jabber.org/protocol/ibb",
        // new DataPacketProvider());
        // ProviderManager.addIQProvider("close",
        // "http://jabber.org/protocol/ibb",
        // new CloseIQProvider());
        // ProviderManager.addExtensionProvider("data",
        // "http://jabber.org/protocol/ibb",
        // new DataPacketProvider());

        // Privacy
        ProviderManager.addIQProvider("query", "jabber:iq:privacy",
                new PrivacyProvider());

        // SHIM
        ProviderManager.addExtensionProvider("headers",
                "http://jabber.org/protocol/shim", new HeadersProvider());
        ProviderManager.addExtensionProvider("header",
                "http://jabber.org/protocol/shim", new HeaderProvider());

        // PubSub
        ProviderManager.addIQProvider("pubsub",
                "http://jabber.org/protocol/pubsub", new PubSubProvider());
        ProviderManager.addExtensionProvider("create",
                "http://jabber.org/protocol/pubsub", new SimpleNodeProvider());
        ProviderManager.addExtensionProvider("items",
                "http://jabber.org/protocol/pubsub", new ItemsProvider());
        ProviderManager.addExtensionProvider("item",
                "http://jabber.org/protocol/pubsub", new ItemProvider());
        ProviderManager.addExtensionProvider("subscriptions",
                "http://jabber.org/protocol/pubsub",
                new SubscriptionsProvider());
        ProviderManager
                .addExtensionProvider("subscription",
                        "http://jabber.org/protocol/pubsub",
                        new SubscriptionProvider());
        ProviderManager
                .addExtensionProvider("affiliations",
                        "http://jabber.org/protocol/pubsub",
                        new AffiliationsProvider());
        ProviderManager.addExtensionProvider("affiliation",
                "http://jabber.org/protocol/pubsub", new AffiliationProvider());
        ProviderManager.addExtensionProvider("options",
                "http://jabber.org/protocol/pubsub", new FormNodeProvider());
        // PubSub owner
        ProviderManager
                .addIQProvider("pubsub",
                        "http://jabber.org/protocol/pubsub#owner",
                        new PubSubProvider());
        ProviderManager.addExtensionProvider("configure",
                "http://jabber.org/protocol/pubsub#owner",
                new FormNodeProvider());
        ProviderManager.addExtensionProvider("default",
                "http://jabber.org/protocol/pubsub#owner",
                new FormNodeProvider());
        // PubSub event
        ProviderManager.addExtensionProvider("event",
                "http://jabber.org/protocol/pubsub#event", new EventProvider());
        ProviderManager.addExtensionProvider("configuration",
                "http://jabber.org/protocol/pubsub#event",
                new ConfigEventProvider());
        ProviderManager.addExtensionProvider("delete",
                "http://jabber.org/protocol/pubsub#event",
                new SimpleNodeProvider());
        ProviderManager.addExtensionProvider("options",
                "http://jabber.org/protocol/pubsub#event",
                new FormNodeProvider());
        ProviderManager.addExtensionProvider("items",
                "http://jabber.org/protocol/pubsub#event", new ItemsProvider());
        ProviderManager.addExtensionProvider("item",
                "http://jabber.org/protocol/pubsub#event", new ItemProvider());
        ProviderManager.addExtensionProvider("retract",
                "http://jabber.org/protocol/pubsub#event",
                new RetractEventProvider());
        ProviderManager.addExtensionProvider("purge",
                "http://jabber.org/protocol/pubsub#event",
                new SimpleNodeProvider());

        // Nick Exchange
        ProviderManager.addExtensionProvider("nick",
                "http://jabber.org/protocol/nick", new Nick.Provider());

        // Attention
        ProviderManager.addExtensionProvider("attention",
                "urn:xmpp:attention:0", new AttentionExtension.Provider());

        // input
        ProviderManager.addIQProvider("si", "http://jabber.org/protocol/si",
                new StreamInitiationProvider());
        ProviderManager.addIQProvider("query",
                "http://jabber.org/protocol/bytestreams",
                new BytestreamsProvider());

    }

    public void init() {

        mMessageListener = new MMessageListener(context);
        cChatStateListener = new CChatStateListener(context);
        acknowledgementListener = new StanzaAcknowledgementListener(context);
        deliveryReceiptListener = new DDeliveryReceiptListener(context);
        SqliteDatabaseManager sqliteDatabaseManager = new SqliteDatabaseManager();
        connectionListener = new XMPPConnectionListener(this,sqliteDatabaseManager);

        initialiseConnection();
    }


    private void initialiseConnection() {

        SmackConfiguration.setDefaultPacketReplyTimeout(REPLY_TIMEOUT);
        XMPPTCPConnectionConfiguration config = null;
        try {
            XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder()
                    .setDebuggerEnabled(XMPP_DEBUG_MODE)
                    .setServiceName(Constant.SERVICE)
                    .setHost(Constant.HOST)
                    .setPort(5222)
                    .setSendPresence(true)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

            KeyStore keyStore = configKeyStore(builder);

            configSSLContext(builder, keyStore);

            config = builder.build();

        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config);
        ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();
        //ServerPingWithAlarmManager.getInstanceFor(connection).setEnabled(true);
        ChatStateManager.getInstance(connection);

        configureProviderManager(connection);
        xmppConnection = new XMPPTCPConnection(config);

        fileTransferManager = FileTransferManager.getInstanceFor(xmppConnection);
        OutgoingFileTransfer.setResponseTimeout(30000);
        //fileTransferManager.addFileTransferListener(mMessageListener);


        getFileTransferManager().addFileTransferListener(
                new FileTransferListener() {
                    @Override
                    public void fileTransferRequest(final FileTransferRequest request) {

                        new Thread() {
                            @Override
                            public void run() {
                                IncomingFileTransfer transfer = request.accept();

                                String fileName = String.valueOf(System.currentTimeMillis());
                                File files = new File(FileUtils.getReceivedImagesDir(context), fileName + FileUtils.IMAGE_EXTENSION);
                                try {
                                    Log.d(TAG, "run: receiving files" + files.getAbsolutePath());
                                    transfer.recieveFile(files);

                                    Thread.sleep(2500);

                                    Log.d(TAG, "run: custom broadcast send!");

                                    while (!transfer.isDone()) {
                                        try {

                                            final double progress = transfer.getProgress();
                                            final double progressPercent = progress * 100.0;
                                            String percComplete = String.format("%1$,.2f", progressPercent);

                                            Log.d(TAG,"File transfer is " + percComplete + "% complete");

                                            Log.d(TAG, "run: getStatus" + transfer.getStatus());

                                            Log.d(TAG, "run: getProgress" + transfer.getProgress());

                                            Thread.sleep(1500);
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


                                        long serverTimestamp = System.currentTimeMillis();

                                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                                        Date resultdate = new Date(serverTimestamp);
                                        String timestamp = sdf.format(resultdate);


                                        Log.d(TAG, "run:getRequestor "+request.getRequestor());
                                        Log.d(TAG, "run:getStreamID "+request.getStreamID());
                                        Log.d(TAG, "run:getDescription "+request.getDescription());
                                        Log.d(TAG, "run:getFileName "+request.getFileName());
                                        Log.d(TAG, "run:getMimeType "+request.getMimeType());
                                        Log.d(TAG, "run:getFileSize "+request.getFileSize());
                                        Log.d(TAG, "run: broadcast send");
                                        Log.d(TAG, "run: transfer completed");

                                        String jid[] =request.getRequestor().split("@");

                                        Events.ChatActivityReferesh chatActivityReferesh =
                                                new Events.ChatActivityReferesh(request.getStreamID());

                                        GlobalBus.getBus().postSticky(chatActivityReferesh);

                                        SendImageTask.newImageMessage(jid[0], "Image", System.currentTimeMillis(), files.getAbsolutePath(), false);



                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }

                            }
                        }.
                                start();

                    }
                }
        );
        connection.addConnectionListener(connectionListener);



        Roster.getInstanceFor(connection).addRosterListener(
                new RRosterListener());


        roster = Roster.getInstanceFor(connection);


        roster.addRosterLoadedListener(
                new RosterLoadedListener() {
                    @Override
                    public void onRosterLoaded(Roster roster) {
                        Log.d(TAG, "onRosterLoaded: ");
                    }
                }
        );


        chatmanager = ChatManager.getInstanceFor(connection);
        try {
            bMgr = BookmarkManager.getBookmarkManager(connection);
            vCardManager = VCardManager.getInstanceFor(connection);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
        chatmanager.addChatListener(
                new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        if (!createdLocally) {
                            chat.addMessageListener(mMessageListener);
                        }
                    }
                });

        StanzaFilter filter1 = MessageTypeFilter.CHAT;

        connection.addSyncStanzaListener(mMessageListener, filter1);

        connection.addStanzaAcknowledgedListener(acknowledgementListener);




        PrivacyListManager
                .getInstanceFor(connection).addListener(new PrivacyListListener() {
            @Override
            public void setPrivacyList(String listName, List<PrivacyItem> listItem) {

                String ss = "asdf";
            }

            @Override
            public void updatedPrivacyList(String listName) {
                String ss = "asdf";
            }
        });


        Log.d(TAG, "initialiseConnection: before MUC MANAGER CALL");


    }



    public void connect(final String caller) {
        Log.d("Login_mys_conn", "enter");
        Log.v("connect_string", caller);

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (connection.isConnected())
                    return false;
                isconnecting = true;
                Log.d("Connect() Function", caller + "=>connecting....");

                try {
                    connection.connect();

                    if (connection.isConnected()) {

                        Log.d("connection", "true");


                        Presence newp = new Presence(Presence.Type.unavailable);
                        IsLoggedIn = true;

                        if (!Utils.isAppIsInBackground(context)) {

                            Log.d(TAG, "login: foreground");
                            newp = new Presence(Presence.Type.available);
                            newp.setMode(Presence.Mode.available);
                            try {
                                connection.sendStanza(newp);
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "login: background");
                            newp = new Presence(Presence.Type.available);
                            newp.setMode(Presence.Mode.dnd);
                            try {
                                connection.sendStanza(newp);
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        Log.d("connection", "false");
                    }
                    DeliveryReceiptManager dm = DeliveryReceiptManager
                            .getInstanceFor(connection);
                    dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);


                    dm.addReceiptReceivedListener(deliveryReceiptListener);
                    connected = true;

                } catch (IOException e) {

                    Log.e("(" + caller + ")", "IOException: " + e.getMessage());
                } catch (SmackException e) {

                    Log.e("(" + caller + ")", "SMACKException: " + e.getMessage());
                } catch (XMPPException e) {
                    if (isToasted)

                        Log.e("connect(" + caller + ")", "XMPPException: " + e.getMessage());
                }
                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }

    public void login() {

        try {
            Log.d("Login_login", loginUser);

            Log.v("uname_is_xmpp_logink", loginUser);
            Log.v("uname_is_xmpp_logink", passwordUser);

            connection.login(loginUser, passwordUser);


            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);


            Presence presence = new Presence(Presence.Type.available);
            presence.setMode(Presence.Mode.available);
            if (Pref.getPreferenceStatus() != null)
                presence.setStatus(Pref.getPreferenceStatus());
            else
                presence.setStatus("Available");

            connection.sendStanza(presence);


            Log.d("Login_login_a", loginUser);
            Log.i("LOGIN", "Yey! We're connected to the Xmpp server!");


            try {


                VCardManager vCardManager = VCardManager.getInstanceFor(connection);
                VCard vCard;
                vCard = vCardManager.loadVCard();
                vCard.setNickName(Pref.getPreferenceUser() + Constant.DOMAIN);
                vCard.setFirstName(loginName);
                vCardManager.saveVCard(vCard);
                Pref.savePreferenceUserProfileName(loginName);

            } catch (Exception e) {
                e.printStackTrace();
                //FileLog.e("Yahala create Account", e.toString());
                Log.e("Openfire create Account", e.toString());

            }


        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();

            /*new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    instance = null;
                    //  Toast.makeText(context, "Invalid username and password", Toast.LENGTH_SHORT).show();
                    context.stopService(new Intent(context, MyService.class));
                    connection.disconnect();
                    if (connection.isConnected()) {
                        Log.d("Login_login_connect", loginUser);
                    } else {
                        Log.d("Login_login_disconnect", loginUser);
                    }

                }
            });*/
            Log.d("Login_login", "catch" + e.toString());

//

        } catch (Exception e) {
            Log.d("Login_login", "catchgeneral");

        }

    }

    public void sendMessage(Message message) {

        try {


            if (connection.isConnected()) {
                if (connection.isAuthenticated()) {
                    //  Mychat.sendMessage(message);
                    connection.sendStanza(message);
                    //        newChat.sendMessage(message);


                    Log.e("xmpp.SendMessage()", "msg sent!- Connected!");
                } else {
                    login();
                }
            } else {
                connection.connect();

            }

        } catch (SmackException.NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public FileTransferManager getFileTransferManager() {
//        FileTransferManager fileTransferManager = FileTransferManager.getInstanceFor(MyXMPP.getConnection());
//        fileTransferManager.createOutgoingFileTransfer(acc);
        return FileTransferManager.getInstanceFor(getConnection());
    }


    public void sendImage(File file, String to)  {



        String realJid = to + "/Smack";
        Log.d(TAG, "doInBackground: " + realJid);
        Log.d(TAG, "doInBackground: getAbsolutePath " + file.getAbsolutePath());
        Log.d(TAG, "doInBackground: getName  " + file.getName());


        String bareJID = XmppStringUtils.parseBareJid(to);



        if (file.exists()) {
            String name = file.getName();
            try {
                FileTransferManager ftm = getFileTransferManager();
                if (ftm == null) {
                    Log.d(TAG, "doInBackground: 1 FileTransferManager not initialized");

                }
                final OutgoingFileTransfer out = ftm.createOutgoingFileTransfer(realJid);
                try {
                    out.sendFile(file, "Image");
                    Log.d(TAG, "doInBackground: " + file.getAbsolutePath());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(out.isDone()){
                    Log.d(TAG, "doInBackground: image send");
                }

                // start service to save the image to sqlite
                if (out.getStatus().equals(FileTransfer.Status.complete)) {
                    Log.d(TAG, "doInBackground: status completed image send");

                }


                // Add listener to cancel transfer is sending file to user who just went offline.
                AndFilter presenceFilter = new AndFilter(new StanzaTypeFilter(Presence.class), FromMatchesFilter.createBare(bareJID));


                final StanzaListener packetListener = new StanzaListener() {
                    @Override
                    public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {
                        Presence presence = (Presence) stanza;
                        if (!presence.isAvailable()) {
                            if (out != null) {
                                out.cancel();
                            }
                        }
                    }
                };

                // Add presence listener to check if user is offline and cancel sending.
                MyXMPP.getConnection().addAsyncStanzaListener(packetListener, presenceFilter);

                FileTransfer.Status lastStatus = FileTransfer.Status.initial;

                while (!out.isDone()) {
                    FileTransfer.Status status = out.getStatus();
                    Log.d(TAG, " doInBackground sendImage: status "+status);

                    try {
                        if (status != lastStatus) {
                            //  Notify.fileProgress(name, status);
                            Log.d(TAG, "doInBackground: 2" + out.getError());
                            lastStatus = status;
                        }
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored) {
                        ignored.printStackTrace();
                        ignored.getMessage();
                    }
                }

                Log.d(TAG, "doInBackground sendImage: lastStatus "+lastStatus);
                Log.d(TAG, "doInBackground: 3" + out.getError());
                Log.d(TAG, "doInBackground:getStreamID " + out.getStreamID());

                String updateSet = " MsgID = ? ";

                String JID[] =to.split("@");

                if (JID[0] != null)
                    new Update(ChatMessages.class)
                            .set(updateSet, out.getStreamID())
                            .where("Jid = ?", JID[0])
                            .execute();

                //Notify.fileProgress(name, out.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
                //Notify.fileProgress(name, FileTransfer.Status.error);
            }
        } else {
            Log.d(TAG, "doInBackground: file doesnt exists" + file.getAbsolutePath());
        }



    }


    private void configSSLContext(XMPPTCPConnectionConfiguration.Builder builder, KeyStore keyStore)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

        builder.setCustomSSLContext(sslContext);
    }

    private KeyStore configKeyStore(XMPPTCPConnectionConfiguration.Builder builder) throws KeyStoreException {
        KeyStore keyStore;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder.setKeystorePath(null);
            builder.setKeystoreType("AndroidCAStore");
            keyStore = KeyStore.getInstance("AndroidCAStore");
        } else {
            builder.setKeystoreType("BKS");
            keyStore = KeyStore.getInstance("BKS");

            String path = System.getProperty("javax.net.ssl.trustStore");
            if (path == null)
                path = System.getProperty("java.home") + File.separator + "etc"
                        + File.separator + "security" + File.separator
                        + "cacerts.bks";
            builder.setKeystorePath(path);
        }
        return keyStore;
    }

    public String getLastSeenMessage(String jid) {

        Log.d("MyXMPP", "getLastSeenMessage: " + jid);
        try {
            LastActivityManager lastActivityManager = LastActivityManager.getInstanceFor(MyXMPP.getConnection());
            LastActivity activity = lastActivityManager.getLastActivity(jid);

            int lastSeenBySeconds = Utilities.parseInt(activity.lastActivity + "");

            Log.d("MyXMPP", "getLastSeenMessage: " + lastSeenBySeconds);
            String lastSeenMessage = "";
            lastSeenMessage = LocaleController.getString("Offline", R.string.Offline);
            if (lastSeenBySeconds >= 1) {
                PrettyTime p = new PrettyTime();
                Date date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, -1 * lastSeenBySeconds);

                lastSeenMessage = p.format(cal.getTime()); //p.format(cal.getTime());

            } else {
                lastSeenMessage = "Online";

            }
            //FileLog.e("LAST ACTIVITY","" + ""+ "" + lastSeenBySeconds +"  "+jid);
            Log.d("MyXMPP", "getLastSeenMessage: " + lastSeenMessage);
            return lastSeenMessage;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MyXMPP", "getLastSeenMessage: " + e.getMessage());
            return "Offline";
        }
        //return "Offline";
    }
}
