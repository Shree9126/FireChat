package com.mindnotix.mnxchats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mindnotix.mnxchats.activeandroid.MyContacts;
import com.mindnotix.mnxchats.adapter.MyContactsAdapter;
import com.mindnotix.mnxchats.adapter.Pager;
import com.mindnotix.mnxchats.adapter.ViewPagerAdapter;
import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.eventbus.Events;
import com.mindnotix.mnxchats.eventbus.GlobalBus;
import com.mindnotix.mnxchats.fragment.ContactFragment;
import com.mindnotix.mnxchats.fragment.ConversationFragment;
import com.mindnotix.mnxchats.model.BasicResponse;
import com.mindnotix.mnxchats.model.User;
import com.mindnotix.mnxchats.network.ApiClient;
import com.mindnotix.mnxchats.network.ChatMindAPI;
import com.mindnotix.mnxchats.openfire.OpenfireService;
import com.mindnotix.mnxchats.utils.Constant;
import com.mindnotix.mnxchats.utils.Pref;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 11/23/2017.
 */

public class MainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    private static final String TAG = "MainActivity";
    ViewPager viewpager;
    TabLayout tabs;
    Toolbar toolbar;
    //this is our pager adapter
    Pager adapter;
    FloatingActionButton fab;
    private ViewPagerAdapter viewPagerAdapter;
    static MainActivity mainActivity;
    ConversationFragment chatConversationFragment;
    public  static synchronized MainActivity getInstance() {
        return mainActivity;
    }


    public ConversationFragment getChatConversationFragment() {
        return chatConversationFragment;
    }

    public void setChatConversationFragment(ConversationFragment chatConversationFragment) {
        this.chatConversationFragment = chatConversationFragment;
    }

    public void triggerCrap() {
        Log.d("TAG", "triggerCrap: ");
        setChatConversationFragment(chatConversationFragment);
        if (getChatConversationFragment() != null) {
            getChatConversationFragment().onRefresh();
            Log.d("TAG", "getChatConversationFragment is not null");
        } else {
            Log.d("TAG", "getChatConversationFragment is null");
        }
    }

    public  void setPage(int fragment_position, int item_position,String jid,String lastmessage) {

        Log.v("fragment_position_3", "" + fragment_position);
        Log.v("fragment_item position ", "" + item_position);
        Log.v("fragment_position_JID", "" + jid);
        Log.v("fragment_position_LM", "" + lastmessage);

        Log.d("TAG", "setPage: ");
        if (viewpager != null) {
            try {
                viewpager.setCurrentItem(fragment_position);
                if(fragment_position == 0){
                    Events.ConversationFragementRefresh conversationFragementRefresh =
                            new Events.ConversationFragementRefresh(fragment_position,item_position,jid,lastmessage);

                    GlobalBus.getBus().postSticky(conversationFragementRefresh);
                }

            } catch (Exception e) {
                Log.e(TAG, "setPage: " + e.getLocalizedMessage());
            }
        }

    }
    public void setPages(int fragment_position) {

        Log.v("fragment_position_3", "" + fragment_position);

        Log.d("TAG", "setPage: ");
        if (viewpager != null) {
            try {
                viewpager.setCurrentItem(fragment_position);
            } catch (Exception e) {
                Log.e(TAG, "setPage: " + e.getLocalizedMessage());
            }
        }

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        UiInitialization();
        Bundle b = getIntent().getExtras();

        if (getIntent() != null) {
            setPages(getIntent().getIntExtra("frag_pos",0));
        }




        //Creating our pager adapter
        adapter = new Pager(getSupportFragmentManager(), tabs.getTabCount());


        //Adding adapter to pager
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        ConversationFragment chatConversationFragment = new ConversationFragment();
        ContactFragment contactFragments = new ContactFragment();
        viewPagerAdapter.addFrag(chatConversationFragment, "Chats");
    //    viewPagerAdapter.addFrag(contactFragments, "Contacts");
        viewpager.setAdapter(viewPagerAdapter);
        tabs.setupWithViewPager(viewpager);

        viewpager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {


                        //  ChatConversationFragment.getInstance().fragrefresh();
                        //   Toast.makeText(MainActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                        if (position == 0) {
                            ConversationFragment fragment = (ConversationFragment) viewPagerAdapter.instantiateItem(viewpager, 0);
                            if (fragment != null) {
                                    fragment.refreshList();

                            }
                        }
                        /* else if (position == 1) {
                            ContactFragment fragment = (ContactFragment) viewPagerAdapter.instantiateItem(viewpager, 1);
                            if (fragment != null) {
                                // fragment.refreshList();
                            }
                        }*/
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }


                }
        );
        //Adding onTabSelectedListener to swipe views
        tabs.addOnTabSelectedListener(this);

        //Change the FAB ICON based on pages
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(final int position) {
                // Check if this is the page you want.
                Log.d(TAG, "onPageSelected: " + position);

                if (position == 0) {

                    fab.setImageResource(R.drawable.ic_message_black_24dp);


                } else {
                    fab.setImageResource(R.drawable.ic_add_black_24dp);


                }
            }
        });

        //FAB click event based on page position
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int pos = tabs.getSelectedTabPosition();

                switch (pos) {
                    case 0:
                        Intent mContactActivity = new Intent(MainActivity.this,ContactActivity.class);
                        startActivity(mContactActivity);
                        Toast.makeText(MainActivity.this, "" + pos, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "" + pos, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }


    private void UiInitialization() {

        //FAB initialize
        fab = (FloatingActionButton)findViewById(R.id.fab);
        //Initialize ViewPager
        viewpager = (ViewPager)findViewById(R.id.viewpager);

        //Initialize TAB layout
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initialize TOOLBAR
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);




    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        Log.d(TAG, "onTabSelected: position " + tab.getPosition());
        viewpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.status_content:
                // setStatusContents();
                OpenfireService.setStatusContents(MainActivity.this);

                break;

            case R.id.profile:

                    Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                    startActivity(intent);

                break;
            default:
                onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }
}
