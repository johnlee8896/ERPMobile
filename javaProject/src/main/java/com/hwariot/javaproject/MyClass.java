package com.hwariot.javaproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MyClass {
    public static void main(String[] args){
//        System.out.print("hello");
//        String content = "V5/B/54/PS/10475/L/191217/LQ/0/Qty/120";
//        int buId = Integer.parseInt(getParsedString(content,"/B/","/PS/"));
//        int psId =  Integer.parseInt(getParsedString(content,"/PS/","/L/"));
//        String lotNo = getParsedString(content,"/L/","/LQ/");
//        int qty = Integer.parseInt(getParsedString(content,"/Qty/",""));
//        System.out.print("buid = " + buId);
//        System.out.print("psId = " + psId);
//        System.out.print("lotNo = " + lotNo);
//        System.out.print("qty = " + qty);
        String content = "/HRID/21618/HRNO/201710211";
        String dateContent = "/Date(1720540800000+0800)/";
//        Date(1720540800000+0800)
        int index = dateContent.indexOf("+0800");
        System.out.println(dateContent.substring(5,index));
//        getParsedScannedString(content);

//        getSaveMeeting();
//        getSaveMeeting12();
        getUpdateMeeting();
    }

    private static String getParsedString(String code,String part,String nextPart){
        if (!nextPart.isEmpty()){
            int p = code.indexOf(part) + part.length();
            int q = code.indexOf(nextPart);
            return code.substring(p,q );
        }else{
            int p = code.indexOf(part) + part.length();
            return code.substring(p);
        }
    }

    private static void getParsedScannedString(String contents){
        if (contents != null && contents.length() > 10 && contents.startsWith("/")){
            String[] splits = contents.split("/");
            if (splits.length == 5){
                String hrId = splits[2];
                String hrNO = splits[4];
                System.out.println("hrid=" + hrId +" hrno=" + hrNO);
            }else{
                System.out.println("error1");
            }
        }else{
            System.out.println("error2");
        }

    }


//    public static void test19() {
//
//
//
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//// HttpPost httpPost = new HttpPost("http://172.16.1.248/index/saveMeeting");
//        HttpPost httpPost = new HttpPost("http://172.16.1.37/index/saveMeeting");
//
//        try {
//            String json = "{\"name\":\"cesjo\",\"tempHost\":\"admin1\",\"startDate\":\"2024-09-05 15:00:00\",\"endDate\":\"2024-09-05 16:00:00\",\"outRoomId\":\"15\",\"outId\":\"13\"}";
//            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
//            httpPost.setEntity(entity);
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");
//
//            CloseableHttpResponse response = httpClient.execute(httpPost);
//            try {
//                System.out.println("response :" + response.getStatusLine());
//            } finally {
//// response.close();
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//// try {
////// httpClient.close();
//// } catch (IOException e) {
//// e.printStackTrace();
//// }
//        }
//    }

    private static void getSaveMeeting (){
        URL url = null;
        try {
//            url = new URL("http://172.16.1.37/index/saveMeeting?name=206&tempHost=ceshi&startDate=2024-09-05%2020:55:00&endDate=2024-09-05%2021:15:00&outRoomId=19&outId=404");
            url = new URL("http://116.236.16.218:8008/index/saveMeeting?name=206&tempHost=ceshi&startDate=2024-09-05%2020:55:00&endDate=2024-09-05%2021:15:00&outRoomId=19&outId=404");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            String message = conn.getResponseMessage();
// 获取响应码
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode + " message :" + message);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void getSaveMeeting12 (){
        URL url = null;
        try {
            url = new URL("http://116.236.16.218:8008/index/saveMeeting?name=206&tempHost=ceshi&startDate=2024-09-05%2020:55:00&endDate=2024-09-05%2021:15:00&outRoomId=19&outId=410");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            String message = conn.getResponseMessage();
// 获取响应码
            int responseCode = conn.getResponseCode();
            StringBuffer stringBuffer = new StringBuffer();
            if (responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputline;

                while ((inputline = in.readLine()) != null){
                    stringBuffer.append(inputline);

                }
                in.close();
                System.out.println("Response Code: " + responseCode + " message :" + message + " parsed message = " + stringBuffer.toString());
            }else{
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputline;

                while ((inputline = in.readLine()) != null){
                    stringBuffer.append(inputline);

                }
                in.close();
                System.out.println("Response Code: " + responseCode + " message :" + message + " parsed message = " + stringBuffer.toString());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void getUpdateMeeting(){
        URL url = null;
        try {
            url = new URL("http://116.236.16.218:8008/index/updateMeeting?name=Update改&tempHost=ceshi&startDate=2024-09-06%2020:51:00&endDate=2024-09-05%2021:11:00&outRoomId=19&outId=410");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            String message = conn.getResponseMessage();
// 获取响应码
            int responseCode = conn.getResponseCode();
            StringBuffer stringBuffer = new StringBuffer();
            if (responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputline;

                while ((inputline = in.readLine()) != null){
                    stringBuffer.append(inputline);

                }
                in.close();
                System.out.println("Response Code: " + responseCode + " message :" + message + " parsed message = " + stringBuffer.toString());
            }else{
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputline;

                while ((inputline = in.readLine()) != null){
                    stringBuffer.append(inputline);

                }
                in.close();
                System.out.println("Response Code: " + responseCode + " message :" + message + " parsed message = " + stringBuffer.toString());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
