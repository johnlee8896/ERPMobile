package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 盘点范围草稿作业 Bean。
 */
public class CountSessionBean {
    @SerializedName("CountSession_ID")
    private int CountSession_ID;
    @SerializedName("CountSession_Name")
    private String CountSession_Name;
    @SerializedName("CheckDate")
    private String CheckDate;
    @SerializedName("CountCategory")
    private int CountCategory;
    @SerializedName("SessionStatus")
    private int SessionStatus;
    @SerializedName("ScopeEditable")
    private boolean ScopeEditable;
    @SerializedName("ScopeStatusName")
    private String ScopeStatusName;
    @SerializedName("SelectedLocationCount")
    private int SelectedLocationCount;

    public int getCountSession_ID() {
        return CountSession_ID;
    }

    public void setCountSession_ID(int countSession_ID) {
        CountSession_ID = countSession_ID;
    }

    public String getCountSession_Name() {
        return CountSession_Name;
    }

    public void setCountSession_Name(String countSession_Name) {
        CountSession_Name = countSession_Name;
    }

    public String getCheckDate() {
        return CheckDate;
    }

    public void setCheckDate(String checkDate) {
        CheckDate = checkDate;
    }

    public int getCountCategory() {
        return CountCategory;
    }

    public void setCountCategory(int countCategory) {
        CountCategory = countCategory;
    }

    public int getSessionStatus() {
        return SessionStatus;
    }

    public void setSessionStatus(int sessionStatus) {
        SessionStatus = sessionStatus;
    }

    public boolean isScopeEditable() {
        return ScopeEditable;
    }

    public void setScopeEditable(boolean scopeEditable) {
        ScopeEditable = scopeEditable;
    }

    public String getScopeStatusName() {
        return ScopeStatusName;
    }

    public void setScopeStatusName(String scopeStatusName) {
        ScopeStatusName = scopeStatusName;
    }

    public int getSelectedLocationCount() {
        return SelectedLocationCount;
    }

    public void setSelectedLocationCount(int selectedLocationCount) {
        SelectedLocationCount = selectedLocationCount;
    }

    public String getFormattedCheckDate() {
        if (CheckDate == null || CheckDate.isEmpty()) {
            return "";
        }
        try {
            String value = CheckDate.replace("/Date(", "").replace(")/", "");
            int plus = value.indexOf('+');
            int minus = value.indexOf('-');
            int cut = -1;
            if (plus >= 0) {
                cut = plus;
            } else if (minus >= 0) {
                cut = minus;
            }
            if (cut >= 0) {
                value = value.substring(0, cut);
            }
            long millis = Long.parseLong(value);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            return sdf.format(new Date(millis));
        } catch (Exception e) {
            return CheckDate;
        }
    }
}
