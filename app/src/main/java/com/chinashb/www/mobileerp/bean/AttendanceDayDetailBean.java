package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/11/20 13:51
 * @author 作者: xxblwf
 * @description
 */

public class AttendanceDayDetailBean {


    /**
     * currentDate : 2020-11-20T00:00:00
     * Result : false
     * ErrorInfo :
     * type : {"code":0,"message":"正常"}
     * out : {"code":0,"message":"正常"}
     * status : {"code":0,"message":"正常"}
     * day_span : {"code":0,"message":"正常"}
     * address : 上海市嘉定区园贸路898号靠近胜华波集团(上海分公司)
     * remark :
     */

    @SerializedName("currentDate") private String currentDate;
    @SerializedName("Result") private boolean Result;
    @SerializedName("ErrorInfo") private String ErrorInfo;
    @SerializedName("type") private TypeBean type;
    @SerializedName("out") private OutBean out;
    @SerializedName("status") private StatusBean status;
    @SerializedName("day_span") private DaySpanBean daySpan;
    @SerializedName("address") private String address;
    @SerializedName("remark") private String remark;

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public boolean isResult() {
        return Result;
    }

    public void setResult(boolean Result) {
        this.Result = Result;
    }

    public String getErrorInfo() {
        return ErrorInfo;
    }

    public void setErrorInfo(String ErrorInfo) {
        this.ErrorInfo = ErrorInfo;
    }

    public TypeBean getType() {
        return type;
    }

    public void setType(TypeBean type) {
        this.type = type;
    }

    public OutBean getOut() {
        return out;
    }

    public void setOut(OutBean out) {
        this.out = out;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public DaySpanBean getDaySpan() {
        return daySpan;
    }

    public void setDaySpan(DaySpanBean daySpan) {
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

    public static class TypeBean {
        /**
         * code : 0
         * message : 正常
         */

        @SerializedName("code") private int code;
        @SerializedName("message") private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class OutBean {
        /**
         * code : 0
         * message : 正常
         */

        @SerializedName("code") private int code;
        @SerializedName("message") private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class StatusBean {
        /**
         * code : 0
         * message : 正常
         */

        @SerializedName("code") private int code;
        @SerializedName("message") private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class DaySpanBean {
        /**
         * code : 0
         * message : 正常
         */

        @SerializedName("code") private int code;
        @SerializedName("message") private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
