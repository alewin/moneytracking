package com.unibo.koci.moneytracking.Broadcast;

/**
 * Created by koale on 31/08/17.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver
{
    MoneyReminder alarm = new MoneyReminder();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            alarm.setAlarm(context);
            Log.w("DEBUGKOCI","ALLARME2");

        }
    }
}