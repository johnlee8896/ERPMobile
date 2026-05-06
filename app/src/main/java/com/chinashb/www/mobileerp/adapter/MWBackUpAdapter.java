package com.chinashb.www.mobileerp.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.IssueOutBackUpBean;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 1/21/26 2:43 PM
 * @author 作者: liweifeng
 * @description 备料adapter
 */
public class MWBackUpAdapter extends BaseRecycleAdapter<IssueOutBackUpBean, MWBackUpAdapter.MWBackUpItemViewHolder> {

    private OnViewClickListener onViewClickListener;
    private OnReworkClickListener onReworkClickListener;

    public MWBackUpAdapter setOnReworkClickListener(OnReworkClickListener onReworkClickListener) {
        this.onReworkClickListener = onReworkClickListener;
        return this;
    }

    private SparseBooleanArray selectedStates = new SparseBooleanArray();


    public MWBackUpAdapter setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        return this;
    }

    @NonNull
    @Override
    public MWBackUpItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MWBackUpItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(MWBackUpItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemCheckbox.setChecked(selectedStates.get(position, false));
        IssueOutBackUpBean backUpBean = dataList.get(position);
//        backUpBean.setSelected(holder.itemCheckbox.isSelected());
//        notifyItemChanged(position);
        holder.itemView.setOnClickListener(v -> {
//            holder.itemView.setSelected(!holder.itemView.isSelected());
//            if (onViewClickListener != null) {
//                onViewClickListener.onClickAction(v, "", holder.itemView.isSelected() ? dataList.get(position) : null);
//            }
//            holder.itemCheckbox.setChecked(holder.itemCheckbox.isSelected());
            boolean newState = !selectedStates.get(position, false);
            selectedStates.put(position, newState);
//            backUpBean.setSelected(holder.itemCheckbox.isChecked());
            backUpBean.setSelected(newState);
            notifyItemChanged(position); // 触发状态更新
            if (onViewClickListener != null) {
//                onViewClickListener.onClickAction(v,"" , holder.itemView.isSelected() ? backUpBean: null);
                onViewClickListener.onClickAction(v, "", backUpBean);
            }
        });

        holder.reworkButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getactivity)
            if (onReworkClickListener != null){
                onReworkClickListener.onReworkClick(backUpBean);
            }
        });


    }

    public static class MWBackUpItemViewHolder extends BaseViewHolder {

        /**
         * POI_ID : 1338971
         * PO_ID : 226056
         * PO_No : D0901202512270001
         * 下单日期 : /Date(1766764800000+0800)/
         * KisCode : 4.01.026.0001
         * Item_ID : 48327
         * 物料编码 : P-SE00600-8001
         * 物料 : 托盘
         * 规格 : 1115X870X620
         * 版本 : A1
         * 单位 : 个
         * 采购数量 : 50.0
         * 已关联数量 : 0.0
         * 未关联数量 : 50.0
         * 金蝶采购单号 : CGDD226204
         */
        @BindView(R.id.item_common_first_info_textView) TextView firstInfoTextView;
        @BindView(R.id.item_first_name_textView) TextView firstNameTextView;
        @BindView(R.id.item_second_info_textView) TextView secondInfoTextView;
        @BindView(R.id.item_second_name_textView) TextView secondNameTextView;
        @BindView(R.id.item_third_info_textView) TextView thirdInfoTextView;
        @BindView(R.id.item_third_name_textView) TextView thirdNameTextView;
        @BindView(R.id.item_fourth_info_textView) TextView fourthInfoTextView;
        @BindView(R.id.item_fourth_name_textView) TextView fourthNameTextView;
        @BindView(R.id.item_fifth_info_textView) TextView fifthInfoTextView;
        @BindView(R.id.item_fifth_name_textView) TextView fifthNameTextView;
        @BindView(R.id.item_sixth_info_textView) TextView sixthInfoTextView;
        @BindView(R.id.item_sixth_name_textView) TextView sixthNameTextView;
        @BindView(R.id.item_seventh_info_textView) TextView seventhInfoTextView;
        @BindView(R.id.item_seventh_name_textView) TextView seventhNameTextView;
        @BindView(R.id.item_common_item_checkbox) CheckBox itemCheckbox;
        @BindView(R.id.item_common_action_button) Button reworkButton;

        public MWBackUpItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_common_list_six_item);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            IssueOutBackUpBean entity = (IssueOutBackUpBean) t;
            if (entity != null) {

                firstInfoTextView.setText("Item_ID：");
                secondInfoTextView.setText("版本：");
                thirdInfoTextView.setText("批次号：");
                fourthInfoTextView.setText("库位：");
                fifthInfoTextView.setText("条码：");
                sixthInfoTextView.setText("数量：");
                seventhInfoTextView.setText("备料员：");
                itemCheckbox.setVisibility(View.VISIBLE);

                firstNameTextView.setText(entity.getItemId() + " " + entity.getTemName());
                secondNameTextView.setText(entity.getItemVersion());
                thirdNameTextView.setText(entity.getLotNo());

                fourthNameTextView.setText(entity.getIstName());
                fifthNameTextView.setText(entity.getScanX());
                sixthNameTextView.setText(entity.getQty() + "");

                fifthNameTextView.setTextSize(14);
                fifthNameTextView.setTextColor(Color.BLACK);
                sixthNameTextView.setTextSize(14);
                sixthNameTextView.setTextColor(Color.BLACK);
                seventhNameTextView.setTextSize(14);
                seventhNameTextView.setTextColor(Color.BLACK);


            }
        }
    }

//    // 新增接口用于外部监听选中状态
//    public interface OnCustomizedCheckedChangeListener {
//        void onItemChecked(int position, boolean isChecked);
//    }
//
//    private OnCustomizedCheckedChangeListener onCheckedChangeListener;
//    public void setOnCheckedChangeListener(OnCustomizedCheckedChangeListener listener) {
//        this.onCheckedChangeListener = listener;
//    }

    public interface OnReworkClickListener{
        void onReworkClick(IssueOutBackUpBean backUpBean);
    }
}



