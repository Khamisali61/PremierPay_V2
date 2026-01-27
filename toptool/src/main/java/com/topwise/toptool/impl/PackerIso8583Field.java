package com.topwise.toptool.impl;


import com.topwise.toptool.api.utils.AppLog;

import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.IIso8583;
import com.topwise.toptool.api.packer.IIso8583.IIso8583Entity.EVarLenFormat;
import com.topwise.toptool.api.packer.IIso8583.IIso8583Entity.IFieldAttrs;
import com.topwise.toptool.api.packer.IIso8583.IIso8583Entity.IFieldAttrs.EPaddingPosition;
import com.topwise.toptool.api.packer.Iso8583Exception;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FieldAttr {
    private static final String TAG = FieldAttr.class.getSimpleName();

    enum Iso8583FieldValueType {
        A,
        N, // right-aligned
        S,
        AN,
        AS,
        // NS,
        ANS,
        B,
        Z,
    }

    enum Iso8583FieldLenType {
        FIXED,
        LVAR,
        LLVAR,
        LLLVAR,
    }

    private String format; // the source format string provided by user, for packing/unpacking

    // NOTE: vt, lt & len are mandatary! for packging/unpacking, they are parsed from 'format' string
    private Iso8583FieldValueType vt;
    private Iso8583FieldLenType lt;
    private int len;

    private String paddingChar;
    private EPaddingPosition pp;

    public FieldAttr() {
        vt = null;
        lt = null;
        pp = null;
        len = 0;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format
     *            the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the paddingChar
     */
    public String getPaddingChar() {
        return paddingChar;
    }

    /**
     * @param paddingChar
     *            the paddingChar to set
     */
    public void setPaddingChar(String paddingChar) {
        this.paddingChar = paddingChar;
    }

    /**
     * @return the vt
     */
    public Iso8583FieldValueType getVt() {
        return vt;
    }

    /**
     * @param vt
     *            the vt to set
     */
    public void setVt(Iso8583FieldValueType vt) {
        this.vt = vt;
    }

    /**
     * @return the lt
     */
    public Iso8583FieldLenType getLt() {
        return lt;
    }

    /**
     * @param lt
     *            the lt to set
     */
    public void setLt(Iso8583FieldLenType lt) {
        this.lt = lt;
    }

    /**
     * @return the pp
     */
    public EPaddingPosition getPp() {
        return pp;
    }

    /**
     * @param pp
     *            the pp to set
     */
    public void setPp(EPaddingPosition pp) {
        this.pp = pp;
    }

    /**
     * @return the len
     */
    public int getLen() {
        return len;
    }

    /**
     * @param len
     *            the len to set
     */
    public void setLen(int len) {
        this.len = len;
    }

    public String toString() {
        return "Format: " + format + ";  Value type:" + vt + ";  Len type: " + lt + ";  Len: " + len
                + "\nPadding pos: " + pp + ";  Padding char:" + paddingChar + "\n";
    }

    public void reset() {
        format = null;
        paddingChar = null;
        vt = null;
        lt = null;
        pp = null;
        len = 0;
    }

    public void parseFormat(IIso8583.IIso8583Entity.EVarLenFormat vlf, String format) throws Iso8583Exception {
        if (format == null || format.length() == 0) {
            AppLog.d(TAG, "format is null, ignored!");
            // the unpack format is optional, so ignore it and return
            // throw new Iso8583Exception(Iso8583Exception.ERR_ARG);
            return;
        }

        AppLog.d(TAG, "processing format " + format);

        Pattern p = Pattern.compile("^([ANSans]{1,3}|[ZBzb]{1})([.]{0,3})([0-9]{1,3})$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(format);
        String type = "";
        String varLen = "";
        String slen = "";
        if (m.find()) {
            type = m.group(1);
            varLen = m.group(2);
            slen = m.group(3);
        } else {
            AppLog.e(TAG, "parse format error!");
            throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_FORMAT, "format string: " + format);
        }
        /*
         * this is no use, trust regex... if (type.isEmpty() || slen.isEmpty()) { AppLog.e(TAG, "parse type error!");
         * throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_FORMAT); }
         */
        if (type.equalsIgnoreCase("A")) {
            vt = Iso8583FieldValueType.A;
        } else if (type.equalsIgnoreCase("N")) {
            vt = Iso8583FieldValueType.N;
        } else if (type.equalsIgnoreCase("S")) {
            vt = Iso8583FieldValueType.S;
        } else if (type.equalsIgnoreCase("AN")) {
            vt = Iso8583FieldValueType.AN;
        } else if (type.equalsIgnoreCase("AS")) {
            vt = Iso8583FieldValueType.AS;
        } /*
         * else if (type.equalsIgnoreCase("NS")) { vt = Iso8583FieldValueType.NS; }
         */else if (type.equalsIgnoreCase("ANS")) {
            vt = Iso8583FieldValueType.ANS;
        } else if (type.equalsIgnoreCase("B")) {
            vt = Iso8583FieldValueType.B;
        } else if (type.equalsIgnoreCase("Z")) {
            vt = Iso8583FieldValueType.Z;
        } else {
            AppLog.e(TAG, "format type " + type + " error!");
            throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_FORMAT, "format type " + type + " error!");
        }

        if (varLen.isEmpty()) {
            lt = Iso8583FieldLenType.FIXED;
        } else if (varLen.length() == 1) {
            lt = Iso8583FieldLenType.LVAR;
        } else if (varLen.length() == 2) {
            lt = Iso8583FieldLenType.LLVAR;
        } else {
            lt = Iso8583FieldLenType.LLLVAR;
        }

        this.len = Integer.valueOf(slen);
        if (vlf != IIso8583.IIso8583Entity.EVarLenFormat.BIN) {
            if ((lt == Iso8583FieldLenType.LVAR) && (this.len >= 10) || (lt == Iso8583FieldLenType.LLVAR)
                    && (this.len >= 100) || (lt == Iso8583FieldLenType.LLLVAR) && (this.len >= 1000)) {
                AppLog.e(TAG, "format type " + type + " length " + this.len + " error!");

                throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_FORMAT, "format type " + type + " length "
                        + this.len + " error!");
            }
        } else {
            if ((lt == Iso8583FieldLenType.LVAR) && (this.len >= 0x10) || (lt == Iso8583FieldLenType.LLVAR)
                    && (this.len >= 0x100) || (lt == Iso8583FieldLenType.LLLVAR) && (this.len >= 0x10000)) {
                AppLog.e(TAG, "format type " + type + " length " + this.len + " error!");

                throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_FORMAT, "format type " + type + " length "
                        + this.len + " error!");
            }
        }

    }

}

class FieldAttrs implements IIso8583.IIso8583Entity.IFieldAttrs {
    private String description; // the description of the field, mainly for debug purpose

    private FieldAttr fa = new FieldAttr();
    private FieldAttr faUnpack = new FieldAttr();

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public IFieldAttrs setVarLenFormat(EVarLenFormat param2EVarLenFormat) {
        return null;
    }

    @Override
    public EVarLenFormat getVarLenFormat() {
        return null;
    }

    @Override
    public IFieldAttrs setVarLenFormatUnpack(EVarLenFormat param2EVarLenFormat) {
        return null;
    }

    @Override
    public EVarLenFormat getVarLenFormatUnpack() {
        return null;
    }

    @Override
    public String getFormat() {
        return fa.getFormat();
    }

    @Override
    public String getFormatUnpack() {
        return faUnpack.getFormat();
    }

    @Override
    public String getPaddingChar() {
        return fa.getPaddingChar();
    }

    @Override
    public String getPaddingCharUnpack() {
        return faUnpack.getPaddingChar();
    }

    @Override
    public EPaddingPosition getPaddingPosition() {
        return fa.getPp();
    }

    @Override
    public EPaddingPosition getPaddingPositionUnpack() {
        return faUnpack.getPp();
    }

    @Override
    public IFieldAttrs setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public IFieldAttrs setFormat(String format) {
        fa.setFormat(format);
        return this;
    }

    @Override
    public IFieldAttrs setFormatUnpack(String formatUnpack) {
        faUnpack.setFormat(formatUnpack);
        return this;
    }

    @Override
    public IFieldAttrs setPaddingChar(String p) {
        fa.setPaddingChar(p);
        return this;
    }

    @Override
    public IFieldAttrs setPaddingCharUnpack(String p) {
        faUnpack.setPaddingChar(p);
        return this;
    }

    @Override
    public IFieldAttrs setPaddingPosition(EPaddingPosition paddingPosition) {
        fa.setPp(paddingPosition);
        return this;
    }

    @Override
    public IFieldAttrs setPaddingPositionUnpack(EPaddingPosition paddingPositionUnpack) {
        faUnpack.setPp(paddingPositionUnpack);
        return this;
    }

    public String toString() {
        return "Packing: " + fa + "\nUnpacking: " + faUnpack;
    }

    public FieldAttr getFieldAttrForPack() {
        return fa;
    }

    public FieldAttr getFieldAttrForUnpack() {
        return faUnpack;
    }

    public void resetAll() {
        fa.reset();
        faUnpack.reset();
    }
}

class PackerIso8583Field {
    private static final String TAG = "Iso8583Field";

    private final String idx;
    private FieldAttrs fas;
    private byte[] value;

    public PackerIso8583Field(String idx) {
        this.idx = idx;
        fas = new FieldAttrs();
    }

    PackerIso8583Field(String idx, String format, String formatUnpack, String description) {
        this.idx = idx;
        fas.setFormat(format);
        fas.setFormatUnpack(formatUnpack);
        fas.setDescription(description);
    }

    PackerIso8583Field setAttrs(EVarLenFormat vlf, IFieldAttrs fieldAttrs) throws Iso8583Exception {
        fas.getFieldAttrForPack().setFormat(fieldAttrs.getFormat());
        fas.getFieldAttrForPack().parseFormat(vlf, fieldAttrs.getFormat());
        fas.getFieldAttrForPack().setPp(fieldAttrs.getPaddingPosition());
        fas.getFieldAttrForPack().setPaddingChar(fieldAttrs.getPaddingChar());

        fas.getFieldAttrForUnpack().setFormat(fieldAttrs.getFormatUnpack());
        fas.getFieldAttrForUnpack().parseFormat(vlf, fieldAttrs.getFormatUnpack());
        fas.getFieldAttrForUnpack().setPp(fieldAttrs.getPaddingPositionUnpack());
        fas.getFieldAttrForUnpack().setPaddingChar(fieldAttrs.getPaddingCharUnpack());

        if (fieldAttrs.getDescription() != null) {
            fas.setDescription(fieldAttrs.getDescription());
        }

        return this;
    }

    String getIdx() {
        return idx;
    }

    PackerIso8583Field set(String description) {
        fas.setDescription(description);
        return this;
    }

    PackerIso8583Field set(byte[] value) {
        this.value = value;
        return this;
    }

    byte[] getValue() {
        return value;
    }

    void resetValue() {
        value = null;
    }

    void resetAll() {
        // idx = null; //should NOT change idx?
        fas.resetAll();

        value = null;
    }

    boolean isPackFormatOk() {
        return ((fas.getFieldAttrForPack().getVt() != null) && (fas.getFieldAttrForPack().getLt() != null) && (fas
                .getFieldAttrForPack().getLen() > 0));
    }

    boolean isUnpackFormatOk() {
        return ((fas.getFieldAttrForUnpack().getVt() != null) && (fas.getFieldAttrForUnpack().getLt() != null) && (fas
                .getFieldAttrForUnpack().getLen() > 0));
    }

    boolean isValueOk() {
        return ((value != null)/* && (value.length > 0) */);
    }

    private byte[] genL(EVarLenFormat vlf, byte len) {
        if (vlf == EVarLenFormat.BCD) {
            return TopTool.getInstance().getConvert()
                    .strToBcd(String.format("%d", len), IConvert.EPaddingPosition.PADDING_LEFT);
        } else if (vlf == EVarLenFormat.ASC) {
            return String.format("%d", len).getBytes(Charset.forName("iso8859-1"));
        } else {
            return new byte[] { len };
        }
    }

    // returns:
    // int[0] - length of V (in unit of specified type. e.g. :for BCD, in nibbles)
    // int[1] - length of L
    private int[] parseL(EVarLenFormat vlf, byte[] L) {
        int[] ret = new int[2];

        if (vlf == EVarLenFormat.BCD) {
            ret[0] = L[0];
            ret[1] = 1;
        } else if (vlf == EVarLenFormat.ASC) {
            ret[0] = L[0] - '0';
            ret[1] = 1;
        } else {
            ret[0] = L[0];
            ret[1] = 1;
        }
        return ret;
    }

    private byte[] genLL(EVarLenFormat vlf, byte len) {
        if (vlf == EVarLenFormat.BCD) {
            return TopTool.getInstance().getConvert()
                    .strToBcd(String.format("%02d", len), IConvert.EPaddingPosition.PADDING_LEFT);
        } else if (vlf == EVarLenFormat.ASC) {
            return String.format("%02d", len).getBytes(Charset.forName("iso8859-1"));
        } else {
            return new byte[] { len };
        }
    }

    // returns:
    // int[0] - length of V (in unit of specified type. e.g. :for BCD, in nibbles)
    // int[1] - length of L
    private int[] parseLL(EVarLenFormat vlf, byte[] LL) {
        int[] ret = new int[2];

        if (vlf == EVarLenFormat.BCD) {
            byte[] Lt = new byte[1];
            Lt[0] = LL[0];
            ret[0] = Integer.valueOf(TopTool.getInstance().getConvert().bcdToStr(Lt));
            ret[1] = 1;
        } else if (vlf == EVarLenFormat.ASC) {
            byte[] Lt = new byte[2];
            Lt[0] = LL[0];
            Lt[1] = LL[1];
            ret[0] = Integer.valueOf(new String(Lt));
            ret[1] = 2;
        } else {
            ret[0] = LL[0];
            ret[1] = 1;
        }

        return ret;
    }

    // returns:
    // int[0] - length of V (in unit of specified type. e.g. :for BCD, in nibbles)
    // int[1] - length of L
    private int[] parseLLL(EVarLenFormat vlf, byte[] LLL) {
        int[] ret = new int[2];

        if (vlf == EVarLenFormat.BCD) {
            byte Lt[] = new byte[2];
            Lt[0] = LLL[0];
            Lt[1] = LLL[1];
            ret[0] = Integer.valueOf(TopTool.getInstance().getConvert().bcdToStr(Lt));
            ret[1] = 2;
        } else if (vlf == EVarLenFormat.ASC) {
            ret[0] = Integer.valueOf(new String(LLL));
            ret[1] = 3;
        } else {
            ret[0] = ((LLL[0] << 8) + LLL[1]);
            ret[1] = 2;
        }

        return ret;
    }

    private byte[] genLLL(EVarLenFormat vlf, int len) {
        if (vlf == EVarLenFormat.BCD) {
            return TopTool.getInstance().getConvert()
                    .strToBcd(String.format("%04d", len),IConvert.EPaddingPosition.PADDING_LEFT);
        } else if (vlf == EVarLenFormat.ASC) {
            return String.format("%03d", len).getBytes(Charset.forName("iso8859-1"));
        } else {
            byte[] ret = new byte[2];
            ret[0] = (byte) ((len >>> 8) & 0xfa);
            ret[1] = (byte) (len & 0xfa);
            return ret;
        }
    }

    private boolean isFixedLength(FieldAttr.Iso8583FieldLenType lt) {
        return (lt == FieldAttr.Iso8583FieldLenType.FIXED);
    }

    // NOTE: CUP has NO Z fixed length scenario, treat it as N
    private boolean isBCDFormat(FieldAttr.Iso8583FieldValueType vt) {
        return ((vt == FieldAttr.Iso8583FieldValueType.N) || (vt == FieldAttr.Iso8583FieldValueType.Z));
    }

    private boolean isBits(FieldAttr.Iso8583FieldValueType vt) {
        return (vt == FieldAttr.Iso8583FieldValueType.B);
    }

    // left-aligned, right padded with blank spaces
    // NOTE: 'NS' & 'S' NOT mentioned in CUP, treat it as Ax
    private boolean isByDefaultLeftAlignedChars(FieldAttr.Iso8583FieldValueType vt) {
        return ((vt == FieldAttr.Iso8583FieldValueType.A) || (vt == FieldAttr.Iso8583FieldValueType.AN)
                || (vt == FieldAttr.Iso8583FieldValueType.ANS) || (vt == FieldAttr.Iso8583FieldValueType.AS)
                /* || (vt == Iso8583FieldValueType.NS) || (vt == Iso8583FieldValueType.S) */);
    }

    private boolean isNeedProvidingExactValueLength(FieldAttr.Iso8583FieldValueType vt) {
        return ((vt == FieldAttr.Iso8583FieldValueType.S) || (vt == FieldAttr.Iso8583FieldValueType.B));
    }

    private byte[] genLV(EVarLenFormat vlf, byte[] encodedValue, int lenInUnitOfTheType) throws Iso8583Exception {
        byte[] ret = null;
        byte[] l = null;
        if (fas.getFieldAttrForPack().getLt() == FieldAttr.Iso8583FieldLenType.LVAR) {
            l = genL(vlf, (byte) lenInUnitOfTheType); // (byte)encodedValue.length);
        } else if (fas.getFieldAttrForPack().getLt() == FieldAttr.Iso8583FieldLenType.LLVAR) {
            l = genLL(vlf, (byte) lenInUnitOfTheType); // (byte)encodedValue.length);
        } else if (fas.getFieldAttrForPack().getLt() == FieldAttr.Iso8583FieldLenType.LLLVAR) {
            l = genLLL(vlf, lenInUnitOfTheType); // encodedValue.length);
        } else {
            throw new RuntimeException("Invalid len type"); // should NOT happen!
        }

        ret = new byte[l.length + encodedValue.length];
        System.arraycopy(l, 0, ret, 0, l.length);
        System.arraycopy(encodedValue, 0, ret, l.length, encodedValue.length);

        return ret;
    }

    // returns unpacked bytes, for LV format, including L & V
    private byte[] parseLV(EVarLenFormat vlf, byte[] in, int offset, int[] unpackedBytes) throws Iso8583Exception {
        // maximum l is 3 bytes
        int lBytes = 3;
        if (in.length - offset < 3) { // check maximum bytes available
            lBytes = in.length - offset;
        }
        byte[] L = new byte[lBytes];
        System.arraycopy(in, offset, L, 0, L.length);
        int lens[];
        int lenOfL;
        int lenOfV;
        int bytesOfV;

        if (fas.getFieldAttrForUnpack().getLt() == FieldAttr.Iso8583FieldLenType.LVAR) {
            lens = parseL(vlf, L);
        } else if (fas.getFieldAttrForUnpack().getLt() == FieldAttr.Iso8583FieldLenType.LLVAR) {
            lens = parseLL(vlf, L);
        } else if (fas.getFieldAttrForUnpack().getLt() == FieldAttr.Iso8583FieldLenType.LLLVAR) {
            lens = parseLLL(vlf, L);
        } else {
            throw new RuntimeException("Field: " + idx + "Invalid len type"); // should NOT happen!
        }

        lenOfV = lens[0];
        lenOfL = lens[1];

        // determine the real bytes occupied for each value type
        if (isBCDFormat(fas.getFieldAttrForUnpack().getVt())) {
            bytesOfV = (lenOfV + 1) / 2;
        } else if (isBits(fas.getFieldAttrForUnpack().getVt())) {
            bytesOfV = (lenOfV + 7) / 8;
        } else {
            bytesOfV = lenOfV;
        }

        // valid the length
        if (offset + lenOfL + bytesOfV > in.length) {
            AppLog.e(TAG, "Field: " + idx + ", parseLV, data offset(" + offset + ") + lenOfL(" + lenOfL
                    + ") + bytesOfV(" + bytesOfV + ") > " + in.length);

            throw new Iso8583Exception(Iso8583Exception.ERR_UNPACK_DATA_OUT_OF_RANGE, "Field: " + idx
                    + ", parseLV, data offset(" + offset + ") + lenOfL(" + lenOfL + ") + bytesOfV(" + bytesOfV + ") > "
                    + in.length);
        }

        unpackedBytes[0] = (lenOfL + bytesOfV);

        byte[] out = new byte[bytesOfV];
        System.arraycopy(in, offset + lenOfL, out, 0, bytesOfV);

        if (isBCDFormat(fas.getFieldAttrForUnpack().getVt())) {
            String s = TopTool.getInstance().getConvert().bcdToStr(out);
            if (s.length() > lenOfV) {
                byte[] real = new byte[lenOfV];
                System.arraycopy(s.getBytes(Charset.forName("iso8859-1")), 0, real, 0, lenOfV); // copy left most bytes
                return real;

            } else if (s.length() < lenOfV) {
                throw new RuntimeException("Field: " + idx + "Invalid value length");
            } else {
                return s.getBytes(Charset.forName("iso8859-1"));
            }
        }

        return out;
    }

    private byte[] packValue(EVarLenFormat vlf) throws Iso8583Exception {
        byte[] ret = null;
        int maxLen = fas.getFieldAttrForPack().getLen();
        FieldAttr.Iso8583FieldValueType vt = fas.getFieldAttrForPack().getVt();
        FieldAttr.Iso8583FieldLenType lt = fas.getFieldAttrForPack().getLt();
        EPaddingPosition pp = fas.getFieldAttrForPack().getPp();
        String pc = fas.getFieldAttrForPack().getPaddingChar();

        // N/Z
        if (isBCDFormat(vt)) {
            if (value.length > maxLen) {
                AppLog.e(TAG, "Field: " + idx + ", pack, value too long: " + value.length + " > " + maxLen);
                throw new Iso8583Exception(Iso8583Exception.ERR_PACK_FIELD_VALUE_TOO_LONG, "Field: " + idx
                        + ", pack, value too long: " + value.length + " > " + maxLen);
            }

            if (isFixedLength(lt)) {
                /*
                 * char paddingChar = '0'; if (pc != null) { paddingChar = pc.toCharArray()[0]; }
                 */
                byte[] paddedBytes = new byte[maxLen];
                for (int i = 0; i < maxLen - value.length; i++) {
                    paddedBytes[i] = '0';
                }
                System.arraycopy(value, 0, paddedBytes, maxLen - value.length, value.length);

                byte[] bcd;
                if (pp == null || pp == EPaddingPosition.PADDING_LEFT) { // default to padding left
                    // left-padded with one 0 nibble.
                    bcd = TopTool.getInstance()
                            .getConvert()
                            .strToBcd(new String(paddedBytes),
                                    IConvert.EPaddingPosition.PADDING_LEFT);
                } else {
                    // right-padded with one 0 nibble.
                    bcd = TopTool.getInstance()
                            .getConvert()
                            .strToBcd(new String(paddedBytes),
                                    IConvert.EPaddingPosition.PADDING_RIGHT);
                }

                ret = new byte[(maxLen + 1) / 2];
                System.arraycopy(bcd, 0, ret, 0, bcd.length);

            } else {
                byte[] bcd = TopTool.getInstance().getConvert()
                        .strToBcd(new String(value), IConvert.EPaddingPosition.PADDING_RIGHT);
                ret = genLV(vlf, bcd, value.length);
            }
        } else if (isByDefaultLeftAlignedChars(vt)) { // A/AN/ANS/AS
            if (value.length > maxLen) {
                AppLog.e(TAG, "Field: " + idx + ", pack, value too long: " + value.length + " > " + maxLen);
                throw new Iso8583Exception(Iso8583Exception.ERR_PACK_FIELD_VALUE_TOO_LONG, "Field: " + idx
                        + ", pack, value too long: " + value.length + " > " + maxLen);
            }

            // default padding char is ' ' (blank space)
            byte paddingChar = ' ';
            if (pc != null) {
                paddingChar = pc.getBytes()[0];
            }

            EPaddingPosition realpp = EPaddingPosition.PADDING_RIGHT;
            if (pp != null) {
                realpp = pp;
            }

            if (isFixedLength(lt)) {
                ret = new byte[maxLen];
                if (realpp == EPaddingPosition.PADDING_RIGHT) {
                    System.arraycopy(value, 0, ret, 0, value.length);
                    for (int i = value.length; i < ret.length; i++) {
                        ret[i] = paddingChar;
                    }
                } else {
                    for (int i = 0; i < ret.length - value.length; i++) {
                        ret[i] = paddingChar;
                    }
                    System.arraycopy(value, 0, ret, ret.length - value.length, value.length);
                }
            } else {
                ret = genLV(vlf, value, value.length);
            }
        } else if (isBits(vt)) { // B
            int bytes = ((maxLen + 7) / 8);
            if (value.length > bytes) {
                AppLog.e(TAG, "Field: " + idx + ", pack, value too long: " + value.length + " > " + bytes);
                throw new Iso8583Exception(Iso8583Exception.ERR_PACK_FIELD_VALUE_TOO_LONG, "Field: " + idx
                        + ", pack, value too long: " + value.length + " > " + bytes);
            }

            // "B" requires exact value length
            if (isFixedLength(lt)) {
                if (value.length != bytes) {
                    AppLog.e(TAG, "Field: " + idx + ", pack, value length NOT equals: " + value.length + " != "
                            + bytes);
                    throw new Iso8583Exception(Iso8583Exception.ERR_PACK_FIELD_VALUE_LENGTH_NOT_EQUAL, "Field: " + idx
                            + ", pack, value NOT equals: " + value.length + " != " + bytes);
                }

                ret = new byte[bytes];
                System.arraycopy(value, 0, ret, 0, bytes);
            } else {
                ret = genLV(vlf, value, value.length * 8);
            }
        } else {
            if (value.length > maxLen) {
                AppLog.e(TAG, "Field: " + idx + ", pack, value too long: " + value.length + " > " + maxLen);
                throw new Iso8583Exception(Iso8583Exception.ERR_PACK_FIELD_VALUE_TOO_LONG, "Field: " + idx
                        + ", pack, value too long: " + value.length + " > " + maxLen);
            }

            // requires exact value length
            if (isFixedLength(lt)) {
                if (value.length != maxLen) {
                    AppLog.e(TAG, "Field: " + idx + ", pack, value length NOT equals: " + value.length + " != "
                            + maxLen);
                    throw new Iso8583Exception(Iso8583Exception.ERR_PACK_FIELD_VALUE_LENGTH_NOT_EQUAL, "Field: " + idx
                            + ", pack, value NOT equals: " + value.length + " != " + maxLen);
                }

                ret = new byte[maxLen];
                System.arraycopy(value, 0, ret, 0, maxLen);
            } else {
                ret = genLV(vlf, value, value.length);
            }
        }
        return ret;
    }

    byte[] pack(EVarLenFormat vlf) throws Iso8583Exception {
        if (!isPackFormatOk()) {
            AppLog.e(TAG, "Field: " + idx + ", pack, format not set!");
            throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_FORMAT, "Field: " + idx + ", pack, format not set!");
        }

        if (!isValueOk()) {
            AppLog.e(TAG, "Field: " + idx + ", pack, value not set!");
            throw new Iso8583Exception(Iso8583Exception.ERR_PACK_FIELD_NO_VALUE, "Field: " + idx
                    + ", pack, value not set!");
        }

        return packValue(vlf);
    }

    private int unpackValue(EVarLenFormat vlf, byte[] buffer, int offset) throws Iso8583Exception {
        byte[] in = null;
        int unpackedBytes = 0;
        int maxLen = fas.getFieldAttrForUnpack().getLen();
        FieldAttr.Iso8583FieldValueType vt = fas.getFieldAttrForUnpack().getVt();
        FieldAttr.Iso8583FieldLenType lt = fas.getFieldAttrForUnpack().getLt();
        EPaddingPosition pp = fas.getFieldAttrForUnpack().getPp();
        String pc = fas.getFieldAttrForUnpack().getPaddingChar();
        int[] upBytes = new int[1];

        // handle LV
        if (!isFixedLength(lt)) {
            value = parseLV(vlf, buffer, offset, upBytes);
            unpackedBytes = upBytes[0];
            return unpackedBytes;
        }

        // handle fixed length
        if (isBCDFormat(vt)) { // B/Z
            in = new byte[(maxLen + 1) / 2];
            if (offset + in.length > buffer.length) {
                AppLog.e(TAG, "Field: " + idx + ", unpack, length out of range: " + offset + in.length + " > "
                        + buffer.length);
                throw new Iso8583Exception(Iso8583Exception.ERR_UNPACK_DATA_OUT_OF_RANGE, "Field: " + idx
                        + ", unpack, length out of range: " + offset + in.length + " > " + buffer.length);
            }

            System.arraycopy(buffer, offset, in, 0, in.length);
            String s = TopTool.getInstance().getConvert().bcdToStr(in);

            // remove padded nibble
            if (s.length() > maxLen) {
                if (pp == null || pp == EPaddingPosition.PADDING_LEFT) {
                    s = s.replaceFirst("^[0]", "");
                } else {
                    s = s.replaceFirst("[0]$", "");
                }
            }

            value = s.getBytes(Charset.forName("iso8859-1"));
            unpackedBytes = in.length;
        } else if (isByDefaultLeftAlignedChars(vt)) { // A/AN/ANS/AS
            in = new byte[maxLen];
            if (offset + in.length > buffer.length) {
                AppLog.e(TAG, "Field: " + idx + ", unpack, length out of range: " + offset + in.length + " > "
                        + buffer.length);
                throw new Iso8583Exception(Iso8583Exception.ERR_UNPACK_DATA_OUT_OF_RANGE, "Field: " + idx
                        + ", unpack, length out of range: " + offset + in.length + " > " + buffer.length);
            }

            // default padding char is ' ' (blank space)
            byte paddingChar = ' ';
            if (pc != null) {
                paddingChar = pc.getBytes()[0];
            }

            EPaddingPosition realpp = EPaddingPosition.PADDING_RIGHT;
            if (pp != null) {
                realpp = pp;
            }

            int realLen = in.length;
            int realOffset = 0;

            System.arraycopy(buffer, offset, in, 0, in.length);
            if (realpp == EPaddingPosition.PADDING_RIGHT) {
                // remove right padded padding chars
                for (int i = in.length - 1; i >= 0; i--) {
                    if (in[i] == paddingChar) {
                        realLen--;
                    }
                }
            } else {
//                AppLog.d(TAG, " paddingChar: " + paddingChar);
//                // remove left padded padding chars
//                for (int i = 0; i < in.length; i++) {
//                    AppLog.d(TAG, " in[i]: " + in[i]);
//                    AppLog.d(TAG, " in[i] == paddingChar: " + (in[i] == paddingChar));
//                    if (in[i] == paddingChar) {
//                        realLen--;
//                        realOffset++;
//                    }
//                }
                while (realOffset < in.length && in[realOffset] == paddingChar) {
                    realOffset++;
                }
                realLen = in.length - realOffset;
            }

            AppLog.d(TAG,
                    String.format("total len %d, real len %d, real offset %d\n", in.length, realLen, realOffset));
            value = new byte[realLen];
            System.arraycopy(in, realOffset, value, 0, realLen);
            AppLog.d(TAG, "p=== value " + TopTool.getInstance().getConvert().bcdToStr(value));
            /*
             * String s = new String(in, Charset.forName("iso8859-1")); if (realpp == EPaddingPosition.PADDING_RIGHT) {
             * // remove right padded padding chars String regex = String.format("[%c]+$", paddingChar); s =
             * s.replaceAll(regex, ""); } else { // remove left padded padding chars String regex =
             * String.format("^[%c]+", paddingChar); s = s.replaceAll(regex, ""); } value =
             * s.getBytes(Charset.forName("iso8859-1"));
             */

            unpackedBytes = in.length;
        } else if (isBits(vt)) { // B
            int bytes = (maxLen + 7) / 8;
            in = new byte[bytes];
            if (offset + in.length > buffer.length) {
                AppLog.e(TAG, "Field: " + idx + ", unpack, length out of range: " + offset + in.length + " > "
                        + buffer.length);
                throw new Iso8583Exception(Iso8583Exception.ERR_UNPACK_DATA_OUT_OF_RANGE, "Field: " + idx
                        + ", unpack, length out of range: " + offset + in.length + " > " + buffer.length);
            }
            System.arraycopy(buffer, offset, in, 0, in.length);

            value = in;
            unpackedBytes = in.length;
            AppLog.d(TAG, "p=== value isBits " + TopTool.getInstance().getConvert().bcdToStr(value));
        } else {
            in = new byte[maxLen];
            if (offset + in.length > buffer.length) {
                AppLog.e(TAG, "Field: " + idx + ", unpack, length out of range: " + offset + in.length + " > "
                        + buffer.length);
                throw new Iso8583Exception(Iso8583Exception.ERR_UNPACK_DATA_OUT_OF_RANGE, "Field: " + idx
                        + ", unpack, length out of range: " + offset + in.length + " > " + buffer.length);
            }
            System.arraycopy(buffer, offset, in, 0, in.length);

            value = in;
            unpackedBytes = in.length;
            AppLog.d(TAG, "p=== value others " + TopTool.getInstance().getConvert().bcdToStr(value));
        }

        return unpackedBytes;
    }

    /**
     * the unpack format is optional, if not set, before unpacking, copy from pack format if neccessary
     */
    void copyPackFormatToUnpackFormatIfNeccessaryBeforeUnpacking() {
        // copy mandatory args for unpacking
        if (!isUnpackFormatOk()) {
            fas.getFieldAttrForUnpack().setLen(fas.getFieldAttrForPack().getLen());
            fas.getFieldAttrForUnpack().setLt(fas.getFieldAttrForPack().getLt());
            fas.getFieldAttrForUnpack().setVt(fas.getFieldAttrForPack().getVt());
        }

        // copy padding postion, if pp for packing is set AND pp for unpacking is NOT set
        if ((fas.getFieldAttrForPack().getPp() != null) && (fas.getFieldAttrForUnpack().getPp() == null)) {
            fas.getFieldAttrForUnpack().setPp(fas.getFieldAttrForPack().getPp());
        }

        // copy padding char, if pc for packing is set AND pc for unpacking is NOT set
        if ((fas.getFieldAttrForPack().getPaddingChar() != null)
                && (fas.getFieldAttrForUnpack().getPaddingChar() == null)) {
            fas.getFieldAttrForUnpack().setPaddingChar(fas.getFieldAttrForPack().getPaddingChar());
        }

    }

    int unpack(EVarLenFormat vlf, byte[] bufaer, int offset) throws Iso8583Exception {
        if (!isUnpackFormatOk()) {
            AppLog.e(TAG, "Field: " + idx + ", unpack, format not set!");
            throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_FORMAT, "Field: " + idx + ", unpack, format not set!");
        }

        return unpackValue(vlf, bufaer, offset);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Field: " + idx + ", (" + fas.getDescription() + ")\n").append(fas.getFieldAttrForPack())
                .append(fas.getFieldAttrForUnpack());

        if (value != null) {
            sb.append("Value\n[").append(new String(value)).append("]\n").append(byte2HexStr(value, 0, value.length))
                    .append("\n");
        }
        sb.append("------------------------\n");

        return sb.toString();
    }

    static String byte2HexStr(byte[] bytes, int offset, int len) {
        if (offset > bytes.length || offset + len > bytes.length) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        for (int i = 0; i < len; i++) {
            sb.append(Integer.toHexString(bytes[i + offset] | 0xFFFFFF00).substring(6));
            sb.append(" ");
            if (((i + 1) % 16) == 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
