package com.mindnotix.mnxchats.model;

/**
 * Created by Sridharan on 12/1/2017.
 */


import java.io.Serializable;

public class UserProfileDetails implements Serializable {

    private String UserName;
    private String Email;
    private String Registered;
    private String Name;
    private String FirstName;
    private String LastName;
    private String City;
    private String State;
    private String Zip;
    private String Phone;
    private String Url;
    private String Date;
    private String Misc;
    private String Text;

    private String Gender;
    private String Latitude;
    private String Longitude;
    private String GPSAddress;

    private String IPAddress;
    private String ProfileImage;
    private byte[] ProfileImageByte;

    private String Location;

    private String ProfileLock;

    public String getGCMToken() {
        return GCMToken;
    }

    public void setGCMToken(String GCMToken) {
        this.GCMToken = GCMToken;
    }

    private String GCMToken;

    public String getProfileLock() {
        return ProfileLock;
    }

    public void setProfileLock(String profileLock) {
        ProfileLock = profileLock;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public byte[] getProfileImageByte() {
        return ProfileImageByte;
    }

    public void setProfileImageByte(byte[] profileImageByte) {
        ProfileImageByte = profileImageByte;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getGPSAddress() {
        return GPSAddress;
    }

    public void setGPSAddress(String gPSAddress) {
        GPSAddress = gPSAddress;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String iPAddress) {
        IPAddress = iPAddress;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return "igaptech1234$$";
    }

    public String getRegistered() {
        return Registered;
    }

    public void setRegistered(String registered) {
        Registered = registered;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String zip) {
        Zip = zip;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMisc() {
        return Misc;
    }

    public void setMisc(String misc) {
        Misc = misc;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
