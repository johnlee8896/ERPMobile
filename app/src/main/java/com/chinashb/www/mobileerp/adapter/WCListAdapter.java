package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.s_WCList;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;

import java.util.List;

/**
 * Created by Paul on 2017/1/21.
 */


public class WCListAdapter extends RecyclerView.Adapter<WCListAdapter.WCListViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<s_WCList>  dataSoure;
    private OnItemClickListener mClickListener;

    public WCListAdapter(Context context, List<s_WCList> wcListList) {
        dataSoure = wcListList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<s_WCList> getDataList(){
        return  dataSoure;
    }
    @Override
    public WCListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_wclist, parent, false);

        LinearLayout test = (LinearLayout)v.findViewById(R.id.layoutTest);
        LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        //test.addView(addFocusView("A"));
        //test.addView(addNormalView("X"),lp);
        //test.setGravity(Gravity.CENTER);
        WCListViewHolder vh = new WCListViewHolder(v,mClickListener);

        return vh;

    }

    private View addFocusView(String Name) {
        ImageView iv = new ImageView(mContext);
        //iv.setImageResource(R.mipmap.box);
        iv.setTag(Name);
        return iv;
    }

    private View addNormalView(String Name) {
        //LinearLayout layout = new LinearLayout(mContext);
        /*layout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView iv = new ImageView(mContext);
        iv.setImageResource(R.mipmap.ic_launcher);
        layout.addView(iv, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        */
        TextView tv = new TextView(mContext);

        tv.setTag(Name);
        return tv;
    }

    @Override
    public void onBindViewHolder(final WCListViewHolder holder, int position) {
        final s_WCList s_WCList = dataSoure.get(position);

        holder.tvWCLName.setText(s_WCList.getListName());
        holder.tvType.setText(s_WCList.getWCL_Type());
        //holder.testA.setImageResource(R.mipmap.bingo);
        //holder.testB.setImageResource(R.mipmap.box_2);
        //holder.tvX.setText(s_WCList.getListName());
    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public static class WCListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvWCLName;
        TextView tvType;
        //ImageView testA;
        //ImageView testB;
        //TextView tvX;

        private  OnItemClickListener mListener;

        public  WCListViewHolder(View itemView, OnItemClickListener listener)
        {
            super(itemView);

            mListener=listener;
            itemView.setOnClickListener(this);

            tvWCLName=(TextView)itemView.findViewById(R.id.tv_listiew_wclist_name);
            tvType=(TextView) itemView.findViewById(R.id.tv_listview_wclist_type);
            //testA=(ImageView)itemView.findViewWithTag("A");
            //testB=(ImageView)itemView.findViewWithTag("B");
            //tvX=(TextView)itemView.findViewWithTag("X");
        }

        WCListViewHolder(View view) {
            super(view);
            tvWCLName = (TextView)view.findViewById(R.id.tv_listiew_wclist_name);
            tvType=(TextView) itemView.findViewById(R.id.tv_listview_wclist_type);
        }

        @Override
        public void onClick(View v) {
            //mListener.OnItemClick(v, getAdapterPosition());
            mListener.OnItemClick(v,getPosition());
        }
    }
}