package com.hwariot.lib.tools.dialog.loadingdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwariot.lib.api.R;


/***
 * @date 创建时间 2018/3/23 10:18
 * @author 作者: liweifeng
 * @description 描述：加载中 dialog
 *
 */
public class LoadingDialog extends Dialog {
    /*** 上面动画View*/
    private ImageView spinnerImageView;
    /*** 下面文字View*/
    private TextView message;

    public LoadingDialog(Context context) {
        super(context, R.style.progressStyle);
        setContentView(R.layout.dialog_progress);
        spinnerImageView = findViewById(R.id.spinnerImageView);
        message = findViewById(R.id.message);
        configDialog();
    }

    private void configDialog() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.5f;
        lp.alpha = 0.7f;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
    }

    /*** 当窗口焦点改变时调用*/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        AnimationDrawable spinner = (AnimationDrawable) spinnerImageView.getBackground();
        spinner.start();
    }

    /*** 设置下面文本内容*/
    public LoadingDialog setMsg(String msg){
        if (msg == null || msg.length() == 0) {
            message.setVisibility(View.GONE);
        } else {
            message.setText(msg);
        }

        return this;
    }

    /*** dialog失去焦点 是否消失*/
    public LoadingDialog setDialogCancelable(boolean cancelable){
        //setCancelable(cancelable);
        setCanceledOnTouchOutside(cancelable);
        return this;
    }

    /*** 设置返回键监听事件*/
    public LoadingDialog setDialogOnCancelListener(OnCancelListener listener){
        setOnCancelListener(listener);
        return this;
    }

    public LoadingDialog showDialog(){
        show();
        return this;
    }



}
