package com.mindnotix.mnxchats.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 6/30/2017.
 */

public class UploadImage implements Serializable {


    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;

    @SerializedName("photoname")
    private String photoname;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhotoname() {
        return photoname;
    }

    public void setPhotoname(String photoname) {
        this.photoname = photoname;
    }
}
