package com.chinashb.www.mobileerp.funs;

import android.view.View;

import com.google.gson.JsonObject;

import java.util.List;

public interface OnLoadDataListener {
    void loadComplete(List<JsonObject>result);
}

