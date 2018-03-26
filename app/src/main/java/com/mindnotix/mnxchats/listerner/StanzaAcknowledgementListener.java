package com.mindnotix.mnxchats.listerner;

import com.mindnotix.mnxchats.service.MyService;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Stanza;

/**
 * Created by Admin on 11/28/2017.
 */

public class StanzaAcknowledgementListener implements StanzaListener {
    public StanzaAcknowledgementListener(MyService context) {
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

    }
}
