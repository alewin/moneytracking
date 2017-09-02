package com.unibo.koci.moneytracking.Broadcast;

/**
 * Created by koale on 31/08/17.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ReminderService extends Service {
    MoneyReminder alarm = new MoneyReminder();

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarm.setAlarm(this);
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        alarm.setAlarm(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}