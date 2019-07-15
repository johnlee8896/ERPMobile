package com.chinashb.www.mobileerp.talk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.MsgAdapter;
import com.chinashb.www.mobileerp.basicobject.Msg;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.myGestureListener;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.widget.EmptyLayoutManageView;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.conversation_image_button) Button imageButton;
    @BindView(R.id.conversation_video_button) Button videoButton;
    @BindView(R.id.conversation_file_button) Button fileButton;
    @BindView(R.id.conversation_emptyManagerView) EmptyLayoutManageView emptyManagerView;
    int contactType = 0;
    HashMap<String, String> contactMap;
    RecyclerView recyclerView;
    MsgAdapter msgAdapter;
    List<Msg> messageList;
    Button sendButton;
    EditText txtNewMessage;
    private TextView tvContactWho;
    private List<JsonObject> ObjectLists;
    private JsonObject sample;
    private boolean firstLoad = true;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);
        setHomeButton();
        tvContactWho = (TextView) findViewById(R.id.tvconversation_withwho);
        recyclerView = (RecyclerView) findViewById(R.id.rv_conversation_msgs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        msgAdapter = new MsgAdapter(this, messageList);
        recyclerView.setAdapter(msgAdapter);
        Intent who = getIntent();
        contactType = who.getIntExtra("ContactType", 0);
        contactMap = (HashMap<String, String>) who.getSerializableExtra("Contact");

        if (contactType == 0 || contactType == 1) {
            String HR_Name2 = contactMap.get("联系人");
            tvContactWho.setText(HR_Name2 + " ---- " + UserSingleton.get().getUserInfo().getHR_Name());
        }
        if (contactType == 2) {
            String GroupName = contactMap.get("小组");
            tvContactWho.setText(GroupName);
        }

        sendButton = (Button) findViewById(R.id.btn_send_new_conversation_msg);
        txtNewMessage = (EditText) findViewById(R.id.et_send_new_message);
        addViewListeners();

        AddScrollShow();
        addRvMsgScrollListener();

        QueryMessageAsyncTask t = new QueryMessageAsyncTask();
        t.execute();
        setDataShowGone();

    }

    private void setDataShowGone() {
        if (messageList != null && messageList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyManagerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyManagerView.setVisibility(View.VISIBLE);
        }
    }


    protected void addRvMsgScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                             @Override
                                             public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                 super.onScrolled(recyclerView, dx, dy);

                                                 if (loading) {
                                                     return;
                                                 }

                                                 if (recyclerView.canScrollVertically(-1)) {
                                                 } else {
                                                     loading = true;
                                                     firstLoad = false;
                                                     QueryMessageAsyncTask t = new QueryMessageAsyncTask();
                                                     t.execute();
                                                 }
                                             }
                                         }

        );


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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    protected void AddScrollShow() {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {

                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                switch (event.getAction()) {
                                                    case MotionEvent.ACTION_DOWN:

                                                        break;
                                                    case MotionEvent.ACTION_MOVE:

                                                        break;
                                                    case MotionEvent.ACTION_UP:
                                                        Log.d("Action", "UP");
                                                        //起来了，停止了移动,
                                                        //起来，算是完成一次加载动作
                                                        loading = false;

                                                        break;
                                                }

                                                //这里一定要返回gestureDetector.onTouchEvent(event)  不然滑动监听无效
                                                GestureDetector gestureDetector = new GestureDetector(ConversationActivity.this, new myGestureListener());
                                                return gestureDetector.onTouchEvent(event);
                                            }


                                        }
        );

    }

    protected void addViewListeners() {
        sendButton.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        videoButton.setOnClickListener(this);
        fileButton.setOnClickListener(this);
    }

    private void handleSend() {
        String messageContent = txtNewMessage.getText().toString();
        if (TextUtils.isEmpty(messageContent)) {
            ToastUtil.showToastLong("请输入消息内容！");
        } else {
            Msg msg = new Msg();
            msg.mSenderID = UserSingleton.get().getHRID();
            msg.Msg = messageContent;
            if (contactMap.get("HR_ID") != null) {
                try {
                    msg.mReceiverID = Integer.valueOf(contactMap.get("HR_ID"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            SendMsgAsyncTask asyncSendMsg = new SendMsgAsyncTask();
            asyncSendMsg.execute(msg);
        }
    }

    private String getMsgHRMsIDSQL(int myId, int otherId, int Msg_Count, int existedMinID) {
        String aboutMinID = "";
        if (existedMinID > 0) {
            aboutMinID = " Where MSID<" + existedMinID;
        }
        //要排除公共信息，群组信息，任务系统信息（单独显示）
        String otherToMeSql;
        otherToMeSql = "Select Msg.MSID,Msg.Sender,HR.HR_Name As SenderName,Convert(nvarchar(100),Msg.SendTime,20) As SendTime,MsgReader.Receiver, Convert(nvarchar(2000), Msg_Text) As Msg_Text From Msg " +
                "Inner Join MsgReader On Msg.MsID=MsgReader.MsID " +
                "Inner Join HR on HR.HR_ID=Msg.Sender " +
                "Where Msg.ForAll=0 And (MG_ID=0 Or MG_ID=null) " +
                " And Not Msg.SubTypeID=601" +
                " And Isnull(Msg.Revoked,0)=0 " +
                "And MsgReader.Receiver=" + myId + " And Msg.Sender=" + otherId;

        String meToOtherSql;
        meToOtherSql = "Select Msg.MSID,Msg.Sender,HR.HR_Name As SenderName,Convert(nvarchar(100),Msg.SendTime,20) As SendTime,MsgReader.Receiver, Convert(nvarchar(2000), Msg_Text) As Msg_Text From Msg " +
                "Inner Join MsgReader On Msg.MsID=MsgReader.MsID " +
                "Inner Join HR on HR.HR_ID=Msg.Sender " +
                "Where Msg.ForAll=0 And (MG_ID=0 Or MG_ID=null) " +
                " And Not Msg.SubTypeID=601" +
                "  And Isnull(Msg.Revoked,0)=0 " +
                "And MsgReader.Receiver=" + otherId + " And Msg.Sender=" + myId;

        String sql = "Select Top " + Msg_Count + " MsID,Sender,SenderName,SendTime,Receiver,Msg_Text From " +
                " (" + otherToMeSql +
                " Union " + meToOtherSql + ") " +
                " As Convers " + aboutMinID + " Order By MSID Desc ";

        //再按顺序
        sql = "Select * From (" + sql + ") As M Order By MSID Asc ";
        return sql;
    }

    public String SQL_getMsg_from_MSIDString(String IDS) {
        String sql = "Select Msg.MsID,Msg.Sender,HR.HR_Name As SenderName, Convert(nvarchar(100),Msg.SendTime,20) As SendTime, Convert(nvarchar(2000), Msg_Text) As Msg_Text From Msg " +
                " Inner Join HR ON HR.HR_ID=Msg.Sender " +
                " Where Msg.MSID IN " + IDS +
                " Order by Msg.MsID ";
        return sql;

    }

    private boolean isHasThisMessage(int messageId) {
        for (Msg message : messageList) {
            if (message.MsID == messageId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == imageButton) {

        } else if (v == videoButton) {

        } else if (v == fileButton) {

        } else if (v == sendButton) {
            handleSend();
        }
    }

    private class QueryMessageAsyncTask extends AsyncTask<String, Void, Void> {
        int myId;
        int otherId;
        String otherName;
        Bitmap otherPicBitmap;

        @Override
        protected Void doInBackground(String... params) {
            switch (contactType) {
                case 0:
                case 1:
                    loadHRMsg();
                    break;
                case 2:
                    loadGroupMsg();
                default:
                    break;
            }
            return null;
        }

        private void loadHRMsg() {
            myId = UserSingleton.get().getHRID();
            otherId = Integer.valueOf(contactMap.get("HR_ID"));
            otherName = WebServiceUtil.getHRName(otherId);
            otherPicBitmap = CommonUtil.getUserPic(ConversationActivity.this, CommonUtil.userPictureMap, otherId);
            int existedMinID = getMinMsID();
            String sql = getMsgHRMsIDSQL(myId, otherId, 10, existedMinID);
            //List<JsonObject> newobjects;
            ObjectLists = WebServiceUtil.getJsonList(sql);
            appendQueryMsg();
        }

        private Integer getMinMsID() {
            if (ObjectLists == null) {
                return -1;
            } else {
                List<Integer> IDs = CommonUtil.getIDListFromJsonList(ObjectLists, "MsID");
                if (IDs != null && IDs.size() > 0) {

                    Collections.sort(IDs);
                    return IDs.get(0);
                }
                return -1;
            }
        }


        private void loadGroupMsg() {
            Integer MG_ID = Integer.valueOf(contactMap.get("MG_ID"));

            Integer existedMinID = getMinMsID();

            WsResult w;
            if (existedMinID == -1) {
                w = WebServiceUtil.getShbCommunicationFun("Msg",
                        "dtGetMsg_Group_MsID", new Object[]{MG_ID, 10});
            } else {
                w = WebServiceUtil.getShbCommunicationFun("Msg",
                        "dtGetMoreMsg_Group_Early_MsID", new Object[]{MG_ID, 10, existedMinID});
            }


            if (w.getResult()) {
                String js = w.getErrorInfo();
                List<JsonObject> objects = WebServiceUtil.ConvertJstring2List(js);
                String IDs = CommonUtil.getIDSqlStringFromJsonList(objects, "MsID");

                String sql = SQL_getMsg_from_MSIDString(IDs);

                ObjectLists = WebServiceUtil.getJsonList(sql);

                List<Integer> Senders = CommonUtil.getIDListFromJsonList(ObjectLists, "Sender");
                CommonUtil.getUsersPic(ConversationActivity.this, CommonUtil.userPictureMap, Senders);
                appendQueryMsg();
            }

        }

        private void appendQueryMsg() {
            if (ObjectLists != null && messageList != null) {
                for (int i = 0; i < ObjectLists.size(); i++) {
                    Msg msg = new Msg();
                    JsonObject mo = ObjectLists.get(i);
                    int messageId = mo.getAsJsonPrimitive("MsID").getAsInt();
                    if (!isHasThisMessage(messageId)) {
                        msg.MsID = messageId;
                        msg.msgTimes = mo.getAsJsonPrimitive("SendTime").getAsString();
                        msg.Msg = mo.getAsJsonPrimitive("Msg_Text").getAsString();
                        msg.mSenderID = mo.getAsJsonPrimitive("Sender").getAsInt();
                        msg.mSender = mo.getAsJsonPrimitive("SenderName").getAsString();
                        if (msg.mSenderID != UserSingleton.get().getHRID()) {
                            msg.HR_Pic2 = CommonUtil.getUserPic(ConversationActivity.this, CommonUtil.userPictureMap, msg.mSenderID);
                        }
                        messageList.add(msg);
                    }
                    //mo.getAsJsonPrimitive("MsID").getAsBigInteger();

                }

                Collections.sort(messageList);
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            //第一次直接滚到最后
            msgAdapter.notifyDataSetChanged();
            if (firstLoad) {
                recyclerView.scrollToPosition(messageList.size() - 1);
            } else {
                recyclerView.scrollToPosition(ObjectLists.size());
            }
            setDataShowGone();
            //pbScan.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class SendMsgAsyncTask extends AsyncTask<Msg, Void, List<JsonObject>> {


        @Override
        protected List<JsonObject> doInBackground(Msg... strings) {

            Msg m = strings[0];


            //objs=WebServiceUtil.getJsonList(sql);

            //结果
            //return objs;

            return null;
        }

        @Override
        protected void onPostExecute(List<JsonObject> result) {
            super.onPostExecute(result);


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

}
