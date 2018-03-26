package com.mindnotix.mnxchats.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mindnotix.mnxchats.ChatActivity;
import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.activeandroid.ChatMessages;
import com.mindnotix.mnxchats.bitmapcache.ImageMessageFetcher;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by Sridharan on 11/30/2017.
 */

public class SingleChatAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int CHAT_SENDER = 1;
    public static final int CHAT_SENDER_IMAGE = 3;
    public static final int CHAT_RECEIVER = 2;
    public static final int CHAT_RECEIVER_IMAGE = 4;
    public static final int CHAT_SENDER_MAIL = 0;
    private static final String TAG ="SingleChatAdapter" ;
    ArrayList<ChatMessages> messagesArrayList;
    ChatActivity chatActivity;
    private ImageMessageFetcher imageFetcher;

    public SingleChatAdapter(ArrayList<ChatMessages> messagesArrayList, ChatActivity chatActivity) {
        this.messagesArrayList = messagesArrayList;
        this.chatActivity = chatActivity;
        imageFetcher = new ImageMessageFetcher(chatActivity);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        Log.d(TAG, "onCreateViewHolder: viewType "+viewType );

        switch (viewType) {
            case CHAT_SENDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sender, parent, false);
                return new SenderViewHolder(view);
            case CHAT_RECEIVER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_receiver, parent, false);
                return new ReceiverViewHolder(view);
            case CHAT_SENDER_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_image_sender, parent, false);
                return new SenderImageViewHolder(view);
            case CHAT_RECEIVER_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflat_image_reciever, parent, false);
                return new ReceiverImageViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int userType;
        userType = Integer.parseInt(messagesArrayList.get(position).getIsUser());
        Log.d(TAG, "onBindViewHolder: usertype "+userType);
        bindSwitchCase(userType,holder,position);
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (messagesArrayList != null) {
            ChatMessages object = messagesArrayList.get(position);
            if (object != null) {
                return Integer.parseInt(object.getIsUser());
            }
        }
        return 0;
    }
    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private EmojiconTextView txtSendMsg;
        private TextView txtDateTime,txtOtherSideID;
        private CircleImageView imgProfilePic;
        private ImageView imgDouble, imgSingle;

        public SenderViewHolder(View itemView) {
            super(itemView);

            txtSendMsg =  (EmojiconTextView)itemView.findViewById(R.id.message_text);
            txtDateTime = (TextView) itemView.findViewById(R.id.message_date_time);
            imgDouble = (ImageView) itemView.findViewById(R.id.imgDoubleTick);
            imgSingle = (ImageView) itemView.findViewById(R.id.imgSingleTick);

        }
    }

    public static class SenderImageViewHolder extends RecyclerView.ViewHolder {
        protected TextView time;
        private ImageView image;
        private ProgressBar progressBar;
        private ImageView imgDouble, imgSingle;
        public SenderImageViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.image);
            time = (TextView)itemView.findViewById(R.id.tv_time);
            progressBar=(ProgressBar)itemView.findViewById(R.id.sending_progress);
            imgDouble = (ImageView) itemView.findViewById(R.id.imgDoubleTick);
            imgSingle = (ImageView) itemView.findViewById(R.id.imgSingleTick);
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private EmojiconTextView txtSendMsg;
        private TextView txtDateTime;
        private ImageView imgDouble, imgSingle;

        public ReceiverViewHolder(View itemView) {
            super(itemView);

            txtSendMsg = (EmojiconTextView) itemView.findViewById(R.id.message_text);
            txtDateTime = (TextView) itemView.findViewById(R.id.message_date_time);
            imgDouble = (ImageView) itemView.findViewById(R.id.imgDoubleTick);
            imgSingle = (ImageView) itemView.findViewById(R.id.imgSingleTick);

        }
    }


    public static class ReceiverImageViewHolder extends RecyclerView.ViewHolder {

        protected TextView time;
        private ImageView ivImg;

        public ReceiverImageViewHolder(View itemView) {
            super(itemView);

            ivImg = (ImageView)itemView.findViewById(R.id.ivImg);
            time = (TextView)itemView.findViewById(R.id.tv_time);

        }
    }

    private void bindSwitchCase(int userType, RecyclerView.ViewHolder holder, int position) {

        switch (userType) {

            case CHAT_SENDER_IMAGE:

                String filePath_sender =messagesArrayList.get(position).getMedia_url();

                Log.d(TAG, "bindSwitchCase:filePath_sender "+filePath_sender);

                final SenderImageViewHolder senderImageViewHolder = (SenderImageViewHolder) holder;


                Picasso.with(chatActivity)
                        .load(new File(filePath_sender))
                        .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(senderImageViewHolder.image, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                                senderImageViewHolder.progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                              //  senderImageViewHolder.progressBar.cancelLongPress();
                                senderImageViewHolder.progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                senderImageViewHolder.time.setText(messagesArrayList.get(position).getTime());

                Log.d(TAG, "bindSwitchCase: status "+messagesArrayList.get(position).getStatus());
                if (messagesArrayList.get(position).getStatus().equals("1")) {
                    senderImageViewHolder.progressBar.setVisibility(View.INVISIBLE);
                    senderImageViewHolder.imgDouble.setVisibility(View.VISIBLE);
                    senderImageViewHolder.imgSingle.setVisibility(View.GONE);
                } else {
                    senderImageViewHolder.progressBar.setVisibility(View.VISIBLE);
                    senderImageViewHolder.imgDouble.setVisibility(View.GONE);
                    senderImageViewHolder.imgSingle.setVisibility(View.VISIBLE);
                }

                break;

            case CHAT_RECEIVER_IMAGE:


                String filePath_reciever =messagesArrayList.get(position).getMedia_url();
                Log.d(TAG, "bindSwitchCase: filePath_reciever "+filePath_reciever);

                ReceiverImageViewHolder receiverImageViewHolder= (ReceiverImageViewHolder) holder;
               // imageFetcher.loadImage(filePath_reciever, receiverImageViewHolder.ivImg);



                Picasso.with(chatActivity)
                        .load(new File(filePath_reciever))
                        .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(receiverImageViewHolder.ivImg, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });
                receiverImageViewHolder.time.setText(messagesArrayList.get(position).getTime());

                break;

            case CHAT_SENDER:
                Log.d(TAG, "SenderViewHolder: " + messagesArrayList.get(position).getBody());
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                senderViewHolder.txtSendMsg.setText(messagesArrayList.get(position).getBody());
                senderViewHolder.txtDateTime.setText(messagesArrayList.get(position).getTime());

                Log.d(TAG, "bindSwitchCase: status "+messagesArrayList.get(position).getStatus());
                if (messagesArrayList.get(position).getStatus().equals("1")) {
                    senderViewHolder.imgDouble.setVisibility(View.VISIBLE);
                    senderViewHolder.imgSingle.setVisibility(View.GONE);
                } else {
                    senderViewHolder.imgDouble.setVisibility(View.GONE);
                    senderViewHolder.imgSingle.setVisibility(View.VISIBLE);
                }


                break;

            case CHAT_RECEIVER:
                Log.d(TAG, "ReceiverViewHolder: " + messagesArrayList.get(position).getBody());
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                receiverViewHolder.txtSendMsg.setText(messagesArrayList.get(position).getBody());
                receiverViewHolder.txtDateTime.setText(messagesArrayList.get(position).getTime());

                Log.d("aaawwwww", "getView: " + messagesArrayList.get(position).getStatus());
                receiverViewHolder.imgDouble.setVisibility(View.GONE);
                receiverViewHolder.imgSingle.setVisibility(View.GONE);
                break;
            case CHAT_SENDER_MAIL:
                break;

        }

    }
}
