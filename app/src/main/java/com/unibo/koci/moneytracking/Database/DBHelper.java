package com.unibo.koci.moneytracking.Database;


import android.content.Context;
import android.os.Environment;

import com.unibo.koci.moneytracking.Entities.DaoMaster;
import com.unibo.koci.moneytracking.Entities.DaoSession;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.Entities.MoneyItemDao;

import org.greenrobot.greendao.database.Database;
import org.joda.time.LocalDate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by koale on 14/08/17.
 */


public class DBHelper {

    private static final String DB_NAME = "moneytrackDB";
    private DaoMaster.DevOpenHelper helper;
    private DaoSession daoSession;
    private DaoMaster daoMaster;
    private Database db;

    public DBHelper(Context context) {
        helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        db = helper.getWritableDb();

        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }


    public double getTotal(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double total = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            total += listIterator.next().getAmount();
        }
        return total;
    }

    public double getTotalExpense(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.lt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double total = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            total += listIterator.next().getAmount();
        }
        return total;
    }

    public double getTotalProfit(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.gt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double total = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            total += listIterator.next().getAmount();
        }
        return total;
    }

    public double getAVGProfit(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.gt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        if (l.size() == 0)
            return 0.0;
        else {
            double total = 0.0;
            ListIterator<MoneyItem> listIterator = l.listIterator();
            while (listIterator.hasNext()) {
                total += listIterator.next().getAmount();
            }
            return total / l.size();
        }
    }

    public double getAVGExpense(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.lt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        if (l.size() == 0)
            return 0.0;
        else {
            double total = 0.0;

            ListIterator<MoneyItem> listIterator = l.listIterator();
            while (listIterator.hasNext()) {
                total += listIterator.next().getAmount();
            }
            return total / l.size();
        }
    }

    public double getMAXProfit(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.gt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double max = 0.0, amount = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            amount = listIterator.next().getAmount();
            if (amount > max)
                max = amount;
        }
        return max;
    }

    public double getMINExpense(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.lt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        if (l.size() == 0)
            return 0.0;
        else {
            double min = 0.0, amount = 0.0;
            ListIterator<MoneyItem> listIterator = l.listIterator();
            while (listIterator.hasNext()) {
                amount = listIterator.next().getAmount();
                if (amount < min)
                    min = amount;
            }
            return min;
        }
    }

    private boolean deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
    public boolean clearReport(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MoneyTrack";
        return deleteFiles(path);
    }
    public void clearAllData(Context c) {
        daoMaster.dropAllTables(db, true);
        daoMaster.createAllTables(db, true);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MoneyTrack";
        deleteFiles(path);


    }
    // TODO per ogni categoria restituisci profit e expense nella data prestabilià


}