package com.harshal.roomiesexpenses.entities;

/**
 * Created by Harshal on 1/13/2015.
 */
public class GeneratedBill {
    private double sharedAmount, paidAmount, remainingAmount;
    private String memberName = "";

    public GeneratedBill(double sharedAmount, double paidAmount, double remainingAmount) {
        this.sharedAmount = sharedAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = remainingAmount;
    }

    public GeneratedBill() {
    }

    public double getSharedAmount() {
        return sharedAmount;
    }

    public void setSharedAmount(double sharedAmount) {
        this.sharedAmount = sharedAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {
        return "GeneratedBill{" +
                "sharedAmount=" + sharedAmount +
                ", paidAmount=" + paidAmount +
                ", remainingAmount=" + remainingAmount +
                '}';
    }
}
