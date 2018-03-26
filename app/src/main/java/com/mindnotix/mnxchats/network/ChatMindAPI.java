package com.mindnotix.mnxchats.network;


import com.mindnotix.mnxchats.model.BasicResponse;
import com.mindnotix.mnxchats.model.RegisterRequest;
import com.mindnotix.mnxchats.model.UploadImage;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;


/**
 * Created by Sridharan on 5/1/2017.
 */

public interface ChatMindAPI {

// server authorization = YWRtaW46Q2hBVCpNaU5EQDE3;

    @Headers({
            "Authorization: Basic YWRtaW46Q2hBVCpNaU5EQDE3",
            "Accept: application/json"
    })
    @GET("restapi/v1/users")
    Call<BasicResponse> getusers();

    @Headers({
            "Authorization: Basic YWRtaW46Q2hBVCpNaU5EQDE3",
            "Content-Type: application/json"
    })
    @POST("restapi/v1/users")
    Call<Void> postusers(@Body RegisterRequest body);

    // local authorization =YWRtaW46bWluZDEyM34=

   /* @Headers({
            "Authorization: Basic YWRtaW46bWluZDEyM34=",
            "Accept: application/json"
    })
    @GET("restapi/v1/users")
    Call<BasicResponse> getusers();

    @Headers({
            "Authorization: Basic YWRtaW46bWluZDEyM34=",
            "Content-Type: application/json"
    })
    @POST("restapi/v1/users")
    Call<Void> postusers(@Body RegisterRequest body);*/




    @GET("presence/status")
    Call<ResponseBody> getUserStatus(@QueryMap Map<String, String> options);

    @GET("presence/status")
    Call<ResponseBody> getOnOffStatus(@QueryMap Map<String, String> options);


    @Multipart
    @POST("updateProfileImage.php")
    Call<UploadImage> uploadChannelPost(@Part MultipartBody.Part file, @Part("file") RequestBody name);

    @Multipart
    @POST("updateMucProfileImage.php")
    Call<UploadImage> uploadMUCProfileImagePost(@Part MultipartBody.Part file, @Part("file") RequestBody name);


}
