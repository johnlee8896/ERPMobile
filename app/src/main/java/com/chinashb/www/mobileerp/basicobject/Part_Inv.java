package com.chinashb.www.mobileerp.basicobject;

import java.io.Serializable;

public class Part_Inv implements Serializable {
    /**
     * Item_ID : 3861
     * 物料编码 : P-ZD1360CE-3003B
     * 名称 : (灰色）调节螺钉
     * 规格 : M10
     * 单位 : 个
     * 质保天数 : null
     * 库存 : 7000.0
     * 最高安全库存 : null
     * 最低安全库存 : null
     * 超三个月库存 : 7000.0
     * 接近过期库存 : null
     * 过期库存 : 7000.0
     * 不合格数 : null
     * 备品数 : null
     * 存储区域 : 9#I 20170401 7,000
     * IV_ID : 14638
     * 版本 : B3 7,000
     * 最近三个月有出入库记录 : null
     */

    private int Item_ID;

    /**
     * Item : P-ZD1360CE-3003B
     * Item_Name : (灰色）调节螺钉
     * Item_Spec2 : M10
     * Item_Unit : 个
     * Inv : 7000.0
     * 最近三个月有出入库记录 : null
     */

    private String Item;
    private String Item_Name;
    private String Item_Spec2;
    private String Item_Unit;
    private double Inv;

    public int getItem_ID() {
        return Item_ID;
    }

    public void setItem_ID(int Item_ID) {
        this.Item_ID = Item_ID;
    }


    public String getItem() {
        return Item;
    }

    public void setItem(String Item) {
        this.Item = Item;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String Item_Name) {
        this.Item_Name = Item_Name;
    }

    public String getItem_Spec2() {
        return Item_Spec2;
    }

    public void setItem_Spec2(String Item_Spec2) {
        this.Item_Spec2 = Item_Spec2;
    }

    public String getItem_Unit() {
        return Item_Unit;
    }

    public void setItem_Unit(String Item_Unit) {
        this.Item_Unit = Item_Unit;
    }

    public double getInv() {
        return Inv;
    }

    public void setInv(double Inv) {
        this.Inv = Inv;
    }
}
