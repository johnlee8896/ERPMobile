package com.chinashb.www.mobileerp.upgrade;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2018/5/22 17:22
 * @author 作者: W.YuLong
 * @description APP版本升级的Bean
 */
public class VersionBean {
    /**
     * id : 8
     * created_time : 1527009136000
     * updated_time : 1527009136000
     * app_name : 3
     * app_client : 2
     * url : https://test-media-hwariot-com.oss-cn-hangzhou.aliyuncs.com/upload/c59d02444add4eb78abe63db9db1e04d.apk
     * code : 12
     * description : 12121
     12312312
     * version_name : 111
     * update_tag : 0
     * is_active : false
     */

    @SerializedName("id") private int id;
    @SerializedName("app_name") private int appName;
    @SerializedName("app_client") private int appClient;
    @SerializedName("app_url") private String url;
    @SerializedName("app_code") private int code;
    @SerializedName("app_description") private String description;
    @SerializedName("version_name") private String versionName;
    @SerializedName("update_tag") private int updateTag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppName() {
        return appName;
    }

    public void setAppName(int appName) {
        this.appName = appName;
    }

    public int getAppClient() {
        return appClient;
    }

    public void setAppClient(int appClient) {
        this.appClient = appClient;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getUpdateTag() {
        return updateTag;
    }

    public void setUpdateTag(int updateTag) {
        this.updateTag = updateTag;
    }
}
