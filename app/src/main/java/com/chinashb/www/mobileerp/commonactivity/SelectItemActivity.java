package com.chinashb.www.mobileerp.commonactivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.chinashb.www.mobileerp.adapter.JsonListAdapter;
import com.chinashb.www.mobileerp.bean.BUItemBean;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.CustomRecyclerView;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class SelectItemActivity extends AppCompatActivity {

    public List<Integer> ColWidthList;
    public List<String> ColCaptionList;
    public List<String> hiddenColList;
    TitleLayoutManagerView titleLayoutManagerView;
    CustomRecyclerView recyclerView;
    String title;
    //    RelativeLayout llSearch;
    LinearLayout llTitle;
    JsonObject sample;
    JsonObject Selected_Object;
    HashMap<String, String> Result;
    private ImageView clearImageView;

    EditText searchEditText;
    TextView searchTextView;
    //    ProgressBar pbBackground;
    private CommProgressDialog progressDialog;
    private String SQL;
    private List<BUItemBean> objectDataList;
        private JsonListAdapter dataAdapter;
    private SearchJsonListAdapter adapter;

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
        titleLayoutManagerView.setTitle(title);
//        setHomeButton();
        GetListAsyncTask getListAsyncTask = new GetListAsyncTask();
        getListAsyncTask.execute();
        setViewsListener();

        adapter = new SearchJsonListAdapter();
        recyclerView.setAdapter(adapter);
//        AddScrollShow();

    }

    protected void setViewsListener() {
        if (searchTextView != null) {
            //筛选清单 objectDataList
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
//                if (s.length() == 0) {
//                }
            }
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

    private void doSearchAction(String input) {
        if (objectDataList == null) {
            return;
        }
        if (input.isEmpty() == false) {
            List<JsonObject> newlist = getFilterList(input);
            bindObjectListsToAdapter(newlist);
        } else {
            //todo
//            bindObjectListsToAdapter(objectDataList);
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
    protected List<JsonObject> getFilterList(String Key) {
        List<JsonObject> tempList = new ArrayList<>();
//        if (objectDataList != null) {
//            for (int i = 0; i < objectDataList.size(); i++) {
//                JsonObject j = objectDataList.get(i);
//                if (IsJsonObjectContainsKeyString(j, Key)) {
//                    tempList.add(j);
//                }
//            }
//        }
        return tempList;
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

    private void bindObjectListsToAdapter(final List<JsonObject> JList) {
        dataAdapter = new JsonListAdapter(SelectItemActivity.this, JList);
        //赋值 列宽度
        dataAdapter.ColWidth = ColWidthList;
        dataAdapter.HiddenCol = hiddenColList;
//        rec.setLayoutManager(new LinearLayoutManager(SelectItemActivity.this));
        recyclerView.setAdapter(dataAdapter);
        dataAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                if (JList != null) {
                    Selected_Object = JList.get(position);
                    //Selected_Object转换成HashMap：Result
                    Result = CommonUtil.Convert_JsonObject_HashMap(Selected_Object);
                    Intent result = new Intent();
                    result.putExtra("SelectItem", (Serializable) Result);
                    setResult(1, result);
                    finish();
                }
            }
        });
    }


    private void bindObjectListsToAdapterBU(final List<BUItemBean> beanList){
        adapter.setData(beanList);

    }

    protected void getBundleExtras() {
        Intent who = getIntent();

        title = (String) who.getStringExtra("Title");

        SQL = (String) who.getStringExtra("SQL");
        ColWidthList = (List<Integer>) who.getSerializableExtra("ColWidthList");
        ColCaptionList = (List<String>) who.getSerializableExtra("ColCaptionList");
        hiddenColList = (List<String>) who.getSerializableExtra("hiddenColList");

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
    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bindObjectListsToAdapterBU(objectDataList);
        }
    };
    private class GetListAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (SQL.isEmpty()) {
                return null;
            }
            //执行SQL
//            objectDataList = WebServiceUtil.getJsonList(SQL);
            objectDataList = WebServiceUtil.getBUBeanList(SQL);
            if (objectDataList != null && objectDataList.size() > 0) {
//                sample = objectDataList.get(0);
                //todo
                uiHandler.sendEmptyMessage(0);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new CommProgressDialog.Builder(SelectItemActivity.this)
                        .setTitle("正在加载").create();
            }
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            if (objectDataList == null || objectDataList.size() == 0) {
                return;
            }
            //建立标题行
            dynamicCreateRowTitle();
//            bindObjectListsToAdapter(objectDataList);
            bindObjectListsToAdapterBU(objectDataList);
//            pbBackground.setVisibility(View.GONE);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        protected void dynamicCreateRowTitle() {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            Integer i = 0;
            if (sample != null) {
                Set<String> cols = sample.keySet();
                Iterator<String> iterator = cols.iterator();
                while (iterator.hasNext()) {
                    String colname = iterator.next();

                    if (hiddenColList != null && hiddenColList.contains(colname)) {
                        i++;
                        continue;
                    }
                    TextView textView = addTextView(colname);
                    if (ColWidthList != null && ColWidthList.size() > i) {
                        textView.setWidth(calculateDpToPx(ColWidthList.get(i)));
                    }

                    if (ColCaptionList != null && ColCaptionList.size() > i) {
                        textView.setText(ColCaptionList.get(i));
                    } else {
                        textView.setText(colname);
                    }

                    llTitle.addView(textView, lp);

                    i++;
                }

            }
        }

        private int calculateDpToPx(int dp) {
            final float scale = getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }

        private TextView addTextView(String Name) {
            TextView tv = new TextView(SelectItemActivity.this);
            tv.setTag(Name);
            return tv;
        }

    }


}
