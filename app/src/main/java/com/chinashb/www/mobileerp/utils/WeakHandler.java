package com.chinashb.www.mobileerp.utils;


import android.os.Handler;

import java.lang.ref.WeakReference;

/***
 * @date 创建时间 2019/9/24 9:02
 * @author 作者: xxblwf
 * @description Handler的弱引用
 */

public class WeakHandler<T> extends Handler {
    private WeakReference<T> weakReference;

    private T instance;
    public WeakHandler(T t){
        weakReference = new WeakReference<>(t);
    }

    public T getInstance() {
        if (instance == null){
            instance = weakReference.get();
        }
        return instance;
    }
}
