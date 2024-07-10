package com.chinashb.www.mobileerp.warehouse;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.Mpi_WcAdapter;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.WorkCenter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.StaticVariableUtils;
import com.chinashb.www.mobileerp.utils.TextWatcherImpl;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SelectMPIWCStepThreeActivity extends BaseActivity {
    private String action = "";
    private RecyclerView selectMWRecyclerView;
    private WorkCenter selectWorkCenter;
    private MpiWcBean selectMpiWcBean;
    private TextView subTitleTextView;
    private List<MpiWcBean> mpiWcBeanList;
    private Mpi_WcAdapter adapter;
    private ProgressBar pbScan;
    private Date showdate = Calendar.getInstance().getTime();

    private Button preDayButton;
    private Button preSevenButton;
    private Button nextDayButton;
    private Button nextSevenButton;
    private TextView txtDay;
    private boolean bu_option_mp_bom = false;
    private TextView searchTextView;
    private EditText searchEditText;
    private ImageView clearImageView;

    private List<MpiWcBean> originalBUDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mpi_wc_step3_layout);
        subTitleTextView = (TextView) findViewById(R.id.tv_select_mpi_wc_show_wc);
        selectMWRecyclerView = (RecyclerView) findViewById(R.id.rv_select_mpi_wc_byday);
        txtDay = (TextView) findViewById(R.id.tv_select_mpi_wc_show_day);
        preDayButton = (Button) findViewById(R.id.btn_select_mpi_wc_pre_day);
        preSevenButton = (Button) findViewById(R.id.btn_select_mpi_wc_pre_seven_days);
        nextDayButton = (Button) findViewById(R.id.btn_select_mpi_wc_next_day);
        nextSevenButton = (Button) findViewById(R.id.btn_select_mpi_wc_next_seven_days);

        searchTextView = (TextView) findViewById(R.id.mpi_filter_search_action_TextView);
        searchEditText = (EditText) findViewById(R.id.mpi_filter_et_keyword_input);
        clearImageView = (ImageView) findViewById(R.id.mpi_filter_search_clear_input_ImageView);

        pbScan = (ProgressBar) findViewById(R.id.progressbar2);
        Intent intent = getIntent();
        selectWorkCenter = (WorkCenter) intent.getSerializableExtra("wc");
        if (selectWorkCenter != null) {
            action = "日计划选择";
            initActionDailyPlan();
        }

        mpiWcBeanList = (List<MpiWcBean>) intent.getSerializableExtra("mws");
        if (mpiWcBeanList != null) {
            actionExistedPlans();
        }

    }

    protected void initActionDailyPlan() {
        subTitleTextView.setText(selectWorkCenter.getWC_Name() + "的生产计划");
        showPlan();
        preDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, -1);
                showPlan();
            }
        });

        preSevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, -7);
                showPlan();
            }
        });

        nextDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, 1);
                showPlan();
            }
        });

        nextSevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdate = CommonUtil.DateAdd(showdate, 7);
                showPlan();
            }
        });

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
//                    adapter.setData(originalBUDataList);

                    adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this,originalBUDataList);
//            adapter.showdeletebutton = showdeletebutton;
                    selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
                    selectMWRecyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new OnItemClickListener() {
                                                       @Override
                                                       public void OnItemClick(View view, int position) {
                                                           //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                                                           if (adapter.getDataList() != null) {
                                                               selectMpiWcBean = adapter.getDataList().get(position);
                                                               Intent intent = new Intent();
                                                               intent.putExtra("mw", selectMpiWcBean);
                                                               setResult(1, intent);
                                                               finish();
                                                           }
                                                       }
                                                   }
                    );

                }
            }
        });
        clearImageView.setOnClickListener(v -> {
            searchEditText.setText("");
            searchTextView.setText("取消");
            adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this,originalBUDataList);
//            adapter.showdeletebutton = showdeletebutton;
            selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            selectMWRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener() {
                                               @Override
                                               public void OnItemClick(View view, int position) {
                                                   //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                                                   if (adapter.getDataList() != null) {
                                                       selectMpiWcBean = adapter.getDataList().get(position);
                                                       Intent intent = new Intent();
                                                       intent.putExtra("mw", selectMpiWcBean);
                                                       setResult(1, intent);
                                                       finish();
                                                   }
                                               }
                                           }
            );
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

    protected void actionExistedPlans() {
        preDayButton.setEnabled(false);
        preSevenButton.setEnabled(false);
        nextDayButton.setEnabled(false);
        nextSevenButton.setEnabled(false);

        subTitleTextView.setText("已经选过的生产计划");
        txtDay.setText("");
        showMpiList(true);
    }

    private void doSearchAction(String keyWord) {
        if (originalBUDataList == null) {
            return;
        }
        if (!keyWord.isEmpty()) {
//            adapter.setData(getFilterList(keyWord));
            adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this, getFilterList(keyWord));
//            adapter.showdeletebutton = showdeletebutton;
            selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            selectMWRecyclerView.setAdapter(adapter);
//            adapter.getDataList()
            adapter.setOnItemClickListener(new OnItemClickListener() {
                                               @Override
                                               public void OnItemClick(View view, int position) {
                                                   //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                                                   if (adapter.getDataList() != null) {
                                                       selectMpiWcBean = adapter.getDataList().get(position);
                                                       Intent intent = new Intent();
                                                       intent.putExtra("mw", selectMpiWcBean);
                                                       setResult(1, intent);
                                                       finish();
                                                   }
                                               }
                                           }
            );
        } else {
            //todo
//            adapter.setData(originalBUDataList);
            adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this, originalBUDataList);
//            adapter.showdeletebutton = showdeletebutton;
            selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            selectMWRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener() {
                                               @Override
                                               public void OnItemClick(View view, int position) {
                                                   //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                                                   if (adapter.getDataList() != null) {
                                                       selectMpiWcBean = adapter.getDataList().get(position);
                                                       Intent intent = new Intent();
                                                       intent.putExtra("mw", selectMpiWcBean);
                                                       setResult(1, intent);
                                                       finish();
                                                   }
                                               }
                                           }
            );
        }
    }

    protected List<MpiWcBean> getFilterList(String keyWord) {
        if (originalBUDataList != null) {
            if (originalBUDataList != null && originalBUDataList.size() > 0) {
                Object bean = originalBUDataList.get(0);

                if (bean instanceof MpiWcBean) {
                    List tempList = new ArrayList<MpiWcBean>();
                    for (int i = 0; i < originalBUDataList.size(); i++) {
                        MpiWcBean buItemBean = originalBUDataList.get(i);
//                        if (buItemBean.getProduct_Chinese_Name().contains(keyWord) || buItemBean.getProduct_PartNo().contains(keyWord)||
//                                buItemBean.getItem_Name().contains(keyWord)|| String.valueOf(buItemBean.getItem_ID()).contains(keyWord)) {
//                            tempList.add(buItemBean);
//                        }
                        if (buItemBean.getMwName().contains(keyWord)) {
                            tempList.add(buItemBean);
                        }
                    }
                    return tempList;
                }
            }
        }
        return null;
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


    protected void showPlan() {
        String dateString;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateString = sdf.format(showdate);
        txtDay.setText(dateString);
        if (!bu_option_mp_bom){
            GetBuOptionAsyncTask task = new GetBuOptionAsyncTask();
            task.execute();
        }else{
            GetMWAsyncTask t = new GetMWAsyncTask();
            t.execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //new IntentIntegrator(MainActivity.this).initiateScan();
                //startScanHR();
            } else {

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class GetMWAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String sql = "Select M.MPIWC_ID,dbo.get_mw_plan_show_name(mpiwc_ID) As MwName,dbo.get_mw_plan_show_name_html(mpiwc_ID) As HtmlMwName, M.MPI_Remark " +
                    "From MPI_WC As M " +
                    "Where M.Deleted=0 And M.WC_ID=" + selectWorkCenter.getWC_ID() + " And MPI_Date=" + CommonUtil.SqlDate(showdate) + (bu_option_mp_bom  ? " and allow_iss=1" : " ") +
//                    "Where M.Deleted=0 And M.WC_ID=" + selectWorkCenter.getWC_ID() + " And MPI_Date=" + CommonUtil.SqlDate(showdate) + "  and allow_iss=1" +
                    " Order By PShift_ID, Shift_No";
            WsResult result = WebServiceUtil.getDataTable(sql);
            if (result != null && result.getResult()) {
                String js = result.getErrorInfo();
                ArrayList<MpiWcBean> mpiWcBeanList = new ArrayList<MpiWcBean>();
                Gson gson = new Gson();
                mpiWcBeanList = gson.fromJson(js, new TypeToken<List<MpiWcBean>>() {
                }.getType());
                SelectMPIWCStepThreeActivity.this.mpiWcBeanList = mpiWcBeanList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");
            showMpiList(false);
            pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    protected void showMpiList(Boolean showdeletebutton) {
        //绑定到不同的计划列表
        if (showdeletebutton) {
            adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this, StaticVariableUtils.selectMpiWcBeanList);
            adapter.showdeletebutton = showdeletebutton;
            selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            selectMWRecyclerView.setAdapter(adapter);
            originalBUDataList = StaticVariableUtils.selectMpiWcBeanList;
            adapter.setOnItemClickListener((view, position) -> {
                //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                if (StaticVariableUtils.selectMpiWcBeanList != null) {
                    selectMpiWcBean = StaticVariableUtils.selectMpiWcBeanList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("mw", selectMpiWcBean);
                    setResult(1, intent);
                    finish();
                }
            }
            );
        } else {
            adapter = new Mpi_WcAdapter(SelectMPIWCStepThreeActivity.this, mpiWcBeanList);
            adapter.showdeletebutton = showdeletebutton;
            selectMWRecyclerView.setLayoutManager(new LinearLayoutManager(SelectMPIWCStepThreeActivity.this));
            selectMWRecyclerView.setAdapter(adapter);
            originalBUDataList = mpiWcBeanList;
            adapter.setOnItemClickListener(new OnItemClickListener() {
                                               @Override
                                               public void OnItemClick(View view, int position) {
                                                   //Toast.makeText(SelectMPIWCStepTwoActivity.this,position+"",Toast.LENGTH_LONG).show();
                                                   if (mpiWcBeanList != null) {
                                                       selectMpiWcBean = mpiWcBeanList.get(position);
                                                       Intent intent = new Intent();
                                                       intent.putExtra("mw", selectMpiWcBean);
                                                       setResult(1, intent);
                                                       finish();
                                                   }
                                               }
                                           }
            );
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


    class GetBuOptionAsyncTask extends AsyncTask<String ,Void ,Void>{

        @Override protected Void doInBackground(String... strings) {
//            String sql = strings[0];
            String sql = "select Option_MP_BOM from bu where bu_id = " + UserSingleton.get().getUserInfo().getBu_ID();
            WsResult result = WebServiceUtil.getDataTable(sql);
            List<OptionMPBoxISSBean> commonDataList = new ArrayList<>();
            if (result != null && result.getResult()) {
                String jsonData = result.getErrorInfo();
                Gson gson = new Gson();
                commonDataList = gson.fromJson(jsonData, new TypeToken<List<OptionMPBoxISSBean>>() {
                }.getType());
                if (commonDataList != null && commonDataList.size() > 0) {
                    if (commonDataList.get(0) instanceof OptionMPBoxISSBean) {
                      bu_option_mp_bom = commonDataList.get(0).optionMPBoM;
                    }
                }
            }
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            GetMWAsyncTask t = new GetMWAsyncTask();
            t.execute();
        }
    }

    /**
     * 该车间是否有可投料的开关
     */
    private class OptionMPBoxISSBean {
        @SerializedName("Option_MP_BOM") boolean optionMPBoM;

        public boolean isOptionMPBoM() {
            return optionMPBoM;
        }

        public void setOptionMPBoM(boolean optionMPBoM) {
            this.optionMPBoM = optionMPBoM;
        }
    }

}