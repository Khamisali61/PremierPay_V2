package com.topwise.toptool.api.utils;

public interface IUtils {
    boolean isByteArrayValueSame(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, int paramInt3);

    void fileSystemSync();

    boolean isDateValid(String paramString);

    boolean isTimeValid(String paramString);

    boolean isIpv4(String paramString);

    String formatIpAddress(String paramString);

    IRingBuffer createRingBuffer(int paramInt);

    public static interface IRingBuffer {
        int available();

        int read(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2);

        byte[] read();

        int write(byte[] param1ArrayOfbyte, int param1Int);

        void reset();
    }
}