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

import com.chinashb.www.mobileerp.MobileMainActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.adapter.JsonListAdapter;
import com.chinashb.www.mobileerp.basicobject.Ws_Result;
import com.chinashb.www.mobileerp.funs.OnItemClickListener;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.funs.CommonUtil;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConversationContactsActivity extends AppCompatActivity {

    RecyclerView rvContact;
    int contactType=0; //0:Usual 1:History 30  2:Group
    List<JsonObject> contacts;
    JsonListAdapter ObjectAdapter;
    public List<Integer> ColWidth;
    public List<String>ColCaption;
    public List<String>HiddenCol;
    JsonObject Selected_Object;
    HashMap<String, String> Result;

    Integer HR_ID;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_contacts);

        setHomeButton();

        rvContact=(RecyclerView)findViewById(R.id.rv_conversation_contactlist);
        contacts=new ArrayList<>();
        JsonListAdapter jsonListAdapter= new JsonListAdapter(this, contacts);
        rvContact.setAdapter(jsonListAdapter);
        rvContact.setLayoutManager(new LinearLayoutManager(this));


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_contact);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        HR_ID= MobileMainActivity.userInfo.getHR_ID();

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

    protected void setHomeButton(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void loadContactUsual(){
        contactType=0;
        AsyncGetContact t=new AsyncGetContact();
        t.execute();
    }

    protected void loadContactHistory(){
        contactType=1;
        AsyncGetContact t=new AsyncGetContact();
        t.execute();
    }

    protected void loadContactGroup(){
        contactType=2;
        AsyncGetContact t=new AsyncGetContact();
        t.execute();
    }


    private class AsyncGetContact extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {


            switch (contactType){
                case 0:
                    loadContactUsualData();
                    break;
                case 1:
                    loadConactHistoryData(30);
                    break;
                case 2:
                    loadConcactGroupData();
                    break;
            }


            return null;
        }


        protected void loadContactUsualData(){
            String sql="Select Distinct Common_Contact_ID As HR_ID, Common_Contact_Name As 联系人 " +
                    " From Msg_Common_Contact Where Owner=" + MobileMainActivity.userInfo.getHR_ID();

            contacts = WebServiceUtil.getJsonList(sql);

            ColWidth=new ArrayList<Integer>(Arrays.asList(10,300));
            ColCaption=new ArrayList<String>(Arrays.asList("HR_ID","联系人"));
            HiddenCol=new ArrayList<String>(Arrays.asList("HR_ID"));

        }

        //交流历史人员，近30天有过聊天的人
        protected void loadConactHistoryData(Integer Days)
        {
            String sql= "Select distinct MsgReader.Receiver As HR_ID, MsgReader.ReceiverName As 联系人 from msgReader " +
                    " Inner Join Msg on msg.MSID = MsgReader.MSID Where Datediff(Day,MSG.SendTime,GetDate())<" + Days + " And MSG.Sender = " + HR_ID +
                    " Union " +
                    " Select distinct Msg.Sender  As HR_ID,  MSG.SenderName  As 联系人 from msgReader " +
                    " Inner Join Msg on msg.MSID = MsgReader.MSID where Datediff(Day,MSG.SendTime,GetDate())<" + Days + " and MsgReader.Receiver  = " + HR_ID  +
                    " Order by 联系人 ";

            contacts = WebServiceUtil.getJsonList(sql);

            ColWidth=new ArrayList<Integer>(Arrays.asList(10,300));
            ColCaption=new ArrayList<String>(Arrays.asList("HR_ID","联系人"));
            HiddenCol=new ArrayList<String>(Arrays.asList("HR_ID"));

        }

        protected void loadConcactGroupData()
        {

           Ws_Result w= WebServiceUtil.getShbCommunicationFun("t_Msg_Group","GetMyGroup",new Object[]{HR_ID,60});

           if (w.getResult())
           {
               String js=w.getErrorInfo();

               contacts= WebServiceUtil.ConvertJstring2List(js);
               ColWidth=new ArrayList<Integer>(Arrays.asList(10,80,300));
               //ColCaption=new ArrayList<String>(Arrays.asList("HR_ID","联系人"));
               HiddenCol=new ArrayList<String>(Arrays.asList("MG_ID"));
           }
        }

        @Override
        protected void onPostExecute(Void result) {

            bindObjectListsToAdapter(contacts);



            //pbScan.setVisibility(View.INVISIBLE);
        }

        protected void bindObjectListsToAdapter(final List<JsonObject> JList){
            ObjectAdapter = new JsonListAdapter( ConversationContactsActivity.this, contacts);
            //赋值 列宽度
            ObjectAdapter.ColWidth = ColWidth;
            ObjectAdapter.HiddenCol =HiddenCol;
            rvContact.setLayoutManager( new LinearLayoutManager(ConversationContactsActivity.this));
            rvContact.setAdapter(ObjectAdapter);

            ObjectAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
                    if(JList!=null)
                    {
                        Selected_Object=JList.get(position);

                        //Selected_Object转换成HashMap：Result
                        Result= CommonUtil.Convert_JsonObject_HashMap(Selected_Object);

                        Intent Conversation = new Intent(ConversationContactsActivity.this, ConversationActivity.class);
                        Conversation.putExtra("ContactType", contactType);
                        Conversation.putExtra("Contact",(Serializable)Result);

                        startActivity(Conversation);

                    }

                }
            });
        }
        @Override
        protected void onPreExecute() {
            //pbScan.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


}
