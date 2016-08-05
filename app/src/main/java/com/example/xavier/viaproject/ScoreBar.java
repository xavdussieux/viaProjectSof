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
    }
}
