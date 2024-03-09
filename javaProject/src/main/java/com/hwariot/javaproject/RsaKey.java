package com.hwariot.javaproject;

/***
 * @date 创建时间 2023/7/5 4:39 PM
 * @author 作者: liweifeng
 * @description
 */
import java.math.BigInteger;

import java.util.Scanner;

import java.security.KeyFactory;

import java.security.PrivateKey;

import java.security.KeyPair;

import java.security.KeyPairGenerator;

import java.security.PublicKey;

import java.security.interfaces.RSAPrivateKey;

import java.security.interfaces.RSAPublicKey;

import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import sun.misc.*;

public class RsaKey {

    public static void main(String[] args) throws Exception {

//生成公私钥对

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");

        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

//将公钥和模进行Base64编码

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        RSAPublicKeySpec publicSpec= keyFactory.getKeySpec(publicKey,RSAPublicKeySpec.class);

        BigInteger modulus = publicSpec.getModulus();

        BigInteger exponent=publicSpec.getPublicExponent();

        byte[] ary_m=modulus.toByteArray();//注意：对公钥和模进行Base64编码时，不是对BigInteger对应的字符串编码，而是对其内部 的字节数组进行编码

        byte[] ary_e=exponent.toByteArray();

        String str_m;

        String str_e;

        if(ary_m[0]==0 && ary_m.length==129)//判断数组首元素是否为0，若是，则将其删除，保证模的位数是128

        {

            byte[] temp=new byte[ary_m.length-1];

            for(int i=1;i< 129;i++)

            {

                temp[i-1]=ary_m[i];

            }

            str_m=(new BASE64Encoder()).encodeBuffer(temp);

        }

        else

        {

            str_m=(new BASE64Encoder()).encodeBuffer(ary_m);

        }

        str_e=(new BASE64Encoder()).encodeBuffer(ary_e);

        System.out.println("公钥为："+str_e);

        System.out.println("模为："+str_m);

        System.out.println("运行.NET程序，用所提供的公钥和模进行加密，然后将加密结果输入本程序进行解密：");

        Scanner sc=new Scanner(System.in);

        String str_en="";

        String st="";

//        while(!(st=sc.nextLine()).equals(""))
//
//        {
//
//            str_en+=st;
//
//        }
        str_en = "123";

        byte[] ary_en=(new BASE64Decoder()).decodeBuffer(str_en);

//解密

//注意Cipher初始化时的参数“RSA/ECB/PKCS1Padding”,代表和.NET用相同的填充算法，如果是标准RSA加密，则参数为“RSA”

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] deBytes = cipher.doFinal(ary_en);

        String s = new String(deBytes );

        System.out.println("解密结果为：" + s);

    }

}

