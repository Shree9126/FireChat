package com.mindnotix.mnxchats.activeandroid;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Sridharan  on 11/30/2017.
 */

@Table(name = "ChatMessages")
public class ChatMessages extends Model {


    @Column(name = "Jid")
    public String jid;

    @Column(name = "MyJid")
    public String MyJid;


    @Column(name = "uname")
    public String uname;

    @Column(name = "MsgID")
    public String MsgID;

    @Column(name = "body")
    private String body;

    @Column(name = "receiptID")
    private String receiptID;

    @Column(name = "isUser")
    private String isUser;

    @Column(name = "Type")
    private String type;


    @Column(name = "Date")
    public String Date;

    @Column(name = "Time")
    public String Time;

    //message deliver or not status status-1 deliver, status -2 pending
    @Column(name = "deliver_status")
    public String status;

    @Column(name = "deliveryReceiptId")
    public String deliveryReceiptId;

    @Column(name = "longitude")
    public String longitude;

    @Column(name = "latitude")
    public String latitude;

    @Column(name = "address")
    public String address;

    @Column(name = "media_url")
    public String media_url;

    public ChatMessages(String body, String isUser, String type, String date, String time, String status, String msgID,String media_url) {
        this.body = body;
        this.isUser = isUser;
        this.type = type;
        Date = date;
        Time = time;
        this.status = status;
        this.MsgID = msgID;
        this.media_url =media_url;

    }

    public ChatMessages(String body, String isUser, String type, String date, String time, String status, String msgID) {
        this.body = body;
        this.isUser = isUser;
        this.type = type;
        Date = date;
        Time = time;
        this.status = status;
        this.MsgID = msgID;

    }

    public ChatMessages() {

    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getMyJid() {
        return MyJid;
    }

    public void setMyJid(String myJid) {
        MyJid = myJid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getMsgID() {
        return MsgID;
    }

    public void setMsgID(String msgID) {
        MsgID = msgID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReceiptID() {
        return receiptID;
    }

    public void setReceiptID(String receiptID) {
        this.receiptID = receiptID;
    }

    public String getIsUser() {
        return isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryReceiptId() {
        return deliveryReceiptId;
    }

    public void setDeliveryReceiptId(String deliveryReceiptId) {
        this.deliveryReceiptId = deliveryReceiptId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }
}


