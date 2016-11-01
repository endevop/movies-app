package com.example.desk.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestURIMatcher extends AndroidTestCase {

    // content://com.example.desk.popularmovies/movie
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;

    // content://com.example.desk.popularmovies/movie/favorite
    private static final Uri TEST_MOVIE_FAVORITE_DIR = MovieContract.MovieEntry.CONTENT_MOVIE_FAVORITE_URI;

    // content://com.example.desk.popularmovies/trailer
    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.CONTENT_URI;

    // content://com.example.desk.popularmovies/review
    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.CONTENT_URI;

    // content://com.example.desk.popularmovies/movie/#/trailer
    private static final Uri TEST_MOVIE_TRAILER_DIR = MovieContract.TrailerEntry.buildMovieTrailerUri(1);

    // content://com.example.desk.popularmovies/movie/#/review
    private static final Uri TEST_MOVIE_REVIEW_DIR = MovieContract.ReviewEntry.buildMovieReviewUri(1);

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);

        assertEquals("Error: The MOVIE FAVORITE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_FAVORITE_DIR), MovieProvider.MOVIE_FAVORITE);

        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_DIR), MovieProvider.TRAILER);

        assertEquals("Error: The REVIEW URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEW);

        assertEquals("Error: The MOVIE TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_TRAILER_DIR), MovieProvider.MOVIE_TRAILER);

        assertEquals("Error: The MOVIE REVIEW URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_REVIEW_DIR), MovieProvider.MOVIE_REVIEW);
    }
}
