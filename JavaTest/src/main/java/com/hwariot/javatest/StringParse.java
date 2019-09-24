package com.hwariot.javatest;

/***
 * @date 创建时间 2019/7/11 2:55 PM
 * @author 作者: liweifeng
 * @description
 */
public class StringParse {
    public static void main(String[] args){
//        V9／MI／6052103／S／3504／IV／35306／P／C131－4100 A1／D／20190710／L／19071001／N／9／Q／505
//        V9/MI/6052103/S/3504/IV/35306/P/C131-4100 A1/D/20190710/L/19071001/N/9/Q/505
        String str1 = "V9／MI／6052103／S／3504／IV／35306／P／C131－4100 A1／D／20190710／L／19071001／N／9／Q／505";
        String str2 = "V9/MI/6052103/S/3504/IV/35306/P/C131-4100 A1/D/20190710/L/19071001/N/9/Q/505";
        if (str1.contains("／")){
            str1 = str1.replace("／","/");
        }
        System.out.println(str1);
        System.out.println(str2);
        if (str1.equals(str2)){
            System.out.println("str1 equals str2");
        }

    }
}
