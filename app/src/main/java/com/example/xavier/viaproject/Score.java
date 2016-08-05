package com.example.xavier.viaproject;

import android.widget.Toast;

/**
 * Created by Xavier on 05/08/2016.
 */
public class Score {

    private int mScore;
    private int mMultiplier;
    private int mPowerMultiplier;
    private int mHitStreak;

    public Score() {
        mScore = 0;
        mHitStreak = 0;
        mMultiplier = 1;
        mPowerMultiplier = 1;
    }

    public void touched () {
        mHitStreak++;
        if(mHitStreak < Constants.MULTIPLIER_STEP_X2){
            mMultiplier = 1;
        } else{
            if(mHitStreak < Constants.MULTIPLIER_STEP_X4) {
                mMultiplier = 2;
            } else {
                if(mHitStreak < Constants.MULTIPLIER_STEP_X8) {
                    mMultiplier = 4;
                } else {
                    mMultiplier = 8;
                }
            }
        }
        mScore += Constants.TOUCH_POINTS * mMultiplier * mPowerMultiplier;
    }

    public void missed () {
        mHitStreak = 0;
        mMultiplier = 0;
        mScore -= Constants.MISS_POINTS;
    }

    public void setPowerMultiplier(int powerMultiplier){
        mPowerMultiplier = powerMultiplier;
    }

    public int getmMultiplier(){
        return mMultiplier;
    }

    public int getScore () {
        return mScore;
    }
}
