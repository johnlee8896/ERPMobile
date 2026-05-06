package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/***
 * @date 创建时间 2026/5/2 15:06
 * @author 作者: code-x John
 * @description 仓库主页按钮权限
 *
 * JSON结果示例:
 * [
 *   {
 *     "HR_ID": 26009,
 *     "ModuleCode": "PART",
 *     "MenuCode": "PART_STOCK_IN",
 *     "MenuTitle": "扫描入库",
 *     "Permit": true,
 *     "SortNo": 10
 *   },
 *   {
 *     "HR_ID": 26009,
 *     "ModuleCode": "PART",
 *     "MenuCode": "PART_FREEZE",
 *     "MenuTitle": "冻结库存",
 *     "Permit": false,
 *     "SortNo": 40
 *   }
 * ]
 *
 * 建议接口字段格式:
 * HR_ID:int
 * ModuleCode:string 例如 PART / PRODUCT
 * MenuCode:string 按钮唯一编码
 * MenuTitle:string 按钮标题
 * Permit:boolean 是否允许显示
 * SortNo:int 排序
 */
public class WarehouseMenuPermissionBean implements Serializable {

    @SerializedName("HR_ID")
    private int HR_ID;
    @SerializedName("ModuleCode")
    private String ModuleCode;
    @SerializedName("MenuCode")
    private String MenuCode;
    @SerializedName("MenuTitle")
    private String MenuTitle;
    @SerializedName("Permit")
    private boolean Permit;
    @SerializedName("SortNo")
    private int SortNo;

    public int getHR_ID() {
        return HR_ID;
    }

    public void setHR_ID(int HR_ID) {
        this.HR_ID = HR_ID;
    }

    public String getModuleCode() {
        return ModuleCode;
    }

    public void setModuleCode(String moduleCode) {
        ModuleCode = moduleCode;
    }

    public String getMenuCode() {
        return MenuCode;
    }

    public void setMenuCode(String menuCode) {
        MenuCode = menuCode;
    }

    public String getMenuTitle() {
        return MenuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        MenuTitle = menuTitle;
    }

    public boolean isPermit() {
        return Permit;
    }

    public void setPermit(boolean permit) {
        Permit = permit;
    }

    public int getSortNo() {
        return SortNo;
    }

    public void setSortNo(int sortNo) {
        SortNo = sortNo;
    }
}
