package to.bs.bruningseriesmeterial.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Phillipp on 12.04.2017.
 */

public class SeasonDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "BurningSeries.db";

    private static final String SQL_CREATE_SEASON = "CREATE TABLE " + SeasonContract.SeasonEntry.TABLE_NAME + " ("+SeasonContract.SeasonEntry._ID + " INTEGER PRIMARY KEY," + SeasonContract.SeasonEntry.SEASON_NAME + " TEXT)";
    private static final String SQL_CREATE_EPISODES = "CREATE TABLE " + SeasonContract.EpisodesEntry.TABLE_NAME + " (" + SeasonContract.EpisodesEntry._ID + " INTEGER PRIMARY KEY," + SeasonContract.EpisodesEntry.SEASON_NAME + " TEXT," + SeasonContract.EpisodesEntry.Episode_NAME + " TEXT)";
    private static final String SQL_CREATE_TOWATCH = "CREATE TABLE " + SeasonContract.ToWatchEntry.TABLE_NAME + " ("+SeasonContract.ToWatchEntry._ID + " INTEGER PRIMARY KEY," + SeasonContract.ToWatchEntry.SEASON_NAME + " TEXT," + SeasonContract.ToWatchEntry.GERNE_NAME + " TEXT," + SeasonContract.ToWatchEntry.DESCRIPTION_NAME + " TEXT,"+SeasonContract.ToWatchEntry.EPS_WATCHED +" INTEGER,"+SeasonContract.ToWatchEntry.EPS_COUNT+" INTEGER)";

    private static final String SQL_DELETE_EPISODES = "DROP TABLE IF EXISTS " + SeasonContract.EpisodesEntry.TABLE_NAME;
    private static final String SQL_DELETE_SEASON = "DROP TABLE IF EXISTS " + SeasonContract.SeasonEntry.TABLE_NAME;
    private static final String SQL_DELETE_TOWATCH = "DROP TABLE IF EXISTS " + SeasonContract.ToWatchEntry.TABLE_NAME;

    public SeasonDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TOWATCH);
        db.execSQL(SQL_CREATE_SEASON);
        db.execSQL(SQL_CREATE_EPISODES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_EPISODES);
        db.execSQL(SQL_DELETE_SEASON);
        db.execSQL(SQL_DELETE_TOWATCH);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public void addEpisode(String serie, String folge){
        if(!isInsertEpisode(serie,folge)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SeasonContract.EpisodesEntry.SEASON_NAME, serie);
            values.put(SeasonContract.EpisodesEntry.Episode_NAME, folge);
            db.insert(SeasonContract.EpisodesEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(SeasonContract.ToWatchEntry.EPS_WATCHED, getWatchedEpisodes(serie) +1);
            String selection = SeasonContract.SeasonEntry.SEASON_NAME + " = ?";
            String[] selectionArgs = { serie };

            db.update(
                    SeasonContract.ToWatchEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }
    public void addEpisodeToWatch(String serie, String folge,String URL){
        if(!isInsertEpisode(serie,folge)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SeasonContract.ToWatchEpisodeEntry.EP_NAME, folge);
            values.put(SeasonContract.ToWatchEpisodeEntry.EP_URL, URL);
            db.insert(serie.replaceAll(" ","_").replace('.','_').replace('/','_'), null, values);
        }
    }
    public boolean isInsertEpisode(String serie,String folge){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                SeasonContract.EpisodesEntry._ID,
                SeasonContract.EpisodesEntry.SEASON_NAME,
                SeasonContract.EpisodesEntry.Episode_NAME
        };

        String selection = SeasonContract.EpisodesEntry.Episode_NAME + " = ? AND " + SeasonContract.SeasonEntry.SEASON_NAME + " = ?";
        String[] selectionArgs = { folge,serie };

        Cursor cursor = db.query(
                SeasonContract.EpisodesEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        boolean b;
        try {
            b = cursor.moveToNext();
        } finally {
            cursor.close();
        }
        return b;
    }
    public void removeEpisode(String serie,String folge){
        if(!isInsertEpisode(serie, folge)){
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        String selection = SeasonContract.EpisodesEntry.SEASON_NAME + " LIKE ? AND " + SeasonContract.EpisodesEntry.Episode_NAME+" LIKE ?";
        String[] selectionArgs = { serie,folge };
        db.delete(SeasonContract.EpisodesEntry.TABLE_NAME, selection, selectionArgs);
        db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(SeasonContract.ToWatchEntry.EPS_WATCHED, getWatchedEpisodes(serie) - 1);

        selection = SeasonContract.SeasonEntry.SEASON_NAME + " = ?";
        selectionArgs = new String[]{serie};

        db.update(
                SeasonContract.ToWatchEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }
    public void addSeason(String serie){
        if(!isInsertSeason(serie)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SeasonContract.SeasonEntry.SEASON_NAME, serie);
            db.insert(SeasonContract.SeasonEntry.TABLE_NAME, null, values);
        }
    }
    public HashMap<String,String> getEpisodsToWatch(String serie){
        HashMap<String,String> seasons = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                SeasonContract.ToWatchEpisodeEntry._ID,
                SeasonContract.ToWatchEpisodeEntry.EP_NAME,
                SeasonContract.ToWatchEpisodeEntry.EP_URL,
        };

        Cursor cursor = db.query(
                serie.replaceAll(" ","_").replace('.','_').replace('/','_'),                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        try {
            while (cursor.moveToNext()) {
                String ep = cursor.getString(cursor.getColumnIndexOrThrow(SeasonContract.ToWatchEpisodeEntry.EP_NAME));
                String link = cursor.getString(cursor.getColumnIndexOrThrow(SeasonContract.ToWatchEpisodeEntry.EP_URL));
                seasons.put(ep,link);
            }
        } finally {
            cursor.close();
        }
        return seasons;
    }
    public List<String> getSeasons(){
        List<String> seasons = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+SeasonContract.ToWatchEntry.TABLE_NAME,null);

        try {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    seasons.add(cursor.getString(cursor.getColumnIndexOrThrow(SeasonContract.ToWatchEntry.SEASON_NAME)));
                    cursor.moveToNext();

                }
            }

        } finally {
            cursor.close();
        }
        return seasons;
    }
    public boolean isInsertSeason(String serie){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                SeasonContract.SeasonEntry._ID,
                SeasonContract.SeasonEntry.SEASON_NAME,
        };

        String selection = SeasonContract.SeasonEntry.SEASON_NAME + " = ? ";
        String[] selectionArgs = { serie };

        Cursor cursor = db.query(
                SeasonContract.SeasonEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        boolean b;
        try {
            b = cursor.moveToNext();
        } finally {
            cursor.close();
        }
        return b;
    }
    public int getEpisodes(String serie){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                SeasonContract.ToWatchEntry._ID,
                SeasonContract.ToWatchEntry.SEASON_NAME,
                SeasonContract.ToWatchEntry.EPS_COUNT
        };

        String selection = SeasonContract.ToWatchEntry.SEASON_NAME + " = ? ";
        String[] selectionArgs = { serie };

        Cursor cursor = db.query(
                SeasonContract.ToWatchEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        int b = 0;
        try {
            if (cursor.moveToNext()) {
                b = cursor.getInt(cursor.getColumnIndexOrThrow(SeasonContract.ToWatchEntry.EPS_COUNT));
            }
        } finally {
            cursor.close();
        }
        return b;
    }
    public int getWatchedEpisodes(String serie){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                SeasonContract.ToWatchEntry._ID,
                SeasonContract.ToWatchEntry.SEASON_NAME,
                SeasonContract.ToWatchEntry.EPS_WATCHED,
        };

        String selection = SeasonContract.ToWatchEntry.SEASON_NAME + " = ? ";
        String[] selectionArgs = { serie };

        Cursor cursor = db.query(
                SeasonContract.ToWatchEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        int b = 0;
        try {
            if (cursor.moveToNext()) {
                b = cursor.getInt(cursor.getColumnIndexOrThrow(SeasonContract.ToWatchEntry.EPS_WATCHED));
            }
        } finally {
            cursor.close();
        }
        return b;
    }
    public void removeSeason(String serie){
        if(!isInsertSeason(serie)){
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        String selection = SeasonContract.SeasonEntry.SEASON_NAME + " LIKE ?";
        String[] selectionArgs = { serie};
        db.delete(SeasonContract.SeasonEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }
    public void addSeasonToWatch(String serie,String gerne,String Desc,int count){
        if(!isInsertSeasonToWatch(serie)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SeasonContract.ToWatchEntry.SEASON_NAME, serie);
            values.put(SeasonContract.ToWatchEntry.GERNE_NAME, gerne);
            values.put(SeasonContract.ToWatchEntry.DESCRIPTION_NAME, Desc);
            values.put(SeasonContract.ToWatchEntry.EPS_COUNT, count);
            values.put(SeasonContract.ToWatchEntry.EPS_WATCHED, 0);
            db.insert(SeasonContract.ToWatchEntry.TABLE_NAME, null, values);
            db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s ("+SeasonContract.ToWatchEpisodeEntry._ID + " INTEGER PRIMARY KEY," + SeasonContract.ToWatchEpisodeEntry.EP_NAME + " TEXT," + SeasonContract.ToWatchEpisodeEntry.EP_URL + " TEXT)",serie.replaceAll(" ","_").replace('.','_').replace('/','_')));
            db.close();
        }
    }
    public boolean isInsertSeasonToWatch(String serie){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                SeasonContract.ToWatchEntry._ID,
                SeasonContract.ToWatchEntry.SEASON_NAME,
        };

        String selection = SeasonContract.ToWatchEntry.SEASON_NAME + " = ? ";
        String[] selectionArgs = { serie };

        Cursor cursor = db.query(
                SeasonContract.ToWatchEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        boolean b;
        try {
            b = cursor.moveToNext();
        } finally {
            cursor.close();
        }
        return b;
    }
    public void removeSeasonToWatch(String serie){
        if(!isInsertSeasonToWatch(serie)){
            return;
        }
        SQLiteDatabase db = getWritableDatabase();
        String selection = SeasonContract.ToWatchEntry.SEASON_NAME + " LIKE ?";
        String[] selectionArgs = { serie};
        db.delete(SeasonContract.ToWatchEntry.TABLE_NAME, selection, selectionArgs);
        db.execSQL(String.format("DROP TABLE IF EXISTS %s",serie.replaceAll(" ","_").replace('.','_').replace('/','_')));
    }
    public void updateCount(String serie,int count){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SeasonContract.ToWatchEntry.EPS_COUNT, count);
        String selection = SeasonContract.SeasonEntry.SEASON_NAME + " = ?";
        String[] selectionArgs = { serie };

        db.update(
                SeasonContract.ToWatchEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }
    public void updateWatchCount(String serie,int count){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SeasonContract.ToWatchEntry.EPS_WATCHED, count);
        String selection = SeasonContract.SeasonEntry.SEASON_NAME + " = ?";
        String[] selectionArgs = { serie };

        db.update(
                SeasonContract.ToWatchEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

}
