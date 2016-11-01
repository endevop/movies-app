package com.example.desk.popularmovies.data;

//*

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Calendar;
import java.util.HashSet;


public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );
        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MovieContract.MovieEntry._ID);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_IS_POPULAR);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_IS_TOP_RATED);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_UPDATED_AT);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testMovieInsert() throws Throwable {
        long now = Calendar.getInstance().getTimeInMillis();
        SQLiteDatabase db = new MovieDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        //enable foreign keys
        db.execSQL("PRAGMA foreign_keys=ON;");

        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID, 1);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE, 1);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Hello world!");
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, "Short overview");
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, "http://");
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "2015-04-02");
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, 3.4);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_UPDATED_AT, now);

        // Insert movie into database and get a row ID back
        long rowID = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
        assertTrue(rowID != -1);

        // Insert review for the movie without movie ID
        ContentValues cvReview = new ContentValues();
        cvReview.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "Reno");
        rowID = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, cvReview);
        assertTrue(rowID == -1);

        // Insert review for the movie with bad movie ID
        cvReview.put(MovieContract.ReviewEntry.COLUMN_MOVIE_DB_ID, 4);
        rowID = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, cvReview);
        assertTrue(rowID == -1);

        // Insert review for the movie with good movie ID
        cvReview.put(MovieContract.ReviewEntry.COLUMN_MOVIE_DB_ID, 1);
        rowID = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, cvReview);
        assertTrue(rowID != -1);

        // Delete movie
        rowID = db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        assertTrue(rowID > 0);

        // Check the review was deleted automatically
        rowID = db.delete(MovieContract.ReviewEntry.TABLE_NAME, null, null);
        assertTrue(rowID == 0);

        db.close();
    }
}

