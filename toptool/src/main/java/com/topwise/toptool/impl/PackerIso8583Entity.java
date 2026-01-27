package com.topwise.toptool.impl;

import android.content.Context;
import android.util.Xml;

import com.topwise.toptool.api.packer.IIso8583;
import com.topwise.toptool.api.packer.Iso8583Exception;
import com.topwise.toptool.api.utils.AppLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

public class PackerIso8583Entity implements IIso8583.IIso8583Entity {
    private static final String TAG = PackerIso8583Entity.class.getSimpleName();

    private static PackerIso8583Entity instance;


    private boolean isSecondaryBitmapOn = false; // this is set by user
    private boolean isSupportTertiaryBitmap = false;
    private EVarLenFormat varLenFormat = EVarLenFormat.BCD;

    private static final int SECONDARY_BITMAP_ID = 1;
    private static final int TERTIARY_BITMAP_ID = 65;
    private byte[] pBitmap = new byte[8]; // primary bitmap
    private boolean hasSBitmap = false; // has secondary bitmap or not?
    private byte[] sBitmap = new byte[8]; // secondary bitmap
    private boolean hasTBitmap = false; // has tertiary bitmap or not?
    private byte[] tBitmap = new byte[8]; // tertiary bitmap

    private PackerIso8583Field header;
    private PackerIso8583Field msgId;
    private PackerIso8583Field[] fields;

    private static final String FIELD_ID_HEADER = "h";
    private static final String FIELD_ID_MSGID = "m";

    private static final int BASE_STANDARD_FIELD_NUMBER = 64;
    private static final int EXTENDED_STANDARD_FIELD_NUMBER = 128;
    private static final int MAX_STANDARD_FIELD_NUMBER = 192;

    private static final String XML_TAG_ISO8583 = "iso8583";
    private static final String XML_TAG_FIELD = "field";

    private static final String XML_ATTR_NAME_FIELD_ID = "id";

    private static final String XML_ATTR_NAME_SECONDARY_BITMAP = "secondary_bitmap";
    private static final String XML_ATTR_NAME_SUPPORT_TERTIARY_BITMAP = "support_tertiary_bitmap";
    private static final String XML_ATTR_NAME_VAR_LEN_FORMAT = "var_len_format";

    private static final String XML_ATTR_NAME_FIELD_FORMAT = "format";
    private static final String XML_ATTR_NAME_FIELD_PADDING_POS = "paddingpos";
    private static final String XML_ATTR_NAME_FIELD_PADDING_CHAR = "paddingchar";
    private static final String XML_ATTR_NAME_FIELD_DESCRIPTION = "desc";

    private static final String XML_ATTR_NAME_FIELD_FORMAT_UNAPCK = "format_unpack";
    private static final String XML_ATTR_NAME_FIELD_PADDING_POS_UNPACK = "paddingpos_unpack";
    private static final String XML_ATTR_NAME_FIELD_PADDING_CHAR_UNPACK = "paddingchar_unpack";

    private PackerIso8583Entity() {
        resetAll();
    }

    public synchronized static PackerIso8583Entity getInstance() {
        if (instance == null) {
            instance = new PackerIso8583Entity();
        }

        return instance;
    }

//    @Override
//    public IIso8583.IIso8583Entity loadTemplate(InputStream is) throws Iso8583Exception, IOException,
//            XmlPullParserException {
////        InputStream is = context.getAssets().open(filePathInAssets);
//        loadTemplate(is);
//        return this;
//    }

    @Override
    public IIso8583.IIso8583Entity loadTemplate(InputStream is) throws Iso8583Exception, IOException, XmlPullParserException {
        // reset all before loading template to ensure the entity is in correct status
        resetAll();

        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(is, null);

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG) {
                String tag = xpp.getName();
//                AppLog.d(TAG, "tag is " + tag);
                if (tag.equals(XML_TAG_ISO8583)) { // global settings

                    resetDefaultGlobalParams();

                    int ac = xpp.getAttributeCount();
                    for (int i = 0; i < ac; i++) {
                        String an = xpp.getAttributeName(i);
                        if (an.equals(XML_ATTR_NAME_SECONDARY_BITMAP)) {
                            String av = xpp.getAttributeValue(i);
                            if (av.equalsIgnoreCase("YES")) {
                                isSecondaryBitmapOn = true;
                            } else {
                                isSecondaryBitmapOn = false;
                            }
                        } else if (an.equals(XML_ATTR_NAME_SUPPORT_TERTIARY_BITMAP)) {
                            String av = xpp.getAttributeValue(i);
                            if (av.equalsIgnoreCase("YES")) {
                                isSupportTertiaryBitmap = true;
                            } else {
                                isSupportTertiaryBitmap = false;
                            }
                        } else if (an.equalsIgnoreCase(XML_ATTR_NAME_VAR_LEN_FORMAT)) {
                            String av = xpp.getAttributeValue(i);
                            if (av.equalsIgnoreCase("BCD")) {
                                varLenFormat = EVarLenFormat.BCD;
                            } else if (av.equalsIgnoreCase("ASC")) {
                                varLenFormat = EVarLenFormat.ASC;
                            } else if (av.equalsIgnoreCase("BIN")) {
                                varLenFormat = EVarLenFormat.BIN;
                            } else {
                                AppLog.e(TAG, "var len format " + av + "invalid!");
                                throw new Iso8583Exception(Iso8583Exception.ERR_VAR_LEN_FORMAT, "arg is "
                                        + varLenFormat);
                            }
                        } else {
                            AppLog.w(TAG, "Ignoring attribute(" + an + ") for tag(" + tag + ")");
                        }
                    }
                } else if (tag.equals(XML_TAG_FIELD)) { // fields
                    // String field = parseFieldTag(tag);
                    // AppLog.d(TAG, "Processing field:" + field);

                    String fieldId = null;

                    int ac = xpp.getAttributeCount();
                    String format = null;
                    String description = null;
                    String paddingPos = null;
                    String paddingChar = null;

                    String formatunpack = null;
                    String paddingPosUnpack = null;
                    String paddingCharUnpack = null;

                    for (int i = 0; i < ac; i++) {
                        String an = xpp.getAttributeName(i);
                        if (an.equalsIgnoreCase(XML_ATTR_NAME_FIELD_ID)) {
                            fieldId = xpp.getAttributeValue(i);
                        } else if (an.equalsIgnoreCase(XML_ATTR_NAME_FIELD_DESCRIPTION)) {
                            description = xpp.getAttributeValue(i);
                        } else if (an.equalsIgnoreCase(XML_ATTR_NAME_FIELD_FORMAT)) {
                            format = xpp.getAttributeValue(i);
                        } else if (an.equalsIgnoreCase(XML_ATTR_NAME_FIELD_FORMAT_UNAPCK)) {
                            formatunpack = xpp.getAttributeValue(i);
                        } else if (an.equalsIgnoreCase(XML_ATTR_NAME_FIELD_PADDING_POS)) {
                            paddingPos = xpp.getAttributeValue(i);
                        } else if (an.equalsIgnoreCase(XML_ATTR_NAME_FIELD_PADDING_POS_UNPACK)) {
                            paddingPosUnpack = xpp.getAttributeValue(i);
                        } else if (an.equalsIgnoreCase(XML_ATTR_NAME_FIELD_PADDING_CHAR)) {
                            paddingChar = xpp.getAttributeValue(i);
                        } else if (an.equalsIgnoreCase(XML_ATTR_NAME_FIELD_PADDING_CHAR_UNPACK)) {
                            paddingCharUnpack = xpp.getAttributeValue(i);
                        } else {
                            AppLog.w(TAG, "Ignoring attribute(" + an + ") for tag(" + tag + ")");
                        }
                    }

                    validateFieldId(fieldId);

                    IFieldAttrs fa = createFieldAttrs();
                    fa.setDescription(description);

                    fa.setFormat(format);
                    fa.setFormatUnpack(formatunpack);

                    if (paddingPos == null) {
                        // default to null
                    } else if (paddingPos.equalsIgnoreCase("L")) {
                        fa.setPaddingPosition(IFieldAttrs.EPaddingPosition.PADDING_LEFT);
                    } else if (paddingPos.equalsIgnoreCase("R")) {
                        fa.setPaddingPosition(IFieldAttrs.EPaddingPosition.PADDING_RIGHT);
                    } else {
                        // default to null
                    }

                    if (paddingPosUnpack == null) {
                        // default to null
                    } else if (paddingPosUnpack.equalsIgnoreCase("L")) {
                        fa.setPaddingPositionUnpack(IFieldAttrs.EPaddingPosition.PADDING_LEFT);
                    } else if (paddingPosUnpack.equalsIgnoreCase("R")) {
                        fa.setPaddingPositionUnpack(IFieldAttrs.EPaddingPosition.PADDING_RIGHT);
                    } else {
                        // default to null
                    }

                    if (paddingChar != null) {
                        fa.setPaddingChar(paddingChar);
                    }

                    if (paddingCharUnpack != null) {
                        fa.setPaddingCharUnpack(paddingCharUnpack);
                    }

                    setFieldAttrs(fieldId, fa);

                    xpp.next();
                    String value = xpp.getText();
                    if (value != null) {
                        // AppLog.w(TAG, "To set field: " + field + " value: " + value);
                        setFieldValue(fieldId, value);
                    } else {
                        setFieldValue(fieldId, (byte[]) null);
                    }
                } else {
                    AppLog.w(TAG, "Ignoring tag " + tag);
                }

            } else if (eventType == XmlPullParser.END_TAG) {

            } else if (eventType == XmlPullParser.TEXT) {

            }
            eventType = xpp.next();
        }
        return this;
    }

    @Override
    public PackerIso8583Entity setSecondaryBitmapOnOff(boolean onOff) {
        isSecondaryBitmapOn = onOff;
        return this;
    }

    @Override
    public boolean getSecondaryBitmapOnOff() {
        return isSecondaryBitmapOn;
    }

    @Override
    public boolean isSupportTertiaryBitmap() {
        return isSupportTertiaryBitmap;
    }

    @Override
    public IIso8583.IIso8583Entity setSupportTertiaryBitmap(boolean flag) {
        isSupportTertiaryBitmap = flag;
        return this;
    }

    @Override
    public PackerIso8583Entity setVarLenFormat(EVarLenFormat vlf) {
        this.varLenFormat = vlf;
        return this;
    }

    @Override
    public EVarLenFormat getVarLenFormat() {
        return varLenFormat;
    }

    @Override
    public IFieldAttrs createFieldAttrs() {
        return new FieldAttrs();
    }

    @Override
    public PackerIso8583Entity setFieldAttrs(String fieldId, IFieldAttrs attrs) throws Iso8583Exception {
        int field = validateFieldId(fieldId);

        if (fieldId.equalsIgnoreCase(FIELD_ID_HEADER)) {
            header.setAttrs(varLenFormat, attrs);
        } else if (fieldId.equalsIgnoreCase(FIELD_ID_MSGID)) {
            msgId.setAttrs(varLenFormat, attrs);
        } else {
            if (isSupportTertiaryBitmap) {
                // NOTE: For field 65, setting the value & format will be ignored!
                if (field == TERTIARY_BITMAP_ID) {
                    AppLog.w(TAG, "setting field id 65 (tertiary bitmap) format ignored!");
                    return this;
                }
            }

            PackerIso8583Field f = fields[field];
            if (f != null) {
                f.setAttrs(varLenFormat, attrs);
            } else {
                PackerIso8583Field nf = new PackerIso8583Field(fieldId);
                nf.setAttrs(varLenFormat, attrs);
                fields[field] = nf;
            }
        }
        return this;
    }

    @Override
    public PackerIso8583Entity setFieldValue(String fieldId, String string) throws Iso8583Exception {
        if (string == null) {
            byte[] n = null;
            setFieldValue(fieldId, n);
        } else {
            setFieldValue(fieldId, string.getBytes(Charset.forName("utf-8")));
        }
        return this;
    }

    @Override
    public PackerIso8583Entity setFieldValue(String fieldId, byte[] value) throws Iso8583Exception {
        int field = validateFieldId(fieldId);

        if (fieldId.equalsIgnoreCase(FIELD_ID_HEADER)) {
            header.set(value);
        } else if (fieldId.equalsIgnoreCase(FIELD_ID_MSGID)) {
            msgId.set(value);
        } else {
            PackerIso8583Field f = fields[field];

            if (isSupportTertiaryBitmap) {
                // NOTE: For field 65, setting the value & format will be ignored!
                if (field == TERTIARY_BITMAP_ID) {
                    AppLog.w(TAG, "setting field id 65 (tertiary bitmap) value ignored!");
                    return this;
                }
            }

            if (f != null) {
                f.set(value);
            } else {
                PackerIso8583Field nf = new PackerIso8583Field(fieldId);
                nf.set(value);
                fields[field] = nf;
            }
        }
        return this;
    }

    @Override
    public boolean hasField(String fieldId) {
        int field = -1;
        try {
            field = validateFieldId(fieldId);
        } catch (Exception e) {
            return false;
        }

        if (fieldId.equalsIgnoreCase(FIELD_ID_HEADER)) {
            return header.isValueOk();
        } else if (fieldId.equalsIgnoreCase(FIELD_ID_MSGID)) {
            return msgId.isValueOk();
        } else {

            if (isSupportTertiaryBitmap) {
                // FIXME: SHOULD I ?
                if (field == TERTIARY_BITMAP_ID) {
                    return hasTBitmap;
                }
            }

            PackerIso8583Field f = fields[field];
            if (f == null) {
                return false;
            } else {
                return f.isValueOk();
            }
        }
    }

    @Override
    public IIso8583.IIso8583Entity resetFieldValue(String fieldId) throws Iso8583Exception {
        byte[] n = null;
        setFieldValue(fieldId, n);
        return this;
    }

    @Override
    public PackerIso8583Entity resetAllFieldsValue() {
        header.resetValue();
        msgId.resetValue();
        resetBitmap();

        for (int i = 0; i < fields.length; i++) {
            PackerIso8583Field f = fields[i];
            if (f != null) {
                f.resetValue();
            }
        }
        return this;
    }

    @Override
    public PackerIso8583Entity resetAll() {
        resetFields();
        resetBitmap();
        resetDefaultGlobalParams();
        return this;
    }

    @Override
    public PackerIso8583Entity dump() {

        String settings = String.format("secondary_bitmap = %s, ", isSecondaryBitmapOn ? "YES" : "NO")
                + String.format("support_tertiary_bitmap = %s ", isSupportTertiaryBitmap ? "YES" : "NO")
                + String.format("var_len_format = %s", varLenFormat.toString());
        AppLog.d(TAG, settings);

        AppLog.d(TAG, "<<=========== DUMPING FIELDS START =============");

        String hs = header.toString();
        String ms = msgId.toString();

        AppLog.d(TAG, hs);
        AppLog.d(TAG, ms);

        for (int i = 2; i < MAX_STANDARD_FIELD_NUMBER + 1; i++) {
            if (fields[i] != null && fields[i].getValue() != null) {
                String fs = fields[i].toString();
                AppLog.d(TAG, fs);
            }
        }
        AppLog.d(TAG, "=========== DUMPING FIELDS END   =============>>");
        return this;
    }

    public byte[] pack() throws Iso8583Exception {
        ByteBuffer bb = ByteBuffer.allocate(10240);
        bb.clear();

        // it's allowed to NOT pack header
        if (header.isPackFormatOk() && header.isValueOk()) {
            bb.put(header.pack(varLenFormat));
        }

        // msgId is mandatory
        bb.put(msgId.pack(varLenFormat));

        // primary bitmap
        int pBitmapPosition = bb.position();
        bb.put(pBitmap); // just for place holder, is temporarily 0s, set it later.

        // secondary bitmap (i.e. field 1)
        if (isSecondaryBitmapOn) {
            hasSBitmap = true;
            try {
                TopTool.getInstance().getAlgo().setBit(pBitmap, 1, (byte) 1);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Iso8583Exception(Iso8583Exception.ERR_BITMAP_OPERATION, "while setting secondary bitmap");
            }
            bb.put(sBitmap); // just for place holder, is temporarily 0s, set it later.
        }

        // pack fields
        int maxFields = getMaxFieldNum();
        if (isSupportTertiaryBitmap) {
            hasTBitmap = hasTertiaryFields();
        }
        AppLog.d(TAG, "pack(): isSecondaryBitmapOn:" + isSecondaryBitmapOn + ",isSupportTertiaryBitmap:"
                + isSupportTertiaryBitmap + ",hasTBitmap:" + hasTBitmap);
        boolean tBitmapPut = false;
        int tBitmapPosition = 0;

        for (int i = 2; i <= maxFields; i++) {
            if (hasFieldForPack("0" + i)) {
                // gen bitmaps
                if (i <= BASE_STANDARD_FIELD_NUMBER) {
                    TopTool.getInstance().getAlgo().setBit(pBitmap, i, (byte) 1);
                } else if (i <= EXTENDED_STANDARD_FIELD_NUMBER) {
                    if (isSupportTertiaryBitmap) {
                        // bypassing field 65
                        if (i == TERTIARY_BITMAP_ID) {
                            continue;
                        }
                    }

                    TopTool.getInstance().getAlgo().setBit(sBitmap, i - BASE_STANDARD_FIELD_NUMBER, (byte) 1);

                    if (isSupportTertiaryBitmap) {
                        // put field 65 right after the last field of primary stream
                        if (hasTBitmap && !tBitmapPut) {
                            tBitmapPut = true;
                            tBitmapPosition = bb.position();
                            TopTool.getInstance().getAlgo().setBit(sBitmap, 1, (byte) 1); // set 65 bit in secondary
                            // bitmap
                            bb.put(tBitmap); // field 65, place holder, update it later
                        }
                    }
                } else if (isSupportTertiaryBitmap) {
                    // Ooops, there can be NO secondary fields at all, put tBitmap here.
                    if (hasTBitmap && !tBitmapPut) {
                        tBitmapPut = true;
                        tBitmapPosition = bb.position();
                        TopTool.getInstance().getAlgo().setBit(sBitmap, 1, (byte) 1); // set 65 bit in secondary
                        // bitmap
                        bb.put(tBitmap); // field 65, place holder, update it later
                    }

                    TopTool.getInstance().getAlgo().setBit(tBitmap, i - EXTENDED_STANDARD_FIELD_NUMBER, (byte) 1); // set
                }

                AppLog.d(TAG, "to pack field " + i);
                bb.put(fields[i].pack(varLenFormat));
            }
        }

        // save the total length
        bb.flip();
        int totalLen = bb.limit();

        // update primary bitmap
        bb.position(pBitmapPosition);
        bb.put(pBitmap);

        // update secondary bitmap if necessary
        if (isSecondaryBitmapOn) {
            bb.position(pBitmapPosition + 8);
            bb.put(sBitmap);
        }

        // update tertiary bitmap if necessary
        if (isSupportTertiaryBitmap) {
            if (hasTBitmap) {
                bb.position(tBitmapPosition);
                bb.put(tBitmap);
            }
        }

        bb.position(0);
        byte[] ret = new byte[totalLen];
        bb.get(ret);
        return ret;
    }

    public byte[] pack(List<String> fieldIds) throws Iso8583Exception {
//        ByteBuffer bb = ByteBuffer.allocate(10240);
        ByteBuffer bb = ByteBuffer.allocate(102400);
        bb.clear();

        for (String fieldId : fieldIds) {
            if (fieldId.equals("h")) {
                if (header.isPackFormatOk() && header.isValueOk()) {
                    bb.put(header.pack(varLenFormat));
                }
            } else if (fieldId.equals("m")) {
                bb.put(msgId.pack(varLenFormat));
            } else {
                AppLog.d(TAG, "to pack field " + fieldId);
                int i = validateFieldId(fieldId);
                bb.put(fields[i].pack(varLenFormat));
            }
        }

        // save the total length
        bb.flip();
        int totalLen = bb.limit();

        bb.position(0);
        byte[] ret = new byte[totalLen];
        bb.get(ret);
        return ret;

    }

    private byte[] getField(String fieldId) {
        int field = -1;
        try {
            field = validateFieldId(fieldId);
        } catch (Exception e) {
            return null;
        }

        if (!hasField(fieldId)) {
            return null;
        }

        if (fieldId.equalsIgnoreCase(FIELD_ID_HEADER)) {
            return header.getValue();
        } else if (fieldId.equalsIgnoreCase(FIELD_ID_MSGID)) {
            return msgId.getValue();
        } else {
            if (isSupportTertiaryBitmap) {
                // FIXME: SHOULD I ?
                if (field == TERTIARY_BITMAP_ID) {
                    return tBitmap;
                }
            }

            PackerIso8583Field f = fields[field];
            if (f == null) {
                return null;
            } else {
                return f.getValue();
            }
        }
    }

    public HashMap<String, byte[]> unpack(byte[] message, boolean isWithHeader) throws Iso8583Exception {
        HashMap<String, byte[]> result = new HashMap<String, byte[]>();

        if (message == null) {
            throw new Iso8583Exception(Iso8583Exception.ERR_ARG, "unpack message is null!");
        }

        // reset all values first
        resetAllFieldsValue();

        // since unpack format is optional, copy format before unpacking
        copyAllFieldsPackFormatToUnpackFormatIfNecessaryBeforeUnpacking();

        int offset = 0;

        // header, it's allowed to have NO header
        // if (header.isUnpackFormatOk()) {
        if (isWithHeader) {
            offset += header.unpack(varLenFormat, message, offset);
            result.put(FIELD_ID_HEADER, getField(FIELD_ID_HEADER));
        }

        // msgId is mendatory
        offset += msgId.unpack(varLenFormat, message, offset);
        result.put(FIELD_ID_MSGID, getField(FIELD_ID_MSGID));

        // primary bitmap
        System.arraycopy(message, offset, pBitmap, 0, 8);
        offset += 8;
        AppLog.d(TAG, "p bitmap " + TopTool.getInstance().getConvert().bcdToStr(pBitmap));

        // secondary bitmap (opt.)
        hasTBitmap = false;
        if (isSecondaryBitmapOn) {
            System.arraycopy(message, offset, sBitmap, 0, 8);
            offset += 8;
            if (isSupportTertiaryBitmap) {
                hasTBitmap = ((sBitmap[0] & 0x80) != 0);
            }
        }
        AppLog.d(TAG, "unpack(): isSecondaryBitmapOn:" + isSecondaryBitmapOn + ",isSupportTertiaryBitmap:"
                + isSupportTertiaryBitmap + ",hasTBitmap:" + hasTBitmap);

        int max = getMaxFieldNum(hasTBitmap);

        for (int i = 2; i <= max; i++) {
            boolean hasValue = false;
            if (i <= BASE_STANDARD_FIELD_NUMBER) {
                if (TopTool.getInstance().getAlgo().getBit(pBitmap, i) == 1) {
                    if (fields[i] == null) {
                        AppLog.e(TAG, "pack: field " + i + " is null!");
                        throw new Iso8583Exception(Iso8583Exception.ERR_UNPACK_FIELD_FORMAT_NOT_SET, "pack: field " + i
                                + " is null!");
                    }
                    AppLog.d(TAG, "unpacking " + i);
                    offset += fields[i].unpack(varLenFormat, message, offset);
                    hasValue = true;
                }
            } else if (isSupportTertiaryBitmap && i == TERTIARY_BITMAP_ID) { // read tertiary bitmap
                if (hasTBitmap) {
                    System.arraycopy(message, offset, tBitmap, 0, 8);
                    offset += 8;
                    // hasValue = true; // do NOT expose tertiary bitmap! it's just for internal use!
                }
            } else if (i <= EXTENDED_STANDARD_FIELD_NUMBER) {
                if (TopTool.getInstance().getAlgo().getBit(sBitmap, (i - BASE_STANDARD_FIELD_NUMBER)) == 1) {
                    if (fields[i] == null) {
                        AppLog.e(TAG, "pack: field " + i + " is null!");
                        throw new Iso8583Exception(Iso8583Exception.ERR_UNPACK_FIELD_FORMAT_NOT_SET, "pack: field " + i
                                + " is null!");
                    }
                    AppLog.d(TAG, "unpacking " + i);
                    offset += fields[i].unpack(varLenFormat, message, offset);
                    hasValue = true;
                }
            } else {
                if (TopTool.getInstance().getAlgo().getBit(tBitmap, (i - EXTENDED_STANDARD_FIELD_NUMBER)) == 1) {
                    if (fields[i] == null) {
                        AppLog.e(TAG, "pack: field " + i + " is null!");
                        throw new Iso8583Exception(Iso8583Exception.ERR_UNPACK_FIELD_FORMAT_NOT_SET, "pack: field " + i
                                + " is null!");
                    }
                    AppLog.d(TAG, "unpacking " + i);
                    offset += fields[i].unpack(varLenFormat, message, offset);
                    hasValue = true;
                }
            }
            if (hasValue) {
                String fieldId = String.format("%d", i);
                result.put(fieldId, getField(fieldId));
            }
        }

        if (offset < message.length) {
            AppLog.w(TAG, "Warning: NOT all bytes are parsed! " + offset + " != " + message.length);
        }

        return result;
    }

    private void resetDefaultGlobalParams() {
        isSecondaryBitmapOn = false;
        isSupportTertiaryBitmap = false;
        varLenFormat = EVarLenFormat.BCD;
    }

    // reset all format & value
    private void resetFields() {
        header = new PackerIso8583Field(FIELD_ID_HEADER);
        msgId = new PackerIso8583Field(FIELD_ID_MSGID);

        // NOTE: this is NOT an error!
        // +1 is for intuitional purpose , field id starts from 1, we also start from 1 for byte array index at the cost
        // of wasting 1 slot.
        fields = new PackerIso8583Field[MAX_STANDARD_FIELD_NUMBER + 1];
    }

    private void resetBitmap() {
        pBitmap = new byte[8];
        hasSBitmap = false;
        sBitmap = new byte[8];
        hasTBitmap = false;
        tBitmap = new byte[8];
    }

    // the return value is ONLY for fields other than h & m
    private int validateFieldId(String fieldId) throws Iso8583Exception {
        int field = -1;
        if (fieldId == null) {
            throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_ID, "field id is null!");
        } else if (fieldId.equalsIgnoreCase(FIELD_ID_HEADER) || fieldId.equals(FIELD_ID_MSGID)) {
            // N/A
        } else {
            try {
                field = Integer.valueOf(fieldId);
            } catch (Exception e) {
                AppLog.e(TAG, "field id " + fieldId + " is invalid!");
                throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_ID, "id is: " + fieldId);
            }

            int max = getMaxFieldNum();

            if ((field < 2) || (field > max)) {
                // AppLog.e(TAG, "filed id " + field + " is invalid!");
                throw new Iso8583Exception(Iso8583Exception.ERR_FIELD_ID, "field id " + field + " < 2 || > " + max);
            }
        }
        return field;
    }

    private int getMaxFieldNum() {
        if (isSecondaryBitmapOn) {
            if (isSupportTertiaryBitmap) {
                return MAX_STANDARD_FIELD_NUMBER; // EXTENDED_STANDARD_FIELD_NUMBER; //may have tertiary bitmap(i.e.
                // fields
                // above 128)
            } else {
                return EXTENDED_STANDARD_FIELD_NUMBER;
            }
        } else {
            return BASE_STANDARD_FIELD_NUMBER;
        }
    }

    private int getMaxFieldNum(boolean hasTertiary) {
        if (isSecondaryBitmapOn) {
            if (!hasTertiary) {
                return EXTENDED_STANDARD_FIELD_NUMBER;
            } else {
                return MAX_STANDARD_FIELD_NUMBER;
            }
        } else {
            return BASE_STANDARD_FIELD_NUMBER;
        }
    }

    private boolean hasFieldForPack(String fieldId) {
        int field = -1;
        try {
            field = validateFieldId(fieldId);
        } catch (Exception e) {
            return false;
        }

        if (fieldId.equalsIgnoreCase(FIELD_ID_HEADER)) {
            return (header.isPackFormatOk() && header.isValueOk());
        } else if (fieldId.equalsIgnoreCase(FIELD_ID_MSGID)) {
            return (msgId.isPackFormatOk() && msgId.isValueOk());
        } else {
            PackerIso8583Field f = fields[field];
            if (f == null) {
                return false;
            } else {
                if (isSupportTertiaryBitmap) {
                    // FIXME: SHOULD I ?
                    if (field == TERTIARY_BITMAP_ID) {
                        return hasTBitmap;
                    }
                }

                return (f.isPackFormatOk() && f.isValueOk());
            }
        }
    }

    // I have to determine if there's tertiary fields (129 ~ 192) early to put field 65 as a placeholder
    private boolean hasTertiaryFields() {
        if (!isSupportTertiaryBitmap) {
            return false;
        }

        for (int i = (EXTENDED_STANDARD_FIELD_NUMBER + 1); i <= MAX_STANDARD_FIELD_NUMBER; i++) {
            if (hasFieldForPack("0" + i)) {
                return true;
            }
        }
        return false;
    }

    private void copyAllFieldsPackFormatToUnpackFormatIfNecessaryBeforeUnpacking() {
        if (header != null) {
            header.copyPackFormatToUnpackFormatIfNeccessaryBeforeUnpacking();
        }

        if (msgId != null) {
            msgId.copyPackFormatToUnpackFormatIfNeccessaryBeforeUnpacking();
        }
        for (PackerIso8583Field field : fields) {
            if (field != null) {
                // AppLog.d(TAG, "coping field " + field.getIdx() +
                // " packformat to unpackformat");
                field.copyPackFormatToUnpackFormatIfNeccessaryBeforeUnpacking();
            }
        }
    }

}
