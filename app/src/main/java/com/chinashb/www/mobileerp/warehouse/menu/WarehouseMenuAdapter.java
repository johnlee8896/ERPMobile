package com.chinashb.www.mobileerp.warehouse.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2026/5/2 15:06
 * @author 作者: code-x John
 * @description 仓库菜单适配器
 */
public class WarehouseMenuAdapter extends RecyclerView.Adapter<WarehouseMenuAdapter.MenuHolder> {

    public interface OnMenuClickListener {
        void onMenuClick(WarehouseMenuItem item);
    }

    private final Context context;
    private final List<WarehouseMenuItem> dataList = new ArrayList<>();
    private OnMenuClickListener onMenuClickListener;

    public WarehouseMenuAdapter(Context context) {
        this.context = context;
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    public void setData(List<WarehouseMenuItem> list) {
        dataList.clear();
        if (list != null) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_warehouse_menu_card, parent, false);
        return new MenuHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuHolder holder, int position) {
        final WarehouseMenuItem item = dataList.get(position);
        holder.iconImageView.setImageResource(item.getIconRes());
        holder.titleTextView.setText(item.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onMenuClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class MenuHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.warehouse_menu_icon_imageView)
        ImageView iconImageView;
        @BindView(R.id.warehouse_menu_title_textView)
        TextView titleTextView;

        MenuHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
