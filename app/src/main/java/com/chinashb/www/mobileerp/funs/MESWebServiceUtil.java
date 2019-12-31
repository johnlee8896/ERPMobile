package com.chinashb.www.mobileerp.funs;

import com.chinashb.www.mobileerp.basicobject.WsResult;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/***
 * @date 创建时间 2019/12/31 8:40
 * @author 作者: xxblwf
 * @description 基于MES的webservice工具类
 */

public class MESWebServiceUtil {
    public static final String IP = "http://223.244.235.164";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String SOAP_ACTION = "http://tempuri.org/WebService/";
//    private static String URL = IP + ":8001/Service.svc";
    private static String URL = IP + ":8001/WebService.svc";

    public static String GetSaveFinishedProductCodeDataByMes(String carton) {
        String webMethodName = "GetSaveFinishedProductCodeDataByMes";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

        AddPrpertyInfo(propertyInfos, "cartonNo", carton);
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        SoapObject obj = (SoapObject) envelope.bodyIn;
        String result = null;
        result = obj.toString();
//        if (obj != null) {
//            int count = obj.getPropertyCount();
//            SoapObject obj2;
//            for (int i = 0; i < count; i++) {
//                obj2 = (SoapObject) obj.getProperty(i);
//                result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
//                if (!result.getResult()) {
//                    result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
//                } else {
//                    result.setID(Long.parseLong(obj2.getProperty("ID").toString()));
//                }
//            }
//
//        } else {
//            result.setResult(false);
//            result.setErrorInfo("无法访问服务器，请检查网络连接是否正常");
//        }
        return result;
    }

    private static SoapSerializationEnvelope invokeSupplierWS(ArrayList<PropertyInfo> propertyInfoList, String webMethName) {
        SoapSerializationEnvelope envelope = null;
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        for (PropertyInfo propertyInfo : propertyInfoList) {
            request.addProperty(propertyInfo);
        }

        // Create envelope
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
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

    private static void AddPrpertyInfo(ArrayList<PropertyInfo> propertyInfos, String Name, Object Value) {
        PropertyInfo propertyInfo = getNewPropertyInfo(Name, Value);
        propertyInfos.add(propertyInfo);
    }

    private static PropertyInfo getNewPropertyInfo(String Name, Object value) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName(Name);
        propertyInfo.setValue(value);
        propertyInfo.setType(value.getClass());
        return propertyInfo;
    }

}
