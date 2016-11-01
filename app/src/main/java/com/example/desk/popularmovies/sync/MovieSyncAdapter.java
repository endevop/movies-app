package com.example.desk.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.example.desk.popularmovies.R;
import com.example.desk.popularmovies.data.Movie;
import com.example.desk.popularmovies.data.MovieContract;
import com.example.desk.popularmovies.data.Review;
import com.example.desk.popularmovies.data.TheMovieDBDataParser;
import com.example.desk.popularmovies.data.Trailer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;


public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    // TheMovieDB updates the popular list once per day, therefore we sync once per day
    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    // rate limit related
    private int mLimitRemaining; // number of requests remaining
    private int mLimitReset;     // time when the number of requests will be reset

    // movie selection by Movie DB ID
    private final String sMovieByIdSelection = MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID + " = ?";

    // movie selection to find obsolete non-favorite entries for deletion
    final String sMovieByTimeAndFavoriteSelection =
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE + " = ? AND " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_UPDATED_AT + " <> ?";

    // review by ID selection
    final String sReviewByIdSelection = MovieContract.ReviewEntry.TABLE_NAME + "." +
            MovieContract.ReviewEntry.COLUMN_REVIEW_DB_ID + " = ?";

    // trailer by ID selection
    final String sTrailerByIdSelection = MovieContract.TrailerEntry.TABLE_NAME + "." +
            MovieContract.TrailerEntry.COLUMN_TRAILER_DB_ID + " = ?";

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    private String fetchJson(Uri uri) {
        String jsonString;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        final String RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
        final String RATE_LIMIT_RESET = "X-RateLimit-Reset";

        try {
            URL url = new URL(uri.toString());
            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // check response codes
            int code = urlConnection.getResponseCode();
            if(code != 200) {
                // connection failed
                Log.e(LOG_TAG, "Response code: " + code);

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                return null;
            }

            // check headers for the rate limit
            String remainingStr = urlConnection.getHeaderField(RATE_LIMIT_REMAINING);
            mLimitRemaining = Integer.parseInt(remainingStr);
            //Log.d("limit remaining = ", remainingStr);
            String resetStr = urlConnection.getHeaderField(RATE_LIMIT_RESET);
            mLimitReset = Integer.parseInt(resetStr);

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if (buffer.length() == 0) {
                // Stream was empty
                return null;
            }
            jsonString = buffer.toString();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return jsonString;
    }

    private void insertOrUpdatePopularMovies(int page, long timestamp) {
        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        // Will parse JSON data
        TheMovieDBDataParser parser = new TheMovieDBDataParser();
        Uri popularMoviesUri = parser.getUriByListName(getContext()
                .getString(R.string.pref_movie_list_popular), page);
        jsonStr = fetchJson(popularMoviesUri);
        ArrayList<Movie> popularMoviesList = parser.parseMovieJsonStr(jsonStr);

        // for each popular movie update or insert into DB
        for (Movie m : popularMoviesList) {
            ContentValues cv = m.toContentValues();
            // additional info
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_POPULAR, 1);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_UPDATED_AT, timestamp);

            int updated = getContext().getContentResolver()
                    .update(MovieContract.MovieEntry.CONTENT_URI,
                            cv,
                            sMovieByIdSelection,
                            new String[]{m.getId()});
            if (updated != 1) {
                // record did not exist, perform insert
                getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
            }
        }
    }

    private void insertOrUpdateTopRatedMovies(int page, long timestamp) {
        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        // Will parse JSON data
        TheMovieDBDataParser parser = new TheMovieDBDataParser();
        // get top rated movies as JSON
        Uri topRatedMoviesUri = parser.getUriByListName(getContext()
                .getString(R.string.pref_movie_list_top_rated), page);
        jsonStr = fetchJson(topRatedMoviesUri);
        ArrayList<Movie> topRatedMoviesList = parser.parseMovieJsonStr(jsonStr);

        // for each top rated movie update or insert into DB
        for (Movie m : topRatedMoviesList) {
            ContentValues cv = m.toContentValues();
            // additional info
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_TOP_RATED, 1);
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_UPDATED_AT, timestamp);

            int updated = getContext().getContentResolver()
                    .update(MovieContract.MovieEntry.CONTENT_URI,
                            cv,
                            sMovieByIdSelection,
                            new String[]{m.getId()});
            if (updated != 1) {
                // record did not exist, perform insert
                getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, cv);
            }
        }
    }

    private void insertOrUpdateReviewsAndTrailers(String movieDbId) {
        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        // Will parse JSON data
        TheMovieDBDataParser parser = new TheMovieDBDataParser();

        Uri uri = parser.getUriByMovieId(movieDbId);

        jsonStr = fetchJson(uri);
        // parse reviews
        ArrayList<Review> reviews = parser.parseAppendedReviewJsonStr(movieDbId, jsonStr);
        for(Review r: reviews) {
            ContentValues cv = r.toContentValues();

            int updated = getContext().getContentResolver()
                    .update(MovieContract.ReviewEntry.CONTENT_URI,
                            cv,
                            sReviewByIdSelection,
                            new String[]{r.getId()});
            if(updated != 1) {
                // record did not exist, perform insert
                getContext().getContentResolver()
                        .insert(MovieContract.ReviewEntry.CONTENT_URI, cv);
            }
        }

        // parse trailers
        ArrayList<Trailer> trailers = parser.parseAppendedTrailerJsonStr(movieDbId, jsonStr);
        for(Trailer t: trailers) {
            ContentValues cv = t.toContentValues();
            int updated = getContext().getContentResolver()
                    .update(MovieContract.TrailerEntry.CONTENT_URI,
                            cv,
                            sTrailerByIdSelection,
                            new String[]{t.getId()});
            if(updated != 1) {
                // record did not exist, perform insert
                getContext().getContentResolver()
                        .insert(MovieContract.TrailerEntry.CONTENT_URI, cv);
            }
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        final String LOG_TAG = "onPerformSync";
        final int MAX_DOWNLOAD_PAGES = 5; // cache the first 5 pages of the selected list
        int page = 0;

        // Timestamp for inserted and updated records
        long ts = Calendar.getInstance().getTimeInMillis();

        // check the network
        ConnectivityManager cm = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = cm.getActiveNetworkInfo();

        if(n == null || !n.isConnected())
            return;

        // get popular movies as JSON and insert into DB
        for(page=1; page<MAX_DOWNLOAD_PAGES; page++)
            insertOrUpdatePopularMovies(page, ts);


        // get top rated movies as JSON and insert into DB
        for(page=1; page<MAX_DOWNLOAD_PAGES; page++)
            insertOrUpdateTopRatedMovies(page, ts);


        // delete all movies where timestamp is not current AND movie is not favorite
        // this should automatically delete all related reviews and trailers
        getContext().getContentResolver()
                .delete(MovieContract.MovieEntry.CONTENT_URI,
                sMovieByTimeAndFavoriteSelection,
                new String[]{"0", Long.toString(ts)});

        // for all movies in the DB we need reviews and trailers. get all movie DB IDs
        Cursor c = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID},
                null, null, null);

        if(c.getCount() > 0) {
            Log.d(LOG_TAG, "DB has " + c.getCount() + " movies");
        } else {
            // nothing to do
            c.close();
            return;
        }

        /*
        for each movie get reviews and trailers. use one request per movie for both reviews
        and trailers in order to minimize the network traffic.
        Example: https://api.themoviedb.org/3/movie/78?api_key=###&append_to_response=reviews,videos
        */

        boolean getMoreData = c.moveToFirst();

        while(getMoreData) {
            insertOrUpdateReviewsAndTrailers(c.getString(0));

            getMoreData = c.moveToNext();
            if(!getMoreData)
                break;

            // cool down if necessary, stop before it is too late
            if(mLimitRemaining < 2) {
                // sleep time in seconds
                long sleepTime = mLimitReset - System.currentTimeMillis() / 1000 + 2; // adding 2 extra to be safe
                SystemClock.sleep(sleepTime * 1000);// takes milliseconds
            }
        }

        c.close();
        long curTime = Calendar.getInstance().getTimeInMillis();
        long diffTime = curTime - ts;
        Log.d(LOG_TAG, "Took " + diffTime/1000 + " seconds to sync everything");
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount,
                context.getString(R.string.content_authority), true);

        //syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
