package com.example.xavier.viaproject;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by Xavier on 01/08/2016.
 */
public class PlayActivity extends Activity{
    private Animation translateAnimation;

    private static final String URI_PATH = "android.resource://com.example.xavier.viaproject/raw/";

    private MediaPlayer mMediaPlayer;
    private int mSongPer;

    /* trying to refactor the code
    public void scrollNote(int note_id){
        //choosing right note view
        ImageView note_img;
        switch(note_id){
            case 1:
                note_img = (ImageView) findViewById(R.id.note_target1);
                break;
            case 2:
                note_img = (ImageView) findViewById(R.id.note_target2);
                break;
            case 3:
                note_img = (ImageView) findViewById(R.id.note_target3);
                break;
            case 4:
                note_img = (ImageView) findViewById(R.id.note_target4);
                break;
            default:
                //solve bad_id
                note_img = (ImageView) findViewById(R.id.note_target1);
                break;
        }
        //launching animation
        translateAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 1f);
        translateAnimation.setDuration(8000);
        translateAnimation.setInterpolator(new LinearInterpolator());
        note_img.startAnimation(translateAnimation);
    }
       */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //basic initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        //setup music
        Intent intent = getIntent();
        String music_name = intent.getStringExtra("music_to_play");
        Uri music_uri = Uri.parse(URI_PATH + music_name);
        mMediaPlayer = MediaPlayer.create(this, music_uri);
        //Start playing the file
        mSongPer = mMediaPlayer.getCurrentPosition();
        mMediaPlayer.start();

        //setup scrolling notes
        translateAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 3f);
        translateAnimation.setDuration(8000);
        translateAnimation.setInterpolator(new LinearInterpolator());
        ImageView note_img;
        note_img = (ImageView) findViewById(R.id.note_target1);
        note_img.startAnimation(translateAnimation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshing song percentage
        mSongPer = mMediaPlayer.getCurrentPosition() * 100 / mMediaPlayer.getDuration();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayer.stop();
    }


}