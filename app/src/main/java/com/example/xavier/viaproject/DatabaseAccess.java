package com.example.xavier.viaproject;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.jar.Attributes;

/**
 * Created by Xavier on 04/08/2016.
 */
public class DatabaseAccess {

    private FirebaseDatabase mDatabase;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public DatabaseAccess (Context context) {
        mDatabase = FirebaseDatabase.getInstance();
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        final DatabaseReference databaseReference = mDatabase.getReference("record/" + music + "/" + playerName + "_record");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer prevRecord = dataSnapshot.getValue(Integer.class);
                if(prevRecord == null || prevRecord < record) {
                    Toast.makeText(mContext, "New best record !", Toast.LENGTH_SHORT).show();
                    databaseReference.setValue(record);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
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
                Log.e("size :", Integer.toString(leaderboardList.size()));
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

    public interface notificationCallback {
        void callback (Record record);
    }

    void quickSort(List<Record> arr) {
        quickSortRec(arr, 0, arr.size() - 1);
    }

    public void recordBreacking (final Record record, final notificationCallback cb) {
        String music = mSharedPreferences.getString(Constants.PREF_MUSIC_KEY, Constants.DEFAULT_MUSIC);
        final DatabaseReference databaseReference = mDatabase.getReference("record/" + music);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String newPlayer = dataSnapshot.getKey();
                Integer newRecord = dataSnapshot.getValue(Integer.class);
                if (!newPlayer.equals(record.name)) {
                    if (newRecord > record.value) {
                        cb.callback(new Record(newPlayer, newRecord));
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String newPlayer = dataSnapshot.getKey();
                Integer newRecord = dataSnapshot.getValue(Integer.class);
                if (!newPlayer.equals(record.name)) {
                    if (newRecord > record.value) {
                        cb.callback(new Record(newPlayer, newRecord));
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
