package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.ShipWorkActiveBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.warehouse.ShipWorkWorkflow;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 成品发货 - 执行中发货作业列表 adapter
 */
public class ShipWorkActiveAdapter extends BaseRecycleAdapter<ShipWorkActiveBean, ShipWorkActiveAdapter.ShipWorkActiveViewHolder> {

    private OnViewClickListener onViewClickListener;

    public ShipWorkActiveAdapter setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

    @NonNull
    @Override
    public ShipWorkActiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShipWorkActiveViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ShipWorkActiveViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ShipWorkActiveBean bean = dataList.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (onViewClickListener != null) {
                onViewClickListener.onClickAction(v, "", bean);
            }
        });
    }

    public static class ShipWorkActiveViewHolder extends BaseViewHolder {

        @BindView(R.id.item_ship_work_no_textView) TextView noTextView;
        @BindView(R.id.item_ship_work_status_textView) TextView statusTextView;
        @BindView(R.id.item_ship_work_stage_textView) TextView stageTextView;
        @BindView(R.id.item_ship_work_track_textView) TextView trackTextView;
        @BindView(R.id.item_ship_work_count_textView) TextView countTextView;

        public ShipWorkActiveViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_ship_work_active);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            ShipWorkActiveBean bean = (ShipWorkActiveBean) t;
            if (bean == null) {
                return;
            }
            String batch = bean.getDaily_Sequence() <= 0
                    ? "本次发货作业" : "第 " + bean.getDaily_Sequence() + " 次发货";
            noTextView.setText("作业号 " + bean.getWork_ID() + "  " + batch);
            statusTextView.setText("状态 " + ShipWorkWorkflow.statusText(bean.getWork_Status())
                    + "  客户 " + (bean.getCustomer_Name() == null ? "" : bean.getCustomer_Name()));
            stageTextView.setText("发货区 " + bean.getStage_Name());
            trackTextView.setText("物流单号 " + bean.getTrackNo()
                    + "  物流ID " + bean.getDelivery_ID());
            countTextView.setText("已锁定" + bean.getLocked_Count()
                    + " 已入区" + bean.getStaged_Count()
                    + " 已扫描" + bean.getScanned_Count()
                    + " 已发运" + bean.getPosted_Count());
        }
    }
}
