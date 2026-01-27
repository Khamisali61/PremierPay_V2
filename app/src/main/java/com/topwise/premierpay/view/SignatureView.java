package com.topwise.premierpay.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.ColorInt;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SignatureView extends View {
    private static final String TAG = SignatureView.class.getSimpleName();
    private Context mContext ;
    public static final int PEN_WIDTH = 8;
    public static final int PEN_COLOR = Color.BLACK;
    public static final int BACK_COLOR = Color.WHITE;

    private static final int DEFAULT_SCALE_WIDTH = 384;
    private static final int DEFAULT_SCALE_HEIGHT = 220;
    //画笔x坐标起点
    private float mPenX;
    //画笔y坐标起点
    private float mPenY;
    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    private Canvas mCanvas;
    private Bitmap cacheBitmap;
    //画笔宽度
    private int mPentWidth = PEN_WIDTH;
    //画笔颜色
    private int mPenColor = PEN_COLOR;
    //画板颜色
    private int mBackColor = BACK_COLOR;
    private boolean isTouched = false;
    private String mSavePath = null;
    private String content;
    private float mSignSize = 60.0f;
    private float RATIO;

    public SignatureView(Context context) {
        this(context, null);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext =context;
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPentWidth);
        mPaint.setColor(mPenColor);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        float ratioWidth = (float) screenWidth / 720;
        float ratioHeight = (float) screenHeight / 1184;
        Log.d(TAG, "ScreenWidth: " + screenWidth + "\nScreenHeight: " + screenHeight + "\nRatioWidth: " + ratioWidth + "\nRatioHeight: " + ratioHeight);
        RATIO = Math.min(ratioWidth, ratioHeight);
        AppLog.e(TAG,"init" );
    }

    public boolean getTouched() {
        return isTouched;
    }

    public void setPentWidth(int pentWidth) {
        mPentWidth = pentWidth;
    }

    public void setPenColor(@ColorInt int penColor) {
        mPenColor = penColor;
    }

    public void setBackColor(@ColorInt int backColor) {
        mBackColor = backColor;
    }

    /**
     * 清空签名
     */
    public void clear() {
        if (mCanvas != null) {
            isTouched = false;
            if (cacheBitmap != null) {
                cacheBitmap = null;
                ensureSignatureBitmap();
            }

            ensureSignatureBitmap();
            invalidate();
        }
    }

    private void ensureSignatureBitmap(){
        if (cacheBitmap == null) {
            cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(cacheBitmap);
            if (TextUtils.isEmpty(content)) {
                return;
            }
            Paint paint = new Paint(257);
            Log.d(TAG, "TextSize: " + (mSignSize * RATIO) + "\nOriginal TextSize: " + mSignSize + "\nRatio: " + RATIO);
            paint.setTextSize(mSignSize * RATIO);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setFakeBoldText(true);
            paint.setStrokeWidth(PEN_WIDTH);
            paint.setColor(Color.BLACK);
            mCanvas = new Canvas(cacheBitmap);
            float width = (((float) cacheBitmap.getWidth()) - measureSignature(paint, content)) / 2.0f;
            float height = ((((float) cacheBitmap.getHeight()) - measureFontHeight(paint)) / 2.0f) + measureFontFullHeight(paint);
            mCanvas.drawText(content,width,height,paint);
            mCanvas.save();
        }
    }

    public static float measureSignature(Paint paint, String signature) {
        if (signature == null) {
            signature = "";
        }
        return paint.measureText(signature);
    }

    public static float measureFontHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    public static float measureFontFullHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.leading - fontMetrics.ascent;
    }

    private Bitmap rotated(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postRotate(0f);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (bitmap1 != null) return bitmap1;
        return bitmap;
    }

    /**
     * 保存图片
     *
     * @param name 保存的地址
     * @param clearBlank 是否清除空白区域
     * @param blank 空白区域留空距离
     * @throws IOException
     */
    @SuppressLint("WrongThread")
    public boolean save(String name , boolean clearBlank, int blank) {
        AppLog.e(TAG,"onSizeChanged save " + cacheBitmap.getByteCount());
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        Bitmap rotated = rotated(cacheBitmap);

        Bitmap whiteBgBitmap = Bitmap.createBitmap(DEFAULT_SCALE_WIDTH, DEFAULT_SCALE_HEIGHT, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawBitmap(rotated,new Rect(0,0,rotated.getWidth(),rotated.getHeight()),
                new Rect(0,0,DEFAULT_SCALE_WIDTH,DEFAULT_SCALE_HEIGHT),null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        whiteBgBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] buffer =TopApplication.topImage.getImgProcessing().bitmapToJbig(whiteBgBitmap);;
        if (buffer != null) {
            String path = Utils.getEsignPath(mContext);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
             file = new File(path, name);

            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
                os.write(buffer);
                os.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if(os!= null)
                       os.close();
                    if(bos!= null)
                        bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return true;
    }

    public Bitmap[] save()  {
        AppLog.e(TAG,"onSizeChanged save " + cacheBitmap.getByteCount());
        Bitmap[] bitmaps = new Bitmap[1];
        //cacheBitmap;//

        Bitmap rotated = rotated(cacheBitmap);
        Bitmap whiteBgBitmap = Bitmap.createBitmap(DEFAULT_SCALE_WIDTH, DEFAULT_SCALE_HEIGHT, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawBitmap(rotated,new Rect(0,0,rotated.getWidth(),rotated.getHeight()),
                new Rect(0,0,DEFAULT_SCALE_WIDTH,DEFAULT_SCALE_HEIGHT),null);

        bitmaps[0] = whiteBgBitmap;
        return bitmaps;
    }

    /**
     * 获取Bitmap缓存
     */
    public Bitmap getBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap = getDrawingCache();
        setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 获取保存路径
     */
    public String getSavePath() {
        return mSavePath;
    }

    /**
     * 逐行扫描，清除边界空白
     *
     * @param blank 边界留多少个像素
     */
    private Bitmap clearBlank(Bitmap bmp, int blank) {
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[width];
        boolean isStop;
        //扫描上边距不等于背景颜色的第一个点
        for (int i = 0; i < height; i++) {
            bmp.getPixels(pixs, 0, width, 0, i, width, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    top = i;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        //扫描下边距不等于背景颜色的第一个点
        for (int i = height - 1; i >= 0; i--) {
            bmp.getPixels(pixs, 0, width, 0, i, width, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    bottom = i;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[height];
        //扫描左边距不等于背景颜色的第一个点
        for (int x = 0; x < width; x++) {
            bmp.getPixels(pixs, 0, 1, x, 0, 1, height);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        //扫描右边距不等于背景颜色的第一个点
        for (int x = width - 1; x > 0; x--) {
            bmp.getPixels(pixs, 0, 1, x, 0, 1, height);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        //计算加上保留空白距离之后的图像大小
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > width - 1 ? width - 1 : right + blank;
        bottom = bottom + blank > height - 1 ? height - 1 : bottom + blank;
        return Bitmap.createBitmap(bmp, left, top, right - left, bottom - top);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        AppLog.e(TAG,"onSizeChanged  w:" + w+"  h:" + h);
        ensureSignatureBitmap();
        isTouched = false;

        AppLog.e(TAG,"onSizeChanged " + cacheBitmap.getByteCount());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(cacheBitmap, 0, 0, mPaint);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        AppLog.e(TAG,"onTouchEvent  " + event.getAction());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPenX = event.getX();
                mPenY = event.getY();
                mPath.moveTo(mPenX, mPenY);
                return true;
            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                float x = event.getX();
                float y = event.getY();
                float penX = mPenX;
                float penY = mPenY;
                float dx = Math.abs(x - penX);
                float dy = Math.abs(y - penY);
                if (dx >= 3 || dy >= 3) {
                    float cx = (x + penX) / 2;
                    float cy = (y + penY) / 2;
                    mPath.quadTo(penX, penY, cx, cy);
                    mPenX = x;
                    mPenY = y;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(mPath, mPaint);
                mPath.reset();
                if (onTouchListener != null){
                    onTouchListener.onReStartTickTimer();
                }
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    void setTouchListener(OnTouchListener touchListener){
        this.onTouchListener = touchListener;
    }
    private OnTouchListener onTouchListener;
    public interface OnTouchListener{
        void onReStartTickTimer();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
