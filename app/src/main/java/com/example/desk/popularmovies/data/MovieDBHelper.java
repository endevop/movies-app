package com.example.desk.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.desk.popularmovies.data.MovieContract.MovieEntry;
import com.example.desk.popularmovies.data.MovieContract.ReviewEntry;
import com.example.desk.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Manages a local database for movie data.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "popularmovies.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // movie ID in the online database
                MovieEntry.COLUMN_MOVIE_DB_ID + " TEXT UNIQUE NOT NULL," +
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " STRING NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_IS_FAVORITE + " INTEGER DEFAULT 0, " +
                MovieEntry.COLUMN_MOVIE_IS_TOP_RATED + " INTEGER DEFAULT 0, " +
                MovieEntry.COLUMN_MOVIE_IS_POPULAR + " INTEGER DEFAULT 0, " +
                MovieEntry.COLUMN_MOVIE_UPDATED_AT + " INTEGER NOT NULL);";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // trailer ID in the online database
                TrailerEntry.COLUMN_MOVIE_DB_ID + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_PATH + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_TRAILER_DB_ID + " TEXT UNIQUE NOT NULL," +
                // Set up the Movie DB ID to be foreign key
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_DB_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_DB_ID + ") ON DELETE CASCADE);";


        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY," +

                // review ID in the online database
                ReviewEntry.COLUMN_MOVIE_DB_ID + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_DB_ID + " TEXT UNIQUE NOT NULL, " +
                // Set up the Movie DB ID to be foreign key
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_DB_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_DB_ID + ") ON DELETE CASCADE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Movies marked as favorites must be preserved
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
