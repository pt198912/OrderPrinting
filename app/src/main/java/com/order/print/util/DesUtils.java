package com.order.print.util;

import android.util.Log;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * Created by pt198 on 17/10/2018.
 */

public class DesUtils {
    private final static String ALGORITHM = "DES";
    private static final String TAG = "DesUtils";
    static final String password = "UTL2011LEO";
    public static void test1(){
        //待加密内容
        String str = "task_id=TSK_000000006870&ledger_id=0715-5572";

        String result = encrypt(str);

        BASE64Encoder base64en = new BASE64Encoder();
//        String strs = new String(base64en.encode(result));

        System.out.println("加密后："+result);
        //直接将如上内容解密
        try {
            String decryResult = decryptor(result);
            System.out.println("解密后："+new String(decryResult));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    public static void test() {
        //密码，长度要是8的倍数

//        String origin="2018-11-01 00:00:00";
        String origin="VmyuXZ/BeWh2Oj0sUGiZ06L8kQn3u2Vp";
        //直接将如上内容解密
        try {
//            String enc=en(origin);
//            Log.d(TAG, "test: enc "+enc);

            String decryResult = decryptor(origin);
            Log.d(TAG,"解密后：" + decryResult);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    /**
     * DES解密算法
     * @param data
     * @param cryptKey  密钥 要是偶数
     * @return
     * @throws Exception
     */
//    public static String decrypt(String data, String cryptKey) throws Exception {
//        return new String(decrypt(hex2byte(data.getBytes()),
//                cryptKey.getBytes()));
//
////        return new String(decrypt(decoder.decodeBuffer(data), cryptKey.getBytes()));
//    }
//    public static String decryptBase64(String data){
//        BASE64Decoder decoder = new BASE64Decoder();
//        Log.d(TAG, "decrypt: ");
//        try {
//            return new String(decoder.decodeBuffer(data));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    public static String encryptBase64(String data){
//        BASE64Encoder encoder = new BASE64Encoder();
//
//        return new String(encoder.encode(data.getBytes()));
//
//    }
//    /**
//     * DES加密算法
//     * @param data
//     * @param cryptKey
//     * @return
//     * @throws Exception
//     */
//    public final static String encrypt(String data, String cryptKey)
//            throws Exception {
//        return byte2hex(encrypt(data.getBytes(), cryptKey.getBytes()));
//
//    }
//
//    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
//        // DES算法要求有一个可信任的随机数源
//        SecureRandom sr = new SecureRandom();
//        // 从原始密匙数据创建DESKeySpec对象
//        DESKeySpec dks = new DESKeySpec(key);
//        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
//        // 一个SecretKey对象
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
//        SecretKey securekey = keyFactory.generateSecret(dks);
//        // Cipher对象实际完成加密操作
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        // 用密匙初始化Cipher对象
//        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
//        // 现在，获取数据并加密
//        // 正式执行加密操作
//        return cipher.doFinal(data);
//    }
//
//    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
//        // DES算法要求有一个可信任的随机数源
//        SecureRandom sr = new SecureRandom();
//        // 从原始密匙数据创建一个DESKeySpec对象
//        DESKeySpec dks = new DESKeySpec(key);
//        // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
//        // 一个SecretKey对象
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
//        SecretKey securekey = keyFactory.generateSecret(dks);
//        // Cipher对象实际完成解密操作
//        Cipher cipher = Cipher.getInstance(ALGORITHM);
//        // 用密匙初始化Cipher对象
//        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
//        // 现在，获取数据并解密
//        // 正式执行解密操作
//        return cipher.doFinal(data);
//    }
//
//    private static byte[] hex2byte(byte[] b) {
//        if ((b.length % 2) != 0)
//            throw new IllegalArgumentException("长度不是偶数");
//        byte[] b2 = new byte[b.length / 2];
//        for (int n = 0; n < b.length; n += 2) {
//            String item = new String(b, n, 2);
//            b2[n / 2] = (byte) Integer.parseInt(item, 16);
//        }
//        return b2;
//    }
//
//    private static String byte2hex(byte[] b) {
//        String hs = "";
//        String stmp = "";
//        for (int n = 0; n < b.length; n++) {
//            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
//            if (stmp.length() == 1)
//                hs = hs + "0" + stmp;
//            else
//                hs = hs + stmp;
//        }
//        return hs.toUpperCase();
//    }
    /**
     *
     * @Method: encrypt
     * @Description: 加密数据
     * @param data
     * @return
     * @throws Exception
     * @date 2016年7月26日
     */
    public static String encrypt(String data) {  //对string进行BASE64Encoder转换
        byte[] bt = encryptByKey(data.getBytes(), password);
        BASE64Encoder base64en = new BASE64Encoder();
        String strs = new String(base64en.encode(bt));
        return strs;
    }
    /**
     *
     * @Method: encrypt
     * @Description: 解密数据
     * @param data
     * @return
     * @throws Exception
     * @date 2016年7月26日
     */
    public static String decryptor(String data) throws Exception {  //对string进行BASE64Encoder转换
        BASE64Decoder base64en = new BASE64Decoder();
        byte[] bt = decrypt(base64en.decodeBuffer(data), password);
        String strs = new String(bt);
        return strs;
    }
    /**
     * 加密
     * @param datasource byte[]

     * @return byte[]
     */
    private static byte[] encryptByKey(byte[] datasource, String key) {
        try{
            SecureRandom random = new SecureRandom();

            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(datasource);
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解密
     * @param src byte[]

     * @return byte[]
     * @throws Exception
     */
    private static byte[] decrypt(byte[] src, String key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(key.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(src);
    }


}