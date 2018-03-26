package com.mindnotix.mnxchats.activeandroid;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.Pref;

import java.util.List;

/**
 * Created by Sridharan on 11/24/2017.
 */

public class SqliteDatabaseManager {


    private static final String TAG = "SqliteDatabaseManager";


    public static MyContacts getContact(String UserJID) {
        return new Select()
                .from(MyContacts.class)
                .where("Jid = ?", UserJID)
                .executeSingle();
    }

    public static List<MyContacts> getMyContactRandom(String fromJID) {
        String from_jid[] = new String[2];
        if (fromJID.contains(Constant.DOMAIN)) {
            from_jid = fromJID.split("@");
        } else {
            from_jid[0] = fromJID;
        }

        return new Select()
                .from(MyContacts.class)
                .where("Jid = ?", from_jid[0])
                .orderBy("RANDOM()")
                .execute();

    }

    public static List<ChatMessages> getAllMessageFromChatMessages(String user_two) {

        String my_jid = Pref.getPreferenceUser() + Constant.DOMAIN;
        return SQLiteUtils.rawQuery(ChatMessages.class,
                "SELECT * from ChatMessages where Jid = ? AND MyJid = ?",
                new String[]{user_two, my_jid});

    }

    public List<MyContacts> getALLContacts() {

        Log.d(TAG, "getContacts: ");
/*        return new Select()
                .all()
                .from(MyContacts.class)
                .where("contact_status = ?", "1")
                .execute();*/


        return new Select()
                .from(MyContacts.class)
                .where("contact_status = ?", "1")
                .groupBy("Jid")
                .execute();


    }


    public static List<MyContacts> getALLConversation() {

        Log.d(TAG, "getContacts: ");
/*
        return new Select()
                .all()
                .from(MyContacts.class)
                .where("conversation_status = ?", "1")
                .execute();*/

        return new Select()
                .from(MyContacts.class)
                .where("conversation_status = ?", "1")
                 .groupBy("Jid")
                .execute();

    }

    public void delContacts() {

        Log.d(TAG, "delContacts: ");

        String whereSet = " Type = ? ," + " contact_status = ? ";

        new Delete().from(MyContacts.class)
                .where("Type = ?", "Friend")
                .where("contact_status = ?", "1")
                .execute();
    }

}
