package com.example.xavier.viaproject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xavier on 08/08/2016.
 */
public class NotificationService extends IntentService {

    private int mUpdateTime;

    private SharedPreferences mSharedPreferences;

    public NotificationService() {
        super("NotficationService");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUpdateTime = 60000 * Integer.parseInt(mSharedPreferences.getString(Constants.PREF_UPDATE_KEY, Constants.DEFAULT_UPDATE));
        return START_STICKY;
    }

    private void showNotification(Record record) {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification n = new Notification.Builder(this)
                .setContentTitle(record.name + " has a better score")
                .setContentText(record.music + " - " + record.value)
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.sof_icon)
                .setSound(sound)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mUpdateTime > 0) {
            while (true) {
                checkForNotification();
                try {
                    Thread.sleep(mUpdateTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void checkForNotification() {
        DatabaseAccess databaseAccess = new DatabaseAccess(this);
        int recordValue = Integer.parseInt(mSharedPreferences.getString(Constants.PREF_RECORD_KEY, Constants.DEFAULT_RECORD));
        String name = mSharedPreferences.getString(Constants.PREF_NAME_KEY, Constants.DEFAULT_NAME);
        Record record = new Record(name, recordValue);
        databaseAccess.recordBreacking(record, new DatabaseAccess.notificationCallback() {
            @Override
            public void callback(final Record record) {
                showNotification(record);
            }
        });
    }
}
