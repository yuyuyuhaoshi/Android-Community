package com.yhslib.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDao {
    private static final String TAG = "UserDao";

    private static DatabaseHelper databaseHelper;

    public UserDao(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
    }

    public boolean createTable() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            databaseHelper.dropTableDiary(db);
            databaseHelper.createTableDiary(db);
            Log.d(TAG, "Create DB Table");
            return true;
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
            return false;
        }
    }

    public boolean insertUser(String userid, String username, String token, String timestamp, String time) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseFiled.User.USERID, userid);
        cv.put(DatabaseFiled.User.USERNAME, username);
        cv.put(DatabaseFiled.User.TOKEN, token);
        cv.put(DatabaseFiled.User.TIMESTAMP, timestamp);
        cv.put(DatabaseFiled.User.TIME, time);
        return db.insert(DatabaseFiled.Tables.USER, null, cv) != -1;
    }

    public boolean updateUser(String userid, String token, String timestamp) {
        try {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DatabaseFiled.User.TOKEN, token);
            cv.put(DatabaseFiled.User.TIMESTAMP, timestamp);
            int x = db.update(DatabaseFiled.Tables.USER, cv, "userid=?", new String[]{userid});
            return x > 0;
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
            return false;
        }
    }

    public boolean searchUser(String userid) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query(DatabaseFiled.Tables.USER, null, "userid=?", new String[]{userid}, null, null, null);
        if (cursor.getCount() == 0) {
            return false;
        }
        return true;
    }
}
