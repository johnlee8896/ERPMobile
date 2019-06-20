package com.chinashb.www.mobileerp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/***
 * @date 创建时间 2019/6/20 9:05 AM
 * @author 作者: liweifeng
 * @description
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {


    public BaseViewHolder(ViewGroup viewGroup, int layoutId) {
        super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
    }

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public <T> void initUIData(T t, int position){
        initUIData(t);
    }

    public abstract <T> void initUIData(T t);

    public void onSelectPosition(boolean isSelected) {
    }

}
