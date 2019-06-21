package com.chinashb.www.mobileerp.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

/***
 * @date 创建时间 2018/4/24 10:37
 * @author 作者: liweifeng
 * @description 通用的顶部标题栏
 */
public class TitleLayoutManagerView extends RelativeLayout implements View.OnClickListener {

    private ImageView backImageView;
    private TextView titleTextView;

    public TitleLayoutManagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.comm_top_title_layout, this);
        backImageView = (ImageView) findViewById(R.id.top_title_back_ImageView);
        titleTextView = (TextView) findViewById(R.id.top_title_content_TextView);

        backImageView.setOnClickListener(this);
        initializeUI(context, attrs);
    }

    private void initializeUI(Context context, AttributeSet attrs) {
        CharSequence title;
        int textSize;
        Drawable imageDrawable;
        ColorStateList colorStateList;
        boolean isHideLeftImage;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleLayoutManagerView);
        title = a.getString(R.styleable.TitleLayoutManagerView_app_title_text);
        textSize = a.getDimensionPixelSize(R.styleable.TitleLayoutManagerView_app_title_text_size, -1);
        colorStateList = a.getColorStateList(R.styleable.TitleLayoutManagerView_app_title_text_color);
        imageDrawable = a.getDrawable(R.styleable.TitleLayoutManagerView_app_back_image_src);
        isHideLeftImage = a.getBoolean(R.styleable.TitleLayoutManagerView_app_hide_back_image, false);
        a.recycle();

        backImageView.setVisibility(isHideLeftImage ? GONE : VISIBLE);
        setTitle(title);

        if (textSize != -1) {
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }

        if (colorStateList != null) {
            titleTextView.setTextColor(colorStateList);
        }
        if (imageDrawable != null) {
            backImageView.setImageDrawable(imageDrawable);
        }

    }


    public void setTitle(CharSequence text) {
        if (text == null) {
            return;
        }
        titleTextView.setText(text);
    }


    public TextView getTitleTextView() {
        return titleTextView;
    }

    public ImageView getBackImageView() {
        return backImageView;
    }

    @Override
    public void onClick(View v) {
        if (v == backImageView) {
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).onBackPressed();
            }
        }
    }
}
