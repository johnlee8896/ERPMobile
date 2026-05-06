package com.chinashb.www.mobileerp.bucompanydelivery;

/***
 * @date 创建时间 4/25/26 3:04 PM
 * @author 作者: liweifeng
 * @description
 */
public interface ScanCallback {
    void onSuccess(String code);
    void onFail(String error);
}
