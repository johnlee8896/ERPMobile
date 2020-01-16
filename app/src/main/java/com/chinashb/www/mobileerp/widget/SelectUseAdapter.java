package com.chinashb.www.mobileerp.widget;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.BaseRecycleAdapter;
import com.chinashb.www.mobileerp.adapter.BaseViewHolder;
import com.chinashb.www.mobileerp.bean.BuBean;
import com.chinashb.www.mobileerp.bean.CompanyBean;
import com.chinashb.www.mobileerp.bean.ReceiverCompanyBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/15 2:57 PM
 * @author 作者: liweifeng
 * @description 选择用途对应的adapter
 */
public class SelectUseAdapter<T> extends BaseRecycleAdapter<T, SelectUseAdapter.SelectUserViewHolder> {

    private OnViewClickListener onViewClickListener;

    public SelectUseAdapter setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

    @NonNull
    @Override
    public SelectUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectUserViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(SelectUserViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v ->{
            if (onViewClickListener != null){
                onViewClickListener.onClickAction(v,"",dataList.get(position));
            }
        });
    }

    public static class SelectUserViewHolder extends BaseViewHolder {

        @BindView(R.id.item_select_use_textView) TextView useTextView;

        public SelectUserViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_select_use_layout);
            ButterKnife.bind(this,itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            if (t instanceof String){

            String use = (String) t;
            if (use != null){
                useTextView.setText(use);
            }
            }else if (t instanceof CompanyBean){
                useTextView.setText(((CompanyBean)t).getCompanyChineseName());
            }else if (t instanceof BuBean){
                useTextView.setText(((BuBean)t).getBuName());
            }else if(t instanceof ReceiverCompanyBean){
                useTextView.setText(((ReceiverCompanyBean)t).getCustomer());
            }


        }
    }
}
