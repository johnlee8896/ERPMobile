package com.chinashb.www.mobileerp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 3/31/26 6:51 PM
 * @author 作者: liweifeng
 * @description 扫描成品释放尾数
 */
public class BuWarehouseTailReleaseOutActivity extends BaseActivity {

    @BindView(R.id.product_bu_warehouse_out_release_scan_button) Button scanBoxButton;
    @BindView(R.id.product_bu_warehouse_out_release_warehouse_in_button) Button productBuWarehouseOutReleaseWarehouseInButton;
    @BindView(R.id.product_bu_warehouse_out_release_input_EditText) EditText inputEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warehouse_tail_out_release_layout);
        ButterKnife.bind(this);
    }

//    private class ExeWarehouseProductFATailReleaseAsyncTask extends AsyncTask<String, Void, Void> {
//        WsResult ws_result;
//
//        @Override
//        protected Void doInBackground(String... params) {
//
////            ws_result = WebServiceUtil.op_Product_Manu_In_Pallet(boxId, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), remark);
////            车间仓尾数入库
////            ws_result = WebServiceUtil.op_Product_Manu_In_Bu_Warehouse_Account(boxId, thePlace.getIst_ID(), thePlace.getSub_Ist_ID(), remark);
//            ws_result = WebServiceUtil.op_Scan_Product_To_Release_Fractional_Library(boxId, remark);
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            if (ws_result != null) {
//                if (!ws_result.getResult()) {
//
//                    CommonUtil.ShowToast(BuWarehouseTailReleaseOutActivity.this, ws_result.getErrorInfo(), R.mipmap.warning);
//
//                } else {
//                    //Toast.makeText(StockInActivity.this,"入库完成",Toast.LENGTH_LONG).show();
////                    CommonUtil.ShowToast(ProductScanBoxInActivity.this, "入库完成" + ws_result.getErrorInfo(), R.mipmap.smiley);
//                    CommonUtil.ShowToast(BuWarehouseTailReleaseOutActivity.this, "扫描尾数释放托盘成功！", R.mipmap.smiley);
//                    hasScanItem = false;
//                    inputEditText.setText("");
//
//                }
//
//            } else {
//                CommonUtil.ShowToast(BuWarehouseTailReleaseOutActivity.this, "扫描尾数入库失败！", R.mipmap.warning);
//
//            }
//        }
//
//    }
}
