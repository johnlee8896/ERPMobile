package com.hwariot.lib.api;

import com.hwariot.lib.bean.BaseBean;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 统一处理线程切换 和 处理网络返回
 *
 * @author liweifeng
 */
public final class RxUtil {
    private static FlowableTransformer ioToMainThreadSchedulerTransformer;
    private static FlowableTransformer newThreadToMainThreadSchedulerTransformer;

    static {
        ioToMainThreadSchedulerTransformer = createIOToMainThreadScheduler();
        newThreadToMainThreadSchedulerTransformer = createNewThreadToMainThreadScheduler();
    }

    private static <T> FlowableTransformer<T, T> createIOToMainThreadScheduler() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 从IO线程切换到主线程
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> applyIOToMainThreadSchedulers() {
        return ioToMainThreadSchedulerTransformer;
    }

    private static <T> FlowableTransformer<T, T> createNewThreadToMainThreadScheduler() {
        return tObservable -> tObservable.subscribeOn(Schedulers.newThread())
                .unsubscribeOn(Schedulers.computation())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> applyNewThreadToMainThreadSchedulers() {
        return newThreadToMainThreadSchedulerTransformer;
    }

    public static <T> FlowableTransformer<String, T> handleOtherResult(Type type) {
        return new FlowableTransformer<String, T>() {
            @Override
            public Publisher<T> apply(Flowable<String> flowable) {
                return flowable.flatMap(new Function<String, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(String s) throws Exception {
                        BaseBean<T> tBaseBean = JsonUtil.parseJsonToObject(s, type);
                        if (tBaseBean.getCode() == 0) {
                            return createData(tBaseBean.getData());
                        } else {
                            return Flowable.error(new ServerException(tBaseBean));
                        }
                    }
                }).compose(applyIOToMainThreadSchedulers());
            }
        };
    }

    /**
     * 处理服务器返回的数据，进一步处理错误信息
     */
    public static <T> FlowableTransformer<BaseBean<T>, T> handleResult() {
        return new FlowableTransformer<BaseBean<T>, T>() {
            @Override
            public Publisher<T> apply(Flowable<BaseBean<T>> flowable) {
                return flowable.flatMap(new Function<BaseBean<T>, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(BaseBean<T> tBaseBean) {
                        if (tBaseBean.getCode() == 0) {
                            return createData(tBaseBean.getData());
                        } else {
                            return Flowable.error(new ServerException(tBaseBean));
                        }
                    }
                }).compose(applyIOToMainThreadSchedulers());
            }
        };

    }

    public static <T> FlowableTransformer<BaseBean<T>, T> handleResultWithError() {
        return new FlowableTransformer<BaseBean<T>, T>() {
            @Override
            public Publisher<T> apply(Flowable<BaseBean<T>> flowable) {
                return flowable.flatMap(new Function<BaseBean<T>, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(BaseBean<T> tBaseBean) {
                        if (tBaseBean.getCode() == 0) {
                            return createData(tBaseBean.getData());
                        } else {
                            return Flowable.error(new ServerException(tBaseBean));
                        }
                    }
                }).compose(applyIOToMainThreadSchedulers());
            }
        };

    }

    /**
     * 创建成功的数据
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Flowable<T> createData(T data) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> flowableEmitter) {
                try {
                    flowableEmitter.onNext(data);
                    flowableEmitter.onComplete();
                } catch (Exception e) {
                    flowableEmitter.onError(e);
                }
            }
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * 自定义 服务器返回异常
     */
    public static class ServerException extends Throwable {
        BaseBean baseBean;
        public ServerException(BaseBean baseBean) {
            this.baseBean = baseBean;
        }

        public BaseBean getBaseBean() {
            return baseBean;
        }

        public ServerException setBaseBean(BaseBean baseBean) {
            this.baseBean = baseBean;
            return this;
        }
    }


}