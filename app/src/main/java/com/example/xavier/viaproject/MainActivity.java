package com.example.xavier.viaproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        String username = mSharedPreferences.getString(Constants.PREF_NAME_KEY, "New player");
        TextView textView = (TextView) findViewById(R.id.textName);
        textView.setText("Player : " + username);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String music = sharedPreferences.getString(Constants.PREF_MUSIC_KEY, "gotc");
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("music_to_play", music);
        startActivity(intent);
    }

    public void showScores (View view) {
        Intent intent = new Intent(this, ShowScoreActivity.class);
        startActivity(intent);
    }

    public void resume(View view) {

    }

    public void init () {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        DatabaseAccess databaseAccess = new DatabaseAccess(this, mSharedPreferences);
        databaseAccess.launchCount();
    }
}
