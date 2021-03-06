package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;

/**
 * Created by Xavier on 03/08/2016
 */
public class GameLoopThread extends Thread {

    private GameView mGameView;
    private boolean mIsRunning = false;
    private MediaPlayer mMediaPlayer;
    private Score mScore;
    private long mInitTime;
    private long mCurrTime;
    private MusicTrack mMusicTrack;
    private int mDuration;
    private int mOffsetStartMusic;
    private String mMusicName;

    public GameLoopThread(GameView view, String musicName, Score score) {
        this.mGameView = view;
        mScore = score;
        mMusicName = musicName;
    }

    public void setRunning(boolean run) {
        mIsRunning = run;
    }

    public boolean IsRunning () {
        return mIsRunning;
    }

    public void initMusic (Context context, MediaPlayer mediaPlayer, int scrolling_time) {
        mMusicTrack = new MusicTrack(context, mGameView, mMusicName);
        mMediaPlayer = mediaPlayer;
        mDuration = mediaPlayer.getDuration() + scrolling_time + 100;
        mOffsetStartMusic = scrolling_time;
    }

    public int getTime () {
        return (int) (mCurrTime - mInitTime);
    }

    public int getSongPer(){
        return mMediaPlayer.getCurrentPosition() * 100 / mMediaPlayer.getDuration();
    }

    @Override
    public void run() {
        long mspt = 1000 / Constants.FPS; // milliseconds per tick
        long sleepTime;
        Canvas c;

        mInitTime = System.currentTimeMillis();

        while (mIsRunning && mScore.getPowerAccumulated() > 0) {
            c = null;
            mCurrTime = System.currentTimeMillis();
            if (!mMediaPlayer.isPlaying()) {
                if (getTime() > mOffsetStartMusic)
                    mMediaPlayer.start();
            }

            mMusicTrack.update(mCurrTime - mInitTime);
            syncAndDraw(mGameView, c);

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

        if(mScore != null && mScore.getPowerAccumulated() == 0){
            onStop();
        }
    }

    public void syncAndDraw(GameView gameView, Canvas canvas){
        try {
            canvas = gameView.getHolder().lockCanvas();
            synchronized (gameView.getHolder()) {
                gameView.onDraw(canvas);
            }
        } finally {
            if (canvas != null) {
                gameView.getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    public void onStop() {
        mMediaPlayer.stop();
        mIsRunning = false;
        Canvas c = null;
        while (mGameView.needUpdate()) {
            syncAndDraw(mGameView, c);
            try {
                sleep(1000);
            } catch (Exception e) {}
        }
    }
}