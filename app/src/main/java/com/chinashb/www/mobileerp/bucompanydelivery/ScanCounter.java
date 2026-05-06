package com.chinashb.www.mobileerp.bucompanydelivery;

/***
 * @date 创建时间 4/25/26 3:00 PM
 * @author 作者: liweifeng
 * @description
 */
public class ScanCounter {

    public static String buildStatus(
            ScanBusinessDispatcher.ScanType currentType,
            String currentTargetCode,
            int currentChildCount,
            int palletCount,
            int containerCount,
            int deliveryCount
    ) {
        String modeName;
        String targetName;
        String childName;
        String hint;

        switch (currentType) {
            case TRAY:
                modeName = "托盘装货";
                targetName = "当前托盘";
                childName = "本托盘货码";
                hint = currentTargetCode == null || currentTargetCode.length() == 0
                        ? "先扫托盘码，再连续扫货码"
                        : "继续扫货码，重扫托盘码可切换当前托盘";
                break;
            case CONTAINER:
                modeName = "集装箱装托盘";
                targetName = "当前集装箱";
                childName = "本集装箱托盘";
                hint = currentTargetCode == null || currentTargetCode.length() == 0
                        ? "先扫集装箱码，再连续扫托盘码"
                        : "继续扫托盘码，重扫集装箱码可切换当前集装箱";
                break;
            case DELIVERY_ORDER:
            default:
                modeName = "发货单装集装箱";
                targetName = "当前发货单";
                childName = "本发货单集装箱";
                hint = currentTargetCode == null || currentTargetCode.length() == 0
                        ? "先扫发货单码，再连续扫集装箱码"
                        : "继续扫集装箱码，切换发货单请先点重置";
                break;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("当前模式：").append(modeName).append("\n");
        sb.append(targetName).append("：")
                .append(currentTargetCode == null || currentTargetCode.length() == 0 ? "未选择" : currentTargetCode)
                .append("\n");
        sb.append(childName).append("：").append(currentChildCount)
                .append("    托盘：").append(palletCount)
                .append("    集装箱：").append(containerCount)
                .append("    发货单：").append(deliveryCount)
                .append("\n");
        sb.append("提示：").append(hint);
        return sb.toString();
    }
}

