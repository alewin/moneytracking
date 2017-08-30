package com.unibo.koci.moneytracking.Database;


import android.content.Context;

import com.unibo.koci.moneytracking.Entities.DaoMaster;
import com.unibo.koci.moneytracking.Entities.DaoSession;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.Entities.MoneyItemDao;

import org.greenrobot.greendao.database.Database;
import org.joda.time.LocalDate;

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

    public DBHelper(Context context) {
        helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        Database db = helper.getWritableDb();
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
        double total = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            total += listIterator.next().getAmount();
        }
        return total/l.size();
    }

    public double getAVGExpense(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.lt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double total = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            total += listIterator.next().getAmount();
        }
        return total/l.size();
    }

    public double getMAXProfit(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.gt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double max = 0.0, amount = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            amount = listIterator.next().getAmount();
            if(amount > max)
                max = amount;
        }
        return max;
    }

    public double getMINExpense(LocalDate start, LocalDate end) {
        List<MoneyItem> l = daoSession.getMoneyItemDao().queryBuilder().where(MoneyItemDao.Properties.Amount.lt(0)).where(MoneyItemDao.Properties.Date.between(start.toDate(), end.toDate())).list();
        double min = Double.MAX_VALUE, amount = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while (listIterator.hasNext()) {
            amount = listIterator.next().getAmount();
            if(amount < min)
                min = amount;
        }
        return min;
    }

    // TODO per ogni categoria restituisci profit e expense nella data prestabilià

     /*  String pdfText = namePerson + "\n|Budget iniziale: " + initial_amount +
                "€\n|Totale: " + String.valueOf(dbh.getTotal()) +
                "€\n|Totale speso: " + String.valueOf(dbh.getTotalLoss()) +
                "€\n|Totale guadagnato: " + String.valueOf(dbh.getTotalEarn()) +
                "€\n|Media spesa: " + String.valueOf(dbh.avgLoss()) +
                "€\n|Media entrata: " + String.valueOf(dbh.avgEarn()) +
                "€\n|Massima spesa: " + String.valueOf(dbh.maxLoss()) +
                "€\n|Massimo guadagno: " + String.valueOf(dbh.maxEarn()) + "€";
                */

}