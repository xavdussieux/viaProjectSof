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

    public ScoreBar(Context context, int screenx, int screeny, Score score) {
        mScore = score;
        mScreenx = screenx;
    }

    public void update (Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        canvas.drawRect(0,0, mScreenx, 70, paint);
        paint.setColor(Color.argb(255,  249, 129, 0));
        paint.setTextSize(40);
        canvas.drawText("Score: " + mScore.getScore(), 10,50, paint);
    }
}
