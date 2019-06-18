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
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        nameEditText = (EditText) findViewById(R.id.et_login_name);
        passwordEditText = (EditText) findViewById(R.id.et_login_password);
        loginButton = (Button) findViewById(R.id.name_sign_in_button);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNamePwd();
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

    protected void setHomeButton() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void checkNamePwd() {

        String Name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (Name.isEmpty() || password.isEmpty()) {
            ToastUtil.showToastLong("请输入名字/密码");
        } else {
            CheckNameAndPasswordAsyncTask task = new CheckNameAndPasswordAsyncTask();
            task.execute();
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

    private class CheckNameAndPasswordAsyncTask extends AsyncTask<String, Void, Void> {

        WsResult result = null;

        @Override
        protected Void doInBackground(String... params) {
            String Name = nameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            result = WebServiceUtil.getTryLogin(Name, password);

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {

            if (this.result.getResult()) {
                UserInfoEntity.ID = this.result.getID().intValue();

                Intent resultIntent = new Intent();
                setResult(1, resultIntent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, this.result.getErrorInfo(), Toast.LENGTH_LONG).show();
            }

            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


}
