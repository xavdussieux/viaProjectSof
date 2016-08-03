package com.example.xavier.viaproject;

import android.graphics.Canvas;

/**
 * Created by Xavier on 03/08/2016.
 */
public class GameLoopThread extends Thread {
    private GameView view;
    private boolean running = false;

    public GameLoopThread(GameView view) {
        this.view = view;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        int i = 0;
        while (running) {
            if(i % 20 == 0)
                view.addNote();
            Canvas c = null;
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
        }
    }
}