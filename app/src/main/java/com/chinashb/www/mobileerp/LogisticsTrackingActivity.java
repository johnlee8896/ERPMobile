package com.chinashb.www.mobileerp;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.AttendanceDetailBean;
import com.chinashb.www.mobileerp.bean.AttendanceInfoBean;
import com.chinashb.www.mobileerp.bean.AttendanceReportBean;
import com.chinashb.www.mobileerp.bean.SystemDateBean;
import com.chinashb.www.mobileerp.funs.WebServiceUtil;
import com.chinashb.www.mobileerp.libapi.bean.BaseBean;
import com.chinashb.www.mobileerp.libapi.bean.BaseListBean;
import com.chinashb.www.mobileerp.permission.PermissionsUtil;
import com.chinashb.www.mobileerp.singleton.GPSLocationSingleton;
import com.chinashb.www.mobileerp.singleton.SPSingleton;
import com.chinashb.www.mobileerp.utils.APIDefine;
import com.chinashb.www.mobileerp.utils.DeviceUtil;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.OnViewClickListener;
import com.chinashb.www.mobileerp.utils.SPDefine;
import com.chinashb.www.mobileerp.utils.StringUtils;
import com.chinashb.www.mobileerp.utils.ToastUtil;
import com.chinashb.www.mobileerp.utils.UnitFormatUtil;
import com.chinashb.www.mobileerp.widget.AttendanceOutsideRemarkDialog;
import com.chinashb.www.mobileerp.widget.CommAlertDialog;
import com.chinashb.www.mobileerp.widget.OnDialogViewClickListener;
import com.chinashb.www.mobileerp.widget.SuccessNoticeDialog;
import com.chinashb.www.mobileerp.widget.calendar.BottomMarkDotBean;
import com.chinashb.www.mobileerp.widget.calendar.CalendarEntity;
import com.chinashb.www.mobileerp.widget.group.GroupImageTextLayout;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/***
 * @date 创建时间 2022/10/26 1:14 PM
 * @author 作者: liweifeng
 * @description 追踪物流信息
 */
class LogisticsTrackingActivity extends BaseActivity {
    private static final int TAG_UPDATE_CURRENT_TIME = 100;
    private static final long ONE_DAY_TIME_MILLIS = 86400000;
    private static final int TAG_UPDATE_ORDER_SUCCESS = 0X101;
    private static final int TAG_Request_fail = 0X102;
    @BindView(R.id.logistics_tracking_location_layout) GroupImageTextLayout locationLayout;
    private double longitude;
    private double latitude;
    private String locationName;
    private boolean canAttendance = false;
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    locationName = location.getAddress();

                    System.out.println("================================================longitude = " + longitude);
                    System.out.println("================================================latitude = " + latitude);
                    System.out.println("================================================locationName = " + locationName);

                    String rule = SPSingleton.get().getString(SPDefine.KEY_attendance_rule, "");
                    if (!StringUtils.isStringValid(rule)) {
//                        canAttendance = false;
                        //规则拿到与否并不影响打卡
                    } else {
                    }
                } else {
                    //定位失败
                    locationLayout.setText("定位失败" + "错误信息:" + location.getErrorInfo());
                    canAttendance = false;
                }
            } else {
                locationLayout.setText("定位失败，获取不到定位位置");
                canAttendance = false;
            }
        }
    };
    private boolean isInnerRange = false;
    private int commitType = 0;
    private boolean isToday = true;
    private boolean isBeforeToday = false;
    private int attendanceRecordCounter = 0;
    //    private SparseBooleanArray eachDayPointStatusArray ;
    private Map<Long, Boolean> dateStatusMap = new HashMap<>();

    private boolean hasRequestedPermission = false;
    private Unbinder unbinder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_tracking_layout);
        unbinder = ButterKnife.bind(this);
        GPSLocationSingleton.get().startRequestPermission(new PermissionsUtil.OnPermissionCallbackImpl() {
            @Override
            public void onSuccess(String[] permission) {
                super.onSuccess(permission);
                hasRequestedPermission = true;
            }
        });

        //// TODO: 2020/11/20
        GPSLocationSingleton.get().beginLocationWithPermission(true, locationListener);

        getAttendanceDetail(System.currentTimeMillis());
        initViews();
        getSystemDate();
        getRecentDaysReport(30);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //todo 当界面离开时，停止定位
        stopLocation();
    }

    private void getRecentDaysReport(int dayNumber) {
        Type type = new TypeToken<BaseBean<BaseListBean<AttendanceReportBean>>>() {
        }.getType();
        Map<String, String> map = new HashMap<>();
        map.put("day", dayNumber + "");
//        PresenterSingleton.get().doGetData(this, APIDefine.API_attendance_report, map, type, this);
    }

    private void getAttendanceDetail(long dateLong) {
        Map<String, String> map = new HashMap<>();
        map.put("date", String.valueOf(dateLong));
        Type type = new TypeToken<BaseBean<BaseListBean<AttendanceDetailBean>>>() {
        }.getType();
//        PresenterSingleton.get().doGetData(this, APIDefine.API_get_attendance_by_date, map, type, this);

        GetAttendanceDetailTask task = new GetAttendanceDetailTask();
        task.execute(UnitFormatUtil.getCurrentYMD());
    }

    private void refreshUIByDay(long selectDate) {
        getAttendanceDetail(selectDate);
    }

    private void showRemarkDialog() {
        AttendanceOutsideRemarkDialog outerDialog = new AttendanceOutsideRemarkDialog(this);
        outerDialog.show();
        outerDialog.setOnViewClickListener(new OnViewClickListener() {
            @Override
            public <T> void onClickAction(View v, String tag, T t) {
                beginAttendance((String) t);
            }
        });
    }

    private void initViews() {
        SimpleDateFormat sdf = new SimpleDateFormat("今天 yyyy-MM-dd EEEE");
//        todayTextView.setText(sdf.format(Calendar.getInstance(TimeZone.getDefault()).getTime()));

    }

    private void getSystemDate() {
        Type type = new TypeToken<BaseBean<SystemDateBean>>() {
        }.getType();
        Map<String, String> map = new HashMap<>();
        map.put("date", String.valueOf(System.currentTimeMillis()));
//        PresenterSingleton.get().doGetData(this, APIDefine.API_get_system_time, map, type, this);
    }

    private CalendarEntity getSchemeCalendar(long dateTime, boolean isException) {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateTime);
        CalendarEntity calendar = new CalendarEntity();
        calendar.setYear(c.get(Calendar.YEAR));
        calendar.setMonth(c.get(Calendar.MONTH) + 1);
        calendar.setDay(c.get(Calendar.DAY_OF_MONTH));

        BottomMarkDotBean bottomMarkDotBean = new BottomMarkDotBean();
        bottomMarkDotBean.setShow(true);
        bottomMarkDotBean.setNormal(!isException);
        if (!isException) {
            calendar.setSchemeColor(getResources().getColor(R.color.color_blue_2E7FEF));
        } else {
            calendar.setSchemeColor(getResources().getColor(R.color.color_red_E94156));
        }
        calendar.setBottomMarkDotBean(bottomMarkDotBean);
        return calendar;
    }

    private void executeAttendance() {
        if (canAttendance) {
            if (isInnerRange) {
                if (attendanceRecordCounter == 2) {
                    showConfirmDialog();
                } else {
                    beginAttendance("");
                }
            } else {
                //提示外勤
                showRemarkDialog();
            }
        }

        //// TODO: 2020/11/20
        beginAttendance("");


    }

    private void showConfirmDialog() {
        new CommAlertDialog.DialogBuilder(this)
                .setTitle("温馨提示").setMessage("你确定要更新打卡吗")
                .setLeftText("确定").setRightText("取消")
                .setAllButtonColorRes(R.color.color_228BE0)
                .setOnViewClickListener(new OnDialogViewClickListener() {
                    @Override
                    public void onViewClick(Dialog dialog, View v, int tag) {
                        if (tag == CommAlertDialog.TAG_CLICK_LEFT) {
                            beginAttendance("");
                        }
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void beginAttendance(String remark) {
//        executeAttendance(HR_ID As Integer, HR_Name As String, day_span As String, device_id As String, latitude As String, longitude As String, address As String, remark As String, out As Integer)

        Map<String, String> map = new HashMap<>();
        map.put("address", locationName);
        map.put("day_span", 0 + "");
        map.put("device_id", DeviceUtil.getDeviceIdString());
        map.put("latitude", latitude + "");
        map.put("longitude", longitude + "");
        map.put("remark", remark);
        map.put("out", isInnerRange ? "0" : "1");

//        Type type = new TypeToken<BaseBean>() {
////        }.getType();
////
////        PresenterSingleton.get().doPostAction(this, APIDefine.API_attendance, map, type, this);
        ExecuteAttendanceTask task = new ExecuteAttendanceTask();
        task.execute(map);
    }

    private void stopLocation() {
        // 停止定位
        GPSLocationSingleton.get().stopLocationLoop();
    }

    private void getAttendanceRule() {
        Type type = new TypeToken<BaseBean<AttendanceInfoBean>>() {
        }.getType();
//        PresenterSingleton.get().doGetData(APIDefine.API_get_attendance_myinfo, type, this);
    }

    private void showApiError(String api, BaseBean msg, String requestApi) {
        if (api.equals(requestApi)) {
//            ToastUtil.showToastShort(getString(R.string.request_server_error) + api + msg.getMessage());
            ToastUtil.showToastShort("接口请求错误" + api + msg.getMessage());
        }
    }

    public void onFail(String api, BaseBean msg) {
        showApiError(api, msg, APIDefine.API_get_system_time);
        showApiError(api, msg, APIDefine.API_get_attendance_myinfo);
        showApiError(api, msg, APIDefine.API_attendance);
        showApiError(api, msg, APIDefine.API_get_attendance_by_date);
        showApiError(api, msg, APIDefine.API_attendance_report);

        if (api.equals(APIDefine.API_get_attendance_by_date)) {
            getSystemDate();
        }
        if (api.equals(APIDefine.API_attendance)) {
            ToastUtil.showToastLong("打卡失败！");
        }
    }

    public <T> void initData(String api, T t) {
        if (api.equals(APIDefine.API_get_system_time)) {
            SystemDateBean bean = (SystemDateBean) t;
            if (bean != null) {
                String attendanceRule = SPSingleton.get().getString(SPDefine.KEY_attendance_rule, null);
                if (bean.isExpired() || !StringUtils.isStringValid(attendanceRule)) {
                    stopLocation();
//                    canAttendance = false;
                    getAttendanceRule();
                }
            }
        } else if (api.equals(APIDefine.API_get_attendance_myinfo)) {
            AttendanceInfoBean bean = (AttendanceInfoBean) t;
            if (bean != null) {
                canAttendance = true;
                String json = JsonUtil.objectToJson(bean);
                SPSingleton.get().putString(SPDefine.KEY_attendance_rule, json);
                if (hasRequestedPermission) {
                    GPSLocationSingleton.get().beginLocationWithPermission(true, locationListener);
                }
            }
        } else if (api.equals(APIDefine.API_attendance)) {
            if (t != null) {
                SuccessNoticeDialog dialog = new SuccessNoticeDialog(this);
                dialog.show();
                getAttendanceDetail(System.currentTimeMillis());

            }
        } else if (api.equals(APIDefine.API_get_attendance_by_date)) {
//            BaseListBean<AttendanceDetailBean> listBean = (BaseListBean<AttendanceDetailBean>) t;
//            attendanceRecordCounter = listBean.getCount();
//            if (isToday) {
//                showTodayAttendance(listBean);
//            } else {
//                showBeforeTodayAttendance(listBean);
//            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private class ExecuteAttendanceTask extends AsyncTask<Map<String, String>, Void, WsResult> {

        @Override
        protected WsResult doInBackground(Map<String, String>... maps) {
            if (maps.length > 0) {
                return WebServiceUtil.executeAttendance(maps[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result.getResult()) {
                ToastUtil.showToastLong(commitType == 1 ? "打卡成功！" : "更新打卡成功！");
            } else {
                ToastUtil.showToastLong("操作失败！" + result.getErrorInfo());
            }
        }
    }

    private class GetAttendanceDetailTask extends AsyncTask<String, Void, WsResult> {

        @Override
        protected WsResult doInBackground(String... strings) {
            try {
                if (strings.length > 0) {
                    return WebServiceUtil.getAttendanceDetailByDay(strings[0]);
                }
                return null;
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(WsResult result) {
            if (result == null) {
                ToastUtil.showToastLong("获取考勤信息失败！");
            } else {
                if (result.getResult()) {
                    ToastUtil.showToastLong(commitType == 1 ? "打卡成功！" : "更新打卡成功！");
                    try {
                        Type type = new TypeToken<List<AttendanceDetailBean>>() {
                        }.getType();
                        List<AttendanceDetailBean> listBean = JsonUtil.parseJsonToObject(result.getErrorInfo(), type);

                    } catch (Exception e) {
                        ToastUtil.showToastShort(e.getMessage());
                    }

                } else {
                    ToastUtil.showToastLong("操作失败！" + result.getErrorInfo());
                }
//                handler.sendEmptyMessage(TAG_UPDATE_ORDER_SUCCESS);
            }

        }
    }
}

