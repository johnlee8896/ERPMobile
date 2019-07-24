package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.WorkLineSelectEntity;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2019/7/24 9:42
 * @author 作者: xxblwf
 * @description 选择产线adapter
 */

public class SelectWorkLineAdapter extends BaseRecycleAdapter<WorkLineSelectEntity, SelectWorkLineAdapter.SelectWorkLineViewHolder> {

    private OnViewClickListener onViewClickListener;
    private List<String> selectWorkLIneStringList;

    public SelectWorkLineAdapter setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

    public List<String> getSelectWorkLIneStringList() {
        selectWorkLIneStringList = new ArrayList<>();
        for (WorkLineSelectEntity entity : dataList){
            if (entity.isSelect()){
                selectWorkLIneStringList.add(entity.getWorkLinName());
            }
        }
        return selectWorkLIneStringList;
    }

    @NonNull
    @Override
    public SelectWorkLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectWorkLineViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(SelectWorkLineViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v -> {
            if (holder.imageView.getVisibility() == View.GONE){
                holder.imageView.setVisibility(View.VISIBLE);
            }else{
                holder.imageView.setVisibility(View.GONE);
            }
            dataList.get(position).setSelect(holder.imageView.getVisibility() == View.VISIBLE);
//            if (onViewClickListener != null) {
//                onViewClickListener.onClickAction(v, "", dataList.get(position));
//            }
        });
    }

    public static class SelectWorkLineViewHolder extends BaseViewHolder {
        @BindView(R.id.item_select_work_line_textView) TextView workLineTextView;
        @BindView(R.id.item_select_work_line_imageView) ImageView imageView;

        public SelectWorkLineViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_select_work_line_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            WorkLineSelectEntity entity = (WorkLineSelectEntity) t;
            if (entity != null) {
                workLineTextView.setText(entity.getWorkLinName());
            }
        }
    }
}
