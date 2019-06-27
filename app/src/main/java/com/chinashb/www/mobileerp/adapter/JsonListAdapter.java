package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Caleb on 2018/10/20.
 */

public class JsonListAdapter extends RecyclerView.Adapter<JsonListAdapter.JsonListViewHolder> {
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<JsonObject> jsonObjectLit;
    private OnItemClickListener mClickListener;
    private static JsonObject firstJsonObject;
    private LinearLayout linearLayout;

    public List<Integer> ColWidth;
    public List<String> HiddenCol;

    public JsonListAdapter(Context context, List<JsonObject> jsonList) {
        jsonObjectLit = jsonList;
        this.context = context;
        if (jsonList != null) {
            if (jsonList.size() >= 1) {
                firstJsonObject = jsonList.get(0);
            }
        }
        layoutInflater = LayoutInflater.from(context);
    }

    public List<JsonObject> getDataList() {
        return jsonObjectLit;
    }

    @Override
    public JsonListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.listview_jsonlist, parent, false);
        linearLayout = (LinearLayout) v.findViewById(R.id.json_linear_layout);
        dynamic_create_linear_layout();

        JsonListViewHolder vh = new JsonListViewHolder(v, mClickListener);
        return vh;

    }

    protected void dynamic_create_linear_layout() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);


        Integer i = 0;
        if (firstJsonObject != null) {
            Set<String> cols = firstJsonObject.keySet();
            Iterator<String> iterator = cols.iterator();
            while (iterator.hasNext()) {
                String colname = iterator.next();
                if (HiddenCol != null && HiddenCol.contains(colname)) {
                    i++;
                    continue;
                }

                TextView textView = addTextView(colname);
                if (ColWidth != null && ColWidth.size() > i) {
                    textView.setWidth(calculateDpToPx(ColWidth.get(i)));
                }

                linearLayout.addView(textView, lp);
                textView.setPadding(calculateDpToPx(5), calculateDpToPx(5), 0, calculateDpToPx(5));

                i++;
            }

        }
    }

    private int calculateDpToPx(int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    private TextView addTextView(String Name) {
        TextView tv = new TextView(context);
        tv.setTag(Name);
        return tv;
    }

    @Override
    public void onBindViewHolder(final JsonListViewHolder holder, int position) {
        final JsonObject obj = jsonObjectLit.get(position);

        HashMap<String, TextView> textViews = holder.stringTextViewHashMap;

        if (firstJsonObject != null) {
            Set<String> cols = firstJsonObject.keySet();
            Iterator<String> iterator = cols.iterator();
            while (iterator.hasNext()) {
                String colname = iterator.next();
                if (!obj.get(colname).isJsonNull()) {
                    String colvalue = obj.get(colname).getAsString();
                    TextView colview = textViews.get(colname);

                    if (colview != null) {
                        colview.setText(colvalue);
                    }
                }

            }
        }

    }

    @Override
    public int getItemCount() {
        return jsonObjectLit == null ? 0 : jsonObjectLit.size();
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public static class JsonListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private HashMap<String, TextView> stringTextViewHashMap;
        private OnItemClickListener onItemClickListener;

        public JsonListViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            onItemClickListener = listener;
            itemView.setOnClickListener(this);
            setStringTextViewHashMap(itemView);
        }

        JsonListViewHolder(View view) {
            super(view);
            setStringTextViewHashMap(view);
        }

        public void setStringTextViewHashMap(View itemView) {
            stringTextViewHashMap = new HashMap<String, TextView>();

            if (firstJsonObject != null) {
                Set<String> cols = firstJsonObject.keySet();
                Iterator<String> iterator = cols.iterator();
                while (iterator.hasNext()) {
                    String colname = iterator.next();
                    TextView colview = (TextView) itemView.findViewWithTag(colname);

                    if (colview != null) {
                        stringTextViewHashMap.put(colname, colview);
                    }
                }
            }
        }

        @Override
        public void onClick(View v) {
            //onItemClickListener.OnItemClick(v, getAdapterPosition());
            onItemClickListener.OnItemClick(v, getPosition());
        }
    }
}