package com.chinashb.www.mobileerp.funs;

import android.view.View;

import com.google.gson.JsonObject;

import java.util.List;

public interface isLoadDataListener {
    public void loadComplete(List<JsonObject>result);
}

