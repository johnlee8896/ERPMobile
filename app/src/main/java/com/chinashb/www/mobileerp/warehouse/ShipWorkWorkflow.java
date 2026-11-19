package com.chinashb.www.mobileerp.warehouse;

import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkDetail;
import com.chinashb.www.mobileerp.bean.ShipWorkModels.WorkHeader;
import com.chinashb.www.mobileerp.bean.ShipWorkPalletBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 发货作业恢复与页面跳转规则。
 */
public final class ShipWorkWorkflow {

    public enum Step {
        INVALID,
        FIFO,
        PREPARE,
        OQC,
        SHIP_SCAN,
        POST,
        COMPLETED
    }

    private ShipWorkWorkflow() {
    }

    public static WorkHeader header(WorkDetail detail) {
        return detail == null || detail.work == null || detail.work.isEmpty() ? null : detail.work.get(0);
    }

    public static List<ShipWorkPalletBean> activePallets(WorkDetail detail) {
        List<ShipWorkPalletBean> result = new ArrayList<>();
        if (detail == null || detail.pallets == null) {
            return result;
        }
        for (ShipWorkPalletBean pallet : detail.pallets) {
            if (!isExcluded(pallet.getPallet_Status())) {
                result.add(pallet);
            }
        }
        return result;
    }

    public static boolean isExcluded(String status) {
        return "Returned".equalsIgnoreCase(status)
                || "Released".equalsIgnoreCase(status)
                || "Posted".equalsIgnoreCase(status);
    }

    public static int countStatus(List<ShipWorkPalletBean> pallets, String status) {
        int count = 0;
        for (ShipWorkPalletBean pallet : pallets) {
            if (status.equalsIgnoreCase(pallet.getPallet_Status())) {
                count++;
            }
        }
        return count;
    }

    public static boolean isOqcPassed(WorkHeader header) {
        if (header == null || header.oqcResult == null) {
            return false;
        }
        String result = header.oqcResult.trim();
        return "Pass".equalsIgnoreCase(result)
                || "true".equalsIgnoreCase(result)
                || "1".equals(result)
                || "Y".equalsIgnoreCase(result);
    }

    public static Step nextStep(WorkDetail detail) {
        WorkHeader header = header(detail);
        if (header == null) {
            return Step.INVALID;
        }
        if ("Posted".equalsIgnoreCase(header.workStatus) || !header.isActive) {
            return Step.COMPLETED;
        }

        List<ShipWorkPalletBean> active = activePallets(detail);
        if (active.isEmpty()) {
            return Step.FIFO;
        }
        if (countStatus(active, "Locked") > 0 || countStatus(active, "PickScanned") > 0) {
            return Step.PREPARE;
        }
        if (!isOqcPassed(header)) {
            return Step.OQC;
        }
        int shipScanned = countStatus(active, "ShipScanned");
        if (shipScanned == active.size()) {
            return Step.POST;
        }
        return Step.SHIP_SCAN;
    }

    public static String statusText(String status) {
        if (status == null) {
            return "待核对";
        }
        if ("Preparing".equalsIgnoreCase(status)) {
            return "备料中";
        }
        if ("Staging".equalsIgnoreCase(status)) {
            return "待发区处理中";
        }
        if ("OQC".equalsIgnoreCase(status)) {
            return "已终检";
        }
        if ("Loading".equalsIgnoreCase(status)) {
            return "发货复扫中";
        }
        if ("Ready".equalsIgnoreCase(status)) {
            return "待出库记账";
        }
        if ("Posted".equalsIgnoreCase(status)) {
            return "已发运";
        }
        return status;
    }

    public static String palletStatusText(String status) {
        if (status == null) {
            return "待核对";
        }
        if ("Locked".equalsIgnoreCase(status)) {
            return "已锁待备料";
        }
        if ("PickScanned".equalsIgnoreCase(status)) {
            return "已备料扫码";
        }
        if ("Staged".equalsIgnoreCase(status)) {
            return "已入待发区";
        }
        if ("OQCPassed".equalsIgnoreCase(status)) {
            return "OQC合格";
        }
        if ("OQCFailed".equalsIgnoreCase(status)) {
            return "OQC不合格";
        }
        if ("ShipScanned".equalsIgnoreCase(status)) {
            return "已装车复扫";
        }
        if ("Returned".equalsIgnoreCase(status)) {
            return "已撤回";
        }
        if ("Released".equalsIgnoreCase(status)) {
            return "已结清释放";
        }
        return status;
    }
}
