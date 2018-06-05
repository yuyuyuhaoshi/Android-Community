package com.yhslib.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yhslib.android.activity.LoginActivity;
import com.yhslib.android.fragment.LoginFragment;

public class UserDao {
    private static final String USERID = "userid";
    private static final String USERNAME = "username";
    private static final String TOKEN = "token";
    private static final String TIMESTAMP="timestamp";
    private static final String TAG="ls";
    private static final String TABLE_NAME = "Login";
    private static DatabaseHelper LoginHelper;
    public UserDao(Context context){
        if (LoginHelper==null){
            LoginHelper=new DatabaseHelper(context);
        }
    }
    public boolean createTable(){
        SQLiteDatabase db =LoginHelper.getWritableDatabase();
        try{
            LoginHelper.dropTableDiary(db);
            LoginHelper.createTableDiary(db);
            Log.d(TAG,"Create DB Table");
            return true;
        }catch (SQLException e){
            Log.d(TAG,e.getMessage());
            return false;
        }
    }
    public boolean insertLogin(String userid, String username, String token,String timestamp){
        SQLiteDatabase db=LoginHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(USERID,userid);
        cv.put(USERNAME,username);
        cv.put(TOKEN,token);
        cv.put(TIMESTAMP,timestamp);
//            Cursor cursor=get
        return db.insert(TABLE_NAME, null, cv) != -1;

    }
    public boolean updateLogin(String token,String timestamp) {
        try {
//            Log.d(TAG, "Do update before getWritableDatabase");
            SQLiteDatabase db = LoginHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(TOKEN,token);
            cv.put(TIMESTAMP,timestamp);
            int x = db.update(TABLE_NAME, cv, null, null);
            return x > 0;
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
            return false;
        }
    }

    public boolean search(String userid){
        SQLiteDatabase db = LoginHelper.getWritableDatabase();
        Cursor cursor=db.query(TABLE_NAME, null, "userid=?", new String[]{userid}, null,null, null);
        if(cursor.getCount() == 0){
            return false;
        }
        return true;
    }


}
