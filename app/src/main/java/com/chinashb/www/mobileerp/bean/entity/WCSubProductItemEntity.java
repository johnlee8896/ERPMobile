package com.chinashb.www.mobileerp.bean.entity;

/***
 * @date 创建时间 2019/12/20 15:56
 * @author 作者: xxblwf
 * @description 物料展示的成品adapter对应的entity
 */

public class WCSubProductItemEntity {
    private WCSubProductEntity wcSubProductEntity;
    private float qty;
    private String buName;
    private String istName;
    private boolean select;
    private String lotNo;

    public String getLotNo() {
        return lotNo;
    }

    public void setLotNo(String lotNo) {
        this.lotNo = lotNo;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public WCSubProductEntity getWcSubProductEntity() {
        return wcSubProductEntity;
    }

    public void setWcSubProductEntity(WCSubProductEntity wcSubProductEntity) {
        this.wcSubProductEntity = wcSubProductEntity;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }

    public String getIstName() {
        return istName;
    }

    public void setIstName(String istName) {
        this.istName = istName;
    }
}
