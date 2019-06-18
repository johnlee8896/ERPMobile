package com.chinashb.www.mobileerp.basicobject;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Ws_Result implements Serializable {
    private boolean result;
    private String ErrorInfo;
    private Long ID;
    private JsonObject jsonObject;

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
