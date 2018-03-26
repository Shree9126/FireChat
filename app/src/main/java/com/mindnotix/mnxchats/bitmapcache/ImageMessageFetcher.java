package com.mindnotix.mnxchats.bitmapcache;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.mindnotix.mnxchats.utils.Utils;


import java.lang.ref.WeakReference;

/**
 * Created by dilli on 2/1/2016.
 */
public class ImageMessageFetcher {

    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 15; // 15MB
    private static final String TAG ="ImageMessageFetcher" ;

    private Context context;

    private LruCache<String, BitmapDrawable> memoryCache;

    public ImageMessageFetcher(Context context) {
        this.context = context;

        memoryCache = new LruCache<String, BitmapDrawable>(DEFAULT_MEM_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                Log.d(TAG, "sizeOf: image_fetcher value "+value);
                Log.d(TAG, "sizeOf: image_fetcher getBitmapSize "+getBitmapSize(value));
                final int bitmapSize = getBitmapSize(value) / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {}
        };
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();

        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        try{

            if (Utils.hasKitKat()) {
                Log.d(TAG, "getBitmapSize: "+bitmap.getAllocationByteCount());
                return bitmap.getAllocationByteCount();
            }

            if (Utils.hasHoneycombMR1()) {
                return bitmap.getByteCount();
            }


        }catch (Exception e){

            e.printStackTrace();
        }


        try{

            Log.d(TAG, "getBitmapSize: getWidth "+bitmap.getWidth());
            Log.d(TAG, "getBitmapSize: getHeight "+bitmap.getHeight());
        }catch (Exception e){
            e.printStackTrace();
        }

        // Pre HC-MR1
       // Log.d(TAG, "getBitmapSize return : "+bitmap.getWidth() * bitmap.getHeight());
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public void addBitmapToCache(String data, BitmapDrawable value) {
        if (data == null || value == null) {
            return;
        }

        if (memoryCache != null) {
            memoryCache.put(data, value);
        }
    }

    public void loadImage(String filePath, ImageView imageView) {
        Log.d(TAG, "loadImage: file path "+filePath);
        BitmapDrawable drawableInMemory = memoryCache.get(filePath);
        if(drawableInMemory == null)
        Log.d(TAG, "loadImage: drawable is null.");
        if (drawableInMemory != null) {
            imageView.setImageDrawable(drawableInMemory);
        } else if (cancelPotentialWork(filePath, imageView)) {
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(filePath, imageView);
            AsyncDrawable drawable = new AsyncDrawable(context.getResources(), null, bitmapWorkerTask);
            imageView.setImageDrawable(drawable);
            bitmapWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static boolean cancelPotentialWork(String filePath, ImageView imageView) {
        BitmapWorkerTask task = getBitmapWorkerTask(imageView);
        if (task != null) {
            String taskFilePath = task.filePath;
            if (taskFilePath == null || !taskFilePath.equals(filePath)) {
                task.cancel(true);
            } else {
                return false;
            }
        }

        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                return ((AsyncDrawable)drawable).getBitmapWorkerTask();
            }

        }
        return null;
    }

    public class BitmapWorkerTask extends AsyncTask<String, Void, Drawable> {
        WeakReference<ImageView> imageViewReference;
        String filePath;

        public BitmapWorkerTask(String filePath, ImageView imageView) {
            this.filePath = filePath;
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Drawable doInBackground(String... params) {
            BitmapDrawable drawable = null;
            if (!isCancelled() && getAttachedImageView() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                drawable = new BitmapDrawable(context.getResources(), bitmap);
            }

            addBitmapToCache(filePath, drawable);

            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (isCancelled()) {
                drawable = null;
            }

            ImageView imageView = getAttachedImageView();
            if (imageView != null && drawable != null) {
                imageView.setImageDrawable(drawable);
            }
        }

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}