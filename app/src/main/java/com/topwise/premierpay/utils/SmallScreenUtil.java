package com.topwise.premierpay.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.topwise.premierpay.R;
import com.topwise.cloudpos.aidl.smallscreen.AidlSmallScreen;
import com.topwise.cloudpos.aidl.smallscreen.BitmapAlign;
import com.topwise.cloudpos.data.LedCode;

import java.util.List;

/**
 * @author caixh
 * @description
 * @date 2024/5/13 17:47
 */
public class SmallScreenUtil {
    private static final String TAG = SmallScreenUtil.class.getSimpleName();
    private static final int HEIGHT_BAR = 22;
    private static final String CLOCK_FORMAT = "h:mm";
    private static final int REFRESH_INTERVAL =50;

    private static SmallScreenUtil instance = new SmallScreenUtil();

    private boolean hasInit = false;
    private Context context;
    private int width = 282;
    private int height = 240;
    private LinearLayout root = null;
    private Bitmap screenBitmap = null;
    private LinearLayout ledLayout = null;
    private ImageView mLed[] = null;
    private boolean ledOn[] = {false,false,false,false};
    private int[] ledDrawable = {R.drawable.blue,R.drawable.yello,R.drawable.green,R.drawable.red};
    private AidlSmallScreen iSmallScreen = null;

    private TextClock tc;
    private long now;
    Handler handler = new Handler();

    Runnable mTicker = new Runnable() {
        @Override
        public void run() {
            if(tc!=null) {
                tc.setFormat24Hour(tc.getFormat24Hour());
                Log.i(TAG, "tc.gettext="+tc.getText());
                Log.d(TAG, "root.getChildCount()="+root.getChildCount());
                if(root!=null&&root.getChildCount()>1) {
                    try {
                        Log.d(TAG, "displayBitmapData-start");
                        iSmallScreen.displayBitmap(getShowBitmap(), BitmapAlign.BITMAP_ALIGN_LEFT);
                        Log.d(TAG, "displayBitmapData-end");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                long cur = System.currentTimeMillis();
                handler.postDelayed(this, REFRESH_INTERVAL - cur % REFRESH_INTERVAL);
            }
        }
    };

    private SmallScreenUtil(){

    }

    public static SmallScreenUtil getInstance(){
        return instance;
    }

    public void init(Context context){
        Log.d(TAG, "SmallScreenUtil-init"+hasInit);
        if(hasInit) {
            return;
        }
        this.context = context.getApplicationContext();
        hasInit = true;
        handler.postDelayed(mTicker, REFRESH_INTERVAL - now % REFRESH_INTERVAL);
    }

    public void destroy(){
        if(this.iSmallScreen!=null) {
            try {
                Log.d(TAG, "stopAppControl");
                iSmallScreen.stopAppControl();
            } catch (RemoteException e) {
                Log.d(TAG, "stopAppControl:" + e.getMessage());
            }
        }
        unbindResource(root);
        handler.removeCallbacks(mTicker);
    }

    public void setISmallScreen(AidlSmallScreen iSmallScreen) {
        this.iSmallScreen = iSmallScreen;

        if(this.iSmallScreen!=null){
            try {
                int[] wh = iSmallScreen.getSmallScreenSize();
                if(wh!=null && wh.length>=2){
                    this.width = wh[0];
                    this.height = wh[1];
                    this.root = new LinearLayout(context);
                    this.root.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                    this.root.setLayoutParams(layoutParams);
                    addLed();
                }
            }catch(RemoteException e){
                Log.d(TAG, "setISmallScreen:"+e.getMessage());
            }
        }

    }

    private void addLed(){
        ledLayout = new LinearLayout(context);
        ledLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, HEIGHT_BAR);
        layoutParams.gravity = Gravity.CENTER;
        ledLayout.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(3,HEIGHT_BAR);
        layoutParams.topMargin = 2;
        mLed = new ImageView[4];
        for (int i = 0; i < 4; i++) {
            View v = new View(context);
            v.setLayoutParams(params);
            ledLayout.addView(v);
            mLed[i] = new ImageView(context);
            if(ledOn[i]){
                mLed[i].setImageResource(ledDrawable[i]);
            }else{
                mLed[i].setImageResource(R.drawable.off);
            }
            ledLayout.addView(mLed[i]);
            v = new View(context);
            v.setLayoutParams(params);
            ledLayout.addView(v);
        }
        LinearLayout.LayoutParams tcparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,HEIGHT_BAR+3);
        tcparams.gravity = Gravity.CENTER_VERTICAL;
        tcparams.topMargin = -7;
        tcparams.leftMargin = 28;
        tc = new TextClock(context);
        tc.setFormat12Hour(CLOCK_FORMAT+" aa");
        tc.setFormat24Hour(CLOCK_FORMAT);
        tc.setTextSize(12);
//        tc.setGravity(Gravity.CENTER_VERTICAL);
        tc.setLayoutParams(tcparams);
        ledLayout.addView(tc);
        root.addView(ledLayout);
    }

    public void add(View view) {
        this.root.addView(view);
    }

    public Bitmap getShowBitmap() {
        this.root.setDrawingCacheEnabled(true);
        measureHeight(root);
        this.root.layout(0, 0, width, height);
        if (this.screenBitmap != null) {
            this.screenBitmap.recycle();
            this.screenBitmap = null;
        }
        synchronized (this) {
            this.screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(this.screenBitmap);
            canvas.drawColor(Color.WHITE);
            this.root.draw(canvas);

            return this.screenBitmap;
        }
    }

    private void updateSmallScreen(){
        try {
            iSmallScreen.displayBitmap(getShowBitmap(), BitmapAlign.BITMAP_ALIGN_LEFT);

            handler.postDelayed(mTicker, REFRESH_INTERVAL - now % REFRESH_INTERVAL);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        synchronized (this) {
            this.unbindResource(root);
            addLed();
            now = SystemClock.uptimeMillis();
        }
    }

    private void unbindResource(View view) {
        if (view == null) {
            Log.i("PrintTemplate", "view == null");
        } else {
            Log.i("PrintTemplate", "unbindResource");
            if (view instanceof ImageView) {
                Log.i("PrintTemplate", "recycle");
                Drawable drawable = ((ImageView)view).getDrawable();
                if(drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    //Log.i("PrintTemplate", "bitmap"+bitmap.toString());
                    try {
                        if (bitmap != null) {
                            if (!bitmap.isRecycled()) {
                                //AppLog.d("PrintTemplate","bitmap.getWidth()="+bitmap.getWidth()+"bitmap.getHeight()="+bitmap.getHeight());
                                Log.i("isRecycled", "isRecycled");
                                bitmap.recycle();
                                bitmap = null;
                            }
                        }
                    }catch (Exception exception){
                        exception.printStackTrace();
                    }
                }
            }
            if (view instanceof ViewGroup) {
                Log.i("PrintTemplate", "ViewGroup");

                for(int i = 0; i < ((ViewGroup)view).getChildCount(); ++i) {
                    this.unbindResource(((ViewGroup)view).getChildAt(i));
                }

                ((ViewGroup)view).removeAllViews();
            }

        }
    }

    private int measureHeight(View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(-1, -2);
        }

        int childMeasureWidth = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int childMeasureHeight;
        if (lp.height > 0) {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY);
        } else {
            childMeasureHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }

        child.measure(childMeasureWidth, childMeasureHeight);
        return child.getMeasuredHeight();
    }

    public void setLed(int light, boolean isOn){
        if(light<LedCode.OPER_LED_ALL||light>LedCode.OPER_LED_BLUE){
            return;
        }
        if(mLed==null){
            return;
        }

        if(light==LedCode.OPER_LED_ALL){
            for (int i = 0; i < 4; i++) {
                if(isOn){
                    mLed[i].setImageResource(ledDrawable[i]);
                }else{
                    mLed[i].setImageResource(R.drawable.off);
                }
                ledOn[i] = isOn;
            }
        }else {
            int l = 0;
            if (light == LedCode.OPER_LED_YELLOW) {
                l = 1;
            } else if (light == LedCode.OPER_LED_GREEN){
                l = 2;
            }else if(light==LedCode.OPER_LED_RED){
                l = 3;
            }
            if(isOn){
                mLed[l].setImageResource(ledDrawable[l]);
            }else{
                mLed[l].setImageResource(R.drawable.off);
            }
            ledOn[l] = isOn;
        }
    }

    public void showLogo(String assetFile){
        if(iSmallScreen==null||root==null){
            return;
        }
        clear();
        Bitmap bitmap = Utils.getImageFromAssetsFile(assetFile);
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        layoutParams.leftMargin = (width-bitmap.getWidth())/2;
        layoutParams.topMargin = (height-bitmap.getHeight())/2-HEIGHT_BAR;
        imageView.setLayoutParams(layoutParams);
        add(imageView);
    }

    public void showAmount(String title, String amount){
        if(iSmallScreen==null||root==null){
            return;
        }
        clear();
        TextView titleTv = new TextView(context);
        titleTv.setText(title);
        titleTv.setTextSize(20);
        titleTv.setTextColor(Color.BLACK);
        titleTv.setGravity(Gravity.CENTER);
        titleTv.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        add(titleTv);

        TextView tipTv = new TextView(context);
        tipTv.setText(" Amount:");
        tipTv.setTextSize(16);
        tipTv.setTextColor(Color.BLACK);
        tipTv.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        add(tipTv);

        TextView amountTv = new TextView(context);
        amountTv.setText(amount);
        amountTv.setTextSize(20);
        amountTv.setTextColor(Color.RED);
        amountTv.setGravity(Gravity.RIGHT);
        amountTv.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        add(amountTv);
    }

    public void showSearchCard(String amount){
        if(iSmallScreen==null||root==null){
            return;
        }
        clear();
        TextView amountTv = new TextView(context);
        amountTv.setText(amount);
        amountTv.setTextSize(16);
        amountTv.setTextColor(Color.RED);
        amountTv.setGravity(Gravity.CENTER);
        amountTv.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        add(amountTv);

        Bitmap bitmap = Utils.getImageFromAssetsFile("quick.bmp");
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        layoutParams.topMargin = 8;
        imageView.setLayoutParams(layoutParams);
        add(imageView);

    }

    public void showMessage(String message){
        if(iSmallScreen==null||root==null){
            return;
        }
        clear();
        TextView tv = new TextView(context);
        tv.setText(message);
        tv.setTextSize(18);
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);
        tv.setHeight(height);
        tv.setWidth(width);
        add(tv);
    }

    public void showBitmap(Bitmap bitmap){
        clear();
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(259, 220);
        layoutParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(layoutParams);
        add(imageView);
    }


    public void showResult(boolean isSuccess, List<String> result){
        if(iSmallScreen==null||root==null){
            return;
        }
        clear();
        ImageView imageView = new ImageView(context);
        Bitmap bit = Utils.getImageFromAssetsFile(isSuccess? "trans_success.png": "trans_faild.png");
//        Drawable drawable = context.getDrawable(isSuccess? R.drawable.trans_success: R.drawable.trans_faild);
//        Log.i(TAG, "R.drawable.trans_success="+drawable);
//        imageView.setImageDrawable(drawable);
        imageView.setImageBitmap(bit);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 80);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = 5;
        imageView.setLayoutParams(layoutParams);
        add(imageView);

        for(String text:result){
            TextView textView = new TextView(context);
            textView.setText(text);
            textView.setTextSize(14);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            add(textView);
        }
    }
}
