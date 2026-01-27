package com.topwise.premierpay.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtilsTest {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";

    /**
     * 得到公钥
     * @param pKey  密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKeyFromPem(String pKey) throws Exception {
//        BufferedReader br = new BufferedReader(new FileReader("./rsa_public_key.pem"));
//        String s = br.readLine();
//        String str = "";
//        s = br.readLine();
//        while (s.charAt(0) != '-') {
//            str += s;
//            s = br.readLine();
//        }
//
//        byte[] b = base64Decode(str);

//        byte [] decoded0 = Base64.decode(pKey,Base64.NO_WRAP);
//        String s = new String(decoded0);
//        String s1 = s.replaceAll("-----BEGIN PUBLIC KEY-----", "")
//        .replaceAll("-----END PUBLIC KEY-----", "")
//        .replaceAll("\\n", "")
//        .replaceAll("\\t", "").trim();
//
//        AppLog.e("wwc","sas Base64.decode s1 " + s1);

        byte[] decoded = Base64.decode(pKey,Base64.NO_WRAP);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        RSAPublicKey pubKey = (RSAPublicKey)kf.generatePublic(keySpec);
        return pubKey;
    }

    /**
     * 得到私钥
     * @param pKey  密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKeyFromPem(String pKey) throws Exception {
//        BufferedReader br = new BufferedReader(new FileReader("./rsa_private_key_pkcs8.pem"));
//        String s = br.readLine();
//        String str = "";
//        s = br.readLine();
//        while (s.charAt(0) != '-') {
//            str += s;
//            s = br.readLine();
//        }
//        byte[] b = base64Decode(str);

        byte[] decoded = Base64.decode(pKey,Base64.NO_WRAP);

        // 生成私匙
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        RSAPrivateKey privateKey = (RSAPrivateKey)kf.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            return RSAUtilsTest.base64EncodeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, RSAUtilsTest.base64Decode(data), privateKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            //每个Cipher初始化方法使用一个模式参数opmod，并用此模式初始化Cipher对象。此外还有其他参数，包括密钥key、包含密钥的证书certificate、算法参数params和随机源random。
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return RSAUtilsTest.base64EncodeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
//            return new String(cipher.doFinal(data.getBytes()));
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, RSAUtilsTest.base64Decode(data), publicKey.getModulus().bitLength()), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    //rsa切割解码  , ENCRYPT_MODE,加密数据   ,DECRYPT_MODE,解密数据
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

    private static String base64EncodeString(byte[] data) {
//        final Base64.Encoder encoder = Base64.getEncoder();
//        encoder.encodeToString(data);
        return Base64.encodeToString(data,Base64.NO_WRAP);
    }

    public static byte[] base64Decode(String data) {
//        final Base64.Decoder decoder = Base64.getDecoder();
//        decoder.decode(data.getBytes());
        return Base64.decode(data.getBytes(),Base64.NO_WRAP);
    }

    public static void mainaaa() throws Exception {
        String str = "12345678901234567890123456789012";
        System.out.println("明文：\n" + str+"\n");
               String f0_48 = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0NCk1JSUJJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBUThBTUlJQkNnS0NBUUVBb1hwalRZekxPeUtnZGpVVEdlNjFrNlVMbzM4a1g1VVoNCmlMT1NTT2lWa2FTUXVxMEJzTXQ4TE5ZcURhRUxNVFFVWHNjUU1lRFJxWVVYdjdCalptWWNnOHBpUUxjdkNRNUpyMGNzajVERUpzcWgNCmMvclpXVTN5a09SaUVBNVM2SlZTdC81dHI3UTUwcm9CWnFMeVRuckVlVDJPbWl5Z0NSMElKSEpRM2lzVGlST0doNWthSlUyZGIvS2sNCk40Ly9yWHlaY1QzdFZpZFZscEI3d1lYNzQrSTZyTmg2Wm90UnI4RHdUZ2ltMmpUVlhWYnRhRUZacFJsV0NBeDJwU3prekJBanRlNlQNCm1NOGw1WmtHcWFwQ3E0b0VWS3BzRWpvVlJyMURkRzNkdkltdC9VUEpuTFN6em1ubWZUKzFlWThBM0o5dmdWWkthUHNvMDhLNEx3QmINClFubnZJUUlEQVFBQg0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0t";
               String f1_48_PIN = "6495C5EA1739C557937BD44915A42B25F80E95C73CF2CA0D6752C7435DA80419738C69F48F6CA4E7ECB578C2CC64FDFD7C66CC20A68EE72942F3826D0FEB1791205B7DE43E8236DCA850224ADAE705EB6117BDA7E8E269E31411F4B278D959EA28AECEEFC0BC714269BEBFB261AE0453BCE1D8BCE4CD929820193EB2F8C63861";
               String f1_48_DATA = "BFB15AC41E6385CB9F2D75DCD2C1875A8685C7A49F41E3E79202CD08ECB283F3C67CB7D78023E224104782204C0B7011F734DBE3C06AF61EE4495D1442441425B3B3D3E5DA1C3A2A806533BE111F5C37D8598979E14F2FC99B43EB76A49205763A943DA09E8EF990CC66BC6C5D5A1B2BBC84CDE3B495D953AFDA0F9310BC2FBC";
//        System.out.println("公钥加密——私钥解密");
//        String encodedData = RSAUtilsTest.publicEncrypt(str, RSAUtilsTest.getPublicKeyFromPem());  //传入明文和公钥加密,得到密文
//        System.out.println("密文：\n" + encodedData);
//        String decodedData = RSAUtilsTest.privateDecrypt(encodedData, RSAUtilsTest.getPrivateKeyFromPem()); //传入密文和私钥,得到明文
//        System.out.println("解密后文字: \n" + decodedData+"\n");

        String g= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwGeeV00BXxZ5Sn8AeH0aKkm7g2psmWOeDyiuH8bx2p1hDeiCmW8Rhea3ibdcvq0z3EPuatPyylhx1t5ECKkDcCFWpIr+saApcvP13TLtAgUuRvv1db/ndoJJUWCnJQKY7xWmOh77KBcq4Ahzczx4LjPmSMFrPfg7zIa5pYPP/41DvojcvAnFn1kq5nNwcQ6WR7+0qj7QBZWNuLy4IdPGkOcqZupxyUcA2Zfr4naWsGnNIXoejKbC31HsXUcTpp+VhdffT00pJ5rW6+/sWhztUi30Zrt6jwfbMaKoda6EjMrPdpxMsvINJQoGzSlDmcU0YBZ1cbi1w1WEcle3AGvwKwIDAQAB";
        String s = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDAZ55XTQFfFnlKfwB4fRoqSbuDamyZY54PKK4fxvHanWEN6IKZbxGF5reJt1y+rTPcQ+5q0/LKWHHW3kQIqQNwIVakiv6xoCly8/XdMu0CBS5G+/V1v+d2gklRYKclApjvFaY6HvsoFyrgCHNzPHguM+ZIwWs9+DvMhrmlg8//jUO+iNy8CcWfWSrmc3BxDpZHv7SqPtAFlY24vLgh08aQ5ypm6nHJRwDZl+vidpawac0heh6MpsLfUexdRxOmn5WF199PTSknmtbr7+xaHO1SLfRmu3qPB9sxoqh1roSMys92nEyy8g0lCgbNKUOZxTRgFnVxuLXDVYRyV7cAa/ArAgMBAAECggEBALvyvF9FTsinOHaIF1qbwjp66hxWkp1IDXd7YpSSD2FhiRRAA4uETtbBaJqzrnpo0RcimLxsLGhy+0lMt6LKDxyyxGNZj7DN5o9vNsYMYC8wBmO67q1GeAFxBdPjmYLyxZgPSuDNsMQZ1X0hZD22XSOs+RArOOpBGZESLjk7yKw1iopFPmJJVwRRuI7i5EEl3r7qPj1jGJRMxgvEJouWh03GTqfQgypTAxsf8uCJZhHzoA7UVW2r+6U1OwYOyTGrjhzxM/uEGBppd8blFFFcNm+KI95/Zp9zgJZubU/5dxsJjpcdHeiw6i/qS8RVSWCa/moyo18naovO6wVjcUxhFtkCgYEA+wgGBrSc4d4l0cgE+ef7Yg2FlVvN3JbrseJztQGEXaTR9eX+jHe6twHiMwFLy0HKaKojWLcIv6+1lFDRwSKgG06mDL47k66tFiBtyd2iH1LPIeVg+GHgD6ajINo/kvcHIHKBk/a63zhwPS1elA45kmQSEV0FZqhRsfiq4vvrFHcCgYEAxDaIsAzXQcKY4u9+4a/+teTRAkXDL8bbHV8Welwfx8nwQyikm+iz0aloSgbKeAT8uYXpvn8hF56AllZG8nRNuh9le8YIXDIC8SrJ43igcTvsEA50X1G/gohsQSR5PH8sJWkC9/UMIO/PUitMUP9itG8nnkKq6aaXdYJ5gjj7cu0CgYBIjw4m3AfIpYxq8ZS2o14e/meRP2Lmktz3osvtEs4uvxhNKjiEY4sdoLbAJWzCCHc3/42N9AHcXi5u+1lVqOt8WSRX5JiV3FR9Y8w4IA7fXPEwC/wWbzvzaigWs97SXQK7Vz2rBhMHuGmA1V9UDjSH4tLyH+ASxy4t8gF68HQHiwKBgQCZcdxrcV9Vug0+S/V6Idyb+xnJIS2Hy4fH7Hjzr9/aqn4Ox1YT52PY7xbz7WOhgKTzoVou89S5V+dPqqFJMtMmXy9KIJChKStL1x6Tmd4/L5Fo7STs9k1Ck9W1DAUT3pBzoTAFIcoeZ+xGYj4IifBzEwp0pcZrezCvYR4NTOq38QKBgQDyCOC7R6nKdlr5UyOZDY+ehUcHmS8KF8SiXaOXTlXBa/c5XpmkZTddqBC9EZB6ja3UWjqt4IglRaT1Mjg9J6TwVjw3pbTY8FIHsqiL9SLvlNBjJSu2xvuSW8Mmy3/rUvjR2VTZdhxidrTTiHE0KzbySsVuMVetjIgsKuoH4CN4Vg==";
        System.out.println("私钥加密——公钥解密");
        String encodedData = RSAUtilsTest.privateEncrypt(str, RSAUtilsTest.getPrivateKeyFromPem(s));  //传入明文和公钥加密,得到密文
        System.out.println("密文：\n" + encodedData);
//        String s1 = base64EncodeString(f1_48_PIN.getBytes());
        String decodedData = RSAUtilsTest.publicDecrypt(encodedData, RSAUtilsTest.getPublicKeyFromPem(g)); //传入密文和私钥,得到明文
        System.out.println("解密后文字: \n" + decodedData);
    }
}
