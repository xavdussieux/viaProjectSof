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
public class Note extends View implements View.OnTouchListener {

    private static final int NOTE_SPEED = 10;

    private Bitmap mBlue;
    private Bitmap mGreen;
    private Bitmap mYellow;
    private Bitmap mRed;
    private int mNoteSize;
    private int mScreenx;
    private int mScreeny;
    private int mBlueX;
    private int mRedX;
    private int mGreenX;
    private int mYellowX;
    private List<Point> mNotes;

    public Note (final Context context, int screenX, int screenY) {
        //initializing view
        super(context);

        //resizing notes according to the player's screen
        mScreenx = screenX;
        mScreeny = screenY;
        mNoteSize = screenX/5;
        mGreenX = screenX * 1/10;
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //method seems to be never called
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            mBlue = mYellow;
            Toast.makeText(this.getContext(), "Touch listened", Toast.LENGTH_SHORT).show();
        }
        return true;
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

    public void update(Canvas canvas) {
        Point del = null;
        for (Point i : mNotes) {
            Paint paint = new Paint();
            paint.setColor(Color.argb(255, 249, 129, 0));
            //switch needs constants
            if (i.x == mGreenX)
                canvas.drawBitmap(mGreen, i.x, i.y, paint);
            if (i.x == mRedX)
                canvas.drawBitmap(mRed, i.x, i.y, paint);
            if (i.x == mYellowX)
                canvas.drawBitmap(mYellow, i.x, i.y, paint);
            if (i.x == mBlueX)
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
