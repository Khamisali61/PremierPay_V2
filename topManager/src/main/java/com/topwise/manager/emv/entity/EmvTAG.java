package com.topwise.manager.emv.entity;

public class EmvTAG {
    private int Tag;
    private int TagLen;
    private byte [] aucTag;
    private byte [] aucVale;
    private int nValeLen;

    public EmvTAG(int tagLen, byte[] aucTag) {
        TagLen = tagLen;
        this.aucTag = aucTag;
    }

    public EmvTAG(int tag, int tagLen, byte[] aucVale, int nValeLen) {
        Tag = tag;
        TagLen = tagLen;
        this.aucVale = aucVale;
        this.nValeLen = nValeLen;
    }

    public int getTag() {
        return Tag;
    }

    public void setTag(int tag) {
        Tag = tag;
    }

    public int getTagLen() {
        return TagLen;
    }

    public void setTagLen(int tagLen) {
        TagLen = tagLen;
    }

    public byte[] getAucVale() {
        return aucVale;
    }

    public void setAucVale(byte[] aucVale) {
        this.aucVale = aucVale;
    }

    public int getnValeLen() {
        return nValeLen;
    }

    public void setnValeLen(int nValeLen) {
        this.nValeLen = nValeLen;
    }
}
