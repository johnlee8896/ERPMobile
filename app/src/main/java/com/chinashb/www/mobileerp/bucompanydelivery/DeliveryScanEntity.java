package com.chinashb.www.mobileerp.bucompanydelivery;

import java.io.Serializable;

/***
 * @date 创建时间 4/25/26 3:06 PM
 * @author 作者: liweifeng
 * @description
 */
public class DeliveryScanEntity implements Serializable {
    private String code;
    private String type;
    private String groupTitle;
    private String detail;
    private long time;

    public DeliveryScanEntity(String code, String type) {
        this(code, type, type, "");
    }

    public DeliveryScanEntity(String code, String type, String groupTitle, String detail) {
        this.code = code;
        this.type = type;
        this.groupTitle = groupTitle;
        this.detail = detail;
        this.time = System.currentTimeMillis();
    }

    public String getCode() { return code; }
    public String getType() { return type; }
    public String getGroupTitle() { return groupTitle; }
    public String getDetail() { return detail; }
    public long getTime() { return time; }
}
