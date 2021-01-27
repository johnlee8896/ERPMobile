package com.chinashb.www.mobileerp.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chinashb.www.mobileerp.BaseActivity;
import com.chinashb.www.mobileerp.PlanShowListActivity;
import com.chinashb.www.mobileerp.R;
import com.chinashb.www.mobileerp.funs.MESWebServiceUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.TimePickerManager;
import com.chinashb.www.mobileerp.widget.TitleLayoutManagerView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2021/1/18 16:39
 * @author 作者: xxblwf
 * @description 任务创建页面
 */

public class TaskCreateActivity extends BaseActivity implements View.OnClickListener, OnViewClickListener {

    @BindView(R.id.task_create_title_managerView) TitleLayoutManagerView TitleManagerView;
    @BindView(R.id.task_create_creator_textView) TextView creatorTextView;
    @BindView(R.id.task_create_add_creator_button) TextView addCreatorButton;
    @BindView(R.id.task_create_executor_textView) TextView executorTextView;
    @BindView(R.id.task_create_add_executor_button) TextView addExecutorButton;
    @BindView(R.id.task_create_responder_textView) TextView responderTextView;
    @BindView(R.id.task_create_add_responder_button) TextView addResponderButton;
    @BindView(R.id.task_create_auditor_textView) TextView auditorTextView;
    @BindView(R.id.task_create_add_auditor_button) TextView addAuditorButton;
    @BindView(R.id.task_create_level_textView) TextView levelTextView;
    @BindView(R.id.task_create_start_date_button) TextView startDateButton;
    @BindView(R.id.task_create_end_date_button) TextView endDateButton;
    @BindView(R.id.task_create_title_editText) EditText titleEditText;
    @BindView(R.id.task_create_content_editText) EditText contentEditText;
    @BindView(R.id.task_create_commit_button) TextView commitButton;

    private TimePickerManager timePickerManager;
    private String startDateString;
    private String endDateString;
    private String currentDate = "";

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_task_create_layout);
        ButterKnife.bind(this);
        initViews();
        setViewsListener();
    }

    private void initViews() {
        currentDate = UnitFormatUtil.formatTimeToDay(System.currentTimeMillis());
        startDateString = currentDate;
        endDateString = currentDate;
        timePickerManager = new TimePickerManager(TaskCreateActivity.this);
    }

    private void setViewsListener() {
      startDateButton.setOnClickListener(this);
      endDateButton.setOnClickListener(this);
    }

    private void showTimePickerDialog(String pickType) {
        timePickerManager
                .setOnViewClickListener(TaskCreateActivity.this)
                .showDialog(pickType);
    }

    private String getText(Date date) {
        return UnitFormatUtil.sdf_YMD.format(date);
    }

    private void commitTask() {
        checkIsOK();
    }

    private void checkIsOK() {

    }

    @Override public void onClick(View view) {
        if (view == startDateButton){
            showTimePickerDialog(TimePickerManager.PICK_TYPE_START);
        }else if (view == endDateButton){
            showTimePickerDialog(TimePickerManager.PICK_TYPE_END);
        }else if (view == commitButton){
            commitTask();
        }
    }

    @Override public <T> void onClickAction(View v, String tag, T date) {
        if (tag.equals(TimePickerManager.PICK_TYPE_START)) {
//            startDateString = ((Date) date).getTime();
            startDateString = getText((Date) date);
            startDateButton.setText(getText((Date) date));
            startDateButton.setTextColor(getResources().getColor(R.color.color_blue_528FFF));

        } else if (tag.equals(TimePickerManager.PICK_TYPE_END)) {
            endDateString = getText((Date) date);
            endDateButton.setText(getText((Date) date));
            endDateButton.setTextColor(getResources().getColor(R.color.color_blue_528FFF));
//            task.execute();


        }
    }
}
