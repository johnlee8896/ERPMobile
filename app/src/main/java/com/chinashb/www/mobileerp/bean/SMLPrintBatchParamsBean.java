package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/***
 * @date 创建时间 6/5/26 3:44 PM
 * @author 作者: liweifeng
 * @description 根据sml,生产记录获取相关打印数据的bean
 */
public class SMLPrintBatchParamsBean {

    /**
     * SML_ID : 4176254
     * SMLICount : 16
     * SMMCount : 0
     * SMTCount : 2
     * SMLI_IDList : [46158851,46158852,46158853,46158854,46158855,46158856,46158857,46158858,46158859,46158860,46158861,46158862,46158863,46158864,46158865,46158866]
     * SMM_IDList : [46158864,46158864,46158865]
     * SMT_IDList : [3114207,3114208]
     * TotalSetCount : 18
     * OP : Prepare
     */

    @SerializedName("SML_ID") private int SMLID;
    @SerializedName("SMLICount") private int SMLICount;
    @SerializedName("SMMCount") private int SMMCount;
    @SerializedName("SMTCount") private int SMTCount;
    @SerializedName("TotalSetCount") private int TotalSetCount;
    @SerializedName("OP") private String OP;
    @SerializedName("SMLI_IDList") private List<Integer> SMLIIDList;
    @SerializedName("SMM_IDList") private List<Integer> SMMIDList;
    @SerializedName("SMT_IDList") private List<Integer> SMTIDList;

    public int getSMLID() {
        return SMLID;
    }

    public void setSMLID(int SMLID) {
        this.SMLID = SMLID;
    }

    public int getSMLICount() {
        return SMLICount;
    }

    public void setSMLICount(int SMLICount) {
        this.SMLICount = SMLICount;
    }

    public int getSMMCount() {
        return SMMCount;
    }

    public void setSMMCount(int SMMCount) {
        this.SMMCount = SMMCount;
    }

    public int getSMTCount() {
        return SMTCount;
    }

    public void setSMTCount(int SMTCount) {
        this.SMTCount = SMTCount;
    }

    public int getTotalSetCount() {
        return TotalSetCount;
    }

    public void setTotalSetCount(int TotalSetCount) {
        this.TotalSetCount = TotalSetCount;
    }

    public String getOP() {
        return OP;
    }

    public void setOP(String OP) {
        this.OP = OP;
    }

    public List<Integer> getSMLIIDList() {
        return SMLIIDList;
    }

    public void setSMLIIDList(List<Integer> SMLIIDList) {
        this.SMLIIDList = SMLIIDList;
    }

    public List<Integer> getSMMIDList() {
        return SMMIDList;
    }

    public void setSMMIDList(List<Integer> SMMIDList) {
        this.SMMIDList = SMMIDList;
    }

    public List<Integer> getSMTIDList() {
        return SMTIDList;
    }

    public void setSMTIDList(List<Integer> SMTIDList) {
        this.SMTIDList = SMTIDList;
    }
}
