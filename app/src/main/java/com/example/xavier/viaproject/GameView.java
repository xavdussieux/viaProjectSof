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
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Xavier on 02/08/2016.
 */

public class GameView extends SurfaceView implements SensorEventListener {

    private float mAcc;
    private float mAccCurr;
    private float mAccLast;
    private long mLastPowerUse;
    private MediaPlayer mBoosPlayer;
    private MediaPlayer mCheersPlayer;
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
    private boolean mIsOnline = false;

    public GameView(Context context, Point screen, MediaPlayer mediaPlayer,
                    DatabaseAccess databaseAccess, SensorManager sensorManager, String musicName,
                    boolean isOnline) {
        super(context);
        init(context, screen, mediaPlayer, databaseAccess, sensorManager, musicName, isOnline);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(final Context context, Point screen, final MediaPlayer mediaPlayer,
                      DatabaseAccess databaseAccess, SensorManager sensorManager, String musicName,
                      boolean isOnline) {

        //initializing accelerometer sensor manager
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        mAcc = 0.00f;
        mAccCurr = SensorManager.GRAVITY_EARTH;
        mAccLast = mAccCurr;
        mLastPowerUse = 0;//enabling power anytime

        mCheersPlayer = MediaPlayer.create(context, Uri.parse(Constants.URI_PATH + "cheers"));
        mBoosPlayer = MediaPlayer.create(context, Uri.parse(Constants.URI_PATH + "boos"));

        mScore = new Score();
        mGameLoopThread = new GameLoopThread(this, musicName, mScore);
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
                mBoosPlayer.stop();
                mCheersPlayer.stop();
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

        mScreenSize = screen;
        mNote = new Note(mScreenSize.x, mScreenSize.y, mGameLoopThread, mScore, noteScrollingTime(context));
        mScoreBar = new ScoreBar(mScreenSize.x, mScreenSize.y, mScore);
        mDatabaseAccess = databaseAccess;
        mBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        mBackground = Bitmap.createScaledBitmap(mBackground, mScreenSize.x, mScreenSize.y, false);

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

        mIsOnline = isOnline;
    }

    private int noteScrollingTime(Context context) {
        int scrolling_time;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (sharedPreferences.getString(Constants.PREF_DIFFICULTY_KEY,
                Constants.DEFAULT_DIFFICULTY)) {
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
        return scrolling_time;
    }

    public void addNote(int color) {
        mNote.spawn(mNoteType[color]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null) {
            if (mGameLoopThread.IsRunning()) {
                updateScreen(canvas);
            } else {
                if(mScore.getPowerAccumulated() == 0){
                    endGameScreen(canvas, false);
                }else {
                    endGameScreen(canvas, true);
                }
            }
        }
    }

    public void drawBackground(Canvas canvas){
        canvas.drawBitmap(mBackground, 0, 0, new Paint());
        canvas.drawPath(mPolyPath, mBlackPoly);
        canvas.drawLine(mScreenSize.x * 267 / 640, 0, mScreenSize.x / 4, mScreenSize.y, mPaintLine);
        canvas.drawLine(mScreenSize.x / 2, 0, mScreenSize.x / 2, mScreenSize.y, mPaintLine);
        canvas.drawLine(mScreenSize.x * 373 / 640, 0, mScreenSize.x * 3 / 4, mScreenSize.y,
                mPaintLine);
    }

    public void drawButtons(Canvas canvas){
        int noteRadius = mNote.getNoteRadius();
        int touchLimit = mNote.getTouchLimit();
        CustomCircle greenButton = new CustomCircle(mScreenSize.x / 30 + noteRadius,
                touchLimit + noteRadius, noteRadius * 9 / 10,
                Color.argb(255, 0, 129, 0), 360, null, canvas);
        CustomCircle redButton = new CustomCircle(mScreenSize.x / 30 +  3 * noteRadius * 16 / 17,
                touchLimit + noteRadius, noteRadius * 9 / 10,
                Color.argb(255, 255, 0, 0), 360, null, canvas);
        CustomCircle yellowButton = new CustomCircle(mScreenSize.x * 29 / 30 - 3 * noteRadius * 16 / 17,
                touchLimit + noteRadius, noteRadius * 9 / 10,
                Color.argb(255, 255, 255, 0), 360, null, canvas);
        CustomCircle blueButton = new CustomCircle(mScreenSize.x * 29 / 30 -  noteRadius,
                touchLimit + noteRadius, noteRadius * 9 / 10,
                Color.argb(255, 0, 0, 255), 360, null, canvas);
        greenButton.draw();
        redButton.draw();
        yellowButton.draw();
        blueButton.draw();
    }

    public void updateScreen(Canvas canvas) {
        drawBackground(canvas);
        drawButtons(canvas);
        mNote.update(canvas);
        mScoreBar.update(canvas, mGameLoopThread.getSongPer());
    }


    public void endGameScreen(final Canvas canvas, boolean hasWon) {

        canvas.drawBitmap(mBackground, 0, 0, new Paint());
        int textSize = mScreenSize.y / 16;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        String headText;
        if(hasWon){
            mCheersPlayer.start();
            headText = "You ROCK!";
        }else{
            mBoosPlayer.start();
            headText = "You SUCK!";
        }

        canvas.drawText(headText, (mScreenSize.x - paint.measureText(headText)) / 2,
                textSize * 3 / 2, paint);
        String scoreText = "Your score: " + mScore.getScore();
        canvas.drawText(scoreText, 0, textSize * 9 / 2, paint);
        String perText = "% hit: " + mScore.getTouchedPer();
        canvas.drawText(perText, 0, textSize * 6, paint);
        String streakText = "Note streak: " + mScore.getBestStreak();
        canvas.drawText(streakText, 0, textSize * 15 / 2, paint);

        if(hasWon && mIsOnline) {
            mDatabaseAccess.storeRecord(mScore.getScore());
            mDatabaseAccess.rank(mScore.getScore());
            String rank = mDatabaseAccess.getRank();
            if(!rank.equals(Constants.DEFAULT_RANK)) {
                mUpdating = false;
            }
            canvas.drawText("Your rank: " + rank, 0, textSize * 9, paint);
        }
        mUpdating = false;//stop game view updating
    }

    public boolean needUpdate() {
        return mUpdating;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int actionMask = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        Point point;
        switch (actionMask) {
            case MotionEvent.ACTION_DOWN:
                //single touch case
                point = new Point((int) event.getX(), (int) event.getY());
                if (mNote.onNoteWithDel(point)) mScore.touched();
                else mScore.missed();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                //multi touch case
                point = new Point((int) event.getX(pointerIndex), (int) event.getY(pointerIndex));
                if (mNote.onNoteWithDel(point)) mScore.touched();
                else mScore.missed();
                break;
        }
        return true;
    }


    public Score getScore() {
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
            mScore.setPowerAccumulated((int) (100 - ((diff * 100 / Constants.POWER_DURATION) / 2)));
            //back to 50% of power
        }else{
            //if activated (by shaking) while star power is at 100%
            if(mAcc > Constants.POWER_ACCELERATION && mScore.getPowerAccumulated() == 100){
                this.getScore().setPowerOn(true);
                mCheersPlayer.start();
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