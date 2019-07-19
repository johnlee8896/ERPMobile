package com.chinashb.www.mobileerp.libapi.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/***
 *@date 创建时间 2018/5/7 18:46
 *@author 作者: YuLong
 *@description 所有Bean的基类
 */
public class BaseBean<T> implements Serializable {
    @SerializedName("code") private int code = -1;
    @SerializedName("message") private String message;
    @SerializedName("data") private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
