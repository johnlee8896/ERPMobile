package com.chinashb.www.mobileerp.talk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.chinashb.www.mobileerp.MobileMainActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.MsgAdapter;
import com.chinashb.www.mobileerp.basicobject.Msg;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.commonactivity.myGestureListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {
    TextView tvContactWho;

    int contactType = 0;
    HashMap<String, String> Contact;

    RecyclerView rvMsgs;
    MsgAdapter msgAdapter;
    List<Msg> msgs;
    Button btnSend;
    EditText txtNewMessage;
    private List<JsonObject> ObjectLists;
    private JsonObject sample;
    private boolean firstload = true;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        setHomeButton();

        tvContactWho = (TextView) findViewById(R.id.tvconversation_withwho);

        rvMsgs = (RecyclerView) findViewById(R.id.rv_conversation_msgs);
        rvMsgs.setLayoutManager(new LinearLayoutManager(this));
        msgs = new ArrayList<>();


        msgAdapter = new MsgAdapter(this, msgs);
        rvMsgs.setAdapter(msgAdapter);

        Intent who = getIntent();

        contactType = who.getIntExtra("ContactType", 0);
        Contact = (HashMap<String, String>) who.getSerializableExtra("Contact");


        if (contactType == 0 || contactType == 1) {
            String HR_Name2 = Contact.get("联系人");
            tvContactWho.setText(HR_Name2 + " ---- " + MobileMainActivity.userInfo.getHR_Name());
        }
        if (contactType == 2) {
            String GroupName = Contact.get("小组");
            tvContactWho.setText(GroupName);
        }

        btnSend = (Button) findViewById(R.id.btn_send_new_conversation_msg);
        txtNewMessage = (EditText) findViewById(R.id.et_send_new_message);

        addSendNewMessage();

        AddScrollShow();
        addRvMsgScrollListener();

        AsyncQueryMessage t = new AsyncQueryMessage();
        t.execute();

    }


    protected void addRvMsgScrollListener() {

        rvMsgs.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                       @Override
                                       public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                           super.onScrolled(recyclerView, dx, dy);

                                           if (loading) {
                                               return;
                                           }

                                           if (rvMsgs.canScrollVertically(-1)) {
                                           } else {
                                               loading = true;
                                               firstload = false;
                                               AsyncQueryMessage t = new AsyncQueryMessage();
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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    protected void AddScrollShow() {

        rvMsgs.setOnTouchListener(new View.OnTouchListener() {

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

    protected void addSendNewMessage() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = txtNewMessage.getText().toString();
                if (s == null || s == "") {
                    return;
                }

                Msg msg = new Msg();
                msg.mSenderID = MobileMainActivity.userInfo.getHR_ID();
                msg.Msg = s;
                msg.mReceiverID = Integer.valueOf(Contact.get("HR_ID"));

                AsyncSendMsg asyncSendMsg = new AsyncSendMsg();
                asyncSendMsg.execute(msg);

            }


        });
    }

    public String SQL_GetMsg_HR_MsID(int HR_ID1, int HR_ID2, int Msg_Count, int existedMinID) {

        String aboutMinID = "";
        if (existedMinID > 0) {
            aboutMinID = " Where MSID<" + existedMinID;
        }

        //要排除公共信息，群组信息，任务系统信息（单独显示）
        String Msg1_2;
        Msg1_2 = "Select Msg.MSID,Msg.Sender,HR.HR_Name As SenderName,Convert(nvarchar(100),Msg.SendTime,20) As SendTime,MsgReader.Receiver, Convert(nvarchar(2000), Msg_Text) As Msg_Text From Msg " +
                "Inner Join MsgReader On Msg.MsID=MsgReader.MsID " +
                "Inner Join HR on HR.HR_ID=Msg.Sender " +
                "Where Msg.ForAll=0 And (MG_ID=0 Or MG_ID=null) " +
                " And Not Msg.SubTypeID=601" +
                " And Isnull(Msg.Revoked,0)=0 " +
                "And MsgReader.Receiver=" + String.valueOf(HR_ID1) + " And Msg.Sender=" + String.valueOf(HR_ID2);

        String Msg2_1;
        Msg2_1 = "Select Msg.MSID,Msg.Sender,HR.HR_Name As SenderName,Convert(nvarchar(100),Msg.SendTime,20) As SendTime,MsgReader.Receiver, Convert(nvarchar(2000), Msg_Text) As Msg_Text From Msg " +
                "Inner Join MsgReader On Msg.MsID=MsgReader.MsID " +
                "Inner Join HR on HR.HR_ID=Msg.Sender " +
                "Where Msg.ForAll=0 And (MG_ID=0 Or MG_ID=null) " +
                " And Not Msg.SubTypeID=601" +
                "  And Isnull(Msg.Revoked,0)=0 " +
                "And MsgReader.Receiver=" + String.valueOf(HR_ID2) + " And Msg.Sender=" + String.valueOf(HR_ID1);

        String S = "Select Top " + String.valueOf(Msg_Count) + " MsID,Sender,SenderName,SendTime,Receiver,Msg_Text From " +
                " (" + Msg1_2 +
                " Union " + Msg2_1 + ") " +
                " As Convers " + aboutMinID + " Order By MSID Desc ";

        //再按顺序
        S = "Select * From (" + S + ") As M Order By MSID Asc ";
        return S;
    }

    public String SQL_getMsg_from_MSIDString(String IDS) {
        String sql = "Select Msg.MsID,Msg.Sender,HR.HR_Name As SenderName, Convert(nvarchar(100),Msg.SendTime,20) As SendTime, Convert(nvarchar(2000), Msg_Text) As Msg_Text From Msg " +
                " Inner Join HR ON HR.HR_ID=Msg.Sender " +
                " Where Msg.MSID IN " + IDS +
                " Order by Msg.MsID ";
        return sql;

    }

    private class AsyncQueryMessage extends AsyncTask<String, Void, Void> {


        int HR_ID1;
        int HR_ID2;

        String HR_Name2;
        Bitmap HR_Pic2;

        @Override
        protected Void doInBackground(String... params) {

            if (contactType == 0 || contactType == 1) {
                loadHRMsg();
            }
            if (contactType == 2) {
                loadGroupMsg();
            }


            return null;
        }

        private void loadHRMsg() {
            HR_ID1 = MobileMainActivity.userInfo.getHR_ID();
            HR_ID2 = Integer.valueOf(Contact.get("HR_ID"));

            HR_Name2 = WebServiceUtil.getHRName(HR_ID2);

            HR_Pic2 = CommonUtil.getUserPic(ConversationActivity.this, MobileMainActivity.userPictureMap, HR_ID2);

            Integer existedMinID = getMinMsID();

            String sql = SQL_GetMsg_HR_MsID(HR_ID1, HR_ID2, 10, existedMinID);

            //List<JsonObject> newobjects;
            ObjectLists = WebServiceUtil.getJsonList(sql);


            Append_Query_Msg();
        }

        private Integer getMinMsID() {
            if (ObjectLists == null) {
                return -1;
            } else {
                List<Integer> IDs = CommonUtil.getIDListFromJsonList(ObjectLists, "MsID");
                Collections.sort(IDs);
                return IDs.get(0);
            }
        }


        private void loadGroupMsg() {
            Integer MG_ID = Integer.valueOf(Contact.get("MG_ID"));

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

                CommonUtil.getUsersPic(ConversationActivity.this, MobileMainActivity.userPictureMap, Senders);

                Append_Query_Msg();
            }

        }

        private void Append_Query_Msg() {
            if (ObjectLists != null && msgs != null) {
                for (int i = 0; i < ObjectLists.size(); i++) {
                    Msg msg = new Msg();

                    JsonObject mo = ObjectLists.get(i);
                    //mo.getAsJsonPrimitive("MsID").getAsBigInteger();
                    msg.MsID = mo.getAsJsonPrimitive("MsID").getAsInt();
                    msg.msgTimes = mo.getAsJsonPrimitive("SendTime").getAsString();
                    msg.Msg = mo.getAsJsonPrimitive("Msg_Text").getAsString();
                    msg.mSenderID = mo.getAsJsonPrimitive("Sender").getAsInt();
                    msg.mSender = mo.getAsJsonPrimitive("SenderName").getAsString();


                    if (msg.mSenderID != MobileMainActivity.userInfo.getHR_ID()) {
                        msg.HR_Pic2 = CommonUtil.getUserPic(ConversationActivity.this, MobileMainActivity.userPictureMap, msg.mSenderID);
                    }


                    msgs.add(msg);

                }

                Collections.sort(msgs);
            }

        }

        @Override
        protected void onPostExecute(Void result) {


            //第一次直接滚到最后
            msgAdapter.notifyDataSetChanged();

            if (firstload) {
                rvMsgs.scrollToPosition(msgs.size() - 1);
            } else {
                rvMsgs.scrollToPosition(ObjectLists.size());
            }


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

    private class AsyncSendMsg extends AsyncTask<Msg, Void, List<JsonObject>> {


        @Override
        protected List<JsonObject> doInBackground(Msg... strings) {

            Msg m = strings[0];


            //objs=WebServiceUtil.getJsonList(sql);

            //结果
            //return objs;

            return null;
        }

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
