package com.chinashb.www.mobileerp;

import android.app.Application;

import com.chinashb.www.mobileerp.utils.ExceptionCatchManager;
import com.umeng.commonsdk.UMConfigure;

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
        ExceptionCatchManager.getInstance().init(get());
        //设置LOG开关，默认为false
        UMConfigure.setLogEnabled(true);
        //// TODO: 2019/8/27 s2
        UMConfigure.init(this,"5d649a6c570df39d02000754","Umeng",UMConfigure.DEVICE_TYPE_PHONE,"s2");
        //统计SDK是否支持采集在子进程中打点的自定义事件，默认不支持
        UMConfigure.setProcessEvent(true);//支持多进程打点
    }

    public static APP get(){
        if (app == null){
            app = new APP();
        }
        return app;
    }
}
