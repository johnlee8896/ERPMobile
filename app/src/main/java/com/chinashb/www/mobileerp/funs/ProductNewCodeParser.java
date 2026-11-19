package com.chinashb.www.mobileerp.funs;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * codex by john 2026-11-19 - 新版成品标签短链接统一解析
 */
public final class ProductNewCodeParser {
    public interface Callback {
        void onResolved(String legacyCode);

        void onError(String message);
    }

    private ProductNewCodeParser() {
    }

    public static boolean isNewProductCode(String code) {
        if (TextUtils.isEmpty(code)) {
            return false;
        }
        String value = code.trim();
        return value.startsWith("https://shbeos.shbgroup.com")
                && value.contains("/p/");
    }

    public static void resolve(final String rawCode, final Callback callback) {
        // 只在 isNewProductCode=true 时调用；Pallet 开头由旧本地解析器处理。
        new AsyncTask<Void, Void, ResolveResult>() {
            @Override
            protected ResolveResult doInBackground(Void... voids) {
                WsResult result;
                try {
                    result = WebServiceUtil.op_Product_New_Code_Pase_Pallet(
                            UserSingleton.get().getUserInfo().getBu_ID(),
                            UserSingleton.get().getHRID(),
                            rawCode);
                } catch (Exception e) {
                    return ResolveResult.failure("新标签解析失败");
                }
                if (result == null || !result.getResult()) {
                    return ResolveResult.failure(result == null
                            ? "新标签解析失败" : error(result));
                }
                String legacyCode = buildLegacyCode(result);
                if (TextUtils.isEmpty(legacyCode) || isNewProductCode(legacyCode)) {
                    return ResolveResult.failure("新标签解析结果为空");
                }
                return ResolveResult.success(legacyCode);
            }

            @Override
            protected void onPostExecute(ResolveResult resolveResult) {
                if (callback == null) {
                    return;
                }
                if (resolveResult.success) {
                    callback.onResolved(resolveResult.legacyCode);
                } else {
                    callback.onError(resolveResult.errorInfo);
                }
            }
        }.execute();
    }

    private static String buildLegacyCode(WsResult result) {
        // 当前服务端约定：Result=true 时 ErrorInfo 直接是 Box_ID 字符串
        String boxIdText = result.getErrorInfo();
        if (!TextUtils.isEmpty(boxIdText)) {
            try {
                long boxId = Long.parseLong(boxIdText.trim());
                if (boxId > 0) {
                    return "Pallet/" + boxId + "/No//PartNo//Q/0";
                }
            } catch (Exception ignored) {
            }
        }

        String info = result.getInfo();
        String legacy = extractLegacyString(info);
        if (TextUtils.isEmpty(legacy)) {
            legacy = extractLegacyString(boxIdText);
        }
        if (!TextUtils.isEmpty(legacy)) {
            return legacy;
        }

        JsonObject json = parseObject(info);
        if (json == null) {
            json = parseObject(boxIdText);
        }
        if (json != null) {
            long boxId = firstLong(json, "Box_ID", "BoxID", "Pallet_Box_ID");
            String codeNo = firstString(json, "Code_No_Box", "CodeNoBox", "No");
            String partNo = firstString(json, "Pallet_PartNo", "PartNo");
            String qty = firstString(json, "ActualBoxQty", "Qty", "Quantity");
            if (boxId > 0) {
                return "Pallet/" + boxId
                        + "/No/" + value(codeNo)
                        + "/PartNo/" + value(partNo)
                        + "/Q/" + value(qty);
            }
        }
        if (result.getID() != null && result.getID() > 0) {
            return "Pallet/" + result.getID() + "/No//PartNo//Q/0";
        }
        return "";
    }

    private static String extractLegacyString(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("Pallet/")) {
            return trimmed;
        }
        return "";
    }

    private static JsonObject parseObject(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            JsonElement element = new JsonParser().parse(value);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }
            if (element.isJsonArray() && element.getAsJsonArray().size() > 0) {
                JsonElement first = element.getAsJsonArray().get(0);
                return first.isJsonObject() ? first.getAsJsonObject() : null;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static long firstLong(JsonObject json, String... keys) {
        for (String key : keys) {
            if (json.has(key) && !json.get(key).isJsonNull()) {
                try {
                    return json.get(key).getAsLong();
                } catch (Exception ignored) {
                }
            }
        }
        return 0L;
    }

    private static String firstString(JsonObject json, String... keys) {
        for (String key : keys) {
            if (json.has(key) && !json.get(key).isJsonNull()) {
                try {
                    return json.get(key).getAsString();
                } catch (Exception ignored) {
                    return json.get(key).toString();
                }
            }
        }
        return "";
    }

    private static String value(String value) {
        return TextUtils.isEmpty(value) ? "" : value;
    }

    private static String error(WsResult result) {
        String errorInfo = result.getErrorInfo();
        return TextUtils.isEmpty(errorInfo) ? "新标签解析失败" : errorInfo;
    }

    private static class ResolveResult {
        final boolean success;
        final String legacyCode;
        final String errorInfo;

        private ResolveResult(boolean success, String legacyCode, String errorInfo) {
            this.success = success;
            this.legacyCode = legacyCode;
            this.errorInfo = errorInfo;
        }

        static ResolveResult success(String legacyCode) {
            return new ResolveResult(true, legacyCode, "");
        }

        static ResolveResult failure(String errorInfo) {
            return new ResolveResult(false, "", errorInfo);
        }
    }
}
