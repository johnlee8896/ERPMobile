package com.chinashb.www.mobileerp.shipment;

import java.io.Serializable;

/***
 * @date 创建时间 2026/5/21 10:28
 * @author 作者: code-x John
 * @description 外贸托盘扫码解析包装信息
 */
public class TradePalletScanBoxBean implements Serializable {
    private long DIII_ID;
    private int BoxLevel;
    private String BoxLevelName;
    private long SMLI_ID;
    private long SMM_ID;
    private long SMT_ID;
    private long Parent_SMM_ID;
    private long Parent_SMT_ID;
    private long SourceDII_ID;
    private double Qty;
    private long Item_ID;
    private long IV_ID;

    public long getDIII_ID() {
        return DIII_ID;
    }

    public void setDIII_ID(long DIII_ID) {
        this.DIII_ID = DIII_ID;
    }

    public int getBoxLevel() {
        return BoxLevel;
    }

    public void setBoxLevel(int boxLevel) {
        BoxLevel = boxLevel;
    }

    public String getBoxLevelName() {
        return BoxLevelName;
    }

    public void setBoxLevelName(String boxLevelName) {
        BoxLevelName = boxLevelName;
    }

    public long getSMLI_ID() {
        return SMLI_ID;
    }

    public void setSMLI_ID(long SMLI_ID) {
        this.SMLI_ID = SMLI_ID;
    }

    public long getSMM_ID() {
        return SMM_ID;
    }

    public void setSMM_ID(long SMM_ID) {
        this.SMM_ID = SMM_ID;
    }

    public long getSMT_ID() {
        return SMT_ID;
    }

    public void setSMT_ID(long SMT_ID) {
        this.SMT_ID = SMT_ID;
    }

    public long getParent_SMM_ID() {
        return Parent_SMM_ID;
    }

    public void setParent_SMM_ID(long parent_SMM_ID) {
        Parent_SMM_ID = parent_SMM_ID;
    }

    public long getParent_SMT_ID() {
        return Parent_SMT_ID;
    }

    public void setParent_SMT_ID(long parent_SMT_ID) {
        Parent_SMT_ID = parent_SMT_ID;
    }

    public long getSourceDII_ID() {
        return SourceDII_ID;
    }

    public void setSourceDII_ID(long sourceDII_ID) {
        SourceDII_ID = sourceDII_ID;
    }

    public double getQty() {
        return Qty;
    }

    public void setQty(double qty) {
        Qty = qty;
    }

    public long getItem_ID() {
        return Item_ID;
    }

    public void setItem_ID(long item_ID) {
        Item_ID = item_ID;
    }

    public long getIV_ID() {
        return IV_ID;
    }

    public void setIV_ID(long IV_ID) {
        this.IV_ID = IV_ID;
    }
}
