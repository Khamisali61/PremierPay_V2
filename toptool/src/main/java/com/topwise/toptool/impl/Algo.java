package com.topwise.toptool.impl;

import com.topwise.toptool.api.algo.IAlgo;
import com.topwise.toptool.api.utils.AppLog;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.zip.CRC32;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Algo implements IAlgo {
    private static final String TAG = Algo.class.getSimpleName();
    private static Algo instance;


    private static char[] base64EncodeChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
            '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    private static byte[] base64DecodeChars = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
            5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1,
            -1, -1, -1};

    private static int[] crc16_table = {0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50A5, 0x60C6, 0x70E7, 0x8108,
            0x9129, 0xA14A, 0xB16B, 0xC18C, 0xD1AD, 0xE1CE, 0xF1EF, 0x1231, 0x0210, 0x3273, 0x2252, 0x52B5, 0x4294,
            0x72F7, 0x62D6, 0x9339, 0x8318, 0xB37B, 0xA35A, 0xD3BD, 0xC39C, 0xF3FF, 0xE3DE, 0x2462, 0x3443, 0x0420,
            0x1401, 0x64E6, 0x74C7, 0x44A4, 0x5485, 0xA56A, 0xB54B, 0x8528, 0x9509, 0xE5EE, 0xF5CF, 0xC5AC, 0xD58D,
            0x3653, 0x2672, 0x1611, 0x0630, 0x76D7, 0x66F6, 0x5695, 0x46B4, 0xB75B, 0xA77A, 0x9719, 0x8738, 0xF7DF,
            0xE7FE, 0xD79D, 0xC7BC, 0x48C4, 0x58E5, 0x6886, 0x78A7, 0x0840, 0x1861, 0x2802, 0x3823, 0xC9CC, 0xD9ED,
            0xE98E, 0xF9AF, 0x8948, 0x9969, 0xA90A, 0xB92B, 0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71, 0x0A50, 0x3A33,
            0x2A12, 0xDBFD, 0xCBDC, 0xFBBF, 0xEB9E, 0x9B79, 0x8B58, 0xBB3B, 0xAB1A, 0x6CA6, 0x7C87, 0x4CE4, 0x5CC5,
            0x2C22, 0x3C03, 0x0C60, 0x1C41, 0xEDAE, 0xFD8F, 0xCDEC, 0xDDCD, 0xAD2A, 0xBD0B, 0x8D68, 0x9D49, 0x7E97,
            0x6EB6, 0x5ED5, 0x4EF4, 0x3E13, 0x2E32, 0x1E51, 0x0E70, 0xFF9F, 0xEFBE, 0xDFDD, 0xCFFC, 0xBF1B, 0xAF3A,
            0x9F59, 0x8F78, 0x9188, 0x81A9, 0xB1CA, 0xA1EB, 0xD10C, 0xC12D, 0xF14E, 0xE16F, 0x1080, 0x00A1, 0x30C2,
            0x20E3, 0x5004, 0x4025, 0x7046, 0x6067, 0x83B9, 0x9398, 0xA3FB, 0xB3DA, 0xC33D, 0xD31C, 0xE37F, 0xF35E,
            0x02B1, 0x1290, 0x22F3, 0x32D2, 0x4235, 0x5214, 0x6277, 0x7256, 0xB5EA, 0xA5CB, 0x95A8, 0x8589, 0xF56E,
            0xE54F, 0xD52C, 0xC50D, 0x34E2, 0x24C3, 0x14A0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405, 0xA7DB, 0xB7FA,
            0x8799, 0x97B8, 0xE75F, 0xF77E, 0xC71D, 0xD73C, 0x26D3, 0x36F2, 0x0691, 0x16B0, 0x6657, 0x7676, 0x4615,
            0x5634, 0xD94C, 0xC96D, 0xF90E, 0xE92F, 0x99C8, 0x89E9, 0xB98A, 0xA9AB, 0x5844, 0x4865, 0x7806, 0x6827,
            0x18C0, 0x08E1, 0x3882, 0x28A3, 0xCB7D, 0xDB5C, 0xEB3F, 0xFB1E, 0x8BF9, 0x9BD8, 0xABBB, 0xBB9A, 0x4A75,
            0x5A54, 0x6A37, 0x7A16, 0x0AF1, 0x1AD0, 0x2AB3, 0x3A92, 0xFD2E, 0xED0F, 0xDD6C, 0xCD4D, 0xBDAA, 0xAD8B,
            0x9DE8, 0x8DC9, 0x7C26, 0x6C07, 0x5C64, 0x4C45, 0x3CA2, 0x2C83, 0x1CE0, 0x0CC1, 0xEF1F, 0xFF3E, 0xCF5D,
            0xDF7C, 0xAF9B, 0xBFBA, 0x8FD9, 0x9FF8, 0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93, 0x3EB2, 0x0ED1, 0x1EF0};

    private Algo() {
    }


    public synchronized static Algo getInstance() {
        if (instance == null) {
            instance = new Algo();
        }

        return instance;
    }

    @Override
    public byte[] xor(byte[] a, byte[] b, int len) {
        if (a == null || b == null) {
            AppLog.e(TAG, "xor a or b is null! a: " + a + ", b: " + b);
            return null;
        }

        if (len > a.length || len > b.length) {
            AppLog.e(TAG, "xor len: " + len + " out of range! a length: " + a.length + ", b length: " + b.length);
            return null;
        }

        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    @Override
    public void randomBytes(byte[] r) {
        if (r == null) {
            AppLog.e(TAG, "randomBytes r is null");
            return;
        }
        Random random = new Random();
        random.nextBytes(r);
    }

    @Override
    public String md5(byte[] data) {
        if (data == null) {
            AppLog.e(TAG, "md5 data is null");
            return null;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return TopTool.getInstance().getConvert().bcdToStr(md5.digest(data));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String sha1(byte[] data) {
        if (data == null) {
            AppLog.e(TAG, "sha1 data is null");
            return null;
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            return TopTool.getInstance().getConvert().bcdToStr(md.digest(data));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String sha256(byte[] data) {
        if (data == null) {
            AppLog.e(TAG, "sha256 data is null");
            return null;
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            return TopTool.getInstance().getConvert().bcdToStr(md.digest(data));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] des(ECryptOperation cryptOperation, ECryptOption cryptOption, ECryptPaddingOption paddingOption,
                      byte[] data, byte[] key, byte[] iv) {
        if (data == null || key == null) {
            AppLog.e(TAG, "des data or key is null!");
            return null;
        }

        String alg;
        String mode;
        String padding;

        if (cryptOption == ECryptOption.ECB) {
            mode = "ECB";
        } else {
            mode = "CBC";
        }

        if (paddingOption == ECryptPaddingOption.NO_PADDING) {
            padding = "NoPadding";
        } else {
            padding = "PKCS5Padding";
        }

        // IV
        byte[] realIv = new byte[8];
        if (iv != null) {
            System.arraycopy(iv, 0, realIv, 0, Math.min(iv.length, 8));
        }

        IvParameterSpec ivParameterSpec = null;
        // iv is only for CBC mode
        if (cryptOption == ECryptOption.CBC) {
            ivParameterSpec = new IvParameterSpec(realIv);
        }

        try {

            int opmode = (cryptOperation == ECryptOperation.ENCRYPT) ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;

            if (key.length == 8) {// DES

                alg = "DES";

                DESKeySpec desKey = new DESKeySpec(key);

                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(alg);
                SecretKey secretKey = keyFactory.generateSecret(desKey);

                Cipher cipher = Cipher.getInstance(alg + "/" + mode + "/" + padding);
                cipher.init(opmode, secretKey, ivParameterSpec);
                return cipher.doFinal(data);

            } else if (key.length == 16 || key.length == 24) {// 3DES

                alg = "DESede";

                SecretKey deskey = new SecretKeySpec(key, alg);

                Cipher cipher = Cipher.getInstance(alg + "/" + mode + "/" + padding);
                cipher.init(opmode, deskey, ivParameterSpec);
                return cipher.doFinal(data);

            } else {
                AppLog.e("Algo", "key len error");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] aes(ECryptOperation cryptOperation, ECryptOption cryptOption, ECryptPaddingOption paddingOption,
                      byte[] data, byte[] key, byte[] iv) {
        if (data == null || key == null) {
            AppLog.e(TAG, "aes data or key is null!");
            return null;
        }

        String alg;
        String mode;
        String padding;

        if (cryptOption == ECryptOption.ECB) {
            mode = "ECB";
        } else {
            mode = "CBC";
        }

        if (paddingOption == ECryptPaddingOption.NO_PADDING) {
            padding = "NoPadding";
        } else {
            padding = "PKCS5Padding";
        }

        // IV
        byte[] realIv = new byte[16];
        if (iv != null) {
            System.arraycopy(iv, 0, realIv, 0, Math.min(iv.length, 16));
        }

        IvParameterSpec ivParameterSpec = null;
        // iv is only for CBC mode
        if (cryptOption == ECryptOption.CBC) {
            ivParameterSpec = new IvParameterSpec(realIv);
        }

        try {
            int opmode = (cryptOperation == ECryptOperation.ENCRYPT) ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;

            alg = "AES";
            SecretKey secretKey = new SecretKeySpec(key, alg);
            Cipher cipher = Cipher.getInstance(alg + "/" + mode + "/" + padding);
            cipher.init(opmode, secretKey, ivParameterSpec);
            return cipher.doFinal(data);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String base64Encode(byte[] data) {
        if (data == null) {
            AppLog.e(TAG, "base64 encode data is null");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }

        return sb.toString();
    }

    @Override
    public byte[] base64Decode(String base64Encoded) {
        if (base64Encoded == null) {
            AppLog.e(TAG, "base64 decode data is null");
            return null;
        }
        Charset cs8859_1 = Charset.forName("ISO-8859-1");

        StringBuilder sb = new StringBuilder();

        byte[] data = base64Encoded.getBytes(Charset.forName("US-ASCII"));

        int len = data.length;
        if (len % 4 != 0) {
            AppLog.e(TAG, "base64 decode input data length if NOT multiple of 4!");
            return null;
        }

        int i = 0;
        int b1, b2, b3, b4;
        while (i < len) {
            /* b1 */
            do {
                b1 = base64DecodeChars[data[i++]];
            } while (i < len && b1 == -1);
            if (b1 == -1)
                break;
            /* b2 */
            do {
                b2 = base64DecodeChars[data[i++]];
            } while (i < len && b2 == -1);
            if (b2 == -1)
                break;
            sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
            /* b3 */
            do {
                b3 = data[i++];
                if (b3 == 61)
                    return sb.toString().getBytes(cs8859_1);
                b3 = base64DecodeChars[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1)
                break;
            sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
            /* b4 */
            do {
                b4 = data[i++];
                if (b4 == 61)
                    return sb.toString().getBytes(cs8859_1);
                b4 = base64DecodeChars[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1)
                break;
            sb.append((char) (((b3 & 0x03) << 6) | b4));
        }

        return sb.toString().getBytes(cs8859_1);

    }

    @Override
    public Short crc16ccitt(byte[] data) {
        if (data == null) {
            AppLog.e(TAG, "crc16ccitt data is null");
            return null;
        }

        int crc = 0;
        int length = data.length;
        for (int i = 0; i < length; i++) {
            crc = ((crc & 0xFF) << 8) ^ crc16_table[(((crc & 0xFF00) >> 8) ^ data[i]) & 0xFF];
        }
        crc &= 0xFFFF;

        return (short) crc;
    }

    @Override
    public Integer crc32(byte[] data) {
        if (data == null) {
            AppLog.e(TAG, "crc32 data is null");
            return null;
        }

        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return (int) crc32.getValue();
    }

    @Override
    public String integerStrAdd(String left, String right) {
        if (left == null || right == null) {
            AppLog.e(TAG, "integerStrAdd left or right is null!");
            return null;
        }

        BigDecimal num1 = new BigDecimal(left.trim());
        BigDecimal num2 = new BigDecimal(right.trim());

        BigDecimal ret = num1.add(num2);

        return ret.toString();
    }

    @Override
    public IRsa getRsa() {
        AlgoRsa rsa = AlgoRsa.getInstance();
        return rsa;
    }

    @Override
    public boolean setBit(byte[] data, int index, byte value) {
        if (data == null || index <= 0) {
            AppLog.e(TAG, "setBit data is null or index <=0");
            return false;
        }

        byte bitValue = (byte) (value >= 1 ? 1 : 0);
        int arryOffset = (index - 1) / 8;
        int bitOffset = (index - 1) % 8;

        if (arryOffset >= data.length) {
            AppLog.e(TAG, "setBit offset out of range!");
            return false;
        }

        byte mask = (byte) (1 << (7 - bitOffset));
        if (bitValue == 0) {
            data[arryOffset] = (byte) (data[arryOffset] & (~mask));
        } else {
            data[arryOffset] = (byte) (data[arryOffset] | (mask));
        }
        return true;
    }

    @Override
    public Byte getBit(byte[] data, int index) {
        if (data == null || index <= 0) {
            AppLog.e(TAG, "getBit data is null or index <=0");
            return null;
        }

        int arryOffset = (index - 1) / 8;
        int bitOffset = (index - 1) % 8;

        if (arryOffset >= data.length) {
            AppLog.e(TAG, "getBit offset out of range!");
            return null;
        }

        byte mask = (byte) (1 << (7 - bitOffset));
        byte ret = (byte) (data[arryOffset] & mask);

        return (byte) (ret != 0 ? 1 : 0);
    }

}
