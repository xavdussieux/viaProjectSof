package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Xavier on 04/08/2016.
 */
public class ScoreBar {

    private Score mScore;
    private int mScreenx;
    private int mScreeny;

    public ScoreBar(Context context, int screenx, int screeny, Score score) {
        mScore = score;
        mScreenx = screenx;
        mScreeny = screeny;
    }

    public void update (Canvas canvas) {
        int rectHeight = mScreeny / 18;
        int textHeight = mScreeny / 16;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0,0, mScreenx, rectHeight, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textHeight);
        String scoreText = "Score: " + mScore.getScore();
        canvas.drawText(scoreText, (mScreenx - paint.measureText(scoreText)) / 2, textHeight - rectHeight / 5, paint);

        Paint circlePaint = new Paint();
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        int color = Color.argb(255, 240, 150 - mScore.getPowerAccumulated(), 10);
        circlePaint.setColor(color);
        int circleRadius = rectHeight / 2;
        textPaint.setTextSize(circleRadius);
        canvas.drawCircle(1 + circleRadius, 1 + circleRadius, circleRadius, circlePaint);
        canvas.drawText(Integer.toString(mScore.getPowerAccumulated()),
                1 + circleRadius - textPaint.measureText(Integer.toString(mScore.getPowerAccumulated())) / 2,
                1 + textHeight * 6 / 10, textPaint);

        String multiplier;
        switch (mScore.getMultiplier()){
            case 2:
                color = Color.argb(255, 66, 210, 4);//green
                if(mScore.getIsPowerOn()) {
                    multiplier = "x4";
                }else{
                    multiplier = "x2";
                }
                break;
            case 3:
                color = Color.argb(255, 250, 240, 40);//yellow
                if(mScore.getIsPowerOn()) {
                    multiplier = "x6";
                }else{
                    multiplier = "x3";
                }
                break;
            case 4:
                color = Color.argb(255, 240, 50, 10);//red
                if(mScore.getIsPowerOn()) {
                    multiplier = "x8";
                }else{
                    multiplier = "x4";
                }
                break;
            default:
                color = Color.argb(255, 3, 180, 210);//blue
                if(mScore.getIsPowerOn()) {
                    multiplier = "x2";
                }else{
                    multiplier = "x1";
                }
                break;
        }

        circlePaint.setColor(color);
        canvas.drawCircle(mScreenx - circleRadius - 1, 1 + circleRadius, circleRadius, circlePaint);
        canvas.drawText(multiplier, mScreenx - circleRadius - textPaint.measureText(Integer.toString(mScore.getPowerAccumulated())) / 2,
                textHeight * 6 / 10 , textPaint);
    }
}
