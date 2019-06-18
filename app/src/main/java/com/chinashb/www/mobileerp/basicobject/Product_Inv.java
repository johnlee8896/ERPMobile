package com.chinashb.www.mobileerp.basicobject;

import java.io.Serializable;

public class Product_Inv implements Serializable {
    /**
     * PS_ID : 5845
     * Item_ID : 16967
     * 产品通称 : F-GMX322.V-LA-1006941X.M
     * 名称 : 抬高电机总成（带记忆）
     * 客户零件编号 : 1006941X
     * 版本 : 05
     * 在产 : true
     * 最新版本 : 05
     * 销售客户 : 佛吉亚Bradford工厂,佛吉亚Sterling heights 工厂
     * 库存 : 63
     * 超三个月库存 : 63
     * 过期库存 : 63
     * 冻结库存 : null
     * 备品数 : null
     * 存储区域 : 待发区 171023/171024 63
     * 最近三个月有出入库记录 : null
     */

    private int PS_ID;
    private int Item_ID;
    private String 产品通称;
    private String 名称;
    private String 客户零件编号;
    private String 版本;
    private boolean 在产;
    private String 最新版本;
    private String 销售客户;
    private int 库存;
    private int 超三个月库存;
    private int 过期库存;
    private Object 冻结库存;
    private Object 备品数;
    private String 存储区域;
    private Object 最近三个月有出入库记录;

    public int getPS_ID() {
        return PS_ID;
    }

    public void setPS_ID(int PS_ID) {
        this.PS_ID = PS_ID;
    }

    public int getItem_ID() {
        return Item_ID;
    }

    public void setItem_ID(int Item_ID) {
        this.Item_ID = Item_ID;
    }

    public String get产品通称() {
        return 产品通称;
    }

    public void set产品通称(String 产品通称) {
        this.产品通称 = 产品通称;
    }

    public String get名称() {
        return 名称;
    }

    public void set名称(String 名称) {
        this.名称 = 名称;
    }

    public String get客户零件编号() {
        return 客户零件编号;
    }

    public void set客户零件编号(String 客户零件编号) {
        this.客户零件编号 = 客户零件编号;
    }

    public String get版本() {
        return 版本;
    }

    public void set版本(String 版本) {
        this.版本 = 版本;
    }

    public boolean is在产() {
        return 在产;
    }

    public void set在产(boolean 在产) {
        this.在产 = 在产;
    }

    public String get最新版本() {
        return 最新版本;
    }

    public void set最新版本(String 最新版本) {
        this.最新版本 = 最新版本;
    }

    public String get销售客户() {
        return 销售客户;
    }

    public void set销售客户(String 销售客户) {
        this.销售客户 = 销售客户;
    }

    public int get库存() {
        return 库存;
    }

    public void set库存(int 库存) {
        this.库存 = 库存;
    }

    public int get超三个月库存() {
        return 超三个月库存;
    }

    public void set超三个月库存(int 超三个月库存) {
        this.超三个月库存 = 超三个月库存;
    }

    public int get过期库存() {
        return 过期库存;
    }

    public void set过期库存(int 过期库存) {
        this.过期库存 = 过期库存;
    }

    public Object get冻结库存() {
        return 冻结库存;
    }

    public void set冻结库存(Object 冻结库存) {
        this.冻结库存 = 冻结库存;
    }

    public Object get备品数() {
        return 备品数;
    }

    public void set备品数(Object 备品数) {
        this.备品数 = 备品数;
    }

    public String get存储区域() {
        return 存储区域;
    }

    public void set存储区域(String 存储区域) {
        this.存储区域 = 存储区域;
    }

    public Object get最近三个月有出入库记录() {
        return 最近三个月有出入库记录;
    }

    public void set最近三个月有出入库记录(Object 最近三个月有出入库记录) {
        this.最近三个月有出入库记录 = 最近三个月有出入库记录;
    }
}
