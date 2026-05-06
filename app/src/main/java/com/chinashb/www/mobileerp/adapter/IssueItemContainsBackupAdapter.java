package com.chinashb.www.mobileerp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.PlannInnerDetailContainBackupEntity;
import com.chinashb.www.mobileerp.widget.QueryStockDialog;

import java.text.DecimalFormat;
import java.util.List;

/***
 * @date 创建时间 3/29/26 10:08 PM
 * @author 作者: liweifeng
 * @description 饮食备料信息的投料bom列表
 */
public class IssueItemContainsBackupAdapter extends RecyclerView.Adapter<IssueItemContainsBackupAdapter.IssuedContainsBackUpItemViewHolder> {
    private final LayoutInflater inflater;
    private final Context mContext;
    private List<PlannInnerDetailContainBackupEntity> planInnerDetailEntityList;

    public IssueItemContainsBackupAdapter(Context context, List<PlannInnerDetailContainBackupEntity> Issued_ItemList) {
        planInnerDetailEntityList = Issued_ItemList;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public List<PlannInnerDetailContainBackupEntity> getDataList(){
        return planInnerDetailEntityList;
    }
    @Override
    public IssueItemContainsBackupAdapter.IssuedContainsBackUpItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_plan_inner_detail_contain_backup_layout, parent, false);
        IssueItemContainsBackupAdapter.IssuedContainsBackUpItemViewHolder viewHolder = new IssueItemContainsBackupAdapter.IssuedContainsBackUpItemViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final IssueItemContainsBackupAdapter.IssuedContainsBackUpItemViewHolder holder, int position) {
        final PlannInnerDetailContainBackupEntity Issued_Item = planInnerDetailEntityList.get(position);
        holder.tvItem.setText(Issued_Item.getItemName());
        holder.tvNextLocation.setText(Issued_Item.getNextLocation());
        //holder.tvNextLotNo.setText(PlannInnerDetailContainBackupEntity.getNextLotNo());

        DecimalFormat df4=new DecimalFormat("#####.####");


        holder.tvSingleQty.setText(df4.format(Issued_Item.getSingleQty()));
        holder.tvNeedQty.setText(df4.format(Issued_Item.getNeedQty()));
        holder.tvIssuedQty.setText(df4.format(Issued_Item.getIssuedQty()));
        holder.tvMoreQty.setText(df4.format(Issued_Item.getMoreQty()));

        holder.tvIssuedLastMoment.setText(Issued_Item.getLastIssueMoment());

        holder.itemIDTextView.setText(Issued_Item.getItem_ID() + "");
        holder.backUpNameTextView.setText(Issued_Item.getHR_Name());
        //下面这句不知为何有问题，一直显示 否
//        holder.hasBackUpTextView.setText(Issued_Item.isHasBackUp() ? "是" : "否");
        if (Issued_Item.getBackUpQty() > 0){
            holder.hasBackUpTextView.setText("是");
            holder.hasBackUpTextView.setTextColor(Color.RED);
        }else{
            holder.hasBackUpTextView.setText("否");
            holder.hasBackUpTextView.setTextColor(Color.BLACK);
        }
        holder.backUpQtyTextView.setText(Issued_Item.getBackUpQty() + "");



    }

    @Override
    public int getItemCount() {
        return planInnerDetailEntityList == null ? 0 : planInnerDetailEntityList.size();
    }

    public static class IssuedContainsBackUpItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        TextView tvNextLocation;
        //TextView tvNextLotNo;
        TextView tvSingleQty;
        TextView tvNeedQty;
        TextView tvIssuedQty;
        TextView tvIssuedLastMoment;
        TextView tvMoreQty;
        TextView itemIDTextView;

        TextView hasBackUpTextView;
        TextView backUpQtyTextView;
        TextView backUpNameTextView;


        IssuedContainsBackUpItemViewHolder(View view) {
            super(view);

            tvItem = (TextView)view.findViewById(R.id.backup_tv_issued_item_name);
            tvNextLocation=(TextView)view.findViewById(R.id.backup_tv_issued_nextlocation);
            //tvNextLotNo=(TextView)view.findViewById(R.id.backup_tv_issued_nextlotno);
            tvSingleQty=(TextView)view.findViewById(R.id.backup_tv_singleqty);
            tvNeedQty=(TextView)view.findViewById(R.id.backup_tv_needqty);
            tvIssuedQty=(TextView)view.findViewById(R.id.backup_tv_issuedqty);
            tvIssuedLastMoment=(TextView)view.findViewById(R.id.backup_tv_issued_last_moment);
            tvMoreQty=(TextView)view.findViewById(R.id.backup_tv_moreqty);

            itemIDTextView = view.findViewById(R.id.backup_tv_item_id_name_textView);
            hasBackUpTextView = view.findViewById(R.id.backup_tv_has_backup );
            backUpQtyTextView = view.findViewById(R.id.backup_tv_backup_qty );
            backUpNameTextView = view.findViewById(R.id.backup_tv_backup_name );



//            tvNextLocation.setOnClickListener(v -> {
            itemView.setOnClickListener(v -> {
//                Activity activity = mconte
                QueryStockDialog queryStockDialog = new QueryStockDialog(tvNextLocation.getContext());

                queryStockDialog.show();
//                queryStockDialog.setOnViewClickListener(onViewClickListener);
                queryStockDialog.setTitle("请选择或添加单据号");
                queryStockDialog.initiateView(Integer.parseInt(itemIDTextView.getText().toString()));
//                commonSelectInputDialog.refreshContent(getNOList());
            });


        }
    }
}
