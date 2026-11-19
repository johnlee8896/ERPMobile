package com.chinashb.www.mobileerp.funs;

import android.text.TextUtils;

/**
 * codex by john 2026-11-19 - 旧成品托盘码按固定标记精确解析
 */
public final class ProductPalletCodeParser {
    public static class PalletCode {
        public final int boxId;
        public final String codeNoBox;
        public final String palletPartNo;
        public final String actualBoxQty;

        private PalletCode(int boxId, String codeNoBox, String palletPartNo,
                           String actualBoxQty) {
            this.boxId = boxId;
            this.codeNoBox = codeNoBox;
            this.palletPartNo = palletPartNo;
            this.actualBoxQty = actualBoxQty;
        }
    }

    private ProductPalletCodeParser() {
    }

    public static PalletCode parse(String content) {
        if (TextUtils.isEmpty(content) || !content.startsWith("Pallet")) {
            return null;
        }

        String prefix = "Pallet/";
        int noIndex = content.indexOf("/No/");
        int partNoIndex = content.indexOf("/PartNo/");
        int qIndex = content.indexOf("/Q/");
        if (noIndex <= prefix.length() || partNoIndex <= noIndex || qIndex <= partNoIndex) {
            return null;
        }

        try {
            String boxText = content.substring(prefix.length(), noIndex);
            String codeNoBox = content.substring(noIndex + "/No/".length(), partNoIndex);
            String palletPartNo = content.substring(
                    partNoIndex + "/PartNo/".length(), qIndex);
            String actualBoxQty = content.substring(qIndex + "/Q/".length()).trim();
            int boxId = Integer.parseInt(boxText.trim());
            return new PalletCode(boxId, codeNoBox, palletPartNo, actualBoxQty);
        } catch (Exception e) {
            return null;
        }
    }
}
