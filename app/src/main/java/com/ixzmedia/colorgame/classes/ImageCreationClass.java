package com.ixzmedia.colorgame.classes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.util.DisplayMetrics;

import androidx.core.content.res.ResourcesCompat;

import com.ixzmedia.colorgame.R;

public class ImageCreationClass {

    private String text1;
    private String text2;

    private int resImageID;

    Context context;

    public ImageCreationClass(String text1, int resImageID, Context context) {
        this.text1 = text1;
        this.text2 = text2;
        this.resImageID = resImageID;
        this.context = context;
    }

    public Bitmap drawtextToBitmap(){
        //int textSize = 78;
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources,resImageID);

        Bitmap.Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }

        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(93, 101, 67));
        //paint.getColor() = Color.rgb(93, 101, 67);
        paint.setTextSize((15 * scale));

        Typeface fontFace = ResourcesCompat.getFont(context, R.font.custom_font);
        paint.setTypeface(Typeface.create(fontFace, Typeface.NORMAL));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        // draw text to the Canvas center
        //Rect bounds = new Rect(0,0,canvas.getWidth(),canvas.getHeight());

//        int width = canvas.getWidth();
//        int height = canvas.getHeight();
//        bounds.set

        // the display area.
        Rect areaRect = new Rect(0, 0, 240, 60);
        RectF bounds = new RectF(areaRect);

        // measure text width
        bounds.right = paint.measureText(text1, 0, text1.length());
        // measure text height
        bounds.bottom = paint.descent() - paint.ascent();

        bounds.left += (areaRect.width() - bounds.right) / 2.0f;
        bounds.top += (areaRect.height() - bounds.bottom) / 2.0f;

        canvas.drawText(text1, bounds.left, bounds.top - paint.ascent(), paint);


        //draw the first text
//        paint.getTextBounds(text1, 0, text1.length(), bounds);
//         float x = (bitmap.getWidth() - bounds.width()) / 2f - 470;
//        float y = (bitmap.getHeight() + bounds.height()) / 2f - 140;
//        canvas.drawText(text1, x, y, paint);


        //draw the second text
//        paint.getTextBounds(text2, 0, text2.length(), bounds);
//        x = (bitmap.getWidth() - bounds.width()) / 2f - 470;
//        y = (bitmap.getHeight() + bounds.height()) / 2f + 235;
//        canvas.drawText(text2, x, y, paint);

        return bitmap;
    }

    public Bitmap secondDraw(){

        //int textSize = 78;
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources,resImageID);

        Bitmap.Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }

        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);

        // the Paint instance(should be assign as a field of class).
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(15);

        // the display area.
        Rect areaRect = new Rect(0, 0, 240, 60);

        // draw the background style (pure color or image)
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(areaRect, mPaint);

        //String pageTitle = "文字小说";
        String pageTitle = text1;

        RectF bounds = new RectF(areaRect);
        // measure text width
        bounds.right = mPaint.measureText(pageTitle, 0, pageTitle.length());
        // measure text height
        bounds.bottom = mPaint.descent() - mPaint.ascent();

        bounds.left += (areaRect.width() - bounds.right) / 2.0f;
        bounds.top += (areaRect.height() - bounds.bottom) / 2.0f;

        mPaint.setColor(Color.WHITE);
        canvas.drawText(pageTitle, bounds.left, bounds.top - mPaint.ascent(), mPaint);
        return bitmap;
    }

    public Bitmap textAsBitmap() {
        // adapted from https://stackoverflow.com/a/8799344/1476989
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(30);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text1) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);

        int trueWidth = width;
        if(width>height)height=width; else width=height;
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text1, width/2-trueWidth/2, baseline, paint);
        return image;
    }

//    private int getTextSize(String text){
//        if (text.length() >= 21)
//            return 15;
//        else
//            return 20;
//    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public int getResImageID() {
        return resImageID;
    }

    public void setResImageID(int resImageID) {
        this.resImageID = resImageID;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
