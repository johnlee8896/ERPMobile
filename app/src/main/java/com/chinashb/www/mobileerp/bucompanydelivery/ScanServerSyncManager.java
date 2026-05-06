package com.chinashb.www.mobileerp.bucompanydelivery;

import android.os.AsyncTask;

import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.JsonUtil;

import java.util.List;

/***
 * @date 创建时间 4/25/26 3:00 PM
 * @author 作者: liweifeng
 * @description
 */
public class ScanServerSyncManager {

    public static void submitDelivery(
            List<DeliveryScanEntity> list,
            SyncCallback callback
    ) {
        new AsyncTask<Void, Void, WsResult>() {
            @Override
            protected WsResult doInBackground(Void... voids) {
                return WebServiceUtil.op_Submit_Delivery(
                        JsonUtil.objectToJson(list)
                );
            }

            @Override
            protected void onPostExecute(WsResult result) {
                if (result != null && result.getResult()) {
                    callback.onSuccess();
                } else {
                    callback.onFail(result == null ? "提交失败，请检查网络后重试" : result.getErrorInfo());
                }
            }
        }.execute();
    }

    public static void undo(
            DeliveryScanEntity entity,
            SyncCallback callback
    ) {
        new AsyncTask<Void, Void, WsResult>() {
            @Override
            protected WsResult doInBackground(Void... voids) {
                return WebServiceUtil.op_Undo_Scan(
                        entity.getCode(),
                        entity.getType()
                );
            }

            @Override
            protected void onPostExecute(WsResult result) {
                if (result != null && result.getResult()) {
                    callback.onSuccess();
                } else {
                    callback.onFail(result == null ? "撤销失败，请检查网络后重试" : result.getErrorInfo());
                }
            }
        }.execute();
    }
}
