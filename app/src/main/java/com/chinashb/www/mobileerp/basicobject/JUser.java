package com.chinashb.www.mobileerp.basicobject;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

public class JUser implements Serializable {


    /**
     * HR_ID : 249
     * Bu_ID : 1
     * Company_ID : 1
     * HR_Name : 金迦勒
     * Bu_Name : 座椅电机
     * Company_Name : 上海胜华波汽车电器有限公司
     */

    private int HR_ID;
    private int Bu_ID;
    private int Company_ID;
    private String HR_Name;
    private String Bu_Name;
    private String Company_Name;

    public int getHR_ID() {
        return HR_ID;
    }

    public void setHR_ID(int HR_ID) {
        this.HR_ID = HR_ID;
    }

    public int getBu_ID() {
        return Bu_ID;
    }

    public void setBu_ID(int Bu_ID) {
        this.Bu_ID = Bu_ID;
    }

    public int getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(int Company_ID) {
        this.Company_ID = Company_ID;
    }

    public String getHR_Name() {
        return HR_Name;
    }

    public void setHR_Name(String HR_Name) {
        this.HR_Name = HR_Name;
    }

    public String getBu_Name() {
        return Bu_Name;
    }

    public void setBu_Name(String Bu_Name) {
        this.Bu_Name = Bu_Name;
    }

    public String getCompany_Name() {
        return Company_Name;
    }

    public void setCompany_Name(String Company_Name) {
        this.Company_Name = Company_Name;
    }
}

