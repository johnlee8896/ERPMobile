package com.chinashb.www.mobileerp.shipment;

/***
 * @date 创建时间 2026/5/2 14:12
 * @author 作者: code-x John
 * @description RecyclerView显示模型
 */
public class ShipmentDisplayEntity {

    private String groupTitle;
    private String code;
    private String detail;

    public ShipmentDisplayEntity(String groupTitle, String code, String detail) {
        this.groupTitle = groupTitle;
        this.code = code;
        this.detail = detail;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
