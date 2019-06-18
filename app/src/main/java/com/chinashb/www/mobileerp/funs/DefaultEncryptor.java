package com.chinashb.www.mobileerp.funs;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
//import org.apache.commons.codec.binary.Base64;
import android.util.Base64;


public class DefaultEncryptor {

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
}