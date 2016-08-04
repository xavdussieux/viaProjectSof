package com.example.xavier.viaproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME_KEY = "pref_key_name";
    private static final String PREF_MUSIC_KEY = "pref_key_music";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        DatabaseAccess databaseAccess = new DatabaseAccess(this);
    }

    @Override
    protected  void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString(PREF_NAME_KEY, "New player");
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
        String music = sharedPreferences.getString(PREF_MUSIC_KEY, "rhcp");
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("music_to_play", music);
        startActivity(intent);
    }

    public void resume(View view) {

    }
}
