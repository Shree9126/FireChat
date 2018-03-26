package com.mindnotix.mnxchats.task;

import android.content.Context;
import android.util.Log;

import com.activeandroid.query.Update;
import com.mindnotix.mnxchats.activeandroid.ChatMessages;
import com.mindnotix.mnxchats.activeandroid.MyContacts;

import static com.mindnotix.mnxchats.task.SendImageTask.STATUS_FAILURE;
import static com.mindnotix.mnxchats.task.SendImageTask.STATUS_SUCCESS;

/**
 * Created by Sridharan on 13/12/2017.
 */
public abstract class SendMessageTask extends BaseAsyncTask<Void, Void, Boolean> {
    private static final String TAG = "SendMessageTask";
    protected String to;
    protected String nickname;
    protected String body;

    public SendMessageTask(Response.Listener<Boolean> listener, Context context, String to, String nickname, String body) {
        super(listener, context);

        this.to = to;
        this.nickname = nickname;
        this.body = body;
    }

    @Override
    public Response<Boolean> doInBackground(Void... params) {
        Context context = getContext();
        String  JID = null;
        if (context == null) {
            return null;
        }

        try {
              JID = newMessage(System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            doSend(context);
        } catch (Exception e) {


            Log.d(TAG, "doInBackground: " + to + " error :" + e.getMessage());

            String updateSet = " deliver_status = ? ";

            if (JID != null)
                new Update(ChatMessages.class)
                        .set(updateSet, STATUS_FAILURE)
                        .where("Jid = ?", JID)
                        .execute();


            return Response.error(e);
        }


        String updateSet = " deliver_status = ? ";


        if (JID != null)
        new Update(ChatMessages.class)
                .set(updateSet, STATUS_SUCCESS)
                .where("Jid = ?", JID)
                .execute();


        return Response.success(true);
    }

    protected abstract String newMessage(long sendTimeMillis) throws Exception;

    protected abstract void doSend(Context context) throws Exception;
}