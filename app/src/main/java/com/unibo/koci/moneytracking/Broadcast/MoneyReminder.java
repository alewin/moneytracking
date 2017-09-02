package com.unibo.koci.moneytracking.Broadcast;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.Entities.PlannedItem;
import com.unibo.koci.moneytracking.R;

import org.joda.time.LocalDate;

import java.util.Date;

public class MoneyReminder extends BroadcastReceiver {
    private int nID = 0;

    SharedPreferences prefs;
    DBHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        checkPlanned(context);
        wl.release();
    }


    private void checkPlanned(Context context) {

        dbHelper = new DBHelper(context);
        PlannedItem p = dbHelper.popPlanned();



        if (p != null) {

            prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int reminder_times = Integer.parseInt(prefs.getString("notification_reminder", "1"));

            LocalDate current_date = new LocalDate();


            Log.w("DEBUGKOCI", p.getDate().getTime() + " -- " + current_date.toDate().getTime());

            if (p.getDate().getTime() == current_date.toDate().getTime()) {

                // convert planneditem to ==> moneyitem
                MoneyItem mi = new MoneyItem(null, p.getName(), p.getDescription(), p.getDate(), p.getAmount(), p.getCategoryID(), p.getLocationID());
                dbHelper.getDaoSession().getMoneyItemDao().insert(mi);

                PlannedNotifyUser(context, "MoneyTrack Transiction added",p.getName() + " planned item, was added to your transiction");
                Log.w("DEBUGKOCI", "moneyitem aggiunto");

                // decrease repeat from planneditem
                int repeat = p.getRepeat();
                p.setRepeat(repeat - 1);
                dbHelper.getDaoSession().update(p);

                Log.w("DEBUGKOCI", "repeat decrementato");


                // if planned item repeat is 0 delete it
                repeat = p.getRepeat();
                if (repeat == 0) {
                    dbHelper.getDaoSession().delete(p);

                    Log.w("DEBUGKOCI", "repeat era zero cancello il planned");

                } else {
                    //update planneditem with new planned_date
                    p.setDate(createPlannedDate(p.getOccurrence(), p.getDate()));
                    dbHelper.getDaoSession().update(p);
                    Log.w("DEBUGKOCI", "aggiorno planned item ora il prossimo sarà" + p.getDate());

                }
                Log.w("DEBUGKOCI", "faccio un'altro giro");
                checkPlanned(context);
            }else if(p.getDate().getTime() == current_date.plusDays(reminder_times).toDate().getTime()){

                //else if(p.getPlannedDate().getTime() "mancano N giorni allora manda una notifica e controlla di non mandarla più")
                PlannedNotifyUser(context,"MoneyTrack Reminder", p.getName() + "\n" + p.getDate());

            }
            Log.w("DEBUGKOC", "non è anroa il momento");



        }

        Log.w("DEBUGKOC", "ALLARME" + nID);

    }

    private Date createPlannedDate(String type, Date d) {
        LocalDate lo = LocalDate.fromDateFields(d);
        switch (type) {
            case "Daily":
                lo = lo.plusDays(1);
                break;
            case "Weekly":
                lo = lo.plusWeeks(1);
                break;
            case "Monthly":
                lo = lo.plusMonths(1);
                break;
            case "Yearly":
                lo = lo.plusYears(1);
                break;
        }
        return lo.toDate();
    }

    private void PlannedNotifyUser(Context context, String title, String description) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(description);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(nID, mBuilder.build());
        nID++;
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MoneyReminder.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pi);
        // 1000 * 60 * 10, pi Millisec * Second * Minute
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, MoneyReminder.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}


