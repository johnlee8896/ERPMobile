package com.chinashb.www.mobileerp.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.BigAreaQtyAdapter;
import com.chinashb.www.mobileerp.bean.BigAreaSumBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ScreenUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2024/2/27 4:02 PM
 * @author 作者: liweifeng
 * @description 在查询库存前加一选大区页面
 */
public class SelectStorageAreaDialog extends BaseDialog implements View.OnClickListener {
//    private  List<SelectStorageAreaBean> SelectStorageAreaBeanList;
    @BindView(R.id.dialog_select_storage_area_recyclerView) CustomRecyclerView recyclerView;
    @BindView(R.id.dialog_storage_area_confirm_Button) Button confirmButton;
    @BindView(R.id.dialog_storage_area_cancel_Button) Button cancelButton;
    private OnViewClickListener onViewClickListener;
//    private SelectStorageAreaAdapter adapter;
    private BigAreaQtyAdapter adapter;
//    private int Ac_Type = 1;
    private int item_id = 0;

//    public SelectStorageAreaDialog(@NonNull Context context, int Ac_Type,List<SelectStorageAreaBean> SelectStorageAreaBeanList) {
//        super(context);
//        this.Ac_Type = Ac_Type;
//        this.SelectStorageAreaBeanList = SelectStorageAreaBeanList;
//    }

    public SelectStorageAreaDialog(@NonNull Context context, int item_id) {
        super(context);
        this.item_id = item_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_storage_area_layout);
        ButterKnife.bind(this);
        configDialog(Gravity.CENTER);
        setCanceledOnTouchOutside(false);
        adapter = new BigAreaQtyAdapter();
        recyclerView.setAdapter(adapter);
//        String[] useArray = APP.get().getResources().getStringArray(R.array.select_use_array);
//        List<SelectStorageAreaBean> useList = new ArrayList<>();
//        for (int i = 0; i < useArray.length; i++) {
//            useList.add(useArray[i]);
//        }

        GetStorageAreaListAsyncTask task = new GetStorageAreaListAsyncTask();
        task.execute();
//        if (SelectStorageAreaBeanList != null && SelectStorageAreaBeanList.size() > 0) {
//            adapter.setData(SelectStorageAreaBeanList);
//        }
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);





    }

    protected void configDialog(int gravity) {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.gravity = gravity;// 设置重力
        if (gravity != Gravity.CENTER) {
            getWindow().setWindowAnimations(R.style.bottomDialogWindowAnim);
        }
        getWindow().setAttributes(wl);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        getWindow().setLayout((int) (ScreenUtil.getScreenWidth() * 0.75),
                WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
        adapter.setOnViewClickListener(onViewClickListener);
    }


    @Override public void onClick(View v) {
        if (v == confirmButton){
            if (onViewClickListener != null){
//                onViewClickListener.onClickAction(v,null,adapter.getList());
                onViewClickListener.onClickAction(v,null,adapter.getSelectAreaEntityList());
            }
            dismiss();
        }else if (v == cancelButton){
            if (onViewClickListener != null){
//                onViewClickListener.onClickAction(v,null,adapter.getList());
                onViewClickListener.onClickAction(v,null,null);
            }
            dismiss();
        }
    }

    private class GetStorageAreaListAsyncTask extends AsyncTask<String, Void, List<BigAreaSumBean>> {
//        List<BigAreaSumBean> storageAreaBeanList;

        @Override
        protected List<BigAreaSumBean> doInBackground(String... params) {
//            上面是集团内销售的，要不同公司
//                String buId = params[0];
            //这个是原来分大区的
//            String sql = String.format("Select * from Item_Storage_Layout where ac_type = %d and bu_id = %d",
//                        Ac_Type,UserSingleton.get().getUserInfo().getBu_ID());
//
//            String sql = String.format("select Item_ID,sum(Qty) as SumQty,item_storage_layout_item.ISL_ID,item_storage_layout.LayOut_Name from Account_Storage_Part2_81_Inv\n" +
//                            " left join Item_storage on Account_Storage_Part2_81_Inv.ist_id = Item_storage.ist_id\n" +
//                            " left join item_storage_layout_item on item_storage_layout_item.ist_id  = Item_storage.ist_id\n" +
//                            " left join item_storage_layout on item_storage_layout.isl_id = item_storage_layout_item.ISL_ID\n" +
//                            " where item_id  = 28099 group by item_storage_layout.isl_id,item_storage_layout_item.ISL_ID,Item_ID,item_storage_layout.LayOut_Name",
//                    Ac_Type,UserSingleton.get().getUserInfo().getBu_ID());


//            int itemId = params[0];
            String js = WebServiceUtil.getQueryPartBigAreaItem(UserSingleton.get().getUserInfo().getBu_ID(), item_id);
            Gson gson = new Gson();
            List<BigAreaSumBean> itemLotInvList = gson.fromJson(js, new TypeToken<List<BigAreaSumBean>>() {
            }.getType());
            return itemLotInvList;

        }

        @Override
        protected void onPostExecute(List<BigAreaSumBean> result) {
            if (result != null && result.size() > 0) {
                adapter.setData(result);
            }
        }

    }
}

