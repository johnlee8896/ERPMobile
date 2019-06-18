package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.MealTypeEntity;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Paul on 2017/1/21.
 */


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<MealTypeEntity>  dataSoure;

    public OrderAdapter(Context context,List<MealTypeEntity> mealTypeList) {
        dataSoure = mealTypeList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public List<MealTypeEntity> getDataList(){
        return  dataSoure;
    }
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.listview_items, parent, false);
        OrderViewHolder vh = new OrderViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, int position) {
        final MealTypeEntity mealType = dataSoure.get(position);
        holder.cbxBreakfast.setChecked(mealType.isBreakfast());
        holder.cbxLunch.setChecked(mealType.isLunch());
        holder.cbxDinner.setChecked(mealType.isDinner());
        holder.cbxSnack.setChecked(mealType.isSnack());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(mealType.getDate());
        holder.tvDate.setText(dateString);
        holder.cbxBreakfast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mealType.setBreakfast(compoundButton.isChecked());

            }
        });
        holder.cbxLunch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mealType.setLunch(compoundButton.isChecked());
            }
        });
        holder.cbxDinner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mealType.setDinner(compoundButton.isChecked());
            }
        });
        holder.cbxSnack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mealType.setSnack(compoundButton.isChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSoure == null ? 0 : dataSoure.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        CheckBox cbxBreakfast;
        CheckBox cbxLunch;
        CheckBox cbxDinner;
        CheckBox cbxSnack;

        OrderViewHolder(View view) {
            super(view);
            tvDate = (TextView)view.findViewById(R.id.tv_date);
            cbxBreakfast = (CheckBox) view.findViewById(R.id.cbx_breakfast);
            cbxLunch = (CheckBox) view.findViewById(R.id.cbx_lunch);
            cbxDinner = (CheckBox) view.findViewById(R.id.cbx_dinner);
            cbxSnack = (CheckBox) view.findViewById(R.id.cbx_snack);

        }
    }
}