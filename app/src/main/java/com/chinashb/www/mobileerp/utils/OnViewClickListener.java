package com.chinashb.www.mobileerp.utils;

import android.view.View;

/***
 * @date 创建时间 2018/4/10 17:15
 * @author 作者: liweifeng
 * @description 增强版的View ClickListener接口
 */
public interface OnViewClickListener {

    /***
     * 通用的点击事件接管
     * @param v 被点击的View
     * @param tag Tag标记
     */
    <T>void onClickAction(View v, String tag, T t);
}
