package com.topwise.iamge.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.topwise.iamge.api.IImgProcessing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 创建日期：2021/5/20 on 10:24
 * 描述:
 * 作者:wangweicheng
 */
public class ImgProcessingBitmapConvertor {
    private static final String TAG = "BitmapConvertor";
    // private ProgressDialog mPd;
    private Context mContext;
    private static ImgProcessingBitmapConvertor instance;

    // the default RGB to mono algorithm
    private static IImgProcessing.IRgbToMonoAlgorithm rgbToMonoAlgoDefault = new ImgProcessing.IRgbToMonoAlgorithm() {

        @Override
        public int evaluate(int r, int g, int b) {
            int v = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            // set new pixel color to output bitmap
            if (v < 128) {
                return 0;
            } else {
                return 1;
            }
        }
    };

    private IImgProcessing.IRgbToMonoAlgorithm rgbToMonoAlgo = rgbToMonoAlgoDefault;

    private ImgProcessingBitmapConvertor(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    static ImgProcessingBitmapConvertor getIntance(Context context) {
        if (instance == null) {
            instance = new ImgProcessingBitmapConvertor(context);
        }
        return instance;
    }

    void setRgbToMonoAlgorithm(IImgProcessing.IRgbToMonoAlgorithm algo) {
        if (algo == null) {
            rgbToMonoAlgo = rgbToMonoAlgoDefault;
        } else {
            rgbToMonoAlgo = algo;
        }
    }

    // only BMP raw data, no header
    byte[] saveAsMonoBmpRaw(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        byte[] monoRaw = convertArgbToMono(bitmap);
        if (monoRaw == null) {
            return null;
        }

        byte[] bmpMono = formatAsBmpMonoData(monoRaw, width, height);
        return bmpMono;
    }

    // returns BMP format data, null on error
    byte[] saveAsMonoBmp(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        byte[] monoRaw = convertArgbToMono(bitmap);
        if (monoRaw == null) {
            return null;
        }

        byte[] bmpMono = formatAsBmpMonoData(monoRaw, width, height);
        return ImgProcessingBMPFile.saveAsBuffer(bmpMono, width, height);
    }

    boolean saveAsMonoBmpFile(Bitmap bitmap, String fileName) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        byte[] monoRaw = convertArgbToMono(bitmap);
        if (monoRaw == null) {
            return false;
        }

        byte[] bmpMono = formatAsBmpMonoData(monoRaw, width, height);
        return saveMonoBmpFile(fileName, bmpMono, width, height);
    }

    /**
     * ת��RGB����ɫģʽ, ������, ���ϵ���. 0��ʾ��, 1��ʾ��
     *
     * @param bitmap
     *            Bitmap����
     * @return ����Ϊ bitmap ��� *�߶� �����ֽ�����. ���������Ϊ null
     */
    byte[] convertArgbToMono(Bitmap bitmap) {
        int pixel;
        int k = 0;
        int B = 0, G = 0, R = 0;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width];

        if (bitmap == null || width == 0 || height == 0) {
            return null;
        }

        byte[] ret = new byte[width * height];
        IImgProcessing.IRgbToMonoAlgorithm algo = rgbToMonoAlgo;

        try {
            for (int y = 0; y < height; y++) {
                // take a line of pixels every time
                Arrays.fill(pixels, 0);
                bitmap.getPixels(pixels, 0, width, 0, y, width, 1);

                for (int x = 0; x < width; x++, k++) {
                    // get one pixel color
                    pixel = pixels[x];

                    // retrieve color of all channels
                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);

                    ret[k] = (byte) algo.evaluate(R, G, B);
                }
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // each byte is a dot, conforms to bmp spec(i.e. each line has to be multiple of 4bytes, 32 dots, 32 bytes here)
    // coordinate is as follows
    // -------->
    // |
    // |
    // |
    // v
    private byte[] formatAsBmpMonoData(byte[] monoRaw, int width, int height) {
        int bmpScanLineSize = ((width + 31) / 32) * 32;
        int bmpBytesPerLine = bmpScanLineSize / 8;
        byte[] bmpRawDots = new byte[(bmpScanLineSize * height)];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < bmpScanLineSize; x++) {
                if (x >= width) {
                    bmpRawDots[y * bmpScanLineSize + x] = 1;
                } else {
                    bmpRawDots[y * bmpScanLineSize + x] = monoRaw[y * width + x];
                }
            }
        }

        byte[] bmpMonoData = new byte[bmpBytesPerLine * height];
        int length = 0;
        for (int i = 0; i < bmpRawDots.length; i = i + 8) {
            byte first = bmpRawDots[i];
            for (int j = 1; j < 8; j++) {
                byte second = (byte) ((first << 1) | bmpRawDots[i + j]);
                first = second;
            }
            bmpMonoData[length] = first;
            length++;
        }

        // reverse y coordinate
        byte[] bmpMonoRet = new byte[bmpBytesPerLine * height];
        for (int line = 0; line < height; line++) {
            System.arraycopy(bmpMonoData, line * bmpBytesPerLine, bmpMonoRet, (height - 1 - line) * bmpBytesPerLine,
                    bmpBytesPerLine);
        }

        return bmpMonoRet;
    }

    private boolean saveMonoBmpFile(String fileName, byte[] bmpMono, int width, int height) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (IOException e) {
            return false;
        }
        ImgProcessingBMPFile.saveAsFile(fileOutputStream, bmpMono, width, height);
        return true;
    }

    /**
     * BMP and Pbm4RawData is nearly identical. 3 diff: 1. y cororinate is reverse (bmp bottom to top, pbm top to
     * bottom) 2. bit color is reverse (bmp 1 white 0 black, pbm 1 black 0 white) 3. bmp line size: (w + 31)/32*32/8,
     * pbm line size: (w + 7)/8
     */
    byte[] bmpRaw2Pbm4Raw(byte[] bmpRaw, int width, int height) {
        int bmpScanLineSize = ((width + 31) / 32) * 32;
        int bmpBytesPerLine = bmpScanLineSize / 8;

        int pbm4BytesPerLine = (width + 7) / 8;
        byte[] pbm4Bytes = new byte[pbm4BytesPerLine * height];
        for (int y = 0; y < height; y++) {
            for (int b = 0; b < pbm4BytesPerLine; b++) {
                pbm4Bytes[b + y * pbm4BytesPerLine] = (byte) (~bmpRaw[b + (height - 1 - y) * bmpBytesPerLine]);
            }
        }
        return pbm4Bytes;
    }

    /**
     * BMP and Pbm4RawData is nearly identical. 3 diff: 1. y cororinate is reverse (bmp bottom to top, pbm top to
     * bottom) 2. bit color is reverse (bmp 1 white 0 black, pbm 1 black 0 white) 3. bmp line size: (w + 31)/32*32/8,
     * pbm line size: (w + 7)/8
     */
    byte[] pbm4Raw2BmpRaw(byte[] pbm4Raw, int width, int height) {
        int bmpScanLineSize = ((width + 31) / 32) * 32;
        int bmpBytesPerLine = bmpScanLineSize / 8;

        int pbm4BytesPerLine = (width + 7) / 8;
        byte[] bmpBytes = new byte[bmpBytesPerLine * height];
        for (int y = 0; y < height; y++) {
            for (int b = 0; b < pbm4BytesPerLine; b++) {
                bmpBytes[b + y * bmpBytesPerLine] = (byte) (~pbm4Raw[b + (height - 1 - y) * pbm4BytesPerLine]);
            }
        }
        return bmpBytes;

    }
}
