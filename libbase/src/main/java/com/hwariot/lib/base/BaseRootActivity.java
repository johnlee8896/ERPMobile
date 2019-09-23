package com.hwariot.lib.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/***
 *@date 创建时间 2018/3/22 11:07
 *@author 作者: yulong
 *@description 所有Activity的基类
 */
public class BaseRootActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, Color.WHITE);
        StatusBarUtil.setStatusBarLightMode(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        enterPendingAnim();
    }

    @Override
    public void finish() {
        super.finish();
        exitPendingAnim();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        enterPendingAnim();
    }

    public void exitPendingAnim() {
        overridePendingTransition(R.anim.base_anim_normal, R.anim.base_slide_out_right);

    }

    public void enterPendingAnim() {
        overridePendingTransition(R.anim.base_slide_in_right, R.anim.base_anim_normal);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }
}

