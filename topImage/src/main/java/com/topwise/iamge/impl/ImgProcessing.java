package com.topwise.iamge.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.topwise.iamge.api.IImgProcessing;

import java.util.Hashtable;
import java.util.List;

/**
 * 创建日期：2021/5/20 on 10:19
 * 描述:
 * 作者:wangweicheng
 */
public class ImgProcessing implements IImgProcessing {
    private static ImgProcessing instance;
    private Context context;

    private ImgProcessing() {
    }

    static {
        System.loadLibrary("jbigprocess");
        Log.e("TopImage", "ImgProcessing loadLibrary jbigprocess ");
    }


    public synchronized static ImgProcessing getInstance() {
        if (instance == null) {
            instance = new ImgProcessing();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public byte[] bitmapToJbig(Bitmap paramBitmap) {
        Bitmap [] bitmaps = new Bitmap[1];
        bitmaps[0] = paramBitmap;
        return encodeNative(bitmaps);
    }

    @Override
    public Bitmap jbigToBitmap(byte[] paramArrayOfbyte) {
        Bitmap[] bitmaps = decodeNative(paramArrayOfbyte);
        return bitmaps[0];
    }

    @Override
    public byte[] bitmapToMonoDots(Bitmap bitmap, IRgbToMonoAlgorithm algo) {
        setRgbToMonoAlgorithm(algo);
        return ImgProcessingBitmapConvertor.getIntance(context).convertArgbToMono(bitmap);
    }

    @Override
    public byte[] bitmapToMonoBmp(Bitmap bitmap, IRgbToMonoAlgorithm algo) {
        setRgbToMonoAlgorithm(algo);
        return ImgProcessingBitmapConvertor.getIntance(context).saveAsMonoBmp(bitmap);
    }

    @Override
    public Bitmap scale(Bitmap bitmap, int w, int h) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (w == width && h == height) {
            return bitmap;
        }

        if (w <= 0 && h <= 0) {
            return bitmap;
        }

        int destWidth = w;
        int destHeight = h;
        if (w <= 0) {
            destWidth = (int) (((float) width / (float) height) * h);
            if (destWidth == 0) {
                destWidth = 1;
            }
        } else if (h <= 0) {
            destHeight = (int) (((float) height / (float) width) * w);
        }

        float scaleW = destWidth / (float) width;
        float scaleH = destHeight / (float) height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }

    private void setRgbToMonoAlgorithm(IRgbToMonoAlgorithm algo) {
        ImgProcessingBitmapConvertor.getIntance(context).setRgbToMonoAlgorithm(algo);
    }




    @Override
    public Bitmap generateBarCode(String contents, int width, int height, BarcodeFormat format) {
        // TODO Auto-generated method stub
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix;
            // ͼ������ת����ʹ���˾���ת��
            if (format == BarcodeFormat.QR_CODE) {
                bitMatrix = new MultiFormatWriter().encode(contents, format, width, width, hints);
            } else {
                bitMatrix = new MultiFormatWriter().encode(contents, format, width, height, hints);
            }
            Bitmap bitmap = bitMatrixToBitmap(bitMatrix, format);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * convert BitMatrix to bitmap
     *
     * @param
     *
     * @param format
     *            BarcodeFormat
     * @return
     */
    private Bitmap bitMatrixToBitmap(BitMatrix bitMatrix, BarcodeFormat format) {

        // ����λͼ�Ŀ�͸�
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];

        boolean isFirstBlackPoint = false;
        int startX = 0;
        int startY = 0;
        // �������ﰴ�ն�ά����㷨��������ɶ�ά���ͼƬ�� ����forѭ����ͼƬ����ɨ��Ľ��
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    if (isFirstBlackPoint == false) {
                        isFirstBlackPoint = true;
                        startX = x;
                        startY = y;
                    }
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        // ���ɶ�ά��ͼƬ�ĸ�ʽ��ʹ��ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // ͨ��������������bitmap,����ο�api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        // ȥ��bitmap�İ�ɫ�߿�
        if (format != BarcodeFormat.QR_CODE) {
            return bitmap;
        }
        if (startX <= 0)
            return bitmap;

        int x1 = startX - 0;
        int y1 = startY - 0;
        if (x1 < 0 || y1 < 0)
            return bitmap;

        int w1 = width - x1 * 2;
        int h1 = height - y1 * 2;

        Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);
        // ����bitmap
        Bitmap bitmapScale = scale(bitmapQR, width, height);
        return bitmapScale;
    }

    @Override
    public Bitmap pageToBitmap(IPage page, int pageWidth) {
        return ImgProcessingPageComposing.getIntance(context).ImageComposing(page, pageWidth);
    }

    @Override
    public IPage createPage() {
        return ImgProcessingPageComposing.getIntance(context).createPage();
    }


    /**
     *
     * @param bitmaps
     * @return
     */
    public native byte[] encodeNative(Bitmap[] bitmaps);
    /**
     *
     * @param data
     * @return
     */
    public native Bitmap[] decodeNative(byte[] data);
}
