package com.example.xavier.viaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.jar.Attributes;

/**
 * Created by Xavier on 04/08/2016.
 */
public class DatabaseAccess {

    private FirebaseDatabase mDatabase;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private String mRank;

    public DatabaseAccess (Context context) {
        mDatabase = FirebaseDatabase.getInstance();
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mRank = Constants.DEFAULT_RANK;
    }

    public void launchCount() {
        final DatabaseReference appLaunchCount = mDatabase.getReference("appLaunchCount");

        appLaunchCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Fetch the value from the snapshot of the data, as an Integer
                Integer value = dataSnapshot.getValue(Integer.class);

                // If the value is null, then the data wasn't defined in the database, so default to 1.
                int newValue;
                if (value == null) {
                    newValue = 1;
                } else {
                    newValue = value + 1;
                }

                // Upload the new value to the database
                appLaunchCount.setValue(newValue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    public void storeRecord (final Integer record){
        String playerName = mSharedPreferences.getString(Constants.PREF_NAME_KEY, Constants.DEFAULT_NAME);
        String music = mSharedPreferences.getString(Constants.PREF_MUSIC_KEY, Constants.DEFAULT_MUSIC);
        final DatabaseReference databaseReference = mDatabase.getReference("record/" + music + "/" + playerName);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer prevRecord = dataSnapshot.getValue(Integer.class);
                if(prevRecord == null || prevRecord < record) {
                    Toast.makeText(mContext, "New best record !", Toast.LENGTH_SHORT).show();
                    databaseReference.setValue(record);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.PREF_RECORD_KEY, Integer.toString(record));
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    public void rank (final Integer record) {
        String music = mSharedPreferences.getString(Constants.PREF_MUSIC_KEY, Constants.DEFAULT_MUSIC);
        final DatabaseReference databaseReference = mDatabase.getReference("record/" + music);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int rank = 1;
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : iterable) {
                    if (snapshot.getValue(Integer.class) > record) {
                        rank ++;
                    }
                }
                mRank = Integer.toString(rank);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String getRank () {
        return mRank;
    }

    public interface leaderboardCallback {
        void callback(List<Record> leaderboardList);
    }

    public void leaderboard(final List<Record> leaderboardList, final leaderboardCallback cb) {
        String music = mSharedPreferences.getString(Constants.PREF_MUSIC_KEY, Constants.DEFAULT_MUSIC);
        final DatabaseReference databaseReference = mDatabase.getReference("record/" + music);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : iterable) {
                    leaderboardList.add(new Record(snapshot.getKey(), snapshot.getValue(int.class)));
                }
                if (leaderboardList.size() > 1)
                    quickSort(leaderboardList);
                cb.callback(leaderboardList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    int partition(List<Record> arr, int left, int right)
    {
        int i = left, j = right;
        int tmp_value;
        String tmp_name;
        int pivot = arr.get((left + right) / 2).value;

        while (i <= j) {
            while (arr.get(i).value > pivot)
                i++;
            while (arr.get(j).value < pivot)
                j--;
            if (i <= j) {
                tmp_value = arr.get(i).value;
                arr.get(i).value = arr.get(j).value;
                arr.get(j).value = tmp_value;
                tmp_name = arr.get(i).name;
                arr.get(i).name = arr.get(j).name;
                arr.get(j).name = tmp_name;
                i++;
                j--;
            }
        }

        return i;
    }

    void quickSortRec(List<Record> arr, int left, int right) {
        int index = partition(arr, left, right);
        if (left < index - 1)
            quickSortRec(arr, left, index - 1);
        if (index < right)
            quickSortRec(arr, index, right);
    }
    void quickSort(List<Record> arr) {
        quickSortRec(arr, 0, arr.size() - 1);
    }

    public static Record getRandomRecord(List<Record> array) {
        int rnd = new Random().nextInt(array.size());
        return array.get(rnd);
    }

    public interface notificationCallback {
        void callback (Record recordToShow);
    }

    public void recordBreacking (final Record record, final notificationCallback cb) {
        final String music = mSharedPreferences.getString(Constants.PREF_MUSIC_KEY, Constants.DEFAULT_MUSIC);
        final DatabaseReference databaseReference = mDatabase.getReference("record/" + music);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Record> betterRecords = new ArrayList<>();
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : iterable) {
                    Record newRecord = new Record(snapshot.getKey(), snapshot.getValue(Integer.class));
                    if(newRecord.value > record.value) {
                        betterRecords.add(newRecord);
                    }
                }
                if(betterRecords.size() > 0) {
                    Record recordToShow = getRandomRecord(betterRecords);
                    recordToShow.music = music;
                    cb.callback(recordToShow);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
