package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2021/1/12 15:38
 * @author 作者: xxblwf
 * @description 任务单的Bean
 */

public class TaskBean implements Parcelable {

    /**
     * TTP_ID : 0
     * New : false
     * TaskTitle : 工伤级别统计
     * TID : 21915
     * TVID : 57324
     * Create_Time : 2018-06-14 11:07:01
     * Start_Time : 2018-06-14
     * End_Time : 2018-06-14
     * Creater_HRID : 5780
     * CreaterName : 刘莉
     * Responder : 三
     * Auditor : 李
     * Percentage : 1.0
     * Remark :
     * LastAccessTime :
     */

    @SerializedName("TTP_ID") private int TTPID;
    @SerializedName("New") private boolean New;
    @SerializedName("TaskTitle") private String TaskTitle;
    @SerializedName("TID") private int TID;
    @SerializedName("TVID") private int TVID;
    @SerializedName("Create_Time") private String CreateTime;
    @SerializedName("Start_Time") private String StartTime;
    @SerializedName("End_Time") private String EndTime;
    @SerializedName("Creater_HRID") private int CreaterHRID;
    @SerializedName("CreaterName") private String CreaterName;
    @SerializedName("Responder") private String Responder;
    @SerializedName("Auditor") private String Auditor;
    @SerializedName("Percentage") private double Percentage;
    @SerializedName("Remark") private String Remark;
    //// TODO: 2021/1/12  最近访问
    @SerializedName("LastAccessTime") private String LastAccessTime;

    protected TaskBean(Parcel in) {
        TTPID = in.readInt();
        New = in.readByte() != 0;
        TaskTitle = in.readString();
        TID = in.readInt();
        TVID = in.readInt();
        CreateTime = in.readString();
        StartTime = in.readString();
        EndTime = in.readString();
        CreaterHRID = in.readInt();
        CreaterName = in.readString();
        Responder = in.readString();
        Auditor = in.readString();
        Percentage = in.readDouble();
        Remark = in.readString();
    }

    public static final Creator<TaskBean> CREATOR = new Creator<TaskBean>() {
        @Override
        public TaskBean createFromParcel(Parcel in) {
            return new TaskBean(in);
        }

        @Override
        public TaskBean[] newArray(int size) {
            return new TaskBean[size];
        }
    };

    public int getTTPID() {
        return TTPID;
    }

    public void setTTPID(int TTPID) {
        this.TTPID = TTPID;
    }

    public boolean isNew() {
        return New;
    }

    public void setNew(boolean New) {
        this.New = New;
    }

    public String getTaskTitle() {
        return TaskTitle;
    }

    public void setTaskTitle(String TaskTitle) {
        this.TaskTitle = TaskTitle;
    }

    public int getTID() {
        return TID;
    }

    public void setTID(int TID) {
        this.TID = TID;
    }

    public int getTVID() {
        return TVID;
    }

    public void setTVID(int TVID) {
        this.TVID = TVID;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public int getCreaterHRID() {
        return CreaterHRID;
    }

    public void setCreaterHRID(int CreaterHRID) {
        this.CreaterHRID = CreaterHRID;
    }

    public String getCreaterName() {
        return CreaterName;
    }

    public void setCreaterName(String CreaterName) {
        this.CreaterName = CreaterName;
    }

    public String getResponder() {
        return Responder;
    }

    public void setResponder(String Responder) {
        this.Responder = Responder;
    }

    public String getAuditor() {
        return Auditor;
    }

    public void setAuditor(String Auditor) {
        this.Auditor = Auditor;
    }

    public double getPercentage() {
        return Percentage;
    }

    public void setPercentage(double Percentage) {
        this.Percentage = Percentage;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

//    public String get最近访问() {
//        return 最近访问;
//    }
//
//    public void set最近访问(String 最近访问) {
//        this.最近访问 = 最近访问;
//    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(TTPID);
        dest.writeByte((byte) (New ? 1 : 0));
        dest.writeString(TaskTitle);
        dest.writeInt(TID);
        dest.writeInt(TVID);
        dest.writeString(CreateTime);
        dest.writeString(StartTime);
        dest.writeString(EndTime);
        dest.writeInt(CreaterHRID);
        dest.writeString(CreaterName);
        dest.writeString(Responder);
        dest.writeString(Auditor);
        dest.writeDouble(Percentage);
        dest.writeString(Remark);
    }
}
