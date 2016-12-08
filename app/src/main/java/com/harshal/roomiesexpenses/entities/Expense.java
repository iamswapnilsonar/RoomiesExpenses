package com.harshal.roomiesexpenses.entities;

/**
 * Created by Harshal on 1/11/2015.
 */
public class Expense {
    private int eId;
    private int eMonthId;
    private String eName;
    private double eAmount;
    private int ePaidBy;
    private String eSharedMembers;
    private String eCreatedDate;

    public Expense(int eId, int eMonthId, String eName, double eAmount, int ePaidBy, String eSharedMembers, String eCreatedDate) {
        this.eId = eId;
        this.eMonthId = eMonthId;
        this.eName = eName;
        this.eAmount = eAmount;
        this.ePaidBy = ePaidBy;
        this.eSharedMembers = eSharedMembers;
        this.eCreatedDate = eCreatedDate;
    }

    public Expense() {
    }

    public int geteId() {
        return eId;
    }

    public void seteId(int eId) {
        this.eId = eId;
    }

    public int geteMonthId() {
        return eMonthId;
    }

    public void seteMonthId(int eMonthId) {
        this.eMonthId = eMonthId;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public double geteAmount() {
        return eAmount;
    }

    public void seteAmount(double eAmount) {
        this.eAmount = eAmount;
    }

    public int getePaidBy() {
        return ePaidBy;
    }

    public void setePaidBy(int ePaidBy) {
        this.ePaidBy = ePaidBy;
    }

    public String geteSharedMembers() {
        return eSharedMembers;
    }

    public void seteSharedMembers(String eSharedMembers) {
        this.eSharedMembers = eSharedMembers;
    }

    public String geteCreatedDate() {
        return eCreatedDate;
    }

    public void seteCreatedDate(String eCreatedDate) {
        this.eCreatedDate = eCreatedDate;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "eId=" + eId +
                ", eMonthId=" + eMonthId +
                ", eName='" + eName + '\'' +
                ", eAmount=" + eAmount +
                ", ePaidBy=" + ePaidBy +
                ", eSharedMembers='" + eSharedMembers + '\'' +
                ", eCreatedDate='" + eCreatedDate + '\'' +
                '}';
    }
}
