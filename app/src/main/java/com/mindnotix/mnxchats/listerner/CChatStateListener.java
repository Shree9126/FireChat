package com.mindnotix.mnxchats.listerner;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mindnotix.mnxchats.service.MyService;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;

/**
 * Created by Sridharan on 11/28/2017.
 */

public  class CChatStateListener implements ChatStateListener {
    public CChatStateListener(MyService context) {
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



                    }
                });

                break;
            case composing:
                Log.d("state", "composing");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub



                    }
                });

                break;
            case paused:
                Log.d("state", "paused");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub


                    }
                });

                break;
            case inactive:
                Log.d("state", "inactive");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub



                    }
                });

                break;
            case gone:
                Log.d("state", "gone");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub



                    }
                });

                break;
        }
    }

    @Override
    public void processMessage(Chat chat, Message message) {

    }
}
