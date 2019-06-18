package com.chinashb.www.mobileerp.basicobject;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public final class Msg implements Serializable, Comparable<Msg> {
    public Integer MsID;
    public Date msgTime;
    public String msgTimes;
    public String mSender;
    public int mSenderID;
    public String mReceiver;
    public int mReceiverID;
    public String Msg;

    public Bitmap HR_Pic1;
    public Bitmap HR_Pic2;

    @Override
    public int compareTo(@NonNull com.chinashb.www.mobileerp.basicobject.Msg o) {
        int i = this.MsID-o.MsID;

        return i ;
    }
}

