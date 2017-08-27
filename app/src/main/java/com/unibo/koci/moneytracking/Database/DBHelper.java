package com.unibo.koci.moneytracking.Database;


import android.content.Context;

import com.amitshekhar.DebugDB;
import com.unibo.koci.moneytracking.Entities.DaoMaster;
import com.unibo.koci.moneytracking.Entities.DaoSession;
import com.unibo.koci.moneytracking.Entities.MoneyItem;

import org.greenrobot.greendao.database.Database;

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

    public double getTotalAmount(){
        List<MoneyItem> l = daoSession.getMoneyItemDao().loadAll();
        double total = 0.0;
        ListIterator<MoneyItem> listIterator = l.listIterator();
        while(listIterator.hasNext()){
            total += listIterator.next().getAmount();


        }
        return total;
    }

}