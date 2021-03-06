package com.chinashb.www.mobileerp.basicobject;

import android.os.AsyncTask;

import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.funs.isLoadDataListener;
import com.google.gson.JsonObject;

import java.util.List;

public class QueryAsyncTask extends AsyncTask<String, Void, List<JsonObject>> {

    private isLoadDataListener loadListener;

    public void setLoadDataCompleteListener(isLoadDataListener dataComplete) {
        this.loadListener = dataComplete;
    }

    @Override
    protected List<JsonObject> doInBackground(String... strings) {

        String sql= strings[0];

        //Ws_Result result=WebServiceUtil.getDataTable(sql);

        List<JsonObject> jsonObjectList;
        jsonObjectList= WebServiceUtil.getJsonList(sql);

        //结果
        return jsonObjectList;

    }
    @Override
    protected void onPostExecute(List<JsonObject> result) {
    super.onPostExecute(result);

        if (loadListener != null) {
            loadListener.loadComplete(result);
        }
}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
