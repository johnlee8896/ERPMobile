package com.chinashb.www.mobileerp.commonactivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.bean.BUItemBean;
import com.chinashb.www.mobileerp.bean.DepartmentBean;
import com.chinashb.www.mobileerp.bean.ResearchItemBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.AppUtil;
import com.chinashb.www.mobileerp.utils.IntentConstant;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/***
 * @date 创建时间 2019/7/4 8:40
 * @author 作者: xxblwf
 * @description 通用的adapter类来展示搜索列表
 */

public class CommonSelectItemActivity extends AppCompatActivity {

    //    public List<Integer> ColWidthList;
//    public List<String> ColCaptionList;
//    public List<String> hiddenColList;
    TitleLayoutManagerView titleLayoutManagerView;
    CustomRecyclerView recyclerView;
    String title;
    //    RelativeLayout llSearch;
    LinearLayout llTitle;
    JsonObject sample;
    JsonObject Selected_Object;
    HashMap<String, String> Result;
    private ImageView clearImageView;
    private EmptyLayoutManageView emptyLayoutManageView;

    EditText searchEditText;
    TextView searchTextView;
    //    ProgressBar pbBackground;
    private CommProgressDialog progressDialog;
    private String SQL;
    //        private List<BUItemBean> originalBUDataList;
    private List<?> originalBUDataList;
    //    private List<DepartmentBean> departmentBeanList;
    //    private JsonListAdapter dataAdapter;
//    private BUSearchListAdapter buAdapter;
//    private DepartmentSearchListAdapter departmentAdapter;
    private CommonItemSearchAdapter adapter;

    private int fromValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBundleExtras();
        setContentView(R.layout.activity_common_search_list_layout);
        titleLayoutManagerView = (TitleLayoutManagerView) findViewById(R.id.search_title_titleManagerView);
        llTitle = (LinearLayout) findViewById(R.id.json_linear_lay_out_title);
//        clearImageView = findViewById(R.id.search_clear_input_ImageView);
        clearImageView = (ImageView) findViewById(R.id.search_clear_input_ImageView);
//        llSearch = (RelativeLayout) findViewById(R.id.json_linear_lay_out_search);
//        llSearch.setVisibility(View.GONE);
        recyclerView = (CustomRecyclerView) findViewById(R.id.rv_select_list);
//        pbBackground = (ProgressBar) findViewById(R.id.pb_webservice_progressbar);
        searchTextView = (TextView) findViewById(R.id.search_action_TextView);
        searchEditText = (EditText) findViewById(R.id.et_keyword_input);
        emptyLayoutManageView = findViewById(R.id.common_search_list_emptyManager);
        titleLayoutManagerView.setTitle(title);
//        setHomeButton();
        GetListAsyncTask getListAsyncTask = new GetListAsyncTask();
        getListAsyncTask.execute();
        setViewsListener();

//        buAdapter = new BUSearchListAdapter();
//        departmentAdapter = new DepartmentSearchListAdapter();
//        recyclerView.setAdapter(buAdapter);
//        AddScrollShow();

        adapter = new CommonItemSearchAdapter();
        recyclerView.setAdapter(adapter);
//        AppUtil.forceHideInputMethod(CommonSelectItemActivity.this);

    }

    protected void setViewsListener() {
        if (searchTextView != null) {
            //筛选清单 originalBUDataList
            searchTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doSearchAction(searchEditText.getText().toString());
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
    }

    private void doSearchAction(String keyWord) {
        if (originalBUDataList == null) {
            return;
        }
        if (!keyWord.isEmpty()) {
//            List<BUItemBean> tempList = getFilterList(input);
//            bindObjectListsToAdapterBU(tempList);
            adapter.setData(getFilterList(keyWord));
        } else {
            //todo
//            bindObjectListsToAdapterBU(originalBUDataList);
            adapter.setData(originalBUDataList);
        }
    }

//    @SuppressLint("ClickableViewAccessibility")
//    protected void AddScrollShow() {
//
//        recyclerView.setOnTouchListener(new View.OnTouchListener() {
//                                            private Boolean Moving = false;
//                                            private float mEndY;
//                                            private float mStartY;
//
//                                            @Override
//                                            public boolean onTouch(View v, MotionEvent event) {
//                                                switch (event.getAction()) {
//                                                    case MotionEvent.ACTION_DOWN:
//                                                        //mStartY = event.getY();
//                                                        //performClick();
//                                                        //Log.d("Action","Down");
//                                                        break;
//                                                    case MotionEvent.ACTION_MOVE:
////                                                        if (Moving == false) {
////                                                            mStartY = event.getY();
////                                                            Moving = true;
////                                                        }
////
////                                                        //Log.d("Action","MOVE");
////                                                        mEndY = event.getY();
////                                                        float DeltaY = mEndY - mStartY;
////                                                        Log.d("mStart:" + String.valueOf(mStartY), "mEnd" + String.valueOf(mEndY));
////                                                        Log.d("Move", String.valueOf(DeltaY));
////                                                        if (DeltaY > 20) {
////                                                            //这个就是当前页面的头布局id
////                                                            if (llSearch.getVisibility() == View.GONE) {
////
////                                                                llSearch.setVisibility(View.VISIBLE);
////                                                            }
////                                                        } else if (DeltaY < -20) {
////                                                            if (llSearch.getVisibility() == View.VISIBLE) {
////
////                                                                llSearch.setVisibility(View.GONE);
////                                                            }
////                                                        }
//                                                        break;
//                                                    case MotionEvent.ACTION_UP:
//                                                        Log.d("Action", "UP");
//                                                        //起来了，停止了移动
//                                                        Moving = false;
//
////                                                        if (Moving == false) {
////                                                            mStartY = event.getY();
////                                                            Moving = true;
////                                                        }
//
//                                                        //Log.d("Action","MOVE");
//                                                        mEndY = event.getY();
//                                                        float DeltaY = mEndY - mStartY;
//                                                        Log.d("mStart:" + String.valueOf(mStartY), "mEnd" + String.valueOf(mEndY));
//                                                        Log.d("Move", String.valueOf(DeltaY));
//                                                        if (DeltaY > 20) {
//                                                            //这个就是当前页面的头布局id
//                                                            if (llSearch.getVisibility() == View.GONE) {
//
//                                                                llSearch.setVisibility(View.VISIBLE);
//                                                            }
//                                                        } else if (DeltaY < -20) {
//                                                            if (llSearch.getVisibility() == View.VISIBLE) {
//
//                                                                llSearch.setVisibility(View.GONE);
//                                                            }
//                                                        }
//
//                                                        break;
//                                                }
//
//                                                //这里一定要返回gestureDetector.onTouchEvent(event)  不然滑动监听无效
//                                                GestureDetector gestureDetector = new GestureDetector(SelectItemActivity.this, new myGestureListener());
//                                                return gestureDetector.onTouchEvent(event);
//                                            }
//
//
//                                        }
//        );
//
//    }

    //todo
    protected List<?> getFilterList(String keyWord) {
//        List<?> tempList ;
        if (originalBUDataList != null) {
//            for (int i = 0; i < originalBUDataList.size(); i++) {
////                JsonObject j = originalBUDataList.get(i);
////                if (IsJsonObjectContainsKeyString(j, Key)) {
////                    tempList.add(j);
////                }
//
//            }

//            for (BUItemBean buItemBean : originalBUDataList) {
//                if (buItemBean.getCompanyChineseName().contains(keyWord) || buItemBean.getBUName().contains(keyWord)) {
//                    tempList.add(buItemBean);
//                }
//            }
            if (originalBUDataList != null && originalBUDataList.size() > 0) {
                Object bean = originalBUDataList.get(0);
//                    for (BUItemBean buItemBean : originalBUDataList) {
//                        if (buItemBean.getCompanyChineseName().contains(keyWord) || buItemBean.getBUName().contains(keyWord)) {
//                            tempList.add(buItemBean);
//                        }
//                    }

                for (int i = 0; i < originalBUDataList.size(); i++) {
                    if (bean instanceof BUItemBean) {
                       List tempList = new ArrayList<BUItemBean>();
                        BUItemBean buItemBean = (BUItemBean) originalBUDataList.get(i);
                        if (buItemBean.getCompanyChineseName().contains(keyWord) || buItemBean.getBUName().contains(keyWord)) {
                            tempList.add(buItemBean);
                        }
                        return tempList;
                    } else if (bean instanceof DepartmentBean) {
                        List tempList = new ArrayList<BUItemBean>();
                        DepartmentBean departmentBean = (DepartmentBean) originalBUDataList.get(i);
                        if (departmentBean.getDepartmentName().contains(keyWord) || departmentBean.getPDN().contains(keyWord)){
                            tempList.add(departmentBean);
                        }
                        return tempList;
                    }
                }
            }
        }
        return null;
    }

    protected Boolean IsJsonObjectContainsKeyString(JsonObject j, String Key) {
        Set<String> cols = j.keySet();
        Iterator<String> iterator = cols.iterator();
        while (iterator.hasNext()) {
            String colname = iterator.next();
            String colvalue = "";
            if (!j.get(colname).isJsonNull()) {
                colvalue = j.get(colname).getAsString();
            }
            if (colvalue.contains(Key)) {
                return true;
            }
        }
        return false;
    }

//    private void bindObjectListsToAdapter(final List<JsonObject> JList) {
//        dataAdapter = new JsonListAdapter(SelectItemActivity.this, JList);
//        //赋值 列宽度
//        dataAdapter.ColWidth = ColWidthList;
//        dataAdapter.HiddenCol = hiddenColList;
////        rec.setLayoutManager(new LinearLayoutManager(SelectItemActivity.this));
//        recyclerView.setAdapter(dataAdapter);
//        dataAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void OnItemClick(View view, int position) {
//                if (JList != null) {
//                    Selected_Object = JList.get(position);
//                    //Selected_Object转换成HashMap：Result
//                    Result = CommonUtil.Convert_JsonObject_HashMap(Selected_Object);
//                    Intent result = new Intent();
//                    result.putExtra("SelectItem", (Serializable) Result);
//                    setResult(1, result);
//                    finish();
//                }
//            }
//        });
//    }


//    private void bindObjectListsToAdapterBU(final List<BUItemBean> beanList) {
//        if (beanList != null && beanList.size() > 0) {
//            buAdapter.setData(beanList);
//        }
//
//    }

//    private void bindObjectListsToAdapterBU( List<BUItemBean> beanList) {
//        if (beanList != null && beanList.size() > 0) {
//            buAdapter.setData(beanList);
//        }
//
//    }
//
//    private void bindObjectListsToAdapterDepartment( List<DepartmentBean> beanList) {
//        if (beanList != null && beanList.size() > 0) {
//            departmentAdapter.setData(beanList);
//        }
//
//    }

    protected void getBundleExtras() {
        Intent intent = getIntent();
        title = (String) intent.getStringExtra("Title");
        SQL = (String) intent.getStringExtra("SQL");
        fromValue = intent.getIntExtra(IntentConstant.Intent_Extra_to_select_search_from_postition, 0);

//        ColWidthList = (List<Integer>) who.getSerializableExtra("ColWidthList");
//        ColCaptionList = (List<String>) who.getSerializableExtra("ColCaptionList");
//        hiddenColList = (List<String>) who.getSerializableExtra("hiddenColList");

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
        } else {
            // This is important, otherwise the wsResult will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
//设置为竖屏幕
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }

    //    private Handler uiHandler = new Handler(this.getMainLooper()){
//    private Handler uiHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            bindObjectListsToAdapterBU(originalBUDataList);
//        }
//    };

    private class GetListAsyncTask extends AsyncTask<String, Void, List<?>> {

        @Override
        protected List<?> doInBackground(String... params) {
            if (SQL.isEmpty()) {
                return null;
            }
            //执行SQL
//            originalBUDataList = WebServiceUtil.getJsonList(SQL);

            switch (fromValue) {
                case IntentConstant.Select_Search_From_Select_BU:
//                    adapter = new CommonItemSearchAdapter();
                    List<BUItemBean> buBeanList = WebServiceUtil.getBUBeanList(SQL);
//            if (originalBUDataList != null && originalBUDataList.size() > 0) {
////                sample = originalBUDataList.get(0);
//                //todo
//                uiHandler.sendEmptyMessage(0);
//            }
                    if (buBeanList != null && buBeanList.size() > 0) {
//                        originalBUDataList = buBeanList;
                        return buBeanList;
                    }
                    break;
                case IntentConstant.Select_Search_From_Select_Department:
                    List<DepartmentBean> departmentBeanList = WebServiceUtil.getDepartmentBeanList(SQL);
                    if (departmentBeanList != null && departmentBeanList.size() > 0) {
                        return departmentBeanList;
                    }
                    break;
                case IntentConstant.Select_Search_From_Select_ReSearch_Program:
//                    List<ResearchItemBean> commonItemBeanList = WebServiceUtil.getCommonItemBeanList(SQL, ResearchItemBean.class);
                    //// TODO: 2019/7/4 继续理解泛型使用
//                    if (commonItemBeanList != null && commonItemBeanList.size() > 0) {
//                        return commonItemBeanList;
//                    }
                    List<ResearchItemBean> researchItemBeanList = WebServiceUtil.getResearchItemBeanList(SQL);
                    if (researchItemBeanList != null && researchItemBeanList.size() > 0){
                        return researchItemBeanList;
                    }
                    break;

                case IntentConstant.Select_Search_From_Select_Check_File:
                    //// TODO: 2019/7/4 没有数据
                    WebServiceUtil.getDepartmentBeanList(SQL);
//                    if (departmentBeanList != null && departmentBeanList.size() > 0) {
//                        return departmentBeanList;
//                    }
                    break;
//                default:
//                    break;
            }
//            List<BUItemBean> buBeanList = WebServiceUtil.getBUBeanList(SQL);
////            if (originalBUDataList != null && originalBUDataList.size() > 0) {
//////                sample = originalBUDataList.get(0);
////                //todo
////                uiHandler.sendEmptyMessage(0);
////            }
//            if (buBeanList != null && buBeanList.size() > 0) {
//                originalBUDataList = buBeanList;
//                return buBeanList;
//            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new CommProgressDialog.Builder(CommonSelectItemActivity.this)
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
            //tv.setText(fahren + "∞ F");
            if (resultList == null || resultList.size() == 0) {
                emptyLayoutManageView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            }
            //建立标题行
//            dynamicCreateRowTitle();
//            bindObjectListsToAdapter(originalBUDataList);
//            bindObjectListsToAdapterBU(resultList);
//            pbBackground.setVisibility(View.GONE);
            Object bean = resultList.get(0);
            adapter.setData(resultList);
            emptyLayoutManageView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
//            if (bean instanceof BUItemBean){
//                adapter.setData(resultList);
//            }else if (bean instanceof  DepartmentBean){
//
//            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

//        protected void dynamicCreateRowTitle() {
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT);
//
//            Integer i = 0;
//            if (sample != null) {
//                Set<String> cols = sample.keySet();
//                Iterator<String> iterator = cols.iterator();
//                while (iterator.hasNext()) {
//                    String colname = iterator.next();
//
//                    if (hiddenColList != null && hiddenColList.contains(colname)) {
//                        i++;
//                        continue;
//                    }
//                    TextView textView = addTextView(colname);
//                    if (ColWidthList != null && ColWidthList.size() > i) {
//                        textView.setWidth(calculateDpToPx(ColWidthList.get(i)));
//                    }
//
//                    if (ColCaptionList != null && ColCaptionList.size() > i) {
//                        textView.setText(ColCaptionList.get(i));
//                    } else {
//                        textView.setText(colname);
//                    }
//
//                    llTitle.addView(textView, lp);
//
//                    i++;
//                }
//
//            }
//        }

        private int calculateDpToPx(int dp) {
            final float scale = getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }

        private TextView addTextView(String Name) {
            TextView tv = new TextView(CommonSelectItemActivity.this);
            tv.setTag(Name);
            return tv;
        }

    }


}

