package com.example.xavier.viaproject;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.List;

/**
 * Created by Xavier on 04/08/2016.
 */
public class DatabaseAccess {

    private FirebaseDatabase mDatabase;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public DatabaseAccess(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setLogLevel(Logger.Level.DEBUG);
        mDatabase = database;
        mContext = context;
    }

    public DatabaseAccess (Context context, SharedPreferences sharedPreferences) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setLogLevel(Logger.Level.DEBUG);
        mDatabase = database;
        mContext = context;
        mSharedPreferences = sharedPreferences;
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
        String playerName = mSharedPreferences.getString(Constants.PREF_NAME_KEY, "New player");
        String music = mSharedPreferences.getString(Constants.PREF_MUSIC_KEY, "rhcp");
        final DatabaseReference appLaunchCount = mDatabase.getReference("record/" + music + "/" + playerName + "_record");

        appLaunchCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer prevRecord = dataSnapshot.getValue(Integer.class);
                if(prevRecord == null || prevRecord < record) {
                    Toast.makeText(mContext, "New best record !", Toast.LENGTH_SHORT).show();
                    appLaunchCount.setValue(record);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
