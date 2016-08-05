package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

/**
 * Created by Xavier on 02/08/2016.
 */

public class GameView extends SurfaceView {

    // TODO add constants in constant class
    private static final int TOUCH_POINTS = 10;
    private static final int MISS_POINTS = 5;
    private SurfaceHolder mSurfaceHolder;
    private GameLoopThread mGameLoopThread;
    private Note mNote;
    private ScoreBar mScoreBar;
    private int mScore;
    private String mNoteType[] = {"green", "red", "yellow", "blue"};

    public GameView(Context context, int screenx, int screeny) {
        super(context);
        init(context, screenx, screeny);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(Context context, int screenx, int screeny) {
        mScore = 0;
        mGameLoopThread = new GameLoopThread(this);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                mGameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        mGameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mGameLoopThread.setRunning(true);
                mGameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // Nothing to be done
            }
        });
        mNote = new Note(context, screenx, screeny);
        mScoreBar = new ScoreBar(context, screenx, screeny);
    }

    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public void addNote() {
        mNote.spawn(getRandom(mNoteType));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(canvas != null) {
            canvas.drawColor(Color.BLACK);
            mNote.update(canvas);
            mScoreBar.update(canvas, mScore);
        }
    }

    protected boolean onNote(Point touchedPoint) {
        // TODO rename method -> onNoteWithDel
        // TODO point counting should not be here
        List<Point> noteList = mNote.getNotes();
        int dx, dy, noteSize;
        Point del = null;
        for(Point p : noteList) {
            dx = touchedPoint.x - p.x;
            dy = touchedPoint.y - p.y;
            noteSize = mNote.getNoteSize();
            if(dx > 0 && dx <  noteSize)
                if (dy > 0 && dy < noteSize){
                    mNote.addNoteToRemove(p);
                    mScore += TOUCH_POINTS;
                    return true;
                }
        }
        mScore -= MISS_POINTS;
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            Point point = new Point((int) event.getX(), (int) event.getY());
            onNote(point);
            //if (onNote(point))
                //Toast.makeText(this.getContext(), "Touched : " + mScore + "pts", Toast.LENGTH_SHORT).show();
            //else
                //Toast.makeText(this.getContext(), "Missed : " + mScore + "pts", Toast.LENGTH_SHORT).show();
        }
        return super.onTouchEvent(event);
    }
}