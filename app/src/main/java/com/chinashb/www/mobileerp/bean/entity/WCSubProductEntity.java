package com.chinashb.www.mobileerp.bean.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2019/12/19 19:56
 * @author 作者: xxblwf
 * @description 成品某条产线对应的子级产品
 */

public class WCSubProductEntity implements Parcelable {
    @SerializedName("Item_ID") private int itemId;
    @SerializedName("IV_ID") private int ivId;
    @SerializedName("Product_ID") private int productId;
    @SerializedName("PS_ID") private int psId;
    @SerializedName("Product_Name") private String productName;
    @SerializedName("Product_Common_Name") private String productCommonName;
    @SerializedName("Newest_Version") private String newestVersion;
    @SerializedName("Approval_detail") private String approvalDetail;

    protected WCSubProductEntity(Parcel in) {
        itemId = in.readInt();
        ivId = in.readInt();
        productId = in.readInt();
        psId = in.readInt();
        productName = in.readString();
        productCommonName = in.readString();
        newestVersion = in.readString();
        approvalDetail = in.readString();
    }

    public static final Creator<WCSubProductEntity> CREATOR = new Creator<WCSubProductEntity>() {
        @Override
        public WCSubProductEntity createFromParcel(Parcel in) {
            return new WCSubProductEntity(in);
        }

        @Override
        public WCSubProductEntity[] newArray(int size) {
            return new WCSubProductEntity[size];
        }
    };

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getIvId() {
        return ivId;
    }

    public void setIvId(int ivId) {
        this.ivId = ivId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getPsId() {
        return psId;
    }

    public void setPsId(int psId) {
        this.psId = psId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCommonName() {
        return productCommonName;
    }

    public void setProductCommonName(String productCommonName) {
        this.productCommonName = productCommonName;
    }

    public String getNewestVersion() {
        return newestVersion;
    }

    public void setNewestVersion(String newestVersion) {
        this.newestVersion = newestVersion;
    }

    public String getApprovalDetail() {
        return approvalDetail;
    }

    public void setApprovalDetail(String approvalDetail) {
        this.approvalDetail = approvalDetail;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemId);
        dest.writeInt(ivId);
        dest.writeInt(productId);
        dest.writeInt(psId);
        dest.writeString(productName);
        dest.writeString(productCommonName);
        dest.writeString(newestVersion);
        dest.writeString(approvalDetail);
    }
}
