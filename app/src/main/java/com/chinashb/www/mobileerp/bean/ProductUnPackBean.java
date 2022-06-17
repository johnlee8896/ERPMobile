package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2022/5/19 3:31 PM
 * @author 作者: liweifeng
 * @description 无包装
 */
class ProductUnPackBean {
    @SerializedName("Lot_ID") private int Lot_ID;
    @SerializedName("PS_ID") private int PS_ID;
    @SerializedName("产品数量") private int 产品数量;
    @SerializedName("状态") private String 状态;
    @SerializedName("批次") private String 批次;
//    @SerializedName("包装日期") private String 包装日期;
    @SerializedName("入库日期") private String 入库日期;
    @SerializedName("备注") private String 标注;
    @SerializedName("存储区域") private String 存储区域;
//    @SerializedName("包装序列号") private String 包装序列号;
}
