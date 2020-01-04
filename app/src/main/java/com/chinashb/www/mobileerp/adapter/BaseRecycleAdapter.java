package com.chinashb.www.mobileerp.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/***
 * @date 创建时间 2019/6/20 9:04 AM
 * @author 作者: liweifeng
 * @description recyclerView对应的adapter
 */
public abstract class BaseRecycleAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<T> dataList;

    protected int selectPosition = -1;

    public BaseRecycleAdapter() {
    }

    public void deleteItem(T t) {
        dataList.remove(t);
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return dataList;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    public T getSelectItemObject() {
        if (dataList == null || selectPosition < 0) {
            return null;
        }
        return dataList.get(selectPosition % getRealCount());
    }

    //添加一条数据
    public void addOneData(T t) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.add(t);
        notifyDataSetChanged();
    }

    //添加一条数据,并清掉原来的数据
    public void setOneData(T t) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        if (dataList != null && dataList.size() > 0){
            dataList.clear();
        }
        dataList.add(t);
        notifyDataSetChanged();
    }

    //设置数据源
    public void setData(List<T> list) {
        this.dataList = list;
        notifyDataSetChanged();
    }

    //添加一组数据
    public void addData(List<T> list) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (holder == null) {
            return;
        }
        final T t = dataList.get(position);
        holder.initUIData(t, position);
        holder.onSelectPosition(position == selectPosition);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return getRealCount();
    }

    public int getRealCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }
}

