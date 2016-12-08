package com.harshal.roomiesexpenses.entities;

/**
 * Created by Harshal on 1/11/2015.
 */
public class Month {
    private int mId;
    private String mName, mCreatedDate;

    public Month(int mId, String mName, String mCreatedDate) {
        this.mId = mId;
        this.mName = mName;
        this.mCreatedDate = mCreatedDate;
    }

    public Month() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmCreatedDate() {
        return mCreatedDate;
    }

    public void setmCreatedDate(String mCreatedDate) {
        this.mCreatedDate = mCreatedDate;
    }

    @Override
    public String toString() {
        return mName + "  ->  " + mCreatedDate;
    }
}
