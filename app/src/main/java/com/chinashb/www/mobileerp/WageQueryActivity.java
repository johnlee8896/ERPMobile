package com.chinashb.www.mobileerp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chinashb.www.mobileerp.basicobject.QueryAsyncTask;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.CommonSelectInputDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * @date 创建时间 2021/2/19 16:39
 * @author 作者: xxblwf
 * @description 工资查询页面
 */

public class WageQueryActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.wage_select_button) TextView selectButton;
    @BindView(R.id.wage_select_result_textView) TextView resultTextView;

    private CommonSelectInputDialog commonSelectInputDialog;
    private OnViewClickListener onViewClickListener = new OnViewClickListener() {
        @Override public <T> void onClickAction(View v, String tag, T t) {
            if (t != null) {
                selectButton.setText((CharSequence) t);
                QueryWageAsyncTask task = new QueryWageAsyncTask();
                task.execute((String) t);
            }
            if (commonSelectInputDialog != null && commonSelectInputDialog.isShowing()) {
                commonSelectInputDialog.dismiss();
            }
        }
    };

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wage_query_layout);
        ButterKnife.bind(this);
        setViewsListener();

    }

    private void setViewsListener() {
        selectButton.setOnClickListener(this);

    }

    private void handleSelectMonth() {
        if (commonSelectInputDialog == null) {
            commonSelectInputDialog = new CommonSelectInputDialog(WageQueryActivity.this);
        }
        commonSelectInputDialog.show();
        commonSelectInputDialog.setOnViewClickListener(onViewClickListener);
        commonSelectInputDialog.setTitle("请选择查询月份");
        commonSelectInputDialog.setSelectOnly(true);
        commonSelectInputDialog.refreshContent(getNOList());
    }

    private List<String> getNOList() {
        List<String> noList = new ArrayList<>();
//        C-20191226-01
//        for (int i = 0; i < 15; i++) {
//            noList.add(String.format("%s-%s", UnitFormatUtil.formatTimeToDayWithoutLine(System.currentTimeMillis()), String.format("%02d", i + 1)));
//        }
//        Date date = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//       int todayNumber = c.get(Calendar.DAY_OF_MONTH);



//        Date date = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        int todayNumber = c.get(Calendar.DAY_OF_MONTH);
//        int i;
//        //20号发工资
//        if (todayNumber >= 20) {
//            i = 1;
//        } else {
//            i = 2;
//        }
////            for (int i = 1; i < 4; i++){
//        int count = 0;
//        for (; ; i++) {
//            if (count < 3){
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.MONTH, 0 - i);
//                noList.add(UnitFormatUtil.formatTimeYYYYMM(calendar.getTimeInMillis()));
//                count++;
//            }
//        }


        for (int i = 0; i < 3; i++){

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,0 - i);
            noList.add(UnitFormatUtil.formatTimeYYYYMM(calendar.getTimeInMillis()));
        }

        return noList;
    }

    @Override public void onClick(View v) {
        handleSelectMonth();
    }

    private class QueryWageAsyncTask extends AsyncTask<String,Void ,WsResult >{

        @Override protected WsResult doInBackground(String... strings) {
            if (strings.length > 0){

                if (!TextUtils.isEmpty(UserSingleton.get().getHRNO())){
                    WsResult result =  WebServiceUtil.queryWage(UserSingleton.get().getHRNO(),strings[0]);
                    return result;
                }
            }
            return null;
        }

        @Override protected void onPostExecute(WsResult result) {
            super.onPostExecute(result);
            if (result != null){
                if (result.getResult()){
                    resultTextView.setText( result.getErrorInfo().replace("\\r\\n","\n"));
                }else{
                    ToastUtil.showToastShort(result.getErrorInfo());
                }
            }
        }
    }
}
