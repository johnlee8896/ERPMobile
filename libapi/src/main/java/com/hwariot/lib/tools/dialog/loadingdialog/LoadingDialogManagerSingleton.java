package com.hwariot.lib.tools.dialog.loadingdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;

/***
 * @date 创建时间 2018/3/23 10:44
 * @author 作者: liweifeng
 * @description 描述：加载等待的dialog
 *
 */
public final class LoadingDialogManagerSingleton implements LoadingDialogInterface {
    private LoadingDialog loadingDialog;

    public static LoadingDialogManagerSingleton get() {
        return LoadingDialogManagerHolder.instance;
    }

    private static class LoadingDialogManagerHolder {
        private static final LoadingDialogManagerSingleton instance = new LoadingDialogManagerSingleton();
    }

    private AlertDialog alertDialog;

    public void showNoNetworkAlertDialog(Context context) {
        if (context == null) {
            return;
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(context)
                    .setTitle("温馨提示")
                    .setMessage("系统检测到您当前网络连接异常，请确认您的网络连接是否正常？")
                    .setPositiveButton("去设置网络", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();

            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
        }

        if (!alertDialog.isShowing()) {
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#05A7F7"));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#05A7F7"));
        }
    }


    public void hideNetworkAlertDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }


    @Override
    public void showDialog(Context context) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context)
                    .setMsg("加载中...")
                    .setDialogCancelable(false)
                    .setDialogOnCancelListener(null)
                    .showDialog();
        }
    }

    @Override
    public void hideDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
