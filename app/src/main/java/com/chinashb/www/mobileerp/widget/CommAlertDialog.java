package com.chinashb.www.mobileerp.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.constraint.Group;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

/***
 * @date 创建时间 2018/5/22 18:48
 * @author 作者: liweifeng
 * @description APP的AlertDialog
 */
public class CommAlertDialog extends Dialog implements View.OnClickListener {
    public static final int TAG_CLICK_LEFT = 1;
    public static final int TAG_CLICK_RIGHT = 2;
    public static final int TAG_CLICK_MIDDLE = 3;

    private TextView titleTextView;
    private TextView messageTextView;
    private Button leftButton;
    private Button middleButton;
    private Button rightButton;
    private Group bottomGroup;
    private EditText messageEditText;
    private NestedScrollView scrollView;

    private DialogBuilder builder;

    private CommAlertDialog(DialogBuilder builder) {
        super(builder.context);
        this.builder = builder;

    }

    public static DialogBuilder with(Context context) {
        return new DialogBuilder(context);
    }

    public static void showAlertDialog(Context context, String message, OnDialogViewClickListener onDialogViewClickListener) {
        with(context).setMessage(message).setMiddleText("确定")
                .setMiddleColor(0xff228BE0)
                .setOnViewClickListener(onDialogViewClickListener).create().show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comm_alert_layout);
        initViewFromXML();
        initUI();

        setViewListener();
    }

    private void initViewFromXML() {
        titleTextView = (TextView) findViewById(R.id.dialog_alert_title_TextView);
        messageTextView = (TextView) findViewById(R.id.dialog_alert_message_TextView);
        leftButton = (Button) findViewById(R.id.dialog_work_line_confirm_Button);
        middleButton = (Button) findViewById(R.id.dialog_alert_middle_Button);
        rightButton = (Button) findViewById(R.id.dialog_work_line_cancel_Button);
        bottomGroup = (Group) findViewById(R.id.dialog_alert_bottom_button_group);
        messageEditText = (EditText) findViewById(R.id.dialog_alert_message_EditText);
        scrollView = (NestedScrollView) findViewById(R.id.dialog_alert_message_container_layout);
    }

    private void setViewListener() {
        leftButton.setOnClickListener(this);
        middleButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
    }


    private void initUI() {
        if (builder != null) {
            if (TextUtils.isEmpty(builder.rightText) &&
                    TextUtils.isEmpty(builder.middleText)
                    && TextUtils.isEmpty(builder.leftText)) {
                bottomGroup.setVisibility(View.GONE);
            } else {
                initText(leftButton, builder.leftText);
                initText(rightButton, builder.rightText);
                initText(middleButton, builder.middleText);
                bottomGroup.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(builder.title)){
                findViewById(R.id.dialog_alert_title_line_View).setVisibility(View.GONE);
                titleTextView.setVisibility(View.GONE);
            } else {
                titleTextView.setVisibility(View.VISIBLE);
                findViewById(R.id.dialog_alert_title_line_View).setVisibility(View.VISIBLE);
                titleTextView.setText(builder.title);
            }

            initText(messageTextView, builder.message);

            if (builder.hintText != null) {
                messageEditText.setHint(builder.hintText);
            }

            if (builder.message == null) {
                scrollView.setVisibility(View.GONE);
            } else {
                scrollView.setVisibility(View.VISIBLE);
            }

            if (builder.isEditMode) {
                messageTextView.setVisibility(View.GONE);
                messageEditText.setVisibility(View.VISIBLE);
            } else {
                messageTextView.setVisibility(View.VISIBLE);
                messageEditText.setVisibility(View.GONE);
            }

            if (builder.messageMinHeight > 0) {
                messageTextView.setMinHeight(dp2px(builder.messageMinHeight));
                messageEditText.setMinHeight(dp2px(builder.messageMinHeight));
            }

            messageTextView.setGravity(builder.messageGravity);
            messageEditText.setGravity(builder.messageGravity);

            initTextColor(titleTextView, builder.titleColorStateList);
            initTextColor(messageTextView, builder.messageColorStateList);
            initTextColor(messageEditText, builder.messageColorStateList);

            initTextColor(leftButton, builder.leftColorStateList);
            initTextColor(middleButton, builder.middleColorStateList);
            initTextColor(rightButton, builder.rightColorStateList);

            initTextSize(titleTextView, builder.titleTextTextSize);
            initTextSize(messageTextView, builder.messageTextTextSize);
            initTextSize(messageEditText, builder.messageTextTextSize);

            initTextSize(leftButton, builder.leftTextTextSize);
            initTextSize(rightButton, builder.rightTextTextSize);
            initTextSize(middleButton, builder.middleTextTextSize);

            setCanceledOnTouchOutside(builder.isTouchOutsideCancel);
            setCancelable(builder.isCancelAble);

            configDialog(builder.gravity);
        }
    }

    public int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }


    private void initText(TextView textView, CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    //获取输入的字符
    public CharSequence getInputString() {
        return messageEditText.getText();
    }

    /*设置控件的字体颜色*/
    private void initTextColor(TextView textView, ColorStateList colorStateList) {
        if (colorStateList != null) {
            textView.setTextColor(colorStateList);
        }
    }

    /*设置控件的字体大小*/
    private void initTextSize(TextView view, int textSize) {
        if (textSize != -1) {
            view.setTextSize(textSize);
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
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

    }

    @Override
    public void onClick(View v) {
        if (builder.onViewClickListener != null) {
            if (v == leftButton) {
                builder.onViewClickListener.onViewClick(this, v, TAG_CLICK_LEFT);
            } else if (v == rightButton) {
                builder.onViewClickListener.onViewClick(this, v, TAG_CLICK_RIGHT);
            } else if (v == middleButton) {
                builder.onViewClickListener.onViewClick(this, v, TAG_CLICK_MIDDLE);
            }
        }

        if (builder.isClickButtonDismiss) {
            dismiss();
        }
    }


    public static class DialogBuilder {
        private Context context;

        private CharSequence title;
        private CharSequence message;
        private CharSequence leftText;
        private CharSequence middleText;
        private CharSequence rightText;

        private CharSequence hintText;

        private OnDialogViewClickListener onViewClickListener;


        private boolean isCancelAble = true;
        private boolean isTouchOutsideCancel = true;

        private ColorStateList leftColorStateList;
        private ColorStateList middleColorStateList;
        private ColorStateList rightColorStateList;

        private ColorStateList titleColorStateList;
        private ColorStateList messageColorStateList;

        private int gravity = Gravity.CENTER;
        //默认高度为
        private int messageMinHeight = 50;

        private int titleTextTextSize = -1;
        private int messageTextTextSize = -1;
        private int leftTextTextSize = -1;
        private int middleTextTextSize = -1;
        private int rightTextTextSize = -1;

        private boolean isEditMode = false;
        private boolean isClickButtonDismiss = false;

        private int messageGravity = Gravity.CENTER;

        public DialogBuilder(Context context) {
            this.context = context;
        }

        public int getMessageMinHeight() {
            return messageMinHeight;
        }

        public DialogBuilder setMessageMinHeight(int messageMinHeight) {
            this.messageMinHeight = messageMinHeight;
            return this;
        }

        public DialogBuilder setHintText(CharSequence hintText) {
            this.hintText = hintText;
            return this;
        }

        public DialogBuilder setEditMode(boolean editMode) {
            isEditMode = editMode;
            return this;
        }

        public DialogBuilder setMessageGravity(int messageGravity) {
            this.messageGravity = messageGravity;
            return this;
        }

        public DialogBuilder setTitleTextTextSize(int titleTextTextSize) {
            this.titleTextTextSize = titleTextTextSize;
            return this;
        }

        public DialogBuilder setMessageTextTextSize(int messageTextTextSize) {
            this.messageTextTextSize = messageTextTextSize;
            return this;
        }

        public DialogBuilder setLeftTextTextSize(int leftTextTextSize) {
            this.leftTextTextSize = leftTextTextSize;
            return this;
        }

        public DialogBuilder setMiddleTextTextSize(int middleTextTextSize) {
            this.middleTextTextSize = middleTextTextSize;
            return this;
        }

        public DialogBuilder setClickButtonDismiss(boolean clickButtonDismiss) {
            isClickButtonDismiss = clickButtonDismiss;
            return this;
        }

        public DialogBuilder setRightTextTextSize(int rightTextTextSize) {
            this.rightTextTextSize = rightTextTextSize;
            return this;
        }

        public DialogBuilder setTouchOutsideCancel(boolean touchOutsideCancel) {
            isTouchOutsideCancel = touchOutsideCancel;
            return this;
        }

        public DialogBuilder setLeftColorRes(@ColorRes int leftColorRes) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.leftColorStateList = context.getResources().getColorStateList(leftColorRes, context.getTheme());
            } else {
                this.leftColorStateList = context.getResources().getColorStateList(leftColorRes);
            }
            return this;
        }

        public DialogBuilder setMiddleColorRes(@ColorRes int colorRes) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.middleColorStateList = context.getResources().getColorStateList(colorRes, context.getTheme());
            } else {
                this.middleColorStateList = context.getResources().getColorStateList(colorRes);
            }
            return this;
        }

        public DialogBuilder setAllButtonColor(int color) {
            rightColorStateList = middleColorStateList = leftColorStateList = ColorStateList.valueOf(color);
            return this;
        }

        //设置所有按钮统一的颜色
        public DialogBuilder setAllButtonColorRes(@ColorRes int colorRes) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                leftColorStateList = middleColorStateList = rightColorStateList =
                        context.getResources().getColorStateList(colorRes, context.getTheme());
            } else {
                leftColorStateList = middleColorStateList = rightColorStateList =
                        context.getResources().getColorStateList(colorRes);
            }
            return this;
        }

        public DialogBuilder setRightColorRes(@ColorRes int colorRes) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.rightColorStateList = context.getResources().getColorStateList(colorRes, context.getTheme());
            } else {
                this.rightColorStateList = context.getResources().getColorStateList(colorRes);
            }
            return this;
        }

        public DialogBuilder setTitleColorRes(@ColorRes int colorRes) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.titleColorStateList = context.getResources().getColorStateList(colorRes, context.getTheme());
            } else {
                this.titleColorStateList = context.getResources().getColorStateList(colorRes);
            }
            return this;
        }

        public DialogBuilder setMessageColorRes(@ColorRes int colorRes) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.messageColorStateList = context.getResources().getColorStateList(colorRes, context.getTheme());
            } else {
                this.messageColorStateList = context.getResources().getColorStateList(colorRes);
            }
            return this;
        }


        public DialogBuilder setLeftColor(int leftColor) {
            leftColorStateList = ColorStateList.valueOf(leftColor);
            return this;
        }

        public DialogBuilder setMiddleColor(int middleColor) {
            this.middleColorStateList = ColorStateList.valueOf(middleColor);
            return this;
        }

        public DialogBuilder setRightColor(int rightColor) {
            this.rightColorStateList = ColorStateList.valueOf(rightColor);
            return this;
        }

        public DialogBuilder setTitleColor(int titleColor) {
            this.titleColorStateList = ColorStateList.valueOf(titleColor);
            return this;
        }

        public DialogBuilder setContentColor(int contentColor) {
            this.messageColorStateList = ColorStateList.valueOf(contentColor);
            return this;
        }


        public DialogBuilder setContext(Context context) {
            this.context = context;
            return this;
        }

        public DialogBuilder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public DialogBuilder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public DialogBuilder setLeftText(CharSequence leftText) {
            this.leftText = leftText;
            return this;
        }

        public DialogBuilder setMiddleText(CharSequence middleText) {
            this.middleText = middleText;
            return this;
        }

        public DialogBuilder setRightText(CharSequence rightText) {
            this.rightText = rightText;
            return this;
        }

        public DialogBuilder setOnViewClickListener(OnDialogViewClickListener onViewClickListener) {
            this.onViewClickListener = onViewClickListener;
            return this;
        }

        public DialogBuilder setCancelAble(boolean cancelAble) {
            isCancelAble = cancelAble;
            return this;
        }

        public DialogBuilder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public void show() {
            create().show();
        }

        public CommAlertDialog create() {
            return new CommAlertDialog(this);
        }
    }
}
