package com.hwariot.lib.base;

import android.os.Bundle;
import android.view.View;

import com.hwariot.lib.swipeBack.SwipeBackActivityBase;
import com.hwariot.lib.swipeBack.SwipeBackActivityHelper;
import com.hwariot.lib.swipeBack.SwipeBackLayout;
import com.hwariot.lib.swipeBack.SwipeBackUtils;

/***
 * @date 创建时间 2018/3/22 11:11
 * @author 作者: yulong
 * @description 基类Activity
 */
public class BaseActivity extends BaseRootActivity implements SwipeBackActivityBase {

    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mHelper != null) {
            mHelper.onPostCreate();
        }
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null) {
            return mHelper.findViewById(id);
        }
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        if (mHelper == null) {
            return null;
        }
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        SwipeBackUtils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }



}
