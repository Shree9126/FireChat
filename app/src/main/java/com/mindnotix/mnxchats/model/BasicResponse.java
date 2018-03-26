package com.mindnotix.mnxchats.model;

import java.util.ArrayList;

/**
 * Created by Sridharan on 11/24/2017.
 */

public class BasicResponse  {


    public ArrayList<User> getUser() {
        return user;
    }

    public void setUser(ArrayList<User> user) {
        this.user = user;
    }

    private ArrayList<User> user;




}
