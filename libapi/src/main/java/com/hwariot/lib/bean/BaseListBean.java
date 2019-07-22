package com.hwariot.lib.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 *@date 创建时间 2018/5/7 18:47
 *@author 作者: YuLong
 *@description 所有List的基类
 */
public class BaseListBean<T> implements Serializable {
    @SerializedName("count") private int count;
    @SerializedName("next") private String next;
    @SerializedName("previous") private String previous;
    @SerializedName("results") private ArrayList<T> results;


    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(ArrayList<T> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "BaseListBean{" +
                "count=" + count +
                ", results=" + results +
                '}';
    }

}
