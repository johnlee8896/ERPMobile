package com.chinashb.www.mobileerp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.AssistItemBean;
import com.chinashb.www.mobileerp.bean.AssistItemNewBean;
import com.chinashb.www.mobileerp.bean.FreezeRecordBean;
import com.chinashb.www.mobileerp.bean.PickGoodsBean;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.warehouse.StockPartMoveActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 8/8/24 11:08 AM
 * @author 作者: liweifeng
 * @description single textview的列表adapter
 */
public class CommonSingleTextViewAdapter<TY> extends BaseRecycleAdapter<TY, CommonSingleTextViewAdapter.CommonItemViewHolder> {

    @NonNull
    @Override
    public CommonSingleTextViewAdapter.CommonItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommonSingleTextViewAdapter.CommonItemViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(CommonSingleTextViewAdapter.CommonItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TY ty = dataList .get(position);
                if (ty instanceof PickGoodsBean){
                    //这里 拣货的话，点击进入移库页面
                    Activity activity = (Activity) holder.itemView.getContext();
                    Intent intent = new Intent(activity, StockPartMoveActivity.class);
                    intent.putExtra(IntentConstant.Intent_Extra_Send_Goods_Move_from, true);
                    PickGoodsBean bean = (PickGoodsBean) dataList .get(position);
                    intent.putExtra(IntentConstant.Intent_Extra_to_pick_goods_bean,bean);
                    activity.startActivityForResult(intent, IntentConstant.Intent_Request_Code_Pick_Goods_To_Stock_Move_Activity);
                }

            }
        });
    }

    public static class CommonItemViewHolder extends BaseViewHolder {


        @BindView(R.id.item_single_common_textView) TextView commonTextView;

        public CommonItemViewHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.list_item_single_textview);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public <T> void initUIData(T t) {
            if (t instanceof PickGoodsBean) {
                PickGoodsBean pickGoodsBean = (PickGoodsBean) t;
                if (pickGoodsBean != null) {
//                    commonTextView.setText(String.format("大区:%s, 存储单元:%s,批次号:%s,Item_ID:%s, 规格型号:%s,物料名称:%s,数量:%s ",
//                            pickGoodsBean.getBigArea(),pickGoodsBean.getAreaUnit(),pickGoodsBean.getLotNo(),pickGoodsBean.getItemID() + "",
//                            pickGoodsBean.getSpec(),pickGoodsBean.getItemName(),pickGoodsBean.getQty() + ""));

                    commonTextView.setText(String.format(" 存储单元:%s,批次号:%s,Item_ID:%s, 规格型号:%s,物料名称:%s,数量:%s ",
                            pickGoodsBean.getAreaUnit(),pickGoodsBean.getLotNo(),pickGoodsBean.getItemID() + "",
                            pickGoodsBean.getSpec(),pickGoodsBean.getItemName(),pickGoodsBean.getQty() + ""));
                }
            }else if (t instanceof FreezeRecordBean){
                FreezeRecordBean freezeRecordBean = (FreezeRecordBean) t;
                if (freezeRecordBean != null){
                    String tempDateString = UnitFormatUtil.getFormatDateStringRemoveTYMD(freezeRecordBean.getOPTime().substring(6,freezeRecordBean.getOPTime().indexOf("+0800")));

                    commonTextView.setText(String.format(" 标签:%s\n, %s时间:%s\n, 操作员:%s, 备注:%s ",
                            freezeRecordBean.getSMTID() > 0 ?  "VG/" + freezeRecordBean.getSMTID() :"VE/" + freezeRecordBean.getSMLIID(),
                            freezeRecordBean.isFreeze() ? "冻结" : "解冻",
                            UnitFormatUtil.formatTimeToSecond(Long.parseLong(tempDateString)),
                            freezeRecordBean.getHRName1(),freezeRecordBean.getRemark()));
                }
            }else if (t instanceof AssistItemBean){
                AssistItemBean assistItemBean = (AssistItemBean) t;
                commonTextView.setText(String.format(" Item_ID:%s 名称:%s 物料编码:%s, 规格:%s, 单位:%s, 单机用量:%s, 备注:%s ",
                        assistItemBean.getItem_ID(),
                        assistItemBean.getItem_Name(),
                        assistItemBean.getItem(),assistItemBean.getItem_Spec2(),assistItemBean.getItem_Unit(),assistItemBean.getQty(), TextUtils.isEmpty(assistItemBean.getRemark()) ? "" : assistItemBean.getRemark() ));
            }else if (t instanceof AssistItemNewBean){
                // 2026-10-29 john 新SQL列表展示，两两换行，储位橙红色标注
                AssistItemNewBean assistItemNewBean = (AssistItemNewBean) t;
                commonTextView.setText(buildAssistItemNewText(assistItemNewBean, commonTextView.getContext()));
            }
        }

        private CharSequence buildAssistItemNewText(AssistItemNewBean bean, Context context) {
            String storageLocation = TextUtils.isEmpty(bean.getStorageLocation()) ? "" : bean.getStorageLocation();
            String itemName = TextUtils.isEmpty(bean.getItem_Name()) ? "" : bean.getItem_Name();
            String itemCode = TextUtils.isEmpty(bean.getItem()) ? "" : bean.getItem();
            String spec = TextUtils.isEmpty(bean.getItem_Spec2()) ? "" : bean.getItem_Spec2();
            String unit = TextUtils.isEmpty(bean.getItem_Unit()) ? "" : bean.getItem_Unit();
            String remark = TextUtils.isEmpty(bean.getRemark()) ? "" : bean.getRemark();
            String lotNo = TextUtils.isEmpty(bean.getLotNo()) ? "" : bean.getLotNo();
            String inboundTime = formatInboundTime(bean);

            String line1 = String.format(Locale.getDefault(), "储位:%s  名称:%s", storageLocation, itemName);
            String line2 = String.format(Locale.getDefault(), "物料编码:%s  Item_ID:%s", itemCode, bean.getItem_ID());
            String line3 = String.format(Locale.getDefault(), "规格:%s  单位:%s", spec, unit);
            String line4 = String.format(Locale.getDefault(), "单机用量:%s  库存数量:%s", bean.getQty(), bean.getInventoryQty());
            String line5 = String.format(Locale.getDefault(), "批次:%s", lotNo);
            String line6 = String.format(Locale.getDefault(), "入库时间:%s", inboundTime);
            String line7 = String.format(Locale.getDefault(), "备注:%s", remark);

            SpannableStringBuilder builder = new SpannableStringBuilder();
            int color = ContextCompat.getColor(context, R.color.color_orange_FF6A00);
            appendLineWithColor(builder, line1, "储位:" + storageLocation, color);
            builder.append("\n");
            appendLineWithColor(builder, line2, "Item_ID:" + bean.getItem_ID(), color);
            builder.append("\n").append(line3);
            builder.append("\n").append(line4);
            if (!TextUtils.isEmpty(lotNo)) {
                builder.append("\n");
                appendLineWithColor(builder, line5, "批次:" + lotNo, color);
            }
            if (!TextUtils.isEmpty(inboundTime)) {
                builder.append("\n").append(line6);
            }
            if (!TextUtils.isEmpty(remark)) {
                builder.append("\n").append(line7);
            }
            return builder;
        }

        private void appendLineWithColor(SpannableStringBuilder builder, String line, String coloredText, int color) {
            int lineStart = builder.length();
            int coloredStart = line.indexOf(coloredText);
            builder.append(line);
            if (coloredStart >= 0) {
                builder.setSpan(new ForegroundColorSpan(color),
                        lineStart + coloredStart, lineStart + coloredStart + coloredText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        private String formatInboundTime(AssistItemNewBean bean) {
            if (bean == null || TextUtils.isEmpty(bean.getInboundTime())) {
                return "";
            }
            String raw = bean.getInboundTime();
            int start = raw.indexOf("(");
            int end = raw.indexOf("+0800");
            if (start >= 0 && end > start) {
                try {
                    String millis = UnitFormatUtil.getFormatDateStringRemoveTYMD(raw.substring(start + 1, end));
                    return UnitFormatUtil.formatTimeToMinute(Long.parseLong(millis));
                } catch (Exception ignored) {
                }
            }
            return raw;
        }
    }
}


