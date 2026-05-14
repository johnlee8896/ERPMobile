package com.chinashb.www.mobileerp.utils;

import android.text.Editable;
import android.text.TextWatcher;
//// TODO: 5/7/26   输入法问题，原来的方法有很大隐患
public abstract class TextWatcherImpl implements TextWatcher {

    @Override
    public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // 不处理
    }

    @Override
    public final void onTextChanged(CharSequence s, int start, int before, int count) {
        // ✅ 在这里统一转发
        onTextChangedSafe(s);
    }

    @Override
    public final void afterTextChanged(Editable s) {
        // ✅ 坚决不碰 Editable
    }

    /**
     * ✅ 子类只关心这个方法
     */
    protected abstract void onTextChangedSafe(CharSequence text);
}
//public class TextWatcherImpl implements TextWatcher {
//
//    @Override
//    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//    }
//
//    @Override
//    public void onTextChangedSafe(CharSequence text) {
//
//    }
//}

