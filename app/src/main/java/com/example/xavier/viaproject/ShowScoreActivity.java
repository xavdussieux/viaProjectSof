package com.example.xavier.viaproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 07/08/2016.
 */
public class ShowScoreActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_score);

        initRecords();
    }

    public void initRecords() {

        DatabaseAccess databaseAccess = new DatabaseAccess(this);
        List<Record> leaderboard = new ArrayList<Record>();
        databaseAccess.leaderboard(leaderboard, new DatabaseAccess.leaderboardCallback() {
                    @Override
                    public void callback(final List<Record> leaderboardList) {
                        TextView textView = null;
                        try {
                            textView = (TextView) findViewById(R.id.bestScore1);
                            textView.setText("1 : " + leaderboardList.get(0).name);
                            textView = (TextView) findViewById(R.id.bestScoreV1);
                            textView.setText(Integer.toString(leaderboardList.get(0).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            textView = (TextView) findViewById(R.id.bestScore2);
                            textView.setText("2 : " + leaderboardList.get(1).name);
                            textView = (TextView) findViewById(R.id.bestScoreV2);
                            textView.setText(Integer.toString(leaderboardList.get(1).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            textView = (TextView) findViewById(R.id.bestScore3);
                            textView.setText("3 : " + leaderboardList.get(2).name);
                            textView = (TextView) findViewById(R.id.bestScoreV3);
                            textView.setText(Integer.toString(leaderboardList.get(2).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            textView = (TextView) findViewById(R.id.bestScore4);
                            textView.setText("4 : " + leaderboardList.get(3).name);
                            textView = (TextView) findViewById(R.id.bestScoreV4);
                            textView.setText(Integer.toString(leaderboardList.get(3).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            textView = (TextView) findViewById(R.id.bestScore5);
                            textView.setText("5 : " + leaderboardList.get(4).name);
                            textView = (TextView) findViewById(R.id.bestScoreV5);
                            textView.setText(Integer.toString(leaderboardList.get(4).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            textView = (TextView) findViewById(R.id.bestScore6);
                            textView.setText("6 : " + leaderboardList.get(5).name);
                            textView = (TextView) findViewById(R.id.bestScoreV6);
                            textView.setText(Integer.toString(leaderboardList.get(5).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            textView = (TextView) findViewById(R.id.bestScore7);
                            textView.setText("7 : " + leaderboardList.get(6).name);
                            textView = (TextView) findViewById(R.id.bestScoreV7);
                            textView.setText(Integer.toString(leaderboardList.get(6).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            textView = (TextView) findViewById(R.id.bestScore8);
                            textView.setText("8 : " + leaderboardList.get(7).name);
                            textView = (TextView) findViewById(R.id.bestScoreV8);
                            textView.setText(Integer.toString(leaderboardList.get(7).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            textView = (TextView) findViewById(R.id.bestScore9);
                            textView.setText("9 : " + leaderboardList.get(8).name);
                            textView = (TextView) findViewById(R.id.bestScoreV9);
                            textView.setText(Integer.toString(leaderboardList.get(8).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            textView = (TextView) findViewById(R.id.bestScore10);
                            textView.setText("10 : " + leaderboardList.get(9).name);
                            textView = (TextView) findViewById(R.id.bestScoreV10);
                            textView.setText(Integer.toString(leaderboardList.get(9).value));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
        );

    }
}