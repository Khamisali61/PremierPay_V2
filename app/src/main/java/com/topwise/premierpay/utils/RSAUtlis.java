package com.topwise.premierpay.utils;

import android.util.Base64;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.TopApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RSAUtlis {
    private static final String KEY_ALGORITHM = "RSA";
    private static final String RSA_PADDING = "rsa/ecb/pkcs1padding";
    private static final int KEY_SIZE = 2048;//设置长度 //RSA_PKCS1_PADDING
    private static final String RSA_NOPADDING = "RSA/None/NoPadding";
    private static final String RSA_ECB_NOPADDING = "RSA/ECB/NoPadding";
    /**
     * 生成公、私钥
     * 根据需要返回String或byte[]类型
     * @return
     */
    public static Map<String, String> createRSAKeys() {
        Map<String, String> keyPairMap = new HashMap<String, String>();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
//            Map<String, byte[]> byteMap = new HashMap<String, byte[]>();
//            byteMap.put(PUBLIC_KEY_NAME, publicKey.getEncoded());
//            byteMap.put(PRIVATE_KEY_NAME, privateKey.getEncoded());

            //获取公、私钥值
            String publicKeyValue = Base64.encodeToString(publicKey.getEncoded(),Base64.NO_WRAP); // getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyValue =  Base64.encodeToString(privateKey.getEncoded(),Base64.NO_WRAP);//Base64.getEncoder().encodeToString(privateKey.getEncoded());
            AppLog.e("Wwc","publicKeyValue " + publicKeyValue);
            AppLog.e("Wwc","privateKeyValue  " + privateKeyValue);

            keyPairMap.put("publicKey", publicKeyValue);
            keyPairMap.put("privateKey", privateKeyValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return keyPairMap;
    }

    /**
     * 解码PublicKey
     * @param key
     * @return
     */
    public static PublicKey getPublicKey(String key) {
        try {
            byte[] byteKey = Base64.decode(key,Base64.NO_WRAP);// getDecoder().decode(key);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, "BC");
            return keyFactory.generatePublic(x509EncodedKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解码PrivateKey
     * @param key
     * @return
     */
    public static PrivateKey getPrivateKey(String key) {
        try {
            byte[] byteKey = Base64.decode(key,Base64.NO_WRAP);//getDecoder().decode(key);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 分段大小
     *
     * @param cipher
     * @param srcBytes
     * @param segmentSize
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    public static byte[] cipherDoFinal(Cipher cipher, byte[] srcBytes, int segmentSize)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (segmentSize <= 0)
            throw new RuntimeException("分段大小必须大于0");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = srcBytes.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > segmentSize) {
                cache = cipher.doFinal(srcBytes, offSet, segmentSize);
            } else {
                cache = cipher.doFinal(srcBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * segmentSize;
        }
        byte[] data = out.toByteArray();
        out.close();
        return data;
    }

    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt1( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decode(publicKey,Base64.NO_WRAP);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance(RSA_NOPADDING);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] bytes = cipher.doFinal(str.getBytes("UTF-8"));
        AppLog.e("Wwc","wwc " + new String(bytes));
        String outStr = Base64.encodeToString(bytes,Base64.NO_WRAP);

//      new String(org.bouncycastle.util.encoders.Base64.encode(cipher.doFinal(str.getBytes("UTF-8"))), Charset.forName("US-ASCII"));
        return outStr;
    }

    /**
     * RSA私钥加密
     *
     * @param str
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String privateKeyEncrypt(String str, String privateKey,String point) throws Exception {
        AppLog.i("{}|RSA私钥加密前的数据|str:{}|publicKey:{}",point + " " +str+ " " +privateKey);
        //base64编码的公钥
        byte[] decoded = Base64.decode(privateKey,Base64.NO_WRAP);
        PrivateKey priKey = KeyFactory.getInstance("RSA").
                generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, priKey);
        String outStr = Base64.encodeToString(cipher.doFinal(str.getBytes()),Base64.NO_WRAP);
        AppLog.i("{}|RSA私钥加密后的数据|outStr:{}",point+ " " +outStr);
        return outStr;
    }

    /**
     * RSA公钥解密
     *
     * @param str
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String publicKeyDecrypt(String str, String publicKey,byte [] point) throws Exception {
        AppLog.i("{}|RSA公钥解密前的数据|str:{}|publicKey:{}",point+ " " +str+ " " +publicKey);

        byte [] decoded = Base64.decode(publicKey,Base64.NO_WRAP);
        String s = new String(decoded);
        AppLog.e("wwc","sas Base64.decode s " + s);

        String s1 = s.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\n", "")
                .replaceAll("\\t", "").trim();

        AppLog.e("wwc","sas Base64.decode s1 " + s1);

        //64位解码加密后的字符串
       // byte[] inputByte = Base64.decode(str.getBytes("UTF-8"),Base64.NO_WRAP);
        //base64编码的私钥
        byte[] decoded1 = Base64.decode(s1,Base64.NO_WRAP);
//        byte[] decoded = Base64.decode(publicKey,Base64.NO_WRAP);
        RSAPublicKey pubKey =  (RSAPublicKey)KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded1));
        //RSA解密
        Cipher cipher = Cipher.getInstance(RSA_NOPADDING);
        cipher.init(Cipher.DECRYPT_MODE, pubKey);


        byte[] bytes = rsaSplitCodec(cipher, Cipher.DECRYPT_MODE,point, pubKey.getModulus().bitLength());
        AppLog.e("wwc","bytes  s " + TopApplication.convert.bcdToStr(bytes));
        return new String(bytes, RSAUtilsTest.CHARSET);

//        byte[] encod111e = Base64.encode(str.getBytes(), Base64.NO_WRAP);
//
////        String outStr = Base64.encodeToString(cipher.doFinal(str.getBytes()),Base64.NO_WRAP);
//        String outStr = new String(cipher.doFinal(encod111e));
//        AppLog.i("{}|RSA公钥解密后的数据|outStr:{}",point+ " " +outStr);
//        return outStr;
    }

    // rsa切割解码  , ENCRYPT_MODE,加密数据   ,DECRYPT_MODE,解密数据
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;  //最大块
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    //可以调用以下的doFinal（）方法完成加密或解密数据：
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();

        try {
            out.close();
        }
        catch(Exception e) {

        }
        return resultDatas;
    }
}
