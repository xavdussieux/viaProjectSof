package com.example.xavier.viaproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by Julien on 01/09/2016.
 */
public class CustomCircle {

    private int mX;
    private int mY;
    private int mCircleRadius;
    private int mColor;
    private int mSweepAngle;
    private String mText;
    private Canvas mCanvas;

    public CustomCircle(){
        mX = 0;
        mY = 0;
        mCircleRadius = 0;
        mText = null;
        mCanvas = null;
    }

    public CustomCircle(int x, int y, int circleRadius, int color, int sweepAngle, String text, Canvas canvas){
        mX = x;
        mY = y;
        mCircleRadius = circleRadius;
        mColor = color;
        mSweepAngle = sweepAngle;
        mText = text;
        mCanvas = canvas;
    }

    public void draw(){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mColor);
        RectF arcBounds = new RectF(mX - mCircleRadius, mY - mCircleRadius,
                mX + mCircleRadius, mY + mCircleRadius);
        mCanvas.drawArc(arcBounds,
                -90, mSweepAngle,
                true, paint);
        paint.setColor(Color.BLACK);
        mCanvas.drawCircle(mX, mY, mCircleRadius * 4 / 5, paint);

        if(mText != null) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(mCircleRadius * 4 / 5);
            textPaint.setAntiAlias(true);
            mCanvas.drawText(mText,
                    mX - textPaint.measureText(mText) / 2,
                    mY + mCircleRadius / 5, textPaint);
        }
    }

}
