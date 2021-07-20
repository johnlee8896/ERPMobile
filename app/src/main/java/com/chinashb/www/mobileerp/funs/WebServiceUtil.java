package com.chinashb.www.mobileerp.funs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.JUser;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.PlanInnerDetailEntity;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.BUItemBean;
import com.chinashb.www.mobileerp.bean.DepartmentBean;
import com.chinashb.www.mobileerp.bean.PanDianItemBean;
import com.chinashb.www.mobileerp.bean.ResearchItemBean;
import com.chinashb.www.mobileerp.bean.StockPermittedBean;
import com.chinashb.www.mobileerp.bean.entity.WCSubProductItemEntity;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.DeviceUtil;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.chinashb.www.mobileerp.funs.CommonUtil.isNothing2String;

//这里的T只是为了getcommonbean方法使用
public class WebServiceUtil {

    public static final String IP = "http://116.236.16.218";
    //    public static final Class<BUItemBean> bu = BUItemBean.class;
    public static String Current_Net_Link = "Intranet";
    private static String NAMESPACE = "http://tempuri.org/";
//    private static String URL = "http://172.16.1.80:8100/Test_Wss/Service.svc";
//        private static String URL = "http://116.236.97.186:8001/Service.svc";

    //如果21本地和server切换，只需要改下面四行
//    private static String URL = IP + ":8001/Service.svc";
//    private static String URL_Internet = IP + ":8001/Service.svc";

    //暂时用本地的，但不太可用
//    private static String URL = IP + ":8002/Service.svc";
//    private static String URL_Internet = IP + ":8002/Service.svc";

    private static String URL = IP + ":8188/Test_Wss/Service.svc";
    private static String URL_Internet = IP + ":8188/Test_Wss/Service.svc";


    private static String URL_QueryWage = IP + ":8188/WageQueryWeb/Service.svc";
    private static String URL_Internet_QueryWage = IP + ":8188/WageQueryWeb/Service.svc";

    private static String URL_Intranet = "http://172.16.1.80:8100/Test_Wss/Service.svc";
    private static String URL_Intranet_Internet_QueryWage = "http://172.16.1.80:8100/WageQueryWeb/Service.svc";


//    private static String URL_Internet = "http://180.167.56.250:8100/Test_Wss/Service.svc";


    //    private static String URL = "http://172.16.1.80:8100/Test_Wss/Service.svc";
//    private static String URL = "http://180.167.56.250:8100/Test_Wss/Service.svc";
    //    private static String URL = "http://172.16.1.43:8100/Test_Wss/Service.svc";
//    private static String URL_Intranet = "http://172.16.1.80:8188/Test_Wss/Service.svc";
//    private static String URL_Intranet = "http://180.167.56.250:8100/Test_Wss/Service.svc";

    //        private static String URL_Intranet = IP + ":8001/Service.svc";
//        private static String URL_Intranet = "http://172.16.1.43:8100/Test_Wss/Service.svc";
    //// TODO: 2019/12/2 切换到迦勒那边时要 加上 test_wss
//    private static String URL_Intranet = "http://172.16.1.43:8001/Service.svc";//测试内网连接


    //    private static String URL_Internet = "http://180.167.56.250:8100/Test_Wss/Service.svc";


    private static String SOAP_ACTION = "http://tempuri.org/IService/";
    private static String SOAP_ACTION2 = "http://tempuri.org/IService2/";

    private static String key = "Money_For_GodMoneyForGod";

    //private int

    public static void set_net_link_to_intranet() {
        URL = URL_Intranet;
        Current_Net_Link = "Intranet";
    }

    public static void set_net_link_to_internet() {
        URL = URL_Internet;
        Current_Net_Link = "Internet";
    }

    //                op_Commit_Work_line_Item_Non_Plan(Bu_ID , Exer ,
//                        Item_ID , IV_ID ,
//                        LotID , LotNo ,
//                        Ist_ID , Sub_Ist_ID ,
//                        SMLI_ID , SMM_ID , SMT_ID ,
//                        Qty As Double, txtEntity , txtRecord , Remark , WC_ID )


    public static WsResult test() {
        String webMethodName = "IService2_op_Test2";
        SoapSerializationEnvelope envelope = invokeSupplierWS(new ArrayList<PropertyInfo>(), webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }
        return null;
    }


    public static WsResult getAnotherTest(String productCode) {
        String webMethodName = "getAnotherTest";

        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("productCode");
        propertyInfo1.setValue(productCode);
        propertyInfo1.setType(String.class);
        propertyInfos.add(propertyInfo1);

//        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
//        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapSerializationEnvelope envelope = null;
        // Create request
        SoapObject soapObject = new SoapObject(NAMESPACE, webMethodName);
        for (PropertyInfo propertyInfo : propertyInfos) {
            soapObject.addProperty(propertyInfo);
        }

        // Create envelope
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.bodyOut = soapObject;

        // Set output SOAP object
        envelope.setOutputSoapObject(soapObject);
        new MarshalDate().register(envelope);

        // Create HTTP call object


        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug = true;
        try {
            // Invole web service
            androidHttpTransport.call(SOAP_ACTION2 + webMethodName, envelope);
            // Get the response
            envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
//			resTxt = "Error occured";
//            ToastUtil.showToastLong(e.getMessage());
        }
//        return envelope;
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }
        return null;
    }

//    queryWage(HR_NO As String, queryMonth As String)   phoneName As String, phoneMac As String
    public static WsResult queryWageCount(String HR_NO,String queryMonth){
        String webMethodName = "queryWageCount";
        ArrayList<PropertyInfo> propertyInfoList = new ArrayList<>();
        AddPropertyInfo(propertyInfoList,"HR_NO",HR_NO);
        AddPropertyInfo(propertyInfoList,"queryMonth",queryMonth);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }

        return null;
    }

    //    queryWage(HR_NO As String, queryMonth As String)   phoneName As String, phoneMac As String
    public static WsResult queryWageWithRecord(String HR_NO,String queryMonth,String IMEI){
        String webMethodName = "queryWageWithRecord";
        ArrayList<PropertyInfo> propertyInfoList = new ArrayList<>();
        AddPropertyInfo(propertyInfoList,"HR_NO",HR_NO);
        AddPropertyInfo(propertyInfoList,"queryMonth",queryMonth);
        AddPropertyInfo(propertyInfoList,"phoneName","Android");
        AddPropertyInfo(propertyInfoList,"phoneMac", IMEI );
//        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);
        SoapSerializationEnvelope envelope = invokeSupplierWS_QueryWage(propertyInfoList, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }

        return null;
    }
    //根据标题，内容，创建简易任务
//    commitSingleTaskFromMobile(HR_ID As Integer, HR_Name As String, executor As Integer, executorName As String, executor_Dep_ID As Integer, taskEndDate As Date, title As String, content As String)

    public static WsResult commitSingleTaskFromMobile(int HR_ID , String HR_Name ,int executor , String executorName ,int executor_Dep_ID ,Date  taskEndDate ,String title, String content ){
        String webMethodName = "commitSingleTaskFromMobile";
        ArrayList<PropertyInfo> propertyInfoList = new ArrayList<>();
        AddPropertyInfo(propertyInfoList, "HR_ID", HR_ID);
//        AddPropertyInfo(propertyInfos, "HR_ID", UserSingleton.get().getHRID());
//        AddPropertyInfo(propertyInfos, "HR_Name", UserSingleton.get().getHRName());
        AddPropertyInfo(propertyInfoList, "HR_Name", HR_Name);
        AddPropertyInfo(propertyInfoList, "executor", executor);
        AddPropertyInfo(propertyInfoList, "executorName", executorName);
        AddPropertyInfo(propertyInfoList, "executor_Dep_ID", executor_Dep_ID);
        AddPropertyInfo(propertyInfoList, "taskEndDate", taskEndDate);
        AddPropertyInfo(propertyInfoList, "title", title);
        AddPropertyInfo(propertyInfoList, "content", content);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }

        return null;
    }
    public static WsResult op_Commit_Work_line_Item_Non_Plan(
            long Item_ID, long IV_ID,
            long LotID, String LotNo,
            long Ist_ID, long Sub_Ist_ID,
            long SMLI_ID, long SMM_ID, long SMT_ID,
            String Qty, String txtEntity, String txtRecord, String Remark, long WC_ID) {

        String webMethodName = "op_Commit_Work_line_Item_Non_Plan";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("Bu_ID");
        propertyInfo1.setValue(UserSingleton.get().getUserInfo().getBu_ID());
        propertyInfo1.setType(Integer.class);
        propertyInfos.add(propertyInfo1);
        System.out.println("================================Bu_ID = " + UserSingleton.get().getUserInfo().getBu_ID());

        //// TODO: 2019/12/31 indate
        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Exer");
        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo2.setType(Long.class);
        propertyInfos.add(propertyInfo2);
        System.out.println("================================Exer = " + UserSingleton.get().getHRID());

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("Item_ID");
        propertyInfo3.setValue(Item_ID);
        propertyInfo3.setType(Long.class);
        propertyInfos.add(propertyInfo3);

        System.out.println("================================Item_ID = " + Item_ID);

        //// TODO: 2019/12/31 wc_id  ?
        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName("IV_ID");
        propertyInfo4.setValue(IV_ID);
        propertyInfo4.setType(Long.class);
        propertyInfos.add(propertyInfo4);
        System.out.println("================================IV_ID = " + IV_ID);

        PropertyInfo propertyInfo5 = new PropertyInfo();
        propertyInfo5.setName("LotID");
        propertyInfo5.setValue(LotID);
        propertyInfo5.setType(Long.class);
        propertyInfos.add(propertyInfo5);
        System.out.println("================================LotID = " + LotID);

        PropertyInfo propertyInfo6 = new PropertyInfo();
        propertyInfo6.setName("LotNo");
        propertyInfo6.setValue(LotNo);
        propertyInfo6.setType(String.class);
        propertyInfos.add(propertyInfo6);
        System.out.println("================================LotNo = " + LotNo);
//
        PropertyInfo propertyInfo7 = new PropertyInfo();
        propertyInfo7.setName("Ist_ID");
        propertyInfo7.setValue(Ist_ID);
        propertyInfo7.setType(Long.class);
        propertyInfos.add(propertyInfo7);
        System.out.println("================================Ist_ID = " + Ist_ID);
//
        PropertyInfo propertyInfo8 = new PropertyInfo();
        propertyInfo8.setName("Sub_Ist_ID");
        propertyInfo8.setValue(Sub_Ist_ID);
        propertyInfo8.setType(Long.class);
        propertyInfos.add(propertyInfo8);
        System.out.println("================================Sub_Ist_ID = " + Sub_Ist_ID);


//        long LotID, String LotNo,
//        long Ist_ID, long Sub_Ist_ID,
//        long SMLI_ID, long SMM_ID, long SMT_ID,
//        double Qty, String txtEntity, String txtRecord, String Remark, int WC_ID
        PropertyInfo propertyInfo9 = new PropertyInfo();
        propertyInfo9.setName("SMLI_ID");
        propertyInfo9.setValue(SMLI_ID);
        propertyInfo9.setType(Long.class);
        propertyInfos.add(propertyInfo9);
        System.out.println("================================SMLI_ID = " + SMLI_ID);
//
        PropertyInfo propertyInfo10 = new PropertyInfo();
        propertyInfo10.setName("SMM_ID");
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setValue(Double.valueOf(subProductItemEntity.getQty()));
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setType(Double.class);
//        propertyInfo10.setType(Float.class);
        propertyInfo10.setValue(SMM_ID);
        propertyInfo10.setType(Long.class);
        propertyInfos.add(propertyInfo10);
        System.out.println("================================SMM_ID = " + SMM_ID);

//        ManuLotNo , ManuDate As Date,
        // Ist_ID , Remark , Recorder , RecorderName
        PropertyInfo propertyInfo11 = new PropertyInfo();
        propertyInfo11.setName("SMT_ID");
        propertyInfo11.setValue(SMT_ID);
        propertyInfo11.setType(Long.class);
        propertyInfos.add(propertyInfo11);
        System.out.println("================================SMT_ID = " + SMT_ID);


        PropertyInfo propertyInfo16 = new PropertyInfo();
        propertyInfo16.setName("Qty");
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setValue(Double.valueOf(subProductItemEntity.getQty()));
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setType(Double.class);
//        propertyInfo10.setType(Float.class);
        propertyInfo16.setValue(Qty);
//        propertyInfo16.setType(Integer.class);
        propertyInfo16.setType(String.class);
        propertyInfos.add(propertyInfo16);
        System.out.println("================================InQty = " + Qty);

//        //// TODO: 2019/12/31 manuDate
        PropertyInfo propertyInfo12 = new PropertyInfo();
        propertyInfo12.setName("txtEntity");
        propertyInfo12.setValue(txtEntity);
        propertyInfo12.setType(String.class);
        propertyInfos.add(propertyInfo12);
        System.out.println("================================txtEntity = " + txtEntity);

        PropertyInfo propertyInfo13 = new PropertyInfo();
        propertyInfo13.setName("txtRecord");
        propertyInfo13.setValue(txtRecord);
        propertyInfo13.setType(String.class);
        propertyInfos.add(propertyInfo13);
        System.out.println("================================txtRecord = " + txtRecord);

        PropertyInfo propertyInfo14 = new PropertyInfo();
        propertyInfo14.setName("Remark");
        propertyInfo14.setValue(Remark);
        propertyInfo14.setType(String.class);
        propertyInfos.add(propertyInfo14);
        System.out.println("================================Remark = " + Remark);

        PropertyInfo propertyInfo15 = new PropertyInfo();
        propertyInfo15.setName("WC_ID");
        propertyInfo15.setValue(WC_ID);
        propertyInfo15.setType(Long.class);
        propertyInfos.add(propertyInfo15);
        System.out.println("================================WC_ID = " + WC_ID);

        PropertyInfo propertyInfo17 = new PropertyInfo();
        propertyInfo17.setName("hrID");
        propertyInfo17.setValue(UserSingleton.get().getHRID());
        propertyInfo17.setType(Integer.class);
        propertyInfos.add(propertyInfo17);
        System.out.println("================================hrID = " + UserSingleton.get().getHRID());


        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }

        return null;

    }

    public static WsResult op_Check_Work_Line_Scan_Item_Barcode_Json(String scanResult) {

        String webMethodName = "op_Check_Work_Line_Scan_Item_Barcode_Json";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
//        PropertyInfo propertyInfo = new PropertyInfo();
//        propertyInfo.setName("Bu_ID");
//        propertyInfo.setValue(UserSingleton.get().getUserInfo().getBu_ID());
//        propertyInfo.setType(Integer.class);
//        propertyInfos.add(propertyInfo);
//
//        PropertyInfo propertyInfo2 = new PropertyInfo();
//        propertyInfo2.setName("X");
//        propertyInfo2.setValue(scanResult);
//        propertyInfo2.setType(String.class);
//        propertyInfos.add(propertyInfo2);

        AddPropertyInfo(propertyInfos, "Bu_ID", UserSingleton.get().getUserInfo().getBu_ID());
        AddPropertyInfo(propertyInfos, "X", scanResult);


//        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
//        SoapObject obj = (SoapObject) envelope.bodyIn;
//        WsResult ws_result = Get_WS_Result(obj);
//        return ws_result;

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult result = getWS_Result(obj);
//     return result ;

//        System.out.println("***************************** obj = " + obj);
//        BoxItemEntity box_item = new BoxItemEntity();
//        if (obj != null) {
//            int count = obj.getPropertyCount();
//            SoapObject obj2;
//            for (int i = 0; i < count; i++) {
//                obj2 = (SoapObject) obj.getProperty(i);
//                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
//                if (!box_item.getResult()) {
//                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
//                } else {
//                    fill_box_item(box_item, obj2);
//                }
//            }
//        }

//
//        BoxItemEntity box_item = new BoxItemEntity();
//        if (obj != null) {
//            int count = obj.getPropertyCount();
//            SoapObject obj2;
//            for (int i = 0; i < count; i++) {
//                obj2 = (SoapObject) obj.getProperty(i);
//                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
//                if (!box_item.getResult()) {
//                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
//                } else {
//                    box_item.setDIII_ID(Long.parseLong(obj2.getProperty("DIII_ID").toString()));
//                    box_item.setSMT_ID(Long.parseLong(obj2.getProperty("SMT_ID").toString()));
//                    box_item.setSMM_ID(Long.parseLong(obj2.getProperty("SMM_ID").toString()));
//                    box_item.setSMLI_ID(Long.parseLong(obj2.getProperty("SMLI_ID").toString()));
//                    box_item.setEntityID(Long.parseLong(obj2.getProperty("EntityID").toString()));
//                    box_item.setEntityName(obj2.getProperty("EntityName").toString());
//                    box_item.setLotID(Long.parseLong(obj2.getProperty("LotID").toString()));
//                    //// TODO: 2019/11/27 这里的lotNo 取的是DII里面的manulotNo
//                    box_item.setLotNo(obj2.getProperty("LotNo").toString());
//                    box_item.setItem_ID(Long.parseLong(obj2.getProperty("Item_ID").toString()));
//                    box_item.setIV_ID(Long.parseLong(obj2.getProperty("IV_ID").toString()));
//                    box_item.setItemName(obj2.getProperty("ItemName").toString());
//                    box_item.setQty(Float.parseFloat(obj2.getProperty("Qty").toString()));
//                    box_item.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
//                    box_item.setBuName(obj2.getProperty("BuName").toString());
//                    box_item.setBoxName(obj2.getProperty("BoxName").toString());
//                    box_item.setBoxNo(obj2.getProperty("BoxNo").toString());
//
//                }
//
//                //int DataMemberCount = obj2.getPropertyCount();
//                //for (int j=0; j<DataMemberCount;j++){
//                //result.add(Boolean.parseBoolean(obj2.getProperty(j).toString()));
//                //}
//            }
//
//        }
//        return box_item;
        return result;

    }

    public static BoxItemEntity op_Check_Work_Line_Scan_Item_Barcode(String scanResult) {
        String webMethodName = "op_Check_Work_Line_Scan_Item_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
//        propertyInfo.setName("Bu_ID");
//        propertyInfo.setValue(UserSingleton.get().getUserInfo().getBu_ID());
//        propertyInfo.setType(Integer.class);
//        propertyInfos.add(propertyInfo);
//
//        PropertyInfo propertyInfo2 = new PropertyInfo();
//        propertyInfo2.setName("X");
//        propertyInfo2.setValue(scanResult);
//        propertyInfo2.setType(String.class);
//        propertyInfos.add(propertyInfo2);

        AddPropertyInfo(propertyInfos, "Bu_ID", UserSingleton.get().getUserInfo().getBu_ID());
        AddPropertyInfo(propertyInfos, "X", scanResult);


//        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
//        SoapObject obj = (SoapObject) envelope.bodyIn;
//        WsResult ws_result = Get_WS_Result(obj);
//        return ws_result;

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        BoxItemEntity box_item = new BoxItemEntity();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    fill_box_item(box_item, obj2);
                }
            }
        }

//
//        BoxItemEntity box_item = new BoxItemEntity();
//        if (obj != null) {
//            int count = obj.getPropertyCount();
//            SoapObject obj2;
//            for (int i = 0; i < count; i++) {
//                obj2 = (SoapObject) obj.getProperty(i);
//                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
//                if (!box_item.getResult()) {
//                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
//                } else {
//                    box_item.setDIII_ID(Long.parseLong(obj2.getProperty("DIII_ID").toString()));
//                    box_item.setSMT_ID(Long.parseLong(obj2.getProperty("SMT_ID").toString()));
//                    box_item.setSMM_ID(Long.parseLong(obj2.getProperty("SMM_ID").toString()));
//                    box_item.setSMLI_ID(Long.parseLong(obj2.getProperty("SMLI_ID").toString()));
//                    box_item.setEntityID(Long.parseLong(obj2.getProperty("EntityID").toString()));
//                    box_item.setEntityName(obj2.getProperty("EntityName").toString());
//                    box_item.setLotID(Long.parseLong(obj2.getProperty("LotID").toString()));
//                    //// TODO: 2019/11/27 这里的lotNo 取的是DII里面的manulotNo
//                    box_item.setLotNo(obj2.getProperty("LotNo").toString());
//                    box_item.setItem_ID(Long.parseLong(obj2.getProperty("Item_ID").toString()));
//                    box_item.setIV_ID(Long.parseLong(obj2.getProperty("IV_ID").toString()));
//                    box_item.setItemName(obj2.getProperty("ItemName").toString());
//                    box_item.setQty(Float.parseFloat(obj2.getProperty("Qty").toString()));
//                    box_item.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
//                    box_item.setBuName(obj2.getProperty("BuName").toString());
//                    box_item.setBoxName(obj2.getProperty("BoxName").toString());
//                    box_item.setBoxNo(obj2.getProperty("BoxNo").toString());
//
//                }
//
//                //int DataMemberCount = obj2.getPropertyCount();
//                //for (int j=0; j<DataMemberCount;j++){
//                //result.add(Boolean.parseBoolean(obj2.getProperty(j).toString()));
//                //}
//            }
//
//        }
        return box_item;

    }

    private static SoapSerializationEnvelope invokeSupplierWS(ArrayList<PropertyInfo> propertyInfoList, String webMethodName) {
        SoapSerializationEnvelope envelope = null;
        // Create request
        SoapObject soapObject = new SoapObject(NAMESPACE, webMethodName);
        for (PropertyInfo propertyInfo : propertyInfoList) {
            soapObject.addProperty(propertyInfo);
        }

        // Create envelope
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.bodyOut = soapObject;

        // Set output SOAP object
        envelope.setOutputSoapObject(soapObject);
        new MarshalDate().register(envelope);

        // Create HTTP call object
//        Current_Net_Link = "Intranet";
        if (UserSingleton.get().isCurrentInnerNetLink() || TextUtils.equals(Current_Net_Link,"Intranet")){
            URL = "http://172.16.1.80:8100/Test_Wss/Service.svc";
        }
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug = true;
        try {
            // Invole web service
            androidHttpTransport.call(SOAP_ACTION + webMethodName, envelope);
            // Get the response
            envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
//			resTxt = "Error occured";
//            ToastUtil.showToastLong(e.getMessage());
        }
        return envelope;
    }


   //专门给工资查询使用，避免因为切换服务器而产生各样问题
    private static SoapSerializationEnvelope invokeSupplierWS_QueryWage(ArrayList<PropertyInfo> propertyInfoList, String webMethodName) {
        SoapSerializationEnvelope envelope = null;
        // Create request
        SoapObject soapObject = new SoapObject(NAMESPACE, webMethodName);
        for (PropertyInfo propertyInfo : propertyInfoList) {
            soapObject.addProperty(propertyInfo);
        }

        // Create envelope
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.bodyOut = soapObject;

        // Set output SOAP object
        envelope.setOutputSoapObject(soapObject);
        new MarshalDate().register(envelope);

        // Create HTTP call object
//        Current_Net_Link = "Intranet";
        if (UserSingleton.get().isCurrentInnerNetLink() || TextUtils.equals(Current_Net_Link,"Intranet")){
            // TODO: 2021/4/22  
            URL = "http://172.16.1.80:8100/WageQueryWeb/Service.svc";
        }
//        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_QueryWage );
        androidHttpTransport.debug = true;
        try {
            // Invole web service
            androidHttpTransport.call(SOAP_ACTION + webMethodName, envelope);
            // Get the response
            envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
//			resTxt = "Error occured";
//            ToastUtil.showToastLong(e.getMessage());
        }
        return envelope;
    }

    private static SoapSerializationEnvelope tempinvokeSupplierWS(ArrayList<PropertyInfo> propertyInfoList, String webMethName, String url) {
        SoapSerializationEnvelope envelope = null;
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        for (PropertyInfo propertyInfo : propertyInfoList) {
            request.addProperty(propertyInfo);
        }

        // Create envelope
        envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.bodyOut = request;

        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        new MarshalDate().register(envelope);

        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.debug = true;

        try {
            // Invole web service todo
            androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
//            androidHttpTransport.call("http://tempuri.org/ReadApkVersion" + webMethName, envelope);
//            androidHttpTransport.call("http://tempuri.org/" + webMethName, envelope);
            // Get the response
            envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
//			resTxt = "Error occured";
        }

        return envelope;
    }

    public static String getHRName(int id) {
        String result = null;
        String webMethodName = "op_Get_HR_Name";
        ArrayList<PropertyInfo> propertyInfoList = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("HR_ID");
        propertyInfo.setValue(id);
        propertyInfo.setType(Integer.class);
        propertyInfoList.add(propertyInfo);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);

        SoapPrimitive response = null;
        try {
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            result = response.toString();
        }

        return result;
    }


    public static UserInfoEntity getHRName_Bu(int id) {
        String result = null;
        String webMethodName = "op_Get_HR_Name_And_Bu";
        ArrayList<PropertyInfo> propertyInfoList = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("HR_ID");
        propertyInfo.setValue(id);
        propertyInfo.setType(Integer.class);
        propertyInfoList.add(propertyInfo);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);
        SoapPrimitive response = null;
        UserInfoEntity userInfoEntity = null;
        try {
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            result = response.toString();
            Gson gson = new Gson();
            ArrayList<JUser> juseList = new ArrayList<JUser>();
            juseList = gson.fromJson(result, new TypeToken<List<JUser>>() {
            }.getType());
            if (juseList != null) {
                if (juseList.size() >= 1) {
                    //Object o = us.get(0);
                    JUser j = juseList.get(0);
                    userInfoEntity = new UserInfoEntity();
                    userInfoEntity.setHR_ID(j.getHR_ID());
                    userInfoEntity.setHR_Name(j.getHR_Name());
                    userInfoEntity.setBu_ID(j.getBu_ID());
                    userInfoEntity.setBu_Name(j.getBu_Name());
                    userInfoEntity.setCompany_ID(j.getCompany_ID());
                    userInfoEntity.setCompany_Name(j.getCompany_Name());
                }
            }
        }
        return userInfoEntity;
    }

    public static WsResult commitFoodOrderForSmallCanteen(int id, Date foodDate, int commitType) {
//        boolean result = false ;
        String webMethodName = "op_Commit_Food_Order_For_Small_Canteen";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("HR_ID");
        propertyInfo.setValue(id);
        propertyInfo.setType(Integer.class);

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("foodDate");
        propertyInfo1.setValue(foodDate);
        propertyInfo1.setType(Date.class);

        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("commitType");
        propertyInfo2.setValue(commitType);
        propertyInfo2.setType(PropertyInfo.INTEGER_CLASS);

        propertyInfos.add(propertyInfo);
        propertyInfos.add(propertyInfo1);
        propertyInfos.add(propertyInfo2);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        WsResult result = new WsResult();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2 = (SoapObject) obj.getProperty(0);
            result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
            result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());

        }

        return result;
    }

    public static boolean getFoodOrder(int id, Date date, int whichFood) {
        boolean result = false;
        String webMethodName = "op_Get_Food_Order";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("HR_ID");
        propertyInfo.setValue(id);
        propertyInfo.setType(Integer.class);

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("FO_Date");
        propertyInfo1.setValue(date);
        propertyInfo1.setType(Date.class);

        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Which_Food");
        propertyInfo2.setValue(whichFood);
        propertyInfo2.setType(Integer.class);

        propertyInfos.add(propertyInfo);
        propertyInfos.add(propertyInfo1);
        propertyInfos.add(propertyInfo2);


        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapPrimitive response = null;
        try {
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            result = Boolean.parseBoolean(response.toString());
        }

        return result;
    }

    public static ArrayList<Boolean> getFoodOrderDay(int id, Date date) {
        ArrayList<Boolean> result = new ArrayList<>();
        String webMethodName = "op_Get_Food_Order_Day";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("HR_ID");
        propertyInfo.setValue(id);
        propertyInfo.setType(Integer.class);

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("FO_Date");
        propertyInfo1.setValue(date);
        propertyInfo1.setType(Date.class);

        propertyInfos.add(propertyInfo);
        propertyInfos.add(propertyInfo1);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                for (int j = 0; j < obj2.getPropertyCount(); j++) {
                    result.add(Boolean.parseBoolean(obj2.getProperty(j).toString()));
                }
            }

        }

        return result;
    }

    public static boolean commitFoodOrder(int id, Date date, int whichFood, boolean eat) {
        boolean result = false;
        String webMethodName = "op_Commit_Food_Order";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("HR_ID");
        propertyInfo.setValue(id);
        propertyInfo.setType(Integer.class);

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("FO_Date");
        propertyInfo1.setValue(date);
        propertyInfo1.setType(Date.class);

        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Which_Food");
        propertyInfo2.setValue(whichFood);
        propertyInfo2.setType(Integer.class);

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("Eat");
        propertyInfo3.setValue(eat);
        propertyInfo3.setType(Boolean.class);

        propertyInfos.add(propertyInfo);
        propertyInfos.add(propertyInfo1);
        propertyInfos.add(propertyInfo2);
        propertyInfos.add(propertyInfo3);

        propertyInfos.add(propertyInfo);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapPrimitive response = null;
        try {
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            result = Boolean.parseBoolean(response.toString());
        }

        return result;
    }

    public static boolean commitFoodOrderDay(int id, Date date, String eat) {
        boolean result = false;
        String webMethodName = "op_Commit_Food_Order_Day_String";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("HR_ID");
        propertyInfo.setValue(id);
        propertyInfo.setType(Integer.class);

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("FO_Date");
        propertyInfo1.setValue(date);
        propertyInfo1.setType(Date.class);

        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Eat");
        propertyInfo2.setValue(eat);
        propertyInfo2.setType(PropertyInfo.STRING_CLASS);

        propertyInfos.add(propertyInfo);
        propertyInfos.add(propertyInfo1);
        propertyInfos.add(propertyInfo2);


        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        if (obj != null) {
            result = Boolean.parseBoolean(obj.toString());
        }

        return result;
    }

    //todo几点疑问  只有get，没有post,delete吗？

    //扫描入库，扫描物料码，来料码
    public static BoxItemEntity op_Check_Commit_DS_Item_Income_Barcode(String X) {
        String webMethodName = "op_Check_Commit_DS_Item_Income_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("X");
        propertyInfo.setValue(X);
        propertyInfo.setType(String.class);
        propertyInfos.add(propertyInfo);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        BoxItemEntity box_item = new BoxItemEntity();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                   try{
                       box_item.setDIII_ID(Long.parseLong(obj2.getProperty("DIII_ID").toString()));
                       box_item.setSMT_ID(Long.parseLong(obj2.getProperty("SMT_ID").toString()));
                       box_item.setSMM_ID(Long.parseLong(obj2.getProperty("SMM_ID").toString()));
                       box_item.setSMLI_ID(Long.parseLong(obj2.getProperty("SMLI_ID").toString()));
                       box_item.setEntityID(Long.parseLong(obj2.getProperty("EntityID").toString()));
                       if (obj2.getProperty("EntityName") != null){
                           box_item.setEntityName(obj2.getProperty("EntityName").toString());
                       }

                       box_item.setLotID(Long.parseLong(obj2.getProperty("LotID").toString()));
                       //// TODO: 2019/11/27 这里的lotNo 取的是DII里面的manulotNo
                       if (obj2.getProperty("LotNo") != null) {
                           box_item.setLotNo(obj2.getProperty("LotNo").toString());
                       }
                       box_item.setItem_ID(Long.parseLong(obj2.getProperty("Item_ID").toString()));
                       box_item.setIV_ID(Long.parseLong(obj2.getProperty("IV_ID").toString()));
                       box_item.setItemName(obj2.getProperty("ItemName").toString());
                       box_item.setQty(Float.parseFloat(obj2.getProperty("Qty").toString()));
                       box_item.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
                       //// TODO: 2020/4/22 这里先去掉，会报illegal property
//                    obj2.getPropertySafely()
//                    if (obj2.getProperty("Company_ID") != null) {
//                        box_item.setCompany_ID(Integer.parseInt(obj2.getProperty("Company_ID").toString()));
//                    }
                       Object tempObject = obj2.getPropertySafely("Company_ID");
                       if (tempObject != null && !TextUtils.isEmpty(tempObject.toString()) && !TextUtils.equals(tempObject.toString(),"null")) {
                           box_item.setCompany_ID(Integer.parseInt(obj2.getProperty("Company_ID").toString()));
                       }
                       box_item.setBuName(obj2.getProperty("BuName").toString());
                       box_item.setBoxName(obj2.getProperty("BoxName").toString());
                       box_item.setBoxNo(obj2.getProperty("BoxNo").toString());
                       box_item.setIstName(obj2.getProperty("IstName").toString());
                   }catch(Exception e){
                       e.printStackTrace();
                   }

                }

                //int DataMemberCount = obj2.getPropertyCount();
                //for (int j=0; j<DataMemberCount;j++){
                //result.add(Boolean.parseBoolean(obj2.getProperty(j).toString()));
                //}
            }

        }

        return box_item;
    }

    //扫描入库，扫描物料码，来料码
    public static BoxItemEntity op_Check_Commit_Product_Income_Barcode(String X) {
        String webMethodName = "op_Check_Commit_Product_Income_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("X");
        propertyInfo.setValue(X);
        propertyInfo.setType(String.class);
        propertyInfos.add(propertyInfo);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        BoxItemEntity box_item = new BoxItemEntity();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    box_item.setDIII_ID(Long.parseLong(obj2.getProperty("DIII_ID").toString()));
                    box_item.setSMT_ID(Long.parseLong(obj2.getProperty("SMT_ID").toString()));
                    box_item.setSMM_ID(Long.parseLong(obj2.getProperty("SMM_ID").toString()));
                    box_item.setSMLI_ID(Long.parseLong(obj2.getProperty("SMLI_ID").toString()));
                    box_item.setEntityID(Long.parseLong(obj2.getProperty("EntityID").toString()));
                    box_item.setEntityName(obj2.getProperty("EntityName").toString());
                    box_item.setLotID(Long.parseLong(obj2.getProperty("LotID").toString()));
                    //// TODO: 2019/11/27 这里的lotNo 取的是DII里面的manulotNo
                    box_item.setLotNo(obj2.getProperty("LotNo").toString());
                    box_item.setItem_ID(Long.parseLong(obj2.getProperty("Item_ID").toString()));
                    box_item.setIV_ID(Long.parseLong(obj2.getProperty("IV_ID").toString()));
                    box_item.setItemName(obj2.getProperty("ItemName").toString());
                    box_item.setQty(Float.parseFloat(obj2.getProperty("Qty").toString()));
                    box_item.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
                    box_item.setBuName(obj2.getProperty("BuName").toString());
                    box_item.setBoxName(obj2.getProperty("BoxName").toString());
                    box_item.setBoxNo(obj2.getProperty("BoxNo").toString());

                }

                //int DataMemberCount = obj2.getPropertyCount();
                //for (int j=0; j<DataMemberCount;j++){
                //result.add(Boolean.parseBoolean(obj2.getProperty(j).toString()));
                //}
            }

        }

        return box_item;
    }

    //解析获取库位
    //VB/MT/579807/S/3506/IV/38574/P/T17-1130-1 A0/D/20190619/L/19061903/N/49/Q/114
    public static IstPlaceEntity op_Check_Commit_IST_Barcode(String scanContent) {
        String webMethodName = "op_Check_Commit_IST_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("X");
        propertyInfo.setValue(scanContent);
        propertyInfo.setType(String.class);
        propertyInfos.add(propertyInfo);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        IstPlaceEntity istPlace = new IstPlaceEntity();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                istPlace.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!istPlace.getResult()) {
                    istPlace.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    istPlace.setIst_ID(Long.parseLong(obj2.getProperty("Ist_ID").toString()));
                    istPlace.setSub_Ist_ID(Long.parseLong(obj2.getProperty("Sub_Ist_ID").toString()));
                    //// TODO: 2019/12/3 IST_Name 是大库对应的数据库字段名，Ist_Name对应的是小区域的？
                    istPlace.setIstName(obj2.getProperty("IstName").toString());
                    istPlace.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
                    istPlace.setBuName(obj2.getProperty("BuName").toString());
                }
            }
        }
        return istPlace;
    }

    //将物料入相应的库,零部件账
    public static WsResult op_Commit_DS_Item_Income_To_Warehouse(BoxItemEntity box_item) {
        String webMethodName = "op_Commit_DS_Item_Income_To_Warehouse";
        System.out.println("=======================================  op_Commit_DS_Item_Income_To_Warehouse");
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("SenderID");
//        propertyInfo.setValue(UserInfoEntity.ID);
        propertyInfo.setValue(UserSingleton.get().getHRID());
        propertyInfo.setType(Integer.class);
        propertyInfos.add(propertyInfo);

        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("DIII_ID");
        propertyInfo2.setValue(box_item.getDIII_ID());
        System.out.println("======================================= diii_id =  " + box_item.getDIII_ID());
        propertyInfo2.setType(Long.class);
        propertyInfos.add(propertyInfo2);

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("Ist_ID");
        propertyInfo3.setValue(box_item.getIst_ID());
        System.out.println("======================================= Ist_ID =  " + box_item.getIst_ID());
        propertyInfo3.setType(Long.class);
        propertyInfos.add(propertyInfo3);

        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName("Sub_Ist_ID");
        propertyInfo4.setValue(box_item.getSub_Ist_ID());
        System.out.println("======================================= Sub_Ist_ID =  " + box_item.getSub_Ist_ID());
        propertyInfo4.setType(Long.class);
        propertyInfos.add(propertyInfo4);

        //todo
//        PropertyInfo propertyInfo5 = new PropertyInfo();
//        propertyInfo4.setName("SQL");
//        propertyInfo4.setValue(sql );
//        propertyInfo4.setType(String.class);
//        propertyInfos.add(propertyInfo5);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        WsResult ws_result = Get_WS_Result(obj);
        return ws_result;

    }


    //将物料入相应的库,成品账
    //Bu_ID , InDate As Date, RecordNo , WC_ID ,
    // WC_Name , PS_ID , Product_ID , Item_ID ,
    // IV_ID , InQty As Double, ManuLotNo , ManuDate As Date,
    // Ist_ID , Remark , Recorder , RecorderName 
//    public static WsResult op_Product_Manu_In_Not_Pallet(WCSubProductItemEntity box_item,long DIII_ID,long Ist_ID,long Sub_Ist_ID) {
//        String webMethodName = "op_Product_Manu_In_Not_Pallet";
//        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
//        PropertyInfo propertyInfo = new PropertyInfo();
//        propertyInfo.setName("SenderID");
////        propertyInfo.setValue(UserInfoEntity.ID);
//        propertyInfo.setValue(UserSingleton.get().getHRID());
//        propertyInfo.setType(Integer.class);
//        propertyInfos.add(propertyInfo);
//
//        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
//        SoapObject obj = (SoapObject) envelope.bodyIn;
//        WsResult ws_result = Get_WS_Result(obj);
//        return ws_result;
//
//    }

    //Bu_ID , InDate As Date, RecordNo , WC_ID ,
    // WC_Name , PS_ID , Product_ID , Item_ID ,
    // IV_ID , InQty As Double, ManuLotNo , ManuDate As Date,
    // Ist_ID , Remark , Recorder , RecorderName 
//    public static WsResult op_Product_Manu_In_Not_Pallet(WcIdNameEntity wcIdNameEntity, WCSubProductItemEntity subProductItemEntity, Date InDate, String RecordNo,
//                                                         Date ManuDate, String Remark, int Recorder, String RecorderName,
//                                                         long Ist_ID, long Sub_Ist_ID, int qty) {

//    Public Function op_Product_Manu_In_Not_Pallet(BoxID As Long, Bu_ID As Integer, RecordNo As String, PS_ID As Long, Product_ID As Long,
//    Item_ID As Long, IV_ID As Long, InQty As Double, ManuLotNo As String, ManuDate As Date, Ist_ID As Long,
//    Remark As String, Recorder As Integer, RecorderName As String) As Tuple(Of Boolean, String)

    public static WsResult op_Product_Manu_In_Not_Pallet(int BoxID, WCSubProductItemEntity subProductItemEntity, String RecordNo,
                                                         Date ManuDate, String Remark, int Recorder, String RecorderName,
                                                         long Ist_ID, long Sub_Ist_ID, int qty) {

        String webMethodName = "op_Product_Manu_In_Not_Pallet";
//        String webMethodName = "op_Product_Manu_In_Not_Pallet_1";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();


        //=====================================================================  注意 变量的顺序也很重要
        //// TODO: 2019/12/31 indate
//        PropertyInfo propertyInfo2 = new PropertyInfo();
//        propertyInfo2.setName("InDate");
//        propertyInfo2.setValue(InDate);
//        propertyInfo2.setType(Date.class);
//        propertyInfos.add(propertyInfo2);
//        System.out.println("================================InDate = " + InDate.toString());

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("BoxID");
        propertyInfo1.setValue(BoxID);
//        propertyInfo1.setType(Long.class);
        propertyInfo1.setType(Integer.class);
        propertyInfos.add(propertyInfo1);
        System.out.println("================================BoxID = " + BoxID);

        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Bu_ID");
        propertyInfo2.setValue(UserSingleton.get().getUserInfo().getBu_ID());
        propertyInfo2.setType(Integer.class);
        propertyInfos.add(propertyInfo2);
        System.out.println("================================Bu_ID = " + UserSingleton.get().getUserInfo().getBu_ID());

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("RecordNo");
        propertyInfo3.setValue(RecordNo);
        propertyInfo3.setType(String.class);
        propertyInfos.add(propertyInfo3);

        System.out.println("================================RecordNo = " + RecordNo);

        //// TODO: 2019/12/31 wc_id  ?
//        PropertyInfo propertyInfo4 = new PropertyInfo();
//        propertyInfo4.setName("WC_ID");
//        propertyInfo4.setValue(wcIdNameEntity.getWcId());
//        propertyInfo4.setType(Long.class);
//        propertyInfos.add(propertyInfo4);
//        System.out.println("================================WC_ID = " + wcIdNameEntity.getWcId());
//
//        PropertyInfo propertyInfo5 = new PropertyInfo();
//        propertyInfo5.setName("WC_Name");
//        propertyInfo5.setValue(wcIdNameEntity.getWcName());
//        propertyInfo5.setType(String.class);
//        propertyInfos.add(propertyInfo5);
//        System.out.println("================================WC_Name = " + wcIdNameEntity.getWcName());

        PropertyInfo propertyInfo6 = new PropertyInfo();
        propertyInfo6.setName("PS_ID");
        propertyInfo6.setValue(subProductItemEntity.getWcSubProductEntity().getPsId());
        propertyInfo6.setType(Long.class);
        propertyInfos.add(propertyInfo6);
        System.out.println("================================PS_ID = " + subProductItemEntity.getWcSubProductEntity().getPsId());
//
        PropertyInfo propertyInfo7 = new PropertyInfo();
        propertyInfo7.setName("Product_ID");
        propertyInfo7.setValue(subProductItemEntity.getWcSubProductEntity().getProductId());
        propertyInfo7.setType(Long.class);
        propertyInfos.add(propertyInfo7);
        System.out.println("================================Product_ID = " + subProductItemEntity.getWcSubProductEntity().getProductId());
//
        PropertyInfo propertyInfo8 = new PropertyInfo();
        propertyInfo8.setName("Item_ID");
        propertyInfo8.setValue(subProductItemEntity.getWcSubProductEntity().getItemId());
        propertyInfo8.setType(Long.class);
        propertyInfos.add(propertyInfo8);
        System.out.println("================================Item_ID = " + subProductItemEntity.getWcSubProductEntity().getItemId());
//
        PropertyInfo propertyInfo9 = new PropertyInfo();
        propertyInfo9.setName("IV_ID");
        propertyInfo9.setValue(subProductItemEntity.getWcSubProductEntity().getIvId());
        propertyInfo9.setType(Long.class);
        propertyInfos.add(propertyInfo9);
        System.out.println("================================IV_ID = " + subProductItemEntity.getWcSubProductEntity().getIvId());
//
        PropertyInfo propertyInfo10 = new PropertyInfo();
        propertyInfo10.setName("InQty");
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setValue(Double.valueOf(subProductItemEntity.getQty()));
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setType(Double.class);
//        propertyInfo10.setType(Float.class);
        propertyInfo10.setValue(qty);
        propertyInfo10.setType(Integer.class);
        propertyInfos.add(propertyInfo10);
        System.out.println("================================InQty = " + subProductItemEntity.getQty());
        System.out.println("================================InQty = " + Double.valueOf(subProductItemEntity.getQty()));

//        ManuLotNo , ManuDate As Date,
        // Ist_ID , Remark , Recorder , RecorderName 
        PropertyInfo propertyInfo11 = new PropertyInfo();
        propertyInfo11.setName("ManuLotNo");
        propertyInfo11.setValue(subProductItemEntity.getLotNo());
        propertyInfo11.setType(String.class);
        propertyInfos.add(propertyInfo11);
        System.out.println("================================ManuLotNo = " + subProductItemEntity.getLotNo());

//        //// TODO: 2019/12/31 manuDate
        PropertyInfo propertyInfo12 = new PropertyInfo();
        propertyInfo12.setName("ManuDate");
        propertyInfo12.setValue(ManuDate);
        propertyInfo12.setType(Date.class);
        propertyInfos.add(propertyInfo12);
        System.out.println("================================ManuDate = " + ManuDate);

        PropertyInfo propertyInfo13 = new PropertyInfo();
        propertyInfo13.setName("Ist_ID");
        propertyInfo13.setValue(Ist_ID);
        propertyInfo13.setType(Long.class);
        propertyInfos.add(propertyInfo13);
        System.out.println("================================Ist_ID = " + Ist_ID);

        PropertyInfo propertyInfo14 = new PropertyInfo();
        propertyInfo14.setName("Remark");
        propertyInfo14.setValue(Remark);
        propertyInfo14.setType(String.class);
        propertyInfos.add(propertyInfo14);
        System.out.println("================================Remark = " + Remark);

        PropertyInfo propertyInfo15 = new PropertyInfo();
        propertyInfo15.setName("Recorder");
        propertyInfo15.setValue(Recorder);
        propertyInfo15.setType(Integer.class);
        propertyInfos.add(propertyInfo15);
        System.out.println("================================Recorder = " + Recorder);

        PropertyInfo propertyInfo16 = new PropertyInfo();
        propertyInfo16.setName("RecorderName");
        propertyInfo16.setValue(RecorderName);
        propertyInfo16.setType(String.class);
        propertyInfos.add(propertyInfo16);
        System.out.println("================================RecorderName = " + RecorderName);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }
        return null;

    }



    //成品扫描托盘入库  BoxID As Long, Ist_ID As Long, Remark As String, Recorder As Integer
    public static WsResult op_Product_Manu_In_Pallet(int BoxID,
                                                         long Ist_ID, String Remark) {

        String webMethodName = "op_Product_Manu_In_Pallet";
//        String webMethodName = "op_Product_Manu_In_Not_Pallet_1";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("BoxID");
        propertyInfo1.setValue(BoxID);
//        propertyInfo1.setType(Long.class);
        propertyInfo1.setType(Integer.class);
        propertyInfos.add(propertyInfo1);
        System.out.println("================================BoxID = " + BoxID);


        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Ist_ID");
        propertyInfo2.setValue(Ist_ID);
        propertyInfo2.setType(Long.class);
        propertyInfos.add(propertyInfo2);
        System.out.println("================================Ist_ID = " + Ist_ID);

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("Remark");
        propertyInfo3.setValue(Remark);
        propertyInfo3.setType(String.class);
        propertyInfos.add(propertyInfo3);
        System.out.println("================================Remark = " + Remark);

        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName("Recorder");
        propertyInfo4.setValue(UserSingleton.get().getUserInfo().getHR_ID());
        propertyInfo4.setType(Integer.class);
        propertyInfos.add(propertyInfo4);
        System.out.println("================================Recorder = " + UserSingleton.get().getUserInfo().getHR_ID());


        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }
        return null;

    }


    /*

     */



//    Function op_Pro_Pallet_Sale_Out_Mobile(DP_Order_ID As Long, Delivery_ID As Long, BoxIDList As List(Of Long), HR_ID As Integer, Bu_ID As Integer, SheetNo As String, CF_Name As String) As Tuple(Of Boolean, String)

    public static WsResult op_Pro_Pallet_Sale_Out_Mobile(long DO_ID, long Delivery_ID,ArrayList<Integer> BoxIDList,
                                                           String SheetNo,String CFName) {
        String webMethodName = "op_Pro_Pallet_Sale_Out_Mobile";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("DP_Order_ID");
        propertyInfo1.setValue(DO_ID);
        propertyInfo1.setType(Long.class);
        propertyInfos.add(propertyInfo1);
        System.out.println("================================DO_ID = " + DO_ID);

        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Delivery_ID");
        propertyInfo2.setValue(Delivery_ID);
        propertyInfo2.setType(Long.class);
        propertyInfos.add(propertyInfo2);
        System.out.println("================================Delivery_ID = " + Delivery_ID);

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("BoxIDList");
        propertyInfo3.setValue(BoxIDList);
        propertyInfo3.setType(Date.class);
        propertyInfos.add(propertyInfo3);
        System.out.println("================================BoxIDList = " + BoxIDList);


        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName("HR_ID");
        propertyInfo4.setValue(UserSingleton.get().getHRID());
        propertyInfo4.setType(Integer.class);
        propertyInfos.add(propertyInfo4);
        System.out.println("================================HR_ID = " + UserSingleton.get().getHRID());

        PropertyInfo propertyInfo5 = new PropertyInfo();
        propertyInfo5.setName("Bu_ID");
        //// TODO: 2020/3/27 车间与操作员关系？
        propertyInfo5.setValue(UserSingleton.get().getUserInfo().getBu_ID());
        propertyInfo5.setType(Integer.class);
        propertyInfos.add(propertyInfo5);
        System.out.println("================================Bu_ID = " + UserSingleton.get().getUserInfo().getBu_ID());

        PropertyInfo propertyInfo6 = new PropertyInfo();
        propertyInfo6.setName("SheetNo");
        propertyInfo6.setValue(SheetNo);
        propertyInfo6.setType(String.class);
        propertyInfos.add(propertyInfo6);
        System.out.println("================================SheetNo = " + SheetNo);


        PropertyInfo propertyInfo7 = new PropertyInfo();
        propertyInfo7.setName("CFName");
        propertyInfo7.setValue(CFName);
        propertyInfo7.setType(String.class);
        propertyInfos.add(propertyInfo7);
        System.out.println("================================CFName = " + CFName);


//       long PS_ID , String Qty

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }
        return null;
    }







    public static WsResult op_Product_Manu_Out_Not_Pallet(Date Delivery_Date, long CF_ID,
                                                          String CFName, String SheetNo,
                                                          long Delivery_ID, long DPI_ID, long DO_ID, int PS_ID, String Qty) {
        String webMethodName = "op_Product_Manu_Out_Not_Pallet";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("Bu_ID");
        //// TODO: 2020/3/27 车间与操作员关系？
        propertyInfo1.setValue(UserSingleton.get().getUserInfo().getBu_ID());
        propertyInfo1.setType(Integer.class);
        propertyInfos.add(propertyInfo1);
        System.out.println("================================Bu_ID = " + UserSingleton.get().getUserInfo().getBu_ID());

        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Delivery_Date");
        propertyInfo2.setValue(Delivery_Date);
        propertyInfo2.setType(Date.class);
        propertyInfos.add(propertyInfo2);
        System.out.println("================================Delivery_Date = " + Delivery_Date);

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("CF_ID");
        propertyInfo3.setValue(CF_ID);
        propertyInfo3.setType(Long.class);
        propertyInfos.add(propertyInfo3);
        System.out.println("================================CF_ID = " + CF_ID);

        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName("CFName");
        propertyInfo4.setValue(CFName);
        propertyInfo4.setType(String.class);
        propertyInfos.add(propertyInfo4);
        System.out.println("================================CFName = " + CFName);

        PropertyInfo propertyInfo5 = new PropertyInfo();
        propertyInfo5.setName("SheetNo");
        propertyInfo5.setValue(SheetNo);
        propertyInfo5.setType(String.class);
        propertyInfos.add(propertyInfo5);
        System.out.println("================================SheetNo = " + SheetNo);

        PropertyInfo propertyInfo6 = new PropertyInfo();
        propertyInfo6.setName("HR_ID");
        propertyInfo6.setValue(UserSingleton.get().getHRID());
        propertyInfo6.setType(Integer.class);
        propertyInfos.add(propertyInfo6);
        System.out.println("================================HR_ID = " + UserSingleton.get().getHRID());

        PropertyInfo propertyInfo7 = new PropertyInfo();
        propertyInfo7.setName("Delivery_ID");
        propertyInfo7.setValue(Delivery_ID);
        propertyInfo7.setType(Long.class);
        propertyInfos.add(propertyInfo7);
        System.out.println("================================Delivery_ID = " + Delivery_ID);

        PropertyInfo propertyInfo8 = new PropertyInfo();
        propertyInfo8.setName("DPI_ID");
        propertyInfo8.setValue(DPI_ID);
        propertyInfo8.setType(Long.class);
        propertyInfos.add(propertyInfo8);
        System.out.println("================================DPI_ID = " + DPI_ID);

        PropertyInfo propertyInfo9 = new PropertyInfo();
        propertyInfo9.setName("DO_ID");
        propertyInfo9.setValue(DO_ID);
        propertyInfo9.setType(Long.class);
        propertyInfos.add(propertyInfo9);
        System.out.println("================================DO_ID = " + DO_ID);

//       PropertyInfo propertyInfo9 = new PropertyInfo();
//       propertyInfo9.setName("HR_ID");
//       propertyInfo9.setValue(UserSingleton.get().getHRID());
//       propertyInfo9.setType(Integer.class);
//       propertyInfos.add(propertyInfo9);
//       System.out.println("================================HR_ID = " + UserSingleton.get().getHRID());

        PropertyInfo propertyInfo10 = new PropertyInfo();
        propertyInfo10.setName("PS_ID");
        propertyInfo10.setValue(PS_ID);
        propertyInfo10.setType(Integer.class);
        propertyInfos.add(propertyInfo10);
        System.out.println("================================PS_ID = " + PS_ID);

        PropertyInfo propertyInfo11 = new PropertyInfo();
        propertyInfo11.setName("Qty");
        propertyInfo11.setValue(Qty);
        propertyInfo11.setType(String.class);
        propertyInfos.add(propertyInfo11);
        System.out.println("================================Qty = " + Qty);

//       long PS_ID , String Qty

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }
        return null;
    }

    //成品出库 物流信息
    public static WsResult op_Product_Logistics(int Abroad, int Mass, int Company_ID, int Bu_ID, int DT_ID,
                                                String TrackNo, String DriverName, String DriverTel,
                                                String CarPlateNO, String LogisticsRemark, int Replenish,
                                                String Delivery_No,
                                                int LC_ID, Date Shiping_Date, long SID, String Contract_No, int CF_ID,
                                                Date Arrive_Date, Date LoadTime, String Des_Address) {

        String webMethodName = "saveLogisticsInfoParams";
//        String webMethodName = "op_Product_Manu_In_Not_Pallet_1";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("Abroad");
        propertyInfo1.setValue(Abroad);
        propertyInfo1.setType(Integer.class);
        propertyInfos.add(propertyInfo1);

        //// TODO: 2019/12/31 indate
        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Mass");
        propertyInfo2.setValue(Mass);
        propertyInfo2.setType(Integer.class);
        propertyInfos.add(propertyInfo2);

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("Company_ID");
        propertyInfo3.setValue(Company_ID);
        propertyInfo3.setType(Integer.class);
        propertyInfos.add(propertyInfo3);


        //// TODO: 2019/12/31 wc_id  ?
        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName("Bu_ID");
        propertyInfo4.setValue(Bu_ID);
        propertyInfo4.setType(Integer.class);
        propertyInfos.add(propertyInfo4);

        PropertyInfo propertyInfo5 = new PropertyInfo();
        propertyInfo5.setName("DT_ID");
        propertyInfo5.setValue(DT_ID);
        propertyInfo5.setType(Integer.class);
        propertyInfos.add(propertyInfo5);

        PropertyInfo propertyInfo6 = new PropertyInfo();
        propertyInfo6.setName("TrackNo");
        propertyInfo6.setValue(TrackNo);
        propertyInfo6.setType(String.class);
        propertyInfos.add(propertyInfo6);

        PropertyInfo propertyInfo7 = new PropertyInfo();
        propertyInfo7.setName("DriverName");
        propertyInfo7.setValue(DriverName);
        propertyInfo7.setType(String.class);
        propertyInfos.add(propertyInfo7);

//        String DriverTel ,
//
//        String CarPlateNO ,String LogisticsRemark , int Replenish ,
//
//        String Delivery_No ,
//
//        int  LC_ID , Date Shiping_Date
        PropertyInfo propertyInfo8 = new PropertyInfo();
        propertyInfo8.setName("DriverTel");
        propertyInfo8.setValue(DriverTel);
        propertyInfo8.setType(String.class);
        propertyInfos.add(propertyInfo8);

        PropertyInfo propertyInfo9 = new PropertyInfo();
        propertyInfo9.setName("CarPlateNO");
        propertyInfo9.setValue(CarPlateNO);
        propertyInfo9.setType(String.class);
        propertyInfos.add(propertyInfo9);
//
        PropertyInfo propertyInfo10 = new PropertyInfo();
        propertyInfo10.setName("LogisticsRemark");
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setValue(Double.valueOf(subProductItemEntity.getQty()));
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setType(Double.class);
//        propertyInfo10.setType(Float.class);
        propertyInfo10.setValue(LogisticsRemark);
        propertyInfo10.setType(String.class);
        propertyInfos.add(propertyInfo10);

//        ManuLotNo , ManuDate As Date,
        // Ist_ID , Remark , Recorder , RecorderName 
        PropertyInfo propertyInfo11 = new PropertyInfo();
        propertyInfo11.setName("Replenish");
        propertyInfo11.setValue(Replenish);
        propertyInfo11.setType(Integer.class);
        propertyInfos.add(propertyInfo11);

//        //// TODO: 2019/12/31 manuDate
        PropertyInfo propertyInfo12 = new PropertyInfo();
        propertyInfo12.setName("Delivery_No");
        propertyInfo12.setValue(Delivery_No);
        propertyInfo12.setType(String.class);
        propertyInfos.add(propertyInfo12);

        PropertyInfo propertyInfo13 = new PropertyInfo();
        propertyInfo13.setName("LC_ID");
        propertyInfo13.setValue(LC_ID);
        propertyInfo13.setType(Integer.class);
        propertyInfos.add(propertyInfo13);

        PropertyInfo propertyInfo14 = new PropertyInfo();
        propertyInfo14.setName("Shiping_Date");
        propertyInfo14.setValue(Shiping_Date);
        propertyInfo14.setType(Date.class);
        propertyInfos.add(propertyInfo14);


//        long SID , String Contract_No , int CF_ID

        PropertyInfo propertyInfo15 = new PropertyInfo();
        propertyInfo15.setName("SID");
        propertyInfo15.setValue(SID);
        propertyInfo15.setType(Long.class);
        propertyInfos.add(propertyInfo15);


        PropertyInfo propertyInfo16 = new PropertyInfo();
        propertyInfo16.setName("Contract_No");
        propertyInfo16.setValue(Contract_No);
        propertyInfo16.setType(String.class);
        propertyInfos.add(propertyInfo16);


        PropertyInfo propertyInfo17 = new PropertyInfo();
        propertyInfo17.setName("CF_ID");
        propertyInfo17.setValue(CF_ID);
        propertyInfo17.setType(Integer.class);
        propertyInfos.add(propertyInfo17);

//        Date Arrive_Date , Date LoadTime?

        PropertyInfo propertyInfo18 = new PropertyInfo();
        propertyInfo18.setName("LoadTime");
        propertyInfo18.setValue(LoadTime);
        propertyInfo18.setType(Date.class);
        propertyInfos.add(propertyInfo18);

        PropertyInfo propertyInfo19 = new PropertyInfo();
        propertyInfo19.setName("Arrive_Date");
        propertyInfo19.setValue(Arrive_Date);
        propertyInfo19.setType(Date.class);
        propertyInfos.add(propertyInfo19);

        PropertyInfo propertyInfo20 = new PropertyInfo();
        propertyInfo20.setName("Des_Address");
        propertyInfo20.setValue(Des_Address);
        propertyInfo20.setType(String.class);
        propertyInfos.add(propertyInfo20);


        PropertyInfo propertyInfo21 = new PropertyInfo();
        propertyInfo21.setName("HR_ID");
        propertyInfo21.setValue(UserSingleton.get().getHRID());
        propertyInfo21.setType(Integer.class);
        propertyInfos.add(propertyInfo21);


        PropertyInfo propertyInfo22 = new PropertyInfo();
        propertyInfo22.setName("HR_Name");
        propertyInfo22.setValue(UserSingleton.get().getHRName());
        propertyInfo22.setType(String.class);
        propertyInfos.add(propertyInfo22);


//        HR_ID

//        PropertyInfo propertyInfo15 = new PropertyInfo();
//        propertyInfo15.setName("Recorder");
//        propertyInfo15.setValue(Recorder);
//        propertyInfo15.setType(Integer.class);
//        propertyInfos.add(propertyInfo15);
//        System.out.println("================================Recorder = " + Recorder);
//
//        PropertyInfo propertyInfo16 = new PropertyInfo();
//        propertyInfo16.setName("RecorderName");
//        propertyInfo16.setValue(RecorderName);
//        propertyInfo16.setType(String.class);
//        propertyInfos.add(propertyInfo16);
//        System.out.println("================================RecorderName = " + RecorderName);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }
        return null;

    }


    //将string解析为mpiwc
    public static MpiWcBean op_Check_Commit_MW_Barcode(String X) {
        String webMethodName = "op_Check_Commit_MW_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("X");
        propertyInfo.setValue(X);
        propertyInfo.setType(String.class);

        propertyInfos.add(propertyInfo);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        MpiWcBean mw = new MpiWcBean();

        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);


                mw.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));

                if (!mw.getResult()) {
                    mw.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    mw.setMPIWC_ID(Long.parseLong(obj2.getProperty("Mpiwc_ID").toString()));
                    mw.setMwName(obj2.getProperty("MwName").toString());
                    mw.setWc_ID(Integer.parseInt(obj2.getProperty("Wc_ID").toString()));
                    mw.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
                    mw.setBuName(obj2.getProperty("BuName").toString());

                }


            }

        }

        return mw;
    }


    //获取已经投料的内容，根据计划id?
    public static List<PlanInnerDetailEntity> opGetMWIssedItems(Long mw_id) {
        String webMethodName = "op_Get_MW_Issued_Items";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("MW_ID");
        propertyInfo.setValue(mw_id);
        propertyInfo.setType(String.class);
        propertyInfos.add(propertyInfo);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        List<PlanInnerDetailEntity> result;
        result = new ArrayList<>();


        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);

                int countX = obj2.getPropertyCount();

                for (int xi = 0; xi < countX; xi++) {
                    SoapObject obj3;
                    obj3 = (SoapObject) obj2.getProperty(xi);

                    PlanInnerDetailEntity im = new PlanInnerDetailEntity();

                    im.setItem_ID(Long.parseLong(obj3.getProperty("Item_ID").toString()));
                    im.setIV_ID(Long.parseLong(obj3.getProperty("IV_ID").toString()));
                    im.setItemName(obj3.getProperty("ItemName").toString());
                    if (!obj3.getProperty("NextLocation").toString().equals("anyType{}")) {
                        im.setNextLocation(obj3.getProperty("NextLocation").toString());
                    } else {
                        im.setNextLocation("(无?)");
                    }
                    //im.setNextLotNo(obj3.getProperty("NextLotNo ").toString());
                    im.setSingleQty(Float.parseFloat(obj3.getProperty("SingleQty").toString()));
                    im.setNeedQty(Float.parseFloat(obj3.getProperty("NeedQty").toString()));
                    im.setIssuedQty(Float.parseFloat(obj3.getProperty("IssuedQty").toString()));
                    if (im.getIssuedQty() > im.getNeedQty()) {
                        im.setMoreQty(0);
                    } else {
                        im.setMoreQty(im.getNeedQty() - im.getIssuedQty());
                    }
                    im.setLastIssueMoment(obj3.getProperty("LastIssueMoment").toString());

                    result.add(im);

                }

            }

        }

        return result;
    }


    //提交新的投料标签
    public static BoxItemEntity op_Check_Commit_MW_Issue_Item_Barcode(Long MW_ID, String X) {
        System.out.println("==op_Check_Commit_MW_Issue_Item_Barcode MW_ID =" + MW_ID + "  X = " + X);
        String webMethodName = "op_Check_Commit_MW_Issue_Item_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPropertyInfo(propertyInfos, "MW_ID", MW_ID);
        AddPropertyInfo(propertyInfos, "X", X);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        BoxItemEntity box_item = new BoxItemEntity();

        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);


                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));

                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    fill_box_item(box_item, obj2);

                    /*box_item.setDIII_ID(Long.parseLong(obj2.getProperty("DIII_ID").toString()));
                    box_item.setSMT_ID(Long.parseLong(obj2.getProperty("SMT_ID").toString()));
                    box_item.setSMM_ID(Long.parseLong(obj2.getProperty("SMM_ID").toString()));
                    box_item.setSMLI_ID(Long.parseLong(obj2.getProperty("SMLI_ID").toString()));
                    box_item.setEntityID(Long.parseLong(obj2.getProperty("EntityID").toString()));
                    box_item.setEntityName(obj2.getProperty("EntityName").toString());
                    box_item.setLotID(Long.parseLong(obj2.getProperty("LotID").toString()));
                    box_item.setItem_ID(Long.parseLong(obj2.getProperty("Item_ID").toString()));
                    box_item.setIV_ID(Long.parseLong(obj2.getProperty("IV_ID").toString()));
                    box_item.setItemName(obj2.getProperty("ItemName").toString());
                    box_item.setQty(Float.parseFloat(obj2.getProperty("Qty").toString()));
                    box_item.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
                    box_item.setBUName(obj2.getProperty("BuName").toString());
                    box_item.setLotNo(obj2.getProperty("LotNo").toString());
                    box_item.setIst_ID(Long.parseLong(obj2.getProperty("Ist_ID").toString()));
                    box_item.setSub_Ist_ID(Long.parseLong(obj2.getProperty("Sub_Ist_ID").toString()));*/
                }

                //int DataMemberCount = obj2.getPropertyCount();
                //for (int j=0; j<DataMemberCount;j++){
                //result.add(Boolean.parseBoolean(obj2.getProperty(j).toString()));
                //}
            }

        }

        return box_item;
    }

    public static BoxItemEntity op_Check_Commit_MW_Issue_Extra_Item_Barcode(Long MW_ID, String X) {
        String webMethodName = "op_Check_Commit_MW_Issue_Extra_Item_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPropertyInfo(propertyInfos, "MW_ID", MW_ID);
        AddPropertyInfo(propertyInfos, "X", X);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        BoxItemEntity box_item = new BoxItemEntity();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    fill_box_item(box_item, obj2);

                    /*box_item.setDIII_ID(Long.parseLong(obj2.getProperty("DIII_ID").toString()));
                    box_item.setSMT_ID(Long.parseLong(obj2.getProperty("SMT_ID").toString()));
                    box_item.setSMM_ID(Long.parseLong(obj2.getProperty("SMM_ID").toString()));
                    box_item.setSMLI_ID(Long.parseLong(obj2.getProperty("SMLI_ID").toString()));
                    box_item.setEntityID(Long.parseLong(obj2.getProperty("EntityID").toString()));
                    box_item.setEntityName(obj2.getProperty("EntityName").toString());
                    box_item.setLotID(Long.parseLong(obj2.getProperty("LotID").toString()));
                    box_item.setItem_ID(Long.parseLong(obj2.getProperty("Item_ID").toString()));
                    box_item.setIV_ID(Long.parseLong(obj2.getProperty("IV_ID").toString()));
                    box_item.setItemName(obj2.getProperty("ItemName").toString());
                    box_item.setQty(Float.parseFloat(obj2.getProperty("Qty").toString()));
                    box_item.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
                    box_item.setBUName(obj2.getProperty("BuName").toString());
                    box_item.setLotNo(obj2.getProperty("LotNo").toString());
                    box_item.setIst_ID(Long.parseLong(obj2.getProperty("Ist_ID").toString()));
                    box_item.setSub_Ist_ID(Long.parseLong(obj2.getProperty("Sub_Ist_ID").toString()));
                    */
                }
            }
        }

        return box_item;
    }

    //部门领料中解析物料
    public static BoxItemEntity op_Check_Commit_Inv_Out_Item_Barcode(int Bu_ID, String X) {

        System.out.println("************************** x = " + X);
        //// TODO: 2019/8/13 为测试Webservice而用，方法结束换回来
//        URL = "http://172.16.2.21:61272/Service.svc";
//        URL = "http://116.236.97.186:8001/Service.svc";
        String webMethodName = "op_Check_Commit_Inv_Out_Item_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPropertyInfo(propertyInfos, "Bu_ID", Bu_ID);
        AddPropertyInfo(propertyInfos, "X", X);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        BoxItemEntity box_item = new BoxItemEntity();

        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));

                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    fill_box_item(box_item, obj2);
                }
            }
        }
        return box_item;
    }

    //todo

    private static void fill_box_item(BoxItemEntity box_item, SoapObject obj) {
        box_item.setDIII_ID(Long.parseLong(obj.getProperty("DIII_ID").toString()));
        box_item.setSMT_ID(Long.parseLong(obj.getProperty("SMT_ID").toString()));
        box_item.setSMM_ID(Long.parseLong(obj.getProperty("SMM_ID").toString()));
        box_item.setSMLI_ID(Long.parseLong(obj.getProperty("SMLI_ID").toString()));
        box_item.setEntityID(Long.parseLong(obj.getProperty("EntityID").toString()));
        box_item.setEntityName(obj.getProperty("EntityName").toString());
        box_item.setLotID(Long.parseLong(obj.getProperty("LotID").toString()));
        box_item.setLotNo(obj.getProperty("LotNo").toString());
        box_item.setItem_ID(Long.parseLong(obj.getProperty("Item_ID").toString()));
        box_item.setIV_ID(Long.parseLong(obj.getProperty("IV_ID").toString()));
        box_item.setItemName(obj.getProperty("ItemName").toString());
        box_item.setBoxQty(Float.parseFloat(obj.getProperty("BoxQty").toString()));
        box_item.setQty(Float.parseFloat(obj.getProperty("Qty").toString()));
        box_item.setBu_ID(Integer.parseInt(obj.getProperty("Bu_ID").toString()));
        box_item.setBuName(obj.getProperty("BuName").toString());
        box_item.setIst_ID(Long.parseLong(obj.getProperty("Ist_ID").toString()));
        box_item.setSub_Ist_ID(Long.parseLong(obj.getProperty("Sub_Ist_ID").toString()));
        box_item.setIstName(obj.getProperty("IstName").toString());
        box_item.setBoxName(obj.getProperty("BoxName").toString());
        box_item.setBoxNo(obj.getProperty("BoxNo").toString());
        box_item.setFreezeStatus(obj.getProperty("FreezeStatus").toString());

        box_item.setLotDescription(isNothing2String(obj.getProperty("LotDescription"), ""));
        box_item.setSmlRemark(isNothing2String(obj.getProperty("SMLRemark"), ""));

        box_item.setManuLotNo(isNothing2String(obj.getProperty("ManuLotNo"), ""));
    }

    //解析返工出库 转成包装
    public static BoxItemEntity op_Check_Commit_WC_Return_Item_Barcode(String X) {
        String webMethodName = "op_Check_Commit_WC_Return_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "X", X);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        BoxItemEntity box_item = new BoxItemEntity();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    fill_box_item(box_item, obj2);
                }
            }
        }
        return box_item;
    }

    private static PropertyInfo getNewPropertyInfo(String Name, Object value) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName(Name);
        propertyInfo.setValue(value);
        propertyInfo.setType(value.getClass());
        return propertyInfo;
    }

    public static void AddPropertyInfo(ArrayList<PropertyInfo> propertyInfos, String Name, Object Value) {
        PropertyInfo propertyInfo = getNewPropertyInfo(Name, Value);
        propertyInfos.add(propertyInfo);
    }

    public static WsResult op_Commit_MW_Issue_Item(Long MW_ID, BoxItemEntity bi) {
        String sqty = String.valueOf(bi.getQty());
//        WsResult Result = op_Commit_MW_Issue_Item(MW_ID, UserInfoEntity.ID, bi.getItem_ID(), bi.getIV_ID(), bi.getLotID(), bi.getLotNo(), bi.getIst_ID(), bi.getSub_Ist_ID(), bi.getSMLI_ID(), bi.getSMM_ID(), bi.getSMT_ID(), sqty);
        WsResult Result = op_Commit_MW_Issue_Item(MW_ID, UserSingleton.get().getHRID(), bi.getItem_ID(), bi.getIV_ID(), bi.getLotID(), bi.getLotNo(), bi.getIst_ID(), bi.getSub_Ist_ID(), bi.getSMLI_ID(), bi.getSMM_ID(), bi.getSMT_ID(), sqty);

        return Result;
    }

    public static WsResult op_Commit_MW_Issue_Item(Long MW_ID, int Sender, Long Item_ID, Long IV_ID, Long LotID, String LotNo, Long Ist_ID, Long Sub_Ist_ID, Long SMLI_ID, Long SMM_ID, Long SMT_ID, String Qty) {
        String webMethodName = "op_Commit_MW_New_Issue_Item";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPropertyInfo(propertyInfos, "MW_ID", MW_ID);
        AddPropertyInfo(propertyInfos, "Exer", Sender);
        AddPropertyInfo(propertyInfos, "Item_ID", Item_ID);
        AddPropertyInfo(propertyInfos, "IV_ID", IV_ID);
        AddPropertyInfo(propertyInfos, "LotID", LotID);
        AddPropertyInfo(propertyInfos, "LotNo", LotNo);
        AddPropertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPropertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPropertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);
        AddPropertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPropertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPropertyInfo(propertyInfos, "Qty", Qty);

        System.out.println("================ MW_ID = " + MW_ID + " exer = " + Sender + " item_id = " + Item_ID);
        System.out.println("================ IV_ID = " + IV_ID + " LotID = " + LotID + " LotNo = " + LotNo);
        System.out.println("================ Ist_ID = " + Ist_ID + " Sub_Ist_ID = " + Sub_Ist_ID + " SMLI_ID = " + SMLI_ID);
        System.out.println("================ SMM_ID = " + SMM_ID + " SMT_ID = " + SMT_ID + " Qty = " + Qty);


        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult ws_result = Get_WS_Result(obj);

        return ws_result;
    }

    public static WsResult op_Commit_MW_Issue_Extra_Item(Long MW_ID, BoxItemEntity bi, String remark) {
        String sqty = String.valueOf(bi.getQty());
//        WsResult Result = op_Commit_MW_Issue_Extra_Item(MW_ID, UserInfoEntity.ID, bi.getItem_ID(), bi.getIV_ID(), bi.getLotID(), bi.getLotNo(), bi.getIst_ID(), bi.getSub_Ist_ID(), bi.getSMLI_ID(), bi.getSMM_ID(), bi.getSMT_ID(), sqty);
        WsResult Result = op_Commit_MW_Issue_Extra_Item(MW_ID, UserSingleton.get().getHRID(), bi.getItem_ID(), bi.getIV_ID(), bi.getLotID(), bi.getLotNo(), bi.getIst_ID(), bi.getSub_Ist_ID(), bi.getSMLI_ID(), bi.getSMM_ID(), bi.getSMT_ID(), sqty, remark);
        return Result;
    }

    public static WsResult op_Commit_MW_Issue_Extra_Item(Long MW_ID, int Sender, Long Item_ID, Long IV_ID, Long LotID, String LotNo, Long Ist_ID, Long Sub_Ist_ID, Long SMLI_ID, Long SMM_ID, Long SMT_ID, String Qty, String remark) {
        String webMethodName = "op_Commit_MW_New_Issue_Extra_Item";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPropertyInfo(propertyInfos, "MW_ID", MW_ID);
        AddPropertyInfo(propertyInfos, "Exer", Sender);
        AddPropertyInfo(propertyInfos, "Item_ID", Item_ID);
        AddPropertyInfo(propertyInfos, "IV_ID", IV_ID);
        AddPropertyInfo(propertyInfos, "LotID", LotID);
        AddPropertyInfo(propertyInfos, "LotNo", LotNo);
        AddPropertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPropertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPropertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);
        AddPropertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPropertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPropertyInfo(propertyInfos, "Qty", Qty);
        AddPropertyInfo(propertyInfos, "Remark", remark);

        System.out.println("op_Commit_MW_New_Issue_Extra_Item MW_ID = " + MW_ID + " Exer=" + Sender + " item_id = " + Item_ID + " IV_ID=" + IV_ID);
        System.out.println("op_Commit_MW_New_Issue_Extra_Item LotID = " + LotID + " LotNo=" + LotNo + " Ist_ID=" + Ist_ID);
        System.out.println("op_Commit_MW_New_Issue_Extra_Item Sub_Ist_ID = " + Sub_Ist_ID + " SMLI_ID = " + SMLI_ID + " SMM_ID=" + SMM_ID);
        System.out.println("op_Commit_MW_New_Issue_Extra_Item SMT_ID = " + SMT_ID + " Qty = " + Qty + " Remark=" + remark);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult ws_result = Get_WS_Result(obj);

        return ws_result;

    }


//    public static WsResult op_Commit_Dep_Out_Item(int Bu_ID, HashMap<String, String> SelectDep, HashMap<String, String> SelectReaseach, BoxItemEntity bi) {
//        String sqty = String.valueOf(bi.getQty());
//
//        String webMethodName = "op_Commit_Dep_Out_Item";
//        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
//
//        AddPropertyInfo(propertyInfos, "Bu_ID", Bu_ID);
//        AddPropertyInfo(propertyInfos, "Exer", UserInfoEntity.ID);
//        AddPropertyInfo(propertyInfos, "EntityID", Long.valueOf(SelectDep.get("Department_ID")));
//        AddPropertyInfo(propertyInfos, "EntityName", SelectDep.get("Department_Name"));
//        AddPropertyInfo(propertyInfos, "Fi_Product_Type_ID", Integer.valueOf(SelectReaseach.get("FiPT_ID")));
//        AddPropertyInfo(propertyInfos, "Fi_Product_Status_ID", Integer.valueOf(SelectReaseach.get("FSPD_ID")));
//        AddPropertyInfo(propertyInfos, "Out_Product_ID", Long.valueOf(SelectReaseach.get("Product_ID")));
//        AddPropertyInfo(propertyInfos, "Out_Program_ID", Long.valueOf(SelectReaseach.get("Program_ID")));
//
//        AddPropertyInfo(propertyInfos, "Item_ID", bi.getItem_ID());
//        AddPropertyInfo(propertyInfos, "IV_ID", bi.getIV_ID());
//        AddPropertyInfo(propertyInfos, "LotID", bi.getLotID());
//        AddPropertyInfo(propertyInfos, "LotNo", bi.getLotNo());
//        AddPropertyInfo(propertyInfos, "Ist_ID", bi.getIst_ID());
//        AddPropertyInfo(propertyInfos, "Sub_Ist_ID", bi.getSub_Ist_ID());
//        AddPropertyInfo(propertyInfos, "SMLI_ID", bi.getSMLI_ID());
//        AddPropertyInfo(propertyInfos, "SMM_ID", bi.getSMM_ID());
//        AddPropertyInfo(propertyInfos, "SMT_ID", bi.getSMT_ID());
//
//        AddPropertyInfo(propertyInfos, "Qty", sqty);
//
//        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
//        SoapObject obj = (SoapObject) envelope.bodyIn;
//
//        WsResult ws_result = Get_WS_Result(obj);
//
//        return ws_result;
//    }

    //部门领料的出库？
    public static WsResult op_Commit_Dep_Out_Item(int Bu_ID, DepartmentBean departmentBean, ResearchItemBean researchItemBean, BoxItemEntity boxItemEntity, String remark) {
        String sqty = String.valueOf(boxItemEntity.getQty());
        String webMethodName = "op_Commit_Dep_Out_Item";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "Bu_ID", Bu_ID);
//        AddPropertyInfo(propertyInfos, "Exer", UserInfoEntity.ID);
        AddPropertyInfo(propertyInfos, "Exer", UserSingleton.get().getHRID());
        if (departmentBean != null) {
            AddPropertyInfo(propertyInfos, "EntityID", departmentBean.getDepartmentID());
            AddPropertyInfo(propertyInfos, "EntityName", departmentBean.getDepartmentName());
        }
        if (researchItemBean != null) {
            AddPropertyInfo(propertyInfos, "Fi_Product_Type_ID", researchItemBean.getFiPTID());
            AddPropertyInfo(propertyInfos, "Fi_Product_Status_ID", researchItemBean.getFSPDID());
            AddPropertyInfo(propertyInfos, "Out_Product_ID", researchItemBean.getProductID());
            AddPropertyInfo(propertyInfos, "Out_Program_ID", researchItemBean.getProgramID());
        }
        if (boxItemEntity != null) {
            AddPropertyInfo(propertyInfos, "Item_ID", boxItemEntity.getItem_ID());
            AddPropertyInfo(propertyInfos, "IV_ID", boxItemEntity.getIV_ID());
            AddPropertyInfo(propertyInfos, "LotID", boxItemEntity.getLotID());
            AddPropertyInfo(propertyInfos, "LotNo", boxItemEntity.getLotNo());
            AddPropertyInfo(propertyInfos, "Ist_ID", boxItemEntity.getIst_ID());
            AddPropertyInfo(propertyInfos, "Sub_Ist_ID", boxItemEntity.getSub_Ist_ID());
            AddPropertyInfo(propertyInfos, "SMLI_ID", boxItemEntity.getSMLI_ID());
            AddPropertyInfo(propertyInfos, "SMM_ID", boxItemEntity.getSMM_ID());
            AddPropertyInfo(propertyInfos, "SMT_ID", boxItemEntity.getSMT_ID());


        }
        AddPropertyInfo(propertyInfos, "Qty", sqty);
        AddPropertyInfo(propertyInfos, "Remark", remark);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        WsResult ws_result = Get_WS_Result(obj);
        return ws_result;
    }

    public static WsResult op_Commit_Return_Item(BoxItemEntity bi) {
//        WsResult Result = op_Commit_Return_Item(UserInfoEntity.ID, bi.getDIII_ID());
        WsResult Result = op_Commit_Return_Item(UserSingleton.get().getHRID(), bi.getDIII_ID());
        return Result;
    }

    public static WsResult op_Commit_Return_Item(int Sender, Long DIII_ID) {
        String webMethodName = "op_Commit_Return_Item";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPropertyInfo(propertyInfos, "Exer", Sender);
        AddPropertyInfo(propertyInfos, "DIII_ID", DIII_ID);


        WsResult ws_result;

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;


        ws_result = Get_WS_Result(obj);

        return ws_result;
    }


    public static Boolean op_Test(double Qty) {
        String webMethodName = "op_Test";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("Qty");
        propertyInfo.setValue(String.valueOf(Qty));
        propertyInfo.setType(String.class);
        propertyInfos.add(propertyInfo);


        Boolean result = false;

        SoapPrimitive response = null;
        try {
            SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            result = Boolean.parseBoolean(response.toString());
        }

        return result;
    }

    //移动库位
    public static BoxItemEntity op_Check_Commit_Move_Item_Barcode(String X) {
        String webMethodName = "op_Check_Commit_Inv_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "X", X);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        BoxItemEntity box_item = new BoxItemEntity();

        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);

                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));

                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {

                    fill_box_item(box_item, obj2);
                    //这个有点特别
                    box_item.setOldIstName(obj2.getProperty("IstName").toString());
                    box_item.setIstName(""); //'新位置还没设置
                    box_item.setIst_ID(0);

                    /*
                    box_item.setDIII_ID(Long.parseLong(obj2.getProperty("DIII_ID").toString()));
                    box_item.setSMT_ID(Long.parseLong(obj2.getProperty("SMT_ID").toString()));
                    box_item.setSMM_ID(Long.parseLong(obj2.getProperty("SMM_ID").toString()));
                    box_item.setSMLI_ID(Long.parseLong(obj2.getProperty("SMLI_ID").toString()));
                    //box_item.setEntityID(Long.parseLong(obj2.getProperty("EntityID").toString()));
                    //box_item.setEntityName(obj2.getProperty("EntityName").toString());
                    //box_item.setLotID(Long.parseLong(obj2.getProperty("LotID").toString()));
                    box_item.setItem_ID(Long.parseLong(obj2.getProperty("Item_ID").toString()));
                    box_item.setIV_ID(Long.parseLong(obj2.getProperty("IV_ID").toString()));
                    box_item.setItemName(obj2.getProperty("ItemName").toString());
                    box_item.setQty(Float.parseFloat(obj2.getProperty("Qty").toString()));
                    //box_item.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
                    //box_item.setBUName(obj2.getProperty("BuName").toString());

                    */

                }

            }

        }

        return box_item;
    }

    //盘点，扫描包装条码
    public static BoxItemEntity op_Check_Stock_Item_Barcode(int Bu_ID, String X) {
        String webMethodName = "op_Commit_Check_Stock_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "Bu_ID", Bu_ID);
        AddPropertyInfo(propertyInfos, "X", X);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        BoxItemEntity box_item = new BoxItemEntity();

        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    fill_box_item(box_item, obj2);
                }
            }
        }
        return box_item;
    }

    public static BoxItemEntity op_Check_Stock_Item_Barcode_V2(String X) {
        String webMethodName = "op_Commit_Check_Stock_Barcode_V2";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "X", X);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        BoxItemEntity box_item = new BoxItemEntity();

        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    fill_box_item(box_item, obj2);
                }
            }
        }
        return box_item;
    }

    public static WsResult op_Commit_Stock_Result(String Exer_Name, int CI_ID, int Bu_ID, String X,
                                                  Long Ist_ID, Long Sub_Ist_ID, Long SMT_ID, Long SMM_ID, Long SMLI_ID,
                                                  Long LotID, String Qty, String remark) {
        String webMethodName = "op_Commit_Check_Stock_Save";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "Exer_Name", Exer_Name);
        AddPropertyInfo(propertyInfos, "CI_ID", CI_ID);
        AddPropertyInfo(propertyInfos, "Bu_ID", Bu_ID);
        AddPropertyInfo(propertyInfos, "X", X);
        AddPropertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPropertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPropertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPropertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPropertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);
        AddPropertyInfo(propertyInfos, "LotID", LotID);
        AddPropertyInfo(propertyInfos, "Qty", Qty);
        AddPropertyInfo(propertyInfos, "Remark", remark);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        return getWS_Result(obj);

    }


    public static WsResult op_Commit_Stock_Result_V2(String Exer_Name, int CI_ID, String X,
                                                     Long Ist_ID, Long Sub_Ist_ID, Long SMT_ID, Long SMM_ID, Long SMLI_ID,
                                                     String Qty, String remark) {
        String webMethodName = "op_Commit_Check_Stock_Save_V2";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "Exer_Name", Exer_Name);
        AddPropertyInfo(propertyInfos, "CI_ID", CI_ID);

        AddPropertyInfo(propertyInfos, "X", X);
        AddPropertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPropertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPropertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPropertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPropertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);

        AddPropertyInfo(propertyInfos, "Qty", Qty);
        AddPropertyInfo(propertyInfos, "Remark", remark);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        return getWS_Result(obj);

    }

    public static WsResult op_Commit_Stock_Result_V4(String Exer_Name, int CI_ID, int Bu_ID, String X,
                                                     Long Ist_ID, Long Sub_Ist_ID, Long SMT_ID, Long SMM_ID, Long SMLI_ID,
                                                     Long LotID, String Qty, String N, String PN, String DQ, String remark) {
        String webMethodName = "op_Commit_Check_Stock_Save_V4";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "Exer_Name", Exer_Name);
        AddPropertyInfo(propertyInfos, "CI_ID", CI_ID);
        AddPropertyInfo(propertyInfos, "Bu_ID", Bu_ID);
        AddPropertyInfo(propertyInfos, "X", X);
        AddPropertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPropertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPropertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPropertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPropertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);
        AddPropertyInfo(propertyInfos, "LotID", LotID);
        AddPropertyInfo(propertyInfos, "Qty", Qty);
        AddPropertyInfo(propertyInfos, "N", N);
        AddPropertyInfo(propertyInfos, "PN", PN);
        AddPropertyInfo(propertyInfos, "DQ", DQ);

        AddPropertyInfo(propertyInfos, "Remark", remark);

        System.out.println("==================================Exer_Name = " + Exer_Name + " ci_id  = " + CI_ID + " bu_id  = " + Bu_ID);
        System.out.println("==================================X = " + X + " Ist_ID  = " + Ist_ID + " Sub_Ist_ID  = " + Sub_Ist_ID);
        System.out.println("==================================SMT_ID = " + SMT_ID + " SMM_ID  = " + SMM_ID + " SMLI_ID  = " + SMLI_ID);
        System.out.println("==================================LotID = " + LotID + " Qty  = " + Qty + " N  = " + N);
        System.out.println("==================================PN = " + PN + " DQ  = " + DQ + " Remark  = " + remark);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        return getWS_Result(obj);

    }


    public static WsResult commit_Self_Product_Pandian(String Exer_Name, int CI_ID, int Bu_ID, String X,
                                                     Long Ist_ID, Long Sub_Ist_ID, int Item_ID, int IV_ID,
                                                     Long LotID, String Qty, String N, String PN, String DQ, String remark,String storeArea,String manuLotNo) {
        String webMethodName = "op_Check_Self_Product_Input_Item";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "Exer_Name", Exer_Name);
        AddPropertyInfo(propertyInfos, "CI_ID", CI_ID);
        AddPropertyInfo(propertyInfos, "Bu_ID", Bu_ID);
        AddPropertyInfo(propertyInfos, "X", X);
        AddPropertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPropertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
//        AddPropertyInfo(propertyInfos, "SMT_ID", SMT_ID);
//        AddPropertyInfo(propertyInfos, "SMM_ID", SMM_ID);
//        AddPropertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);
                AddPropertyInfo(propertyInfos, "Item_ID", Item_ID);
        AddPropertyInfo(propertyInfos, "IV_ID", IV_ID);
        AddPropertyInfo(propertyInfos, "LotID", LotID);
        AddPropertyInfo(propertyInfos, "Qty", Qty);
        AddPropertyInfo(propertyInfos, "N", N);
        AddPropertyInfo(propertyInfos, "PN", PN);
        AddPropertyInfo(propertyInfos, "DQ", DQ);

        AddPropertyInfo(propertyInfos, "Remark", remark);
        AddPropertyInfo(propertyInfos, "storeArea", storeArea);
        AddPropertyInfo(propertyInfos, "manuLotNo", manuLotNo);

        System.out.println("==================================Exer_Name = " + Exer_Name + " ci_id  = " + CI_ID + " bu_id  = " + Bu_ID);
        System.out.println("==================================X = " + X + " Ist_ID  = " + Ist_ID + " Sub_Ist_ID  = " + Sub_Ist_ID);
        System.out.println("==================================Item_ID = " + Item_ID + " IV_ID  = " + IV_ID );
        System.out.println("==================================LotID = " + LotID + " Qty  = " + Qty + " N  = " + N);
        System.out.println("==================================PN = " + PN + " DQ  = " + DQ + " Remark  = " + remark + " ");
        System.out.println("==================================storeArea = " + storeArea + " manuLotNo  = " + manuLotNo );

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        return getWS_Result(obj);

    }


    public static WsResult op_Commit_Stock_Result_V3(String Exer_Name, int CI_ID, String X,
                                                     Long Ist_ID, Long Sub_Ist_ID, Long SMT_ID, Long SMM_ID, Long SMLI_ID,
                                                     String Qty, String N, String PN, String DQ, String remark) {
        String webMethodName = "op_Commit_Check_Stock_Save_V3";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "Exer_Name", Exer_Name);
        AddPropertyInfo(propertyInfos, "CI_ID", CI_ID);

        AddPropertyInfo(propertyInfos, "X", X);
        AddPropertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPropertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPropertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPropertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPropertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);

        AddPropertyInfo(propertyInfos, "Qty", Qty);
        AddPropertyInfo(propertyInfos, "N", N);
        AddPropertyInfo(propertyInfos, "PN", PN);
        AddPropertyInfo(propertyInfos, "DQ", DQ);

        AddPropertyInfo(propertyInfos, "Remark", remark);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        return getWS_Result(obj);

    }

    public static BoxItemEntity op_Check_Commit_Freeze_Item_Barcode(String X) {
        String webMethodName = "op_Check_Commit_Inv_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "X", X);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        BoxItemEntity box_item = new BoxItemEntity();

        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);

                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));

                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    fill_box_item(box_item, obj2);

                    /*
                    box_item.setDIII_ID(Long.parseLong(obj2.getProperty("DIII_ID").toString()));
                    box_item.setSMT_ID(Long.parseLong(obj2.getProperty("SMT_ID").toString()));
                    box_item.setSMM_ID(Long.parseLong(obj2.getProperty("SMM_ID").toString()));
                    box_item.setSMLI_ID(Long.parseLong(obj2.getProperty("SMLI_ID").toString()));
                    box_item.setItem_ID(Long.parseLong(obj2.getProperty("Item_ID").toString()));
                    box_item.setIV_ID(Long.parseLong(obj2.getProperty("IV_ID").toString()));
                    box_item.setItemName(obj2.getProperty("ItemName").toString());
                    box_item.setQty(Float.parseFloat(obj2.getProperty("Qty").toString()));
                    box_item.setIstName(obj2.getProperty("IstName").toString());
                    box_item.setFreezeStatus(obj2.getProperty("FreezeStatus").toString());
                    */
                }

            }

        }

        return box_item;
    }

    public static WsResult op_Commit_Move_Item(BoxItemEntity box_item) {
        String webMethodName = "op_Commit_Inv_Move";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPropertyInfo(propertyInfos, "Bu_ID", UserSingleton.get().getUserInfo().getBu_ID());
        AddPropertyInfo(propertyInfos, "SenderID", UserSingleton.get().getHRID());
        AddPropertyInfo(propertyInfos, "SMLI_ID", box_item.getSMLI_ID());
        AddPropertyInfo(propertyInfos, "SMM_ID", box_item.getSMM_ID());
        AddPropertyInfo(propertyInfos, "SMT_ID", box_item.getSMT_ID());
        AddPropertyInfo(propertyInfos, "Ist_ID", box_item.getIst_ID());
        AddPropertyInfo(propertyInfos, "Sub_Ist_ID", box_item.getSub_Ist_ID());

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult result = Get_WS_Result(obj);


        return result;

    }

    public static WsResult op_Commit_Freeze_Inv(BoxItemEntity box_item) {
        String webMethodName = "op_Commit_Freeze_Inv";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

//        AddPropertyInfo(propertyInfos, "SenderID", UserInfoEntity.ID);
        AddPropertyInfo(propertyInfos, "SenderID", UserSingleton.get().getHRID());
        AddPropertyInfo(propertyInfos, "SMLI_ID", box_item.getSMLI_ID());
        AddPropertyInfo(propertyInfos, "SMT_ID", box_item.getSMT_ID());

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult result = Get_WS_Result(obj);


        return result;

    }

    public static WsResult op_Commit_FreezeNot_Inv(BoxItemEntity box_item) {
        String webMethodName = "op_Commit_FreezeNot_Inv";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

//        AddPropertyInfo(propertyInfos, "SenderID", UserInfoEntity.ID);
        AddPropertyInfo(propertyInfos, "SenderID", UserSingleton.get().getHRID());
        AddPropertyInfo(propertyInfos, "SMLI_ID", box_item.getSMLI_ID());
        AddPropertyInfo(propertyInfos, "SMT_ID", box_item.getSMT_ID());

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult result = Get_WS_Result(obj);

        return result;

    }

    public static WsResult getWS_Result(SoapObject obj) {
        return Get_WS_Result(obj);
    }

    //todo
    public static WsResult Get_WS_Result(SoapObject obj) {

        WsResult result = new WsResult();

        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;

            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                if (obj2.getProperty("Result") != null) {
                    result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                }

                if (obj2.getProperty("ErrorInfo") != null) {
                    result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                }
                if (obj2.getProperty("ID") != null) {
                    result.setID(Long.parseLong(obj2.getProperty("ID").toString()));
                }
            }
        }
        return result;
    }

    public static WsResult getDataTable(String sql) {
        System.out.println("================ getDataTable sql =" + sql);
        int maxCount = 1000;
        //方便打印完整log
        if (sql.length() > maxCount) {
            for (int i = 0; i < sql.length(); i += maxCount) {
                if (i + maxCount < sql.length()) {
                    Log.i("resCounter" + i, sql.substring(i, i + maxCount));
//                   System.out.println("resCounter" + i + sql.substring(i, i + maxCount));
                } else {
                    Log.i("resCounter" + i, sql.substring(i, sql.length()));
//                    System.out.println("resCounter" + i + sql.substring(i, sql.length()));
                }
            }
        } else {
//            Log.i("resinfo", sql);
            Log.i("resCounter", sql);
        }
//        System.out.println(String.format("********* %s", sql));
        WsResult result = null;
        String webMethodName = "GetAndroidData";
        ArrayList<PropertyInfo> propertyInfoList = new ArrayList<>();
        String crpSql = "";
        try {
            crpSql = EncryptDecryptUtil.encryptToBase64(sql, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        AddPropertyInfo(propertyInfoList, "CryptSQL", crpSql);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        result = getWS_Result(obj);
        System.out.println("================ json =" + result.getErrorInfo());
        return result;
    }

//    public static boolean insertDeliveryOrder_Item(SDZHSinglePartBean model) {
//
//        String sql = "INSERT INTO DeliveryOrder_Item (BoxCode, DOI_NO, CustomerProductNo,DOI_Code, WorkNo, ProductId, LineId, IsDelete) VALUES('" + model.BoxCode + "','" + model.DOI_NO + "','" + model.CustomerProductNo + "','" + model.DOI_Code + "','" + model.WorkNo + "'," + model.ProductId.ToString() + "," + model.LineId.ToString() + ",0)";
//        return DC.ExecuteNonQuery(sql);
//    }
//
//
//    public static boolean updateDeliveryOrder_Box(String columns, String where) {
//
//        String sql = " UPDATE DeliveryOrder_Box SET  " + columns + " WHERE " + where;
//        return DC.ExecuteNonQuery(sql)
//    }


    public static JsonObject getDataRow(String sql) {
        JsonObject jsonObject = null;
        String result = null;
        String webMethodName = "GetAndroidData";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        String crpSql = "";
        try {
            crpSql = EncryptDecryptUtil.encryptToBase64(sql, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        AddPropertyInfo(propertyInfos, "CryptSQL", crpSql);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapPrimitive response = null;
        try {
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            result = response.toString();
            try {
                JsonArray jsonArray = (JsonArray) new JsonParser().parse(result);
                jsonObject = jsonArray.get(0).getAsJsonObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        return jsonObject;
    }

    public static List<JsonObject> getJsonList(String SQL) {
        String resultData = getJsonDataBySQL(SQL);
        if (resultData == null) {
            return null;
        }
        //变成List
        List<JsonObject> jsonObjectList = ConvertJstring2List(resultData);
        //结果
        return jsonObjectList;

    }


    public static String getVersionCode() {
//        http://172.16.2.20:8002/WebService.asmx/ReadApkVersion
        String webMethodName = "ReadApkVersion";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        SoapSerializationEnvelope envelope = tempinvokeSupplierWS(propertyInfos, webMethodName, "http://172.16.2.20:8002/WebService.asmx");
//        Object response = null;
        SoapObject response = null;
        try {
//            response = (SoapPrimitive) envelope.getResponse();
            response = (SoapObject) envelope.getResponse();
            SoapObject result = (SoapObject) envelope.bodyIn;

        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            return response.toString();
        }
        return "";
    }

    public static List<BUItemBean> getBUBeanList(String SQL) {
        String resultData = getJsonDataBySQL(SQL);
        if (resultData == null) {
            return null;
        }
        //变成List
//        List<JsonObject> ojsonObjectList = ConvertJstring2List(resultData);
//        BUItemBean buItemBean = JsonUtil.parseJsonToObject(resultData, BUItemBean.class);
        Type type = new TypeToken<List<BUItemBean>>() {
        }.getType();
        List<BUItemBean> list = JsonUtil.parseJsonToObject(resultData, type);
        //结果
        return list;

    }

    /**
     * 获取可录入单据的权限的人
     */
    public static List<StockPermittedBean> getStockInPermittedHRIDList(String Sql){
        String resultData = getJsonDataBySQL(Sql);
        if (resultData == null) {
            return null;
        }
        //变成List
//        List<JsonObject> ojsonObjectList = ConvertJstring2List(resultData);
//        BUItemBean buItemBean = JsonUtil.parseJsonToObject(resultData, BUItemBean.class);
        Type type = new TypeToken<List<StockPermittedBean>>() {
        }.getType();
        List<StockPermittedBean> list = JsonUtil.parseJsonToObject(resultData, type);
        //结果
        return list;
    }

    public static <T> List<T> getCommonItemBeanList(String SQL, T t) {
        String resultData = getJsonDataBySQL(SQL);
        if (resultData == null) {
            return null;
        }
        //变成List
//        List<JsonObject> ojsonObjectList = ConvertJstring2List(resultData);
//        BUItemBean buItemBean = JsonUtil.parseJsonToObject(resultData, BUItemBean.class);
        Type type = new TypeToken<List<T>>() {
        }.getType();
        List<T> list = JsonUtil.parseJsonToObject(resultData, type);
        //结果
        return list;

    }

    public static List<DepartmentBean> getDepartmentBeanList(String SQL) {
        String resultData = getJsonDataBySQL(SQL);
        if (resultData == null) {
            return null;
        }
        //变成List
//        List<JsonObject> ojsonObjectList = ConvertJstring2List(resultData);
//        BUItemBean buItemBean = JsonUtil.parseJsonToObject(resultData, BUItemBean.class);
        Type type = new TypeToken<List<DepartmentBean>>() {
        }.getType();
        List<DepartmentBean> list = JsonUtil.parseJsonToObject(resultData, type);
        //结果
        return list;

    }

    public static List<PanDianItemBean> getPanDianBeanList(String SQL) {
        String resultData = getJsonDataBySQL(SQL);
        if (resultData == null) {
            return null;
        }
        //变成List
//        List<JsonObject> ojsonObjectList = ConvertJstring2List(resultData);
//        BUItemBean buItemBean = JsonUtil.parseJsonToObject(resultData, BUItemBean.class);
        Type type = new TypeToken<List<PanDianItemBean>>() {
        }.getType();
        List<PanDianItemBean> list = JsonUtil.parseJsonToObject(resultData, type);
        //结果
        return list;

    }

    public static List<ResearchItemBean> getResearchItemBeanList(String SQL) {
        String resultData = getJsonDataBySQL(SQL);
        if (resultData == null) {
            return null;
        }
        //变成List
//        List<JsonObject> ojsonObjectList = ConvertJstring2List(resultData);
//        BUItemBean buItemBean = JsonUtil.parseJsonToObject(resultData, BUItemBean.class);
        Type type = new TypeToken<List<ResearchItemBean>>() {
        }.getType();
        List<ResearchItemBean> list = JsonUtil.parseJsonToObject(resultData, type);
        //结果
        return list;

    }

    private static String getJsonDataBySQL(String SQL) {
        WsResult result = getDataTable(SQL);
        if (result == null) {
            return null;
        }
        if (!result.getResult()) {
            return null;
        }
        String resultData = result.getErrorInfo();
        if (resultData.isEmpty() || resultData.equalsIgnoreCase("null")) {
            return null;
        }
        return resultData;
    }

    public static List<JsonObject> ConvertJstring2List(String js) {
        //变成List
        JsonArray jsonArray = (JsonArray) new JsonParser().parse(js);
        Iterator<JsonElement> iter = jsonArray.iterator();
        List<JsonObject> objs = new ArrayList<JsonObject>();
        while (iter.hasNext()) {
            JsonObject obj = iter.next().getAsJsonObject();
            objs.add(obj);
        }
        return objs;
    }


    public static WsResult getERPObject(String ObjectName, String IDValue) {


        String webMethodName = "op_Get_ERP_Object";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        String crpObjectName = "";
        String crpIDValue = "";
        try {
            crpObjectName = EncryptDecryptUtil.encryptToBase64(ObjectName, key);
            crpIDValue = EncryptDecryptUtil.encryptToBase64(IDValue, key);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        AddPropertyInfo(propertyInfos, "ObjectName", crpObjectName);
        AddPropertyInfo(propertyInfos, "IDValue", crpIDValue);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult result = getWS_Result(obj);

        if (result.getResult()) {
            String js = result.getErrorInfo();
            //变成List
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(js);

            result.setJsonObject(jsonObject);
            /*Iterator<JsonElement> iter = jsonArray.iterator();
            List<JsonObject> objs = new ArrayList<JsonObject>();


            while(iter.hasNext())
            {
                JsonObject jobj = iter.next().getAsJsonObject();
                objs.add(jobj);

            }*/

        }
        return result;
    }


    public static WsResult getShbCommunicationFun(String ObjectName, String FunctionName, Object X[]) {


        String webMethodName = "op_Exe_SHB_Communication_Fun";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        String crpObjectName = "";
        String crpFunctionName = "";
        try {
            crpObjectName = EncryptDecryptUtil.encryptToBase64(ObjectName, key);
            crpFunctionName = EncryptDecryptUtil.encryptToBase64(FunctionName, key);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Gson gson = new Gson();
        String JsonPara = gson.toJson(X);

        AddPropertyInfo(propertyInfos, "ObjectName", crpObjectName);
        AddPropertyInfo(propertyInfos, "FunctionName", crpFunctionName);
        AddPropertyInfo(propertyInfos, "JsonPara", JsonPara);


        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult result = getWS_Result(obj);


        return result;
    }

    public static Bitmap getHRPhoto(int HR_ID) {
        byte[] result = null;
        Bitmap bitmap = null;
        String webMethodName = "op_Get_HR_Photo100";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "HR_ID", HR_ID);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapPrimitive response = null;
        try {
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            byte[] bytes = Base64.decode(response.toString(), Base64.NO_WRAP);
            result = bytes;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                bitmap = ImageDispose.getPicFromBytes(result, options);
            } catch (Exception ex) {
            }
        }
        return bitmap;
    }

    public static WsResult getTryLogin(String name, String pw) {
        String webMethodName = "op_Try_Login";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        String crpName = "";
        String crpPw = "";
        try {
//            String newp = EncryptDecryptUtil.encode(pw);
            crpName = EncryptDecryptUtil.encryptToBase64(name, key);
            crpPw = EncryptDecryptUtil.encryptToBase64(pw, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        AddPropertyInfo(propertyInfos, "HR_Name", crpName);
        AddPropertyInfo(propertyInfos, "PW", crpPw);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);


        //20210421 解决 登录闪退的问题
        if (envelope.bodyIn instanceof SoapFault) {
            WsResult result = new WsResult();
            result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
            result.setResult(false);
            return result;
        } else {
//            SoapObject obj = (SoapObject) envelope.bodyIn;
//            WsResult ws_result = Get_WS_Result(obj);
//            return ws_result;

            SoapObject obj = (SoapObject) envelope.bodyIn;
            WsResult result = new WsResult();
            if (obj != null) {
                int count = obj.getPropertyCount();
                SoapObject obj2;
                for (int i = 0; i < count; i++) {
                    obj2 = (SoapObject) obj.getProperty(i);
                    result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                    if (!result.getResult()) {
                        result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                    } else {
                        result.setID(Long.parseLong(obj2.getProperty("ID").toString()));
//                    result.setHR_NO(obj2.getProperty("HR_NO").toString());
//                    result.setHR_IDCardNO(obj2.getProperty("HR_IDCard_NO").toString());
                    }
                }

            } else {
                result.setResult(false);
                result.setErrorInfo("无法访问服务器，请检查网络连接是否正常");
            }
            return result;
        }




//        return null;
    }

    public static WsResult getHRIDNO() {
        String webMethodName = "getHRIDNO";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "HR_ID", UserSingleton.get().getHRID());
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        WsResult result = new WsResult();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!result.getResult()) {
                    result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    result.setHR_NO(obj2.getProperty("HR_NO").toString());
                    result.setHR_IDCardNO(obj2.getProperty("HR_IDCard_NO").toString());
                }
            }

        } else {
            result.setResult(false);
            result.setErrorInfo("无法访问服务器，请检查网络连接是否正常");
        }
        return result;
    }



    public static WsResult GetSaveFinishedProductCodeDataByMes(String carton) {
        String webMethodName = "GetSaveFinishedProductCodeDataByMes";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "productCode", carton);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        WsResult result = new WsResult();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!result.getResult()) {
                    result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                }
            }

        } else {
            result.setResult(false);
            result.setErrorInfo("无法访问服务器，请检查网络连接是否正常");
        }
        return result;
    }

    //获取Mes数据，只是为了出库，拿 到数量，ps_id信息
    public static WsResult GetSaveFinishedProductCodeDataByMesForSaleOut(String carton) {
        String webMethodName = "GetSaveFinishedProductCodeDataByMesForSaleOut";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "productCode", carton);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        WsResult result = new WsResult();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!result.getResult()) {
                    result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                }
            }

        } else {
            result.setResult(false);
            result.setErrorInfo("无法访问服务器，请检查网络连接是否正常");
        }
        return result;
    }


    public static String getQueryInv(int Bu_ID, int Ac_Type, String F, int PageNi, int NumberInPage) {
        String result = null;
        String webMethodName = "op_Query_Proudct_Inv";
        ArrayList<PropertyInfo> propertyInfoList = new ArrayList<>();

        AddPropertyInfo(propertyInfoList, "Bu_ID", Bu_ID);
        AddPropertyInfo(propertyInfoList, "Ac_Type", Ac_Type);
        AddPropertyInfo(propertyInfoList, "F", F);
        AddPropertyInfo(propertyInfoList, "PageNi", PageNi);
        AddPropertyInfo(propertyInfoList, "NumberInPage", NumberInPage);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);
        SoapPrimitive response = null;

        try {
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            result = response.toString();
        }

        return result;
    }

    public static String getQueryPartInvItem(int Bu_ID, int Item_ID) {
        String result = null;
        String webMethodName = "op_Query_Part_Inv_Item";
        ArrayList<PropertyInfo> propertyInfoList = new ArrayList<>();
        AddPropertyInfo(propertyInfoList, "Bu_ID", Bu_ID);
        AddPropertyInfo(propertyInfoList, "Item_ID", Item_ID);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);
        SoapPrimitive response = null;
        try {
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            result = response.toString();
        }
        return result;
    }

    public static String getQueryProductInvItem(int Bu_ID, int PS_ID) {
        String result = null;
        String webMethodName = "op_Query_Product_Inv_Item";
        ArrayList<PropertyInfo> propertyInfoList = new ArrayList<>();
        AddPropertyInfo(propertyInfoList, "Bu_ID", Bu_ID);
        AddPropertyInfo(propertyInfoList, "PS_ID", PS_ID);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);
        SoapPrimitive response = null;
        try {
            response = (SoapPrimitive) envelope.getResponse();
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
        }
        if (response != null) {
            result = response.toString();
        }
        return result;
    }

    public static WsResult op_Commit_Update_Lot_Description(int operationHRID, int LotID, String Description) {
        String webMethodName = "op_Update_Lot_Description";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "Editor", operationHRID);
        AddPropertyInfo(propertyInfos, "LotID", LotID);
        AddPropertyInfo(propertyInfos, "Description", Description);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        WsResult result = getWS_Result(obj);
        return result;
    }

    public static String getMobileVersion() {
        System.out.println("Polling...");
        String nameSpace = "http://tempuri.org/";
//        String serviceURL = "http://******:8001/ChargeService.asmx";
        String serviceURL = "http://172.16.2.20:8002/WebService.asmx";
//        String methodName = "GetPhoneUnCharged";
        String methodName = "ReadApkVersion";
//        String soapAction = "http://tempuri.org/GetPhoneUnCharged";
        String soapAction = "http://tempuri.org/ReadApkVersion";
        SoapObject request = new SoapObject(nameSpace, methodName);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = request;
        envelope.dotNet = true;
        HttpTransportSE ht = new HttpTransportSE(serviceURL);
        ht.debug = false;
        try {
            ht.call(soapAction, envelope);
            if (envelope.getResponse() != null) {
                String result = envelope.getResponse().toString();
                Log.d("result", result);
//                Gson gson = new Gson();
//                List<cha_phonechargedto> ps = gson.fromJson(result, new TypeToken<List<cha_phonechargedto>>() {
//                }.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }


    public static BoxItemEntity op_Check_Commit_Sale_Out_Item_Barcode(String scanContent) {
        String webMethodName = "op_Check_Commit_Sale_Out_Item_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPropertyInfo(propertyInfos, "Bu_ID", UserSingleton.get().getUserInfo().getBu_ID());
        AddPropertyInfo(propertyInfos, "X", scanContent);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        BoxItemEntity box_item = new BoxItemEntity();
        if (obj != null) {
            int count = obj.getPropertyCount();
            SoapObject obj2;
            for (int i = 0; i < count; i++) {
                obj2 = (SoapObject) obj.getProperty(i);
                box_item.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                if (!box_item.getResult()) {
                    box_item.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                } else {
                    fill_box_item(box_item, obj2);
                }
            }
        }
        return box_item;
    }

//    Function op_Commit_Sale_Out_Item(Bu_ID As Integer, Exer As Integer,
//                                     EntityID As Long, EntityName As String,
//                                     Item_ID As Long, IV_ID As Long,
//                                     LotID As Long, LotNo As String,
//                                     Ist_ID As Long, Sub_Ist_ID As Long,
//                                     SMLI_ID As Long, SMM_ID As Long, SMT_ID As Long,
//                                     Qty As String) As Web_Answer


    public static WsResult op_Commit_Sale_Out_Item(int Bu_ID, int Exer, long EntityID, String EntityName,
                                                   long Item_ID, long IV_ID,
                                                   long LotID, String LotNo,
                                                   long Ist_ID, long Sub_Ist_ID,
                                                   long SMLI_ID, long SMM_ID, long SMT_ID,
                                                   String Qty) {

        String webMethodName = "op_Commit_Sale_Out_Item";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("Bu_ID");
//        propertyInfo1.setValue(UserSingleton.get().getUserInfo().getBu_ID());
        propertyInfo1.setValue(Bu_ID);
        propertyInfo1.setType(Integer.class);
        propertyInfos.add(propertyInfo1);
        System.out.println("================================Bu_ID = " + Bu_ID);

        //// TODO: 2019/12/31 indate
        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("Exer");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo2.setValue(Exer);
        propertyInfo2.setType(Long.class);
        propertyInfos.add(propertyInfo2);
        System.out.println("================================Exer = " + UserSingleton.get().getHRID());

        PropertyInfo propertyInfo20 = new PropertyInfo();
        propertyInfo20.setName("EntityID");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo20.setValue(EntityID);
        propertyInfo20.setType(Long.class);
        propertyInfos.add(propertyInfo20);
        System.out.println("================================EntityID = " + EntityID);

        PropertyInfo propertyInfo21 = new PropertyInfo();
        propertyInfo21.setName("EntityName");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo21.setValue(EntityName);
        propertyInfo21.setType(String.class);
        propertyInfos.add(propertyInfo21);
        System.out.println("================================EntityName = " + EntityName);

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("Item_ID");
        propertyInfo3.setValue(Item_ID);
        propertyInfo3.setType(Long.class);
        propertyInfos.add(propertyInfo3);

        System.out.println("================================Item_ID = " + Item_ID);

        //// TODO: 2019/12/31 wc_id  ?
        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName("IV_ID");
        propertyInfo4.setValue(IV_ID);
        propertyInfo4.setType(Long.class);
        propertyInfos.add(propertyInfo4);
        System.out.println("================================IV_ID = " + IV_ID);

        PropertyInfo propertyInfo5 = new PropertyInfo();
        propertyInfo5.setName("LotID");
        propertyInfo5.setValue(LotID);
        propertyInfo5.setType(Long.class);
        propertyInfos.add(propertyInfo5);
        System.out.println("================================LotID = " + LotID);

        PropertyInfo propertyInfo6 = new PropertyInfo();
        propertyInfo6.setName("LotNo");
        propertyInfo6.setValue(LotNo);
        propertyInfo6.setType(String.class);
        propertyInfos.add(propertyInfo6);
        System.out.println("================================LotNo = " + LotNo);
//
        PropertyInfo propertyInfo7 = new PropertyInfo();
        propertyInfo7.setName("Ist_ID");
        propertyInfo7.setValue(Ist_ID);
        propertyInfo7.setType(Long.class);
        propertyInfos.add(propertyInfo7);
        System.out.println("================================Ist_ID = " + Ist_ID);
//
        PropertyInfo propertyInfo8 = new PropertyInfo();
        propertyInfo8.setName("Sub_Ist_ID");
        propertyInfo8.setValue(Sub_Ist_ID);
        propertyInfo8.setType(Long.class);
        propertyInfos.add(propertyInfo8);
        System.out.println("================================Sub_Ist_ID = " + Sub_Ist_ID);


//        long LotID, String LotNo,
//        long Ist_ID, long Sub_Ist_ID,
//        long SMLI_ID, long SMM_ID, long SMT_ID,
//        double Qty, String txtEntity, String txtRecord, String Remark, int WC_ID
        PropertyInfo propertyInfo9 = new PropertyInfo();
        propertyInfo9.setName("SMLI_ID");
        propertyInfo9.setValue(SMLI_ID);
        propertyInfo9.setType(Long.class);
        propertyInfos.add(propertyInfo9);
        System.out.println("================================SMLI_ID = " + SMLI_ID);
//
        PropertyInfo propertyInfo10 = new PropertyInfo();
        propertyInfo10.setName("SMM_ID");
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setValue(Double.valueOf(subProductItemEntity.getQty()));
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setType(Double.class);
//        propertyInfo10.setType(Float.class);
        propertyInfo10.setValue(SMM_ID);
        propertyInfo10.setType(Long.class);
        propertyInfos.add(propertyInfo10);
        System.out.println("================================SMM_ID = " + SMM_ID);

//        ManuLotNo , ManuDate As Date,
        // Ist_ID , Remark , Recorder , RecorderName
        PropertyInfo propertyInfo11 = new PropertyInfo();
        propertyInfo11.setName("SMT_ID");
        propertyInfo11.setValue(SMT_ID);
        propertyInfo11.setType(Long.class);
        propertyInfos.add(propertyInfo11);
        System.out.println("================================SMT_ID = " + SMT_ID);


        PropertyInfo propertyInfo16 = new PropertyInfo();
        propertyInfo16.setName("Qty");
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setValue(Double.valueOf(subProductItemEntity.getQty()));
//        propertyInfo10.setValue(subProductItemEntity.getQty());
//        propertyInfo10.setType(Double.class);
//        propertyInfo10.setType(Float.class);
        propertyInfo16.setValue(Qty);
//        propertyInfo16.setType(Integer.class);
        propertyInfo16.setType(String.class);
        propertyInfos.add(propertyInfo16);
        System.out.println("================================InQty = " + Qty);


        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }

        return null;

    }

    public static WsResult executeAttendance(Map<String, String> map) {
        String webMethodName = "executeAttendance";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("HR_ID");
//        propertyInfo1.setValue(UserSingleton.get().getUserInfo().getBu_ID());
        propertyInfo1.setValue(UserSingleton.get().getHRID());
        propertyInfo1.setType(Integer.class);
        propertyInfos.add(propertyInfo1);
        System.out.println("================================HR_ID = " + UserSingleton.get().getHRID());

        //// TODO: 2019/12/31 indate
        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("HR_Name");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo2.setValue(UserSingleton.get().getHRName());
        propertyInfo2.setType(Long.class);
        propertyInfos.add(propertyInfo2);
        System.out.println("================================HR_Name = " + UserSingleton.get().getHRName());

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("day_span");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo3.setValue(map.get("day_span"));
        propertyInfo3.setType(String.class);
        propertyInfos.add(propertyInfo3);
        System.out.println("================================day_span = " + map.get("day_span"));

        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName("device_id");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo4.setValue(map.get("device_id"));
        propertyInfo4.setType(String.class);
        propertyInfos.add(propertyInfo4);
        System.out.println("================================device_id = " + map.get("device_id"));

        PropertyInfo propertyInfo5 = new PropertyInfo();
        propertyInfo5.setName("latitude");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo5.setValue(map.get("latitude"));
        propertyInfo5.setType(String.class);
        propertyInfos.add(propertyInfo5);
        System.out.println("================================latitude = " + map.get("latitude"));

        PropertyInfo propertyInfo6 = new PropertyInfo();
        propertyInfo6.setName("longitude");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo6.setValue(map.get("longitude"));
        propertyInfo6.setType(String.class);
        propertyInfos.add(propertyInfo6);
        System.out.println("================================longitude = " + map.get("longitude"));

        PropertyInfo propertyInfo7 = new PropertyInfo();
        propertyInfo7.setName("address");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo7.setValue(map.get("address"));
        propertyInfo7.setType(String.class);
        propertyInfos.add(propertyInfo7);
        System.out.println("================================address = " + map.get("address"));

        PropertyInfo propertyInfo8 = new PropertyInfo();
        propertyInfo8.setName("remark");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo8.setValue(map.get("remark"));
        propertyInfo8.setType(String.class);
        propertyInfos.add(propertyInfo8);
        System.out.println("================================remark = " + map.get("remark"));

        PropertyInfo propertyInfo9 = new PropertyInfo();
        propertyInfo9.setName("out");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo9.setValue(map.get("out"));
        propertyInfo9.setType(String.class);
        propertyInfos.add(propertyInfo9);
        System.out.println("================================out = " + map.get("out"));

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }

        return null;
    }


    public static WsResult getAttendanceDetailByDay(String dayDate) {
        String webMethodName = "getAttendanceDetailByDay";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName("HR_ID");
//        propertyInfo1.setValue(UserSingleton.get().getUserInfo().getBu_ID());
        propertyInfo1.setValue(UserSingleton.get().getHRID());
        propertyInfo1.setType(Integer.class);
        propertyInfos.add(propertyInfo1);
        System.out.println("================================HR_ID = " + UserSingleton.get().getHRID());

        //// TODO: 2019/12/31 indate
        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName("dayDate");
//        propertyInfo2.setValue(UserSingleton.get().getHRID());
        propertyInfo2.setValue(dayDate);
        propertyInfo2.setType(String.class);
        propertyInfos.add(propertyInfo2);
        System.out.println("================================dayDate = " + dayDate);



        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        if (envelope != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                WsResult result = new WsResult();
                result.setErrorInfo(((SoapFault) envelope.bodyIn).faultstring);
                result.setResult(false);
                return result;
            } else {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                WsResult ws_result = Get_WS_Result(obj);
                return ws_result;
            }
        }

        return null;
    }
}




