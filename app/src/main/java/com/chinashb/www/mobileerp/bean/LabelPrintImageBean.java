package com.chinashb.www.mobileerp.bean;

import java.io.Serializable;

/***
 * @date 创建时间 2026/5/28 10:16
 * @author 作者: code-x John
 * @description 标签打印图片信息
 */
public class LabelPrintImageBean implements Serializable {
    private String LabelType;
    private long ObjectID;
    private String MimeType;
    private String FileName;
    private String ImageBase64;
    private int Width;
    private int Height;

    public String getLabelType() {
        return LabelType;
    }

    public void setLabelType(String labelType) {
        LabelType = labelType;
    }

    public long getObjectID() {
        return ObjectID;
    }

    public void setObjectID(long objectID) {
        ObjectID = objectID;
    }

    public String getMimeType() {
        return MimeType;
    }

    public void setMimeType(String mimeType) {
        MimeType = mimeType;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getImageBase64() {
        return ImageBase64;
    }

    public void setImageBase64(String imageBase64) {
        ImageBase64 = imageBase64;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }
}
