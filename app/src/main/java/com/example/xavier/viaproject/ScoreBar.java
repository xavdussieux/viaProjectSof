package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by Xavier on 04/08/2016.
 */
public class ScoreBar {

    private Score mScore;
    private int mScreenx;
    private int mScreeny;

    public ScoreBar(int screenx, int screeny, Score score) {
        mScore = score;
        mScreenx = screenx;
        mScreeny = screeny;
    }

    public void update (Canvas canvas, int songPer) {
        int rectHeight = mScreeny / 18;
        int textHeight = mScreeny / 16;

        //drawing black rectangle to show score
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0,0, mScreenx, rectHeight, paint);

        //drawing score in white
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(textHeight);
        String scoreText = Integer.toString(mScore.getScore());
        canvas.drawText(scoreText, (mScreenx - paint.measureText(scoreText)) / 2, textHeight - rectHeight / 5, paint);

        //drawing music time line
        paint.setColor(Color.RED);
        paint.setAntiAlias(false);
        canvas.drawRect(0,rectHeight, mScreenx * songPer / 100, rectHeight * 10 / 9, paint);

        //drawing power circle
        Paint circlePaint = new Paint();
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        int color = Color.argb(255, 240, 150 - mScore.getPowerAccumulated(), 10);
        circlePaint.setColor(color);
        circlePaint.setAntiAlias(true);
        int circleRadius = rectHeight;
        textPaint.setTextSize(circleRadius * 4 / 5);
        textPaint.setAntiAlias(true);
        RectF arcBounds = new RectF(circleRadius / 4, circleRadius * 5 / 4,
                                    circleRadius * 9 / 4, circleRadius * 13 / 4);
        canvas.drawArc(arcBounds,
                       -90, mScore.getPowerAccumulated() * 360 / 100,
                       true, circlePaint);
        circlePaint.setColor(Color.BLACK);
        canvas.drawCircle(circleRadius * 5 / 4,circleRadius * 9 / 4, circleRadius * 4 / 5, circlePaint);
        canvas.drawText(Integer.toString(mScore.getPowerAccumulated()),
                circleRadius * 5 / 4 - textPaint.measureText(Integer.toString(mScore.getPowerAccumulated())) / 2,
                circleRadius * 10 / 4, textPaint);

        //drawing multiplier circle
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
        canvas.drawCircle(mScreenx - circleRadius * 5 / 4, circleRadius * 9 / 4, circleRadius, circlePaint);
        circlePaint.setColor(Color.BLACK);
        canvas.drawCircle(mScreenx - circleRadius * 5 / 4, circleRadius * 9 / 4, circleRadius * 4 / 5, circlePaint);
        canvas.drawText(multiplier, mScreenx - circleRadius * 5 / 4 - textPaint.measureText(Integer.toString(mScore.getPowerAccumulated())) / 2,
                circleRadius * 10 / 4 , textPaint);
    }
}
