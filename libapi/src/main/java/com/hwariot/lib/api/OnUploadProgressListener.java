package com.hwariot.lib.api;

import java.io.File;

/***
 * @date 创建时间 2018/11/14 14:18
 * @author 作者: W.YuLong
 * @description
 */
public interface OnUploadProgressListener {
    void onRequestProgress(File file, long progress, long contentLength);
}
