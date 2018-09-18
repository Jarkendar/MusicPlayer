package com.example.jaroslaw.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DataBaseLackey extends SQLiteOpenHelper {

    private static final String TAG = "*********";

    private static final String DATABASE_NAME = "DATABASE_NAME.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRACKS = "TABLE_TRACKS";
    private static final String FIELD_ROW_ID = "_id";
    private static final String FIELD_ARTIST = "ARTIST";
    private static final String FIELD_TITLE = "TITLE";
    private static final String FIELD_DATA = "DATA";
    private static final String FIELD_DISPLAY_NAME = "DISPLAY_NAME";
    private static final String FIELD_DURATION = "DURATION";
    private static final String FIELD_COUNT_OF_PLAYED = "COUNT_OF_PLAYED";

    private static final String TABLE_STATE = "TABLE_STATE";
    private static final String FIELD_STATE = "STATE";
    private static final String FIELD_POSITION = "POSITION";
    private static final String FIELD_CURRENT_DURATION = "CURRENT_DURATION";

    private static final String STATE_CURRENT = "CURRENT";
    private static final String STATE_NEXT = "NEXT";
    private static final String STATE_HISTORY = "HISTORY";

    private static final String INDEX_ON_TITLE = "INDEX_ON_TITLE";
    private static final String INDEX_ON_DATA = "INDEX_ON_DATA";

    public DataBaseLackey(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DataBaseLackey: "+context);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onDowngrade: "+db+" old "+oldVersion+"new"+newVersion);
        super.onDowngrade(db, oldVersion, newVersion);
        upgradeDataBase(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: "+sqLiteDatabase);
        upgradeDataBase(sqLiteDatabase, 0, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: "+sqLiteDatabase+" old "+oldVersion+"new"+newVersion);
        upgradeDataBase(sqLiteDatabase, oldVersion, newVersion);
    }

    private void upgradeDataBase(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "upgradeDataBase: "+oldVersion+" new "+ newVersion);
        if (oldVersion < 1) {
            String tracksCreateQuery = "CREATE TABLE " + TABLE_TRACKS + " (" +
                    FIELD_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIELD_ARTIST + " TEXT NULL," +
                    FIELD_TITLE + " TEXT NOT NULL, " +
                    FIELD_DATA + " TEXT UNIQUE, " +
                    FIELD_DISPLAY_NAME + " TEXT NULL, " +
                    FIELD_DURATION + " LONG NOT NULL, " +
                    FIELD_COUNT_OF_PLAYED + " INTEGER NOT NULL DEFAULT 0 " +
                    ");";
            sqLiteDatabase.execSQL(tracksCreateQuery);
            Log.d(TAG, "upgradeDataBase: " + tracksCreateQuery);
            String indexOnNameQuery = "CREATE INDEX " + INDEX_ON_TITLE +
                    " ON " + TABLE_TRACKS + "(" + FIELD_TITLE +
                    ");";
            sqLiteDatabase.execSQL(indexOnNameQuery);
            Log.d(TAG, "upgradeDataBase: " + indexOnNameQuery);
            String indexOnPathQuery = "CREATE INDEX " + INDEX_ON_DATA +
                    " ON " + TABLE_TRACKS + "(" + FIELD_DATA +
                    ");";
            sqLiteDatabase.execSQL(indexOnPathQuery);
            Log.d(TAG, "upgradeDataBase: " + indexOnPathQuery);
            String stateCreateQuery = "CREATE TABLE " + TABLE_STATE + " (" +
                    FIELD_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIELD_ARTIST + " TEXT NULL," +
                    FIELD_TITLE + " TEXT NOT NULL, " +
                    FIELD_DATA + " TEXT NOT NULL, " +
                    FIELD_DISPLAY_NAME + " TEXT NULL, " +
                    FIELD_DURATION + " LONG NOT NULL, " +
                    FIELD_CURRENT_DURATION + " LONG NOT NULL, " +
                    FIELD_STATE + " TEXT NOT NULL, " +
                    FIELD_POSITION + " LONG NOT NULL" +
                    ");";
            sqLiteDatabase.execSQL(stateCreateQuery);
            Log.d(TAG, "upgradeDataBase: " + stateCreateQuery);
        }
    }

    public void updateTableTracks(SQLiteDatabase sqLiteDatabase, List<Track> tracks) {
        if (tracks != null) {
            Cursor cursor = sqLiteDatabase.query(TABLE_TRACKS, new String[]{FIELD_DATA}, null, null, null, null, FIELD_DATA);
            Map<String, AtomicInteger> content = new HashMap<>();
            while (cursor.moveToNext()) {
                content.put(cursor.getString(0), new AtomicInteger(0));
            }
            for (Track track : new ArrayList<>(tracks)) {
                if (content.containsKey(track.getData())) {
                    content.get(track.getData()).getAndIncrement();
                    tracks.remove(track);
                }
            }
            cursor.close();
            for (Track track : tracks) {
                insertTrackToDatabase(sqLiteDatabase, track);
            }
            for (Map.Entry<String, AtomicInteger> entry : content.entrySet()) {
                if (entry.getValue().get() == 0) {
                    deleteTrackFromDatabase(sqLiteDatabase, entry.getKey());
                }
            }
        }
    }

    private void insertTrackToDatabase(SQLiteDatabase sqLiteDatabase, Track track) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_ARTIST, track.getArtist());
        contentValues.put(FIELD_TITLE, track.getTitle());
        contentValues.put(FIELD_DATA, track.getData());
        contentValues.put(FIELD_DISPLAY_NAME, track.getDisplayName());
        contentValues.put(FIELD_DURATION, track.getDurationTime());
        sqLiteDatabase.insert(TABLE_TRACKS, null, contentValues);
        Log.d(TAG, "insertTrackToDatabase: " + contentValues);
    }

    private void deleteTrackFromDatabase(SQLiteDatabase sqLiteDatabase, String data) {
        sqLiteDatabase.delete(TABLE_TRACKS, FIELD_DATA + "=?", new String[]{data});
        Log.d(TAG, "deleteTrackFromDatabase: " + data);
    }

    public synchronized void saveState(SQLiteDatabase sqLiteDatabase, PlayerState state){
        Log.d(TAG, "saveState: "+sqLiteDatabase+" "+state);
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.delete(TABLE_STATE, null, null);
        Log.d(TAG, "saveState: drop base");
        for (int i = 0; i<state.getHistory().size(); ++i){
            sqLiteDatabase.insert(TABLE_STATE, null, createTrackValues(state.getHistory().get(i), STATE_HISTORY, i));
        }
        Log.d(TAG, "saveState: insert all history");
        sqLiteDatabase.insert(TABLE_STATE, null, createTrackValues(state.getCurrent(), STATE_CURRENT, 0));
        Log.d(TAG, "saveState: insert current");
        for (int i = 0; i<state.getNext().size(); ++i){
            sqLiteDatabase.insert(TABLE_STATE, null, createTrackValues(state.getNext().get(i), STATE_NEXT, i));
        }
        Log.d(TAG, "saveState: insert all next");
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    private ContentValues createTrackValues(Track track, String state, int position){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIELD_ARTIST, track.getArtist());
        contentValues.put(FIELD_TITLE, track.getTitle());
        contentValues.put(FIELD_DATA, track.getData());
        contentValues.put(FIELD_DISPLAY_NAME, track.getDisplayName());
        contentValues.put(FIELD_DURATION, track.getDurationTime());
        contentValues.put(FIELD_CURRENT_DURATION, state.equals(STATE_CURRENT) ? track.getCurrentDuration() : 0);
        contentValues.put(FIELD_STATE, state);
        contentValues.put(FIELD_POSITION, position);
        return contentValues;
    }

    public PlayerState readStateFromDatabase(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "readStateFromDatabase: " + sqLiteDatabase);
        Cursor cursor = sqLiteDatabase.query(TABLE_STATE
                , new String[]{FIELD_ARTIST, FIELD_TITLE, FIELD_DATA, FIELD_DISPLAY_NAME, FIELD_DURATION, FIELD_CURRENT_DURATION, FIELD_STATE, FIELD_POSITION}
                , null
                , null
                , null
                , null
                , FIELD_STATE + " ASC, " + FIELD_POSITION + " ASC");
        Log.d(TAG, "readStateFromDatabase: cursor = " + cursor.getCount());
        PlayerState state = new PlayerState();
        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor.getColumnIndex(FIELD_ARTIST));
            String title = cursor.getString(cursor.getColumnIndex(FIELD_TITLE));
            String data = cursor.getString(cursor.getColumnIndex(FIELD_DATA));
            String displayName = cursor.getString(cursor.getColumnIndex(FIELD_DISPLAY_NAME));
            long duration = cursor.getLong(cursor.getColumnIndex(FIELD_DURATION));
            long currentDuration = cursor.getLong(cursor.getColumnIndex(FIELD_CURRENT_DURATION));
            String trackState = cursor.getString(cursor.getColumnIndex(FIELD_STATE));
            switch (trackState) {
                case STATE_CURRENT: {
                    state.setCurrent(new Track(artist, title, data, displayName, duration, currentDuration));
                    break;
                }
                case STATE_HISTORY: {
                    state.addLastHistory(new Track(artist, title, data, displayName, duration));
                    break;
                }
                case STATE_NEXT: {
                    state.addLastNext(new Track(artist, title, data, displayName, duration));
                }
            }
        }
        cursor.close();
        return state;
    }
}
