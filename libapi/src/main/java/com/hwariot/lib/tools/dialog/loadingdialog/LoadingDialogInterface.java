package com.hwariot.lib.tools.dialog.loadingdialog;

import android.content.Context;

/***
 * @date 创建时间 2018/3/23 10:51
 * @author 作者: liweifeng
 * @description 描述： loading dialog 接口
 *
 */
public interface LoadingDialogInterface {
    /*** 显示Dialog*/
    void showDialog(Context context);

    /*** 隐藏Dialog*/
    void hideDialog();
}
