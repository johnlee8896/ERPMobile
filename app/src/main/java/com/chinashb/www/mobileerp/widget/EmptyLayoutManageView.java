package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * @date 创建时间 2019/6/20 2:06 PM
 * @author 作者: liweifeng
 * @description 没有数据时的UI显示
 */
public class EmptyLayoutManageView extends LinearLayout {

    public static final int EMPTY_TYPE_NO_MESSAGE = 1;
    public static final int EMPTY_TYPE_NO_NETWORK = 2;
    public static final int EMPTY_TYPE_NO_SEARCH = 3;
    public static final int EMPTY_TYPE_NO_DATA = 4;
    public static final int EMPTY_TYPE_NO_VOUCHER = 5;

    private static final int[] GRAVITY_ARRAY = {
            Gravity.CENTER_HORIZONTAL,
            Gravity.LEFT,
            Gravity.RIGHT,
            Gravity.CENTER,
            Gravity.CENTER_VERTICAL
    };
    ImageView dataImageView;
    TextView dataTextView;

    public EmptyLayoutManageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.comm_empty_view, this);
        dataImageView = (ImageView) view.findViewById(R.id.empty_data_ImageView);
        dataTextView = (TextView) view.findViewById(R.id.empty_data_TextView);
        initializeView(context, attrs);
    }

    private void initializeView(Context context, @Nullable AttributeSet attrs) {
        float textSize;
        String text;
        ColorStateList textColor;
        int imageWidth, imageHeight;
        Drawable imageDrawable;
        int gravityIndex;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyLayoutManageView);
        text = a.getString(R.styleable.EmptyLayoutManageView_app_empty_text);
        textSize = a.getDimensionPixelSize(R.styleable.EmptyLayoutManageView_app_empty_text_size, -1);
        textColor = a.getColorStateList(R.styleable.EmptyLayoutManageView_app_empty_text_color);
        imageDrawable = a.getDrawable(R.styleable.EmptyLayoutManageView_app_empty_image_src);
        imageWidth = a.getDimensionPixelSize(R.styleable.EmptyLayoutManageView_app_empty_image_width, -1);
        imageHeight = a.getDimensionPixelSize(R.styleable.EmptyLayoutManageView_app_empty_image_height, -1);
        gravityIndex = a.getInt(R.styleable.EmptyLayoutManageView_app_empty_text_gravity, 0);
        a.recycle();

        setTextMessage(text);

        setTextGravity(GRAVITY_ARRAY[gravityIndex]);

        if (textColor != null) {
            dataTextView.setTextColor(textColor);
        }
        if (textSize != -1) {
            dataTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }

        if (imageDrawable != null) {
            dataImageView.setImageDrawable(imageDrawable);
        }

        LayoutParams params = (LayoutParams) dataImageView.getLayoutParams();
        if (imageWidth != -1) {
            params.weight = imageWidth;
        }
        if (imageHeight != -1) {
            params.height = imageHeight;
        }
        dataImageView.setLayoutParams(params);
    }

    public void setEmptyType(@EmptyTYpe int type) {
        int textResId;
        int imgResId;
        switch (type) {
            case EMPTY_TYPE_NO_MESSAGE:
                textResId = R.string.empty_no_message;
                imgResId = R.mipmap.ic_empty_no_message;
                break;

            case EMPTY_TYPE_NO_SEARCH:
                textResId = R.string.empty_no_search;
                imgResId = R.mipmap.ic_empty_no_search;
                break;
            case EMPTY_TYPE_NO_VOUCHER:
                textResId = R.string.empty_no_voucher;
                imgResId = R.mipmap.ic_empty_no_voucher;
                break;
            case EMPTY_TYPE_NO_NETWORK:
                textResId = R.string.empty_no_network;
                imgResId = R.mipmap.ic_empty_no_network;
                break;
            case EMPTY_TYPE_NO_DATA:
            default:
                textResId = R.string.empty_no_data;
                imgResId = R.mipmap.ic_empty_no_data;
                break;
        }
        dataImageView.setImageResource(imgResId);
        dataTextView.setText(textResId);
    }

    public void setImageRes(int resId) {
        dataImageView.setImageResource(resId);
    }

    public void setTextId(int text) {
        dataTextView.setText(text);
    }

    public void setTextMessage(CharSequence text) {
        if (text != null) {
            dataTextView.setText(text);
        }
    }

    public void setTextGravity(int gravity){
        dataTextView.setGravity(gravity);
    }

    public ImageView getImageView() {
        return dataImageView;
    }

    public TextView getTextView() {
        return dataTextView;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EMPTY_TYPE_NO_MESSAGE, EMPTY_TYPE_NO_NETWORK, EMPTY_TYPE_NO_SEARCH,
            EMPTY_TYPE_NO_DATA, EMPTY_TYPE_NO_VOUCHER})
    public @interface EmptyTYpe {
    }
}

