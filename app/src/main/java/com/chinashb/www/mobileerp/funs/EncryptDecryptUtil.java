package com.chinashb.www.mobileerp.funs;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.commons.codec.binary.Base64;


public class EncryptDecryptUtil {

    private static final String ALGORITHM = "DESede";

    //默认为 DESede/ECB/PKCS5Padding
    private static final String CIPHER_TRANSFORMAT = "DESede/ECB/PKCS5Padding";

    private static final String ENCODING = "UTF-8";

    public static String encryptToBase64(String plainText, String key) throws Exception {
        SecretKey deskey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher c1 = Cipher.getInstance(CIPHER_TRANSFORMAT);
        c1.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] result = c1.doFinal(plainText.getBytes(ENCODING));
        //return Base64.encodeBase64String(result);

//
        System.out.println("********encryptToBase64 plainText = " + plainText + " key = " + key);
        System.out.println("********encryptToBase64 result = " + Base64.encodeToString(result,Base64.DEFAULT));
        return Base64.encodeToString(result,Base64.DEFAULT);
    }

    public static String decryptFromBase64(String base64, String key) throws Exception {
        SecretKey deskey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher c1 = Cipher.getInstance(CIPHER_TRANSFORMAT);
        c1.init(Cipher.DECRYPT_MODE, deskey);
        //byte[] result = c1.doFinal(Base64.decodeBase64(base64));
        byte[] result = c1.doFinal(Base64.decode(base64,Base64.DEFAULT));
        return new String(result, ENCODING);
    }

    //作测试用的 另一种方法

    // 密钥
//    private final static String secretKey = "liuyunqiang@lx100$#365#$";
    private final static String secretKey = "Money_For_GodMoneyForGod";
    // 向量
//    private final static String iv = "01234567";
    private final static String iv = "01234567";
    // 加解密统一使用的编码方式
    private final static String encoding = "utf-8";

    public static String encode(String plainText) throws Exception {
//        SecretKey deskey = new SecretKeySpec(key.getBytes(), ALGORITHM);
//        Cipher c1 = Cipher.getInstance(CIPHER_TRANSFORMAT);
//        c1.init(Cipher.ENCRYPT_MODE, deskey);
//        byte[] result = c1.doFinal(plainText.getBytes(ENCODING));
//        //return Base64.encodeBase64String(result);
//
////
//        System.out.println("********encryptToBase64 plainText = " + plainText + " key = " + key);
//        System.out.println("********encryptToBase64 result = " + Base64.encodeToString(result,Base64.DEFAULT));
//        return Base64.encodeToString(result,Base64.DEFAULT);





        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
//        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(ALGORITHM);
        deskey = keyfactory.generateSecret(spec);

//        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMAT);
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
//        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        System.out.println("********encode result = " + Base64.encodeToString(encryptData,Base64.DEFAULT ));
//        return Base64.encode(encryptData);
        return Base64.encodeToString(encryptData,Base64.DEFAULT );
    }







}