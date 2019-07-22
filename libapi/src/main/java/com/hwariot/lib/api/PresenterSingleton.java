package com.hwariot.lib.api;

import android.content.Context;

import com.hwariot.lib.APILibAPP;
import com.hwariot.lib.bean.BaseBean;
import com.hwariot.lib.tools.dialog.loadingdialog.LoadingDialogManagerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/***
 * @date 创建时间 2018/4/19 22:53
 * @author 作者: yulong
 * @description 通用的网络请求封装库
 */
public class PresenterSingleton {
    public static final int ERROR_JSON_FORMAT = -100;
    public static final int ERROR_RESPONSE_FORMAT = -99;
    public static final int ERROR_No_netWork = -101;


    private static Map<String, PresenterSingleton> singletonMap = new HashMap<>();
    protected CompositeDisposable compositeDisposable;
    protected RetrofitService retrofitService;

    /*** 缓存时间 7天*/
    private static final int CACHE_TIME = 60 * 60 * 24 * 7;
    /*** 设置网络请求超时时间*/
    private static final int TIME_OUT = 30;
    private volatile OkHttpClient okHttpClient;

    public static void clearAllData() {
        singletonMap.clear();
    }

    public static PresenterSingleton get(String service) {
        if (singletonMap.get(service) == null) {
            synchronized (PresenterSingleton.class) {
                if (singletonMap.get(service) == null) {
                    PresenterSingleton singleton = new PresenterSingleton(service);
                    singletonMap.put(service, singleton);
                }
            }
        }
        return singletonMap.get(service);
    }

    public static PresenterSingleton get() {
        return get(APILibAPP.getDefaultService());
    }


    private PresenterSingleton(String service) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(APILibAPP.getBaseAPIUrl(service))
                .client(getOkHttpClient())
                .addConverterFactory(new ToStringConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofitService = retrofit.create(RetrofitService.class);

        compositeDisposable = new CompositeDisposable();
    }


    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (PresenterSingleton.class) {
                Cache cache = new Cache(new File(APILibAPP.get().getCacheDir() + "HttpCache"),
                        1024 * 1024 * 100);
                if (okHttpClient == null) {
                    HttpLogInterceptor logInterceptor = new HttpLogInterceptor();

                    okHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
//                            .addInterceptor(interceptor)
                            .addNetworkInterceptor(logInterceptor)
                            .addInterceptor(headerInterceptor)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    /**
     * 配置缓存策略
     * 有网络读服务器最新数据，没网读缓存
     */
    private final Interceptor cacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetWorkUtils.isNetworkAvailable()) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response response = chain.proceed(request);
            if (NetWorkUtils.isNetworkAvailable()) {
                response.newBuilder().header("Cache-Control", "public, max-age=" + 0)
                        .removeHeader("Pragma")
                        .build();
            } else {
                response.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_TIME)
                        .removeHeader("Pragma")
                        .build();
            }

            return response;
        }
    };

    private final Interceptor headerInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Authorization", APILibAPP.getToken())
                    .header("version", APILibAPP.getAPIVersion())
                    .header("clientName", "Bao-Android")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        }
    };


    protected RequestBody createRequestBody(String content) {
        return RequestBody.create(MediaType.parse("application/json"), content);
    }


    public String urlToType(String url) {
        if (url.toLowerCase().contains(".jpg")
                || url.toLowerCase().contains(".png")
                || url.toLowerCase().contains(".jpeg")) {
            return TYPE_IMAGE;
        } else if (url.toLowerCase().contains(".mp4")
                || url.toLowerCase().contains(".rmvb")
                || url.toLowerCase().contains(".3gp")
                || url.toLowerCase().contains(".avi")
                || url.toLowerCase().contains(".mov")
                || url.toLowerCase().contains(".wmv")
                || url.toLowerCase().contains(".mkv")) {
            return TYPE_VIDEO;
        } else {
            return TYPE_UNKNOW;
        }

    }

    public static final String TYPE_UNKNOW = "*/*";
    public static final String TYPE_IMAGE = "image/*";
    public static final String TYPE_VIDEO = "video/*";

    /***************************POST请求********************************************************/
    //直接用OKHTTP的方式上传文件，注意得要在异步线程调用
    public Response uploadFileWithOKHttp(String url, Map<String, String> map, String filePath) throws IOException {
        File file = new File(filePath);
        return uploadFileWithOKHttp(url, map, file);
    }

    /**
     * 直接用OKHTTP的方式上传文件，注意得要在异步线程调用
     */
    public Response uploadFileWithOKHttp(String url, Map<String, String> map, File file) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        //文件存在，且要大于10KB
        if (file != null && file.exists() && file.length() > 10240) {
            RequestBody imageBody;
            if (urlToType(file.getAbsolutePath()).equals(TYPE_VIDEO)) {
                imageBody = RequestBody.create(MediaType.parse("video/mp4"), file);
            } else if (urlToType(file.getAbsolutePath()).equals(TYPE_IMAGE)) {
                imageBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
            } else {
                imageBody = RequestBody.create(MediaType.parse("*/*"), file);
            }
            builder.addFormDataPart("file", file.getName(), imageBody);
        }

        Request request = new Request.Builder().url(url).post(builder.build()).build();
        Response response = getOkHttpClient().newCall(request).execute();
        return response;
    }


    /*Post请求*/
    public void doPostAction(String api, Map<String, String> postMap, Type type, BaseViewInterface viewInterface) {
        doPostAction(null, api, postMap, type, viewInterface);
    }


    /*带有Loading的Post请求*/
    public void doPostAction(Context context, String api, Map<String, String> map, Type type, BaseViewInterface viewInterface) {
        String postJson = JsonUtil.objectToJson(map);
        compositeDisposable.add(getCommDisposable(retrofitService.postData(api, createRequestBody(postJson)),
                context, api, type, viewInterface));
    }

    public void doPostAction(Context context, String api, Type type, BaseViewInterface viewInterface) {
        compositeDisposable.add(getCommDisposable(retrofitService.postNoBody(api), context, api, type, viewInterface));
    }

    public void doPostAction(String api, String postJson, Type type, BaseViewInterface viewInterface) {
        doPostAction(null, api, postJson, type, viewInterface);
    }

    public void doPostAction(Context context, String api, String postJson, Type type, BaseViewInterface viewInterface) {
        compositeDisposable.add(getCommDisposable(retrofitService.postData(api, createRequestBody(postJson)),
                context, api, type, viewInterface));
    }

    /*Post的请求,可直接传实体类*/
    public <E> void doPostActionWithEntity(String api, E e, Type type, BaseViewInterface viewInterface) {
        doPostActionWithEntity(null, api, e, type, viewInterface);
    }

    /*Post的请求,可直接传实体类*/
    public <E> void doPostActionWithEntity(Context context, String api, E e, Type type, BaseViewInterface viewInterface) {
        String json = JsonUtil.objectToJson(e);
        compositeDisposable.add(getCommDisposable(retrofitService.postData(api, createRequestBody(json)),
                context, api, type, viewInterface));
    }


/***************************GET请求********************************************************/
    /*** 不带参数的Get请求*/
    public void doGetData(String api, Type type, BaseViewInterface viewInterface) {
        doGetData(null, api, type, viewInterface);
    }

    public void doGetData(Context context, String api, Type type, BaseViewInterface viewInterface) {
        compositeDisposable.add(getCommDisposable(retrofitService.getData(api), context, api, type, viewInterface));
    }


    /*** 带有参数的Get请求*/
    public void doGetData(Context context, String api, Map<String, String> queryMap, Type type, BaseViewInterface viewInterface) {
        compositeDisposable.add(getCommDisposable(retrofitService.getListDataWithQuery(api, queryMap), context, api, type, viewInterface));
    }


    /**********************PUT请求*****************************************************/
    public void doPutAction(Context context, String api, Map<String, String> putMap, Type type, BaseViewInterface viewInterface) {
        compositeDisposable.add(getCommDisposable(retrofitService.putData(api, createRequestBody(JsonUtil.objectToJson(putMap))), context, api, type, viewInterface));
    }

    public void doPutAction(Context context, String api, String requestBody, Type type, BaseViewInterface viewInterface) {
        compositeDisposable.add(getCommDisposable(retrofitService.putData(api, createRequestBody(requestBody)), context, api, type, viewInterface));
    }

    public void doPutAction(Context context, String api, Type type, BaseViewInterface viewInterface) {
        compositeDisposable.add(getCommDisposable(retrofitService.putNoBody(api), context, api, type, viewInterface));
    }

    /****************DELETE操作*****************************************/

    public void doDeleteWithBodyAction(Context context, String api, HashMap<String, Object> map, Type type, BaseViewInterface viewInterface) {
        compositeDisposable.add(getCommDisposable(retrofitService.deleteDataWithQuery(api, map), context, api, type, viewInterface));
    }

    public void doDeleteAction(Context context, String api, Type type, BaseViewInterface viewInterface) {
        compositeDisposable.add(getCommDisposable(retrofitService.deleteData(api), context, api, type, viewInterface));
    }


    private <T> Disposable getCommDisposable(Flowable<String> flowable, Context context, String api, Type type, BaseViewInterface viewInterface) {
        if (!NetWorkUtils.isNetworkAvailable() && context != null) {
            LoadingDialogManagerSingleton.get().showNoNetworkAlertDialog(context);
            BaseBean data = new BaseBean<>();
            data.setCode(ERROR_No_netWork);
            data.setMessage("无网络连接");
            viewInterface.onFail(api, data);
        }
        return flowable.map(new Function<String, BaseBean<T>>() {
            @Override
            public BaseBean<T> apply(String s) {
                BaseBean<T> data;
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.has("message") && jsonObject.has("code")) {
                        data = JsonUtil.parseJsonToObject(s, type);
                    } else {
                        data = new BaseBean<>();
                        data.setCode(ERROR_JSON_FORMAT);
                        data.setMessage("返回的Json格式不标准");
                        APILog.e("json format error", s);
                    }
                } catch (JSONException e) {
                    data = new BaseBean<>();
                    data.setCode(ERROR_RESPONSE_FORMAT);
                    data.setMessage("response is not json text.");
                    APILog.e("response is not json text.", s);
                }
                return data;
            }
        }).compose(RxUtil.handleResult()).subscribeWith(
                new ABSNetWorkResponseProxy<T>(compositeDisposable, context) {
                    @Override
                    protected void onNextAction(T t) {
                        if (t != null) {
                            try {
                                viewInterface.initData(api, t);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    protected void onErrorAction(BaseBean msg) {
                        viewInterface.onFail(api, msg);
                    }
                });
    }
}
