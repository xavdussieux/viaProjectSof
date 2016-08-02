package com.example.xavier.viaproject;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

/**
 * Created by Xavier on 01/08/2016.
 */
public class PlayActivity extends Activity{

    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        Intent intent = getIntent();
        String music_to_play = intent.getStringExtra("music_to_play");
        switch (music_to_play) {
            case "rhcp":
                mMediaPlayer = MediaPlayer.create(this, R.raw.rhcp);
                break;
            case "passenger_let_her_go":
                mMediaPlayer = MediaPlayer.create(this, R.raw.passenger_let_her_go);
                break;
        }
        // Start playing the file
        mMediaPlayer.start();
    }
}
