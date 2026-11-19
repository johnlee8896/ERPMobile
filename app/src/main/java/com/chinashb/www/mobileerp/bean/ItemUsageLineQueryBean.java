package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/**
 * codex by john 2026-11-12 - 物料使用线别查询 Bean
 */
public class ItemUsageLineQueryBean {
    @SerializedName("产线") private String lineName;
    @SerializedName("白班需求数") private String dayShiftQty;
    @SerializedName("夜班需求数") private String nightShiftQty;
    @SerializedName("合计需求数") private String totalQty;
    @SerializedName("需求计划数") private String planCount;

    public String getLineName() {
        return lineName;
    }

    public String getDayShiftQty() {
        return dayShiftQty;
    }

    public String getNightShiftQty() {
        return nightShiftQty;
    }

    public String getTotalQty() {
        return totalQty;
    }

    public String getPlanCount() {
        return planCount;
    }
}
