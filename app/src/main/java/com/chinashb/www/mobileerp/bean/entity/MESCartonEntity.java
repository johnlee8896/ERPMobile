package com.chinashb.www.mobileerp.bean.entity;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/2/20 5:59 PM
 * @author 作者: liweifeng
 * @description
 */
public class MESCartonEntity {

    /**
     * Code : 0
     * Message : [{"PS_ID":1829,"Product_ID":1936,"Product_Chinese_Name":"抬高电机(改短版)","Product_DrawNo":"","Product_Version":"14","Item_ID":10901,"IV_ID":11219,"Qty":2,"Item_Unit":"个"}]
     */

    @SerializedName("Code") private int Code;
    @SerializedName("Message") private String Message;

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }
}
