package com.example.xavier.viaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 02/08/2016.
 */
public class Note {

    private Bitmap mBlue;
    private Bitmap mGreen;
    private Bitmap mYellow;
    private Bitmap mRed;
    private int mEndY;
    private int mNoteSize;
    private int mScreeny;
    private int mBlueX;
    private int mRedX;
    private int mGreenX;
    private int mYellowX;
    private Score mScore;
    private float mSpeed;
    private float radius;
    private GameLoopThread mGameLoopThread;

    public class TypeNote {
        public int spawnTime;
        public Point pos;

        public TypeNote(int spawnTime_ini, Point pos_ini) {
            spawnTime = spawnTime_ini;
            pos = pos_ini;
        }
    }

    private List<TypeNote> mNotes;
    private List<TypeNote> mNotesToRemove;

    public Note(Context context, int screenX, int screenY, GameLoopThread gameLoopThread, Score score, int scrolling_time) {

        //resizing notes according to the player's screen
        mScreeny = screenY;
        mNoteSize = screenX / 5;
        mGreenX = screenX / 10 + fromDP(context,20);
        mRedX = screenX * 3 / 10 + fromDP(context,32);
        mYellowX = screenX * 5 / 10 + fromDP(context,45);
        mBlueX = screenX * 7 / 10 + fromDP(context,58);
        mEndY = screenY - 3 * mNoteSize / 2;
        float endY = screenY - 3 * mNoteSize / 2;
        mScore = score;
        float interval = endY - Constants.NOTE_START_Y;
        radius = mScreeny / 18;
        interval = mEndY - Constants.NOTE_START_Y;

        mSpeed = interval / scrolling_time;

        mBlue = BitmapFactory.decodeResource(context.getResources(), R.drawable.bluenote4);
        mGreen = BitmapFactory.decodeResource(context.getResources(), R.drawable.greennote);
        mRed = BitmapFactory.decodeResource(context.getResources(), R.drawable.rednote);
        mYellow = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellownote);
        mBlue = Bitmap.createScaledBitmap(mBlue, mNoteSize, mNoteSize, false);
        mGreen = Bitmap.createScaledBitmap(mGreen, mNoteSize, mNoteSize, false);
        mRed = Bitmap.createScaledBitmap(mRed, mNoteSize, mNoteSize, false);
        mYellow = Bitmap.createScaledBitmap(mYellow, mNoteSize, mNoteSize, false);

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

    public void addNoteToRemove(TypeNote note) {
        mNotesToRemove.add(note);
    }

    public void update(Canvas canvas) {
        TypeNote del = null;
        for (TypeNote note : mNotes) {
            Paint paintBlue = new Paint();
            Paint paintRed = new Paint();
            Paint paintYellow = new Paint();
            Paint paintGreen = new Paint();

            paintGreen.setColor(Color.argb(255, 0, 129, 0));
            paintRed.setColor(Color.argb(255, 255, 0, 0));
            paintYellow.setColor(Color.argb(255, 255, 255, 0));
            paintBlue.setColor(Color.argb(255, 0, 0, 255));


            //switch needs constants
            if (note.pos.x == mGreenX)
                canvas.drawCircle(note.pos.x, note.pos.y, radius, paintGreen);
            if (note.pos.x == mRedX) canvas.drawCircle(note.pos.x, note.pos.y, radius, paintRed);
            if (note.pos.x == mYellowX)
                canvas.drawCircle(note.pos.x, note.pos.y, radius, paintYellow);
            if (note.pos.x == mBlueX) canvas.drawCircle(note.pos.x, note.pos.y, radius, paintBlue);
            int dt = mGameLoopThread.getTime() - note.spawnTime;
            note.pos.y = (int) (dt * mSpeed + Constants.NOTE_START_Y);
            if (note.pos.y > mScreeny) {
                del = note;
            }
        }
        if (del != null) {
            addNoteToRemove(del);
            mScore.missed();
        }
        removeNotes();
    }

    public void removeNotes() {
        if (mNotesToRemove != null && !mNotesToRemove.isEmpty()) {
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
        for (TypeNote note : mNotes) {
            dx = touchedPoint.x - note.pos.x;
            dy = touchedPoint.y - note.pos.y;
            if ((Math.sqrt(dx * dx + dy * dy) < radius))
            {
                addNoteToRemove(note);
                return true;
            }
        }
        return false;
    }

    public int fromDP(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }
}
