package com.chinashb.www.mobileerp.widget.group;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class GroupImageTextLayout extends RelativeLayout {
    public static final int IMAGE_LEFT_TEXT_RIGHT = 0;
    public static final int IMAGE_TOP_TEXT_BOTTOM = 1;
    public static final int IMAGE_RIGHT_TEXT_LEFT = 2;
    public static final int IMAGE_BOTTOM_TEXT_TOP = 3;
    //两端对齐
    public static final int IMAGE_RIGHT_TEXT_LEFT_SIDE = 4;

    private static final ImageView.ScaleType[] SCALE_TYPE_ARRAY = {
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };
    private static final int[] GRAVITY_ARRAY = {
            Gravity.CENTER_HORIZONTAL,
            Gravity.LEFT,
            Gravity.RIGHT,
            Gravity.CENTER,
            Gravity.CENTER_VERTICAL
    };

    private @OrientationType int orientationType = IMAGE_TOP_TEXT_BOTTOM;
    private ImageView imageView;
    private TextView textView;

    public GroupImageTextLayout(Context context){
        super(context,null);
        inflate(context, R.layout.group_image_text_layout, this);
        imageView = findViewById(R.id.group_image_ImageView);
        textView = findViewById(R.id.group_text_TextView);
        initializeAttrs(context,null);
    }

    public GroupImageTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.group_image_text_layout, this);
        imageView = findViewById(R.id.group_image_ImageView);
        textView = findViewById(R.id.group_text_TextView);
        initializeAttrs(context, attrs);
    }

    private void initializeAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GroupImageTextLayout);
        int imageWidth = a.getDimensionPixelOffset(R.styleable.GroupImageTextLayout_app_image_width, 0);
        int imageHeight = a.getDimensionPixelOffset(R.styleable.GroupImageTextLayout_app_image_height, 0);
        int viewMarginStart = a.getDimensionPixelOffset(R.styleable.GroupImageTextLayout_app_child_view_margin_start, 0);

        int textMaxWidth= a.getDimensionPixelOffset(R.styleable.GroupImageTextLayout_app_text_max_width, -1);

        Drawable imageDrawable = a.getDrawable(R.styleable.GroupImageTextLayout_app_image_src);
        int scaleTypeIndex = a.getInt(R.styleable.GroupImageTextLayout_app_scale_type, -1);

        Drawable imageBackgroundDrawable = a.getDrawable(R.styleable.GroupImageTextLayout_app_image_background);
        Drawable textBackgroundDrawable = a.getDrawable(R.styleable.GroupImageTextLayout_app_text_background);

        int textSize = a.getDimensionPixelSize(R.styleable.GroupImageTextLayout_app_text_size, 16);
        ColorStateList textColor = a.getColorStateList(R.styleable.GroupImageTextLayout_app_text_color);
        String textString = a.getString(R.styleable.GroupImageTextLayout_app_text);
        int gravityIndex = a.getInt(R.styleable.GroupImageTextLayout_app_text_gravity, 0);
        boolean isSingleLine = a.getBoolean(R.styleable.GroupImageTextLayout_app_text_single_line, false);
        int maxLine = a.getInt(R.styleable.GroupImageTextLayout_app_text_max_line, -1);

        orientationType = a.getInt(R.styleable.GroupImageTextLayout_app_parent_orientation, IMAGE_TOP_TEXT_BOTTOM);
        int textMarginImageSize = a.getDimensionPixelOffset(R.styleable.GroupImageTextLayout_app_text_margin_image_size, 0);
        a.recycle();

        textView.setSingleLine(isSingleLine);
        if (maxLine != -1){
            textView.setMaxLines(maxLine);
        }
        if (imageDrawable != null) {
            imageView.setImageDrawable(imageDrawable);
        }

        if (scaleTypeIndex >= 0) {
            setScaleType(SCALE_TYPE_ARRAY[scaleTypeIndex]);
        }

        if (textColor != null) {
            textView.setTextColor(textColor);
        }

        if (textMaxWidth >= 0){
            textView.setMaxWidth(textMaxWidth);
        }

        if (textBackgroundDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textView.setBackground(textBackgroundDrawable);
            } else {
                textView.setBackgroundDrawable(textBackgroundDrawable);
            }
        }

        if (imageBackgroundDrawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackground(imageBackgroundDrawable);
            } else {
                imageView.setBackgroundDrawable(imageBackgroundDrawable);
            }
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        if (textString != null) {
            textView.setText(textString);
        }
        textView.setGravity(GRAVITY_ARRAY[gravityIndex]);

        initTextOrientationStyle(imageWidth, imageHeight, textMarginImageSize, viewMarginStart);

    }

    public void setSelectStatus(boolean isSelected) {
        textView.setSelected(isSelected);
        imageView.setSelected(isSelected);
    }

    private void initTextOrientationStyle(int imageWidth, int imageHeight, int textMarginImageSize, int startMargin) {
        LayoutParams textParams = (LayoutParams) textView.getLayoutParams();
        LayoutParams imageParams = (LayoutParams) imageView.getLayoutParams();

        if (imageWidth > 0) {
            imageParams.width = imageWidth;
        }
        if (imageHeight > 0) {
            imageParams.height = imageHeight;
        }

        switch (orientationType) {
            case IMAGE_LEFT_TEXT_RIGHT:
                textParams.addRule(RelativeLayout.RIGHT_OF, R.id.group_image_ImageView);
                textParams.addRule(RelativeLayout.CENTER_VERTICAL);

                imageParams.addRule(RelativeLayout.CENTER_VERTICAL);

                textParams.leftMargin = textMarginImageSize / 2;
                imageParams.rightMargin = textMarginImageSize / 2;
                imageParams.leftMargin = startMargin;
                break;
            case IMAGE_RIGHT_TEXT_LEFT_SIDE:
                textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case IMAGE_RIGHT_TEXT_LEFT:

                textParams.addRule(RelativeLayout.CENTER_VERTICAL);

                imageParams.addRule(RelativeLayout.RIGHT_OF, R.id.group_text_TextView);
                imageParams.addRule(RelativeLayout.CENTER_VERTICAL);

                textParams.leftMargin = startMargin;

                textParams.rightMargin = textMarginImageSize / 2;
                imageParams.leftMargin = textMarginImageSize / 2;
                break;
            case IMAGE_TOP_TEXT_BOTTOM:
                textParams.addRule(RelativeLayout.BELOW, R.id.group_image_ImageView);
                textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                textParams.topMargin = textMarginImageSize / 2;
                imageParams.bottomMargin = textMarginImageSize / 2;
                imageParams.topMargin = startMargin;
                break;
            case IMAGE_BOTTOM_TEXT_TOP:
                textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageParams.addRule(RelativeLayout.BELOW, R.id.group_text_TextView);
                imageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                textParams.topMargin = startMargin;
                textParams.bottomMargin = textMarginImageSize / 2;
                imageParams.topMargin = textMarginImageSize / 2;
                break;

            default:
        }

        textView.setLayoutParams(textParams);
        imageView.setLayoutParams(imageParams);
    }



    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void setTextColor(int color){
        textView.setTextColor(color);
    }

    public void setImageAndText(@DrawableRes int imgId, @StringRes int textID) {
        imageView.setImageResource(imgId);
        textView.setText(textID);
    }

    public void setImageAndText(@DrawableRes int imgId, CharSequence text) {
        imageView.setImageResource(imgId);
        textView.setText(text);
    }

    public void setImageByResId(int imageResId){
        imageView.setImageResource(imageResId);
    }

    public void setText(@StringRes int resId) {
        textView.setText(resId);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public CharSequence getText(){
        return textView.getText();
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        imageView.setScaleType(scaleType);
    }

    /**
     * 设置图文的布局样式
     */
    public void setTextOrientation(int orientation) {
        this.orientationType = orientation;
        invalidate();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IMAGE_LEFT_TEXT_RIGHT, IMAGE_TOP_TEXT_BOTTOM, IMAGE_RIGHT_TEXT_LEFT, IMAGE_BOTTOM_TEXT_TOP, IMAGE_RIGHT_TEXT_LEFT_SIDE})
    public @interface OrientationType {
    }

}
