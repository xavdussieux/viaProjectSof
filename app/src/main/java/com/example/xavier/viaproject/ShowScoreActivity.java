package com.example.xavier.viaproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
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

    public void initRecords () {
        List<Integer> bestRecordValues = new ArrayList<Integer>();
        List<String> bestRecordOwners = new ArrayList<String>();
        // TODO get best score in database https://stringsonfire-40ff9.firebaseio.com/record/"music"
        // where "music" should be replaced by the music name
        /*
        TextView textView = (TextView) findViewById(R.id.bestScore1);
        textView.setText("1 : " + bestRecordOwners.get(0) + ">>>>" + bestRecordValues.get(0));

        textView = (TextView) findViewById(R.id.bestScore2);
        textView.setText("2 : " + bestRecordOwners.get(1) + ">>>>" + bestRecordValues.get(1));

        textView = (TextView) findViewById(R.id.bestScore3);
        textView.setText("3 : " + bestRecordOwners.get(2) + ">>>>" + bestRecordValues.get(2));

        textView = (TextView) findViewById(R.id.bestScore4);
        textView.setText("4 : " + bestRecordOwners.get(3) + ">>>>" + bestRecordValues.get(3));

        textView = (TextView) findViewById(R.id.bestScore5);
        textView.setText("5 : " + bestRecordOwners.get(4) + ">>>>" + bestRecordValues.get(4));

        textView = (TextView) findViewById(R.id.bestScore6);
        textView.setText("6 : " + bestRecordOwners.get(5) + ">>>>" + bestRecordValues.get(5));

        textView = (TextView) findViewById(R.id.bestScore7);
        textView.setText("7 : " + bestRecordOwners.get(6) + ">>>>" + bestRecordValues.get(6));

        textView = (TextView) findViewById(R.id.bestScore8);
        textView.setText("8 : " + bestRecordOwners.get(7) + ">>>>" + bestRecordValues.get(7));

        textView = (TextView) findViewById(R.id.bestScore9);
        textView.setText("9 : " + bestRecordOwners.get(8) + ">>>>" + bestRecordValues.get(8));

        textView = (TextView) findViewById(R.id.bestScore10);
        textView.setText("10 : " + bestRecordOwners.get(9) + ">>>>" + bestRecordValues.get(9));
*/
    }
}
