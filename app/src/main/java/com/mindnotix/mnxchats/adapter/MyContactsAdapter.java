package com.mindnotix.mnxchats.adapter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Update;
import com.mindnotix.mnxchats.ChatActivity;
import com.mindnotix.mnxchats.ContactActivity;
import com.mindnotix.mnxchats.ProfileImageUpdateActivity;
import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.UserProfileActivity;
import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.activeandroid.SqliteDatabaseManager;
import com.mindnotix.mnxchats.bitmapcache.AvatarImageFetcher;
import com.mindnotix.mnxchats.connection.MyXMPP;
import com.mindnotix.mnxchats.listerner.MMessageListener;
import com.mindnotix.mnxchats.openfire.OpenfireService;
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
 * Created by Sridharan on 11/24/2017.
 */

public class MyContactsAdapter extends RecyclerView.Adapter<MyContactsAdapter.ViewHolder>  {
    private static final String TAG = "MyContactsAdapter";
    ContactActivity contactActivity;
    ArrayList<MyContacts> myContactsArrayList;
    private String letter;
    SqliteDatabaseManager databaseManager;
    OpenfireService openfireService;
    String online_offline=null;


    private AvatarImageFetcher imageFetcher;

    public AvatarImageFetcher getImageFetcher() {
        return imageFetcher;
    }

    public MyContactsAdapter(ContactActivity contactActivity, ArrayList<MyContacts> myContactsArrayList, String on_off) {
        this.contactActivity = contactActivity;
        databaseManager = new SqliteDatabaseManager();
        this.myContactsArrayList = myContactsArrayList;
        openfireService = new OpenfireService();

        online_offline = on_off;
        imageFetcher = AvatarImageFetcher.getAvatarImageFetcher(contactActivity);
    }

    @Override
    public MyContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contactActivity).inflate(R.layout.items_list_contact, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyContactsAdapter.ViewHolder holder, int position) {


        Log.d(TAG, "onBindViewHolder: " + myContactsArrayList.get(position).getUname());
        MyContacts myContacts = myContactsArrayList.get(position);

        String title = "";

        Log.d(TAG, "onBindViewHolder: STATUS" + myContacts.getStatus());




        if (myContacts.getUname() != null) {
            title = myContacts.getUname().substring(0, 1).toUpperCase();

            title += myContacts.getUname().substring(1).toLowerCase();

            letter = String.valueOf(title);
        } else {
            title = myContacts.getUname();
            String part[] = title.split("@");
            letter = String.valueOf(part[0]);
        }

        holder.tvName.setText(letter);

        holder.tvStatus.setText(myContacts.getStatus());


        if(myContacts.getImage_path()!=null){
            Log.d(TAG, "onBindViewHolder:if "+myContacts.getImage_path());

                Picasso.with(contactActivity)
                        .load(myContacts.getImage_path())
                        .placeholder(R.drawable.ic_def_profile) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.ic_def_profile)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(holder.ivProfile);


        }else{
            Log.d(TAG, "onBindViewHolder:e "+myContacts.getImage_path());
           /* SetProfileImageAsync task = new SetProfileImageAsync(holder.ivProfile, myContacts.getJid(),position);
            task.execute();*/
            imageFetcher.loadImage(myContacts.getJid()+Constant.DOMAIN, holder.ivProfile);

        }



    }

    @Override
    public int getItemCount() {
        return myContactsArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        RelativeLayout rootlay;
        LinearLayout laymain;
        TextView tvStatus;
        TextView tvName;
        CircleImageView ivProfile;

        public ViewHolder(View itemView) {
            super(itemView);

            rootlay =(RelativeLayout) itemView.findViewById(R.id.rootlay);
            laymain = (LinearLayout)itemView.findViewById(R.id.lay_main);
            tvStatus = (TextView)itemView.findViewById(R.id.tv_Status);
            tvName =(TextView) itemView.findViewById(R.id.tv_Name);
            ivProfile = (CircleImageView)itemView.findViewById(R.id.iv_Profile);
            laymain.setOnClickListener(this);
            ivProfile.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int pos = getAdapterPosition();
            switch (v.getId()){

                case R.id.iv_Profile:

               /*     Log.d(TAG, "onClick:image_path  "+myContactsArrayList.get(pos).getImage_path()+" pos "+pos);
                    if(myContactsArrayList.get(pos).getImage_path()!=null){
                        Log.d(TAG, "onBindViewHolder:if "+myContactsArrayList.get(pos).getImage_path());




                    }else{
                        Log.d(TAG, "onBindViewHolder:e "+myContactsArrayList.get(pos).getImage_path());

                        openfireService.GetUserProfileImage(myContactsArrayList.get(pos).getJid());
                    }*/
                    Intent intent = new Intent(contactActivity,ProfileImageUpdateActivity.class);
                    intent.putExtra("userType",0);
                    intent.putExtra("image_path",myContactsArrayList.get(pos).getImage_path());
                    contactActivity.startActivity(intent);
                    break;

                case R.id.lay_main:
                    Log.d(TAG, "onClick:position "+pos);
                    Log.d(TAG, "onClick:position "+myContactsArrayList.get(pos).getJid());
                    Log.d(TAG, "onClick:position "+myContactsArrayList.get(pos).getUname());
                    Pref.saveSecondUserPreference(myContactsArrayList.get(pos).getJid() + Constant.DOMAIN);

                  /*  MMessageListener.getBuilder();
                    MMessageListener.getNotificationManager();*/

                  /*  ChatActivity.navigate(myContactsArrayList.get(pos).getJid(),myContactsArrayList.get(pos).getUname(),
                            pos,pos,myContactsArrayList.get(pos).getBlock_status(),
                            myContactsArrayList.get(pos).getMute_status(),
                            myContactsArrayList.get(pos).getImage_path(),
                            myContactsArrayList.get(pos).getStatus(),"1",contactActivity);*/

                    Intent mChatActivity = new Intent(contactActivity,ChatActivity.class);
                    mChatActivity.putExtra("TO_JID",myContactsArrayList.get(pos).getJid());
                    mChatActivity.putExtra("TO_JID_NAME",myContactsArrayList.get(pos).getUname());
                    mChatActivity.putExtra("fragment_position",pos);
                    mChatActivity.putExtra("item_position",pos);
                    mChatActivity.putExtra("block_status",myContactsArrayList.get(pos).getBlock_status());
                    mChatActivity.putExtra("mute_status",myContactsArrayList.get(pos).getMute_status());
                    mChatActivity.putExtra("image_path",myContactsArrayList.get(pos).getImage_path());
                    mChatActivity.putExtra("presence_status",myContactsArrayList.get(pos).getStatus());
                    //Navigation one(1) means navigate from ContactList Activity screen
                    mChatActivity.putExtra("NavigateFrom","1");

                    contactActivity.startActivity(mChatActivity);
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

        public SetProfileImageAsync(CircleImageView imageView, String TAG,int pos) {
            mImageView = imageView;
            this.TAG = TAG;
            this.pos = pos;
        }

        @Override
        protected String doInBackground(Integer... params) {

            Log.d(TAG, "doInBackground: ");

            VCardManager vCardManager = VCardManager.getInstanceFor(MyXMPP.getConnection());
            boolean isSupported = false;
            try {
                Log.d(TAG, "onBindViewHolder: username " + TAG+ Constant.DOMAIN);
                isSupported = vCardManager.isSupported(TAG + Constant.DOMAIN);
                if (isSupported) {
                    Log.d(TAG, "onBindViewHolder: username " + TAG);
                    VCard vCard = vCardManager.loadVCard(TAG + Constant.DOMAIN);
                    myContactsArrayList.get(pos).setImage_path(vCard.getEmailHome());

                    //get the image path from EMAILHOME here
                    return  vCard.getEmailHome();
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

                Picasso.with(contactActivity)
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
