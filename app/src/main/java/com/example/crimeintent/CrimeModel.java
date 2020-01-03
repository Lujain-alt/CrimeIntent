package com.example.crimeintent;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeModel {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspectId;

    public String getmSuspectId() {
        return mSuspectId;
    }

    public void setmSuspectId(String mSuspectId) {
        this.mSuspectId = mSuspectId;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    private String mPhoneNumber;

    public String getmSuspect() {
        return mSuspect;
    }

    public void setmSuspect(String mSuspect) {
        this.mSuspect = mSuspect;
    }

    private String mSuspect;

    public Date getmTime() {
        return mTime;
    }

    public void setmTime(Date mTime) {
        this.mTime = mTime;
    }

    private Date mTime;

    public CrimeModel() {
        this(UUID.randomUUID());
        //mId = UUID.randomUUID();
       // mDate = new Date();
        //mTime= new Date();

    }
    public CrimeModel(UUID id)
    {
        mId=id;
        mDate = new Date();
        mTime= new Date();

    }


    public UUID getmId() {
        return mId;
    }


    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getmDate() {
        return mDate;
    }

    public String getStringDate() {
        String convertDate=null;
        DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
        convertDate=inputFormat.format(mDate);
        return convertDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean ismSolved() {
        return mSolved;
    }

    public void setmSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }

    public String getPhotoFilename() {
        return "IMG_" + getmId().toString() + ".jpg";
    }



}
