package com.topwise.iamge.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.topwise.iamge.api.IImgProcessing.*;
import com.topwise.iamge.api.IImgProcessing.IPage.*;
import com.topwise.iamge.api.IImgProcessing.IPage.ILine.*;
import com.topwise.iamge.api.IImgProcessing.IRgbToMonoAlgorithm.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import android.widget.TextView;
/**
 * 创建日期：2021/5/20 on 10:24
 * 描述:
 * 作者:wangweicheng
 */
public class ImgProcessingPageComposing {
    private Context mContext;
    private static ImgProcessingPageComposing instance;
    private static Hashtable<String, Typeface> dl = new Hashtable();

    private ImgProcessingPageComposing(Context context) {
        this.mContext = context;
    }

    static ImgProcessingPageComposing getIntance(Context context) {
        if (instance == null) {
            instance = new ImgProcessingPageComposing(context);
        }

        return instance;
    }

    protected final IPage createPage() {

        return new ImgProcessingPageComposing.Page();
    }

    final Bitmap ImageComposing(IPage page, int pageWidth) {
        View view;
        (view = this.getView(this.mContext, page, page.getLines(), pageWidth))
                .measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, pageWidth, view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(pageWidth, view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    private View getView(Context context, IPage page, List<ILine> list, int pageWidth) {
        ScrollView scrollView;
        (scrollView = new ScrollView(context)).setLayoutParams(new LinearLayout.LayoutParams(pageWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        scrollView.setBackgroundColor(Color.WHITE);
        LinearLayout linearLayout;
        (linearLayout = new LinearLayout(context)).setLayoutParams(new LinearLayout.LayoutParams(pageWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.WHITE);
        pageWidth = 0;
        Iterator iterator = list.iterator();

        while(iterator.hasNext()) {
            ILine iLine = (ILine)iterator.next();
            ++pageWidth;
            LinearLayout linear;
            (linear = new LinearLayout(context)).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linear.setOrientation(LinearLayout.HORIZONTAL);
            linear.setGravity(Gravity.CENTER_VERTICAL);
            List listTmep = iLine.getUnits();
            ILine.IUnit iUnit = null;
            Iterator iteratorTemp = listTmep.iterator();

            while(true) {
                while(iteratorTemp.hasNext()) {
                    iUnit = (ILine.IUnit)iteratorTemp.next();
                    String typeFace = page.getTypeFace();
                    float weight = iUnit.getWeight();
                    Bitmap bitmap = iUnit.getBitmap();
                    String content = iUnit.getText();
                    ImgProcessingPageComposing.PrintView printView;
                    if (bitmap == null && content == null) {
                        (printView = new ImgProcessingPageComposing.PrintView(context, typeFace)).setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
                        printView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
                        linear.addView(printView);
                    } else if (content != null && content.length() > 0) {
                        (printView = new ImgProcessingPageComposing.PrintView(context, typeFace)).setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
                        SpannableString spannableString;
                        (spannableString = new SpannableString(iUnit.getText())).setSpan(new AbsoluteSizeSpan(iUnit.getFontSize()), 0, spannableString.toString().length(), 33);
                        printView.setText(spannableString);
                        printView.setTextColor(Color.BLACK );
                        printView.setTextSize((float)iUnit.getFontSize());

                        switch(iUnit.getAlign()) {
                            case CENTER:
                                printView.setGravity(Gravity.CENTER);
                                break;
                            case RIGHT:
                                printView.setGravity(Gravity.RIGHT);
                                break;
                            case LEFT:
                            default:
                                printView.setGravity(Gravity.LEFT);
                        }
//                        switch(iUnit.getAlign().ordinal()) {
//                            case 1:
//                                printView.setGravity(17);
//                                break;
//                            case 2:
//                                printView.setGravity(5);
//                                break;
//                            case 0:
//                            default:
//                                printView.setGravity(3);
//                        }

                        printView.getPaint().setFakeBoldText(false);
                        printView.getPaint().setUnderlineText(false);
                        if ((iUnit.getTextStyle() & 1) != 0) {
                            printView.getPaint().setFakeBoldText(true);
                        }

                        if ((iUnit.getTextStyle() & 2) != 0) {
                            printView.getPaint().setUnderlineText(true);
                        }

                        linear.addView(printView);
                    } else if (bitmap != null) {
                        ImageView iGview;
                        (iGview = new ImageView(context)).setLayoutParams(new LinearLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight(), 0.0F));
                        iGview.setScaleType(ImageView.ScaleType.CENTER);
                        switch(iUnit.getAlign()) {

                            case CENTER:
                                linear.setGravity(Gravity.CENTER);
                                break;
                            case RIGHT:
                                linear.setGravity(Gravity.RIGHT);
                                break;
                            case LEFT:
                            default:
                                linear.setGravity(Gravity.LEFT);
                        }

                        iGview.setImageBitmap(bitmap);
                        linear.addView(iGview);
                    }
                }

                linearLayout.addView(linear);
                if (pageWidth != 1) {
                    try {
                        IPage.class.getDeclaredMethod("getLineSpaceAdjustment");
                        ILine.class.getDeclaredMethod("getTopSpaceAdjustment");
                        int spaceAdjustment = page.getLineSpaceAdjustment();
                        int spaceAdd;
                        if ((spaceAdd = iLine.getTopSpaceAdjustment()) != 65535) {
                            spaceAdjustment = spaceAdd;
                        }

                        Log.e("spaceAdjustment","spaceAdjustment " +spaceAdjustment);

                        LinearLayout.LayoutParams layoutParams;
                        (layoutParams = (LinearLayout.LayoutParams)linear.getLayoutParams()).topMargin = spaceAdjustment;
                        linear.setLayoutParams(layoutParams);
                    } catch (NoSuchMethodException e) {
                    }
                }
                break;
            }
        }

        scrollView.addView(linearLayout);
        return scrollView;
    }

    private static Typeface e(String var0) {
        Typeface var1;
        if ((var1 = (Typeface)dl.get(var0)) == null) {
            try {
                var1 = Typeface.createFromFile(var0);
            } catch (Exception var2) {
                var2.printStackTrace();
                return null;
            }

            dl.put(var0, var1);
        }

        return var1;
    }

    private class Line implements ILine {
        private List<IUnit> line;
        private ImgProcessingPageComposing.Unit unit;
        private int spacingAdd;

        private Line() {
            this.spacingAdd = 65535;
            this.line = new ArrayList();
        }

        public List<IUnit> getUnits() {
            return this.line;
        }

        public ILine addUnit() {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(IUnit var1) {
            this.line.add(var1);
            return this;
        }

        public ILine addUnit(int weight) {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setWeight((float)weight);
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(Bitmap bitmap) {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setBitmap(bitmap);
            this.unit.setText("");
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(Bitmap bitmap, EAlign align) {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setBitmap(bitmap);
            this.unit.setText("");
            this.unit.setAlign(align);
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(String content, int fontsize) {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setText(content);
            this.unit.setFontSize(fontsize);
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(String content, int fontsize, int textstyle) {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setText(content);
            this.unit.setFontSize(fontsize);
            this.unit.setTextStyle(textstyle);
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(String content, int fontsize, float weight) {
            this.unit =ImgProcessingPageComposing.this.new Unit();
            this.unit.setText(content);
            this.unit.setFontSize(fontsize);
            this.unit.setWeight(weight);
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(String content, int fontsize, EAlign align) {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setText(content);
            this.unit.setFontSize(fontsize);
            this.unit.setAlign(align);
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(String content, int fontsize, int textstyle, float weight) {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setText(content);
            this.unit.setFontSize(fontsize);
            this.unit.setTextStyle(textstyle);
            this.unit.setWeight(weight);
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(String content, int fontsize, EAlign align, float weight) {
            if (content == null) {
                content = "";
            }
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setText(content);
            this.unit.setFontSize(fontsize);
            this.unit.setAlign(align);
            this.unit.setWeight(weight);
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(String content, int fontsize, EAlign align, int textstyle) {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setText(content);
            this.unit.setFontSize(fontsize);
            this.unit.setAlign(align);
            this.unit.setTextStyle(textstyle);
            this.line.add(this.unit);
            return this;
        }

        public ILine addUnit(String content, int fontsize, EAlign align, int textstyle, float weight) {
            this.unit = ImgProcessingPageComposing.this.new Unit();
            this.unit.setText(content);
            this.unit.setFontSize(fontsize);
            this.unit.setAlign(align);
            this.unit.setTextStyle(textstyle);
            this.unit.setWeight(weight);
            this.line.add(this.unit);
            return this;
        }

        public ILine adjustTopSpace(int spacingAdd) {
            this.spacingAdd = spacingAdd;
            return this;
        }

        public int getTopSpaceAdjustment() {
            return this.spacingAdd;
        }
    }

    private class Page implements IPage {
        private String typeFace;
        private List<ILine> pages;
        private ImgProcessingPageComposing.Line line;
        private int spacingAdd;

        private Page() {
            this.typeFace = "";
            this.spacingAdd = 0;
            this.pages = new ArrayList();
        }

        public ILine addLine() {
            this.line = ImgProcessingPageComposing.this.new Line();
            this.pages.add(this.line);
            return this.line;
        }

        public List<ILine> getLines() {
            return this.pages;
        }

        public String getTypeFace() {
            return this.typeFace;
        }

        public void setTypeFace(String typeFace) {
            this.typeFace = typeFace;
        }

        public ILine.IUnit createUnit() {
            return ImgProcessingPageComposing.this.new Unit();
        }

        public void adjustLineSpace(int spacingAdd) {
            this.spacingAdd = spacingAdd;
        }

        public int getLineSpaceAdjustment() {
            return this.spacingAdd;
        }

        public Bitmap toBitmap(int param1Int) {
            return ImgProcessingPageComposing.this.ImageComposing(this, param1Int);
        }
    }

    @SuppressLint("AppCompatCustomView")
    private class PrintView extends TextView {
        TextPaint mTextPaint = new TextPaint();
        Typeface mTypeFace;

        public PrintView(Context context, String typeface) {
            super(context);
            if (typeface != null && !typeface.equals("")) {
                this.mTypeFace = ImgProcessingPageComposing.e(typeface);
            }

            this.setTypeface(this.mTypeFace);
        }

        @SuppressLint({"DrawAllocation"})
        protected void onDraw(Canvas canvas) {
            this.mTextPaint.setTypeface(this.mTypeFace);
            TextPaint textPaint = this.mTextPaint;
            float textSize = this.getTextSize();
            String txtString = null;
            float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
            textPaint.setTextSize((float)((int)(textSize / scaledDensity + 0.5F)));
            this.mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.mTextPaint.setColor(Color.BLACK);
            this.mTextPaint.setSubpixelText(true);
            this.mTextPaint.setLinearText(true);
            if (this.getPaint().isFakeBoldText()) {
                this.mTextPaint.setFakeBoldText(true);
            }

            if (this.getPaint().isUnderlineText()) {
                this.mTextPaint.setUnderlineText(true);
            }

            txtString = this.getText().toString();
            String newtext = "";
            String[] strArrs;
            int counts = (strArrs = txtString.split("\n")).length;

            int var4;
            for(var4 = 0; var4 < counts; ++var4) {
                if ((txtString = strArrs[var4]).length() == 0) {
                    newtext = newtext + "\n";
                } else {
                    while(txtString.length() > 0) {
                        int paintSize = this.mTextPaint.breakText(txtString, true, (float)this.getWidth(), (float[])null);
                        newtext = newtext + txtString.substring(0, paintSize) + "\n";
                        txtString = txtString.substring(paintSize);
                    }
                }
            }

            StaticLayout mTextLayout;
            if ((var4 = this.getGravity()) != 49 && var4 != 17 && var4 != 8388627) {
                if (var4 == 53) {
                    mTextLayout = new StaticLayout(newtext, this.mTextPaint, this.getWidth(), Layout.Alignment.ALIGN_OPPOSITE, 1.0F, 0.0F, false);
                } else {
                    mTextLayout = new StaticLayout(newtext, this.mTextPaint, this.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
                }
            } else {
                mTextLayout = new StaticLayout(newtext, this.mTextPaint, this.getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
            }
            canvas.scale(1, 1);
            canvas.save();
            canvas.translate(0.0F, 0.0F);
            mTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    private class Unit implements IUnit {
        private String content;
        private Bitmap bitmap;
        private int fontSize;
        private EAlign align;
        private int textStyle;
        private float weight;

        private Unit() {
            this.weight = 1.0F;
            this.textStyle = 0;
            this.weight = 1.0F;
            this.fontSize = 20;
            this.align = EAlign.LEFT;
            this.content = " ";
            this.bitmap = null;
        }

        public String getText() {
            return this.content;
        }

        public IUnit setText(String text) {
            this.content = text;
            return this;
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }

        public IUnit setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public int getFontSize() {
            return this.fontSize;
        }

        public IUnit setFontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public EAlign getAlign() {
            return this.align;
        }

        public IUnit setAlign(EAlign align) {
            this.align = align;
            return this;
        }

        public int getTextStyle() {
            return this.textStyle;
        }

        public IUnit setTextStyle(int textStyle) {
            this.textStyle = textStyle;
            return this;
        }

        public float getWeight() {
            return this.weight;
        }

        public IUnit setWeight(float weight) {
            this.weight = weight;
            return this;
        }
    }
}
