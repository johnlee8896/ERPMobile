package com.chinashb.www.mobileerp.basicobject;

import java.io.Serializable;

/**
 * Created by Paul on 2017/1/20.
 */

public class UserInfoEntity implements Serializable {

    //    public static int ID;
//    public static String IP;
    private int HR_ID;
    private String hrNum;
    private String HR_Name;
    private String Bu_Name;
    private int Bu_ID;
    private String Company_Name;
    private int Company_ID;

    public int getHR_ID() {
        return HR_ID;
    }

    public void setHR_ID(int hrID) {
//        ID = hrID;
        this.HR_ID = hrID;
    }

    public String getHrNum() {
        return hrNum;
    }

    public void setHrNum(String hrNum) {
        this.hrNum = hrNum;
    }

    public String getHR_Name() {
        return HR_Name;
    }

    public void setHR_Name(String hrNum) {
        this.HR_Name = hrNum;
    }

    public String getBu_Name() {
        return Bu_Name;
    }

    public void setBu_Name(String Bu_Name) {
        this.Bu_Name = Bu_Name;
    }

    public int getBu_ID() {
        return Bu_ID;
    }

    public void setBu_ID(int Bu_ID) {
        this.Bu_ID = Bu_ID;
    }

    public String getCompany_Name() {
        return Company_Name;
    }

    public void setCompany_Name(String Company_Name) {
        this.Company_Name = Company_Name;
    }

    public int getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(int Company_ID) {
        this.Company_ID = Company_ID;
    }

}
