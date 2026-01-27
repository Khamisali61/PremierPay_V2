package com.topwise.toptool.impl;

import android.content.Context;

import com.topwise.toptool.api.convert.IConvert;
import com.topwise.toptool.api.packer.ITlv;
import com.topwise.toptool.api.packer.TlvException;
import com.topwise.toptool.api.utils.AppLog;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackerTlv implements ITlv {
    private static final String TAG = "Tlv";

    private static PackerTlv instance;
    private Context context;

    private PackerTlv() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public synchronized static PackerTlv getInstance() {
        if (instance == null) {
            instance = new PackerTlv();
        }

        return instance;
    }

    @Override
    public ITlvDataObjList createTlvDataObjectList() {
        return new TlvDataObjList();
    }

    @Override
    public ITlvDataObj createTlvDataObject() {
        return new TlvDataObj();
    }

    @Override
    public byte[] pack(ITlvDataObj obj) throws TlvException {
        if (obj == null) {
            return new byte[0];
        }

        ByteBuffer bb = ByteBuffer.allocate(1024 * 100);
        bb.clear();
        bb.order(ByteOrder.BIG_ENDIAN);

        byte[] tag = obj.getTag();
        if (tag == null || tag.length == 0) {
            throw new TlvException(TlvException.ERR_NO_TAG);
        }
        bb.put(tag);

        byte[] value = obj.getValue();
        if (value == null) {
            // throw new TlvException(TlvException.ERR_NO_VALUE, "idx " + i);
            value = new byte[0]; // allowing no value.
        }

        if (value.length == 0) {
            AppLog.w(TAG, "data object value length is 0!");
        }

        byte[] len = genLen(value.length);
        bb.put(len);
        bb.put(value);

        bb.flip();
        byte[] ret = new byte[bb.limit()];
        bb.get(ret);
        return ret;
    }

    @Override
    public byte[] pack(ITlvDataObjList objs) throws TlvException {
        if (objs == null) {
            return new byte[0];
        }

        ByteBuffer bb = ByteBuffer.allocate(1024 * 100);
        bb.clear();
        bb.order(ByteOrder.BIG_ENDIAN);

        List<ITlvDataObj> list = objs.getDataObjectList();

        for (int i = 0; i < list.size(); i++) {
            ITlvDataObj dataObj = list.get(i);
            byte[] tag = dataObj.getTag();
            if (tag == null || tag.length == 0) {
                throw new TlvException(TlvException.ERR_NO_TAG, "idx " + i);
            }
            bb.put(tag);

            byte[] value = dataObj.getValue();
            if (value == null) {
                // throw new TlvException(TlvException.ERR_NO_VALUE, "idx " + i);
                value = new byte[0]; // allowing no value.
            }

            if (value.length == 0) {
                AppLog.w(TAG, "data object idx " + i + " value length is 0!");
            }

            byte[] len = genLen(value.length);
            bb.put(len);
            bb.put(value);
        }

        bb.flip();
        byte[] ret = new byte[bb.limit()];
        bb.get(ret);
        return ret;
    }

    @Override
    public ITlvDataObjList unpack(byte[] data) throws TlvException {
        int offset = 0;
        ITlvDataObjList list = new TlvDataObjList();

        while (offset < data.length) {
            TlvDataObj obj = new TlvDataObj();
            // tag
            int len = obj.setTag(data, offset);

            offset += len;

            if (offset > data.length) {
                AppLog.e(TAG, "data corrupted detected after unpack tag");
                throw new TlvException(TlvException.ERR_DATA_CORRUPTED, "data corrupted detected after unpack TAG");
            }

            int[] lenOffset = parseLen(data, offset);
            if (lenOffset[1] > 3 || lenOffset[1] < 0) {
                AppLog.e(TAG, "len of L " + lenOffset[1] + " is tooooo long!");
                throw new TlvException(TlvException.ERR_LENGTH_OF_L_TOO_LONG, "len of L is " + lenOffset[1]);
            }

            offset += lenOffset[1]; // bypass len of L

            if (offset > data.length) {
                AppLog.e(TAG, "data corrupted detected after unpack L");
                throw new TlvException(TlvException.ERR_DATA_CORRUPTED, "data corrupted detected after unpack L");
            }

            if (offset + lenOffset[0] > data.length) {
                AppLog.e(TAG, "data corrupted, value exceeds range of data");
                throw new TlvException(TlvException.ERR_DATA_CORRUPTED, "data corrupted, value exceeds range of data");
            }

            byte[] v = new byte[lenOffset[0]];
            obj.setLength(lenOffset[0]);
            System.arraycopy(data, offset, v, 0, lenOffset[0]);
            obj.setValue(v);
            offset += lenOffset[0];

            list.addDataObj(obj);
        }

        return list;
    }

    @Override
    public ITlvDataObjList unpackDDol(byte[] data) throws TlvException {
        int offset = 0;
        ITlvDataObjList list = new TlvDataObjList();

        while (offset < data.length) {
            TlvDataObj obj = new TlvDataObj();
            // tag
            int len = obj.setTag(data, offset);
            AppLog.e(TAG, "data tag len = " + len);
            offset += len;

            if (offset > data.length) {
                AppLog.e(TAG, "data corrupted detected after unpack tag");
                throw new TlvException(TlvException.ERR_DATA_CORRUPTED, "data corrupted detected after unpack TAG");
            }

            int[] lenOffset = parseLen(data, offset);
            if (lenOffset[1] > 3 || lenOffset[1] < 0) {
                AppLog.e(TAG, "len of L " + lenOffset[1] + " is tooooo long!");
                throw new TlvException(TlvException.ERR_LENGTH_OF_L_TOO_LONG, "len of L is " + lenOffset[1]);
            }

            offset += lenOffset[1]; // bypass len of L

            if (offset > data.length) {
                AppLog.e(TAG, "data corrupted detected after unpack L");
                throw new TlvException(TlvException.ERR_DATA_CORRUPTED, "data corrupted detected after unpack L");
            }
            AppLog.e(TAG, "data len = " +lenOffset[0]);
            obj.setLength(lenOffset[0]);
            list.addDataObj(obj);
        }

        return list;
    }

    private class TlvDataObj implements ITlvDataObj {
        private byte[] tag;
        private byte[] value;
        private int length;
        @Override
        public boolean isConstructed() {
            if (tag != null && tag.length > 0) {
                return ((tag[0] & 0x20) != 0);
            }
            return false;
        }

        @Override
        public int setTag(byte[] tag) {
            if (tag == null || tag.length == 0)
                return 0;
            this.tag = new byte[tag.length];
            System.arraycopy(tag, 0, this.tag, 0, this.tag.length);
            return this.tag.length;
        }

        @Override
        public int setTag(int tag) {
            this.tag = PackerTlv.tagFromInt(tag);
            return this.tag.length;
        }

        @Override
        public void setValue(byte value) {
            this.value = new byte[1];
            this.value[0] = value;
        }

        @Override
        public void setValue(byte[] value) {
            if (value == null || value.length == 0) {
                return;
            }
            this.value = new byte[value.length];
            System.arraycopy(value, 0, this.value, 0, value.length);
        }

        @Override
        public byte[] getValue() {
            return value;
        }

        @Override
        public byte[] getTag() {
            return tag;
        }

        @Override
        public Integer getIntTag() {
            if (tag == null) {
                return null;
            }

            byte[] t = new byte[4];
            System.arraycopy(tag, 0, t, t.length - tag.length, tag.length);
            return TopTool.getInstance().getConvert().intFromByteArray(t, 0, IConvert.EEndian.BIG_ENDIAN);
        }

        @Override
        public void setLength(int length) {
            this.length = length;
        }

        @Override
        public int getLength() {
            return length;
        }

        /**
         * parse a TAG from input data with specified offset
         *
         * @param data
         *            [input] data to parse
         * @param offset
         *            offset of the first byte to parse
         * @return number of bytes of this tag
         */
        private int setTag(byte[] data, int offset) {
            int len = 0;
            if (data[offset] == 0x00) {
                return 0;
            }

            // 0x1f
            if ((data[offset] & 0x1f) == 0x1f) {
                // 0x10
                if ((data[offset + 1] & 0x80) == 0x80)
                    len = 3;
                else
                    len = 2;
            } else {
                len = 1;
            }
            this.tag = new byte[len];

            System.arraycopy(data, offset, this.tag, 0, len);
//            AppLog.e(TAG,"tag == " +TopTool.getInstance().getConvert().bcdToStr(tag) );
            return len;
        }

        @Override
        public String toString() {
            IConvert conv = TopTool.getInstance().getConvert();

            return String.format("Tag: %s, length:%d Value: %s", (tag == null) ? "null" : conv.bcdToStr(tag),
                    length,
                    (value == null) ? "null" : conv.bcdToStr(value));

        }
        //
        // @Override
        // public byte[] pack() throws TlvException {
        // ByteBuffer bb = ByteBuffer.allocate(1024 * 100);
        // bb.clear();
        // bb.order(ByteOrder.BIG_ENDIAN);
        //
        // byte[] tag = this.getTag();
        // if (tag == null || tag.length == 0) {
        // throw new TlvException(TlvException.ERR_NO_TAG);
        // }
        // bb.put(tag);
        //
        // byte[] value = this.getValue();
        // if (value == null) {
        // // throw new TlvException(TlvException.ERR_NO_VALUE, "idx " + i);
        // value = new byte[0]; // allowing no value.
        // }
        //
        // if (value.length == 0) {
        // AppLog.w(TAG, "data object value length is 0!");
        // }
        //
        // byte[] len = genLen(value.length);
        // bb.put(len);
        // bb.put(value);
        //
        // bb.flip();
        // byte[] ret = new byte[bb.limit()];
        // bb.get(ret);
        // return ret;
        // }

    }

    private class TlvDataObjList implements ITlvDataObjList {
        ArrayList<ITlvDataObj> list = new ArrayList<ITlvDataObj>();

        @Override
        public void addDataObj(ITlvDataObj obj) {
            if (obj != null) {
                list.add(obj);
            }
        }

        @Override
        public List<ITlvDataObj> getDataObjectList() {
            return list;
        }

        @Override
        public ITlvDataObj getByTag(byte[] param1ArrayOfbyte) {
            return null;
        }

        @Override
        public ITlvDataObj getByTag(int tag) {
            byte[] t = PackerTlv.tagFromInt(tag);

            for (int i = 0; i < list.size(); i++) {
                ITlvDataObj tlvDataObj = list.get(i);
                if (Arrays.equals(tlvDataObj.getTag(), t))
                    return tlvDataObj;
            }

            return null;
        }

        @Override
        public List<ITlvDataObj> getDataObjectListByTag(byte[] param1ArrayOfbyte) {
            return null;
        }

        @Override
        public List<ITlvDataObj> getDataObjectListByTag(int tag) {
            byte[] t = PackerTlv.tagFromInt(tag);

            ArrayList<ITlvDataObj> ret = new ArrayList<ITlv.ITlvDataObj>();
            for (int i = 0; i < list.size(); i++) {
                ITlvDataObj tlvDataObj = list.get(i);
                if (Arrays.equals(tlvDataObj.getTag(), t)) {
                    ret.add(tlvDataObj);
                }
            }

            if (ret.size() > 0) {
                return ret;
            } else {
                return null;
            }
        }

        @Override
        public byte[] getValueByTag(byte[] param1ArrayOfbyte) {
            return new byte[0];
        }

        @Override
        public byte[] getValueByTag(int tag) {
            ITlvDataObj obj = getByTag(tag);
            if (obj == null) {
                return null;
            }

            return obj.getValue();
        }

        @Override
        public List<byte[]> getValueListByTag(byte[] param1ArrayOfbyte) {
            return null;
        }

        @Override
        public List<byte[]> getValueListByTag(int tag) {
            List<ITlvDataObj> objs = getDataObjectListByTag(tag);
            if (objs == null) {
                return null;
            }

            ArrayList<byte[]> ret = new ArrayList<byte[]>();
            for (ITlvDataObj obj : objs) {
                ret.add(obj.getValue());
            }

            if (ret.size() > 0) {
                return ret;
            } else {
                return null;
            }
        }

        @Override
        public int getIndexByTag(byte[] param1ArrayOfbyte) {
            return 0;
        }

        @Override
        public int getIndexByTag(int tag) {
            if (list == null) {
                return -1;
            }
            byte[] t = PackerTlv.tagFromInt(tag);

            for (int i = 0; i < list.size(); i++) {
                ITlvDataObj tlvDataObj = list.get(i);
                if (Arrays.equals(tlvDataObj.getTag(), t))
                    return i;
            }
            return -1;
        }

        @Override
        public int[] getIndicesByTag(byte[] param1ArrayOfbyte) {
            return new int[0];
        }

        @Override
        public int[] getIndicesByTag(int tag) {
            if (list == null) {
                return null;
            }
            byte[] t = PackerTlv.tagFromInt(tag);

            ArrayList<Integer> records = new ArrayList<Integer>();
            for (int i = 0; i < list.size(); i++) {
                ITlvDataObj tlvDataObj = list.get(i);
                if (Arrays.equals(tlvDataObj.getTag(), t)) {
                    records.add(i);
                }
            }

            int size = records.size();
            if (size > 0) {
                int[] ret = new int[size];
                for (int i = 0; i < size; i++) {
                    ret[i] = records.get(i);
                }
                return ret;

            } else {
                return null;
            }
        }

        @Override
        public boolean updateValueByTag(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2) {
            return false;
        }

        @Override
        public boolean updateValueByTag(int tag, byte[] value) {
            if (list == null || value == null)
                return false;

            ITlvDataObj tlvDataObj = getByTag(tag);
            if (tlvDataObj == null) {
                return false;
            }

            tlvDataObj.setValue(value);
            return true;
        }

        @Override
        public boolean updateValueByTag(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2, int param1Int) {
            return false;
        }

        @Override
        public boolean updateValueByTag(int tag, byte[] value, int index) {
            if (list == null || value == null)
                return false;

            List<ITlvDataObj> tlvDataObjs = getDataObjectListByTag(tag);
            if (tlvDataObjs == null) {
                return false;
            }

            if (index >= tlvDataObjs.size()) {
                return false;
            }

            if (index != -1) {
                tlvDataObjs.get(index).setValue(value);
            } else {
                for (int i = 0; i < tlvDataObjs.size(); i++) {
                    tlvDataObjs.get(i).setValue(value);
                }
            }
            return true;
        }

        @Override
        public void removeByTag(byte[] param1ArrayOfbyte) {

        }

        @Override
        public void removeByTag(int tag) {
            if (list == null) {
                return;
            }

            ITlvDataObj tlvDataObj = getByTag(tag);
            if (tlvDataObj == null) {
                return;
            }
            list.remove(tlvDataObj);
        }

        @Override
        public void removeByTag(byte[] param1ArrayOfbyte, int param1Int) {

        }

        @Override
        public void removeByTag(int tag, int index) {
            if (list == null) {
                return;
            }

            List<ITlvDataObj> objs = getDataObjectListByTag(tag);
            if (objs == null) {
                return;
            }

            if (index >= objs.size() || index < 0) {
                return;
            }

            list.remove(objs.get(index));
        }

        @Override
        public int getSize() {
            if (list == null) {
                AppLog.e(TAG,"list is null ");
                return 0;
            }
            return list.size();
        }

        @Override
        public ITlvDataObj getIndexTag(int index) {
            if (list == null) {
                AppLog.e(TAG,"list is null ");
                return null;
            }

            return list.get(index);
        }

        @Override
        public String toString() {
            if (list == null) {
                return "TLV NULL list";
            }

            StringBuffer sb = new StringBuffer();
            sb.append("obj num " + list.size() + "\n");
            for (ITlvDataObj obj : list) {
                sb.append(obj).append("\n");
            }

            return sb.toString();
        }
        //
        // @Override
        // public byte[] pack() throws TlvException {
        // ByteBuffer bb = ByteBuffer.allocate(1024 * 100);
        // bb.clear();
        // bb.order(ByteOrder.BIG_ENDIAN);
        //
        // for (int i = 0; i < list.size(); i++) {
        // ITlvDataObj dataObj = list.get(i);
        // byte[] tag = dataObj.getTag();
        // if (tag == null || tag.length == 0) {
        // throw new TlvException(TlvException.ERR_NO_TAG, "idx " + i);
        // }
        // bb.put(tag);
        //
        // byte[] value = dataObj.getValue();
        // if (value == null) {
        // // throw new TlvException(TlvException.ERR_NO_VALUE, "idx " + i);
        // value = new byte[0]; // allowing no value.
        // }
        //
        // if (value.length == 0) {
        // AppLog.w(TAG, "data object idx " + i + " value length is 0!");
        // }
        //
        // byte[] len = genLen(value.length);
        // bb.put(len);
        // bb.put(value);
        // }
        //
        // bb.flip();
        // byte[] ret = new byte[bb.limit()];
        // bb.get(ret);
        // return ret;
        // }
    }

    /**
     *
     * @param data
     * @param offset
     * @return
     */
    private int[] parseLen(byte[] data, int offset) {
        int[] ret = new int[2];
        if ((data[offset] & 0x80) == 0) {
            ret[0] = data[offset];
            ret[1] = 1;
            return ret;
        }

        int bytes = data[offset] & 0x7f;

        offset++;
        int len = 0;
        int i = 0;
        while (i < bytes) {
            len <<= 8;
            len += (data[offset] & 0xff);

            i++;
            offset++;
        }

        ret[0] = len;
        ret[1] = bytes + 1;

        return ret;
    }

    /**
     * ����ָ�������ݳ�������LENGTH
     *
     * @param len
     *            ���ݳ���
     * @return LENGTH�ֽ�����
     */
    private byte[] genLen(int len) {
        byte[] ret;
        if (len <= 127) {
            ret = new byte[1];
            ret[0] = (byte) len;
            return ret;
        }

        int tmp = len;
        int b = 0;
        while (tmp != 0) {
            b++;
            tmp = (tmp >> 8);
        }

        ret = new byte[b + 1];
        ret[0] = (byte) (0x80 + b);
        byte[] lenBytes = new byte[4];

        TopTool.getInstance().getConvert().intToByteArray(len, lenBytes, 0, IConvert.EEndian.BIG_ENDIAN);
        System.arraycopy(lenBytes, 4 - b, ret, 1, b);

        return ret;
    }

    private static byte[] tagFromInt(int tag) {
        IConvert convert = TopTool.getInstance().getConvert();
        byte[] t = convert.intToByteArray(tag, IConvert.EEndian.BIG_ENDIAN);
        int realTagLen = t.length;
        for (int i = 0; i < t.length; i++) {
            if (t[i] == 0) {
                realTagLen--;
            }
        }

        byte[] ret = new byte[realTagLen];
        System.arraycopy(t, t.length - realTagLen, ret, 0, realTagLen);
        return ret;
    }
}
