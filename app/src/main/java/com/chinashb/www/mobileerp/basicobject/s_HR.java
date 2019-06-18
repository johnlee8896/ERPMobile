package com.shb.stockinout.basicobject;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class s_HR implements Serializable {
    /**
     * HR_ID : 1
     * HR_N : null
     * HR_NO : 200306008
     * HR_No_Old : 30608
     * HR_No1009 : 20030608
     * HR_Name : 丁明森
     * HR_Sex : 男
     * HR_Birthday : 1949-09-26T00:00:00
     * HR_Nationality : 汉族
     * HR_Home_Address : 江苏省苏州市浒墅关镇下塘北街
     * HR_PostNo :
     * HR_IDNo : 320511194909262010
     * HR_IDNo_SDate : 1900-01-01T00:00:00
     * HR_IDNo_EDate : 1900-01-01T00:00:00
     * HR_IDNo_Years : 0
     * HR_Marital_Status : 已婚
     * HR_Telephone : 辞职
     * HR_Cellphone : 13311678881
     * HR_Email :
     * HR_Degree : 高中毕业
     * HR_Major :
     * HR_School :
     * HR_Speciality : null
     * HR_EntryDate : 2016-05-01T00:00:00
     * HR_Political : 党员
     * HR_JoinDate : 2008-03-07T00:00:00
     * Company_ID : 1
     * Department_ID : 12
     * Role_ID : 0
     * Job_ID : 2
     * TryMonths : 3
     * Leave : true
     * LeaveID : 16851
     * LeaveDate : 2016-12-07T00:00:00
     * Rehab : true
     * RehabDate : 2016-05-01T00:00:00
     * SafeStatus : 未办
     * HR_Introducer :
     * Introducer_Tele :
     * HR_Sudden_Teller :
     * Sudden_Teller_Tele :
     * Sudden_Teller_Addr :
     * HR_Remark :
     * HC_ID : null
     * DML_ID : null
     * AccSure : null
     * HR_Department : 行政部
     * HR_Office : true
     * HR_Work_Type : null
     * Hr_position : null
     * DeptID : null
     * HR_Dormitory : null
     * Dormitory_ID : null
     * Room_ID : null
     * Job :
     * office_phoneno : 8004
     * short_phoneno :
     * NameHelperPY : DMS
     * HR_IsLaborDispatch : false
     * HR_LaborDispatchCompany :
     * HR_Card : 卡已办
     * HR_IDName : 丁明森
     * HR_IsOutSource : false
     * HR_OutSourceCompany :
     * HR_Residency : null
     * HR_Level_ID : null
     * HR_Position_ID : null
     * HR_Level : null
     */

    private Long HR_ID;
    private Object HR_N;
    private String HR_NO;
    private String HR_No_Old;
    private String HR_No1009;
    private String HR_Name;
    private String HR_Sex;
    private String HR_Birthday;
    private String HR_Nationality;
    private String HR_Home_Address;
    private String HR_PostNo;
    private String HR_IDNo;
    private String HR_IDNo_SDate;
    private String HR_IDNo_EDate;
    private int HR_IDNo_Years;
    private String HR_Marital_Status;
    private String HR_Telephone;
    private String HR_Cellphone;
    private String HR_Email;
    private String HR_Degree;
    private String HR_Major;
    private String HR_School;
    private Object HR_Speciality;
    private String HR_EntryDate;
    private String HR_Political;
    private String HR_JoinDate;
    private int Company_ID;
    private int Department_ID;
    private int Role_ID;
    private int Job_ID;
    private int TryMonths;
    private boolean Leave;
    private int LeaveID;
    private String LeaveDate;
    private boolean Rehab;
    private String RehabDate;
    private String SafeStatus;
    private String HR_Introducer;
    private String Introducer_Tele;
    private String HR_Sudden_Teller;
    private String Sudden_Teller_Tele;
    private String Sudden_Teller_Addr;
    private String HR_Remark;
    private Object HC_ID;
    private Object DML_ID;
    private Object AccSure;
    private String HR_Department;
    private boolean HR_Office;
    private Object HR_Work_Type;
    private Object Hr_position;
    private Object DeptID;
    private Object HR_Dormitory;
    private Object Dormitory_ID;
    private Object Room_ID;
    private String Job;
    private String office_phoneno;
    private String short_phoneno;
    private String NameHelperPY;
    private boolean HR_IsLaborDispatch;
    private String HR_LaborDispatchCompany;
    private String HR_Card;
    private String HR_IDName;
    private boolean HR_IsOutSource;
    private String HR_OutSourceCompany;
    private Object HR_Residency;
    private Object HR_Level_ID;
    private Object HR_Position_ID;
    private Object HR_Level;
    /**
     * HR_ID : 1
     * HR_N : null
     * HR_Speciality : null
     * HC_ID : null
     * DML_ID : null
     * AccSure : null
     * HR_Work_Type : null
     * Hr_position : null
     * DeptID : null
     * HR_Dormitory : null
     * Dormitory_ID : null
     * Room_ID : null
     * HR_Residency : null
     * HR_Level_ID : null
     * HR_Position_ID : null
     * HR_Level : null
     */

    @SerializedName("HR_ID")
    private int HR_IDX;
    @SerializedName("HR_N")
    private Object HR_NX;
    @SerializedName("HR_Speciality")
    private Object HR_SpecialityX;
    @SerializedName("HC_ID")
    private Object HC_IDX;
    @SerializedName("DML_ID")
    private Object DML_IDX;
    @SerializedName("AccSure")
    private Object AccSureX;
    @SerializedName("HR_Work_Type")
    private Object HR_Work_TypeX;
    @SerializedName("Hr_position")
    private Object Hr_positionX;
    @SerializedName("DeptID")
    private Object DeptIDX;
    @SerializedName("HR_Dormitory")
    private Object HR_DormitoryX;
    @SerializedName("Dormitory_ID")
    private Object Dormitory_IDX;
    @SerializedName("Room_ID")
    private Object Room_IDX;
    @SerializedName("HR_Residency")
    private Object HR_ResidencyX;
    @SerializedName("HR_Level_ID")
    private Object HR_Level_IDX;
    @SerializedName("HR_Position_ID")
    private Object HR_Position_IDX;
    @SerializedName("HR_Level")
    private Object HR_LevelX;

    public Long getHR_ID() {
        return HR_ID;
    }

    public void setHR_ID(Long HR_ID) {
        this.HR_ID = HR_ID;
    }

    public Object getHR_N() {
        return HR_N;
    }

    public void setHR_N(Object HR_N) {
        this.HR_N = HR_N;
    }

    public String getHR_NO() {
        return HR_NO;
    }

    public void setHR_NO(String HR_NO) {
        this.HR_NO = HR_NO;
    }

    public String getHR_No_Old() {
        return HR_No_Old;
    }

    public void setHR_No_Old(String HR_No_Old) {
        this.HR_No_Old = HR_No_Old;
    }

    public String getHR_No1009() {
        return HR_No1009;
    }

    public void setHR_No1009(String HR_No1009) {
        this.HR_No1009 = HR_No1009;
    }

    public String getHR_Name() {
        return HR_Name;
    }

    public void setHR_Name(String HR_Name) {
        this.HR_Name = HR_Name;
    }

    public String getHR_Sex() {
        return HR_Sex;
    }

    public void setHR_Sex(String HR_Sex) {
        this.HR_Sex = HR_Sex;
    }

    public String getHR_Birthday() {
        return HR_Birthday;
    }

    public void setHR_Birthday(String HR_Birthday) {
        this.HR_Birthday = HR_Birthday;
    }

    public String getHR_Nationality() {
        return HR_Nationality;
    }

    public void setHR_Nationality(String HR_Nationality) {
        this.HR_Nationality = HR_Nationality;
    }

    public String getHR_Home_Address() {
        return HR_Home_Address;
    }

    public void setHR_Home_Address(String HR_Home_Address) {
        this.HR_Home_Address = HR_Home_Address;
    }

    public String getHR_PostNo() {
        return HR_PostNo;
    }

    public void setHR_PostNo(String HR_PostNo) {
        this.HR_PostNo = HR_PostNo;
    }

    public String getHR_IDNo() {
        return HR_IDNo;
    }

    public void setHR_IDNo(String HR_IDNo) {
        this.HR_IDNo = HR_IDNo;
    }

    public String getHR_IDNo_SDate() {
        return HR_IDNo_SDate;
    }

    public void setHR_IDNo_SDate(String HR_IDNo_SDate) {
        this.HR_IDNo_SDate = HR_IDNo_SDate;
    }

    public String getHR_IDNo_EDate() {
        return HR_IDNo_EDate;
    }

    public void setHR_IDNo_EDate(String HR_IDNo_EDate) {
        this.HR_IDNo_EDate = HR_IDNo_EDate;
    }

    public int getHR_IDNo_Years() {
        return HR_IDNo_Years;
    }

    public void setHR_IDNo_Years(int HR_IDNo_Years) {
        this.HR_IDNo_Years = HR_IDNo_Years;
    }

    public String getHR_Marital_Status() {
        return HR_Marital_Status;
    }

    public void setHR_Marital_Status(String HR_Marital_Status) {
        this.HR_Marital_Status = HR_Marital_Status;
    }

    public String getHR_Telephone() {
        return HR_Telephone;
    }

    public void setHR_Telephone(String HR_Telephone) {
        this.HR_Telephone = HR_Telephone;
    }

    public String getHR_Cellphone() {
        return HR_Cellphone;
    }

    public void setHR_Cellphone(String HR_Cellphone) {
        this.HR_Cellphone = HR_Cellphone;
    }

    public String getHR_Email() {
        return HR_Email;
    }

    public void setHR_Email(String HR_Email) {
        this.HR_Email = HR_Email;
    }

    public String getHR_Degree() {
        return HR_Degree;
    }

    public void setHR_Degree(String HR_Degree) {
        this.HR_Degree = HR_Degree;
    }

    public String getHR_Major() {
        return HR_Major;
    }

    public void setHR_Major(String HR_Major) {
        this.HR_Major = HR_Major;
    }

    public String getHR_School() {
        return HR_School;
    }

    public void setHR_School(String HR_School) {
        this.HR_School = HR_School;
    }

    public Object getHR_Speciality() {
        return HR_Speciality;
    }

    public void setHR_Speciality(Object HR_Speciality) {
        this.HR_Speciality = HR_Speciality;
    }

    public String getHR_EntryDate() {
        return HR_EntryDate;
    }

    public void setHR_EntryDate(String HR_EntryDate) {
        this.HR_EntryDate = HR_EntryDate;
    }

    public String getHR_Political() {
        return HR_Political;
    }

    public void setHR_Political(String HR_Political) {
        this.HR_Political = HR_Political;
    }

    public String getHR_JoinDate() {
        return HR_JoinDate;
    }

    public void setHR_JoinDate(String HR_JoinDate) {
        this.HR_JoinDate = HR_JoinDate;
    }

    public int getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(int Company_ID) {
        this.Company_ID = Company_ID;
    }

    public int getDepartment_ID() {
        return Department_ID;
    }

    public void setDepartment_ID(int Department_ID) {
        this.Department_ID = Department_ID;
    }

    public int getRole_ID() {
        return Role_ID;
    }

    public void setRole_ID(int Role_ID) {
        this.Role_ID = Role_ID;
    }

    public int getJob_ID() {
        return Job_ID;
    }

    public void setJob_ID(int Job_ID) {
        this.Job_ID = Job_ID;
    }

    public int getTryMonths() {
        return TryMonths;
    }

    public void setTryMonths(int TryMonths) {
        this.TryMonths = TryMonths;
    }

    public boolean isLeave() {
        return Leave;
    }

    public void setLeave(boolean Leave) {
        this.Leave = Leave;
    }

    public int getLeaveID() {
        return LeaveID;
    }

    public void setLeaveID(int LeaveID) {
        this.LeaveID = LeaveID;
    }

    public String getLeaveDate() {
        return LeaveDate;
    }

    public void setLeaveDate(String LeaveDate) {
        this.LeaveDate = LeaveDate;
    }

    public boolean isRehab() {
        return Rehab;
    }

    public void setRehab(boolean Rehab) {
        this.Rehab = Rehab;
    }

    public String getRehabDate() {
        return RehabDate;
    }

    public void setRehabDate(String RehabDate) {
        this.RehabDate = RehabDate;
    }

    public String getSafeStatus() {
        return SafeStatus;
    }

    public void setSafeStatus(String SafeStatus) {
        this.SafeStatus = SafeStatus;
    }

    public String getHR_Introducer() {
        return HR_Introducer;
    }

    public void setHR_Introducer(String HR_Introducer) {
        this.HR_Introducer = HR_Introducer;
    }

    public String getIntroducer_Tele() {
        return Introducer_Tele;
    }

    public void setIntroducer_Tele(String Introducer_Tele) {
        this.Introducer_Tele = Introducer_Tele;
    }

    public String getHR_Sudden_Teller() {
        return HR_Sudden_Teller;
    }

    public void setHR_Sudden_Teller(String HR_Sudden_Teller) {
        this.HR_Sudden_Teller = HR_Sudden_Teller;
    }

    public String getSudden_Teller_Tele() {
        return Sudden_Teller_Tele;
    }

    public void setSudden_Teller_Tele(String Sudden_Teller_Tele) {
        this.Sudden_Teller_Tele = Sudden_Teller_Tele;
    }

    public String getSudden_Teller_Addr() {
        return Sudden_Teller_Addr;
    }

    public void setSudden_Teller_Addr(String Sudden_Teller_Addr) {
        this.Sudden_Teller_Addr = Sudden_Teller_Addr;
    }

    public String getHR_Remark() {
        return HR_Remark;
    }

    public void setHR_Remark(String HR_Remark) {
        this.HR_Remark = HR_Remark;
    }

    public Object getHC_ID() {
        return HC_ID;
    }

    public void setHC_ID(Object HC_ID) {
        this.HC_ID = HC_ID;
    }

    public Object getDML_ID() {
        return DML_ID;
    }

    public void setDML_ID(Object DML_ID) {
        this.DML_ID = DML_ID;
    }

    public Object getAccSure() {
        return AccSure;
    }

    public void setAccSure(Object AccSure) {
        this.AccSure = AccSure;
    }

    public String getHR_Department() {
        return HR_Department;
    }

    public void setHR_Department(String HR_Department) {
        this.HR_Department = HR_Department;
    }

    public boolean isHR_Office() {
        return HR_Office;
    }

    public void setHR_Office(boolean HR_Office) {
        this.HR_Office = HR_Office;
    }

    public Object getHR_Work_Type() {
        return HR_Work_Type;
    }

    public void setHR_Work_Type(Object HR_Work_Type) {
        this.HR_Work_Type = HR_Work_Type;
    }

    public Object getHr_position() {
        return Hr_position;
    }

    public void setHr_position(Object Hr_position) {
        this.Hr_position = Hr_position;
    }

    public Object getDeptID() {
        return DeptID;
    }

    public void setDeptID(Object DeptID) {
        this.DeptID = DeptID;
    }

    public Object getHR_Dormitory() {
        return HR_Dormitory;
    }

    public void setHR_Dormitory(Object HR_Dormitory) {
        this.HR_Dormitory = HR_Dormitory;
    }

    public Object getDormitory_ID() {
        return Dormitory_ID;
    }

    public void setDormitory_ID(Object Dormitory_ID) {
        this.Dormitory_ID = Dormitory_ID;
    }

    public Object getRoom_ID() {
        return Room_ID;
    }

    public void setRoom_ID(Object Room_ID) {
        this.Room_ID = Room_ID;
    }

    public String getJob() {
        return Job;
    }

    public void setJob(String Job) {
        this.Job = Job;
    }

    public String getOffice_phoneno() {
        return office_phoneno;
    }

    public void setOffice_phoneno(String office_phoneno) {
        this.office_phoneno = office_phoneno;
    }

    public String getShort_phoneno() {
        return short_phoneno;
    }

    public void setShort_phoneno(String short_phoneno) {
        this.short_phoneno = short_phoneno;
    }

    public String getNameHelperPY() {
        return NameHelperPY;
    }

    public void setNameHelperPY(String NameHelperPY) {
        this.NameHelperPY = NameHelperPY;
    }

    public boolean isHR_IsLaborDispatch() {
        return HR_IsLaborDispatch;
    }

    public void setHR_IsLaborDispatch(boolean HR_IsLaborDispatch) {
        this.HR_IsLaborDispatch = HR_IsLaborDispatch;
    }

    public String getHR_LaborDispatchCompany() {
        return HR_LaborDispatchCompany;
    }

    public void setHR_LaborDispatchCompany(String HR_LaborDispatchCompany) {
        this.HR_LaborDispatchCompany = HR_LaborDispatchCompany;
    }

    public String getHR_Card() {
        return HR_Card;
    }

    public void setHR_Card(String HR_Card) {
        this.HR_Card = HR_Card;
    }

    public String getHR_IDName() {
        return HR_IDName;
    }

    public void setHR_IDName(String HR_IDName) {
        this.HR_IDName = HR_IDName;
    }

    public boolean isHR_IsOutSource() {
        return HR_IsOutSource;
    }

    public void setHR_IsOutSource(boolean HR_IsOutSource) {
        this.HR_IsOutSource = HR_IsOutSource;
    }

    public String getHR_OutSourceCompany() {
        return HR_OutSourceCompany;
    }

    public void setHR_OutSourceCompany(String HR_OutSourceCompany) {
        this.HR_OutSourceCompany = HR_OutSourceCompany;
    }

    public Object getHR_Residency() {
        return HR_Residency;
    }

    public void setHR_Residency(Object HR_Residency) {
        this.HR_Residency = HR_Residency;
    }

    public Object getHR_Level_ID() {
        return HR_Level_ID;
    }

    public void setHR_Level_ID(Object HR_Level_ID) {
        this.HR_Level_ID = HR_Level_ID;
    }

    public Object getHR_Position_ID() {
        return HR_Position_ID;
    }

    public void setHR_Position_ID(Object HR_Position_ID) {
        this.HR_Position_ID = HR_Position_ID;
    }

    public Object getHR_Level() {
        return HR_Level;
    }

    public void setHR_Level(Object HR_Level) {
        this.HR_Level = HR_Level;
    }

    public int getHR_IDX() {
        return HR_IDX;
    }

    public void setHR_IDX(int HR_IDX) {
        this.HR_IDX = HR_IDX;
    }

    public Object getHR_NX() {
        return HR_NX;
    }

    public void setHR_NX(Object HR_NX) {
        this.HR_NX = HR_NX;
    }

    public Object getHR_SpecialityX() {
        return HR_SpecialityX;
    }

    public void setHR_SpecialityX(Object HR_SpecialityX) {
        this.HR_SpecialityX = HR_SpecialityX;
    }

    public Object getHC_IDX() {
        return HC_IDX;
    }

    public void setHC_IDX(Object HC_IDX) {
        this.HC_IDX = HC_IDX;
    }

    public Object getDML_IDX() {
        return DML_IDX;
    }

    public void setDML_IDX(Object DML_IDX) {
        this.DML_IDX = DML_IDX;
    }

    public Object getAccSureX() {
        return AccSureX;
    }

    public void setAccSureX(Object AccSureX) {
        this.AccSureX = AccSureX;
    }

    public Object getHR_Work_TypeX() {
        return HR_Work_TypeX;
    }

    public void setHR_Work_TypeX(Object HR_Work_TypeX) {
        this.HR_Work_TypeX = HR_Work_TypeX;
    }

    public Object getHr_positionX() {
        return Hr_positionX;
    }

    public void setHr_positionX(Object Hr_positionX) {
        this.Hr_positionX = Hr_positionX;
    }

    public Object getDeptIDX() {
        return DeptIDX;
    }

    public void setDeptIDX(Object DeptIDX) {
        this.DeptIDX = DeptIDX;
    }

    public Object getHR_DormitoryX() {
        return HR_DormitoryX;
    }

    public void setHR_DormitoryX(Object HR_DormitoryX) {
        this.HR_DormitoryX = HR_DormitoryX;
    }

    public Object getDormitory_IDX() {
        return Dormitory_IDX;
    }

    public void setDormitory_IDX(Object Dormitory_IDX) {
        this.Dormitory_IDX = Dormitory_IDX;
    }

    public Object getRoom_IDX() {
        return Room_IDX;
    }

    public void setRoom_IDX(Object Room_IDX) {
        this.Room_IDX = Room_IDX;
    }

    public Object getHR_ResidencyX() {
        return HR_ResidencyX;
    }

    public void setHR_ResidencyX(Object HR_ResidencyX) {
        this.HR_ResidencyX = HR_ResidencyX;
    }

    public Object getHR_Level_IDX() {
        return HR_Level_IDX;
    }

    public void setHR_Level_IDX(Object HR_Level_IDX) {
        this.HR_Level_IDX = HR_Level_IDX;
    }

    public Object getHR_Position_IDX() {
        return HR_Position_IDX;
    }

    public void setHR_Position_IDX(Object HR_Position_IDX) {
        this.HR_Position_IDX = HR_Position_IDX;
    }

    public Object getHR_LevelX() {
        return HR_LevelX;
    }

    public void setHR_LevelX(Object HR_LevelX) {
        this.HR_LevelX = HR_LevelX;
    }
}
