package com.example.xavier.viaproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.titleText);
        textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/sixty.ttf"));

        Intent intent = new Intent(this, NotificationService.class);
        this.startService(intent);

        init();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        String username = mSharedPreferences.getString(Constants.PREF_NAME_KEY, Constants.DEFAULT_NAME);
        TextView textView = (TextView) findViewById(R.id.textName);
        String userText = "Player : " + username;
        textView.setText(userText);
    }

    @Override
    protected void onStop () {
        super.onStop();
    }

    public void showSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void play(View view) {
        String music = mSharedPreferences.getString(Constants.PREF_MUSIC_KEY, Constants.DEFAULT_MUSIC);
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("music_to_play", music);
        startActivity(intent);
    }

    public void showScores (View view) {
        Intent intent = new Intent(this, ShowScoreActivity.class);
        startActivity(intent);
    }

    public void init () {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ImageButton exitButton = (ImageButton) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        DatabaseAccess databaseAccess = new DatabaseAccess(this);
        databaseAccess.launchCount();
    }
}
