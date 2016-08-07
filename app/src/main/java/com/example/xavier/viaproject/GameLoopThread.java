package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;

/**
 * Created by Xavier on 03/08/2016.
 */
public class GameLoopThread extends Thread {

    private GameView mGameView;
    private Object mPauseLock;
    private boolean mIsRunning = false;
    private boolean mPaused;
    private MediaPlayer mMediaPlayer;
    private long mInitTime;
    private long mCurrTime;
    private MusicTrack mMusicTrack;
    private int mDuration;
    private int mOffsetStartMusic;

    public GameLoopThread(GameView view) {
        this.mGameView = view;
        mPauseLock = new Object();
        mPaused = false;
    }

    public void setRunning(boolean run) {
        mIsRunning = run;
    }

    public boolean IsRunning () {
        return mIsRunning;
    }

    public void initMusic (Context context, MediaPlayer mediaPlayer, int scrolling_time) {
        mMusicTrack = new MusicTrack(context, mGameView);
        mMediaPlayer = mediaPlayer;
        mDuration = mediaPlayer.getDuration() + scrolling_time + 100;
        mOffsetStartMusic = scrolling_time;
    }

    public int getTime () {
        return (int) (mCurrTime - mInitTime);
    }

    @Override
    public void run() {
        long mspt = 1000 / Constants.FPS; // milliseconds per tick
        long sleepTime;
        Canvas c;

        mInitTime = System.currentTimeMillis();

        while (mIsRunning) {
            c = null;
            mCurrTime = System.currentTimeMillis();
            if (!mMediaPlayer.isPlaying()) {
                if (getTime() > mOffsetStartMusic)
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
            synchronized (mPauseLock) {
                while (mPaused) {
                    try {
                        mPauseLock.wait();
                    } catch (InterruptedException e) {

                    }
                }
            }

            if(getTime() > mDuration) {
                onStop();
            }

            sleepTime = mspt - (System.currentTimeMillis() - mCurrTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }

    public void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }

    public void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }

    public void onStop() {
        mIsRunning = false;
        Canvas c = null;
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
    }
}