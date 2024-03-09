package com.hwariot.javaproject;

import javax.xml.ws.Endpoint;

/***
 * @date 创建时间 2023/7/6 10:38 AM
 * @author 作者: liweifeng
 * @description
 */
public class WeatherServer {
    public static void main(String[] args) {
        Endpoint.publish("http://127.0.0.1:12345/weather",new WeatherInterfaceImpl());
    }
}
