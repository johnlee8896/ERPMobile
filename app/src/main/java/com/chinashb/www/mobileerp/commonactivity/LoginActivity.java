package com.chinashb.www.mobileerp.commonactivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.Ws_Result;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etPw;
    private Button btnLogin;
    private ProgressBar pbScan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etName=(EditText)findViewById(R.id.et_login_name);
        etPw=(EditText)findViewById(R.id.et_login_password);
        btnLogin=(Button)findViewById(R.id.name_sign_in_button);
        pbScan=(ProgressBar)findViewById(R.id.login_progress);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_name_and_password();
            }
        });

        setHomeButton();

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

 protected void check_name_and_password(){

     String Name = etName.getText().toString();
     String password = etPw.getText().toString();

     if (Name.isEmpty() || password.isEmpty())
     {
         Toast.makeText(LoginActivity.this,  "请输入名字/密码", Toast.LENGTH_LONG).show();
     }
     else
     {
         CheckNameAndPasswordAsyncTask task = new CheckNameAndPasswordAsyncTask();
         task.execute();
     }

 }



    private class CheckNameAndPasswordAsyncTask extends AsyncTask<String, Void, Void> {

        Ws_Result r = null;
        @Override
        protected Void doInBackground(String... params) {
            String Name = etName.getText().toString();
            String Pw = etPw.getText().toString();

            r= WebServiceUtil.getTryLogin(Name,Pw);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (r.getResult())
            {
                UserInfoEntity.ID=  r.getID().intValue();

                Intent resultIntent = new Intent();
                setResult(1,resultIntent);
                finish();
            }
            else
            {
                Toast.makeText(LoginActivity.this,  r.getErrorInfo(), Toast.LENGTH_LONG).show();
            }

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
