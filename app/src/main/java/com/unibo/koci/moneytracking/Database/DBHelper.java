package com.unibo.koci.moneytracking.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.Date;

/**
 * Created by koale on 14/08/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    /* TABLE MONEY STRUCT */
    public static final String TABLE_MONEY = "moneytrack";
    public static final String MT_ID_MONEY = "id_transaction";
    public static final String MT_DATE_TRANSACTION = "date";
    public static final String MT_POSITION_TRANSACTION = "position";
    public static final String MT_CATEGORY_TRANSACTION = "category";
    public static final String MT_NAME_TRANSACTION = "name";
    public static final String MT_DESCRIPTION_TRANSACTION = "description";
    public static final String MT_AMOUNT_TRANSACTION = "amount";

    /* TABLE CATEGORY STRUCT */
    public static final String TABLE_CATEGORY = "categories";
    public static final String C_ID_CATEGORY = "id_category";
    public static final String C_NAME_CATEGORY = "category_name";

    /* TABLE CATEGORY STRUCT */
    public static final String TABLE_POSITION = "categories";
    public static final String P_ID_POSITION = "id_position";
    public static final String P_NAME = "name";
    public static final String P_LATITUDE = "lat";
    public static final String P_LONGITUDE = "long";


    /* DATABASE STRUCT */
    private static final String DATABASE_NAME = "moneytrack.db";
    private static final int DATABASE_VERSION = 1;

    /* MONEY TABLE CREATION */
    private static final String MONEYTRACK_CREATE = "CREATE TABLE "
            + TABLE_MONEY + "( "
            + MT_ID_MONEY + " INTEGER PRIMARY KEY, "
            + MT_NAME_TRANSACTION + "TEXT NOT NULL, "
            + MT_CATEGORY_TRANSACTION + "TEXT NOT NULL, "
            + MT_DATE_TRANSACTION + "TEXT NOT NULL, "
            + MT_AMOUNT_TRANSACTION + "TEXT NOT NULL "
            + MT_DESCRIPTION_TRANSACTION + "TEXT, "
            + MT_POSITION_TRANSACTION + "INTEGER )";

    /* CATEGORY TABLE CREATION */
    private static final String CATEGORY_CREATE = "CREATE TABLE "
            + TABLE_CATEGORY + "( "
            + C_ID_CATEGORY + " INTEGER PRIMARY KEY, "
            + C_NAME_CATEGORY + " TEXT NOT NULL );";

    /* POSITION TABLE CREATION */
    private static final String POSITION_CREATE = "CREATE TABLE "
            + TABLE_POSITION + "( "
            + P_ID_POSITION + " INTEGER PRIMARY KEY, "
            + P_NAME + "TEXT NOT NULL, "
            + P_LONGITUDE + "TEXT NOT NULL, "
            + P_LATITUDE + " TEXT NOT NULL );";


    /*------------------------------------DATABASE EVENT------------------------------------------*/

   
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(MONEYTRACK_CREATE);
        database.execSQL(CATEGORY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONEY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }


   /*------------------------------------DELETE QUERY---------------------------------------------*/

    public void deleteAll() {
        getWritableDatabase().delete(DBHelper.TABLE_MONEY, null, null);
        getWritableDatabase().delete(DBHelper.TABLE_CATEGORY, null, null);
    }

    public void deleteData() {
        getWritableDatabase().delete(DBHelper.TABLE_MONEY, null, null);
    }
/*
    public int removeCategory(int cat_id) {
        return getWritableDatabase().delete(TABLE_CATEGORY, C_NAME_CATEGORY + "=?", String.valueOf(cat_id));
    }
*/


    /*----------------------------------- INSERT QUERY -------------------------------------------*/

    public float insertNewExpense(String name, String desc, Date date, String cat, int amount) {
        ContentValues cv = new ContentValues();

        cv.put(MT_DATE_TRANSACTION,  date.toString());
        cv.put(MT_DESCRIPTION_TRANSACTION, desc);
        cv.put(MT_NAME_TRANSACTION, name);
        cv.put(MT_AMOUNT_TRANSACTION, amount);

        float code = getWritableDatabase().insert(TABLE_MONEY, null, cv);
        return code;
    }

    public float insertCategory (String cat) {
        ContentValues cv = new ContentValues();
        cv.put(C_NAME_CATEGORY, cat);
        float code = getWritableDatabase().insert(TABLE_CATEGORY, null, cv);
        return code;
    }



    /*----------------------------------- GET QUERY -------------------------------------------*/
    public Cursor getCategories() {
        return getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);
    }

    public Cursor getBudget() {
        return getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_MONEY, null);
    }


}