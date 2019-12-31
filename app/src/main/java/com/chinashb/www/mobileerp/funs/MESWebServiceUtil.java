package com.chinashb.www.mobileerp.funs;

import com.chinashb.www.mobileerp.basicobject.WsResult;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
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
    //    public static final String IP = "http://223.244.235.164";
    public static final String IP = "http://116.236.16.218";
    private static String NAMESPACE = "http://tempuri.org/";
    //    private static String SOAP_ACTION = "http://tempuri.org/MES_Service/";
//    private static String SOAP_ACTION = "http://tempuri.org/WebService/";
    private static String SOAP_ACTION = "http://tempuri.org/IService/";
    //    private static String SOAP_ACTION = "http://tempuri.org/WebService?wsdl";
//    private static String SOAP_ACTION = "http://tempuri.org/";
//    private static String SOAP_ACTION = "http://tempuri.org/GetSaveFinishedProductCodeDataByMes";
    private static String URL = IP + ":8001/Service.svc";
//    private static String URL = IP + ":8001/WebService.asmx";

    public static WsResult GetSaveFinishedProductCodeDataByMes(String carton) {
        String webMethodName = "GetSaveFinishedProductCodeDataByMes";
        ArrayList<PropertyInfo> propertyInfos = new ArrayList<>();

//        AddPrpertyInfo(propertyInfos, "cartonNo", carton);
        AddPrpertyInfo(propertyInfos, "productCode", carton);
//        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
//        String result = null;
//        SoapObject obj = (SoapObject) envelope.bodyIn;
//        if (envelope.bodyIn != null){
//            if (envelope.bodyIn instanceof SoapFault){
//                result = ((SoapFault)envelope.bodyIn).faultstring;
//            }else if (envelope.bodyIn instanceof SoapObject) {
//                SoapObject obj = (SoapObject) envelope.bodyIn;
//            }
//        }
        SoapSerializationEnvelope envelope = invokeSupplierWS(propertyInfos, webMethodName);
        WsResult result = new WsResult();
        String resultString ;
        if (envelope.bodyIn != null) {
            if (envelope.bodyIn instanceof SoapFault) {
                resultString = ((SoapFault) envelope.bodyIn).faultstring;
                result.setResult(true);
                result.setErrorInfo(resultString);
            } else if (envelope.bodyIn instanceof SoapObject) {
                SoapObject obj = (SoapObject) envelope.bodyIn;
                int count = obj.getPropertyCount();
                SoapObject obj2 = (SoapObject) obj.getProperty(0);
                result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
                result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
            }
        }
//        SoapObject obj = (SoapObject) envelope.bodyIn;
//        if (obj != null) {
//            int count = obj.getPropertyCount();
//            SoapObject obj2 = (SoapObject) obj.getProperty(0);
//            result.setResult(Boolean.parseBoolean(obj2.getProperty("Result").toString()));
//            result.setErrorInfo(obj2.getProperty("ErrorInfo").toString());
//
//        }

//        return result;
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

//    public static String getService(String user) {
//        java.net.URL url = null;
//        try {
//            url = new URL(
//                    "http://192.168.0.100:8080/ca3/services/caSynrochnized");
//        } catch (MalformedURLException mue) {
//            return mue.getMessage();
//        }
//        // This is the main SOAP object
//        Call soapCall = new Call();
//        // Use SOAP encoding
//        soapCall.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
//        // This is the remote object we're asking for the price
//        soapCall.setTargetObjectURI("urn:xmethods-caSynrochnized");
//        // This is the name of the method on the above object
//        soapCall.setMethodName("getUser");
//        // We need to send the ISBN number as an input parameter to the method
//        Vector soapParams = new Vector();
//
//        // name, type, value, encoding style
//        Parameter isbnParam = new Parameter("userName", String.class, user,
//                null);
//        soapParams.addElement(isbnParam);
//        soapCall.setParams(soapParams);
//        try {
//            // Invoke the remote method on the object
//            Response soapResponse = soapCall.invoke(url, "");
//            // Check to see if there is an error, return "N/A"
//            if (soapResponse.generatedFault()) {
//                Fault fault = soapResponse.getFault();
//                String f = fault.getFaultString();
//                return f;
//            } else {
//                // read result
//                Parameter soapResult = soapResponse.getReturnValue();
//                // get a string from the result
//                return soapResult.getValue().toString();
//            }
//        } catch (SOAPException se) {
//            return se.getMessage();
//        }
//    }


}
