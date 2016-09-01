package com.example.xavier.viaproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Xavier on 04/08/2016.
 */
public class ScoreBar {

    private Score mScore;
    private int mScreenX;
    private int mScreenY;

    public ScoreBar(int screenX, int screenY, Score score) {
        mScore = score;
        mScreenX = screenX;
        mScreenY = screenY;
    }

    public void update (Canvas canvas, int songPer) {
        int rectHeight = mScreenY / 18;
        int textHeight = mScreenY / 16;

        //drawing black rectangle to show score
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0,0, mScreenX, rectHeight, paint);

        //drawing score in white
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(textHeight);
        String scoreText = Integer.toString(mScore.getScore());
        canvas.drawText(scoreText, (mScreenX - paint.measureText(scoreText)) / 2,
                textHeight - rectHeight / 5, paint);

        //drawing music time line
        paint.setColor(Color.RED);
        paint.setAntiAlias(false);
        canvas.drawRect(0,rectHeight, mScreenX * songPer / 100, rectHeight * 10 / 9, paint);

        //drawing power circle
        int color = Color.argb(255, 240, 150 - mScore.getPowerAccumulated(), 10);
        CustomCircle powerCircle = new CustomCircle(rectHeight * 5 / 4, rectHeight * 9 / 4,
                rectHeight, color, mScore.getPowerAccumulated() * 360 / 100,
                Integer.toString(mScore.getPowerAccumulated()), canvas);
        powerCircle.draw();

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
        CustomCircle multiplierCircle = new CustomCircle(mScreenX - rectHeight * 5 / 4,
                rectHeight * 9 / 4, rectHeight, color, 360, multiplier, canvas);
        multiplierCircle.draw();

    }
}
