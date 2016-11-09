package com.example.desk.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.desk.popularmovies.sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesGridFragment.Callback {

    private String mMovieListSetting;
    private static final String DETAILFRAGMENT_TAG  = "DFTAG";
    private static final String TRAILERFRAGMENT_TAG = "TFTAG";
    private static final String REVIEWFRAGMENT_TAG  = "RFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        mMovieListSetting = getMovieListSetting();
        setContentView(R.layout.activity_main);
        // check for two pane mode
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                /* DO not create anything before the user selects a movie
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailsFragment(), DETAILFRAGMENT_TAG)
                        .commit();


                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_trailer_container, new MovieTrailersFragment(), TRAILERFRAGMENT_TAG)
                        .commit();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_review_container, new MovieReviewsFragment(), REVIEWFRAGMENT_TAG)
                        .commit();*/
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check if the settings have been modified
        String curListSetting = getMovieListSetting();
        if(!curListSetting.equals(mMovieListSetting)) {
            mMovieListSetting = curListSetting;
            MoviesGridFragment mgf = (MoviesGridFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_movies_grid);
            if(null != mgf) {
                mgf.onMovieListSettingChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent launchSettings = new Intent(this, SettingsActivity.class);
            startActivity(launchSettings);

            return true;
        }

        if (id == R.id.action_about) {
            View aboutView = getLayoutInflater().inflate(R.layout.about, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(aboutView);
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getMovieListSetting() {
        String movieList;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        movieList = settings.getString(getString(R.string.pref_movie_list_key),
                getString(R.string.pref_movie_list_default));

        return movieList;
    }

    public void onItemSelected(Uri movieUri, View view) {
        if(mTwoPane) {
            // details
            Bundle detailsArgs = new Bundle();
            detailsArgs.putParcelable(MovieDetailsFragment.DETAIL_URI, movieUri);
            MovieDetailsFragment detailsFragment = new MovieDetailsFragment();
            detailsFragment.setArguments(detailsArgs);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailsFragment, DETAILFRAGMENT_TAG)
                    .commit();

            // trailers
            Bundle trailersArgs = new Bundle();
            trailersArgs.putParcelable(MovieTrailersFragment.DETAIL_URI, movieUri);
            MovieTrailersFragment trailersFragment = new MovieTrailersFragment();
            trailersFragment.setArguments(trailersArgs);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_trailer_container, trailersFragment, TRAILERFRAGMENT_TAG)
                    .commit();

            // reviews
            Bundle reviewsArgs = new Bundle();
            reviewsArgs.putParcelable(MovieReviewsFragment.DETAIL_URI, movieUri);
            MovieReviewsFragment reviewsFragment = new MovieReviewsFragment();
            reviewsFragment.setArguments(reviewsArgs);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_review_container, reviewsFragment, REVIEWFRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class).setData(movieUri);
            //startActivity(intent);
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            new Pair<View, String>(view,
                                    getString(R.string.detail_movie_poster_transition_name)));
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        }
    }
}
