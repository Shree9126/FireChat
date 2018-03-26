package com.mindnotix.mnxchats.model;

/**
 * Created by Admin on 6/30/2017.
 */


import android.app.Activity;
import android.os.Handler;
import android.os.Looper;


import com.mindnotix.mnxchats.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by Admin on 1/13/2017.
 */

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mPath;
    private UploadCallbacks mListener;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    Activity mactivity;



    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
        void onError();
        void onFinish();
    }

    public ProgressRequestBody(final File file, final  UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
    }
    public ProgressRequestBody(final File file, final  UploadCallbacks listener, Activity activity) {
        mFile = file;
        mListener = listener;
        mactivity =activity;
    }
    @Override
    public MediaType contentType() {
        return MediaType.parse("*/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                // update progress on UI thread
                handler.post(new ProgressUpdater(uploaded, fileLength));

                uploaded += read;
                sink.write(buffer, 0, read);
            }
        }
        catch (OutOfMemoryError outOfMemoryError){
            Utils.showDialogDetails("Upload note:","Based on mobile performance the video/audio file time length and file size(ex:20Mb) will be accepted while uploading. Kindly reduce the time length or compress the file size and re-upload it, if your file not get uploaded instantly.",mactivity);
        }
        finally {
            in.close();
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));
        }
    }
}