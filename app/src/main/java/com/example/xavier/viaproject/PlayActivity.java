package com.example.xavier.viaproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

/**
 * Created by Xavier on 01/08/2016.
 */
public class PlayActivity extends Activity{

    private SensorManager mSensorManager;
    GameView gameView;
    MediaPlayer mMediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic initialization
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Intent intent = getIntent();
        String music_name = intent.getStringExtra("music_to_play");
        Uri music_uri = Uri.parse(Constants.URI_PATH + music_name);
        mMediaPlayer = MediaPlayer.create(this, music_uri);

        DatabaseAccess databaseAccess = new DatabaseAccess(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gameView = new GameView(this, size.x, size.y, mMediaPlayer, databaseAccess, mSensorManager);
        setContentView(gameView);



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.pause();
        mSensorManager.unregisterListener(gameView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayer.stop();
        mSensorManager.unregisterListener(gameView);
    }
}