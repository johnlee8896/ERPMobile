package com.chinashb.www.mobileerp.basicobject;

import java.io.Serializable;

/***
 * @date 创建时间 3/29/26 10:10 PM
 * @author 作者: liweifeng
 * @description
 */
public class PlannInnerDetailContainBackupEntity implements Serializable {
    private boolean hasBackUp;
    private String HR_Name;
    private float BackUpQty;

    private boolean result;
    public  boolean getResult(){return result;}
    public void setResult(boolean result){this.result=result;}

    private String ErrorInfo;
    public  String getErrorInfo(){return ErrorInfo;}
    public void setErrorInfo(String ErrorInfo){this.ErrorInfo=ErrorInfo;}


    private long LotID;
    public  long getLotID(){return LotID;}
    public void setLotID(long LotID){this.LotID=LotID;}

    private long Item_ID;
    public  long getItem_ID(){return Item_ID;}
    public void setItem_ID(long Item_ID){this.Item_ID=Item_ID;}

    private long IV_ID;
    public  long getIV_ID(){return IV_ID;}
    public void setIV_ID(long IV_ID){this.IV_ID=IV_ID;}


    private String ItemName;
    public  String getItemName(){return ItemName;}
    public void setItemName(String ItemName){this.ItemName=ItemName;}

    private String NextLocation;
    public  String getNextLocation(){return NextLocation;}
    public void setNextLocation(String NextLocation){this.NextLocation=NextLocation;}

    private String NextLotNo;
    public  String getNextLotNo(){return NextLotNo;}
    public void setNextLotNo(String NextLotNo){this.NextLotNo=NextLotNo;}

    //单机用量
    private float SingleQty;
    public  float getSingleQty(){return SingleQty;}
    public void setSingleQty(float SingleQty){this.SingleQty=SingleQty;}


    //需求量
    private float NeedQty;
    public  float getNeedQty(){return NeedQty;}
    public void setNeedQty(float NeedQty){this.NeedQty=NeedQty;}


    //已发数量
    private float IssuedQty;
    public  float getIssuedQty(){return IssuedQty;}
    public void setIssuedQty(float IssuedQty){this.IssuedQty=IssuedQty;}

    //还需要
    private float MoreQty;
    public  float getMoreQty(){return MoreQty;}
    public void setMoreQty(float MoreQty){this.MoreQty=MoreQty;}


    private String LastIssueMoment;
    public  String getLastIssueMoment(){return LastIssueMoment;}
    public void setLastIssueMoment(String LastIssueMoment){this.LastIssueMoment=LastIssueMoment;}


    public boolean isHasBackUp() {
        return hasBackUp;
    }

    public PlannInnerDetailContainBackupEntity setHasBackUp(boolean hasBackUp) {
        this.hasBackUp = hasBackUp;
        return this;
    }

    public String getHR_Name() {
        return HR_Name;
    }

    public PlannInnerDetailContainBackupEntity setHR_Name(String HR_Name) {
        this.HR_Name = HR_Name;
        return this;
    }

    public float getBackUpQty() {
        return BackUpQty;
    }

    public PlannInnerDetailContainBackupEntity setBackUpQty(float backUpQty) {
        BackUpQty = backUpQty;
        return this;
    }

    public boolean isResult() {
        return result;
    }
}

