package com.example.xavier.viaproject;

import android.content.Context;
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

    private int mScreeny;
    private int mScreenx;
    private int mBlueX;
    private int mRedX;
    private int mGreenX;
    private int mYellowX;
    private Score mScore;
    private float mSpeed;
    private float mRadius;
    private GameLoopThread mGameLoopThread;
    private float mTouchedLimit;

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
        mScreenx = screenX;
        int noteSize = screenX / 6;
        mGreenX = screenX * 55 / 355;
        mRedX = screenX * 139 / 355;
        mYellowX = screenX * 224 / 355;
        mBlueX = screenX * 305 / 355;
        float endY = screenY - 3 * noteSize / 2;
        mScore = score;
        float interval = endY - Constants.NOTE_START_Y;
        mRadius = mScreeny / 18;

        mTouchedLimit = screenY - 4 * mRadius;
        mSpeed = interval / scrolling_time;

        mNotes = new ArrayList<TypeNote>();
        mNotesToRemove = new ArrayList<TypeNote>();
        mGameLoopThread = gameLoopThread;
    }

    public void spawn(String noteType) {
        Paint paint = new Paint();
        paint.setColor(Color.argb(255, 249, 129, 0));
        switch (noteType) {
            case "green":
                mNotes.add(new TypeNote(mGameLoopThread.getTime(), new Point(mGreenX, Constants.NOTE_START_Y)));
                break;
            case "red":
                mNotes.add(new TypeNote(mGameLoopThread.getTime(), new Point(mRedX, Constants.NOTE_START_Y)));
                break;
            case "yellow":
                mNotes.add(new TypeNote(mGameLoopThread.getTime(), new Point(mYellowX, Constants.NOTE_START_Y)));
                break;
            case "blue":
                mNotes.add(new TypeNote(mGameLoopThread.getTime(), new Point(mBlueX, Constants.NOTE_START_Y)));
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
            Paint paintWhite = new Paint();

            if(mScore.getIsPowerOn()){
                //if power is activated, all the notes should be orange
                paintGreen.setColor(Color.argb(255, 240, 50, 10));
                paintRed.setColor(Color.argb(255, 240, 50, 10));
                paintYellow.setColor(Color.argb(255, 240, 50, 10));
                paintBlue.setColor(Color.argb(255, 240, 50, 10));
            }else {
                paintGreen.setColor(Color.argb(255, 0, 129, 0));
                paintRed.setColor(Color.argb(255, 255, 0, 0));
                paintYellow.setColor(Color.argb(255, 255, 255, 0));
                paintBlue.setColor(Color.argb(255, 0, 0, 255));
            }
            paintWhite.setColor(Color.WHITE);

            //switch needs constants
            if (note.pos.x == mGreenX)
                canvas.drawCircle(note.pos.x, note.pos.y, mRadius, paintGreen);
            if (note.pos.x == mRedX)
                canvas.drawCircle(note.pos.x, note.pos.y, mRadius, paintRed);
            if (note.pos.x == mYellowX)
                canvas.drawCircle(note.pos.x, note.pos.y, mRadius, paintYellow);
            if (note.pos.x == mBlueX)
                canvas.drawCircle(note.pos.x, note.pos.y, mRadius, paintBlue);
            canvas.drawLine(0, mTouchedLimit, mScreenx, mTouchedLimit, paintYellow);
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
        for (TypeNote note : mNotes) {
            dx = touchedPoint.x - note.pos.x;
            dy = touchedPoint.y - note.pos.y;
            if (touchedPoint.y > mTouchedLimit) {
                if ((Math.sqrt(dx * dx + dy * dy) < mRadius)) {
                    addNoteToRemove(note);
                    return true;
                }
            }
        }
        return false;
    }
}
