package com.mindnotix.mnxchats.model;

/**
 * Created by Sridharan on 11/23/2017.
 */

public class RegisterRequest {

    String username;
    String name;
    String email;
    String password;

  /*  public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }*/

    public RegisterRequest(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
