package com.chinashb.www.mobileerp.basicobject;

/***
 * @date 创建时间 2019/7/24 10:30
 * @author 作者: xxblwf
 * @description 选择产线entity,用于dialog可多选
 */

public class WorkLineSelectEntity {
    private String workLinName;
    private boolean isSelect;

    public String getWorkLinName() {
        return workLinName;
    }

    public void setWorkLinName(String workLinName) {
        this.workLinName = workLinName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
