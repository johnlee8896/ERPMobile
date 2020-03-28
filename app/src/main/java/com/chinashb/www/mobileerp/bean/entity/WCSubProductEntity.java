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
    @SerializedName("Item_ID") private long itemId;
    @SerializedName("IV_ID") private long ivId;
    @SerializedName("Product_ID") private long productId;
    @SerializedName("PS_ID") private long psId;
    @SerializedName("Product_Name") private String productName;
    @SerializedName("Product_Common_Name") private String productCommonName;
    @SerializedName("Newest_Version") private String newestVersion;
    @SerializedName("Approval_detail") private String approvalDetail;

    public WCSubProductEntity(){

    }

    protected WCSubProductEntity(Parcel in) {
        itemId = in.readLong();
        ivId = in.readLong();
        productId = in.readLong();
        psId = in.readLong();
        productName = in.readString();
        productCommonName = in.readString();
        newestVersion = in.readString();
        approvalDetail = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(itemId);
        dest.writeLong(ivId);
        dest.writeLong(productId);
        dest.writeLong(psId);
        dest.writeString(productName);
        dest.writeString(productCommonName);
        dest.writeString(newestVersion);
        dest.writeString(approvalDetail);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public long getItemId() {
        return itemId;
    }

    public WCSubProductEntity setItemId(long itemId) {
        this.itemId = itemId;
        return this;
    }

    public long getIvId() {
        return ivId;
    }

    public WCSubProductEntity setIvId(long ivId) {
        this.ivId = ivId;
        return this;
    }

    public long getProductId() {
        return productId;
    }

    public WCSubProductEntity setProductId(long productId) {
        this.productId = productId;
        return this;
    }

    public long getPsId() {
        return psId;
    }

    public WCSubProductEntity setPsId(long psId) {
        this.psId = psId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public WCSubProductEntity setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductCommonName() {
        return productCommonName;
    }

    public WCSubProductEntity setProductCommonName(String productCommonName) {
        this.productCommonName = productCommonName;
        return this;
    }

    public String getNewestVersion() {
        return newestVersion;
    }

    public WCSubProductEntity setNewestVersion(String newestVersion) {
        this.newestVersion = newestVersion;
        return this;
    }

    public String getApprovalDetail() {
        return approvalDetail;
    }

    public WCSubProductEntity setApprovalDetail(String approvalDetail) {
        this.approvalDetail = approvalDetail;
        return this;
    }
}
