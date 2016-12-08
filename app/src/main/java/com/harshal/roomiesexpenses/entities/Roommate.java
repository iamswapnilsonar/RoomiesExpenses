package com.harshal.roomiesexpenses.entities;

import android.graphics.Bitmap;

/**
 * Created by Harshal on 1/9/2015.
 */
public class Roommate {
    private int id;
    private String name, emailId, mobileNo;
    private Bitmap picture;
    private boolean isSharing, isActive = true;

    public Roommate(int id, String name, String emailId, String mobileNo, Bitmap picture, boolean isActive) {
        this.id = id;
        this.name = name;
        this.emailId = emailId;
        this.mobileNo = mobileNo;
        this.picture = picture;
        this.isActive = isActive;
    }

    public Roommate() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public boolean isSharing() {
        return isSharing;
    }

    public void setSharing(boolean isSharing) {
        this.isSharing = isSharing;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
