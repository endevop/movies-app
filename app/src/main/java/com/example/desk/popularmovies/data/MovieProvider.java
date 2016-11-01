package com.example.desk.popularmovies.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MovieProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_FAVORITE = 101;
    static final int MOVIE_POPULAR = 102;
    static final int MOVIE_TOP_RATED = 103;
    static final int TRAILER = 200;
    static final int MOVIE_TRAILER = 201; // trailer for a specific movie
    static final int REVIEW = 300;
    static final int MOVIE_REVIEW = 301; // review for a specific movie

    // movie.movie_is_favorite = ?
    private static final String sMovieFavoriteSelection = MovieContract.MovieEntry.TABLE_NAME + "."
            + MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE + " = ?";

    // movie.movie_is_popular = ?
    private static final String sMoviePopularSelection = MovieContract.MovieEntry.TABLE_NAME + "."
            + MovieContract.MovieEntry.COLUMN_MOVIE_IS_POPULAR + " = ?";

    // movie.movie_is_top_rated = ?
    private static final String sMovieTopRatedSelection = MovieContract.MovieEntry.TABLE_NAME + "."
            + MovieContract.MovieEntry.COLUMN_MOVIE_IS_TOP_RATED + " = ?";

    // trailer.movie_id = ?
    private static final String sMovieTrailerSelection = MovieContract.TrailerEntry.TABLE_NAME + "."
            + MovieContract.TrailerEntry.COLUMN_MOVIE_DB_ID + " = ?";

    // review.movie_id = ?
    private static final String sMovieReviewSelection = MovieContract.ReviewEntry.TABLE_NAME + "."
            + MovieContract.ReviewEntry.COLUMN_MOVIE_DB_ID + " = ?";

    static UriMatcher buildUriMatcher() {
        UriMatcher um = new UriMatcher(UriMatcher.NO_MATCH);

        // all movies
        um.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);

        // a movie
        um.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE);

        // all trailers
        um.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER, TRAILER);

        // all reviews
        um.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW, REVIEW);

        // all favorite movies
        um.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/favorite",
                MOVIE_FAVORITE);

        // all popular movies
        um.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/popular",
                MOVIE_POPULAR);

        // all top rated movies
        um.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/top_rated",
                MOVIE_TOP_RATED);

        // all trailers for a movie
        um.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#/" +
                MovieContract.PATH_TRAILER, MOVIE_TRAILER);

        // all reviews for a movie
        um.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#/" +
                MovieContract.PATH_REVIEW, MOVIE_REVIEW);

        return um;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_FAVORITE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_POPULAR:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_TOP_RATED:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case MOVIE_TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case MOVIE_REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        String movieId;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = getMovie(projection, selection, selectionArgs, sortOrder);
                break;
            case MOVIE_FAVORITE:
                retCursor = getMovie(projection, sMovieFavoriteSelection, new String[]{"1"},
                        sortOrder);
                break;
            case MOVIE_POPULAR:
                retCursor = getMovie(projection, sMoviePopularSelection, new String[]{"1"},
                        sortOrder);
                break;
            case MOVIE_TOP_RATED:
                retCursor = getMovie(projection, sMovieTopRatedSelection, new String[]{"1"},
                        MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " DESC");
                break;
            case TRAILER:
                retCursor = getTrailer(projection, selection, selectionArgs, sortOrder);
                break;
            case MOVIE_TRAILER:
                movieId = MovieContract.getMovieIdFromUri(uri);
                retCursor = getTrailer(projection, sMovieTrailerSelection, new String[]{movieId},
                        sortOrder);
                break;
            case REVIEW:
                retCursor = getReview(projection, selection, selectionArgs, sortOrder);
                break;
            case MOVIE_REVIEW:
                movieId = MovieContract.getMovieIdFromUri(uri);
                retCursor = getReview(projection, sMovieReviewSelection, new String[]{movieId},
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long id;

        //enable foreign keys
        db.execSQL("PRAGMA foreign_keys=ON;");

        switch(match) {
            case MOVIE:
                id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case REVIEW:
                id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case TRAILER:
                id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        //enable foreign keys
        db.execSQL("PRAGMA foreign_keys=ON;");

        switch(match) {
            case MOVIE:
                // for each deleted movie we must delete reviews and trailers
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        //enable foreign keys
        db.execSQL("PRAGMA foreign_keys=ON;");

        switch(match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private Cursor getMovie(String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
        Cursor retCursor;

        retCursor = mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        return retCursor;
    }

    private Cursor getTrailer(String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
        Cursor retCursor;

        retCursor = mOpenHelper.getReadableDatabase().query(
                MovieContract.TrailerEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        return retCursor;
    }

    private Cursor getReview(String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
        Cursor retCursor;

        retCursor = mOpenHelper.getReadableDatabase().query(
                MovieContract.ReviewEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        return retCursor;
    }
}
