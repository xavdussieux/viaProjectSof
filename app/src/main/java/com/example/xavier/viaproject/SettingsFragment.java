package com.example.xavier.viaproject;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Xavier on 30/07/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}