package com.chinashb.www.mobileerp;

import android.app.Application;

/***
 * @date 创建时间 2019/6/18 10:09 AM
 * @author 作者: liweifeng
 * @description
 */
public class APP extends Application {
    private static APP app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static APP get(){
        if (app == null){
            app = new APP();
        }
        return app;
    }
}
