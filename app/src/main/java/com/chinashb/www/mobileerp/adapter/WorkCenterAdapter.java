package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.WorkCenter;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;

import java.util.List;

/**
 * Created by Paul on 2017/1/21.
 */


public class WorkCenterAdapter extends RecyclerView.Adapter<WorkCenterAdapter.WorkerCenterViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<WorkCenter>  dataSoure;
    private OnItemClickListener mClickListener;

    public WorkCenterAdapter(Context context, List<WorkCenter> WorkCenterList) {
        dataSoure = WorkCenterList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<WorkCenter> getDataList(){
        return  dataSoure;
    }
    @Override
    public WorkerCenterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_workcenter, parent, false);
        WorkerCenterViewHolder vh = new WorkerCenterViewHolder(v,mClickListener);
        return vh;

    }

    @Override
    public void onBindViewHolder(final WorkerCenterViewHolder holder, int position) {
        final WorkCenter wc = dataSoure.get(position);

        holder.tvWCName.setText(wc.getWC_Name());
    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public static class WorkerCenterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvWCName;

        private  OnItemClickListener mListener;

        public WorkerCenterViewHolder(View itemView, OnItemClickListener listener)
        {
            super(itemView);

            mListener=listener;
            itemView.setOnClickListener(this);

            tvWCName =(TextView)itemView.findViewById(R.id.tv_listiew_wc_name);
        }

        WorkerCenterViewHolder(View view) {
            super(view);
            tvWCName = (TextView)view.findViewById(R.id.tv_listiew_wc_name);

        }

        @Override
        public void onClick(View v) {
            //mListener.OnItemClick(v, getAdapterPosition());
            Integer p = getPosition();
            if (mListener != null)
            {mListener.OnItemClick(v,p);}

        }
    }
}