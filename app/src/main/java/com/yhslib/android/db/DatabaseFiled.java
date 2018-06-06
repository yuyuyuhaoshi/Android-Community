package com.yhslib.android.db;

public final class DatabaseFiled {
    public static final String DATABASE_NAME = "community.db";
    public static final int DATABASE_VERSION = 1;

    public static final class Tables {
        public static final String USER = "user";
    }

    public static final class User {
        public static final String USERID = "userid";
        public static final String USERNAME = "username";
        public static final String TOKEN = "token";
        public static final String TIME = "time";
        public static final String TIMESTAMP = "timestamp";
    }
}
