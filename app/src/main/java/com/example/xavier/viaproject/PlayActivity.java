package com.example.xavier.viaproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;

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
        gameView = new GameView(this, size, mMediaPlayer, databaseAccess, mSensorManager,
                music_name, isOnline());
        setContentView(gameView);



    }

    //internet availability check
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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