package com.example.xavier.viaproject;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Xavier on 30/07/2016.
 */

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}