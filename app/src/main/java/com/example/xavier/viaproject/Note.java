package com.example.xavier.viaproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 02/08/2016.
 */
public class Note  {

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
    private Score mScore;
    private int mEndY;
    private float mSpeed;
    private GameLoopThread mGameLoopThread;

    public class TypeNote {
        public int spawnTime;
        public Point pos;
        public TypeNote (int spawnTime_ini, Point pos_ini) {
            spawnTime = spawnTime_ini;
            pos = pos_ini;
        }
    }

    private List<TypeNote> mNotes;
    private List<TypeNote> mNotesToRemove;

    public Note (Context context, int screenX, int screenY, GameLoopThread gameLoopThread, Score score) {

        //resizing notes according to the player's screen
        mScreeny = screenY;
        mNoteSize = screenX / 5;
        mGreenX = screenX / 10;
        mRedX = screenX * 3/10;
        mYellowX = screenX * 5/10;
        mBlueX = screenX * 7/10;
        mEndY = screenY;
        mScore = score;
        float interval = mEndY - Constants.NOTE_START_Y;
        mSpeed = interval / Constants.NOTE_SCROLLING_TIME;


        mBlue = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluenote);
        mGreen = BitmapFactory.decodeResource(context.getResources(), R.drawable.greennote);
        mRed = BitmapFactory.decodeResource(context.getResources(), R.drawable.rednote);
        mYellow = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellownote);
        mBlue = Bitmap.createScaledBitmap(mBlue,mNoteSize, mNoteSize, false);
        mGreen = Bitmap.createScaledBitmap(mGreen,mNoteSize, mNoteSize, false);
        mRed = Bitmap.createScaledBitmap(mRed,mNoteSize, mNoteSize, false);
        mYellow = Bitmap.createScaledBitmap(mYellow,mNoteSize, mNoteSize, false);

        mNotes = new ArrayList<TypeNote>();
        mNotesToRemove = new ArrayList<TypeNote>();
        mGameLoopThread = gameLoopThread;
    }

    public void spawn(String noteType) {
        Paint paint = new Paint();
        paint.setColor(Color.argb(255, 249, 129, 0));
        switch (noteType) {
            case "green":
                mNotes.add(new TypeNote(mGameLoopThread.getTime(), new Point(mGreenX, -200)));
                break;
            case "red":
                mNotes.add(new TypeNote(mGameLoopThread.getTime(), new Point(mRedX, -200)));
                break;
            case "yellow":
                mNotes.add(new TypeNote(mGameLoopThread.getTime(), new Point(mYellowX, -200)));
                break;
            case "blue":
                mNotes.add(new TypeNote(mGameLoopThread.getTime(), new Point(mBlueX, -200)));
                break;
        }
    }

    public void addNoteToRemove(TypeNote note){
        mNotesToRemove.add(note);
    }

    public void update(Canvas canvas) {
        TypeNote del = null;
        for (TypeNote note : mNotes) {
            Paint paint = new Paint();
            paint.setColor(Color.argb(255, 249, 129, 0));
            //switch needs constants
            if (note.pos.x == mGreenX) canvas.drawBitmap(mGreen, note.pos.x, note.pos.y, paint);
            if (note.pos.x == mRedX) canvas.drawBitmap(mRed, note.pos.x, note.pos.y, paint);
            if (note.pos.x == mYellowX) canvas.drawBitmap(mYellow, note.pos.x, note.pos.y, paint);
            if (note.pos.x == mBlueX) canvas.drawBitmap(mBlue, note.pos.x, note.pos.y, paint);
            int dt = mGameLoopThread.getTime() - note.spawnTime;
            note.pos.y = (int) (dt * mSpeed + Constants.NOTE_START_Y);
            Log.e("posy", Integer.toString(note.pos.y));
            if (note.pos.y > mScreeny) {
                del = note;
            }
        }
        if(del != null){
            addNoteToRemove(del);
            mScore.missed();
        }
        removeNotes();
    }

    public void removeNotes(){
        if(mNotesToRemove != null && !mNotesToRemove.isEmpty()) {
            //remove notes
            for (TypeNote note : mNotesToRemove) {
                mNotes.remove(note);
            }
            //reinitialize array
            mNotesToRemove.removeAll(mNotesToRemove);
        }
    }

    public boolean onNoteWithDel(Point touchedPoint) {
        int dx, dy;
        TypeNote del = null;
        for(TypeNote note : mNotes) {
            dx = touchedPoint.x - note.pos.x;
            dy = touchedPoint.y - note.pos.y;
            if(dx > 0 && dx <  mNoteSize)
                if (dy > 0 && dy < mNoteSize){
                    addNoteToRemove(note);
                    return true;
                }
        }
        return false;
    }

}
