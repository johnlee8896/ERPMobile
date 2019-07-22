package com.hwariot.lib.api;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface RetrofitService {

    @GET
    Flowable<String> getListDataWithQuery(@Url String url, @QueryMap Map<String, String> map);

    @GET
    Flowable<String> getData(@Url String url);

    @Headers({"Content-Type: application/json"})
    @PUT
    Flowable<String> putData(@Url String url, @Body RequestBody request);

    @PUT
    Flowable<String> putNoBody(@Url String url);

    @POST
    Flowable<String> postNoBody(@Url String url);

    @POST
    Flowable<String> postData(@Url String url, @Body RequestBody request);

    @DELETE
    Flowable<String> deleteData(@Url String url);

    @DELETE
    Flowable<String> deleteDataWithQuery(@Url String url, @QueryMap Map<String, Object> map);

}
