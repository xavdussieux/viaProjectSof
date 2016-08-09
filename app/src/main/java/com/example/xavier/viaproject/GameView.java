package com.example.xavier.viaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Xavier on 02/08/2016.
 */

public class GameView extends SurfaceView implements SensorEventListener{

    private SensorManager mSensorManager;
    private float mAcc;
    private float mAccCurr;
    private float mAccLast;
    private long mLastPowerUse;


    private GameLoopThread mGameLoopThread;
    private Note mNote;
    private ScoreBar mScoreBar;
    private Score mScore;
    private String mNoteType[] = {"green", "red", "yellow", "blue"};
    private Point mScreenSize;
    private DatabaseAccess mDatabaseAccess;

    public GameView(Context context, int screenx, int screeny, MediaPlayer mediaPlayer, DatabaseAccess databaseAccess, SensorManager sensorManager) {
        super(context);
        init(context, screenx, screeny, mediaPlayer, databaseAccess, sensorManager);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(final Context context, int screenx, int screeny, final MediaPlayer mediaPlayer, DatabaseAccess databaseAccess, SensorManager sensorManager) {
        //initializing accelerometer sensor manager
        mSensorManager = sensorManager;
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        mAcc = 0.00f;
        mAccCurr = SensorManager.GRAVITY_EARTH;
        mAccLast = mAccCurr;
        mLastPowerUse = 0;//enabling power anytime


        mGameLoopThread = new GameLoopThread(this);
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                mGameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        mGameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mGameLoopThread.setRunning(true);
                mGameLoopThread.initMusic(context, mediaPlayer, noteScrollingTime(context));
                mGameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // Nothing to be done
            }
        });
        mScore = new Score();
        mNote = new Note(context, screenx, screeny, mGameLoopThread, mScore, noteScrollingTime(context));
        mScoreBar = new ScoreBar(context, screenx, screeny, mScore);
        mScreenSize = new Point(screenx, screeny);
        mDatabaseAccess = databaseAccess;
    }

    private int noteScrollingTime (Context context) {
        int scrolling_time = 0;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (sharedPreferences.getString(Constants.PREF_DIFFICULTY_KEY, Constants.DEFAULT_DIFFICULTY)) {
            case "easy":
                scrolling_time = Constants.NOTE_SCROLLING_TIME_EASY;
                break;
            case "intermediate":
                scrolling_time = Constants.NOTE_SCROLLING_TIME_INTERMEDIATE;
                break;
            case "hard":
                scrolling_time = Constants.NOTE_SCROLLING_TIME_HARD;
                break;
            default:
                scrolling_time = Constants.NOTE_SCROLLING_TIME_EASY;
                break;
        }
        return  scrolling_time;
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
                updateScreen(canvas);
            }
            else {
                endGameScreen(canvas);
            }
        }
    }

    public void updateScreen (Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        mNote.update(canvas);
        mScoreBar.update(canvas);
    }

    public void endGameScreen(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(mScreenSize.y / 16);
        String scoreText = "Your score: " + mScore.getScore();
        canvas.drawText(scoreText, (mScreenSize.x - paint.measureText(scoreText)) / 2, mScreenSize.y / 2 - mScreenSize.y / 32, paint);
        mDatabaseAccess.storeRecord(mScore.getScore());
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

    public Score getScore(){
        return mScore;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        mAccLast = mAccCurr;
        mAccCurr = (float) Math.sqrt((double) (x * x) + (y * y) + (z * z));
        float delta = mAccCurr - mAccLast;
        mAcc = mAcc * 0.9f + delta;

        //Power value has to be locked for a given duration
        long timeMillis = System.currentTimeMillis();
        if((timeMillis - mLastPowerUse) < Constants.POWER_DURATION){
            //case where power has to be locked: do nothing
        }else{
            //if activated (by shaking)
            if(mAcc > Constants.POWER_ACCELERATION){
                this.getScore().setPowerMultiplier(Constants.POWER_MULTIPLIER);
                mLastPowerUse = timeMillis;
                Toast.makeText(this.getContext(), "Strings On Fire!", Toast.LENGTH_SHORT).show();
            }else{
                this.getScore().setPowerMultiplier(Constants.DEFAULT_POWER_MULTIPLIER);
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do nothing
    }
}