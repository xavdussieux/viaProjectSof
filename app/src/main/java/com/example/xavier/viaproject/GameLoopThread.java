package com.example.xavier.viaproject;

import android.graphics.Canvas;

/**
 * Created by Xavier on 03/08/2016.
 */
public class GameLoopThread extends Thread {
    private GameView view;
    private boolean running = false;
    static final long FPS = 50;

    public GameLoopThread(GameView view) {
        this.view = view;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        long MSPT = 1000 / FPS; // milliseconds per tick
        long startTime;
        long sleepTime;
        int i = 0;
        while (running) {
            if(i % 20 == 0)
                view.addNote();
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.onDraw(c);
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            i ++;
            sleepTime = MSPT-(System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }
}