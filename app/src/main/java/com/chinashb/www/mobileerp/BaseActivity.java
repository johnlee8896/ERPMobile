package com.chinashb.www.mobileerp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

/***
 * @date 创建时间 2019/8/27 13:11
 * @author 作者: xxblwf
 * @description base基类，便于统计等
 */

public class BaseActivity  extends AppCompatActivity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
