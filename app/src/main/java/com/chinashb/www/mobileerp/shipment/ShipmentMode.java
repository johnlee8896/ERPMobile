package com.chinashb.www.mobileerp.shipment;

/***
 * @date 创建时间 2026/5/2 14:12
 * @author 作者: code-x John
 * @description 外贸发运扫码模式
 */
public enum ShipmentMode {
    SHIPMENT_ATTACH_CONTAINER("发运单装集装箱", "发运单", "集装箱", "装入"),
    SHIPMENT_DETACH_CONTAINER("发运单移出集装箱", "发运单", "集装箱", "移出"),
    CONTAINER_ATTACH_PALLET("集装箱装托盘", "集装箱", "托盘", "装入"),
    CONTAINER_DETACH_PALLET("集装箱移出托盘", "集装箱", "托盘", "移出"),
    PALLET_ADD_ITEM("托盘装物料", "托盘", "物料", "加入"),
    PALLET_REMOVE_ITEM("托盘移出物料", "托盘", "物料", "移出");

    private final String modeTitle;
    private final String primaryName;
    private final String secondaryName;
    private final String actionName;

    ShipmentMode(String modeTitle, String primaryName, String secondaryName, String actionName) {
        this.modeTitle = modeTitle;
        this.primaryName = primaryName;
        this.secondaryName = secondaryName;
        this.actionName = actionName;
    }

    public String getModeTitle() {
        return modeTitle;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public String getSecondaryName() {
        return secondaryName;
    }

    public String getActionName() {
        return actionName;
    }

    public String getWaitingPrimaryHint() {
        return "请先扫描并校验" + primaryName + "标签";
    }

    public String getWaitingSecondaryHint() {
        return "请继续扫描" + secondaryName + "标签执行" + actionName;
    }

    public String getRelationGroupTitle() {
        return primaryName + "关联列表";
    }
}
