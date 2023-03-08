package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.funs.CommonUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Caleb on 2018/9/21.
 */


public class IssueMoreItemAdapter extends RecyclerView.Adapter<IssueMoreItemAdapter.IssueMoreItemViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<BoxItemEntity> boxItemEntityList;

    public Boolean showNeedMore = true;

    public IssueMoreItemAdapter(Context context, List<BoxItemEntity> boxItemEntityList) {
        this.boxItemEntityList = boxItemEntityList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<BoxItemEntity> getDataList() {
        return boxItemEntityList;
    }

    @Override
    public IssueMoreItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_issue_more_items, parent, false);
        IssueMoreItemViewHolder vh = new IssueMoreItemViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final IssueMoreItemViewHolder holder, int position) {
        final BoxItemEntity Box_Item = boxItemEntityList.get(position);
        holder.tvItem.setText(Box_Item.getItemName());

        holder.tvLotNo.setText(Box_Item.getLotNo());
        holder.tvBoxNo.setText(Box_Item.getBoxNameNo());

//        holder.tvSMLRemark.setText(CommonUtil.isNothing2String(Box_Item.getSmlRemark(), ""));
        holder.tvSMLRemark.setText(CommonUtil.isNothing2String(Box_Item.getSmlRemark(), ""));
        holder.tvLotDescription.setText(Box_Item.getLotDescription());

        DecimalFormat dfs = new DecimalFormat("####.####");
        holder.etQty.setText(dfs.format(Box_Item.getQty()));
        holder.qtyTextView.setText(dfs.format(Box_Item.getQty()));

        if (Box_Item.getCanNotEdit()){
            holder.etQty.setVisibility(View.GONE);
            holder.qtyTextView.setVisibility(View.VISIBLE);
        }else{
            holder.etQty.setVisibility(View.VISIBLE);
            holder.qtyTextView.setVisibility(View.GONE);
        }

        if (!showNeedMore) {
            holder.tvNeedTitle.setVisibility(View.GONE);
            holder.tvNeed.setVisibility(View.GONE);
        }
        holder.tvNeed.setText(CommonUtil.DecimalFormat(Box_Item.getNeedMoreQty()));

        holder.etQty.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
                //Log.e("输入过程中执行该方法", "文字变化");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 输入前的监听
                //Log.e("输入前确认执行该方法", "开始输入");
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听
                //Log.e("输入结束执行该方法", "输入结束");

                try {
                    String q = s.toString();
                    if (!q.isEmpty()) {
                        if (Float.parseFloat(q) > 0) {
                            Box_Item.setQty(Float.parseFloat(q));
                        } else {
                            Box_Item.setQty(0);
                        }
                    } else {
                        Box_Item.setQty(0);
                    }
                } finally {

                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return boxItemEntityList == null ? 0 : boxItemEntityList.size();
    }

    public static class IssueMoreItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        TextView tvLotNo;
        TextView tvSMLRemark;
        TextView tvBoxNo;
        TextView tvNeedTitle;
        TextView tvNeed;
        TextView tvLotDescription;
        EditText etQty;
        TextView qtyTextView;


        IssueMoreItemViewHolder(View view) {
            super(view);

            tvItem = (TextView) view.findViewById(R.id.tv_issue_more_item_name);
            tvLotNo = (TextView) view.findViewById(R.id.tv_issue_more_item_lotno);
            tvSMLRemark = (TextView) view.findViewById(R.id.tv_issue_more_item_smlremark);
            tvBoxNo = (TextView) view.findViewById(R.id.tv_issue_more_item_boxno);
            tvNeedTitle = (TextView) view.findViewById(R.id.tv_issue_more_item_needmore_row);
            tvNeed = (TextView) view.findViewById(R.id.tv_issue_more_item_needmore);
            tvLotDescription = (TextView) view.findViewById(R.id.tv_issue_more_item_lot_description);
            etQty = (EditText) view.findViewById(R.id.et_issue_more_qty);
            qtyTextView = view.findViewById(R.id.tv_issue_more_qty);


        }
    }
}