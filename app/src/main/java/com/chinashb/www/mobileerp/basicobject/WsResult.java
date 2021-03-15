package com.chinashb.www.mobileerp.basicobject;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class WsResult implements Serializable {
    private boolean result;
    private String ErrorInfo;
    private Long ID;
    private JsonObject jsonObject;
    private String HR_NO;
    private String HR_IDCardNO;

    public String getHR_IDCardNO() {
        return HR_IDCardNO;
    }

    public void setHR_IDCardNO(String HR_IDCardNO) {
        this.HR_IDCardNO = HR_IDCardNO;
    }

    public String getHR_NO() {
        return HR_NO;
    }

    public void setHR_NO(String HR_NO) {
        this.HR_NO = HR_NO;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorInfo() {
        return ErrorInfo;
    }

    public void setErrorInfo(String ErrorInfo) {
        this.ErrorInfo = ErrorInfo;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

}
