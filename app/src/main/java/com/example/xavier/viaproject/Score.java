package com.example.xavier.viaproject;

import android.widget.Toast;

/**
 * Created by Xavier on 05/08/2016.
 */
public class Score {

    private int mScore;
    private int mMultiplier;

    public Score() {
        mScore = 0;
        mMultiplier = 1;
    }

    public void touched () {
        mScore += Constants.TOUCH_POINTS * mMultiplier;
    }

    public void missed () {
        mScore -= Constants.MISS_POINTS;
    }

    public int getScore () {
        return mScore;
    }
}
