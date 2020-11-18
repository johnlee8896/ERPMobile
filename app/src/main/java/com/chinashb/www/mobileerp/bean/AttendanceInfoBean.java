package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/***
 * @date 创建时间 2018/7/10 1:50 PM
 * @author 作者: liweifeng
 * @description 考勤制度及我个人出勤
 */
public class AttendanceInfoBean implements Parcelable{

    /**
     * current_time : 1531201612174
     * name : 华瑞物联考勤组
     * org_id : 1002
     * org_name : 华瑞物联
     * longitude : 121.487329
     * latitude : 31.290570
     * scope : 100
     * address : 明道大厦
     * on_duty : 9:00
     * off_duty : 18:00
     * flexible : 1
     * flexible_time : 30
     * remark : 华瑞物联考勤组，弹性30分钟
     * attendances : [{"date":1531194029000,"type":{"code":0,"message":"上班"},"out":{"code":1,"message":"外勤"},"status":{"code":1,"message":"迟到"},"day_span":{"code":0,"message":"正常"},"address":"string","remark":"string"},{"date":1531194045000,"type":{"code":1,"message":"下班"},"out":{"code":1,"message":"外勤"},"status":{"code":2,"message":"早退"},"day_span":{"code":0,"message":"正常"},"address":"string","remark":"string"}]
     */

    @SerializedName("current_time") private long currentTime;
    @SerializedName("name") private String name;
    @SerializedName("org_id") private int orgId;
    @SerializedName("org_name") private String orgName;
    @SerializedName("longitude") private String longitude;
    @SerializedName("latitude") private String latitude;
    @SerializedName("scope") private int scope;
    @SerializedName("address") private String address;
    @SerializedName("on_duty") private String onDuty;
    @SerializedName("off_duty") private String offDuty;
    @SerializedName("flexible") private int flexible;
    @SerializedName("flexible_time") private int flexibleTime;
    @SerializedName("remark") private String remark;
    @SerializedName("attendances") private List<AttendancesBean> attendances;

    protected AttendanceInfoBean(Parcel in) {
        currentTime = in.readLong();
        name = in.readString();
        orgId = in.readInt();
        orgName = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        scope = in.readInt();
        address = in.readString();
        onDuty = in.readString();
        offDuty = in.readString();
        flexible = in.readInt();
        flexibleTime = in.readInt();
        remark = in.readString();
    }

    public static final Creator<AttendanceInfoBean> CREATOR = new Creator<AttendanceInfoBean>() {
        @Override
        public AttendanceInfoBean createFromParcel(Parcel in) {
            return new AttendanceInfoBean(in);
        }

        @Override
        public AttendanceInfoBean[] newArray(int size) {
            return new AttendanceInfoBean[size];
        }
    };

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOnDuty() {
        return onDuty;
    }

    public void setOnDuty(String onDuty) {
        this.onDuty = onDuty;
    }

    public String getOffDuty() {
        return offDuty;
    }

    public void setOffDuty(String offDuty) {
        this.offDuty = offDuty;
    }

    public int getFlexible() {
        return flexible;
    }

    public void setFlexible(int flexible) {
        this.flexible = flexible;
    }

    public int getFlexibleTime() {
        return flexibleTime;
    }

    public void setFlexibleTime(int flexibleTime) {
        this.flexibleTime = flexibleTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<AttendancesBean> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendancesBean> attendances) {
        this.attendances = attendances;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(currentTime);
        dest.writeString(name);
        dest.writeInt(orgId);
        dest.writeString(orgName);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeInt(scope);
        dest.writeString(address);
        dest.writeString(onDuty);
        dest.writeString(offDuty);
        dest.writeInt(flexible);
        dest.writeInt(flexibleTime);
        dest.writeString(remark);
    }

    public static class AttendancesBean implements Parcelable{
        /**
         * date : 1531194029000
         * type : {"code":0,"message":"上班"}
         * out : {"code":1,"message":"外勤"}
         * status : {"code":1,"message":"迟到"}
         * day_span : {"code":0,"message":"正常"}
         * address : string
         * remark : string
         */

        @SerializedName("date") private long date;
        @SerializedName("type") private StatusBean type;
        @SerializedName("out") private StatusBean out;
        @SerializedName("status") private StatusBean status;
        @SerializedName("day_span") private StatusBean daySpan;
        @SerializedName("address") private String address;
        @SerializedName("remark") private String remark;

        protected AttendancesBean(Parcel in) {
            date = in.readLong();
            address = in.readString();
            remark = in.readString();
        }

        public static final Creator<AttendancesBean> CREATOR = new Creator<AttendancesBean>() {
            @Override
            public AttendancesBean createFromParcel(Parcel in) {
                return new AttendancesBean(in);
            }

            @Override
            public AttendancesBean[] newArray(int size) {
                return new AttendancesBean[size];
            }
        };

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public StatusBean getType() {
            return type;
        }

        public void setType(StatusBean type) {
            this.type = type;
        }

        public StatusBean getOut() {
            return out;
        }

        public void setOut(StatusBean out) {
            this.out = out;
        }

        public StatusBean getStatus() {
            return status;
        }

        public void setStatus(StatusBean status) {
            this.status = status;
        }

        public StatusBean getDaySpan() {
            return daySpan;
        }

        public void setDaySpan(StatusBean daySpan) {
            this.daySpan = daySpan;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(date);
            dest.writeString(address);
            dest.writeString(remark);
        }




    }
}
