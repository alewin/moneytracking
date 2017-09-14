package com.unibo.koci.moneytracking.Broadcast;

import android.app.AlarmManager;
import android.app.Notification;
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

import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.Entities.PlannedItem;
import com.unibo.koci.moneytracking.MainActivity;
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
            boolean notification_state = prefs.getBoolean("notifications_switch", true);

            LocalDate current_date = new LocalDate();
            if (p.getDate().getTime() <= current_date.toDate().getTime()) {
                // convert planneditem to ==> moneyitem
                MoneyItem mi = new MoneyItem(null, p.getName(), p.getDescription(), p.getDate(), p.getAmount(), p.getCategoryID(), p.getLocationID());
                dbHelper.getDaoSession().getMoneyItemDao().insert(mi);
                if(notification_state) {
                    PlannedNotifyUser(context, "MoneyTrack Transiction added", p.getName() + " planned item, was added to your transiction");
                }
                // decrease repeat from planneditem
                int repeat = p.getRepeat();
                p.setRepeat(repeat - 1);
                dbHelper.getDaoSession().update(p);

                // if planned item repeat is 0 delete it
                repeat = p.getRepeat();
                if (repeat == 0) {
                    dbHelper.getDaoSession().delete(p);

                } else {
                    //update planneditem with new planned_date
                    p.setDate(createPlannedDate(p.getOccurrence(), p.getDate()));
                    dbHelper.getDaoSession().update(p);
                }

                checkPlanned(context);
            } else if (p.getDate().getTime() < current_date.plusDays(reminder_times).toDate().getTime()) {
                if(notification_state) {
                    PlannedNotifyUser(context, "MoneyTrack Reminder", p.getName() + "\n" + p.getDate());
                }
            }

        }

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


        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notification = mBuilder.build();

        notification.contentIntent = intent; // .setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(nID, notification);

        nID++;
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, MoneyReminder.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 3600000, pi);
        // 1000 * 60 * 1, pi Millisec * Second * Minute  ---- * hour * day
        // 1 giorno 3600000
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, MoneyReminder.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}


