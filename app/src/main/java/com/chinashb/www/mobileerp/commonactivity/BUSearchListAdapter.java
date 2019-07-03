package com.chinashb.www.mobileerp.commonactivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.BaseRecycleAdapter;
import com.chinashb.www.mobileerp.adapter.BaseViewHolder;
import com.chinashb.www.mobileerp.bean.BUItemBean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BUSearchListAdapter extends BaseRecycleAdapter<BUItemBean, BUSearchListAdapter.BUSearchViewHolder> {
    @Override
    public BUSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BUSearchViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(BUSearchViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("SelectItem", dataList.get(position));
                Activity activity = (Activity) holder.itemView.getContext();
                activity.setResult(1, intent);
                activity.finish();

            }
        });
    }

    public static class BUSearchViewHolder extends BaseViewHolder {

        @BindView(R.id.item_search_company_textView)
        TextView companyTextView;
        @BindView(R.id.item_search_brief_company_textView)
        TextView briefCompanyTextView;
        @BindView(R.id.item_search_bu_number_textView)
        TextView buNumberTextView;
        @BindView(R.id.item_search_bu_name_textView)
        TextView buNameTextView;

        public BUSearchViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_bu_search_list_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            BUItemBean buItemBean = (BUItemBean) t;
            if (buItemBean != null) {
                companyTextView.setText(buItemBean.getCompanyChineseName());
                briefCompanyTextView.setText(buItemBean.getBrief());
//                buNumberTextView.setText(buItemBean.getBUId());
                buNameTextView.setText(buItemBean.getBUName());
            }
        }
    }



}
