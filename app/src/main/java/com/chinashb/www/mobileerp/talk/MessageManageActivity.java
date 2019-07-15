package com.chinashb.www.mobileerp.talk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.JsonListAdapter;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.CommProgressDialog;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MessageManageActivity extends AppCompatActivity {

    public List<Integer> ColWidth;
    public List<String> ColCaption;
    public List<String> HiddenCol;
    private RecyclerView recyclerView;
    private int contactType = 0; //0:Usual 1:History 30  2:Group
    private List<JsonObject> contacts;
    private JsonListAdapter ObjectAdapter;
    private JsonObject Selected_Object;
    private HashMap<String, String> Result;

    private EmptyLayoutManageView emptyLayoutManageView;

//    Integer HR_ID;

    //todo 从menu里移过来的
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_contact_usual:
                    loadContactUsual();
                    return true;
                case R.id.navigation_contact_history:
                    loadContactHistory();
                    return true;
                case R.id.navigation_contact_group:
                    loadContactGroup();
                    return true;
            }
            return false;
        }
    };
    private CommProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_main_layout);
        setHomeButton();
        recyclerView = (RecyclerView) findViewById(R.id.rv_conversation_contactlist);
        contacts = new ArrayList<>();
        JsonListAdapter jsonListAdapter = new JsonListAdapter(this, contacts);
        recyclerView.setAdapter(jsonListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyLayoutManageView = (EmptyLayoutManageView) findViewById(R.id.message_emptyManager);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_contact);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        HR_ID= UserSingleton.get().getHRID();
//        HR_ID= UserSingleton.get().getHRID();
        loadContactUsual();

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

    protected void loadContactUsual() {
        contactType = 0;
        GetMessageAsyncTask task = new GetMessageAsyncTask();
        task.execute();
    }

    protected void loadContactHistory() {
        contactType = 1;
        GetMessageAsyncTask t = new GetMessageAsyncTask();
        t.execute();
    }

    protected void loadContactGroup() {
        contactType = 2;
        GetMessageAsyncTask t = new GetMessageAsyncTask();
        t.execute();
    }

    private class GetMessageAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            switch (contactType) {
                case 0:
                    loadContactUsualData();
                    break;
                case 1:
                    loadContactHistoryData(30);
                    break;
                case 2:
                    loadContactGroupData();
                    break;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new CommProgressDialog.Builder(MessageManageActivity.this).setTitle("正在获取数据").create();
            }
            progressDialog.show();
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            bindObjectListsToAdapter(contacts);
            //pbScan.setVisibility(View.INVISIBLE);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        private void loadContactUsualData() {
            String sql = "Select Distinct Common_Contact_ID As HR_ID, Common_Contact_Name As 联系人 " +
                    " From Msg_Common_Contact Where Owner=" + UserSingleton.get().getHRID();
            contacts = WebServiceUtil.getJsonList(sql);
            ColWidth = new ArrayList<>(Arrays.asList(10, 300));
            ColCaption = new ArrayList<>(Arrays.asList("HR_ID", "联系人"));
            HiddenCol = new ArrayList<>(Arrays.asList("HR_ID"));

        }

        //交流历史人员，近30天有过聊天的人
        private void loadContactHistoryData(int Days) {
            int HR_ID = UserSingleton.get().getHRID();
            String sql = "Select distinct MsgReader.Receiver As HR_ID, MsgReader.ReceiverName As 联系人 from msgReader " +
                    " Inner Join Msg on msg.MSID = MsgReader.MSID Where Datediff(Day,MSG.SendTime,GetDate())<" + Days + " And MSG.Sender = " + HR_ID +
                    " Union " +
                    " Select distinct Msg.Sender  As HR_ID,  MSG.SenderName  As 联系人 from msgReader " +
                    " Inner Join Msg on msg.MSID = MsgReader.MSID where Datediff(Day,MSG.SendTime,GetDate())<" + Days + " and MsgReader.Receiver  = " + HR_ID +
                    " Order by 联系人 ";

            contacts = WebServiceUtil.getJsonList(sql);
            ColWidth = new ArrayList<>(Arrays.asList(10, 300));
            ColCaption = new ArrayList<>(Arrays.asList("HR_ID", "联系人"));
            HiddenCol = new ArrayList<>(Arrays.asList("HR_ID"));
        }

        private void loadContactGroupData() {
            WsResult w = WebServiceUtil.getShbCommunicationFun("t_Msg_Group", "GetMyGroup", new Object[]{UserSingleton.get().getHRID(), 60});
            if (w.getResult()) {
                String js = w.getErrorInfo();
                contacts = WebServiceUtil.ConvertJstring2List(js);
                ColWidth = new ArrayList<>(Arrays.asList(10, 80, 300));
                //ColCaptionList=new ArrayList<String>(Arrays.asList("HR_ID","联系人"));
                HiddenCol = new ArrayList<>(Arrays.asList("MG_ID"));
            }
        }

        protected void bindObjectListsToAdapter(final List<JsonObject> JList) {
            if (contacts != null && contacts.size() > 0) {
                ObjectAdapter = new JsonListAdapter(MessageManageActivity.this, contacts);
                //赋值 列宽度
                ObjectAdapter.ColWidth = ColWidth;
                ObjectAdapter.HiddenCol = HiddenCol;
                recyclerView.setLayoutManager(new LinearLayoutManager(MessageManageActivity.this));
                recyclerView.setAdapter(ObjectAdapter);
                ObjectAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        if (JList != null) {
                            Selected_Object = JList.get(position);

                            //Selected_Object转换成HashMap：Result
                            Result = CommonUtil.Convert_JsonObject_HashMap(Selected_Object);

                            Intent Conversation = new Intent(MessageManageActivity.this, ConversationActivity.class);
                            Conversation.putExtra("ContactType", contactType);
                            Conversation.putExtra("Contact", (Serializable) Result);

                            startActivity(Conversation);

                        }

                    }
                });
                recyclerView.setVisibility(View.VISIBLE);
                emptyLayoutManageView.setVisibility(View.GONE);
            } else {
                ToastUtil.showToastLong("您还没有设置常联系人,请添加!");
                emptyLayoutManageView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }

    }


}
