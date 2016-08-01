package com.example.xavier.viaproject;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

/**
 * Created by Xavier on 01/08/2016.
 */
public class PlayActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        MediaPlayer mMediaPlayer = MediaPlayer.create(this, R.raw.rhcp);
        // Start playing the file
        mMediaPlayer.start();
    }
}
