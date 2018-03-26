package com.mindnotix.mnxchats.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mindnotix.mnxchats.application.MyApplication;
import com.mindnotix.mnxchats.connection.MyXMPP;

import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sridharan on 11/10/15.
 */
public class Utils {
    private static final String TAG ="UTILS" ;
    static boolean isOnline = false;
    public static String aaaa= null;


    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static boolean isOnline() {

        try {

            ConnectivityManager cm =
                    (ConnectivityManager) MyApplication.
                            getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();


        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }

    }

    public static Bitmap decodeScaledBitmapFromSdCard(String filePath,
                                                      int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }







    static final String serverDateFormat = "yyyy-MM-dd HH:mm:ss";

    public static final String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    static int progressBarStatus = 0;
    static long fileSize = 0;

    public static final Log Debug(String msg) {
        Log.e("MNXCHAT", msg);
        return null;

    }

    public static boolean isValidWord(String w) {
        return w.matches("[A-Za-z][0-9]");
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public static void requestPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }

    public static final boolean hasText(EditText edt) {
        String hasSomeText = edt.getText().toString();
        int mLength = hasSomeText.length();
        if (mLength > 0) {
            return true;
        }
        return false;
    }

    public static final boolean isValidPassword(EditText edt) {

        String mPassword = edt.getText().toString();
        int mPassLength = mPassword.length();
        if (mPassLength > 6 && mPassLength < 20) {
            return true;
        }
        return false;
    }

    public static final boolean isEmailValid(String email) {

        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

    }

    public static final void ShowToast(Context context, String msg) {
        if (!msg.isEmpty())
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static final boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static final void debug(String s) {
        Log.e("HomeStar", "Message : " + s);
    }

    public static final int getWidth(Activity mContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static boolean checkNullValues(String valueToCheck) {
        if (!(valueToCheck == null)) {
            String valueCheck = valueToCheck.trim();
            if (valueCheck.equals("") || valueCheck.equals("0")) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static final int getHeight(Activity mContext) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        return height;
    }



    public static final void showDialogDetails(String mTitle, String mMessage, final Context mContext) {
        LayoutInflater inflater;
        AlertDialog alertDialog = new AlertDialog.Builder(
                mContext).create();

        // Setting Dialog Title
        alertDialog.setTitle(mTitle);

        // Setting Dialog Message
        alertDialog.setMessage(mMessage);

        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.kicon);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                //  Toast.makeText(mContext, "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public static final boolean hasText(TextView edt) {
        String hasSomeText = edt.getText().toString();
        int mLength = hasSomeText.length();
        if (mLength > 0) {
            return true;
        }
        return false;
    }

    public static ProgressDialog createProgressbar(Context context) {
        final ProgressDialog progressBar;

        final Handler progressBarHandler = new Handler();

        // creating progress bar dialog
        progressBar = new ProgressDialog(context);
        progressBar.setCancelable(true);
        progressBar.setMessage("File uploading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        //reset progress bar and filesize status
        progressBarStatus = 0;
        fileSize = 0;


        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {
                    // performing operation
                    progressBarStatus = doOperation();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Updating the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }
                // performing operation if file is downloaded,
                if (progressBarStatus >= 100) {
                    // sleeping for 1 second after operation completed
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // close the progress bar dialog
                    progressBar.dismiss();
                }
            }
        }).start();


        return progressBar;
    }


    public static int doOperation() {
        //The range of ProgressDialog starts from 0 to 10000
        while (fileSize <= 10000) {
            fileSize++;
            if (fileSize == 1000) {
                return 10;
            } else if (fileSize == 2000) {
                return 20;
            } else if (fileSize == 3000) {
                return 30;
            } else if (fileSize == 4000) {
                return 40;//you can add more else if
            } else {
                return 100;
            }
        }//end of while
        return 100;
    }//end





    public final static boolean isEmailValid(EditText edtEmail) {
        String email = edtEmail.getText().toString();
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public final static String toUpperCaseFirstChar(final String target) {

        if ((target == null) || (target.length() == 0)) {
            return target;
        }
        return Character.toUpperCase(target.charAt(0))
                + (target.length() > 1 ? target.substring(1) : "");
    }

    public final static boolean isPhoneNumberValid(EditText e) {
        int count = e.getText().toString().length();
        return count >= 10 ? true : false;
    }

    public final static boolean isValidZipCode(EditText e) {
        int count = e.getText().toString().length();
        return count == 5 ? true : false;
    }

    public static final void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            // Ignore exceptions if any
            Log.e("KeyBoardUtil", e.toString(), e);
        }
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {

            return true;
        }

        return false;
    }


    /*public void getLastSeen(){
        try {
            LastActivity activity = LastActivity.getStatusMessage();
            Log.d("LAST ACTIVITY", activity.lastActivity+"");
        } catch (XMPPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }*/















    public static void pressLogout(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Logout");

        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //	logout(context);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static String getNetworkClass(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnected())
            return "-"; //not connected
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return "WIFI";
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                    return "4G";
                default:
                    return "?";
            }
        }
        return "?";
    }





    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static boolean isValidMobile(String phone) {
        String regexStr = "^[7-9][0-9]{9}$";
        //return phone.length() == 10 && android.util.Patterns.PHONE.matcher(phone).matches();
            return  phone.matches(regexStr);
    }

    public static boolean isValidMail(String email) {
        return (email.length() > 3 || email.length() < 250) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static void logError(Throwable t) {

        Log.e("Retrofit Failure", t.getMessage(), t);

    }


    public static String getTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        String stringTime = sdf.format(date);
        stringTime = stringTime.replace(".m.", "m");
        return stringTime;
    }


    public static String getDateString(String strDate) {
        Date date = stringToDate(strDate);
        return getDateString(date);

    }

    public static String getDateString_(String strDate) {
        Date date = stringToDate(strDate);
        return getDateString_(date);
    }

    public static String getDateString(Date date) {
        String stringDate;
        if (Utils.isToday(date)) {
            stringDate = "Today";
        } else if (Utils.isYesterday(date)) {
            stringDate = "Yesterday";
        } else {
            stringDate = String.valueOf(date.getDate() + " " + months[date.getMonth()] + " " + (date.getYear() + 1900));
        }
        return stringDate;
    }

    public static String getDateString_(Date date) {
        return String.valueOf(date.getDate() + "-" + months[date.getMonth()] + "-" + (date.getYear() + 1900));
    }

    public static Date stringToDate(String time) {

        // this is for d
        if (time.length() == 10) time = time + " 00:00:00";

        SimpleDateFormat formatter = new SimpleDateFormat(serverDateFormat);
        Date createDate = null;
        try {
            createDate = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return createDate;
    }


    public static boolean isToday(Date date) {

        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, 0); // today

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date); // your date

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isYesterday(Date date) {

        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date); // your date

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static String getTimeString(String strDate) {
        Date date = stringToDate(strDate);
        return getTimeString(date);
    }

    public static String getDateTimeString(String news_datetime) {
        return getDateString(news_datetime) + " " + getTimeString(news_datetime);
    }




    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;


        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public static Bitmap StringToBitMap(String encodedString){
        try{
            Log.d(TAG, "StringToBitMap: "+encodedString);
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static void changeProfile(String path) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            Log.d(TAG, "changeProfile: "+Constant.IMG_PATH+path);


            URL myURL = new URL(Constant.IMG_PATH+path);

            VCardManager vCardManager = VCardManager.getInstanceFor(MyXMPP.getConnection());
            VCard vCard ;
            vCard = vCardManager.loadVCard();
            vCard.setNickName(Pref.getPreferenceUser()+Constant.DOMAIN);
            URL urldefault = new URL(Constant.IMG_PATH+path);
            //Store image URL here instead of EMAILHOME
            vCard.setEmailHome(Constant.IMG_PATH+path);

            /*
            InputStream stream = urldefault.openStream();
            byte[] avatar1 = readBytes(stream);
            vCard.setAvatar(avatar1, "avatar1/jpg");
            */

        //    vCard.setAvatar(urldefault);//Image Path should be URL or Can be Byte Array etc.
            vCard.setPhoneHome("mobile", Pref.getPreferenceUser());
            vCardManager.saveVCard(vCard);


        } catch (Exception e) {
            e.printStackTrace();
            //FileLog.e("Yahala create Account", e.toString());
            Log.e("Yahala create Account", e.toString());

        }
    }


    public void changeProfilewithBitmap(String path) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            Log.d(TAG, "changeProfile: "+Constant.IMG_PATH+path);

            VCardManager vCardManager = VCardManager.getInstanceFor(MyXMPP.getConnection());
            VCard vCard;
            vCard = vCardManager.loadVCard();
            vCard.setNickName(Pref.getPreferenceUser()+Constant.DOMAIN);
            URL urldefault = new URL(Constant.IMG_PATH+path);
            InputStream stream = urldefault.openStream();
            byte[] avatar1 = readBytes(stream);
            vCard.setAvatar(avatar1, "avatar1/jpg");
            vCard.setPhoneHome("mobile", Pref.getPreferenceUser());
            vCardManager.saveVCard(vCard);
            Pref.setProfileImg(Constant.IMG_PATH+path);


        } catch (Exception e) {
            e.printStackTrace();
            //FileLog.e("Yahala create Account", e.toString());
            Log.e("Yahala create Account", e.toString());

        }
    }
    private static byte[] readBytes(InputStream stream) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

}
