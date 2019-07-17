package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;

import java.util.List;

/**
 * Created by Paul on 2017/1/21.
 */


public class Mpi_WcAdapter extends RecyclerView.Adapter<Mpi_WcAdapter.Mpi_WcViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<MpiWcBean> dataSoure;
    private OnItemClickListener mClickListener;
    public Boolean showdeletebutton = false;

    public Mpi_WcAdapter(Context context, List<MpiWcBean> Mpi_WcList) {
        dataSoure = Mpi_WcList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<MpiWcBean> getDataList() {
        return dataSoure;
    }

    @Override
    public Mpi_WcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_mpi_wc, parent, false);
        Mpi_WcViewHolder vh = new Mpi_WcViewHolder(v, mClickListener);
        return vh;

    }

    @Override
    public void onBindViewHolder(final Mpi_WcViewHolder holder, int position) {
        final MpiWcBean wc = dataSoure.get(position);
        if (wc == null ){
            return;
        }
        wc.setMwNameTextView(holder.tvMWName);
        holder.tvMWRemark.setText(wc.getMPI_Remark());

        if (!showdeletebutton ) {
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = holder.getAdapterPosition();
                dataSoure.remove(p);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public static class Mpi_WcViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvMWName;
        TextView tvMWRemark;
        Button btnDelete;

        private OnItemClickListener mListener;

        public Mpi_WcViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            mListener = listener;
            itemView.setOnClickListener(this);

            tvMWName = (TextView) itemView.findViewById(R.id.tv_listview_mpi_wc_name);
            tvMWRemark = (TextView) itemView.findViewById(R.id.tv_listview_mpi_wc_remark);
            btnDelete = (Button) itemView.findViewById(R.id.btn_remove_mw_from_list);
        }

        Mpi_WcViewHolder(View view) {
            super(view);
            tvMWName = (TextView) view.findViewById(R.id.tv_listview_mpi_wc_name);
            tvMWRemark = (TextView) itemView.findViewById(R.id.tv_listview_mpi_wc_remark);
            btnDelete = (Button) itemView.findViewById(R.id.btn_remove_mw_from_list);
        }

        @Override
        public void onClick(View v) {
            //mListener.OnItemClick(v, getAdapterPosition());
            Integer p = getPosition();
            if (mListener != null) {
                mListener.OnItemClick(v, p);
            }

        }
    }
}