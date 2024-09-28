package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.CommonItemCommonSixAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.PickGoodsBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.chinashb.www.mobileerp.widget.TimePickerManager;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 7/24/24 3:38 PM
 * @author 作者: liweifeng
 * @description 零件拣货任务
 */
public class PickGoodsActivity extends BaseActivity implements View.OnClickListener , OnViewClickListener {
    @BindView(R.id.common_search_title_titleManagerView) TitleLayoutManagerView titleManagerView;
    @BindView(R.id.common_search_action_TextView) TextView searchTextView;
    @BindView(R.id.common_et_keyword_input) EditText searchEditText;
    @BindView(R.id.common_search_clear_input_ImageView) ImageView clearImageView;
    @BindView(R.id.common_rv_select_list) CustomRecyclerView recyclerView;
    @BindView(R.id.common_common_search_list_emptyManager) EmptyLayoutManageView emptyLayoutManageView;
    @BindView(R.id.pick_goods_today_button) Button todayButton;
    @BindView(R.id.pick_goods_tomorrow_button) Button tomorrowButton;
    @BindView(R.id.pick_goods_tomorrowplus1_button) Button tomorrowPlus1Button;
    @BindView(R.id.pick_goods_tomorrowplus2_button) Button tomorrowPlus2Button;
    @BindView(R.id.pick_goods_time_area_start_button) Button startTimeButton;
    @BindView(R.id.pick_goods_time_area_end_button) Button endTimeButton;
    @BindView(R.id.pick_goods_time_area_layout) LinearLayout timeAreaLayout;

    private CommonItemCommonSixAdapter adapter;
    private List<?> originalBUDataList;
    private CommProgressDialog progressDialog;
    private Date startDate;
    private Date endDate;
    private String[] splitKeyWordArray;
    private TimePickerManager timePickerManager;
    private boolean selectStartDate;
    private int currentStartEndMode = -1;//0代表点击开始时间 ，1结束时间

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_goods_layout);
        ButterKnife.bind(this);
        setViewsListener();
        adapter = new CommonItemCommonSixAdapter();
        recyclerView.setAdapter(adapter);
        titleManagerView.setTitle("拣货任务页面");

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        startDate = today;
        endDate = today;
        GetPickGoodsDataAsyncTask task = new GetPickGoodsDataAsyncTask();
        task.execute();
        todayButton.setSelected(true);
//        startDate =
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.Intent_Request_Code_Pick_Goods_To_Stock_Move_Activity) {
            //处理离开之前的保存的数据
        }
    }

    protected void setViewsListener() {
        if (searchTextView != null) {
            //筛选清单 originalBUDataList
            searchTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (searchTextView.getText().equals("取消")) {
                        finish();
                    } else {
                        doSearchAction(searchEditText.getText().toString());
                    }
                }
            });
        }

        searchEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                clearImageView.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                searchTextView.setText(s.length() > 0 ? "搜索" : "取消");
                if (s.length() == 0) {
//                    bindObjectListsToAdapterBU(originalBUDataList);
                    adapter.setData(originalBUDataList);
                }
            }
        });
        clearImageView.setOnClickListener(v -> {
            searchEditText.setText("");
            searchTextView.setText("取消");
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String input = searchEditText.getText().toString();
                    if (TextUtils.isEmpty(input)) {
                        ToastUtil.showToastShort("请输入搜索内容");
                    } else {
                        doSearchAction(input);
                    }
                    return true;
                }
                return false;
            }
        });

        todayButton.setOnClickListener(this);
        tomorrowButton.setOnClickListener(this);
        tomorrowPlus1Button.setOnClickListener(this);
        tomorrowPlus2Button.setOnClickListener(this);
//        startTimeButton.setOnClickListener(v -> {
////            if (currentStartEndMode == 0){
////                ToastUtil.showToastShort("已");
////            }
//            currentStartEndMode = 0;
//            selectDate();
////            selectStartDate = true;
//        });
        endTimeButton.setOnClickListener(v ->{

//            if (selectStartDate){
//                selectDate();
//            }else {
//                ToastUtil.showToastShort("请先选择开始时间");
//            }
            selectDate();
        });
    }

    private void selectDate() {
        showTimePickerDialog(TimePickerManager.PICK_TYPE_OUT_DATE);
    }

    private void showTimePickerDialog(String pickType) {
        if (timePickerManager == null) {
            timePickerManager = new TimePickerManager(PickGoodsActivity.this);

        }
        if (pickType == TimePickerManager.PICK_TYPE_ARRIVE_DATE || pickType == TimePickerManager.PICK_TYPE_OUT_DATE) {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 3);
        } else {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 5);
        }
        timePickerManager
                .setOnViewClickListener(PickGoodsActivity.this)
                .showDialog(pickType);
        AppUtil.forceHideInputMethod(PickGoodsActivity.this);
    }

    private void doSearchAction(String keyWord) {
        if (originalBUDataList == null) {
            return;
        }
        if (!keyWord.isEmpty()) {
//            List<BUItemBean> tempList = getFilterList(input);

            parseKeyWord(keyWord);

//            bindObjectListsToAdapterBU(tempList);
            adapter.setData(getFilterList(keyWord));
        } else {
            //todo
//            bindObjectListsToAdapterBU(originalBUDataList);
            adapter.setData(originalBUDataList);
        }
    }

    private void parseKeyWord(String keyWord) {
        if (keyWord.contains(" ")) {
            splitKeyWordArray = keyWord.split(" ");
        } else {

        }
    }

    protected List<?> getFilterList(String keyWord) {
//        List<?> tempList ;
        if (originalBUDataList != null) {
            if (originalBUDataList != null && originalBUDataList.size() > 0) {
                Object bean = originalBUDataList.get(0);

                if (bean instanceof PickGoodsBean) {
                    List tempList = new ArrayList<PickGoodsBean>();
                    for (int i = 0; i < originalBUDataList.size(); i++) {
                        PickGoodsBean pickGoodsBean = (PickGoodsBean) originalBUDataList.get(i);
                        if (pickGoodsBean.getAreaUnit().contains(keyWord) || pickGoodsBean.getItemName().contains(keyWord)) {
                            tempList.add(pickGoodsBean);
                        }
                    }
                    return tempList;
                }
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
//        Date date = new Date();
        unSelectAllButton();
        if (v == todayButton) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            startDate = today;
            endDate = today;
            todayButton.setSelected(true);
        } else if (v == tomorrowButton) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            calendar.add(Calendar.DATE, 1);
            Date tomorrowDate = calendar.getTime();
            startDate = today;
            endDate = tomorrowDate;
            tomorrowButton.setSelected(true);
        } else if (v == tomorrowPlus1Button) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            calendar.add(Calendar.DATE, 2);
            Date tomorrowP1Date = calendar.getTime();
            startDate = today;
            endDate = tomorrowP1Date;
            tomorrowPlus1Button.setSelected(true);
        } else if (v == tomorrowPlus2Button) {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            calendar.add(Calendar.DATE, 3);
            Date tomorrowP2Date = calendar.getTime();
            startDate = today;
            endDate = tomorrowP2Date;
            tomorrowPlus2Button.setSelected(true);
        }
        GetPickGoodsDataAsyncTask task = new GetPickGoodsDataAsyncTask();
        task.execute();
    }

    private void unSelectAllButton() {
        todayButton.setSelected(false);
        tomorrowButton.setSelected(false);
        tomorrowPlus1Button.setSelected(false);
        tomorrowPlus2Button.setSelected(false);
    }

    @Override
    public <T> void onClickAction(View v, String tag, T date) {
        if (tag.equals(TimePickerManager.PICK_TYPE_OUT_DATE)) {
//            if (selectStartDate){
//                endTimeButton.setText(UnitFormatUtil.sdf_YMD_Chinese.format(date));
//                endDate = (Date) date;
//                GetPickGoodsDataAsyncTask task = new GetPickGoodsDataAsyncTask();
//                task.execute();
//            }else{
//                startTimeButton.setText(UnitFormatUtil.sdf_YMD_Chinese.format(date));
//                startDate = (Date) date;
//                selectStartDate = true;
//            }
            endTimeButton.setText(UnitFormatUtil.sdf_YMD_Chinese.format(date));
            endDate = (Date) date;
            GetPickGoodsDataAsyncTask task = new GetPickGoodsDataAsyncTask();
            task.execute();
        }
    }


    private class GetPickGoodsDataAsyncTask extends AsyncTask<String, Void, List<?>> {
        WsResult wsResult;

        @Override
        protected List<?> doInBackground(String... params) {

//            switch (fromValue) {
//                case IntentConstant.Select_Search_From_Select_BU:
////                    adapter = new CommonItemSearchAdapter();
//                    List<BUItemBean> buBeanList = WebServiceUtil.getBUBeanList(SQL);
//                    if (buBeanList != null && buBeanList.size() > 0) {
////                        originalBUDataList = buBeanList;
//                        return buBeanList;
//                    }
//                    break;
//                case IntentConstant.Select_Search_From_Select_Department:
//                    List<DepartmentBean> departmentBeanList = WebServiceUtil.getDepartmentBeanList(SQL);
//                    if (departmentBeanList != null && departmentBeanList.size() > 0) {
//                        return departmentBeanList;
//                    }
//                    break;
//
//            }
            wsResult = WebServiceUtil.getPickGoodsData(startDate, endDate);
            if (wsResult != null && wsResult.getResult()) {
                Type type = new TypeToken<List<PickGoodsBean>>() {
                }.getType();
                List<PickGoodsBean> beanList = JsonUtil.parseJsonToObject(wsResult.getErrorInfo(), type);
                return beanList;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new CommProgressDialog.Builder(PickGoodsActivity.this)
                        .setTitle("正在加载").create();
            }
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<?> resultList) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            originalBUDataList = resultList;
            if (wsResult == null) {
                ToastUtil.showToastShort("获取拣货任务失败！");
            } else {
                if (!wsResult.getResult()) {
                    ToastUtil.showToastShort("错误:原因" + wsResult.getErrorInfo());
                }
            }
            //tv.setText(fahren + "∞ F");
            if (resultList == null || resultList.size() == 0) {
                emptyLayoutManageView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            }
            adapter.setData(resultList);
            emptyLayoutManageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (selectStartDate){
                selectStartDate = false;
            }

        }


    }

}
