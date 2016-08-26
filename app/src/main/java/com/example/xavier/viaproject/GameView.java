package com.example.xavier.viaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Xavier on 02/08/2016.
 */

public class GameView extends SurfaceView implements SensorEventListener{

    private float mAcc;
    private float mAccCurr;
    private float mAccLast;
    private long mLastPowerUse;
    private Bitmap mBackground;
    private Path mPolyPath;
    private Paint mBlackPoly;
    private Paint mPaintLine;

    private GameLoopThread mGameLoopThread;
    private Note mNote;
    private ScoreBar mScoreBar;
    private Score mScore;
    private String mNoteType[] = {"green", "red", "yellow", "blue"};
    private Point mScreenSize;
    private DatabaseAccess mDatabaseAccess;
    private boolean mUpdating = true;

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

        //initializing accelerometer sensor manager
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        mAcc = 0.00f;
        mAccCurr = SensorManager.GRAVITY_EARTH;
        mAccLast = mAccCurr;
        mLastPowerUse = 0;//enabling power anytime


        mGameLoopThread = new GameLoopThread(this, musicName);
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
        mNote = new Note(screenx, screeny, mGameLoopThread, mScore, noteScrollingTime(context));
        mScoreBar = new ScoreBar(screenx, screeny, mScore);
        mScreenSize = new Point(screenx, screeny);
        mDatabaseAccess = databaseAccess;
        mBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        mBackground = Bitmap.createScaledBitmap(mBackground, screenx, screeny, false);

        mBlackPoly= new Paint();
        mBlackPoly.setColor(Color.BLACK);
        mBlackPoly.setStyle(Paint.Style.FILL);
        mBlackPoly.setAntiAlias(true);
        mPolyPath = new Path();
        mPolyPath.moveTo(mScreenSize.x / 3, 0);
        mPolyPath.lineTo(mScreenSize.x * 2 / 3, 0);
        mPolyPath.lineTo(mScreenSize.x, mScreenSize.y);
        mPolyPath.lineTo(0, mScreenSize.y);
        mPolyPath.lineTo(mScreenSize.x / 3, 0);

        mPaintLine = new Paint();
        mPaintLine.setColor(Color.WHITE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(mScreenSize.x / 100);

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
        canvas.drawBitmap(mBackground,0,0,new Paint());
        canvas.drawPath(mPolyPath, mBlackPoly);
        canvas.drawLine(mScreenSize.x  * 267 / 640, 0, mScreenSize.x / 4, mScreenSize.y, mPaintLine);
        canvas.drawLine(mScreenSize.x  / 2, 0, mScreenSize.x  / 2, mScreenSize.y, mPaintLine);
        canvas.drawLine(mScreenSize.x  * 373 / 640, 0, mScreenSize.x * 3 / 4, mScreenSize.y, mPaintLine);
        mNote.update(canvas);
        mScoreBar.update(canvas, mGameLoopThread.getSongPer());
    }

    public void endGameScreen(final Canvas canvas) {

        canvas.drawBitmap(mBackground,0,0,new Paint());
        int textSize = mScreenSize.y / 16;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
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

        int actionMask = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        Point point;
        switch (actionMask) {
            case MotionEvent.ACTION_DOWN:
                point = new Point((int) event.getX(), (int) event.getY());
                if (mNote.onNoteWithDel(point)) mScore.touched();
                else mScore.missed();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                point = new Point((int) event.getX(pointerIndex), (int) event.getY(pointerIndex));
                if (mNote.onNoteWithDel(point)) mScore.touched();
                else mScore.missed();
                break;
        }
        return true;
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