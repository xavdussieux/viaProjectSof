package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 02/08/2016.
 */
public class Note  {

    private static final int NOTE_SPEED = 10;

    private Bitmap mBlue;
    private Bitmap mGreen;
    private Bitmap mYellow;
    private Bitmap mRed;
    private int mNoteSize;
    private int mScreeny;
    private int mBlueX;
    private int mRedX;
    private int mGreenX;
    private int mYellowX;
    private List<Point> mNotes;

    public Note (final Context context, int screenX, int screenY) {

        //resizing notes according to the player's screen
        mScreeny = screenY;
        mNoteSize = screenX / 5;
        mGreenX = screenX / 10;
        mRedX = screenX * 3/10;
        mYellowX = screenX * 5/10;
        mBlueX = screenX * 7/10;


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
                mNotes.add(new Point(mGreenX, -200));
                break;
            case "red":
                mNotes.add(new Point(mRedX, -200));
                break;
            case "yellow":
                mNotes.add(new Point(mYellowX, -200));
                break;
            case "blue":
                mNotes.add(new Point(mBlueX, -200));
                break;
        }
    }

    public int getNoteSize() {
        return mNoteSize;
    }

    public List<Point> getNotes() {
        return mNotes;
    }

    public void update(Canvas canvas) {
        Point del = null;
        for (Point point : mNotes) {
            if(point != null) {
                Paint paint = new Paint();
                paint.setColor(Color.argb(255, 249, 129, 0));
                //switch needs constants
                if (point.x == mGreenX)
                    canvas.drawBitmap(mGreen, point.x, point.y, paint);
                if (point.x == mRedX)
                    canvas.drawBitmap(mRed, point.x, point.y, paint);
                if (point.x == mYellowX)
                    canvas.drawBitmap(mYellow, point.x, point.y, paint);
                if (point.x == mBlueX)
                    canvas.drawBitmap(mBlue, point.x, point.y, paint);
                point.y += NOTE_SPEED;
                if (point.y > mScreeny) {
                    del = point;
                }
            }
        }
        if(del != null){
            removeNote(del);
        }
    }

    public void removeNote(Point noteObj){
        mNotes.remove(noteObj);
    }

}
