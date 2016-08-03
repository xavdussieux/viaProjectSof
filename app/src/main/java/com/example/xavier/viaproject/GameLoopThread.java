package com.example.xavier.viaproject;

import android.graphics.Canvas;

/**
 * Created by Xavier on 03/08/2016.
 */
public class GameLoopThread extends Thread {

    private GameView mGameView;
    private boolean mIsRunning = false;
    static final long FPS = 50;

    public GameLoopThread(GameView view) {
        this.mGameView = view;
    }

    public void setRunning(boolean run) {
        mIsRunning = run;
    }

    @Override
    public void run() {
        long MSPT = 1000 / FPS; // milliseconds per tick
        long startTime;
        long sleepTime;
        int i = 0;
        while (mIsRunning) {
            if(i % 20 == 0)
                mGameView.addNote();
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = mGameView.getHolder().lockCanvas();
                synchronized (mGameView.getHolder()) {
                    mGameView.onDraw(c);
                }
            } finally {
                if (c != null) {
                    mGameView.getHolder().unlockCanvasAndPost(c);
                }
            }
            i++;
            sleepTime = MSPT - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }
}