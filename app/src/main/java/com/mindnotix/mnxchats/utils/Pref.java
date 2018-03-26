package com.mindnotix.mnxchats.utils;

import android.content.SharedPreferences;

import com.mindnotix.mnxchats.application.MyApplication;


public class Pref {

    public static final String PRE_ACCOUNT_ID = "account_id";
    public static final String PRE_TOKEN = "token";
    public static final String CLIENT_ACCOUNT_UNAME = "CLIENT_ACCOUNT_UNAME";
    public static final String CLIENT_ACCOUNT_NAME = "CLIENT_ACCOUNT_NAME";
    public static final String CLIENT_ACCOUNT_UNAME_PROFILE_IMG = "CLIENT_ACCOUNT_UNAME_PROFILE_IMG";
    public static final String CLIENT_ACCOUNT_PASS = "CLIENT_ACCOUNT_PASS";
    public static final String CLIENT_USER_TWO = "CLIENT_USER_TWO";
    public static final String CLIENT_ACCOUNT_STATUS = "CLIENT_ACCOUNT_STATUS";
    public static final String CHAT_ONE_TO_ONE_CLEAR = "Clear";
    public static final String CHAT_ONE_TO_ONE_BLOCK = "Block";
    public static final String CHAT_ONE_TO_ONE_MUTE = "Mute";
    public static final String CHAT_ACTIVITY_WALLPAPER = "";
    public static final String WALLPAPER = "Wallpaper";
    public static final String DEFAULT_WALLPAPER = "Default Wallpaper";
    public static int APP_FIRST_BOOT = 0;




    static SharedPreferences preferences = MyApplication.sharedpreferences;



    public static void saveChaWallPaper(String wallpaper) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(WALLPAPER, wallpaper);

        editor.commit();
    }

    public static String getChatMenudefaultWallPaper() {
        return preferences.getString(DEFAULT_WALLPAPER, "Default Wallpaper");
    }


    public static String getChatMenuWallPaper() {
        return preferences.getString(WALLPAPER, "Wallpaper");
    }

    public static String getChatScreenWallPaper() {
        return preferences.getString(CHAT_ACTIVITY_WALLPAPER, "");
    }


    public static void setChatScreenWallPaper(String mKey) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CHAT_ACTIVITY_WALLPAPER, mKey);
        editor.commit();
    }


    public static int getAPP_FIRST_BOOT() {
        return APP_FIRST_BOOT;
    }

    public static void setAPP_FIRST_BOOT(int APP_FIRST_BOOTX) {
        APP_FIRST_BOOT = APP_FIRST_BOOTX;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("APP_FIRST_BOOT", APP_FIRST_BOOTX);

        editor.commit();
    }

    public static int getAccountId() {
        return preferences.getInt(PRE_ACCOUNT_ID, 0);
    }

    public static void setAccountId(int account_id) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CLIENT_ACCOUNT_UNAME, account_id);

        editor.commit();
    }

    public static void setdevicetoken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PRE_TOKEN, token);
        editor.commit();
    }

    public static String getPreToken() {
        return preferences.getString(PRE_TOKEN, null);
    }

    public static String getProfileImg() {
        return preferences.getString(CLIENT_ACCOUNT_UNAME_PROFILE_IMG, null);
    }

    public static void setProfileImg(String imgpath) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CLIENT_ACCOUNT_UNAME_PROFILE_IMG, imgpath);
        editor.commit();
    }

    public static void savePreference(String mKey, String mValue) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CLIENT_ACCOUNT_UNAME, mKey);
        editor.putString(CLIENT_ACCOUNT_PASS, mValue);

        editor.commit();
    }


    public static void savePreferenceUserProfileName(String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CLIENT_ACCOUNT_NAME, name);
        editor.commit();
    }


    public static void saveChatMenu(String clear, String mute, String block) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CHAT_ONE_TO_ONE_CLEAR, clear);
        editor.putString(CHAT_ONE_TO_ONE_MUTE, mute);
        editor.putString(CHAT_ONE_TO_ONE_BLOCK, block);

        editor.commit();
    }


    public static void saveChatBlock(String block) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CHAT_ONE_TO_ONE_BLOCK, block);

        editor.commit();
    }


    public static void saveChatMute(String mute) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CHAT_ONE_TO_ONE_MUTE, mute);

        editor.commit();
    }

    public static String getChatMenuClear() {
        return preferences.getString(CHAT_ONE_TO_ONE_CLEAR, "Clear");
    }

    public static String getChatMenuBlock() {
        return preferences.getString(CHAT_ONE_TO_ONE_BLOCK, "Block");
    }

    public static String getChatMenuMute() {
        return preferences.getString(CHAT_ONE_TO_ONE_MUTE, "Mute");
    }


    public static void saveSecondUserPreference(String mKey) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CLIENT_USER_TWO, mKey);
        editor.commit();
    }

    public static void saveSecondUser_profileName(String mKey) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CLIENT_USER_TWO, mKey);
        editor.commit();
    }

    public static void saveSecondUserPresenceStatus(String mKey) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CLIENT_ACCOUNT_STATUS, mKey);
        editor.commit();
    }


    public static String getPreferenceStatus() {
        return preferences.getString(CLIENT_ACCOUNT_STATUS, null);

    }

    public static String getPreferenceUser() {
        return preferences.getString(CLIENT_ACCOUNT_UNAME, null);

    }

    public static String getPreferenceUserProfileName() {
        return preferences.getString(CLIENT_ACCOUNT_NAME, null);

    }

    public static String getPreferenceSecondUser() {
        return preferences.getString(CLIENT_USER_TWO, null);

    }

    public static String getPreferencePass() {
        return preferences.getString(CLIENT_ACCOUNT_PASS, null);

    }

    public static void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }


}
