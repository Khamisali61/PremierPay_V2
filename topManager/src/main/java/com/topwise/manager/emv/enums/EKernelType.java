package com.topwise.manager.emv.enums;

/**
 *     //Kernel Type
 *     public static final int KERNTYPE_DEF = 0x00;
 *     public static final int KERNTYPE_EMV = 0x00;
 *     public static final int KERNTYPE_VISAAP = 0x01;
 *     public static final int KERNTYPE_MC = 0x02; //TransPayPass
 *     public static final int KERNTYPE_VISA = 0x03; //qVSDC
 *     public static final int KERNTYPE_AMEX = 0x04;
 *     public static final int KERNTYPE_JCB = 0x05; //J/Speedy
 *     public static final int KERNTYPE_ZIP = 0x06; //Discover ZIP or 16
 *     public static final int KERNTYPE_DPAS = 0x06; //Discover DPAS
 *     public static final int KERNTYPE_QPBOC = 0x07;
 *     public static final int KERNTYPE_QUICS = 0x17;// with qPBOC
 *     public static final int KERNTYPE_RUPAY = 0x0D;
 *     public static final int KERNTYPE_FLASH = 0x10;
 *     public static final int KERNTYPE_EFT = 0x11;
 *     public static final int KERNTYPE_PURE = 0x12;
 *     public static final int KERNTYPE_PAGO = 0x13;
 *     public static final int KERNTYPE_MIR = 0x14;
 *     public static final int KERNTYPE_PBOC = 0xE1; //Contact PBOC
 *     public static final int KERNTYPE_NSICC = 0xE2;
 *     public static final int KERNTYPE_RFU = 0xFF;
 */
public enum EKernelType {
    KERNTYPE_DEF((byte) 0x00,"DEF",""),
    KERNTYPE_VISAAP((byte) 0x01,"VISAAP","A000000003"),
    KERNTYPE_MC((byte) 0x02,"MC","A000000004"),
    KERNTYPE_VISA((byte) 0x03,"VISA","A000000003"),
    KERNTYPE_AMEX((byte) 0x04,"AMEX","A000000025"),
    KERNTYPE_JCB((byte) 0x05,"JCB","A000000065"),
    KERNTYPE_DPAS((byte) 0x06,"DPAS","A000000152"),
    KERNTYPE_QPBOC((byte) 0x07,"QPBOC","A000000333"),
    KERNTYPE_RUPAY((byte) 0x0D,"RUPAY","A000000524"),
    KERNTYPE_FLASH((byte) 0x10,"FLASH","A000000277"),
    KERNTYPE_EFT((byte) 0x11,"EFT","A000000384"),
    KERNTYPE_PURE((byte) 0x12,"PURE",""),
    KERNTYPE_PAGO((byte) 0x13,"PAGO","A000000141"),
    KERNTYPE_MIR((byte) 0x14,"MIR","A000000658"),
    KERNTYPE_QUICS((byte) 0x17,"QUICS",""),
    KERNTYPE_PBOC((byte) 0xE1,"PBOC","A000000333"),
    KERNTYPE_NSICC((byte) 0xE2,"NSICC",""),
    KERNTYPE_RFU((byte) 0xFF,"RFU",""),
    ;


    private static final EKernelType[] VALUES = EKernelType.values();
    private byte kernelID;
    private String kernelType;
    private String RID;

    EKernelType(byte kernelID, String kernelType, String RID) {
        this.kernelID = kernelID;
        this.kernelType = kernelType;
        this.RID = RID;
    }

    public byte index() {
        return (byte)ordinal();
    }

    public byte getKernelID() {
        return kernelID;
    }

    public void setKernelID(byte kernelID) {
        this.kernelID = kernelID;
    }

    public String getKernelType() {
        return kernelType;
    }

    public void setKernelType(String kernelType) {
        this.kernelType = kernelType;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    /**
     *
     * @param bKernelID
     * @return
     */
    public static EKernelType getKernelType(byte bKernelID) {
        for (EKernelType kernelType : VALUES) {
            if (kernelType.getKernelID() == bKernelID) {
                return kernelType;
            }
        }
        return KERNTYPE_DEF;
    }

    /**
     * Get kernel type by RID
     * @param RID
     * @return
     */
    public static EKernelType getKernelType(String RID) {
        for (EKernelType kernelType : VALUES) {
            if (RID.equals(kernelType.getRID())) {
                return kernelType;
            }
        }
        return KERNTYPE_DEF;
    }

    @Override
    public String toString() {
        return "EKernelType{ kernelID=" + String.format("%02X",kernelID) + ", kernelType= " + kernelType + ", RID=" + RID + "}";
    }
}
