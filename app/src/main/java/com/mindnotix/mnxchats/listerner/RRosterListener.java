package com.mindnotix.mnxchats.listerner;

import android.util.Log;

import com.activeandroid.query.Update;
import com.mindnotix.mnxchats.BaseActivity;
import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.activeandroid.SqliteDatabaseManager;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;

/**
 * Created by Admin on 11/23/2017.
 */

public class RRosterListener  extends BaseActivity implements RosterListener {
    private static final String TAG = "RRosterListener";

    @Override
    public void entriesAdded(Collection<String> addresses) {
        Log.d(TAG, "entriesAdded: "+addresses.iterator());
    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {
        Log.d(TAG, "entriesUpdated: ");
    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {
        Log.d(TAG, "entriesDeleted: ");
    }

    @Override
    public void presenceChanged(Presence presence) {

        Log.d(TAG, "presenceChanged: STATUS "+presence.getStatus() + "\n FROM "+presence.getFrom());
        Log.d(TAG, "presenceChanged: STATUS 0 "+presence.getLanguage() );





        String userJID[] = presence.getFrom().split("@");
        MyContacts myContacts = SqliteDatabaseManager.getContact(userJID[0]);

        if(myContacts != null){


            if(presence.getPriority()== 24){
                String updateSet = " blockStatus = ? ";

                new Update(MyContacts.class)
                        .set(updateSet,"1")
                        .where("Jid = ?", userJID[0])
                        .execute();

            }else{

                String updateSet = " blockStatus = ? ";

                new Update(MyContacts.class)
                        .set(updateSet,"0")
                        .where("Jid = ?", userJID[0])
                        .execute();
            }


            if(presence.getLanguage() != null){
                String updateSet = " image_path = ? ";

                new Update(MyContacts.class)
                        .set(updateSet,presence.getLanguage())
                        .where("Jid = ?", userJID[0])
                        .execute();

                Events.ActivityUserProfileChange activityUserProfileChange =
                        new Events.ActivityUserProfileChange(presence.getLanguage(),userJID[0]);

                GlobalBus.getBus().postSticky(activityUserProfileChange);
            }

            if(presence.getStatus() != null ){
                String updateSet = " status = ? ";

                new Update(MyContacts.class)
                        .set(updateSet,presence.getStatus())
                        .where("Jid = ?", userJID[0])
                        .execute();

                Events.ActivityUserStatusChange activityGroupNameChange =
                        new Events.ActivityUserStatusChange(presence.getStatus(),userJID[0]);

                GlobalBus.getBus().postSticky(activityGroupNameChange);
            }
        }



    }


    public static String byteArrayToURLString(byte in[]) {
        byte ch = 0x00;
        int i = 0;
        if (in == null || in.length <= 0)
            return null;

        String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "A", "B", "C", "D", "E", "F" };
        StringBuffer out = new StringBuffer(in.length * 2);

        while (i < in.length) {
            // First check to see if we need ASCII or HEX
            if ((in[i] >= '0' && in[i] <= '9')
                    || (in[i] >= 'a' && in[i] <= 'z')
                    || (in[i] >= 'A' && in[i] <= 'Z') || in[i] == '$'
                    || in[i] == '-' || in[i] == '_' || in[i] == '.'
                    || in[i] == '!') {
                out.append((char) in[i]);
                i++;
            } else {
                out.append('%');
                ch = (byte) (in[i] & 0xF0); // Strip off high nibble
                ch = (byte) (ch >>> 4); // shift the bits down
                ch = (byte) (ch & 0x0F); // must do this is high order bit is
                // on!
                out.append(pseudo[(int) ch]); // convert the nibble to a
                // String Character
                ch = (byte) (in[i] & 0x0F); // Strip off low nibble
                out.append(pseudo[(int) ch]); // convert the nibble to a
                // String Character
                i++;
            }
        }

        String rslt = new String(out);

        return rslt;

    }
}
