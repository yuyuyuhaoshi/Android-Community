package com.yhslib.android.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.support.constraint.Constraints.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseFiled.DATABASE_NAME, null, DatabaseFiled.DATABASE_VERSION);
        Log.d(TAG, "Create DatabaseHelper Object Version=" + DatabaseFiled.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + DatabaseFiled.Tables.USER + " ("
                + DatabaseFiled.User.USERID + " TEXT PRIMARY KEY, "
                + DatabaseFiled.User.USERNAME + " TEXT NOT NULL,"
                + DatabaseFiled.User.TOKEN + " TEXT NOT NULL,"
                + DatabaseFiled.User.TIME + " TEXT NOT NULL,"
                + DatabaseFiled.User.TIMESTAMP + " TEXT NOT NULL"
                + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade=" + newVersion);
        dropTableDiary(db);
        createTableDiary(db);
        Log.d(TAG, "onUpgrade success");
    }

    void createTableDiary(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + DatabaseFiled.Tables.USER + " ("
                + DatabaseFiled.User.USERID + " TEXT PRIMARY KEY, "
                + DatabaseFiled.User.USERNAME + " TEXT NOT NULL,"
                + DatabaseFiled.User.TOKEN + " TEXT NOT NULL,"
                + DatabaseFiled.User.TIME + " TEXT NOT NULL,"
                + DatabaseFiled.User.TIMESTAMP + " TEXT NOT NULL"
                + ");";
        db.execSQL(sql);
    }

    void dropTableDiary(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseFiled.DATABASE_NAME);
    }
}

