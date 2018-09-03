package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseLackey extends SQLiteOpenHelper {

    private static final String TAG = "*********";

    private static final String DATABASE_NAME = "DATABASE_NAME";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRACKS = "TRACKS";
    private static final String FIELD_ROW_ID = "_id";
    private static final String FIELD_TRACK_NAME = "TRACK_NAME";
    private static final String FIELD_TRACK_PATH = "TRACK_PATH";
    private static final String FIELD_TRACK_LENGTH = "TRACK_LENGTH";

    public DataBaseLackey(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DataBaseLackey(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        upgradeDataBase(db,oldVersion,newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        upgradeDataBase(sqLiteDatabase, 0, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        upgradeDataBase(sqLiteDatabase, oldVersion, newVersion);
    }

    private void upgradeDataBase(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        if (oldVersion < 1) {
            String tracksCreateQuery = "CREATE TABLE " + TABLE_TRACKS + " (" +
                    FIELD_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIELD_TRACK_NAME + " TEXT NOT NULL, " +
                    FIELD_TRACK_PATH + " TEXT UNIQUE, " +
                    FIELD_TRACK_LENGTH + " LONG NOT NULL" +
                    ");";
            sqLiteDatabase.execSQL(tracksCreateQuery);
            Log.d(TAG, "upgradeDataBase: "+tracksCreateQuery);
        }
    }
}
