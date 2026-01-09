package com.chinashb.www.mobileerp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chinashb.www.mobileerp.adapter.CommonSingleTextViewAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.TimeZone.getTimeZone;


/***
 * @date 创建时间 8/8/24 10:47 AM
 * @author 作者: liweifeng
 * @description 拣货页面新版本，之前的一个显示较大，占屏幕
 */
public class PickGoodsNewShowActivity extends BaseActivity implements View.OnClickListener, OnViewClickListener {
    @BindView(R.id.new_common_search_title_titleManagerView) TitleLayoutManagerView titleManagerView;
    @BindView(R.id.new_common_search_action_TextView) TextView searchTextView;
    @BindView(R.id.new_common_et_keyword_input) EditText searchEditText;
    @BindView(R.id.new_common_search_clear_input_ImageView) ImageView clearImageView;
    @BindView(R.id.new_common_rv_select_list) CustomRecyclerView recyclerView;
    @BindView(R.id.new_common_common_search_list_emptyManager) EmptyLayoutManageView emptyLayoutManageView;
    @BindView(R.id.pick_new_goods_today_button) Button todayButton;
    @BindView(R.id.pick_new_goods_tomorrow_button) Button tomorrowButton;
    @BindView(R.id.pick_new_goods_tomorrowplus1_button) Button tomorrowPlus1Button;
    @BindView(R.id.pick_new_goods_tomorrowplus2_button) Button tomorrowPlus2Button;
    @BindView(R.id.pick_new_goods_time_area_start_button) Button startTimeButton;
    @BindView(R.id.pick_new_goods_time_area_end_button) Button endTimeButton;
    @BindView(R.id.pick_new_goods_time_area_layout) LinearLayout timeAreaLayout;
    @BindView(R.id.pick_new_goods_order_area_button) Button orderAreaButton;
    @BindView(R.id.pick_goods_day_radioButton) RadioButton dayRadioButton;
    @BindView(R.id.pick_goods_night_radioButton) RadioButton nightRadioButton;
    @BindView(R.id.day_night_shift_RadioGroup) RadioGroup shiftRadioGroup;

    private CommonSingleTextViewAdapter adapter;
    //    private List<?> originalBUDataList;
    private List<?> originalBUDataList;
    private CommProgressDialog progressDialog;
    private Date startDate;
    private Date endDate;
    private String[] splitKeyWordArray;
    private TimePickerManager timePickerManager;
    private boolean selectStartDate;
    private int currentStartEndMode = -1;//0代表点击开始时间 ，1结束时间
    private int currentDayNightMode = 1;//1表示白天，2代表晚班

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_goods_new_layout);
        ButterKnife.bind(this);
        setViewsListener();
        adapter = new CommonSingleTextViewAdapter();
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

    /**
     * 拣货之后的刷新
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.Intent_Request_Code_Pick_Goods_To_Stock_Move_Activity) {
            //处理离开之前的保存的数据
            if (data != null) {
                PickGoodsBean tempPickGoodsBean = data.getParcelableExtra(IntentConstant.Intent_Extra_to_pick_goods_bean_back);
                boolean removeSuccess = false;
                if (tempPickGoodsBean != null) {
                    if (originalBUDataList != null) {
                        for (PickGoodsBean bean : (List<PickGoodsBean>) originalBUDataList) {
                            if (bean.getSubIstID() == tempPickGoodsBean.getSubIstID()) {
                                removeSuccess = originalBUDataList.remove(bean);
                                break;
                            }
                        }
                    }
                    if (removeSuccess && originalBUDataList != null) {
                        adapter.setData(originalBUDataList);
                        //// TODO: 8/9/24  如果有搜索之类的也保留
                        doSearchAction(searchEditText.getText().toString());
                    }
                }
            }
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
        orderAreaButton.setOnClickListener(v -> {

//            Collections.sort(originalBUDataList, new Comparator<PickGoodsBean>() {
//                @Override
//                public int compare(PickGoodsBean o1, PickGoodsBean o2) {
//                    if (o1.getSubIstID() > o2.getSubIstID()){
//                        return 1;
//                    }
//                    return 0;
//                }
//            });

//            Collections.sort(originalBUDataList, new Comparator<Object>() {
//                @Override
//                public int compare(Object o1, Object o2) {
//                    if (o1 instanceof PickGoodsBean && o2 instanceof  PickGoodsBean){
////                       if (((PickGoodsBean) o1) .getSubIstID() < ((PickGoodsBean) o2).getSubIstID()) {
////                           return -1;
////                       }
//                        return ((PickGoodsBean) o1) .getSubIstID() - ((PickGoodsBean) o2).getSubIstID();
//                    }
//                    return -1;
//                }
//            });

            //// TODO: 8/15/24 经典排序案例，字符串间的 如M20.M20-16-1之类的排序 oa1Unit.compareTo(oa2Unit);
            Collections.sort(originalBUDataList, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    if (o1 instanceof PickGoodsBean && o2 instanceof PickGoodsBean) {
                        String oa1Unit = ((PickGoodsBean) o1).getAreaUnit();
                        String oa2Unit = ((PickGoodsBean) o2).getAreaUnit();
                        return oa1Unit.compareTo(oa2Unit);
                    }
                    return -1;
                }
            });

            adapter.setData(originalBUDataList);
        });
//        startTimeButton.setOnClickListener(v -> {
////            if (currentStartEndMode == 0){
////                ToastUtil.showToastShort("已");
////            }
//            currentStartEndMode = 0;
//            selectDate();
////            selectStartDate = true;
//        });
        endTimeButton.setOnClickListener(v -> {

//            if (selectStartDate){
//                selectDate();
//            }else {
//                ToastUtil.showToastShort("请先选择开始时间");
//            }
            selectDate();
        });

        shiftRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.pick_goods_day_radioButton){
                    currentDayNightMode = 1;
                }else if (checkedId == R.id.pick_goods_night_radioButton){
                    currentDayNightMode = 2;
                }
            }
        });
    }

    private void selectDate() {
        showTimePickerDialog(TimePickerManager.PICK_TYPE_OUT_DATE);
    }

    private void showTimePickerDialog(String pickType) {
        if (timePickerManager == null) {
            timePickerManager = new TimePickerManager(PickGoodsNewShowActivity.this);

        }
        if (pickType == TimePickerManager.PICK_TYPE_ARRIVE_DATE || pickType == TimePickerManager.PICK_TYPE_OUT_DATE) {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 3);
        } else {
            timePickerManager.setShowType(TimePickerManager.PICK_TYPE_YEAR, 5);
        }
        timePickerManager
                .setOnViewClickListener(PickGoodsNewShowActivity.this)
                .showDialog(pickType);
        AppUtil.forceHideInputMethod(PickGoodsNewShowActivity.this);
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
                        if (pickGoodsBean.getAreaUnit().contains(keyWord) || pickGoodsBean.getItemName().contains(keyWord) || String.valueOf(pickGoodsBean.getItemID()).contains(keyWord)) {
                            tempList.add(pickGoodsBean);
                        }
                    }
                    return tempList;
                }
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
//        Date date = new Date();
        unSelectAllButton();
        //获取时区 这句加上，很关键。

//        TimeZone timeZoneChina = getTimeZone("Asia/Shanghai");
        if (v == todayButton) {
            Calendar calendar = Calendar.getInstance();
//            calendars.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            //下面获取方式有问题
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//            }

//            TimeZone timeZoneChina = null;//获取时区 这句加上，很关键。
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");
//            }
            //设置系统时区
            calendar.setTimeZone(getTimeZone("Asia/Shanghai"));
            Date today = calendar.getTime();
            startDate = today;
            endDate = today;
            todayButton.setSelected(true);
        } else if (v == tomorrowButton) {
            Calendar calendar = Calendar.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                calendar.setTimeZone(getTimeZone("GMT+8:00"));
            }
            Date today = calendar.getTime();
            calendar.add(Calendar.DATE, 1);
            Date tomorrowDate = calendar.getTime();
            startDate = today;
            endDate = tomorrowDate;
            tomorrowButton.setSelected(true);
        } else if (v == tomorrowPlus1Button) {
            Calendar calendar = Calendar.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                calendar.setTimeZone(getTimeZone("GMT+8:00"));
            }
            Date today = calendar.getTime();
            calendar.add(Calendar.DATE, 2);
            Date tomorrowP1Date = calendar.getTime();
            startDate = today;
            endDate = tomorrowP1Date;
            tomorrowPlus1Button.setSelected(true);
        } else if (v == tomorrowPlus2Button) {
            Calendar calendar = Calendar.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                calendar.setTimeZone(getTimeZone("GMT+8:00"));
            }
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
//            wsResult = WebServiceUtil.getPickGoodsData(startDate, endDate);
            if (currentDayNightMode == 1){

                wsResult = WebServiceUtil.getPickGoodsDataNewByShift(startDate, endDate,currentDayNightMode);
            }else if(currentDayNightMode == 2){
                //晚班班次的话，视同为和之前一样，因为例如今天白班是白班，晚班是今天全天，明天白班是今天加明天白班，明天晚班是今天加明天一天
                wsResult = WebServiceUtil.getPickGoodsData(startDate, endDate);
            }
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
                progressDialog = new CommProgressDialog.Builder(PickGoodsNewShowActivity.this)
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
            if (selectStartDate) {
                selectStartDate = false;
            }

        }


    }

}

