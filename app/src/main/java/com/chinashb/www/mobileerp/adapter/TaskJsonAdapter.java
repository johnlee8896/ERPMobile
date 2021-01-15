package com.chinashb.www.mobileerp.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.TaskBean;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2021/1/5 16:13
 * @author 作者: xxblwf
 * @description 任务列表的adapter
 */

public class TaskJsonAdapter extends BaseRecycleAdapter<TaskBean, TaskJsonAdapter.TaskViewHolder> {
    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    @NonNull @Override public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(parent);
    }

    @Override public void onBindViewHolder(TaskViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(v -> {
            if (onViewClickListener != null){
                onViewClickListener.onClickAction(v,"",dataList.get(position));
            }
        });
    }

    public static class TaskViewHolder extends BaseViewHolder {

        @BindView(R.id.task_creater_iamgeView) ImageView creatorImageView;
        @BindView(R.id.task_tv_task_creater) TextView createNameTextView;
        @BindView(R.id.task_tv_task_create_time) TextView createTimeTextView;
        @BindView(R.id.task_tv_task_title) TextView taskTitleTextView;
        @BindView(R.id.task_tv_task_executor) TextView executorTextView;
        @BindView(R.id.task_tv_task_finish_time) TextView dueTimeTextView;
        @BindView(R.id.task_tv_task_finish_time_done) TextView doneTimeTextView;

        public TaskViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.task_listview_task_layout);
            ButterKnife.bind(this, itemView);
        }

        @Override public <T> void initUIData(T t) {
            if (t != null) {
                TaskBean bean = (TaskBean) t;
                if (CommonUtil.userPictureMap.containsKey(bean.getCreaterHRID())) {
                    creatorImageView.setImageBitmap(CommonUtil.userPictureMap.get(bean.getCreaterHRID()));
                }
                taskTitleTextView.setText(bean.getTaskTitle());
                executorTextView.setText(bean.getResponder());
                createTimeTextView.setText(bean.getCreateTime());
                createNameTextView.setText(bean.getCreaterName());
                doneTimeTextView.setText(bean.getEndTime());
                dueTimeTextView.setText(bean.getStartTime());

            }
        }
    }


}
