package com.topwise.iamge.api;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import java.util.List;
/**
 * 创建日期：2021/5/20 on 10:17
 * 描述:
 * 作者:wangweicheng
 */
public interface IImgProcessing {
    byte[] bitmapToJbig(Bitmap paramBitmap);

    Bitmap jbigToBitmap(byte[] paramArrayOfbyte);

    byte[] bitmapToMonoDots(Bitmap paramBitmap, IRgbToMonoAlgorithm paramIRgbToMonoAlgorithm);

    byte[] bitmapToMonoBmp(Bitmap paramBitmap, IRgbToMonoAlgorithm paramIRgbToMonoAlgorithm);

    Bitmap scale(Bitmap paramBitmap, int paramInt1, int paramInt2);

    Bitmap generateBarCode(String paramString, int paramInt1, int paramInt2, BarcodeFormat paramBarcodeFormat);

    IPage createPage();

    Bitmap pageToBitmap(IPage paramIPage, int paramInt);

    public static interface IPage {
        ILine addLine();

        ILine.IUnit createUnit();

        List<ILine> getLines();

        String getTypeFace();

        void setTypeFace(String param1String);

        void adjustLineSpace(int param1Int);

        int getLineSpaceAdjustment();

        Bitmap toBitmap(int param1Int);

        public enum EAlign {
            LEFT, CENTER, RIGHT;
        }

        public static interface ILine {
            List<IUnit> getUnits();

            /**
             * Add a line
             * @return ILine
             */
            ILine addUnit();

            ILine addUnit(int param2Int);

            ILine addUnit(IUnit param2IUnit);

            ILine addUnit(Bitmap param2Bitmap);

            ILine addUnit(Bitmap param2Bitmap, IImgProcessing.IPage.EAlign param2EAlign);

            ILine addUnit(String param2String, int param2Int);

            ILine addUnit(String param2String, int param2Int1, int param2Int2);

            ILine addUnit(String param2String, int param2Int, float param2Float);

            ILine addUnit(String param2String, int param2Int, IImgProcessing.IPage.EAlign param2EAlign);

            ILine addUnit(String param2String, int param2Int1, int param2Int2, float param2Float);

            ILine addUnit(String param2String, int param2Int, IImgProcessing.IPage.EAlign param2EAlign, float param2Float);

            ILine addUnit(String param2String, int param2Int1, IImgProcessing.IPage.EAlign param2EAlign, int param2Int2);

            ILine addUnit(String param2String, int param2Int1, IImgProcessing.IPage.EAlign param2EAlign, int param2Int2, float param2Float);

            ILine adjustTopSpace(int param2Int);

            int getTopSpaceAdjustment();

            public interface IUnit {
                public static final int TEXT_STYLE_NORMAL = 0;

                public static final int TEXT_STYLE_BOLD = 1;

                public static final int TEXT_STYLE_UNDERLINE = 2;

                String getText();

                IUnit setText(String param3String);

                Bitmap getBitmap();

                IUnit setBitmap(Bitmap param3Bitmap);

                int getFontSize();

                IUnit setFontSize(int param3Int);

                IImgProcessing.IPage.EAlign getAlign();

                IUnit setAlign(IImgProcessing.IPage.EAlign param3EAlign);

                IUnit setTextStyle(int param3Int);

                int getTextStyle();

                float getWeight();

                IUnit setWeight(float param3Float);
            }
        }

    }

    public static interface IRgbToMonoAlgorithm {
        int evaluate(int param1Int1, int param1Int2, int param1Int3);
    }
}
