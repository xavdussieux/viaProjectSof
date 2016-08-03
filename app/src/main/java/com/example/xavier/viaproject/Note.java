package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 02/08/2016.
 */
public class Note {

    private static final int NOTE_SPEED = 10;

    private Bitmap mBlue;
    private Bitmap mGreen;
    private Bitmap mYellow;
    private Bitmap mRed;
    private int mNoteSize;
    private int mScreenx;
    private int mScreeny;
    private int BLUE_X;
    private int RED_X;
    private int GREEN_X;
    private int YELLOW_X;
    private List<Point> mNotes;

    public Note (Context context, int screenX, int screenY) {

        mScreenx = screenX;
        mScreeny = screenY;
        mNoteSize = screenX/3;

        GREEN_X = screenX * 1/5 - 200;
        RED_X = screenX * 2/5 - 200;
        YELLOW_X = screenX * 3/5 - 200;
        BLUE_X = screenX * 4/5 - 200;


        mBlue = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluenote);
        mGreen = BitmapFactory.decodeResource(context.getResources(), R.drawable.greennote);
        mRed = BitmapFactory.decodeResource(context.getResources(), R.drawable.rednote);
        mYellow = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellownote);
        mBlue = Bitmap.createScaledBitmap(mBlue,mNoteSize, mNoteSize, false);
        mGreen = Bitmap.createScaledBitmap(mGreen,mNoteSize, mNoteSize, false);
        mRed = Bitmap.createScaledBitmap(mRed,mNoteSize, mNoteSize, false);
        mYellow = Bitmap.createScaledBitmap(mYellow,mNoteSize, mNoteSize, false);
        mNotes = new ArrayList<Point>();
    }

    public void spawn(String noteType) {
        Paint paint = new Paint();
        paint.setColor(Color.argb(255, 249, 129, 0));
        switch (noteType) {
            case "green":
                mNotes.add(new Point(GREEN_X, -200));
                break;
            case "red":
                mNotes.add(new Point(RED_X, -200));
                break;
            case "yellow":
                mNotes.add(new Point(YELLOW_X, -200));
                break;
            case "blue":
                mNotes.add(new Point(BLUE_X, -200));
                break;
        }
    }

    public void update(Canvas canvas) {
        Point del = null;
        for (Point i : mNotes) {
            Paint paint = new Paint();
            paint.setColor(Color.argb(255, 249, 129, 0));
            //switch needs constants
            if (i.x == GREEN_X)
                canvas.drawBitmap(mGreen, i.x, i.y, paint);
            if (i.x == RED_X)
                canvas.drawBitmap(mRed, i.x, i.y, paint);
            if (i.x == YELLOW_X)
                canvas.drawBitmap(mYellow, i.x, i.y, paint);
            if (i.x == BLUE_X)
                canvas.drawBitmap(mBlue, i.x, i.y, paint);
            i.y += NOTE_SPEED;
            if (i.y > mScreeny){
                del = i;
            }
        }
        if(del != null){
            mNotes.remove(del);
        }
    }
}
