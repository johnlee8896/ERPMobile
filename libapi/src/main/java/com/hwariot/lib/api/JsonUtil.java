package com.hwariot.lib.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/***
 *@date 创建时间 2018/3/21 14:07
 *@author 作者: YuLong
 *@description Json的统一处理类
 */
public class JsonUtil {
    private static Gson gson;

    private static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().create();
        }
        return gson;
    }

    public static <T> T parseJsonToObject(String json, Type classOfT) throws JsonSyntaxException {
        return getGson().fromJson(json, classOfT);
    }

    public static <T> T parseJsonToObject(String json, Class<T> classOfT) throws JsonSyntaxException {
        return getGson().fromJson(json, classOfT);
    }

    public static <T> String objectToJson(T t) {
        return getGson().toJson(t);
    }

}