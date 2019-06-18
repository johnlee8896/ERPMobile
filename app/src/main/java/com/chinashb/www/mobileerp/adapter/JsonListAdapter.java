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
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<JsonObject>  dataSoure;
    private OnItemClickListener mClickListener;
    static JsonObject sample;
    private LinearLayout linearLayout;

    public List<Integer> ColWidth;
    public List<String> HiddenCol;

    public JsonListAdapter(Context context, List<JsonObject> jsonList) {
        dataSoure = jsonList;
        mContext = context;

        if(jsonList!=null)
        {if (jsonList.size()>=1)
        {
            sample=jsonList.get(0);
        }
        }
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<JsonObject> getDataList(){
        return  dataSoure;
    }
    @Override
    public JsonListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_jsonlist, parent, false);

        linearLayout=(LinearLayout)v.findViewById(R.id.json_linear_layout);

        dynamic_create_linear_layout();

        JsonListViewHolder vh = new JsonListViewHolder(v,mClickListener);
        return vh;

    }

    protected void dynamic_create_linear_layout()
    {
        LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);


        Integer i =0 ;
        if (sample != null)
        {
            Set<String> cols= sample.keySet();
            Iterator<String> iterator= cols.iterator();
            while (iterator.hasNext())
            {
                String colname = iterator.next();

                if(HiddenCol !=null && HiddenCol.contains(colname))
                {
                    i++;
                    continue;
                }

                TextView textView=addTextView(colname);
                if(ColWidth !=null && ColWidth.size()>i)
                {textView.setWidth(calculateDpToPx(ColWidth.get(i)));}

                linearLayout.addView(textView,lp);
                textView.setPadding(calculateDpToPx(5),calculateDpToPx(5),0,calculateDpToPx(5));

                i++;
            }

        }
    }
    private int calculateDpToPx(int dp){
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return  (int) (dp * scale + 0.5f);
    }


    private TextView addTextView(String Name) {
        TextView tv = new TextView(mContext);
        tv.setTag(Name);
        return tv;
    }

    @Override
    public void onBindViewHolder(final JsonListViewHolder holder, int position) {
        final JsonObject obj = dataSoure.get(position);

        HashMap<String,TextView> textViews = holder.textViews;

        if (sample != null)
        {
            Set<String> cols= sample.keySet();
            Iterator<String> iterator= cols.iterator();
            while (iterator.hasNext())
            {
                String colname = iterator.next();
                if(!obj.get(colname).isJsonNull())
                {
                    String colvalue = obj.get(colname).getAsString();
                    TextView colview = textViews.get(colname);

                    if(colview!=null)
                    {
                        colview.setText(colvalue);
                    }
                }

            }
        }

    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public static class JsonListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        HashMap<String,TextView> textViews;

        private  OnItemClickListener mListener;

        public JsonListViewHolder(View itemView, OnItemClickListener listener)
        {
            super(itemView);

            mListener=listener;
            itemView.setOnClickListener(this);

            setTextViews(itemView);

        }

        JsonListViewHolder(View view) {
            super(view);
            setTextViews(view);
        }

        public void setTextViews(View itemView)
        {
            textViews = new HashMap<String, TextView>();

            if (sample != null)
            {
                Set<String> cols= sample.keySet();
                Iterator<String> iterator= cols.iterator();
                while (iterator.hasNext())
                {
                    String colname = iterator.next();

                    TextView colview = (TextView)itemView.findViewWithTag(colname);

                    if(colview!=null)
                    {
                        textViews.put(colname, colview);
                    }
                }
            }
        }

        @Override
        public void onClick(View v) {
            //mListener.OnItemClick(v, getAdapterPosition());
            mListener.OnItemClick(v,getPosition());
        }
    }
}