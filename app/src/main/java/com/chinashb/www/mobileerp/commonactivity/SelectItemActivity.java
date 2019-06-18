package com.chinashb.www.mobileerp.commonactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.JsonListAdapter;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class SelectItemActivity extends AppCompatActivity {

    Context mContext;
    TextView tvTitle;
    RecyclerView rvSelectList;

    String Title;
    private String SQL;
    private List<JsonObject> ObjectLists;
    private JsonListAdapter ObjectAdapter;

    LinearLayout llSearch;
    LinearLayout llTitle;
    JsonObject sample;
    JsonObject Selected_Object;
    HashMap<String,String> Result;

    EditText txtKey;
    Button btnFilter;


    public List<Integer> ColWidth;
    public List<String>ColCaption;
    public List<String>HiddenCol;

    ProgressBar pbBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext=this;

        getExtras();

        setContentView(R.layout.activity_select_simple);

        tvTitle=(TextView)findViewById(R.id.tv_select_title);
        llTitle=(LinearLayout)findViewById(R.id.json_linear_lay_out_title);
        llSearch=(LinearLayout)findViewById(R.id.json_linear_lay_out_search);

        llSearch.setVisibility(View.GONE);

        rvSelectList =(RecyclerView)findViewById(R.id.rv_select_list);
        pbBackground=(ProgressBar)findViewById(R.id.pb_webservice_progressbar);

        btnFilter=(Button)findViewById(R.id.btn_execute_filter);
        txtKey=(EditText)findViewById(R.id.et_keyword_input);

        tvTitle.setText(Title);

        setHomeButton();

        AsyncGetLists t = new AsyncGetLists();
        t.execute();

        AddButtonListener();

        AddScrollShow();

    }


    protected  void AddButtonListener(){
        if(btnFilter!=null)
        {
            //筛选清单 ObjectLists
            btnFilter.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    String Key = txtKey.getText().toString();

                    if(ObjectLists==null)
                    {return;}

                    if (Key.isEmpty()==false)
                    {
                        List<JsonObject> newlist = Filter(Key);

                        bindObjectListsToAdapter(newlist);
                    }
                    else
                    {
                        bindObjectListsToAdapter(ObjectLists);
                    }

                }
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected  void AddScrollShow(){

        rvSelectList.setOnTouchListener(new View.OnTouchListener() {
            private Boolean Moving=false;
            private float mEndY;
            private float mStartY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //mStartY = event.getY();
                        //performClick();
                        //Log.d("Action","Down");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(Moving==false)
                        {
                            mStartY = event.getY();
                            Moving=true;
                        }

                        //Log.d("Action","MOVE");
                        mEndY = event.getY();
                        float DeltaY = mEndY - mStartY;
                        Log.d("mStart:" + String.valueOf(mStartY),"mEnd"+String.valueOf(mEndY));
                        Log.d("Move" ,String.valueOf(DeltaY));
                        if (DeltaY > 2 ) {
                        //这个就是当前页面的头布局id
                            llSearch.setVisibility(View.VISIBLE); }
                        else if (DeltaY < -2)
                            {llSearch.setVisibility(View.GONE); }
                            break;
                    case MotionEvent.ACTION_UP:
                        Log.d("Action","UP");
                        //起来了，停止了移动
                        Moving=false;

                        break; }

                //这里一定要返回gestureDetector.onTouchEvent(event)  不然滑动监听无效
                GestureDetector gestureDetector= new GestureDetector(SelectItemActivity.this, new myGestureListener());
                return gestureDetector.onTouchEvent(event);
            }


        }
        );

    }




    protected List<JsonObject> Filter(String Key){
        List<JsonObject> newlist = new ArrayList<>();

        if(ObjectLists!= null)
        {
            for(int i =0;i< ObjectLists.size(); i++)
            {
                JsonObject j = ObjectLists.get(i);

                if(IsJsonObjectContainsKeyString(j,Key))
                { newlist.add(j);}

            }
        }
        return newlist;
    }

    protected Boolean IsJsonObjectContainsKeyString(JsonObject j, String Key)
    {
        Set<String> cols = j.keySet();
        Iterator<String> iterator = cols.iterator();
        while (iterator.hasNext()) {
            String colname = iterator.next();
            String colvalue = "";
            if(!j.get(colname).isJsonNull())
            {
                colvalue = j.get(colname).getAsString();
            }

            if (colvalue.contains(Key))
            {
                return true;
            }
        }

        return false;
    }

    protected void bindObjectListsToAdapter(final List<JsonObject> JList){
        ObjectAdapter = new JsonListAdapter( SelectItemActivity.this, JList);
        //赋值 列宽度
        ObjectAdapter.ColWidth = ColWidth;
        ObjectAdapter.HiddenCol =HiddenCol;
        rvSelectList.setLayoutManager( new LinearLayoutManager(SelectItemActivity.this));
        rvSelectList.setAdapter(ObjectAdapter);

        ObjectAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                if(JList!=null)
                {
                    Selected_Object=JList.get(position);

                    //Selected_Object转换成HashMap：Result
                    Result= CommonUtil.Convert_JsonObject_HashMap(Selected_Object);

                    Intent result = new Intent();

                    result.putExtra("SelectItem",(Serializable) Result);
                    setResult(1,result);
                    finish();

                }

            }
        });
    }


    protected void getExtras()
    {
        Intent who = getIntent();

        Title = (String) who.getStringExtra("Title");

        SQL=(String)who.getStringExtra("SQL");
        ColWidth =(List<Integer>)who.getSerializableExtra("ColWidth");
        ColCaption =(List<String>)who.getSerializableExtra("ColCaption");
        HiddenCol =(List<String>)who.getSerializableExtra("HiddenCol");

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

    protected void setHomeButton(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {       }
        else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class AsyncGetLists extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            if (SQL.isEmpty())
            {
                return null;
            }

            //执行SQL
            ObjectLists = WebServiceUtil.getJsonList(SQL);

            if(ObjectLists !=null && ObjectLists.size()>0)
            {sample=ObjectLists.get(0);}

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            //tv.setText(fahren + "∞ F");

            if(ObjectLists==null){
                return;
            }

            //建立标题行
            dynamic_create_row_title();

            bindObjectListsToAdapter(ObjectLists);

            pbBackground.setVisibility(View.GONE);
        }



        protected void dynamic_create_row_title()
        {
            LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            Integer i =0 ;
            if (sample != null)
            {
                Set<String> cols= sample.keySet();
                Iterator<String> iterator= cols.iterator();
                while (iterator.hasNext())
                {
                    String colname = iterator.next();

                    if(HiddenCol!=null && HiddenCol.contains(colname))
                    {
                        i++;
                        continue;
                    }
                    TextView textView=addTextView(colname);
                    if(ColWidth !=null && ColWidth.size()>i)
                    {textView.setWidth(calculateDpToPx(ColWidth.get(i)));}

                    if(ColCaption!=null && ColCaption.size()>i)
                    {textView.setText(ColCaption.get(i));}
                    else
                    {textView.setText(colname);}

                    llTitle.addView(textView,lp);

                    i++;
                }

            }
        }
        private int calculateDpToPx(int dp){
            final float scale = getResources().getDisplayMetrics().density;
            return  (int) (dp * scale + 0.5f);
        }


        private TextView addTextView(String Name) {
            TextView tv = new TextView(mContext);
            tv.setTag(Name);
            return tv;
        }

        @Override
        protected void onPreExecute() {
            pbBackground.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    @Override
    protected void onResume() {
//设置为竖屏幕
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT )
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        }

        super.onResume();
    }


}
