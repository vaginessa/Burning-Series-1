package to.bs.bruningseriesmeterial.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import to.bs.bruningseriesmeterial.Utils.ImageCoder;

/**
 * Created by Phillipp on 12.04.2017.
 */

public class SeasonDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BurningSeries.db";
    private static final String SQL_CREATE_SEASON = "CREATE TABLE " + SeasonContract.SeasonEntry.TABLE_NAME + " ("+SeasonContract.SeasonEntry._ID + " INTEGER PRIMARY KEY," + SeasonContract.SeasonEntry.SEASON_NAME + " TEXT )";
    private static final String SQL_CREATE_EPISODES = "CREATE TABLE " + SeasonContract.EpisodesEntry.TABLE_NAME + " (" + SeasonContract.EpisodesEntry._ID + " INTEGER PRIMARY KEY," + SeasonContract.EpisodesEntry.SEASON_NAME + " TEXT," + SeasonContract.EpisodesEntry.Episode_NAME + " TEXT)";
    private static final String SQL_CREATE_TOWATCH = "CREATE TABLE " + SeasonContract.ToWatchEntry.TABLE_NAME + " ("+SeasonContract.ToWatchEntry._ID + " INTEGER PRIMARY KEY," + SeasonContract.ToWatchEntry.SEASON_NAME + " TEXT," + SeasonContract.ToWatchEntry.GERNE_NAME + " TEXT," + SeasonContract.ToWatchEntry.DESCRIPTION_NAME + " TEXT)";

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
        db.close();
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == newVersion){
            return;
        }
        db.execSQL(SQL_DELETE_EPISODES);
        db.execSQL(SQL_DELETE_SEASON);
        db.execSQL(SQL_DELETE_TOWATCH);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public void addEpisode(String serie,String folge){
        if(!isInsertEpisode(serie,folge)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SeasonContract.EpisodesEntry.SEASON_NAME, serie);
            values.put(SeasonContract.EpisodesEntry.Episode_NAME, folge);
            db.insert(SeasonContract.EpisodesEntry.TABLE_NAME, null, values);
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
            db.close();
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
        db.close();
    }
    public void addSeason(String serie){
        if(!isInsertSeason(serie)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SeasonContract.SeasonEntry.SEASON_NAME, serie);
            db.insert(SeasonContract.SeasonEntry.TABLE_NAME, null, values);
            db.close();
        }
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
            db.close();
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
    public void addSeasonToWatch(String serie,String gerne,String Desc){
        if(!isInsertSeasonToWatch(serie)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SeasonContract.ToWatchEntry.SEASON_NAME, serie);
            values.put(SeasonContract.ToWatchEntry.GERNE_NAME, gerne);
            values.put(SeasonContract.ToWatchEntry.DESCRIPTION_NAME, Desc);
            db.insert(SeasonContract.ToWatchEntry.TABLE_NAME, null, values);
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
            db.close();
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
        db.close();
    }

}
