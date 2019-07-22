package com.hwariot.lib.api;

import com.hwariot.lib.bean.BaseBean;

/***
 *@date 创建时间 2018/4/19 22:58
 *@author 作者: yulong
 *@description Presenter的请求响应接口
 */
public interface BaseViewInterface {
    /**
     * 网络请求失败，每一个网络请求都要实现这个回调
     */
    void onFail(String api, BaseBean msg);

    /**
     * 网络请求成功后的数据
     */
    <T> void initData(String api, T t);

}
