package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

/**
 * Created by Xavier on 02/08/2016.
 */

public class GameView extends SurfaceView {

    private SurfaceHolder mSurfaceHolder;
    private GameLoopThread mGameLoopThread;
    private Note mNote;
    private String mNoteType[] = {"green", "red", "yellow", "blue"};

    public GameView(Context context, int screenx, int screeny) {
        super(context);
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
        }
    }

    protected boolean onNote(Point touchedPoint) {
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
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            Point point = new Point((int) event.getX(), (int) event.getY());
            if (onNote(point))
                Toast.makeText(this.getContext(), "Touché", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this.getContext(), "Pas touché", Toast.LENGTH_SHORT).show();
        }
        return super.onTouchEvent(event);
    }
}