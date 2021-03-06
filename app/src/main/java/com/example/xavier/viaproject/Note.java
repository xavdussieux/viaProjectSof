package com.example.xavier.viaproject;

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
    private int mNoteRadius;
    private GameLoopThread mGameLoopThread;
    private int mTouchLimit;
    private float mLeakPoint;

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

    public Note(int screenX, int screenY, GameLoopThread gameLoopThread, Score score, int scrolling_time) {

        //resizing notes according to the player's screen
        mScreeny = screenY;
        mScreenx = screenX;
        mGreenX = screenX / 8;
        mRedX = screenX * 3 / 8;
        mYellowX = screenX * 5 / 8;
        mBlueX = screenX * 7 / 8;

        mNoteRadius = mScreenx / 8;

        float endY = screenY - 3 * mNoteRadius;
        float interval = endY - Constants.NOTE_START_Y;
        mLeakPoint = -1 * screenY / 2;

        mTouchLimit = screenY - mNoteRadius * 13 / 5;
        mSpeed = interval / scrolling_time;

        mNotes = new ArrayList<TypeNote>();
        mNotesToRemove = new ArrayList<TypeNote>();
        mGameLoopThread = gameLoopThread;
        mScore = score;
    }

    public int getNoteRadius(){
        return mNoteRadius;
    }

    public int getTouchLimit(){
        return mTouchLimit;
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

            paintGreen.setAntiAlias(true);
            paintRed.setAntiAlias(true);
            paintYellow.setAntiAlias(true);
            paintBlue.setAntiAlias(true);

            paintWhite.setColor(Color.WHITE);

            // perspective effect
            float perspectiveRatio = (note.pos.y - mLeakPoint) / (mScreeny - mLeakPoint);
            float dx = note.pos.x - mScreenx / 2;
            float newX = mScreenx / 2 + dx * perspectiveRatio;

            //switch needs constants
            if (note.pos.x == mGreenX)
                canvas.drawCircle((int) newX, note.pos.y, mNoteRadius * perspectiveRatio, paintGreen);
            if (note.pos.x == mRedX)
                canvas.drawCircle((int) newX, note.pos.y, mNoteRadius * perspectiveRatio, paintRed);
            if (note.pos.x == mYellowX)
                canvas.drawCircle((int) newX, note.pos.y, mNoteRadius * perspectiveRatio, paintYellow);
            if (note.pos.x == mBlueX)
                canvas.drawCircle((int) newX, note.pos.y, mNoteRadius * perspectiveRatio, paintBlue);

            //white fret delimiting touching area
            canvas.drawLine(mScreenx / 12, mTouchLimit, mScreenx * 11 / 12, mTouchLimit, paintWhite);
            int dt = mGameLoopThread.getTime() - note.spawnTime;
            note.pos.y = (int) (dt * mSpeed + Constants.NOTE_START_Y);
            if (note.pos.y > mScreeny) {
                mScore.missed();
                addNoteToRemove(note);
            }
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
            if (touchedPoint.y > mTouchLimit) {
                if (Math.sqrt(dx * dx + dy * dy) < mNoteRadius * 15 /10) {
                    addNoteToRemove(note);
                    return true;
                }
            }
        }
        return false;
    }
}
