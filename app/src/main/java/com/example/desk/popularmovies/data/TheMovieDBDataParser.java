package com.example.desk.popularmovies.data;

import android.net.Uri;
import android.util.Log;

import com.example.desk.popularmovies.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TheMovieDBDataParser {
    private final String LOG_TAG = TheMovieDBDataParser.class.getSimpleName();
    private final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final String PARAM_API_KEY = "api_key";
    private final String APPEND_TO_RESPONSE_KEY = "append_to_response";
    private final String APPEND_TO_RESPONSE_VALUE = "reviews,videos";
    private final String PARAM_PAGE = "page";

    public TheMovieDBDataParser() {}

    public Uri getUriByListName(String listName, int pageNumber) {

        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(listName)
                .appendQueryParameter(PARAM_PAGE, Integer.toString(pageNumber))
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY)
                .build();

        return uri;
    }

    public Uri getUriByMovieId(String movieId) {

        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.MY_MOVIE_DB_API_KEY)
                .appendQueryParameter(APPEND_TO_RESPONSE_KEY, APPEND_TO_RESPONSE_VALUE)
                .build();

        return uri;
    }

    /* this method parses data returned from
   https://api.themoviedb.org/3/movie/78/videos?api_key=###
    */
    public ArrayList<Trailer> parseTrailerJsonStr(String jsonStr) {
        // JSON objects needed
        final String TMDB_RESULTS          = "results";
        final String TMDB_ID               = "id";  // there are two IDs: 1) movie ID, 2) trailer ID
        final String TMDB_KEY              = "key"; // youtube video ID
        final String TMDB_NAME             = "name";
        final String TMDB_SITE             = "site";

        // this will be returned
        ArrayList<Trailer> trailerList = new ArrayList<>();

        try {
            JSONObject trailersJSON = new JSONObject(jsonStr);
            // first get the movie ID for these trailers
            String movieId = trailersJSON.getString(TMDB_ID);

            // get all other fields
            JSONArray trailersArray = trailersJSON.getJSONArray(TMDB_RESULTS);
            for(int i=0; i<trailersArray.length(); i++) {
                JSONObject j = trailersArray.getJSONObject(i);
                Trailer t = new Trailer();
                t.setMovieId(movieId);

                // id
                t.setId(j.getString(TMDB_ID));

                // name
                t.setName(j.getString(TMDB_NAME));

                // path
                t.setPath(j.getString(TMDB_SITE), j.getString(TMDB_KEY));

                trailerList.add(t);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return trailerList;
    }

    /* this method parses data returned from
  https://api.themoviedb.org/3/movie/78/reviews?api_key=###
   */
    public ArrayList<Review> parseReviewJsonStr(String jsonStr) {
        // JSON objects needed
        final String TMDB_RESULTS          = "results";
        final String TMDB_ID               = "id";  // there are two IDs: 1) movie ID, 2) review ID
        final String TMDB_AUTHOR           = "author";
        final String TMDB_CONTENT          = "content";

        // this will be returned
        ArrayList<Review> reviewList = new ArrayList<>();

        try {
            JSONObject reviewsJSON = new JSONObject(jsonStr);
            // first get the movie ID for these trailers
            String movieId = reviewsJSON.getString(TMDB_ID);

            // get all other fields
            JSONArray reviewsArray = reviewsJSON.getJSONArray(TMDB_RESULTS);
            for(int i=0; i<reviewsArray.length(); i++) {
                JSONObject j = reviewsArray.getJSONObject(i);
                Review r = new Review();
                r.setMovieId(movieId);

                // id
                r.setId(j.getString(TMDB_ID));

                // author
                r.setAuthor(j.getString(TMDB_AUTHOR));

                // content
                r.setContent(j.getString(TMDB_CONTENT));

                reviewList.add(r);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return reviewList;
    }


    public ArrayList<Movie> parseMovieJsonStr(String jsonStr) {
        // JSON objects needed
        final String TMDB_RESULTS               = "results";
        final String TMDB_POSTER_PATH           = "poster_path";
        final String TMDB_OVERVIEW              = "overview";
        final String TMDB_RELEASE_DATE          = "release_date";
        final String TMDB_ID                    = "id";
        final String TMDB_TITLE                 = "title";
        final String TMDB_VOTE_AVERAGE          = "vote_average";

        // this will be returned
        ArrayList<Movie> moviesList = new ArrayList<>();
        try {
            JSONObject moviesJSON = new JSONObject(jsonStr);
            JSONArray moviesArray = moviesJSON.getJSONArray(TMDB_RESULTS);

            for(int i=0; i<moviesArray.length(); i++) {
                JSONObject j = moviesArray.getJSONObject(i);
                Movie m = new Movie();

                // poster path
                m.setPosterPath(j.getString(TMDB_POSTER_PATH));

                // overview
                m.setOverview(j.getString(TMDB_OVERVIEW));

                // release date
                m.setReleaseDate(j.getString(TMDB_RELEASE_DATE));

                // id
                m.setId(j.getString(TMDB_ID));

                // title
                m.setTitle(j.getString(TMDB_TITLE));

                // vote average
                m.setVoteAverage(j.getString(TMDB_VOTE_AVERAGE));

                moviesList.add(m);
            }
            
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return moviesList;
    }

     /* this method parses data returned from
   https://api.themoviedb.org/3/movie/78?api_key=###&append_to_response=reviews,videos
    */
     public ArrayList<Review> parseAppendedReviewJsonStr(String movieId, String jsonStr) {
         // JSON objects needed
         final String TMDB_REVIEWS          = "reviews";
         final String TMDB_RESULTS          = "results";
         final String TMDB_ID               = "id";
         final String TMDB_AUTHOR           = "author";
         final String TMDB_CONTENT          = "content";

         // this will be returned
         ArrayList<Review> reviewList = new ArrayList<>();

         if(jsonStr == null)
             return reviewList;

         try {
             JSONObject appendedJSON = new JSONObject(jsonStr);
             JSONObject reviewsJSON = appendedJSON.getJSONObject(TMDB_REVIEWS);

             // get all fields
             JSONArray reviewsArray = reviewsJSON.getJSONArray(TMDB_RESULTS);
             for(int i=0; i<reviewsArray.length(); i++) {
                 JSONObject j = reviewsArray.getJSONObject(i);
                 Review r = new Review();
                 r.setMovieId(movieId);

                 // id
                 r.setId(j.getString(TMDB_ID));

                 // author
                 r.setAuthor(j.getString(TMDB_AUTHOR));

                 // content
                 r.setContent(j.getString(TMDB_CONTENT));

                 reviewList.add(r);
             }
         } catch (JSONException e) {
             Log.e(LOG_TAG, e.getMessage(), e);
         }

         return reviewList;
     }

    /* this method parses data returned from
   https://api.themoviedb.org/3/movie/78?api_key=###&append_to_response=reviews,videos
    */
    public ArrayList<Trailer> parseAppendedTrailerJsonStr(String movieId, String jsonStr) {
        // JSON objects needed
        final String TMDB_VIDEOS           = "videos";
        final String TMDB_RESULTS          = "results";
        final String TMDB_ID               = "id";
        final String TMDB_KEY              = "key"; // youtube video ID
        final String TMDB_NAME             = "name";
        final String TMDB_SITE             = "site";

        // this will be returned
        ArrayList<Trailer> trailerList = new ArrayList<>();

        if(jsonStr == null)
            return trailerList;

        try {
            JSONObject appendedJSON = new JSONObject(jsonStr);
            JSONObject trailersJSON = appendedJSON.getJSONObject(TMDB_VIDEOS);

            // get all other fields
            JSONArray trailersArray = trailersJSON.getJSONArray(TMDB_RESULTS);
            for(int i=0; i<trailersArray.length(); i++) {
                JSONObject j = trailersArray.getJSONObject(i);
                Trailer t = new Trailer();
                t.setMovieId(movieId);

                // id
                t.setId(j.getString(TMDB_ID));

                // name
                t.setName(j.getString(TMDB_NAME));

                // path
                t.setPath(j.getString(TMDB_SITE), j.getString(TMDB_KEY));

                trailerList.add(t);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return trailerList;
    }
}
