package com.topwise.iamge.impl;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * 创建日期：2021/5/20 on 10:24
 * 描述:
 * 作者:wangweicheng
 */
public class ImgProcessingBMPFile {

    // --- Private constants
    private final static int BITMAPFILEHEADER_SIZE = 14;
    private final static int BITMAPINFOHEADER_SIZE = 40;

    // --- Bitmap file header
    private static byte bfType[] = { (byte) 'B', (byte) 'M' };
    private static int bfSize = 0;
    private static int bfReserved1 = 0;
    private static int bfReserved2 = 0;
    private static int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE + 8;

    // --- Bitmap info header
    private static int biSize = BITMAPINFOHEADER_SIZE;
    private static int biWidth = 0;
    private static int biHeight = 0;
    private static int biPlanes = 1;
    private static int biBitCount = 1;
    private static int biCompression = 0;
    private static int biSizeImage = 0;
    private static int biXPelsPerMeter = 0x0;
    private static int biYPelsPerMeter = 0x0;
    private static int biClrUsed = 2;
    private static int biClrImportant = 2;

    // --- Bitmap raw data
    private static byte bitmap[];

    // ---- Scanlinsize;
    private static int scanLineSize = 0;

    // -- Color Pallette to be used for pixels.
    private static byte colorPalette[] = { 0, 0, 0, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255 };

    public static byte[] saveAsBuffer(byte[] monoRaw, int width, int height) {
        prepare(monoRaw, width, height);

        ByteBuffer bb = ByteBuffer.allocate(bfSize);

        writeBmpFileHeader(bb);
        writeBmpInfoHeader(bb);
        writeBmpPixels(bb);
        bb.flip();

        byte[] ret = new byte[bfSize];
        bb.get(ret);
        return ret;
    }

    /**
     * save pixels as bmp file
     *
     * @param fos
     * @param monoRaw
     *            MUST conforms to the BMP spec(e.g. bottom --> top)
     * @param parWidth
     * @param parHeight
     */
    public static void saveAsFile(FileOutputStream fos, byte[] monoRaw, int parWidth, int parHeight) {
        try {
            prepare(monoRaw, parWidth, parHeight);
            writeBmpFileHeader(fos);
            writeBmpInfoHeader(fos);
            writeBmpPixels(fos);
        } catch (Exception saveEx) {
            saveEx.printStackTrace();
        }

    }

    /*
     * computes some information for the bitmap info header.
     */
    private static void prepare(byte[] imagePix, int parWidth, int parHeight) {
        bitmap = imagePix;
        bfSize = 62 + (((parWidth + 31) / 32) * 4 * parHeight);
        biWidth = parWidth;
        biHeight = parHeight;
        scanLineSize = ((parWidth * 1 + 31) / 32) * 4;
    }

    /*
     * writeBitmap converts the image returned from the pixel grabber to the format required. Remember: scan lines are
     * inverted in a bitmap file! Each scan line must be padded to an even 4-byte boundary.
     */

    /*
     * writeBmpFileHeader writes the bitmap file header to the file.
     */
    private static void writeBmpFileHeader(FileOutputStream fos) {

        try {
            fos.write(bfType);
            fos.write(intToDWord(bfSize));
            fos.write(intToWord(bfReserved1));
            fos.write(intToWord(bfReserved2));
            fos.write(intToDWord(bfOffBits));

        } catch (Exception wbfh) {
            wbfh.printStackTrace();
        }

    }

    private static void writeBmpFileHeader(ByteBuffer bb) {
        bb.put(bfType);
        bb.put(intToDWord(bfSize));
        bb.put(intToWord(bfReserved1));
        bb.put(intToWord(bfReserved2));
        bb.put(intToDWord(bfOffBits));
    }

    /*
     * writeBmpInfoHeader writes the bitmap information header to the file.
     */

    private static void writeBmpInfoHeader(FileOutputStream fos) {
        try {
            fos.write(intToDWord(biSize));
            fos.write(intToDWord(biWidth));
            fos.write(intToDWord(biHeight));
            fos.write(intToWord(biPlanes));
            fos.write(intToWord(biBitCount));
            fos.write(intToDWord(biCompression));
            fos.write(intToDWord(biSizeImage));
            fos.write(intToDWord(biXPelsPerMeter));
            fos.write(intToDWord(biYPelsPerMeter));
            fos.write(intToDWord(biClrUsed));
            fos.write(intToDWord(biClrImportant));
            fos.write(colorPalette);
        } catch (Exception wbih) {
            wbih.printStackTrace();
        }
    }

    private static void writeBmpInfoHeader(ByteBuffer bb) {
        bb.put(intToDWord(biSize));
        bb.put(intToDWord(biWidth));
        bb.put(intToDWord(biHeight));
        bb.put(intToWord(biPlanes));
        bb.put(intToWord(biBitCount));
        bb.put(intToDWord(biCompression));
        bb.put(intToDWord(biSizeImage));
        bb.put(intToDWord(biXPelsPerMeter));
        bb.put(intToDWord(biYPelsPerMeter));
        bb.put(intToDWord(biClrUsed));
        bb.put(intToDWord(biClrImportant));
        bb.put(colorPalette);
    }

    private static void writeBmpPixels(FileOutputStream fos) {
        try {
            fos.write(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeBmpPixels(ByteBuffer bb) {
        bb.put(bitmap);
    }

    /*
     * intToWord converts an int to a word, where the return value is stored in a 2-byte array.
     */
    private static byte[] intToWord(int parValue) {

        byte retValue[] = new byte[2];
        retValue[0] = (byte) (parValue & 0x00FF);
        retValue[1] = (byte) ((parValue >> 8) & 0x00FF);
        return (retValue);

    }

    /*
     * intToDWord converts an int to a double word, where the return value is stored in a 4-byte array.
     */
    private static byte[] intToDWord(int parValue) {

        byte retValue[] = new byte[4];
        retValue[0] = (byte) (parValue & 0x00FF);
        retValue[1] = (byte) ((parValue >> 8) & 0x000000FF);
        retValue[2] = (byte) ((parValue >> 16) & 0x000000FF);
        retValue[3] = (byte) ((parValue >> 24) & 0x000000FF);
        return (retValue);

    }

}
