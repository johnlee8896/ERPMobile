package com.chinashb.www.mobileerp.commonactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.R;

import org.w3c.dom.Text;

public class InputBoxActivity extends AppCompatActivity {

    TextView tvTitle;
    EditText etInput;
    Button btnOk;
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_box);

        tvTitle=(TextView)findViewById(R.id.tv_input_box_title);
        etInput=(EditText)findViewById(R.id.et_inputbox_text);
        btnOk =(Button)findViewById(R.id.btn_input_box_commit);
        btnCancel=(Button)findViewById(R.id.btn_input_box_cancel);


        Intent who=getIntent();

        String Title =who.getStringExtra("Title");

        if (Title.isEmpty()==false)
        {
            tvTitle.setText(Title);
        }

        String OriText =who.getStringExtra("OriText");

        etInput.setText("");
        if (OriText.isEmpty()==false)
        {
            if (OriText.equals("null")==false){etInput.setText(OriText);}

        }


        setHomeButton();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String T=etInput.getText().toString();

                Intent result = new Intent();
                result.putExtra("Input",T);
                setResult(1,result);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

}
