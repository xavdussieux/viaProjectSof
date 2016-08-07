package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;

/**
 * Created by Xavier on 03/08/2016.
 */
public class GameLoopThread extends Thread {

    private GameView mGameView;
    private boolean mIsRunning = false;
    private MediaPlayer mMediaPlayer;
    private long mInitTime;
    private long mCurrTime;
    private MusicTrack mMusicTrack;

    public GameLoopThread(GameView view) {
        this.mGameView = view;
    }

    public void setRunning(boolean run) {
        mIsRunning = run;
    }

    public void initMusic (Context context, MediaPlayer mediaPlayer) {
        mMusicTrack = new MusicTrack(context, mGameView);
        mMediaPlayer = mediaPlayer;
    }

    public int getTime () {
        return (int) (mCurrTime - mInitTime);
    }

    @Override
    public void run() {
        long mspt = 1000 / Constants.FPS; // milliseconds per tick
        long sleepTime;
        Canvas c;
        int i = 0;

        mInitTime = System.currentTimeMillis();

        while (mIsRunning) {
            c = null;
            mCurrTime = System.currentTimeMillis();
            if (!mMediaPlayer.isPlaying()) {
                if (getTime() > Constants.NOTE_SCROLLING_TIME)
                    mMediaPlayer.start();
            }
            mMusicTrack.update(mCurrTime - mInitTime);
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
            sleepTime = mspt - (System.currentTimeMillis() - mCurrTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }
}