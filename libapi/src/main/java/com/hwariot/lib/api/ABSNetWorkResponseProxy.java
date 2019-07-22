package com.hwariot.lib.api;

import android.app.Activity;
import android.content.Context;

import com.hwariot.lib.APILibAPP;
import com.hwariot.lib.bean.BaseBean;
import com.hwariot.lib.tools.dialog.loadingdialog.LoadingDialogManagerSingleton;

import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;
import retrofit2.HttpException;

/***
 *@date 创建时间 2018/5/7 20:16
 *@author 作者: YuLong
 *@description 代理 RxJava得到的网络请求返回，用于统一处理一些操作，比如异常情况
 */
public abstract class ABSNetWorkResponseProxy<T> extends DisposableSubscriber<T> {
    private CompositeDisposable compositeDisposable;
    private Context mContext;


    /**/
    public ABSNetWorkResponseProxy(CompositeDisposable mCompositeSubscription, Context context) {
        this.mContext = context;
        this.compositeDisposable = mCompositeSubscription;
    }

    /**
     * 这个一定要有 Presenter的逻辑在这里处理
     */
    @Override
    public void onNext(T t) {
        if (mContext != null && mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
        }
        onNextAction(t);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        //隐藏dialog
        LoadingDialogManagerSingleton.get().hideDialog();
        if (!NetWorkUtils.isNetworkAvailable()) {
//            Toast.makeText(mContext,"当前无网络", Toast.LENGTH_SHORT).show();
            APILog.e("当前无网络....");
//            ToastUtil.toast(R.string.network_error);
        } else if (e instanceof RxUtil.ServerException) {
            BaseBean baseBean = ((RxUtil.ServerException) e).getBaseBean();
            //token 过期了
            if (baseBean != null && baseBean.getCode() == 403) {
                APILibAPP.onTokenExpired();
            }
            doErrorAction(baseBean);
        } else {
            if (e instanceof HttpException) {
                HttpException exception = (HttpException) e;

                try {
                    String errorInfo = exception.response().errorBody().string();
                    BaseBean data;
                    if ((errorInfo.startsWith("{") && errorInfo.endsWith("}"))) {
                        data = JsonUtil.parseJsonToObject(errorInfo, BaseBean.class);
                    } else {
                        data = new BaseBean();
                        data.setCode(exception.response().code());
                        data.setMessage(errorInfo);
                    }
                    doErrorAction(data);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else {
                BaseBean baseBean = new BaseBean();
                baseBean.setMessage("");
                baseBean.setCode(-1);
                doErrorAction(baseBean);
            }
        }
        if (compositeDisposable != null) {
            compositeDisposable.remove(this);
        }
    }

    @Override
    public void onComplete() {
        if (compositeDisposable != null) {
            compositeDisposable.remove(this);
        }
        //隐藏dialog
        LoadingDialogManagerSingleton.get().hideDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mContext != null) {
            //显示dialog
            LoadingDialogManagerSingleton.get().showDialog(mContext);
        }
    }

    /**
     * 代理onNext的方法
     *
     * @param t
     */
    protected abstract void onNextAction(T t);

    /**
     * 代理OnError的方法
     *
     * @param msg
     */
    protected void doErrorAction(BaseBean msg){
        if (mContext != null && mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
        }
        onErrorAction(msg);
    }

    protected abstract void onErrorAction(BaseBean msg);

}
