package com.mindnotix.mnxchats.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Update;
import com.mindnotix.mnxchats.ChatActivity;
import com.mindnotix.mnxchats.ProfileImageUpdateActivity;
import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.bitmapcache.AvatarImageFetcher;
import com.mindnotix.mnxchats.connection.MyXMPP;
import com.mindnotix.mnxchats.listerner.MMessageListener;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.Pref;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sridharan on 12/1/2017.
 */

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {

    private static final String TAG = "ChatConversationAdapter";
    String letter;
    ArrayList<MyContacts> myContactsArrayList;
    private Activity activity;
    private AvatarImageFetcher imageFetcher;

    public AvatarImageFetcher getImageFetcher() {
        return imageFetcher;
    }

    public ChatConversationAdapter(Activity activity, ArrayList<MyContacts> myContactsArrayList) {

        Log.d(TAG, "ChatConversationAdapter: constructor");
        this.activity = activity;
        this.myContactsArrayList = myContactsArrayList;
        imageFetcher = AvatarImageFetcher.getAvatarImageFetcher(activity);
    }

    @Override
    public ChatConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.conversation_list_items, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatConversationAdapter.ViewHolder holder, int position) {
        MyContacts conversationTable = myContactsArrayList.get(position);
        String title = "";
        if (conversationTable.getUname() != null) {

            Log.d(TAG, "onBindViewHolder:contactRequestTable " + conversationTable.getUname());

            title = conversationTable.getUname().substring(0, 1).toUpperCase();

            title += conversationTable.getUname().substring(1).toLowerCase();

            letter = String.valueOf(title);
        } else {
            title = conversationTable.getNickname();
            String part[] = title.split("@");
            letter = String.valueOf(part[0]);
        }

        holder.tvName.setText(letter);
        holder.tvCount.setVisibility(View.VISIBLE);
        holder.tvCount.setText(myContactsArrayList.get(position).getUn_read());
        holder.tvmessage.setText(myContactsArrayList.get(position).getLastMessage());

        if (conversationTable.getImage_path() != null) {

            Log.d(TAG, "onBindViewHolder:if " + conversationTable.getImage_path());

            Picasso.with(activity)
                    .load(conversationTable.getImage_path())
                    .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(holder.ivProfile);


        } else {

            imageFetcher.loadImage(conversationTable.getJid()+Constant.DOMAIN, holder.ivProfile);
           /* Log.d(TAG, "onBindViewHolder:e " + conversationTable.getImage_path());
            SetProfileImageAsync task = new SetProfileImageAsync(holder.ivProfile, conversationTable.getJid(),position);
            task.execute();*/

        }
    }

    @Override
    public int getItemCount() {

        Log.d(TAG, "getItemCount: size " + myContactsArrayList.size());
        return myContactsArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCount;
        LinearLayout laymain;
        TextView tvmessage;
        TextView tvName;
        CircleImageView ivProfile;

        ViewHolder(View itemView) {
            super(itemView);

            tvCount = (TextView) itemView.findViewById(R.id.tv_Count);
            laymain = (LinearLayout)itemView.findViewById(R.id.lay_main);
            laymain.setOnClickListener(this);
            tvmessage = (TextView)itemView.findViewById(R.id.tv_message);
            tvName = (TextView) itemView.findViewById(R.id.tv_Name);
            ivProfile =(CircleImageView) itemView.findViewById(R.id.iv_Profile);
            ivProfile.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();
            switch (v.getId()) {

                case R.id.iv_Profile:
                    Log.d(TAG, "onClick:image_path  "+myContactsArrayList.get(pos).getImage_path()+" pos "+pos);
                    Intent intent = new Intent(activity,ProfileImageUpdateActivity.class);
                    intent.putExtra("userType",0);
                    intent.putExtra("image_path",myContactsArrayList.get(pos).getImage_path());
                    activity.startActivity(intent);
                    break;
                case  R.id.lay_main:

                    Log.d(TAG, "onClick: "+pos);
                    Pref.saveSecondUserPreference(myContactsArrayList.get(pos).getJid() + Constant.DOMAIN);

                    tvCount.setVisibility(View.INVISIBLE);
                    myContactsArrayList.get(pos).setUn_read("0");
                    notifyDataSetChanged();
/*
                    ChatActivity.navigate(myContactsArrayList.get(pos).getJid(),myContactsArrayList.get(pos).getUname(),
                            0,pos,myContactsArrayList.get(pos).getBlock_status(),
                            myContactsArrayList.get(pos).getMute_status(),
                            myContactsArrayList.get(pos).getImage_path(),
                            myContactsArrayList.get(pos).getStatus(),"0",activity);*/
                    Intent mChatActivity = new Intent(activity,ChatActivity.class);
                    mChatActivity.putExtra("TO_JID",myContactsArrayList.get(pos).getJid());
                    mChatActivity.putExtra("TO_JID_NAME",myContactsArrayList.get(pos).getUname());
                    mChatActivity.putExtra("fragment_position",0);
                    mChatActivity.putExtra("item_position",pos);
                    mChatActivity.putExtra("block_status",myContactsArrayList.get(pos).getBlock_status());
                    mChatActivity.putExtra("mute_status",myContactsArrayList.get(pos).getMute_status());
                    mChatActivity.putExtra("image_path",myContactsArrayList.get(pos).getImage_path());
                    mChatActivity.putExtra("presence_status",myContactsArrayList.get(pos).getStatus());
                    //Navigation zero(0) means navigate from conversation fragment screen
                    mChatActivity.putExtra("NavigateFrom","0");

                   /* MMessageListener.getBuilder();
                    MMessageListener.getNotificationManager();*/
                    activity.startActivity(mChatActivity);

                    break;
                default:

            }

        }
    }

    private class SetProfileImageAsync extends AsyncTask<Integer, Void, String> {

        private CircleImageView mImageView;
        private String TAG;
        private String bitmap;

        private int pos;
        public SetProfileImageAsync(CircleImageView imageView, String TAG, int position) {
            mImageView = imageView;
            this.TAG = TAG;
            this.pos = position;
        }

        @Override
        protected String doInBackground(Integer... params) {

            Log.d(TAG, "doInBackground: position "+pos);

            VCardManager vCardManager = VCardManager.getInstanceFor(MyXMPP.getConnection());
            boolean isSupported = false;
            try {
                Log.d(TAG, "onBindViewHolder: username " + TAG + Constant.DOMAIN);
                isSupported = vCardManager.isSupported(TAG + Constant.DOMAIN);
                if (isSupported) {
                    Log.d(TAG, "onBindViewHolder: username " + TAG);
                    VCard vCard = vCardManager.loadVCard(TAG + Constant.DOMAIN);
                    myContactsArrayList.get(pos).setImage_path(vCard.getEmailHome());
                    //get the image path from EMAILHOME here
                    return vCard.getEmailHome();
                } else {
                    return null;
                }

            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(String bitmap) {
            if (bitmap != null) {


                String updateSet = " image_path = ? ";

                new Update(MyContacts.class)
                        .set(updateSet,bitmap)
                        .where("Jid = ?", TAG)
                        .execute();
                Picasso.with(activity)
                        .load(bitmap)
                        .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(mImageView);

            } else {
                Log.d(TAG, "onPostExecute: Bitmap is NULL");
            }
        }

    }


}
