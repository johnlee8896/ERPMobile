package com.hwariot.javaproject;


/***
 * @date 创建时间 2023/7/6 10:37 AM
 * @author 作者: liweifeng
 * @description
 */
public class WeatherInterfaceImpl implements WeatherInterface{
    @Override
    public String queryWeather(String cityName) {
        System.out.println("获取城市名" + cityName);
        String weather = "Rain";
        return weather;
    }
}
