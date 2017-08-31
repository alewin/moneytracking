package com.unibo.koci.moneytracking.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by koale on 31/08/17.
 */

public class MoneyReminder extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, MoneyReminder.class);
        context.startService(service);
    }
}