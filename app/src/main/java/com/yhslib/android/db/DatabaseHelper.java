package com.yhslib.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

import com.caverock.androidsvg.CSSParser;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Mydb.db";
    private static final int DATABASE_VERSION =8;
    private static final String TABLE_NAME = "Login";
    private static final String USERID = "userid";
    private static final String TIME="time";
    private static final String TIMESTAMP="timestamp";
    private static final String USERNAME = "username";
    private static final String TOKEN = "token";

    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Create DatabaseHelper Object Version=" + DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + USERID
                + " INTEGER PRIMARY KEY, " + USERNAME+ " TEXT NOT NULL,"+ TOKEN+ " TEXT NOT NULL,"+ TIMESTAMP + " TEXT NOT NULL"+");";
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
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + USERID
                + " INTEGER PRIMARY KEY, " +  USERNAME+ " TEXT NOT NULL,"+ TOKEN+ " TEXT NOT NULL,"+ TIMESTAMP + " TEXT NOT NULL"+");";
        db.execSQL(sql);

    }

    void dropTableDiary(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}

