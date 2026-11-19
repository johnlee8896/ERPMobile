package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 高新研发项目Bean
 * 对应服务端 Get_Product_Fee_RDM 返回的JSON
 */
public class ProductFeeRdmBean implements Parcelable {

    @SerializedName("编号") private String number;
    @SerializedName("高新项目编号") private String highTechProjectNo;
    @SerializedName("FiPT_ID") private int fiptID;
    @SerializedName("研发类型") private String researchType;

    public ProductFeeRdmBean() {}

    protected ProductFeeRdmBean(Parcel in) {
        number = in.readString();
        highTechProjectNo = in.readString();
        fiptID = in.readInt();
        researchType = in.readString();
    }

    public static final Creator<ProductFeeRdmBean> CREATOR = new Creator<ProductFeeRdmBean>() {
        @Override
        public ProductFeeRdmBean createFromParcel(Parcel in) {
            return new ProductFeeRdmBean(in);
        }

        @Override
        public ProductFeeRdmBean[] newArray(int size) {
            return new ProductFeeRdmBean[size];
        }
    };

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHighTechProjectNo() {
        return highTechProjectNo;
    }

    public void setHighTechProjectNo(String highTechProjectNo) {
        this.highTechProjectNo = highTechProjectNo;
    }

    public int getFiptID() {
        return fiptID;
    }

    public void setFiptID(int fiptID) {
        this.fiptID = fiptID;
    }

    public String getResearchType() {
        return researchType;
    }

    public void setResearchType(String researchType) {
        this.researchType = researchType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(highTechProjectNo);
        dest.writeInt(fiptID);
        dest.writeString(researchType);
    }
}
