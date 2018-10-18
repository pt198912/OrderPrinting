package com.order.print.util;

import android.util.Log;

import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * Created by pt198 on 17/10/2018.
 */

public class DesUtils {
    private final static String ALGORITHM = "DES";
    private static final String TAG = "DesUtils";
    public static final String password = "UTL2011L";

    public static void test() {
        //密码，长度要是8的倍数

//        String origin="2018-11-01 00:00:00";
        String origin="B15B72E8D2197C648E601628CD77097C102F64274DC551D0";
        //直接将如上内容解密
        try {
//            String enc=en(origin);
//            Log.d(TAG, "test: enc "+enc);

//            String decryResult = decryptor(origin);
            String res=DecodeDES(origin,password);
            Log.d(TAG,"解密后：" + res);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    // 解密数据
    /**
     * DES解密
     * @param message
     * @param key
     * @return
     * @throws Exception
     *
     * lee on 2016-12-26 00:28:18
     */
    public static String DecodeDES(String message, String key) throws Exception {

        byte[] bytesrc = convertHexString(message);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    /**
     * DES加密
     * @param message
     * @param key
     * @return
     * @throws Exception
     *
     * lee on 2016-12-26 00:28:28
     */
    public static byte[] EncodeDES(String message, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    /**
     * MD5加密
     * 使用org.apache.commons.codec.digest.DigestUtils加密
     * http://commons.apache.org/proper/commons-codec/download_codec.cgi
     * @return
     *
     * lee on 2016-12-26 00:28:28
     */
//    public static String EncodeMD5(String message) {
//        return DigestUtils.md5Hex(message);
//    }


    public static byte[] convertHexString(String ss) {
        byte digest[] = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }
        return digest;
    }

    public static String toHexString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2)
                plainText = "0" + plainText;
            hexString.append(plainText);
        }
        return hexString.toString();
    }


}