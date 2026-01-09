package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 4/12/25 8:38 PM
 * @author 作者: liweifeng
 * @description 辅料物料bean
 */
public class AssistItemBean implements Parcelable {
    @SerializedName("Item_ID") private int Item_ID;
    @SerializedName("名称") private String Item_Name;
    @SerializedName("物料编码") private String Item;
    @SerializedName("规格") private String Item_Spec2;
    @SerializedName("单位") private String Item_Unit;
    @SerializedName("单机用量") private float Qty;
    @SerializedName("备注") private String Remark;

    protected AssistItemBean(Parcel in) {
        Item_ID = in.readInt();
        Item_Name = in.readString();
        Item = in.readString();
        Item_Spec2 = in.readString();
        Item_Unit = in.readString();
        Qty = in.readFloat();
        Remark = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Item_ID);
        dest.writeString(Item_Name);
        dest.writeString(Item);
        dest.writeString(Item_Spec2);
        dest.writeString(Item_Unit);
        dest.writeFloat(Qty);
        dest.writeString(Remark);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AssistItemBean> CREATOR = new Creator<AssistItemBean>() {
        @Override
        public AssistItemBean createFromParcel(Parcel in) {
            return new AssistItemBean(in);
        }

        @Override
        public AssistItemBean[] newArray(int size) {
            return new AssistItemBean[size];
        }
    };

    public int getItem_ID() {
        return Item_ID;
    }

    public AssistItemBean setItem_ID(int item_ID) {
        Item_ID = item_ID;
        return this;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public AssistItemBean setItem_Name(String item_Name) {
        Item_Name = item_Name;
        return this;
    }

    public String getItem() {
        return Item;
    }

    public AssistItemBean setItem(String item) {
        Item = item;
        return this;
    }

    public String getItem_Spec2() {
        return Item_Spec2;
    }

    public AssistItemBean setItem_Spec2(String item_Spec2) {
        Item_Spec2 = item_Spec2;
        return this;
    }

    public String getItem_Unit() {
        return Item_Unit;
    }

    public AssistItemBean setItem_Unit(String item_Unit) {
        Item_Unit = item_Unit;
        return this;
    }

    public float getQty() {
        return Qty;
    }

    public AssistItemBean setQty(float qty) {
        Qty = qty;
        return this;
    }

    public String getRemark() {
        return Remark;
    }

    public AssistItemBean setRemark(String remark) {
        Remark = remark;
        return this;
    }
}
