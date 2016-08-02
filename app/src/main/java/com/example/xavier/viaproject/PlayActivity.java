package com.example.xavier.viaproject;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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
        String music_name = intent.getStringExtra("music_to_play");
        Uri music_uri = Uri.parse("android.resource://com.example.xavier.viaproject/raw/" + music_name);
        mMediaPlayer = MediaPlayer.create(this, music_uri);

        // Start playing the file
        mMediaPlayer.start();
    }
}
