package com.example.xavier.viaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
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
    private Bitmap mBackground;

    private GameLoopThread mGameLoopThread;
    private Note mNote;
    private ScoreBar mScoreBar;
    private Score mScore;
    private String mNoteType[] = {"green", "red", "yellow", "blue"};
    private Point mScreenSize;
    private DatabaseAccess mDatabaseAccess;
    private boolean mUpdating = true;
    private String mMusicName;

    public GameView(Context context, int screenx, int screeny, MediaPlayer mediaPlayer, DatabaseAccess databaseAccess, SensorManager sensorManager, String musicName) {
        super(context);
        init(context, screenx, screeny, mediaPlayer, databaseAccess, sensorManager, musicName);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(final Context context, int screenx, int screeny, final MediaPlayer mediaPlayer, DatabaseAccess databaseAccess, SensorManager sensorManager, String musicName) {
        mMusicName = musicName;

        //initializing accelerometer sensor manager
        mSensorManager = sensorManager;
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        mAcc = 0.00f;
        mAccCurr = SensorManager.GRAVITY_EARTH;
        mAccLast = mAccCurr;
        mLastPowerUse = 0;//enabling power anytime


        mGameLoopThread = new GameLoopThread(this, mMusicName);
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
        mBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        mBackground = Bitmap.createScaledBitmap(mBackground, screenx, screeny, false);

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
        Paint paintBackground = new Paint();
        canvas.drawBitmap(mBackground,0,0,paintBackground);
        mNote.update(canvas);
        mScoreBar.update(canvas);
    }

    public void endGameScreen(final Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        int textSize = mScreenSize.y / 16;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        String headText = "Stats";
        canvas.drawText(headText, (mScreenSize.x - paint.measureText(headText)) / 2, textSize * 3 / 2, paint);
        String scoreText = "Your score: " + mScore.getScore();
        canvas.drawText(scoreText, 0, textSize * 9 / 2, paint);
        String perText = "% hit: " + mScore.getTouchedPer();
        canvas.drawText(perText, 0, textSize * 6, paint);
        String streakText = "Note streak: " + mScore.getBestStreak();
        canvas.drawText(streakText, 0, textSize * 15 / 2, paint);
        mDatabaseAccess.storeRecord(mScore.getScore());
        mDatabaseAccess.rank(mScore.getScore());
        String rank = mDatabaseAccess.getRank();
        if(!rank.equals(Constants.DEFAULT_RANK))
            mUpdating = false;
        String rankText = "Your rank: " + rank;
        canvas.drawText(rankText, 0, textSize * 9, paint);
    }

    public boolean updating() {
        return mUpdating;
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
        long diff = timeMillis - mLastPowerUse;
        if(diff <= Constants.POWER_DURATION){
            //case where power has to be locked: decrease power accumulated
            mScore.setPowerAccumulated((int) (100 - (diff * 100 / Constants.POWER_DURATION)));
        }else{
            //if activated (by shaking)
            if(mAcc > Constants.POWER_ACCELERATION && mScore.getPowerAccumulated() == 100){
                this.getScore().setPowerOn(true);
                mLastPowerUse = timeMillis;
                Toast.makeText(this.getContext(), "Strings On Fire!", Toast.LENGTH_SHORT).show();
            }else{
                this.getScore().setPowerOn(false);
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do nothing
    }
}