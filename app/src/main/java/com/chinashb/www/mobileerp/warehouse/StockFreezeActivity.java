package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.AdapterFreezeBoxItem;
import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.CustomScannerActivity;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 冻结库存页面
 */
public class StockFreezeActivity extends BaseActivity {
    private Button btnAddTray;
    private Button btnAddRemark;
    private Button btnFreezeBox;
    private Button btnFreezeNot;
    private EditText inputEditText;
    private RecyclerView mRecyclerView;
    private String remark = "";
    private TextView remarkTitleTextView;

    private AdapterFreezeBoxItem boxitemAdapter;
    private List<BoxItemEntity> boxitemList;
    private String scanstring;
    private CommonSelectInputDialog remarkDialog;

    private OnViewClickListener remarkOnViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                remark = (String) t;
            }
            //// TODO: 5/6/25 能调用 此方法说明是点了确定按钮，取消按钮则是直接dismiss
            if (remark.length() > 0){
                ToastUtil.showToastShort("备注添加成功！");
                remarkTitleTextView.setText(String.format("备注：%s",remark));
                remarkTitleTextView.setTextColor(getResources().getColor(R.color.color_orange_F58B23));
                if (remarkDialog != null && remarkDialog.isShowing()) {
                    remarkDialog.dismiss();
                }
            }else{
                ToastUtil.showToastShort("备注为空！");
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_freeze_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_freeze_box);

        btnAddTray = (Button) findViewById(R.id.btn_move_add_tray);
        btnAddRemark = (Button) findViewById(R.id.btn_freeze_remark);
        btnFreezeBox = (Button) findViewById(R.id.btn_freeze_box);
        btnFreezeNot = (Button) findViewById(R.id.btn_freeze_not);
        inputEditText = findViewById(R.id.stock_freeze_input_EditeText);
        remarkTitleTextView = findViewById(R.id.tv_freeze_box_title);

        boxitemList = new ArrayList<>();

        boxitemAdapter = new AdapterFreezeBoxItem(StockFreezeActivity.this, boxitemList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(boxitemAdapter);


        btnAddTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (boxitemList.size() < 10) {
                    new IntentIntegrator(StockFreezeActivity.this).setCaptureActivity(CustomScannerActivity.class).initiateScan();
                } else {
                    Toast.makeText(StockFreezeActivity.this, "一次操作不超过10个托盘 ", Toast.LENGTH_LONG).show();
                }

            }

        });

        btnAddRemark.setOnClickListener(v -> {
            if (remarkDialog == null) {
                remarkDialog = new CommonSelectInputDialog(StockFreezeActivity.this);
            }
            remarkDialog.show();
            remarkDialog.setInputDialogTitle("请添加备注");
            remarkDialog.setInputOnly(true);
            remarkDialog.setOnViewClickListener(remarkOnViewClickListener);
        });

        btnFreezeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (boxitemList.size() > 0) {
                    AsyncExeFreezeBox task = new AsyncExeFreezeBox();
                    task.execute();

                }

            }

        });


        btnFreezeNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (boxitemList.size() > 0) {
                    AsyncExeFreezeNot task = new AsyncExeFreezeNot();
                    task.execute();
                }

            }

        });

        inputEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override public void onTextChangedSafe(CharSequence text) {
                //                if (text.toString().endsWith("\n")) {
                if (text.toString().length() > 0) {
//                    ToastUtil.showToastLong("扫描结果:" + text.toString());
                    System.out.println("========================扫描结果:" + text.toString());
                    parseScanResult(text.toString());
                }
            }
        });


    }

    private void parseScanResult(String result) {
        //// TODO: 2023/9/15 去掉下面这个scanned提示
//        Toast.makeText(this, "Scanned: " + result, Toast.LENGTH_LONG).show();
//        String X = result.getContents();
        if (result.contains("/")) {
            String[] qrContent;
            qrContent = result.split("/");
            if (qrContent.length >= 2) {
                String qrTitle = qrContent[0];
                if (!qrTitle.equals("")) {
                    if (qrTitle.equals("VE") || qrTitle.equals("VF") || qrTitle.equals("VG") || qrTitle.equals("V9") || qrTitle.equals("VA") || qrTitle.equals("VB") || qrTitle.equals("VC")) {
                        //物品条码
                        scanstring = result;
                        AsyncGetBox task = new AsyncGetBox();
                        task.execute();
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setHomeButton() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //不要重复启动扫码
                //new IntentIntegrator(StockMoveActivity.this).initiateScan();
            } else {
                parseScanResult(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class AsyncGetBox extends AsyncTask<String, Void, Void> {
        BoxItemEntity scanresult;

        @Override
        protected Void doInBackground(String... params) {

            BoxItemEntity bi = WebServiceUtil.op_Check_Commit_Freeze_Item_Barcode(scanstring);

            scanresult = bi;

            if (bi.getResult() ) {
                if (!is_box_existed(bi)) {
                    bi.setSelect(true);
                    boxitemList.add(bi);
                } else {
                    bi.setResult(false);
                    bi.setErrorInfo("该包装已经在列表中");
                }

            } else {

            }

            return null;
        }

        protected Boolean is_box_existed(BoxItemEntity box_item) {
            Boolean result = false;

            if (boxitemList != null) {
                for (int i = 0; i < boxitemList.size(); i++) {
                    if (boxitemList.get(i).getDIII_ID() == box_item.getDIII_ID()) {
                        return true;
                    }
                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if (scanresult != null) {
                if (!scanresult.getResult() ) {
                    Toast.makeText(StockFreezeActivity.this, scanresult.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }


            mRecyclerView.setAdapter(boxitemAdapter);

            remarkTitleTextView.setText("冻结/解冻托盘");
            remarkTitleTextView.setTextColor(getResources().getColor(R.color.black));
            //// TODO: 7/14/25
            inputEditText.setText("");
        }


    }


    private class AsyncExeFreezeBox extends AsyncTask<String, Void, Void> {
        WsResult wsResult;
        @Override
        protected Void doInBackground(String... params) {

            for (int i = 0; i < boxitemList.size(); i++) {
                BoxItemEntity bi = boxitemList.get(i);
                wsResult = WebServiceUtil.op_Commit_Freeze_Inv(bi,remark, UserSingleton.get().getUserInfo().getBu_ID());
                bi.setWs_result(wsResult);
                if (wsResult.getResult() ) {
                    bi.setFreezeStatus("冻结");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (wsResult != null ){
                if (wsResult.getResult()){
                    ToastUtil.showToastShort("执行冻结成功！");
                }else {
                    ToastUtil.showToastShort("执行冻结失败，错误原因：" + wsResult.getErrorInfo());
                }
                boxitemAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(boxitemAdapter);
                remark = "";
            }else{
                ToastUtil.showToastShort("执行冻结失败");
            }
            inputEditText.setText("");
            //tv.setText(fahren + "∞ F");
            //boxitemAdapter= new AdapterFreezeBoxItem(StockFreezeActivity.this, boxitemList);



        }

    }

    private class AsyncExeFreezeNot extends AsyncTask<String, Void, Void> {
        WsResult wsResult;
        @Override
        protected Void doInBackground(String... params) {

            for (int i = 0; i < boxitemList.size(); i++) {
                BoxItemEntity bi = boxitemList.get(i);
                wsResult = WebServiceUtil.op_Commit_FreezeNot_Inv(bi,remark,UserSingleton.get().getUserInfo().getBu_ID());
                bi.setWs_result(wsResult);
                if (wsResult.getResult() ) {
                    bi.setFreezeStatus("正常");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (wsResult != null ){
                if (wsResult.getResult()){
                    ToastUtil.showToastShort("执行解冻成功！");
                }else {
                    ToastUtil.showToastShort("执行解冻失败，错误原因：" + wsResult.getErrorInfo());
                }
                boxitemAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(boxitemAdapter);
                remark = "";
            }else{
                ToastUtil.showToastShort("执行解冻失败");
            }
            inputEditText.setText("");

        }


    }



}
