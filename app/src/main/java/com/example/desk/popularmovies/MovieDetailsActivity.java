package com.example.desk.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;

public class MovieDetailsActivity extends AppCompatActivity
        implements MovieDetailsFragment.Callback {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.DETAIL_URI, getIntent().getData());

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment, MovieDetailsFragment.TAG)
                    .commit();

            // Being here means we are in animation mode
            //supportPostponeEnterTransition();
        }
    }

    public void showTrailersList(Uri movieUri) {
        MovieTrailersFragment fragment = new MovieTrailersFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(MovieTrailersFragment.DETAIL_URI, movieUri);
        fragment.setArguments(arguments);

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.movie_detail_container, fragment);
        transaction.addToBackStack(MovieDetailsFragment.TAG);
        transaction.commit();
    }

    public void showReviewsFlipper(Uri movieUri) {
        Log.d("show reviews ", movieUri.toString());
        MovieReviewsFragment fragment = new MovieReviewsFragment();
        fragment.setHideActionBar(true);
        Bundle arguments = new Bundle();
        arguments.putParcelable(MovieReviewsFragment.DETAIL_URI, movieUri);
        fragment.setArguments(arguments);
        // add transition effect
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(1000);
        fragment.setEnterTransition(slide);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.movie_detail_container, fragment);
        transaction.addToBackStack(MovieDetailsFragment.TAG);
        transaction.commit();
    }
}
