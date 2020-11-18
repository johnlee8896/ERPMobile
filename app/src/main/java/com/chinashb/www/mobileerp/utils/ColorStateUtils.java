package com.chinashb.www.mobileerp.utils;

import android.content.Context;

import com.chinashb.www.mobileerp.R;


/***
 * @date 创建时间 2018/6/29 9:59 AM
 * @author 作者: liweifeng
 * @description 颜色处理的一些公共类
 */
public class ColorStateUtils {

    /**
     * 根据状态获取要显示的颜色值
     * @param context
     * @param status
     */
    public static int getColorByState(Context context, int status) {
        int resultColor = context.getResources().getColor(R.color.color_red_E94156);
        switch (status) {
            case 0:
            case 4:
                resultColor = context.getResources().getColor(R.color.color_blue_2E7FEF);
                break;
            default:
                break;
        }
        return resultColor;
    }
}
