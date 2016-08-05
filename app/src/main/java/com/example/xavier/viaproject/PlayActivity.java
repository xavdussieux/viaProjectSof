package com.example.xavier.viaproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;

/**
 * Created by Xavier on 01/08/2016.
 */
public class PlayActivity extends Activity{

    private int mSongPer;

    GameView gameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic initialization
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //setup music
        Intent intent = getIntent();
        String music_name = intent.getStringExtra("music_to_play");
        Uri music_uri = Uri.parse(Constants.URI_PATH + music_name);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, music_uri);

        gameView = new GameView(this, size.x, size.y, mediaPlayer);
        setContentView(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshing song percentage
        //mSongPer = mMediaPlayer.getCurrentPosition() * 100 / mMediaPlayer.getDuration();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mMediaPlayer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mMediaPlayer.stop();
    }
}