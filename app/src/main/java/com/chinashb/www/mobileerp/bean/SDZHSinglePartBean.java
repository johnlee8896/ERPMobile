package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/2/27 4:52 PM
 * @author 作者: liweifeng
 * @description 三点照合 单机条码 bean
 */
public class SDZHSinglePartBean {

    @SerializedName("DOI_Id") private int DOIId;
    @SerializedName("BoxCode") private String BoxCode;
    @SerializedName("DOI_NO") private String doiNO;
    @SerializedName("ProductNo") private String ProductNo;
    @SerializedName("DOI_Code") private String DOI_Code;
    @SerializedName("WorkNo") private String WorkNo;
    @SerializedName("ProductId") private String ProductId;
    @SerializedName("LineId") private int LineId;
    @SerializedName("IsDelete") private int IsDelete;

    public int getDOIId() {
        return DOIId;
    }

    public SDZHSinglePartBean setDOIId(int DOIId) {
        this.DOIId = DOIId;
        return this;
    }

    public String getBoxCode() {
        return BoxCode;
    }

    public SDZHSinglePartBean setBoxCode(String boxCode) {
        BoxCode = boxCode;
        return this;
    }

    public String getDoiNO() {
        return doiNO;
    }

    public SDZHSinglePartBean setDoiNO(String doiNO) {
        this.doiNO = doiNO;
        return this;
    }

    public String getProductNo() {
        return ProductNo;
    }

    public SDZHSinglePartBean setProductNo(String productNo) {
        ProductNo = productNo;
        return this;
    }

    public String getDOI_Code() {
        return DOI_Code;
    }

    public SDZHSinglePartBean setDOI_Code(String DOI_Code) {
        this.DOI_Code = DOI_Code;
        return this;
    }

    public String getWorkNo() {
        return WorkNo;
    }

    public SDZHSinglePartBean setWorkNo(String workNo) {
        WorkNo = workNo;
        return this;
    }

    public String getProductId() {
        return ProductId;
    }

    public SDZHSinglePartBean setProductId(String productId) {
        ProductId = productId;
        return this;
    }

    public int getLineId() {
        return LineId;
    }

    public SDZHSinglePartBean setLineId(int lineId) {
        LineId = lineId;
        return this;
    }

    public int getIsDelete() {
        return IsDelete;
    }

    public SDZHSinglePartBean setIsDelete(int isDelete) {
        IsDelete = isDelete;
        return this;
    }
}
