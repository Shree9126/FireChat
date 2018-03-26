package com.mindnotix.mnxchats.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.mindnotix.mnxchats.ChatActivity;
import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.activeandroid.ChatMessages;
import com.mindnotix.mnxchats.bitmapcache.BitmapUtils;
import com.mindnotix.mnxchats.bitmapcache.Compressor;
import com.mindnotix.mnxchats.connection.MyXMPP;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;
import com.mindnotix.mnxchats.utils.CommonMethods;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.FileUtils;
import com.mindnotix.mnxchats.utils.Pref;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Sridharan on 13/12/2017.
 */
public class SendImageTask extends SendMessageTask {
    private String fileName;
    private Uri uri;
    private File file;
    private File compressedImage;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_PENDING = 2;
    public static final int STATUS_FAILURE = 3;

    //Image
    public static final int TYPE_INCOMING_IMAGE = 4;
    public static final int TYPE_OUTGOING_IMAGE = 3;
    Context context;

    public SendImageTask(Response.Listener<Boolean> listener, Context context, String to, String nickname, String body, Uri uri, String fileName) {
        super(listener, context, to, nickname, body);

        this.context =context;
        this.uri = uri;
        this.fileName = fileName;
    }

    public static String newImageMessage(String jid, String body, long timeMillis, String path, boolean outgoing) {

        long serverTimestamp;
        String timestamp;
        serverTimestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        Date resultdate = new Date(serverTimestamp);
        System.out.println("processPacket_system_timestamp" + sdf.format(resultdate));
        timestamp = sdf.format(resultdate);
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.setJid(jid);
        chatMessages.setBody(body);
        chatMessages.setDate(CommonMethods.getCurrentDate());
        chatMessages.setTime(timestamp);
        chatMessages.setIsUser(String.valueOf(outgoing ? TYPE_OUTGOING_IMAGE : TYPE_INCOMING_IMAGE));
        chatMessages.setMedia_url(path);
        chatMessages.setMyJid(Pref.getPreferenceUser()+Constant.DOMAIN);
        chatMessages.setStatus(String.valueOf(outgoing ? STATUS_PENDING : STATUS_SUCCESS));
        chatMessages.save();


        Events.ChatActvityReceiveImages chatActvityReceiveImages =
                new Events.ChatActvityReceiveImages(jid,body,timeMillis,path,
                        String.valueOf(outgoing ? STATUS_PENDING : STATUS_SUCCESS),
                        String.valueOf(outgoing ? TYPE_OUTGOING_IMAGE : TYPE_INCOMING_IMAGE));

        GlobalBus.getBus().postSticky(chatActvityReceiveImages);
        return jid;
    }

    private File createSentImageFile(Context context, String fileName, Bitmap bitmap) throws IOException {

        file = new File(FileUtils.getSentImagesDir(context), fileName + FileUtils.IMAGE_EXTENSION);
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        return file;
    }

    @Override
    protected String  newMessage(long sendTimeMillis) throws IOException {

        Context context = getContext();
        int size = context.getResources().getDimensionPixelSize(R.dimen.sent_image_size);
        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromStream(context, uri, size, size);
        file = createSentImageFile(context, fileName, bitmap);
        return newImageMessage(to, body, sendTimeMillis, file.getAbsolutePath(), true);

    }

    @Override
    protected void doSend(Context context) throws Exception {

        if (file != null) {

            new Compressor(context)
                    .compressToFileAsFlowable(file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            compressedImage = file;
                               MyXMPP.getInstance().sendImage(compressedImage, to + Constant.DOMAIN);

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();

                        }
                    });
        } else {
            throw new FileNotFoundException(fileName);
        }

    }


}