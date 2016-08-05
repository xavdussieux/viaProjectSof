package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Xavier on 04/08/2016.
 */
public class ScoreBar {

    private Bitmap mBar;
    private Score mScore;

    public ScoreBar(Context context, int screenx, int screeny, Score score) {
        mBar = BitmapFactory.decodeResource(context.getResources(), R.drawable.fontbar);
        mBar = Bitmap.createScaledBitmap(mBar,screenx, 70, false);
        mScore = score;
    }

    public void update (Canvas canvas) {
        Paint paint = new Paint();
        canvas.drawBitmap(mBar, 0,0, paint);
        paint.setColor(Color.argb(255,  249, 129, 0));
        paint.setTextSize(40);
        canvas.drawText("Score: " + mScore.getScore(), 10,50, paint);
    }
}
