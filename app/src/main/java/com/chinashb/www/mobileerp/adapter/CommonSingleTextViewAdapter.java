package com.chinashb.www.mobileerp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.FreezeRecordBean;
import com.chinashb.www.mobileerp.bean.PickGoodsBean;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.warehouse.StockPartMoveActivity;

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
            }
        }
    }
}


