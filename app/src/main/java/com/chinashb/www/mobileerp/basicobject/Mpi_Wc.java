package com.chinashb.www.mobileerp.basicobject;

import android.text.Html;
import android.widget.TextView;

import java.io.Serializable;

public class Mpi_Wc implements Serializable {
    private boolean result;
    /**
     * MPIWC_ID : 477592
     * Product_Chinese_Name : TA电机
     * Abb : F-1817872X-ST00810
     * Product_PartNo : 1817872X
     * PS_Version : 02
     * Item : P-1817872X
     * Item_Name : TA电机
     * Item_Spec2 :
     * Item_Version : 02
     * MPI_Date : 2018-10-01T00:00:00
     * Shift_Name : 白
     * Shift_No : 1
     * Product_ID : 9571
     * PS_ID : 14310
     * Item_ID : 31080
     * IV_ID : 43542
     * MPI_Quantity : 2000
     * MPI_Remark : 国内

     */

    private Long MPIWC_ID;
    private String Product_Chinese_Name;
    private String Abb;
    private String Product_PartNo;
    private String PS_Version;
    private String Item;
    private String Item_Name;
    private String Item_Spec2;
    private String Item_Version;
    private String MPI_Date;
    private String Shift_Name;
    private int Shift_No;
    private int Product_ID;
    private int PS_ID;
    private int Item_ID;
    private int IV_ID;
    private int MPI_Quantity;
    private String MPI_Remark;

    public  boolean getResult(){return result;}
    public void setResult(boolean result){this.result=result;}

    private String ErrorInfo;
    public  String getErrorInfo(){return ErrorInfo;}
    public void setErrorInfo(String ErrorInfo){this.ErrorInfo=ErrorInfo;}

    /*
    private long Mpiwc_ID;
    public  long getMpiwc_ID(){return Mpiwc_ID;}
    public void setMpiwc_ID(long Mpiwc_ID){this.Mpiwc_ID=Mpiwc_ID;}
*/

    private String MwName;
    public  String getMwName(){return MwName;}
    public void setMwName(String MwName){this.MwName=MwName;}

    private String HtmlMwName;
    public  String getHtmlMwName(){return HtmlMwName;}
    public void setHtmlMwName(String HtmlMwName){this.HtmlMwName=HtmlMwName;}


    private long Wc_ID;
    public  long getWc_ID(){return Wc_ID;}
    public void setWc_ID(long Wc_ID){this.Wc_ID=Wc_ID;}


    private int Bu_ID;
    public  int getBu_ID(){return Bu_ID;}
    public void setBu_ID(int Bu_ID){this.Bu_ID=Bu_ID;}


    private String BuName;
    public  String getBuName(){return BuName;}
    public void setBuName(String BuName){this.BuName=BuName;}


    public Long getMPIWC_ID() {
        return MPIWC_ID;
    }

    public void setMPIWC_ID(Long MPIWC_ID) {
        this.MPIWC_ID = MPIWC_ID;
    }

    public String getProduct_Chinese_Name() {
        return Product_Chinese_Name;
    }

    public void setProduct_Chinese_Name(String Product_Chinese_Name) {
        this.Product_Chinese_Name = Product_Chinese_Name;
    }

    public String getAbb() {
        return Abb;
    }

    public void setAbb(String Abb) {
        this.Abb = Abb;
    }

    public String getProduct_PartNo() {
        return Product_PartNo;
    }

    public void setProduct_PartNo(String Product_PartNo) {
        this.Product_PartNo = Product_PartNo;
    }

    public String getPS_Version() {
        return PS_Version;
    }

    public void setPS_Version(String PS_Version) {
        this.PS_Version = PS_Version;
    }

    public String getItem() {
        return Item;
    }

    public void setItem(String Item) {
        this.Item = Item;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String Item_Name) {
        this.Item_Name = Item_Name;
    }

    public String getItem_Spec2() {
        return Item_Spec2;
    }

    public void setItem_Spec2(String Item_Spec2) {
        this.Item_Spec2 = Item_Spec2;
    }

    public String getItem_Version() {
        return Item_Version;
    }

    public void setItem_Version(String Item_Version) {
        this.Item_Version = Item_Version;
    }

    public String getMPI_Date() {
        return MPI_Date;
    }

    public void setMPI_Date(String MPI_Date) {
        this.MPI_Date = MPI_Date;
    }

    public String getShift_Name() {
        return Shift_Name;
    }

    public void setShift_Name(String Shift_Name) {
        this.Shift_Name = Shift_Name;
    }

    public int getShift_No() {
        return Shift_No;
    }

    public void setShift_No(int Shift_No) {
        this.Shift_No = Shift_No;
    }

    public int getProduct_ID() {
        return Product_ID;
    }

    public void setProduct_ID(int Product_ID) {
        this.Product_ID = Product_ID;
    }

    public int getPS_ID() {
        return PS_ID;
    }

    public void setPS_ID(int PS_ID) {
        this.PS_ID = PS_ID;
    }

    public int getItem_ID() {
        return Item_ID;
    }

    public void setItem_ID(int Item_ID) {
        this.Item_ID = Item_ID;
    }

    public int getIV_ID() {
        return IV_ID;
    }

    public void setIV_ID(int IV_ID) {
        this.IV_ID = IV_ID;
    }

    public int getMPI_Quantity() {
        return MPI_Quantity;
    }

    public void setMPI_Quantity(int MPI_Quantity) {
        this.MPI_Quantity = MPI_Quantity;
    }

    public String getMPI_Remark() {
        return MPI_Remark;
    }

    public void setMPI_Remark(String MPI_Remark) {
        this.MPI_Remark = MPI_Remark;
    }

    public void setMwNameTextView(TextView tv)
    {
        if(tv !=null)
        {
            if ( !HtmlMwName.equals(""))
            {
                tv.setText(Html.fromHtml(HtmlMwName));
            }
            else
            {tv.setText(MwName);}
        }

    }

}
