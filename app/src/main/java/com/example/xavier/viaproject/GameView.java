package com.example.xavier.viaproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by Xavier on 02/08/2016.
 */

public class GameView extends SurfaceView {

    private SurfaceHolder mSurfaceHolder;
    private GameLoopThread mGameLoopThread;
    private Note mNote;
    private ScoreBar mScoreBar;
    private Score mScore;
    private String mNoteType[] = {"green", "red", "yellow", "blue"};
    private Point mScreenSize;

    public GameView(Context context, int screenx, int screeny, MediaPlayer mediaPlayer) {
        super(context);
        init(context, screenx, screeny, mediaPlayer);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(final Context context, int screenx, int screeny, final MediaPlayer mediaPlayer) {
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
                mGameLoopThread.initMusic(context, mediaPlayer);
                mGameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // Nothing to be done
            }
        });
        mScore = new Score();
        mNote = new Note(context, screenx, screeny, mGameLoopThread, mScore);
        mScoreBar = new ScoreBar(context, screenx, screeny, mScore);
        mScreenSize = new Point(screenx, screeny);
    }

    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public void addNote(int color) {
        mNote.spawn(mNoteType[color]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(canvas != null) {
            if(mGameLoopThread.IsRunning()) {
                canvas.drawColor(Color.BLACK);
                mNote.update(canvas);
                mScoreBar.update(canvas);
            }
            else {
                canvas.drawColor(Color.BLACK);
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setTextSize(mScreenSize.y / 16);
                String scoreText = "Your score: " + mScore.getScore();
                canvas.drawText(scoreText, (mScreenSize.x - paint.measureText(scoreText)) / 2, mScreenSize.y / 2 - mScreenSize.y / 32, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            Point point = new Point((int) event.getX(), (int) event.getY());
            if (mNote.onNoteWithDel(point))
                mScore.touched();
            else
                mScore.missed();
        }
        return super.onTouchEvent(event);
    }
}