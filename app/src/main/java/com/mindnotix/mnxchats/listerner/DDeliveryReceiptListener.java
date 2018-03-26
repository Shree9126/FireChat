package com.mindnotix.mnxchats.listerner;

import android.util.Log;

import com.activeandroid.query.Update;
import com.mindnotix.mnxchats.activeandroid.ChatMessages;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;
import com.mindnotix.mnxchats.service.MyService;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

/**
 * Created by Sridharan on 11/28/2017.
 */

public class DDeliveryReceiptListener implements ReceiptReceivedListener {
    public static String delivery_receiptID = "";

    public DDeliveryReceiptListener(MyService context) {
    }

    @Override
    public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {


        delivery_receiptID = receiptId;
        Log.i("DeliveryReceiptManager", "From jid" + fromJid);
        Log.i("DeliveryReceiptManager", "To jid " + toJid);
        Log.i("DeliveryReceiptManager", "Receipt id  " + receiptId);
        Log.i("DeliveryReceiptManager", "recipt " + receipt.toString());
        // Log.d("DeliveryReceiptManager", "onReceiptReceived: from: " + fromid + " to: " + toid + " deliveryReceiptId: " + msgid + " stanza: " + packet);

        String[] parts = fromJid.split("@");
        String part1 = parts[0];
        String part2 = parts[1];
        Log.d("MyXMPP", "onReceiptReceived_part1: " + part1);
        Log.d("MyXMPP", "onReceiptReceived_part2: " + part2);

        String deliver_status = "2";
        String updateSet = " deliver_status = ? ," +
                " deliveryReceiptId = ? ";

        String whereSet = "Jid = ? and MsgID = ?";

                            new Update(ChatMessages.class)
                                    .set(updateSet, deliver_status, receiptId)
                                    .where(whereSet, part1, receiptId)
                                    .execute();

        Events.ChatActivityReferesh chatActivityReferesh =
                new Events.ChatActivityReferesh(receiptId);

        GlobalBus.getBus().postSticky(chatActivityReferesh);
        //Log.d("MyXMPP", "onReceiptReceived:array_size " + ChatActivity.messages.size());

        //  getRefresh(receiptID);

    }
}
