package com.chinashb.www.mobileerp.bucompanydelivery;

import android.os.AsyncTask;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

/***
 * @date 创建时间 4/25/26 2:58 PM
 * @author 作者: liweifeng
 * @description
 */
public class ScanBusinessDispatcher {

    public enum ScanType {
        TRAY, CONTAINER, DELIVERY_ORDER
    }

    public static void handleScan(
            ScanType type,
            String code,
            ScanCallback callback
    ) {
        switch (type) {
            case TRAY:
                callback.onSuccess(code);
                break;
            case CONTAINER:
                validateContainer(code, callback);
                break;
            case DELIVERY_ORDER:
                validateDeliveryOrder(code, callback);
                break;
        }
    }

    private static void validateContainer(String code, ScanCallback callback) {
        new AsyncTask<String, Void, WsResult>() {
            @Override
            protected WsResult doInBackground(String... params) {
                return WebServiceUtil.op_Check_Container(code);
            }

            @Override
            protected void onPostExecute(WsResult result) {
                if (result != null && result.getResult()) {
                    callback.onSuccess(code);
                } else {
                    callback.onFail(result == null ? "校验失败，请检查网络后重试" : result.getErrorInfo());
                }
            }
        }.execute();
    }

    private static void validateDeliveryOrder(String code, ScanCallback callback) {
        new AsyncTask<String, Void, WsResult>() {
            @Override
            protected WsResult doInBackground(String... params) {
                return WebServiceUtil.op_Check_DeliveryOrder(code);
            }

            @Override
            protected void onPostExecute(WsResult result) {
                if (result != null && result.getResult()) {
                    callback.onSuccess(code);
                } else {
                    callback.onFail(result == null ? "校验失败，请检查网络后重试" : result.getErrorInfo());
                }
            }
        }.execute();
    }
}
