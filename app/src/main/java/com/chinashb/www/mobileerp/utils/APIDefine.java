package com.chinashb.www.mobileerp.utils;

/***
 * @date 创建时间 2018/7/10 21:12
 * @author 作者: liweifeng
 * @description 接口API定义
 */
public class APIDefine {
    //预发布环境
    public static final int API_PRE_RELEASE = -1;
    //测试环境
    public static final int API_DEBUG = 0;
    //发布的正式环境
    public static final int API_RELEASE = 1;

    //发布前记得修改环境
    public static int apiEnvironment = API_DEBUG;


    /***************以下是API环境定义************************************/
    public static final String SERVICE_ERP_OA = "yx-oa-service";
    public static final String SERVICE_ERP_USER = "yx-erp-user-service";

    private static final String apiVersion = "/api/v1/";
//    private static final String BASE_IP_SERVER_DEBUG = "http://192.168.6.69:8080/";
    private static final String BASE_IP_SERVER_DEBUG = "http://47.98.116.87:8080/";
    private static final String BASE_IP_SERVER_PER_RELEASE = "http://192.168.6.69:8080/";
    private static final String BASE_IP_SERVER_RELEASE = "https://erp-bank-api.hwariot.com/";


    public static String getAPIVersion() {
        if (apiEnvironment == API_RELEASE) {
            return "";
        } else if (apiEnvironment == API_DEBUG) {
            return "180530";
        } else {
            return "180530";
        }

    }

    public static String getBaseRestUrlByVersion(String serviceName) {
        if (apiEnvironment == API_RELEASE) {
            return BASE_IP_SERVER_RELEASE + serviceName + apiVersion;
        } else if (apiEnvironment == API_DEBUG) {
            return BASE_IP_SERVER_DEBUG + serviceName + apiVersion;
        } else {
            return BASE_IP_SERVER_PER_RELEASE + serviceName + apiVersion;
        }
    }

    /*应用检查升级的接口*/
    private static String getUpgradeBaseUrl() {
        if (apiEnvironment == API_RELEASE) {
            // 应用内升级使用，正式环境
            return "http://app.hwariot.com/user-service/api/v1/";
        } else if (apiEnvironment == API_DEBUG) {
            // 应用内升级使用，测试环境
            return "http://test.app.hwariot.com/user-service/api/v1/";
        } else {
            // 应用内升级使用，预发布环境
            return "http://test.app.hwariot.com/user-service/api/v1/";
        }

    }

/***************以下是具体API接口定义************************************/



    public static final String API_get_system_time = "attendance/sysdate/";

    /***************************************考勤相关*********************************************/

    //获取服务器时间

    //GET 根据日期查询考勤情况接口
    public static final String API_get_attendance_by_date = "attendance/detail/";

    //POST 打卡接口
    public static final String API_attendance = "attendance/";

    //POST 考勤组配置接口
    public static final String API_attendance_group = "attendance/group/";

    //POST 考勤组增加用户接口
    public static final String API_attendance_users = "attendance/group/users/";

    //GET 取得当前考勤组配置及当天考勤情况接口
    public static final String API_get_attendance_myinfo = "attendance/myinfo/";

    //GET 考勤报表接口
    public static final String API_attendance_report = "attendance/report/";

    //Get 获取用户详情
    public static final String API_get_user_info = "user/detail/";

    //Get 获取用户信息详情
    public static final String API_get_user_info_by_user_id = "user/detail/{userId}/";



    /***************************************通讯录相关*********************************************/

    //获取组织架构
    public static final String API_get_department_company = "department/companys/";

    //获取全部通讯录通过departmentId
    public static final String API_get_all_address_list = "department/users/";

    //获取全部分组通讯录 通过departmentId
    public static final String API_get_group_address_list = "department/usergroup/";

    /***************************************审批相关*********************************************/

    //我的审批
    public static final String API_get_my_approval = "workflow/approving/";

    //审批流通过
    public static final String API_approve_agree = "workflow/approve/";

    //抄送我的设为已读
    public static final String API_set_unread_read = "workflow/cc/read/";

    //我发起的申请列表
    public static final String API_my_request = "workflow/my/";

    //抄送我的列表
    public static final String API_copy_me = "workflow/cc/";

    //我的审批人列表
    public static final String API_get_my_approvers = "workflow/approvers/";


    //获取申请单详情
    public static final String API_get_order_detail = "workflow/details/";

    //审批驳回
    public static final String API_workflow_reject = "workflow/reject/";

    //发起申请
    public static final String API_start_a_flow_apply = "workflow/";

    //我已审批未审批数目
    public static final String API_get_approve_count = "workflow/approving/count/";

    //抄送我的已读未读数目
    public static final String API_get_copy_me_read_count = "workflow/cc/count/";

    //我发起的中待审批和已审批数量
    public static final String API_get_my_request_count = "workflow/my/count/";



    //登录
    public static final String API_login_in = "userlogin/login/";

    //获取验证码
    public static final String API_login_get_code = "userlogin/sms/";

    //获取公钥
    public static final String API_get_public_key = "userlogin/keypair/";

}
