package com.example.xavier.viaproject;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Xavier on 04/08/2016.
 */
public class DatabaseAccess {

    public DatabaseAccess (final Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setLogLevel(Logger.Level.DEBUG);
        final DatabaseReference appLaunchCount = database.getReference("appLaunchCount");

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
                // Toast the amount of times the app has been opened
                Toast.makeText(context, "App launched: " + newValue + " times", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
