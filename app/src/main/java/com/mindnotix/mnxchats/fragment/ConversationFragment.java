package com.mindnotix.mnxchats.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mindnotix.mnxchats.MainActivity;
import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.activeandroid.SqliteDatabaseManager;
import com.mindnotix.mnxchats.adapter.ChatConversationAdapter;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sridharan on 11/23/2017.
 */

public class ConversationFragment extends Fragment {
    private static final String TAG = "ConversationFragment";
    TextView txtEmpty;
    RecyclerView conversationList;
    ChatConversationAdapter adapter;
    ArrayList<MyContacts> myContactsArrayList = new ArrayList<>();


    public void onRefresh() {
        Log.d("TAG", "onRefresh: ");
        getConversationHistory();
        getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                }
        );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.conversation_fragment, container, false);
        UiInitialization(view);
        ((MainActivity)getActivity()).setChatConversationFragment(this);
        GlobalBus.getBus().register(this);
        getConversationHistory();
        adapter = new ChatConversationAdapter(getActivity(), myContactsArrayList);
        conversationList.setAdapter(adapter);
        return view;

    }

    public ChatConversationAdapter getAdapter() {

        getConversationHistory();
        if (adapter == null) {
            adapter = new ChatConversationAdapter(getActivity(), myContactsArrayList);
            conversationList.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        return adapter;
    }

    public void refreshList() {

        getConversationHistory();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GlobalBus.getBus().unregister(this);
    }


    private void getConversationHistory() {

        Log.d(TAG, "getConversationHistory: ");
        myContactsArrayList.clear();
        List<MyContacts> myContactsList = SqliteDatabaseManager.getALLConversation();
        myContactsArrayList.addAll(myContactsList);

        Log.d(TAG, "getConversationHistory: size " + myContactsArrayList.size());
        if (myContactsArrayList.size() != 0) {
            txtEmpty.setVisibility(View.INVISIBLE);
        } else {
            txtEmpty.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        getConversationHistory();
    }

    private void UiInitialization(View view) {

        txtEmpty = (TextView) view.findViewById(R.id.txtEmpty);
        conversationList =(RecyclerView) view.findViewById(R.id.userList);
        conversationList.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    //Event bus

    //Instant update un read message count

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ActivityActivityMessage activityActivityMessage) {

        Log.d(TAG, "getMessage:count " + activityActivityMessage.getCount());
        Log.d(TAG, "getMessage: " + activityActivityMessage.getMessage());
        Log.d(TAG, "getMessage:getFromname  " + activityActivityMessage.getFromname());


        if(myContactsArrayList.size() == 0){

            Log.d(TAG, "getMessage: first time refreshment");
            getConversationHistory();


            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    if(adapter !=null)
                        adapter.notifyDataSetChanged();

                }
            });

        }else{

            Log.d(TAG, "getMessage: second time update count");
            for (int i = 0; i < myContactsArrayList.size(); i++) {
                if (myContactsArrayList.get(i).getJid().equals(activityActivityMessage.getFromname())) {

                    MyContacts contactRequestTable = myContactsArrayList.get(i);
                    contactRequestTable.setUn_read(activityActivityMessage.getCount());
                    contactRequestTable.setLastMessage(activityActivityMessage.getMessage());
                    adapter.notifyItemChanged(i);
                }
            }

        }


    }

    //Event bus
    //Change profile picture instantly
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ActivityUserProfileChange activityUserProfileChange) {

        Log.d(TAG, "getMessage:getProfileImg " + activityUserProfileChange.getProfileImg());
        if (activityUserProfileChange.getProfileImg() != null) {


            for (int i = 0; i < myContactsArrayList.size(); i++) {
                if (myContactsArrayList.get(i).getJid().equals(activityUserProfileChange.getFromname())) {

                    MyContacts contactRequestTable = myContactsArrayList.get(i);
                    contactRequestTable.setImage_path(activityUserProfileChange.getProfileImg());


                    if(adapter != null)
                    adapter.notifyItemChanged(i);
                }
            }

        }



    }


    //Event bus
    //Refresh the adatper from main activity
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ConversationFragementRefresh conversationFragementRefresh) {

        Log.d(TAG, "getMessage:ConversationFragementRefresh " + conversationFragementRefresh.getFragmentpostion());
        Log.d(TAG, "getMessage:ConversationFragementRefresh " + conversationFragementRefresh.getLastMessage());
        if (conversationFragementRefresh.getFragmentpostion() == 0 && conversationFragementRefresh.getLastMessage() != null) {



            for (int i = 0; i < myContactsArrayList.size(); i++) {
                if (myContactsArrayList.get(i).getJid().equals(conversationFragementRefresh.getFromJID())) {

                    Log.d(TAG, "getMessage: t");
                    MyContacts contactRequestTable = myContactsArrayList.get(i);
                    contactRequestTable.setLastMessage(conversationFragementRefresh.getLastMessage());
                    if(adapter != null)
                    adapter.notifyItemChanged(i);
                }else{
                    Log.d(TAG, "getMessage: f");
                }
            }
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        });
    }



    //Event bus
    //Block/Unblock status append in array list
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ChatActivityBlockUnblockStatus chatActivityBlockUnblockStatus) {

        Log.d(TAG, "getMessage:chatActivityBlockUnblockStatus " + chatActivityBlockUnblockStatus.getBlock_status());
        Log.d(TAG, "getMessage:chatActivityBlockUnblockStatus " + chatActivityBlockUnblockStatus.getItem_position());
      if (chatActivityBlockUnblockStatus.getBlock_status() != null ) {

          try
          {


              myContactsArrayList.get(chatActivityBlockUnblockStatus.getItem_position()).setBlock_status(chatActivityBlockUnblockStatus.getBlock_status());
              if (adapter != null)
                  adapter.notifyDataSetChanged();
          }catch (Exception e){
              e.getMessage();
          }
        }

    }


    //Event bus
    //Block/Unblock status append in array list
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getMessage(Events.ChatActivityMuteImmuteStatus chatActivityMuteImmuteStatus) {

        Log.d(TAG, "getMessage:chatActivityMuteImmuteStatus " + chatActivityMuteImmuteStatus.getMute_status());
        Log.d(TAG, "getMessage:chatActivityMuteImmuteStatus " + chatActivityMuteImmuteStatus.getItem_position());
        if (chatActivityMuteImmuteStatus.getMute_status() != null ) {

            myContactsArrayList.get(chatActivityMuteImmuteStatus.getItem_position()).setBlock_status(chatActivityMuteImmuteStatus.getMute_status());
            if (adapter != null)
            adapter.notifyDataSetChanged();
        }

    }

}

