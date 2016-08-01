package com.example.xavier.viaproject;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Xavier on 01/08/2016.
 */
public class ListPreference extends android.preference.ListPreference {

    public ListPreference (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        setSummary(getSummary());
    }

    @Override
    public String getSummary() {
        if(this.getEntry() != null)
            return this.getEntry().toString();
        else
            return "New entry";
    }
}
