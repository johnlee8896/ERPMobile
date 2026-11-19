package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

/**
 * 成品发货流程使用的单文本列表 adapter。
 */
public class ShipWorkSimpleAdapter<T> extends BaseRecycleAdapter<T, ShipWorkSimpleAdapter.SimpleViewHolder<T>> {

    public interface TextProvider<T> {
        String getText(T item);
    }

    public interface ItemClickListener<T> {
        void onItemClick(T item);
    }

    private final TextProvider<T> textProvider;
    private ItemClickListener<T> itemClickListener;

    public ShipWorkSimpleAdapter(TextProvider<T> textProvider) {
        this.textProvider = textProvider;
    }

    public ShipWorkSimpleAdapter<T> setOnItemClickListener(ItemClickListener<T> listener) {
        this.itemClickListener = listener;
        return this;
    }

    @NonNull
    @Override
    public SimpleViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleViewHolder<>(parent, textProvider);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder<T> holder, int position) {
        super.onBindViewHolder(holder, position);
        final T item = dataList.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(item);
            }
        });
    }

    public static class SimpleViewHolder<T> extends BaseViewHolder {
        final TextView textView;
        final TextProvider<T> textProvider;

        SimpleViewHolder(ViewGroup parent, TextProvider<T> textProvider) {
            super(parent, R.layout.item_ship_work_simple);
            textView = itemView.findViewById(R.id.item_ship_work_simple_textView);
            this.textProvider = textProvider;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void initUIData(Object item) {
            textView.setText(textProvider.getText((T) item));
        }
    }
}
