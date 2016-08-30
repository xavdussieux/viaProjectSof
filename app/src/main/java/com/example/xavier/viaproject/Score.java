package com.example.xavier.viaproject;

/**
 * Created by Xavier on 05/08/2016.
 */
public class Score {

    private int mScore;
    private int mMultiplier;
    private int mPowerMultiplier;
    private float mPowerAccumulated; //in percentage
    private int mHitStreak;
    private int mBestStreak;
    private int mNoteNum;
    private boolean mIsPowerOn;
    private int mGoodNum;

    public Score() {
        mScore = 0;
        mHitStreak = 0;
        mMultiplier = 1;
        mPowerMultiplier = 1;
        mPowerAccumulated = 50;
        mIsPowerOn = false;
        mBestStreak = 0;
        mNoteNum = 0;
        mGoodNum = 0;
    }

    public void touched () {
        mHitStreak++;
        mNoteNum++;
        mGoodNum++;
        if(mHitStreak < Constants.MULTIPLIER_STEP_X2){
            mMultiplier = 1;
        } else{
            if(mHitStreak < Constants.MULTIPLIER_STEP_X3) {
                mMultiplier = 2;
            } else {
                if(mHitStreak < Constants.MULTIPLIER_STEP_X4) {
                    mMultiplier = 3;
                } else {
                    mMultiplier = 4;
                }
            }
        }
        mScore += Constants.TOUCH_POINTS * mMultiplier * mPowerMultiplier;
        float powerUp = (float) (mMultiplier * Constants.TOUCH_POINTS) / 40;
        mPowerAccumulated = Math.min(mPowerAccumulated + powerUp, 100);
    }

    public int getBestStreak(){
        if(mBestStreak == 0){
            //no note missed
            return mNoteNum;
        }
        return mBestStreak;
    }

    public int getTouchedPer(){
        //rounding down
        return mGoodNum * 100 / mNoteNum;
    }

    public void missed () {
        if(mHitStreak > mBestStreak){
            mBestStreak = mHitStreak;
        }
        mHitStreak = 0;
        mMultiplier = 0;
        mNoteNum++;
        mScore -= Constants.MISS_POINTS;
        float powerDown = (float) (Constants.TOUCH_POINTS) / 10;
        mPowerAccumulated = Math.max(mPowerAccumulated - powerDown, 0);

    }

    public void setPowerOn(boolean isPowerOn){
        if(isPowerOn) {
            mPowerMultiplier = Constants.POWER_MULTIPLIER;
            mIsPowerOn = true;
        }else{
            mPowerMultiplier = Constants.DEFAULT_POWER_MULTIPLIER;
            mIsPowerOn = false;
        }
    }

    public boolean getIsPowerOn(){
        return mIsPowerOn;
    }

    public int getPowerAccumulated(){
        return (int)mPowerAccumulated;
    }

    public void setPowerAccumulated(int power){
        //power accumulated should remain between 0 and 100
        if(power < 0) {
            mPowerAccumulated = 0;
        }else{
            if(power < 100){
                mPowerAccumulated = power;
            }else{
                mPowerAccumulated = 100;
            }
        }
    }

    public int getMultiplier(){
        return mMultiplier;
    }

    public int getScore () {
        return mScore;
    }

}
