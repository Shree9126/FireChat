package com.mindnotix.mnxchats.network;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



/**
 * Created by Admin on 5/1/2017.
 */

public class ApiClient {


    private static final String TAG = "ApiClient";
    private static Retrofit retrofit;

    public static Retrofit getClient(String BASE_URL) {
        if (retrofit == null) {

            Log.d(TAG, "getClient: "+BASE_URL);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            // add your other interceptor

            // add logging as last interceptor
            httpClient.addInterceptor(logging);  // <-- this is the important line!

                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)

                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build();

        }

        return retrofit;

    }
}
