package com.chinashb.www.mobileerp.funs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.chinashb.www.mobileerp.basicobject.BoxItemEntity;
import com.chinashb.www.mobileerp.basicobject.Issued_Item;
import com.chinashb.www.mobileerp.basicobject.IstPlaceEntity;
import com.chinashb.www.mobileerp.basicobject.JUser;
import com.chinashb.www.mobileerp.basicobject.MpiWcBean;
import com.chinashb.www.mobileerp.basicobject.UserInfoEntity;
import com.chinashb.www.mobileerp.basicobject.WsResult;
import com.chinashb.www.mobileerp.bean.BUItemBean;
import com.chinashb.www.mobileerp.bean.DepartmentBean;
import com.chinashb.www.mobileerp.bean.ResearchItemBean;
import com.chinashb.www.mobileerp.singleton.UserSingleton;
import com.chinashb.www.mobileerp.utils.JsonUtil;
import com.chinashb.www.mobileerp.utils.ToastUtil;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.chinashb.www.mobileerp.funs.CommonUtil.isNothing2String;

//这里的T只是为了getcommonbean方法使用
public class WebServiceUtil {

    public static final Class<BUItemBean> bu = BUItemBean.class;
    public static String Current_Net_Link = "Intranet";

    private static String NAMESPACE = "http://tempuri.org/";
    private static String URL = "http://172.16.1.80:8100/Test_Wss/Service.svc";
    //    private static String URL = "http://172.16.1.43:8100/Test_Wss/Service.svc";
    private static String URL_Intranet = "http://172.16.1.80:8100/Test_Wss/Service.svc";
    //    private static String URL_Intranet = "http://172.16.1.43:8100/Test_Wss/Service.svc";
    private static String URL_Internet = "http://180.167.56.250:8100/Test_Wss/Service.svc";
    private static String SOAP_ACTION = "http://tempuri.org/IService/";

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


    private static SoapSerializationEnvelope invokeSupplierWS(ArrayList<PropertyInfo> propertyInfoList, String webMethName) {
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
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug = true;

        try {
            // Invole web service
            androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
            // Get the response
            envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
//			resTxt = "Error occured";
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
                    box_item.setDIII_ID(Long.parseLong(obj2.getProperty("DIII_ID").toString()));
                    box_item.setSMT_ID(Long.parseLong(obj2.getProperty("SMT_ID").toString()));
                    box_item.setSMM_ID(Long.parseLong(obj2.getProperty("SMM_ID").toString()));
                    box_item.setSMLI_ID(Long.parseLong(obj2.getProperty("SMLI_ID").toString()));
                    box_item.setEntityID(Long.parseLong(obj2.getProperty("EntityID").toString()));
                    box_item.setEntityName(obj2.getProperty("EntityName").toString());
                    box_item.setLotID(Long.parseLong(obj2.getProperty("LotID").toString()));
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
                    istPlace.setIstName(obj2.getProperty("IstName").toString());
                    istPlace.setBu_ID(Integer.parseInt(obj2.getProperty("Bu_ID").toString()));
                    istPlace.setBuName(obj2.getProperty("BuName").toString());
                }
            }
        }
        return istPlace;
    }

    public static WsResult op_Commit_DS_Item(BoxItemEntity box_item) {
        String webMethodName = "op_Commit_DS_Item_Income_To_Warehouse";
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
        propertyInfo2.setType(Long.class);
        propertyInfos.add(propertyInfo2);

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName("Ist_ID");
        propertyInfo3.setValue(box_item.getIst_ID());
        propertyInfo3.setType(Long.class);
        propertyInfos.add(propertyInfo3);

        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName("Sub_Ist_ID");
        propertyInfo4.setValue(box_item.getSub_Ist_ID());
        propertyInfo4.setType(Long.class);
        propertyInfos.add(propertyInfo4);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult ws_result = Get_WS_Result(obj);


        return ws_result;

    }


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


    //获取已经投料的内容
    public static List<Issued_Item> opGetMWIssedItems(Long mw_id) {
        String webMethodName = "op_Get_MW_Issued_Items";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("MW_ID");
        propertyInfo.setValue(mw_id);
        propertyInfo.setType(String.class);

        propertyInfos.add(propertyInfo);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        List<Issued_Item> result;
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

                    Issued_Item im = new Issued_Item();

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
        String webMethodName = "op_Check_Commit_MW_Issue_Item_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPrpertyInfo(propertyInfos, "MW_ID", MW_ID);
        AddPrpertyInfo(propertyInfos, "X", X);

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

        AddPrpertyInfo(propertyInfos, "MW_ID", MW_ID);
        AddPrpertyInfo(propertyInfos, "X", X);

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

    public static BoxItemEntity op_Check_Commit_Inv_Out_Item_Barcode(int Bu_ID, String X) {
        String webMethodName = "op_Check_Commit_Inv_Out_Item_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPrpertyInfo(propertyInfos, "Bu_ID", Bu_ID);
        AddPrpertyInfo(propertyInfos, "X", X);

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

    public static void fill_box_item(BoxItemEntity box_item, SoapObject obj) {
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

    public static BoxItemEntity op_Check_Commit_WC_Return_Item_Barcode(String X) {
        String webMethodName = "op_Check_Commit_WC_Return_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPrpertyInfo(propertyInfos, "X", X);

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

    private static PropertyInfo getNewPrpertyInfo(String Name, Object Value) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName(Name);
        propertyInfo.setValue(Value);
        propertyInfo.setType(Value.getClass());
        return propertyInfo;
    }

    public static void AddPrpertyInfo(ArrayList<PropertyInfo> propertyInfos, String Name, Object Value) {
        PropertyInfo propertyInfo = getNewPrpertyInfo(Name, Value);
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

        AddPrpertyInfo(propertyInfos, "MW_ID", MW_ID);
        AddPrpertyInfo(propertyInfos, "Exer", Sender);
        AddPrpertyInfo(propertyInfos, "Item_ID", Item_ID);
        AddPrpertyInfo(propertyInfos, "IV_ID", IV_ID);
        AddPrpertyInfo(propertyInfos, "LotID", LotID);
        AddPrpertyInfo(propertyInfos, "LotNo", LotNo);
        AddPrpertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPrpertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPrpertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);
        AddPrpertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPrpertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPrpertyInfo(propertyInfos, "Qty", Qty);


        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult ws_result = Get_WS_Result(obj);

        return ws_result;
    }

    public static WsResult op_Commit_MW_Issue_Extra_Item(Long MW_ID, BoxItemEntity bi) {
        String sqty = String.valueOf(bi.getQty());
//        WsResult Result = op_Commit_MW_Issue_Extra_Item(MW_ID, UserInfoEntity.ID, bi.getItem_ID(), bi.getIV_ID(), bi.getLotID(), bi.getLotNo(), bi.getIst_ID(), bi.getSub_Ist_ID(), bi.getSMLI_ID(), bi.getSMM_ID(), bi.getSMT_ID(), sqty);
        WsResult Result = op_Commit_MW_Issue_Extra_Item(MW_ID, UserSingleton.get().getHRID(), bi.getItem_ID(), bi.getIV_ID(), bi.getLotID(), bi.getLotNo(), bi.getIst_ID(), bi.getSub_Ist_ID(), bi.getSMLI_ID(), bi.getSMM_ID(), bi.getSMT_ID(), sqty);
        return Result;
    }

    public static WsResult op_Commit_MW_Issue_Extra_Item(Long MW_ID, int Sender, Long Item_ID, Long IV_ID, Long LotID, String LotNo, Long Ist_ID, Long Sub_Ist_ID, Long SMLI_ID, Long SMM_ID, Long SMT_ID, String Qty) {
        String webMethodName = "op_Commit_MW_New_Issue_Extra_Item";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPrpertyInfo(propertyInfos, "MW_ID", MW_ID);
        AddPrpertyInfo(propertyInfos, "Exer", Sender);
        AddPrpertyInfo(propertyInfos, "Item_ID", Item_ID);
        AddPrpertyInfo(propertyInfos, "IV_ID", IV_ID);
        AddPrpertyInfo(propertyInfos, "LotID", LotID);
        AddPrpertyInfo(propertyInfos, "LotNo", LotNo);
        AddPrpertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPrpertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPrpertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);
        AddPrpertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPrpertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPrpertyInfo(propertyInfos, "Qty", Qty);

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
//        AddPrpertyInfo(propertyInfos, "Bu_ID", Bu_ID);
//        AddPrpertyInfo(propertyInfos, "Exer", UserInfoEntity.ID);
//        AddPrpertyInfo(propertyInfos, "EntityID", Long.valueOf(SelectDep.get("Department_ID")));
//        AddPrpertyInfo(propertyInfos, "EntityName", SelectDep.get("Department_Name"));
//        AddPrpertyInfo(propertyInfos, "Fi_Product_Type_ID", Integer.valueOf(SelectReaseach.get("FiPT_ID")));
//        AddPrpertyInfo(propertyInfos, "Fi_Product_Status_ID", Integer.valueOf(SelectReaseach.get("FSPD_ID")));
//        AddPrpertyInfo(propertyInfos, "Out_Product_ID", Long.valueOf(SelectReaseach.get("Product_ID")));
//        AddPrpertyInfo(propertyInfos, "Out_Program_ID", Long.valueOf(SelectReaseach.get("Program_ID")));
//
//        AddPrpertyInfo(propertyInfos, "Item_ID", bi.getItem_ID());
//        AddPrpertyInfo(propertyInfos, "IV_ID", bi.getIV_ID());
//        AddPrpertyInfo(propertyInfos, "LotID", bi.getLotID());
//        AddPrpertyInfo(propertyInfos, "LotNo", bi.getLotNo());
//        AddPrpertyInfo(propertyInfos, "Ist_ID", bi.getIst_ID());
//        AddPrpertyInfo(propertyInfos, "Sub_Ist_ID", bi.getSub_Ist_ID());
//        AddPrpertyInfo(propertyInfos, "SMLI_ID", bi.getSMLI_ID());
//        AddPrpertyInfo(propertyInfos, "SMM_ID", bi.getSMM_ID());
//        AddPrpertyInfo(propertyInfos, "SMT_ID", bi.getSMT_ID());
//
//        AddPrpertyInfo(propertyInfos, "Qty", sqty);
//
//        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
//        SoapObject obj = (SoapObject) envelope.bodyIn;
//
//        WsResult ws_result = Get_WS_Result(obj);
//
//        return ws_result;
//    }

    public static WsResult op_Commit_Dep_Out_Item(int Bu_ID, DepartmentBean departmentBean, ResearchItemBean researchItemBean, BoxItemEntity boxItemEntity, String remark) {
        String sqty = String.valueOf(boxItemEntity.getQty());
        String webMethodName = "op_Commit_Dep_Out_Item";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPrpertyInfo(propertyInfos, "Bu_ID", Bu_ID);
//        AddPrpertyInfo(propertyInfos, "Exer", UserInfoEntity.ID);
        AddPrpertyInfo(propertyInfos, "Exer", UserSingleton.get().getHRID());
        if (departmentBean != null) {
            AddPrpertyInfo(propertyInfos, "EntityID", departmentBean.getDepartmentID());
            AddPrpertyInfo(propertyInfos, "EntityName", departmentBean.getDepartmentName());
        }
        if (researchItemBean != null) {
            AddPrpertyInfo(propertyInfos, "Fi_Product_Type_ID", researchItemBean.getFiPTID());
            AddPrpertyInfo(propertyInfos, "Fi_Product_Status_ID", researchItemBean.getFSPDID());
            AddPrpertyInfo(propertyInfos, "Out_Product_ID", researchItemBean.getProductID());
            AddPrpertyInfo(propertyInfos, "Out_Program_ID", researchItemBean.getProgramID());
        }
        if (boxItemEntity != null) {
            AddPrpertyInfo(propertyInfos, "Item_ID", boxItemEntity.getItem_ID());
            AddPrpertyInfo(propertyInfos, "IV_ID", boxItemEntity.getIV_ID());
            AddPrpertyInfo(propertyInfos, "LotID", boxItemEntity.getLotID());
            AddPrpertyInfo(propertyInfos, "LotNo", boxItemEntity.getLotNo());
            AddPrpertyInfo(propertyInfos, "Ist_ID", boxItemEntity.getIst_ID());
            AddPrpertyInfo(propertyInfos, "Sub_Ist_ID", boxItemEntity.getSub_Ist_ID());
            AddPrpertyInfo(propertyInfos, "SMLI_ID", boxItemEntity.getSMLI_ID());
            AddPrpertyInfo(propertyInfos, "SMM_ID", boxItemEntity.getSMM_ID());
            AddPrpertyInfo(propertyInfos, "SMT_ID", boxItemEntity.getSMT_ID());


        }
        AddPrpertyInfo(propertyInfos, "Qty", sqty);
        AddPrpertyInfo(propertyInfos, "Remark", remark);
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

        AddPrpertyInfo(propertyInfos, "Exer", Sender);
        AddPrpertyInfo(propertyInfos, "DIII_ID", DIII_ID);


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

    public static BoxItemEntity op_Check_Commit_Move_Item_Barcode(String X) {
        String webMethodName = "op_Check_Commit_Inv_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPrpertyInfo(propertyInfos, "X", X);

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
        AddPrpertyInfo(propertyInfos, "Bu_ID", Bu_ID);
        AddPrpertyInfo(propertyInfos, "X", X);

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

        AddPrpertyInfo(propertyInfos, "X", X);

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
        AddPrpertyInfo(propertyInfos, "Exer_Name", Exer_Name);
        AddPrpertyInfo(propertyInfos, "CI_ID", CI_ID);
        AddPrpertyInfo(propertyInfos, "Bu_ID", Bu_ID);
        AddPrpertyInfo(propertyInfos, "X", X);
        AddPrpertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPrpertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPrpertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPrpertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPrpertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);
        AddPrpertyInfo(propertyInfos, "LotID", LotID);
        AddPrpertyInfo(propertyInfos, "Qty", Qty);
        AddPrpertyInfo(propertyInfos, "Remark", remark);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        return getWS_Result(obj);

    }


    public static WsResult op_Commit_Stock_Result_V2(String Exer_Name, int CI_ID, String X,
                                                     Long Ist_ID, Long Sub_Ist_ID, Long SMT_ID, Long SMM_ID, Long SMLI_ID,
                                                     String Qty, String remark) {
        String webMethodName = "op_Commit_Check_Stock_Save_V2";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPrpertyInfo(propertyInfos, "Exer_Name", Exer_Name);
        AddPrpertyInfo(propertyInfos, "CI_ID", CI_ID);

        AddPrpertyInfo(propertyInfos, "X", X);
        AddPrpertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPrpertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPrpertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPrpertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPrpertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);

        AddPrpertyInfo(propertyInfos, "Qty", Qty);
        AddPrpertyInfo(propertyInfos, "Remark", remark);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        return getWS_Result(obj);

    }

    public static WsResult op_Commit_Stock_Result_V4(String Exer_Name, int CI_ID, int Bu_ID, String X,
                                                     Long Ist_ID, Long Sub_Ist_ID, Long SMT_ID, Long SMM_ID, Long SMLI_ID,
                                                     Long LotID, String Qty, String N, String PN, String DQ, String remark) {
        String webMethodName = "op_Commit_Check_Stock_Save_V4";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPrpertyInfo(propertyInfos, "Exer_Name", Exer_Name);
        AddPrpertyInfo(propertyInfos, "CI_ID", CI_ID);
        AddPrpertyInfo(propertyInfos, "Bu_ID", Bu_ID);
        AddPrpertyInfo(propertyInfos, "X", X);
        AddPrpertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPrpertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPrpertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPrpertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPrpertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);
        AddPrpertyInfo(propertyInfos, "LotID", LotID);
        AddPrpertyInfo(propertyInfos, "Qty", Qty);
        AddPrpertyInfo(propertyInfos, "N", N);
        AddPrpertyInfo(propertyInfos, "PN", PN);
        AddPrpertyInfo(propertyInfos, "DQ", DQ);

        AddPrpertyInfo(propertyInfos, "Remark", remark);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        return getWS_Result(obj);

    }


    public static WsResult op_Commit_Stock_Result_V3(String Exer_Name, int CI_ID, String X,
                                                     Long Ist_ID, Long Sub_Ist_ID, Long SMT_ID, Long SMM_ID, Long SMLI_ID,
                                                     String Qty, String N, String PN, String DQ, String remark) {
        String webMethodName = "op_Commit_Check_Stock_Save_V3";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPrpertyInfo(propertyInfos, "Exer_Name", Exer_Name);
        AddPrpertyInfo(propertyInfos, "CI_ID", CI_ID);

        AddPrpertyInfo(propertyInfos, "X", X);
        AddPrpertyInfo(propertyInfos, "Ist_ID", Ist_ID);
        AddPrpertyInfo(propertyInfos, "Sub_Ist_ID", Sub_Ist_ID);
        AddPrpertyInfo(propertyInfos, "SMT_ID", SMT_ID);
        AddPrpertyInfo(propertyInfos, "SMM_ID", SMM_ID);
        AddPrpertyInfo(propertyInfos, "SMLI_ID", SMLI_ID);

        AddPrpertyInfo(propertyInfos, "Qty", Qty);
        AddPrpertyInfo(propertyInfos, "N", N);
        AddPrpertyInfo(propertyInfos, "PN", PN);
        AddPrpertyInfo(propertyInfos, "DQ", DQ);

        AddPrpertyInfo(propertyInfos, "Remark", remark);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;

        return getWS_Result(obj);

    }

    public static BoxItemEntity op_Check_Commit_Freeze_Item_Barcode(String X) {
        String webMethodName = "op_Check_Commit_Inv_Barcode";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();
        AddPrpertyInfo(propertyInfos, "X", X);

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

//        AddPrpertyInfo(propertyInfos, "SenderID", UserInfoEntity.ID);
        AddPrpertyInfo(propertyInfos, "SenderID", UserSingleton.get().getHRID());
        AddPrpertyInfo(propertyInfos, "SMLI_ID", box_item.getSMLI_ID());
        AddPrpertyInfo(propertyInfos, "SMM_ID", box_item.getSMM_ID());
        AddPrpertyInfo(propertyInfos, "SMT_ID", box_item.getSMT_ID());
        AddPrpertyInfo(propertyInfos, "Ist_ID", box_item.getIst_ID());
        AddPrpertyInfo(propertyInfos, "Sub_Ist_ID", box_item.getSub_Ist_ID());

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult result = Get_WS_Result(obj);


        return result;

    }

    public static WsResult op_Commit_Freeze_Inv(BoxItemEntity box_item) {
        String webMethodName = "op_Commit_Freeze_Inv";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

//        AddPrpertyInfo(propertyInfos, "SenderID", UserInfoEntity.ID);
        AddPrpertyInfo(propertyInfos, "SenderID", UserSingleton.get().getHRID());
        AddPrpertyInfo(propertyInfos, "SMLI_ID", box_item.getSMLI_ID());
        AddPrpertyInfo(propertyInfos, "SMT_ID", box_item.getSMT_ID());

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult result = Get_WS_Result(obj);


        return result;

    }

    public static WsResult op_Commit_FreezeNot_Inv(BoxItemEntity box_item) {
        String webMethodName = "op_Commit_FreezeNot_Inv";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

//        AddPrpertyInfo(propertyInfos, "SenderID", UserInfoEntity.ID);
        AddPrpertyInfo(propertyInfos, "SenderID", UserSingleton.get().getHRID());
        AddPrpertyInfo(propertyInfos, "SMLI_ID", box_item.getSMLI_ID());
        AddPrpertyInfo(propertyInfos, "SMT_ID", box_item.getSMT_ID());

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

                result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));

                if (obj2.getProperty("ErrorInfo") != null) {
                    result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
                }

            }

        }

        return result;
    }


    public static WsResult getDataTable(String sql) {
        int maxCount = 1000;
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
            crpSql = DefaultEncryptor.encryptToBase64(sql, key);
        } catch (
                Exception e) {
            e.printStackTrace();
            return null;
        }

        AddPrpertyInfo(propertyInfoList, "CryptSQL", crpSql);

        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfoList, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        result =

                getWS_Result(obj);


        return result;
    }


    public static JsonObject getDataRow(String sql) {

        JsonObject jsonObject = null;

        String result = null;
        String webMethodName = "GetAndroidData";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        String crpSql = "";
        try {
            crpSql = DefaultEncryptor.encryptToBase64(sql, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        AddPrpertyInfo(propertyInfos, "CryptSQL", crpSql);

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
            crpObjectName = DefaultEncryptor.encryptToBase64(ObjectName, key);
            crpIDValue = DefaultEncryptor.encryptToBase64(IDValue, key);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        AddPrpertyInfo(propertyInfos, "ObjectName", crpObjectName);
        AddPrpertyInfo(propertyInfos, "IDValue", crpIDValue);

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
            crpObjectName = DefaultEncryptor.encryptToBase64(ObjectName, key);
            crpFunctionName = DefaultEncryptor.encryptToBase64(FunctionName, key);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Gson gson = new Gson();
        String JsonPara = gson.toJson(X);

        AddPrpertyInfo(propertyInfos, "ObjectName", crpObjectName);
        AddPrpertyInfo(propertyInfos, "FunctionName", crpFunctionName);
        AddPrpertyInfo(propertyInfos, "JsonPara", JsonPara);


        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);

        SoapObject obj = (SoapObject) envelope.bodyIn;

        WsResult result = getWS_Result(obj);


        return result;
    }

    public static Bitmap getHRPhoto(int HR_ID) {


        byte[] result = null;
        Bitmap bm = null;

        String webMethodName = "op_Get_HR_Photo100";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();


        AddPrpertyInfo(propertyInfos, "HR_ID", HR_ID);

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
                bm = ImageDispose.getPicFromBytes(result, options);
            } catch (Exception ex) {
            }
        }


        return bm;
    }

    public static WsResult getTryLogin(String name, String pw) {

        String webMethodName = "op_Try_Login";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        String crpName = "";
        String crpPw = "";
        try {
            crpName = DefaultEncryptor.encryptToBase64(name, key);
            crpPw = DefaultEncryptor.encryptToBase64(pw, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        AddPrpertyInfo(propertyInfos, "HR_Name", crpName);
        AddPrpertyInfo(propertyInfos, "PW", crpPw);

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
                    result.setID(Long.parseLong(obj2.getProperty("ID").toString()));
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

        AddPrpertyInfo(propertyInfoList, "Bu_ID", Bu_ID);
        AddPrpertyInfo(propertyInfoList, "Ac_Type", Ac_Type);
        AddPrpertyInfo(propertyInfoList, "F", F);
        AddPrpertyInfo(propertyInfoList, "PageNi", PageNi);
        AddPrpertyInfo(propertyInfoList, "NumberInPage", NumberInPage);

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
        AddPrpertyInfo(propertyInfoList, "Bu_ID", Bu_ID);
        AddPrpertyInfo(propertyInfoList, "Item_ID", Item_ID);
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
        AddPrpertyInfo(propertyInfoList, "Bu_ID", Bu_ID);
        AddPrpertyInfo(propertyInfoList, "PS_ID", PS_ID);
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

    public static WsResult op_Commit_Update_Lot_Description(int Oper, int LotID, String Description) {
        String webMethodName = "op_Update_Lot_Description";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPrpertyInfo(propertyInfos, "Editor", Oper);
        AddPrpertyInfo(propertyInfos, "LotID", LotID);
        AddPrpertyInfo(propertyInfos, "Description", Description);

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

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
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

//    public static String invokeRemoteAddr(String wsdl, String method,
//                                          Object[] objects) throws Exception {
//        Provider.Service service = new Service();
//        Call call;
//        try {
//            call = (Call) service.createCall();
//            call.setSOAPActionURI("http://tempuri.org/receiveDataTask");
//            call.setTargetEndpointAddress(wsdl);
//            call.setOperationName(method);// WSDL里面描述的接口名称
//            call.addParameter("orig",
//                    org.apache.axis.encoding.XMLType.XSD_STRING,
//                    javax.xml.rpc.ParameterMode.IN);// 接口的参数
//            call.addParameter("sign",
//                    org.apache.axis.encoding.XMLType.XSD_STRING,
//                    javax.xml.rpc.ParameterMode.IN);// 接口的参数
//            call.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);// 设置返回类型
//            call.invoke(objects);
//            return "end";
//        } catch (ServiceException e) {
//            throw new Exception(e);
//        }
//
//
//    }
}




