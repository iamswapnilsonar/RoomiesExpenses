package com.harshal.roomiesexpenses.entities;

/**
 * Created by Harshal on 8/10/2015.
 */
public class AccountInfo {
    private int acId;
    private String acHoldersName, acBankName, acBranch, acNumber, acIfscCode, acCreatedOn;
    private boolean isAcActive;

    public AccountInfo() {
    }

    public AccountInfo(int acId, String acHoldersName, String acBankName, String acBranch, String acNumber, String acIfscCode, String acCreatedOn, boolean isAcActive) {
        this.acId = acId;
        this.acHoldersName = acHoldersName;
        this.acBankName = acBankName;
        this.acBranch = acBranch;
        this.acNumber = acNumber;
        this.acIfscCode = acIfscCode;
        this.acCreatedOn = acCreatedOn;
        this.isAcActive = isAcActive;
    }

    public int getAcId() {
        return acId;
    }

    public void setAcId(int acId) {
        this.acId = acId;
    }

    public String getAcHoldersName() {
        return acHoldersName;
    }

    public void setAcHoldersName(String acHoldersName) {
        this.acHoldersName = acHoldersName;
    }

    public String getAcBankName() {
        return acBankName;
    }

    public void setAcBankName(String acBankName) {
        this.acBankName = acBankName;
    }

    public String getAcBranch() {
        return acBranch;
    }

    public void setAcBranch(String acBranch) {
        this.acBranch = acBranch;
    }

    public String getAcNumber() {
        return acNumber;
    }

    public void setAcNumber(String acNumber) {
        this.acNumber = acNumber;
    }

    public String getAcIfscCode() {
        return acIfscCode;
    }

    public void setAcIfscCode(String acIfscCode) {
        this.acIfscCode = acIfscCode;
    }

    public String getAcCreatedOn() {
        return acCreatedOn;
    }

    public void setAcCreatedOn(String acCreatedOn) {
        this.acCreatedOn = acCreatedOn;
    }

    public boolean isAcActive() {
        return isAcActive;
    }

    public void setIsAcActive(boolean isAcActive) {
        this.isAcActive = isAcActive;
    }

    @Override
    public String toString() {
        return "Ac Holders Name : " + acHoldersName + "\nBank Name : " + acBankName +"\nBranch : " + acBranch + "\nAc Number : " + acNumber + "\nIFSC Code : " + acIfscCode + "\nCreated On : " + acCreatedOn + "\nIs Active : " + (isAcActive?"YES":"NO");
    }
}
