package com.example.desk.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * DB table definitions
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.desk.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // all possible paths
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";

    // movie table - all movies (popular, top rated, favorite) are stored in this table.
    // one movie can belong to all three lists
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movie_vote_average";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";
        // TheMovieDB poster URL
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
        // TheMovieDB ID
        public static final String COLUMN_MOVIE_DB_ID = "movie_db_id";
        // favorite list indicator
        public static final String COLUMN_MOVIE_IS_FAVORITE = "movie_is_favorite";
        // top rated list indicator
        public static final String COLUMN_MOVIE_IS_TOP_RATED = "movie_is_top_rated";
        // popular list indicator
        public static final String COLUMN_MOVIE_IS_POPULAR = "movie_is_popular";
        // last update date
        public static final String COLUMN_MOVIE_UPDATED_AT = "movie_updated_at";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE)
                .build();

        public static final Uri CONTENT_MOVIE_FAVORITE_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(PATH_FAVORITE)
                .build();

        public static final Uri CONTENT_MOVIE_POPULAR_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(PATH_POPULAR)
                .build();

        public static final Uri CONTENT_MOVIE_TOP_RATED_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(PATH_TOP_RATED)
                .build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // trailer table
    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_MOVIE_DB_ID = "movie_db_id";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER_PATH = "trailer_path";
        public static final String COLUMN_TRAILER_DB_ID = "trailer_db_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER)
                .build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieTrailerUri(long movieId) {
            Uri movieUri = MovieEntry.buildMovieUri(movieId);
            return movieUri.buildUpon().appendPath(PATH_TRAILER).build();
        }
    }

    // review table
    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "review";

        public static final String COLUMN_MOVIE_DB_ID = "movie_db_id";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";
        public static final String COLUMN_REVIEW_DB_ID = "review_db_id";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW)
                .build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieReviewUri(long movieId) {
            Uri movieUri = MovieEntry.buildMovieUri(movieId);
            return movieUri.buildUpon().appendPath(PATH_REVIEW).build();
        }
    }

    public static String getMovieIdFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }
}
