package com.mindnotix.mnxchats.activeandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Sridharan on 11/24/2017.
 */
@Table(name = "MyContacts")
public class MyContacts extends Model {

    @Column(name = "Jid")
    public String jid;

    @Column(name = "nickname")
    public String nickname;

    @Column(name = "uname")
    public String uname;

    @Column(name = "email")
    public String email;

    @Column(name = "status")
    public String status;

    @Column(name = "image_path")
    public String image_path;

    @Column(name = "Type")
    public String Type;

    @Column(name = "un_read")
    public String un_read;

    @Column(name = "message")
    public String message;

    @Column(name = "user_statusMSG")
    public String user_statusMSG;

    @Column(name = "blockStatus")
    public String block_status;

    @Column(name = "roomLeaveStatus")
    public String roomLeaveStatus;

    @Column(name = "mute_status")
    public String mute_status;

    @Column(name = "lastMessage")
    private String lastMessage;

    //if conversation_status =0 conversation not start
    //if conversation_status =1 conversation  start
    @Column(name = "conversation_status")
    private String conversation_status;

    @Column(name = "contact_status")
    private String contact_status;


    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getUn_read() {
        return un_read;
    }

    public void setUn_read(String un_read) {
        this.un_read = un_read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_statusMSG() {
        return user_statusMSG;
    }

    public void setUser_statusMSG(String user_statusMSG) {
        this.user_statusMSG = user_statusMSG;
    }

    public String getBlock_status() {
        return block_status;
    }

    public void setBlock_status(String block_status) {
        this.block_status = block_status;
    }

    public String getRoomLeaveStatus() {
        return roomLeaveStatus;
    }

    public void setRoomLeaveStatus(String roomLeaveStatus) {
        this.roomLeaveStatus = roomLeaveStatus;
    }

    public String getMute_status() {
        return mute_status;
    }

    public void setMute_status(String mute_status) {
        this.mute_status = mute_status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getConversation_status() {
        return conversation_status;
    }

    public void setConversation_status(String conversation_status) {
        this.conversation_status = conversation_status;
    }

    public String getContact_status() {
        return contact_status;
    }

    public void setContact_status(String contact_status) {
        this.contact_status = contact_status;
    }
}
