package com.chinashb.www.mobileerp.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

/***
 * @date 创建时间 2019/6/19 10:12 AM
 * @author 作者: liweifeng
 * @description
 */
public class CommProgressDialog extends Dialog {

    private TextView titleTextView;

    private Builder builder;

    public CommProgressDialog(@NonNull Builder builder) {
        super(builder.context);
        this.builder = builder;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress_layout);
        titleTextView = (TextView) findViewById(R.id.dialog_progress_title_TextView);

        if (builder.textColor != -1) {
            titleTextView.setTextColor(builder.textColor);
        }
        if (builder.textSize != -1) {
            titleTextView.setTextSize(builder.textSize);
        }


        setTitleText(builder.title);

        setCancelable(builder.isCancelable);
        setCanceledOnTouchOutside(builder.isTouchOutsideCancel);


        configDialog(builder.gravity);
    }


    public void setTitleText(CharSequence title) {
        if (title != null) {
            titleTextView.setText(title);
        }
    }

    protected void configDialog(int gravity) {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.gravity = gravity;// 设置重力

        if (gravity == Gravity.BOTTOM) {
            getWindow().setWindowAnimations(R.style.bottomDialogWindowAnim);
        }
        getWindow().setAttributes(wl);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public static Builder with(Context context) {
        return new Builder(context);
    }


    public static class Builder {
        private Context context;
        private CharSequence title;
        private int textSize = -1;
        private int textColor = -1;
        private boolean isCancelable = true;
        private boolean isTouchOutsideCancel = true;
        private int gravity = Gravity.CENTER;

        public Builder(Context context) {
            this.context = context;
        }


        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            isCancelable = cancelable;
            return this;
        }

        public Builder setTouchOutsideCancel(boolean touchOutsideCancel) {
            isTouchOutsideCancel = touchOutsideCancel;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public CommProgressDialog create() {
            return new CommProgressDialog(this);
        }
    }
}

