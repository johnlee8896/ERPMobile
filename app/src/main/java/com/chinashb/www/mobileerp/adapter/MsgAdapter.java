package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinashb.www.mobileerp.MobileMainActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.Msg;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.singleton.UserSingleton;

import java.util.List;

/**
 * Created by Paul on 2017/1/21.
 */


public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<Msg> dataSoure;
    private OnItemClickListener mClickListener;

    public MsgAdapter(Context context, List<Msg> wcListList) {
        dataSoure = wcListList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<Msg> getDataList() {
        return dataSoure;
    }

    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_msg, parent, false);

        //LinearLayout test = (LinearLayout)v.findViewById(R.id.layoutTest);
        //LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
        //              LinearLayout.LayoutParams.WRAP_CONTENT);

        MsgViewHolder vh = new MsgViewHolder(v, mClickListener);

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
    public void onBindViewHolder(final MsgViewHolder holder, int position) {
        final Msg msg = dataSoure.get(position);

//        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        //holder.tvTime.setText(sdf.format(msg.msgTime));
        holder.tvTime.setText(msg.msgTimes);
        holder.tvMsg.setText(msg.Msg);

        if (msg.mSenderID == UserSingleton.get().getHRID()) {
            holder.ivNotMe.setVisibility(View.INVISIBLE);
            holder.ivMe.setVisibility(View.VISIBLE);
            if (CommonUtil.pictureBitmap != null) {
                holder.ivMe.setImageBitmap(CommonUtil.pictureBitmap);

            }
            //holder.tvMsg.setGravity(Gravity.RIGHT);

        } else {
            holder.ivMe.setVisibility(View.INVISIBLE);
            holder.ivNotMe.setVisibility(View.VISIBLE);
            holder.ivNotMe.setImageBitmap(msg.HR_Pic2);
        }

        //调整TextView宽度
        TextView t = holder.tvMsg;
        try {
            //LinearLayout llMsg=holder.llMsg;
            //int mtvMsgWidth=dip2px(mContext,80);
            float textWidth = t.getPaint().measureText(t.getText().toString());
            if (t.getWidth() > textWidth) {
                t.setWidth((int) textWidth);


            }
        } catch (Exception e) {
        }


    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public void setOnItemClickListener(OnItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public static class MsgViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTime;
        TextView tvMsg;
        ImageView ivNotMe;
        ImageView ivMe;
        //LinearLayout llMsg;

        //TextView tvType;
        //ImageView testA;
        //ImageView testB;
        //TextView tvX;

        private OnItemClickListener mListener;

        public MsgViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);

            mListener = listener;
            itemView.setOnClickListener(this);


            tvTime = (TextView) itemView.findViewById(R.id.tv_conversation_time);
            tvMsg = (TextView) itemView.findViewById(R.id.tv_msg_content);
            ivNotMe = (ImageView) itemView.findViewById(R.id.iv_talker_not_me);
            ivMe = (ImageView) itemView.findViewById(R.id.iv_talker_me);
            //llMsg=(LinearLayout)itemView.findViewById(R.id.linear_layout_converation_msg);

            //tvType=(TextView) itemView.findViewById(R.id.tv_listview_wclist_type);
            //testA=(ImageView)itemView.findViewWithTag("A");
            //testB=(ImageView)itemView.findViewWithTag("B");
            //tvX=(TextView)itemView.findViewWithTag("X");
        }

        MsgViewHolder(View view) {
            super(view);

            tvTime = (TextView) itemView.findViewById(R.id.tv_conversation_time);
            tvMsg = (TextView) itemView.findViewById(R.id.tv_msg_content);
            ivNotMe = (ImageView) itemView.findViewById(R.id.iv_talker_not_me);
            ivMe = (ImageView) itemView.findViewById(R.id.iv_talker_me);
            //llMsg=(LinearLayout)itemView.findViewById(R.id.linear_layout_converation_msg);
        }

        @Override
        public void onClick(View v) {
            //mListener.OnItemClick(v, getAdapterPosition());
            if (mListener != null) {
                mListener.OnItemClick(v, getPosition());
            }

        }
    }
}