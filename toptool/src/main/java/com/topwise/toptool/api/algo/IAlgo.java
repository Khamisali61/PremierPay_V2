package com.topwise.toptool.api.algo;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public interface IAlgo {
    byte[] xor(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt);

    void randomBytes(byte[] paramArrayOfbyte);

    String md5(byte[] paramArrayOfbyte);

    String sha1(byte[] paramArrayOfbyte);

    String sha256(byte[] paramArrayOfbyte);

    byte[] des(ECryptOperation paramECryptOperation, ECryptOption paramECryptOption, ECryptPaddingOption paramECryptPaddingOption, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3);

    byte[] aes(ECryptOperation paramECryptOperation, ECryptOption paramECryptOption, ECryptPaddingOption paramECryptPaddingOption, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3);

    String base64Encode(byte[] paramArrayOfbyte);

    byte[] base64Decode(String paramString);

    Short crc16ccitt(byte[] paramArrayOfbyte);

    Integer crc32(byte[] paramArrayOfbyte);

    String integerStrAdd(String paramString1, String paramString2);

    IRsa getRsa();

    boolean setBit(byte[] paramArrayOfbyte, int paramInt, byte paramByte);

    Byte getBit(byte[] paramArrayOfbyte, int paramInt);

    public enum ECryptOperation {
        ENCRYPT, DECRYPT;
    }

    public enum ECryptOption {
        CBC, ECB;
    }

    public enum ECryptPaddingOption {
        NO_PADDING, PCKS5_PADDING;
    }

    public static interface IRsa {
        KeyPair genKeyPair(int param1Int);

        RSAPublicKey genPublicKey(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2);

        RSAPrivateKey genPrivateKey(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2);

        byte[] encryptWithPublicKey(RSAPublicKey param1RSAPublicKey, byte[] param1ArrayOfbyte, PaddingOption param1PaddingOption);

        byte[] encryptWithPrivateKey(RSAPrivateKey param1RSAPrivateKey, byte[] param1ArrayOfbyte, PaddingOption param1PaddingOption);

        byte[] decryptWithPrivateKey(RSAPrivateKey param1RSAPrivateKey, byte[] param1ArrayOfbyte, PaddingOption param1PaddingOption);

        byte[] decryptWithPublicKey(RSAPublicKey param1RSAPublicKey, byte[] param1ArrayOfbyte, PaddingOption param1PaddingOption);

        byte[] sign(byte[] param1ArrayOfbyte, RSAPrivateKey param1RSAPrivateKey, SignAlgorithm param1SignAlgorithm);

        boolean verifySign(byte[] param1ArrayOfbyte1, RSAPublicKey param1RSAPublicKey, byte[] param1ArrayOfbyte2, SignAlgorithm param1SignAlgorithm);

        public enum SignAlgorithm {
            MD5_WITH_RSA, SHA1_WITH_RSA, SHA256_WITH_RSA;
        }

        public enum PaddingOption {
            NO_PADDING, PKCS1_PADDING;
        }
    }
}
