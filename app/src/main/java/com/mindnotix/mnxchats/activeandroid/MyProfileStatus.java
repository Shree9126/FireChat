package com.mindnotix.mnxchats.activeandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Sridharan on 11/27/2017.
 */

@Table(name = "MyProfileStatus")
public class MyProfileStatus extends Model {

    @Column(name = "Jid")
    public String jid;
    @Column(name = "status")
    public String status;

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
