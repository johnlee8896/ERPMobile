package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2019/7/4 13:39
 * @author 作者: xxblwf
 * @description 研究项目bean
 */

public class ResearchItemBean implements Parcelable {

    /**
     * Product_ID : 4100
     * PS_ID : 7220
     * Abb : F-EPS2-GMD(VAVE)-4513427
     * FiPT_ID : 31
     * Program : ECS电子控制系统
     * FSPD_ID : 1
     * Status : 大批量生产
     * Program_ID : 19
     */

    @SerializedName("Product_ID") private int ProductID;
    @SerializedName("PS_ID") private int PSID;
    @SerializedName("Abb") private String Abb;
    @SerializedName("FiPT_ID") private int FiPTID;
    @SerializedName("Program") private String Program;
    @SerializedName("FSPD_ID") private int FSPDID;
    @SerializedName("Status") private String Status;
    @SerializedName("Program_ID") private int ProgramID;

    protected ResearchItemBean(Parcel in) {
        ProductID = in.readInt();
        PSID = in.readInt();
        Abb = in.readString();
        FiPTID = in.readInt();
        Program = in.readString();
        FSPDID = in.readInt();
        Status = in.readString();
        ProgramID = in.readInt();
    }

    public static final Creator<ResearchItemBean> CREATOR = new Creator<ResearchItemBean>() {
        @Override
        public ResearchItemBean createFromParcel(Parcel in) {
            return new ResearchItemBean(in);
        }

        @Override
        public ResearchItemBean[] newArray(int size) {
            return new ResearchItemBean[size];
        }
    };

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int ProductID) {
        this.ProductID = ProductID;
    }

    public int getPSID() {
        return PSID;
    }

    public void setPSID(int PSID) {
        this.PSID = PSID;
    }

    public String getAbb() {
        return Abb;
    }

    public void setAbb(String Abb) {
        this.Abb = Abb;
    }

    public int getFiPTID() {
        return FiPTID;
    }

    public void setFiPTID(int FiPTID) {
        this.FiPTID = FiPTID;
    }

    public String getProgram() {
        return Program;
    }

    public void setProgram(String Program) {
        this.Program = Program;
    }

    public int getFSPDID() {
        return FSPDID;
    }

    public void setFSPDID(int FSPDID) {
        this.FSPDID = FSPDID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public int getProgramID() {
        return ProgramID;
    }

    public void setProgramID(int ProgramID) {
        this.ProgramID = ProgramID;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ProductID);
        dest.writeInt(PSID);
        dest.writeString(Abb);
        dest.writeInt(FiPTID);
        dest.writeString(Program);
        dest.writeInt(FSPDID);
        dest.writeString(Status);
        dest.writeInt(ProgramID);
    }
}
