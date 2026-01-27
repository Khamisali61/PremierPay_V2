package com.topwise.toptool.impl;

import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.utils.AppLog;

import java.text.DecimalFormat;

public class Convert implements IConvert {
    private static final String TAG = Convert.class.getSimpleName();
    private static Convert instance;


    private Convert() {
    }

    public synchronized static Convert getInstance() {
        if (instance == null) {
            instance = new Convert();
        }

        return instance;
    }

    @Override
    public long strToLong(String paramString,EPaddingPosition paramEPaddingPosition) throws IllegalArgumentException {
        if (paramString == null || paramEPaddingPosition == null) {
            AppLog.e(TAG, "bcdtolong input arg is null");
            throw new IllegalArgumentException("bcdtolong input arg is null");
        }
        byte[] bcd = strToBcd(paramString, paramEPaddingPosition);
        long temp = 0x00;
        for(int i = 0; i < bcd.length; i++) {
            temp = (temp <<= 8) + (bcd[i] & 0xFF);
        }
        return temp;
    }

    @Override
    public String bcdToStr(byte[] b) throws IllegalArgumentException {
        if (b == null) {
            AppLog.e(TAG, "bcdToStr input arg is null");
            return "";
        }

        char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }

        return sb.toString();
    }

    @Override
    public byte[] strToBcd(String str, EPaddingPosition paddingPosition) throws IllegalArgumentException {
        if (str == null || paddingPosition == null) {
            AppLog.e(TAG, "strToBcd input arg is null");
            throw new IllegalArgumentException("strToBcd input arg is null");
        }

        int len = str.length();
        int mod = len % 2;
        if (mod != 0) {
            if (paddingPosition == EPaddingPosition.PADDING_RIGHT) {
                str = str + "0";
            } else {
                str = "0" + str;
            }
            len = str.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = str.getBytes();
        int j, k;
        for (int p = 0; p < str.length() / 2; p++) {
            if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else if ((abt[2 * p] >= 'A') && (abt[2 * p] <= 'Z')) {
                j = abt[2 * p] - 'A' + 0x0a;
            } else {
                j = abt[2 * p] - '0';
            }

            if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else if ((abt[2 * p + 1] >= 'A') && (abt[2 * p + 1] <= 'Z')) {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            } else {
                k = abt[2 * p + 1] - '0';
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    @Override
    public void longToByteArray(long l, byte[] to, int offset, EEndian endian) throws IllegalArgumentException {
        if (to == null || endian == null) {
            AppLog.e(TAG, "longToByteArray input arg is null");
            throw new IllegalArgumentException("longToByteArray input arg is null");
        }

        if (endian == EEndian.BIG_ENDIAN) {
            to[offset] = (byte) ((l >>> 56) & 0xff);
            to[offset + 1] = (byte) ((l >>> 48) & 0xff);
            to[offset + 2] = (byte) ((l >>> 40) & 0xff);
            to[offset + 3] = (byte) ((l >>> 32) & 0xff);
            to[offset + 4] = (byte) ((l >>> 24) & 0xff);
            to[offset + 5] = (byte) ((l >>> 16) & 0xff);
            to[offset + 6] = (byte) ((l >>> 8) & 0xff);
            to[offset + 7] = (byte) (l & 0xff);
        } else {
            to[offset + 7] = (byte) ((l >>> 56) & 0xff);
            to[offset + 6] = (byte) ((l >>> 48) & 0xff);
            to[offset + 5] = (byte) ((l >>> 40) & 0xff);
            to[offset + 4] = (byte) ((l >>> 32) & 0xff);
            to[offset + 3] = (byte) ((l >>> 24) & 0xff);
            to[offset + 2] = (byte) ((l >>> 16) & 0xff);
            to[offset + 1] = (byte) ((l >>> 8) & 0xff);
            to[offset] = (byte) (l & 0xff);
        }
    }

    @Override
    public byte[] longToByteArray(long l, EEndian endian) throws IllegalArgumentException {
        if (endian == null) {
            AppLog.e(TAG, "longToByteArray input arg is null");
            throw new IllegalArgumentException("longToByteArray input arg is null");
        }

        byte[] to = new byte[8];

        if (endian == EEndian.BIG_ENDIAN) {
            to[0] = (byte) ((l >>> 56) & 0xff);
            to[1] = (byte) ((l >>> 48) & 0xff);
            to[2] = (byte) ((l >>> 40) & 0xff);
            to[3] = (byte) ((l >>> 32) & 0xff);
            to[4] = (byte) ((l >>> 24) & 0xff);
            to[5] = (byte) ((l >>> 16) & 0xff);
            to[6] = (byte) ((l >>> 8) & 0xff);
            to[7] = (byte) (l & 0xff);
        } else {
            to[7] = (byte) ((l >>> 56) & 0xff);
            to[6] = (byte) ((l >>> 48) & 0xff);
            to[5] = (byte) ((l >>> 40) & 0xff);
            to[4] = (byte) ((l >>> 32) & 0xff);
            to[3] = (byte) ((l >>> 24) & 0xff);
            to[2] = (byte) ((l >>> 16) & 0xff);
            to[1] = (byte) ((l >>> 8) & 0xff);
            to[0] = (byte) (l & 0xff);
        }

        return to;
    }

    @Override
    public void intToByteArray(int i, byte[] to, int offset, EEndian endian) throws IllegalArgumentException {
        if (to == null || endian == null) {
            AppLog.e(TAG, "longToByteArray input arg is null");
            throw new IllegalArgumentException("longToByteArray input arg is null");
        }

        if (endian == EEndian.BIG_ENDIAN) {
            to[offset] = (byte) ((i >>> 24) & 0xff);
            to[offset + 1] = (byte) ((i >>> 16) & 0xff);
            to[offset + 2] = (byte) ((i >>> 8) & 0xff);
            to[offset + 3] = (byte) (i & 0xff);
        } else {
            to[offset] = (byte) (i & 0xff);
            to[offset + 1] = (byte) ((i >>> 8) & 0xff);
            to[offset + 2] = (byte) ((i >>> 16) & 0xff);
            to[offset + 3] = (byte) ((i >>> 24) & 0xff);
        }
    }

    @Override
    public byte[] intToByteArray(int i, EEndian endian) throws IllegalArgumentException {
        if (endian == null) {
            AppLog.e(TAG, "intToByteArray input arg is null");
            throw new IllegalArgumentException("intToByteArray input arg is null");
        }

        byte[] to = new byte[4];

        if (endian == EEndian.BIG_ENDIAN) {
            to[0] = (byte) ((i >>> 24) & 0xff);
            to[1] = (byte) ((i >>> 16) & 0xff);
            to[2] = (byte) ((i >>> 8) & 0xff);
            to[3] = (byte) (i & 0xff);
        } else {
            to[0] = (byte) (i & 0xff);
            to[1] = (byte) ((i >>> 8) & 0xff);
            to[2] = (byte) ((i >>> 16) & 0xff);
            to[3] = (byte) ((i >>> 24) & 0xff);
        }

        return to;
    }

    @Override
    public void shortToByteArray(short s, byte[] to, int offset, EEndian endian) throws IllegalArgumentException {
        if (to == null || endian == null) {
            AppLog.e(TAG, "shortToByteArray input arg is null");
            throw new IllegalArgumentException("shortToByteArray input arg is null");
        }

        if (endian == EEndian.BIG_ENDIAN) {
            to[offset] = (byte) ((s >>> 8) & 0xff);
            to[offset + 1] = (byte) (s & 0xff);
        } else {
            to[offset] = (byte) (s & 0xff);
            to[offset + 1] = (byte) ((s >>> 8) & 0xff);
        }
    }

    @Override
    public byte[] shortToByteArray(short s, EEndian endian) throws IllegalArgumentException {
        if (endian == null) {
            AppLog.e(TAG, "shortToByteArray input arg is null");
            throw new IllegalArgumentException("shortToByteArray input arg is null");
        }

        byte[] to = new byte[2];

        if (endian == EEndian.BIG_ENDIAN) {
            to[0] = (byte) ((s >>> 8) & 0xff);
            to[1] = (byte) (s & 0xff);
        } else {
            to[0] = (byte) (s & 0xff);
            to[1] = (byte) ((s >>> 8) & 0xff);
        }

        return to;
    }

    @Override
    public long longFromByteArray(byte[] from, int offset, EEndian endian) throws IllegalArgumentException {
        if (from == null || endian == null) {
            AppLog.e(TAG, "longFromByteArray input arg is null");
            throw new IllegalArgumentException("longFromByteArray input arg is null");
        }

        if (endian == EEndian.BIG_ENDIAN) {
            return ((from[offset] << 56) & 0xff00000000000000L) | ((from[offset + 1] << 48) & 0xff000000000000L)
                    | ((from[offset + 2] << 40) & 0xff0000000000L) | ((from[offset + 3] << 32) & 0xff00000000L)
                    | ((from[offset + 4] << 24) & 0xff000000) | ((from[offset + 5] << 16) & 0xff0000)
                    | ((from[offset + 6] << 8) & 0xff00) | (from[offset + 7] & 0xff);
        } else {
            return ((from[offset + 7] << 56) & 0xff00000000000000L) | ((from[offset + 6] << 48) & 0xff000000000000L)
                    | ((from[offset + 5] << 40) & 0xff0000000000L) | ((from[offset + 4] << 32) & 0xff00000000L)
                    | ((from[offset + 3] << 24) & 0xff000000) | ((from[offset + 2] << 16) & 0xff0000)
                    | ((from[offset + 1] << 8) & 0xff00) | (from[offset] & 0xff);
        }
    }

    @Override
    public int intFromByteArray(byte[] from, int offset, EEndian endian) throws IllegalArgumentException {
        if (from == null || endian == null) {
            AppLog.e(TAG, "intFromByteArray input arg is null");
            throw new IllegalArgumentException("intFromByteArray input arg is null");
        }

        if (endian == EEndian.BIG_ENDIAN) {
            return ((from[offset] << 24) & 0xff000000) | ((from[offset + 1] << 16) & 0xff0000)
                    | ((from[offset + 2] << 8) & 0xff00) | (from[offset + 3] & 0xff);
        } else {
            return ((from[offset + 3] << 24) & 0xff000000) | ((from[offset + 2] << 16) & 0xff0000)
                    | ((from[offset + 1] << 8) & 0xff00) | (from[offset] & 0xff);
        }
    }

    @Override
    public short shortFromByteArray(byte[] from, int offset, EEndian endian) throws IllegalArgumentException {
        if (from == null || endian == null) {
            AppLog.e(TAG, "shortFromByteArray input arg is null");
            throw new IllegalArgumentException("shortFromByteArray input arg is null");
        }

        if (endian == EEndian.BIG_ENDIAN) {
            return (short) (((from[offset] << 8) & 0xff00) | (from[offset + 1] & 0xff));
        } else {
            return (short) (((from[offset + 1] << 8) & 0xff00) | (from[offset] & 0xff));
        }
    }

    public String stringPadding(String src, char paddingChar, long expLength, EPaddingPosition paddingpos)
            throws IllegalArgumentException {
        if (src == null || paddingpos == null) {
            AppLog.e(TAG, "stringPadding input arg is null");
            throw new IllegalArgumentException("stringPadding input arg is null");
        }

        if (src.length() >= expLength) {
            return src;
        }

        if (paddingpos == EPaddingPosition.PADDING_RIGHT) {

            StringBuffer sb = new StringBuffer(src);
            for (int i = 0; i < expLength - src.length(); i++) {
                sb.append(paddingChar);
            }

            return sb.toString();
        } else {

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < expLength - src.length(); i++) {
                sb.append(paddingChar);
            }

            sb.append(src);
            return sb.toString();
        }
    }

    @Override
    public String amountMajorToMinUnit(double major, ECurrencyExponent exponent) throws IllegalArgumentException {
        if (exponent == null) {
            AppLog.e(TAG, "amountMajorToMinUnit input arg is null");
            throw new IllegalArgumentException("amountMajorToMinUnit input arg is null");
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        String majorS = decimalFormat.format(major);

        String[] ht = majorS.split("\\.");

        String h = ht[0];
        String t = ht[1];

        if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_2) {

            t = t.substring(0, 2);

            return h + t;
        } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_0) {
            return h;
        } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_1) {

            t = t.substring(0, 1);

            return h + t;
        } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_3) {

            t = t.substring(0, 3);

            return h + t;
        }

        return "";
    }

    @Override
    public String amountMajorToMinUnit(String major, ECurrencyExponent exponent) throws IllegalArgumentException {
        if (major == null || exponent == null) {
            AppLog.e(TAG, "amountMajorToMinUnit input arg is null");
            throw new IllegalArgumentException("amountMajorToMinUnit input arg is null");
        }
        major = major.replace(",", "");
        return amountMajorToMinUnit(Double.parseDouble(major.trim()), exponent);
    }

    @Override
    public String amountMinUnitToMajor(String minUnit, ECurrencyExponent exponent, boolean isCommaStyle)
            throws IllegalArgumentException {
        if (minUnit == null || exponent == null || minUnit.equals("")) {
            AppLog.e(TAG, "amountMinUnitToMajor input arg is null");
            throw new IllegalArgumentException("amountMinUnitToMajor input arg is null");
        }
        if (!minUnit.matches("[0-9]+")) {
            AppLog.e(TAG, "amountMinUnitToMajor input arg is illegal");
            throw new IllegalArgumentException("amountMinUnitToMajor input arg is illegal");
        }
        // ȥ��ͷβ���ܵĿո�
        String temp = minUnit.trim();
        temp = Long.parseLong(temp) + "";// ɾ��minUnit��ǰ��0
        if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_2) {
            if (temp.length() < 3) {
                int add = 3 - temp.length();
                for (int i = 0; i < add; i++) {
                    temp = "0" + temp;
                }
            }
            temp = temp.substring(0, temp.length() - 2) + "." + temp.substring(temp.length() - 2);
        } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_1) {
            if (temp.length() < 2) {
                int add = 2 - temp.length();
                for (int i = 0; i < add; i++) {
                    temp = "0" + temp;
                }
            }
            temp = temp.substring(0, temp.length() - 1) + "." + temp.substring(temp.length() - 1);
        } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_3) {
            if (temp.length() < 4) {
                int add = 4 - temp.length();
                for (int i = 0; i < add; i++) {
                    temp = "0" + temp;
                }
            }
            temp = temp.substring(0, temp.length() - 3) + "." + temp.substring(temp.length() - 3);
        }
        if (isCommaStyle == true) {
            int index = temp.indexOf(".");
            String strAdd = "";
            if (index != -1) {
                strAdd = temp.substring(index);
                temp = temp.substring(0, index);
            }
            temp = new StringBuffer(temp).reverse().toString(); // �Ƚ��ַ����ߵ�˳��
            String str2 = "";
            int size = (temp.length() % 3 == 0) ? (temp.length() / 3) : (temp.length() / 3 + 1); // ÿ��λȡһ����
            /*
             * �����һ���ַ����ֳ�n��,��n�ο��ܲ���������,�п�����һ����������, �ֽ��ַ����ֳ�������.һ����Ϊǰn-1��,�ڶ�����Ϊ��n��.ǰn-1�Σ�ÿһ�μ�һ",".����n��ֱ��ȡ������
             */
            for (int i = 0; i < size - 1; i++) { // ǰn-1��
                str2 += temp.substring(i * 3, i * 3 + 3) + ",";
            }
            for (int i = size - 1; i < size; i++) { // ��n��
                str2 += temp.substring(i * 3, temp.length());
            }
            str2 = new StringBuffer(str2).reverse().toString();
            str2 += strAdd;
            return str2;
        }
        return temp;
    }

}
