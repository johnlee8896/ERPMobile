package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.MobileMainActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by Caleb on 2018/10/20.
 */


public class TaskJsonListAdapter extends RecyclerView.Adapter<TaskJsonListAdapter.JsonListViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<JsonObject> dataSoure;
    private OnItemClickListener mClickListener;
    static JsonObject sample;
    private LinearLayout linearLayout;

    public List<Integer> ColWidth;
    public List<String> HiddenCol;

    public TaskJsonListAdapter(Context context, List<JsonObject> jsonList) {
        dataSoure = jsonList;
        mContext = context;

        if (jsonList != null) {
            if (jsonList.size() >= 1) {
                sample = jsonList.get(0);
            }
        }
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<JsonObject> getDataList() {
        return dataSoure;
    }

    @Override
    public JsonListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_task_layout, parent, false);


        JsonListViewHolder vh = new JsonListViewHolder(v, mClickListener);
        return vh;

    }


    @Override
    public void onBindViewHolder(final JsonListViewHolder holder, int position) {
        final JsonObject obj = dataSoure.get(position);

        //HashMap<String,TextView> stringTextViewHashMap = holder.stringTextViewHashMap;

        Integer creater = obj.get("Creater").getAsInt();

        if (!obj.get("任务").isJsonNull() ) {
            holder.tvTask.setText(obj.get("任务").getAsString());
        } else {
            holder.tvTask.setText("（没有填写任务标题）");
        }
        if (!obj.get("责任人").isJsonNull() ) {
            holder.tvExer.setText(obj.get("责任人").getAsString());
        } else {
            holder.tvExer.setText("");
        }
        if (!obj.get("Create_Time").isJsonNull() ) {
            holder.tvCreateTime.setText(obj.get("Create_Time").getAsString());
        } else {
            holder.tvCreateTime.setText("?");
        }

        if (!obj.get("End_Time").isJsonNull() ) {
            holder.tvEndTime.setText(obj.get("End_Time").getAsString());
        } else {
            holder.tvEndTime.setText("TBD");
        }

        if (CommonUtil.userPictureMap.containsKey(creater)) {
            holder.ivCreater.setImageBitmap(CommonUtil.userPictureMap.get(creater));
        }
        if (!obj.get("制定人").isJsonNull() ) {
            holder.tvCreater.setText(obj.get("制定人").getAsString());
        } else {
            holder.tvCreater.setText("");
        }

        //holder.ivCreater.set;

    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public static class JsonListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //HashMap<String,TextView> stringTextViewHashMap;

        TextView tvCreateTime;
        TextView tvCreater;
        TextView tvTask;
        ImageView ivCreater;
        TextView tvExer;
        TextView tvEndTime;

        private OnItemClickListener mListener;

        public JsonListViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            mListener = listener;
            itemView.setOnClickListener(this);

            setTextViews(itemView);

        }

        JsonListViewHolder(View view) {
            super(view);
            setTextViews(view);
        }

        public void setTextViews(View itemView) {
            //stringTextViewHashMap = new HashMap<String, TextView>();

            tvCreateTime = (TextView) itemView.findViewById(R.id.tv_task_create_time);
            tvCreater = (TextView) itemView.findViewById(R.id.tv_task_creater);
            ivCreater = (ImageView) itemView.findViewById(R.id.iv_task_creater);
            tvTask = (TextView) itemView.findViewById(R.id.tv_task_title);
            tvEndTime = (TextView) itemView.findViewById(R.id.tv_task_plan_end_time);
            tvExer = (TextView) itemView.findViewById(R.id.tv_task_exer);

        }

        @Override
        public void onClick(View v) {
            //mListener.OnItemClick(v, getAdapterPosition());
            mListener.OnItemClick(v, getPosition());
        }
    }
}