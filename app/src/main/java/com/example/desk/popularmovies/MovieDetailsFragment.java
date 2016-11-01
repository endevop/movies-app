package com.example.desk.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.desk.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static final String DETAIL_URI = "URI";

    static final String TAG = "MDF_TAG";
    private static final int MOVIE_DETAILS_LOADER_ID = 200;
    private Uri mUri;
    private String mMovieDbId;
    private int mMovieIsFavorite;

    private TextView mMovieTitleView;
    private RatingBar mMovieVoteAverageView;
    private TextView mMovieReleaseDateView;
    private TextView mMovieOverviewView;
    private ImageView mMoviePosterView;
    private Button mMovieWatchTrailersButton;
    private Button mMovieReadReviewsButton;
    private MenuItem mActionFavoriteItem;

    final private String[] MOVIE_DETAILS_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE
    };

    final private int COLUMN_MOVIE_TITLE        = 0;
    final private int COLUMN_MOVIE_OVERVIEW     = 1;
    final private int COLUMN_MOVIE_RELEASE_DATE = 2;
    final private int COLUMN_MOVIE_POSTER_PATH  = 3;
    final private int COLUMN_MOVIE_VOTE_AVERAGE = 4;
    final private int COLUMN_MOVIE_IS_FAVORITE  = 5;

    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args != null) {
            mUri = args.getParcelable(DETAIL_URI);
            mMovieDbId = mUri.getLastPathSegment();
        }

        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);


        // title
        mMovieTitleView = (TextView) view.findViewById(R.id.movie_title);

        // vote average
        mMovieVoteAverageView = (RatingBar) view.findViewById(R.id.movie_vote_average);

        // release date
        mMovieReleaseDateView = (TextView) view.findViewById(R.id.movie_release_date);

        // overview
        mMovieOverviewView = (TextView) view.findViewById(R.id.movie_overview);

        // poster
        mMoviePosterView = (ImageView) view.findViewById(R.id.movie_poster);

        // trailers button only exists in one pane mode
        mMovieWatchTrailersButton = (Button) view.findViewById(R.id.movie_watch_trailers);
        if(mMovieWatchTrailersButton != null) {
            if (hasTrailers()) {
                mMovieWatchTrailersButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Callback) getActivity()).showTrailersList(mUri);
                    }
                });
            } else {
                mMovieWatchTrailersButton.setText(getString(R.string.button_no_trailers));
                mMovieWatchTrailersButton.setEnabled(false);
                mMovieWatchTrailersButton.setTextColor(getResources().getColor(R.color.colorFontInactiveButton));
            }
        }

        // reviews button only exists in one pane mode
        mMovieReadReviewsButton = (Button) view.findViewById(R.id.movie_read_reviews);
        if(mMovieReadReviewsButton != null) {
            if (hasReviews()) {
                mMovieReadReviewsButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Callback) getActivity()).showReviewsFlipper(mUri);
                    }
                });
            } else {
                mMovieReadReviewsButton.setText(getString(R.string.button_no_reviews));
                mMovieReadReviewsButton.setEnabled(false);
                mMovieReadReviewsButton.setTextColor(getResources().getColor(R.color.colorFontInactiveButton));
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // make sure the action bar is visible because the favorites star is shown there
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.moviedetailfragment, menu);

        // Retrieve the share menu item
        mActionFavoriteItem = menu.findItem(R.id.action_favorites);

        if(mMovieIsFavorite == 1)
            mActionFavoriteItem.setIcon(android.R.drawable.btn_star_big_on);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {

        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER_ID, null, this);
        super.onActivityCreated(savedInstance);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(null != mUri)
            return new CursorLoader(getActivity(),
                    mUri,
                    MOVIE_DETAILS_COLUMNS,
                    MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID + " = ?",
                    new String[]{mUri.getLastPathSegment()},
                    null);

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("on load finished", "finishing load");
        if(data != null && data.moveToFirst()) {
            String title = data.getString(COLUMN_MOVIE_TITLE);
            float voteAverage = data.getFloat(COLUMN_MOVIE_VOTE_AVERAGE);
            String overview = data.getString(COLUMN_MOVIE_OVERVIEW);
            String posterPath = data.getString(COLUMN_MOVIE_POSTER_PATH);
            mMovieIsFavorite = data.getInt(COLUMN_MOVIE_IS_FAVORITE);

            int releaseDateFormat = R.string.format_release_date;
            String rawDateStr = data.getString(COLUMN_MOVIE_RELEASE_DATE);
            String releaseDate = String.format(getString(releaseDateFormat), rawDateStr);

            mMovieTitleView.setText(title);
            mMovieVoteAverageView.setRating(voteAverage);
            mMovieReleaseDateView.setText(releaseDate);
            mMovieOverviewView.setText(overview);

            // only load poster in one pane mode
            if(mMoviePosterView != null) {
                Picasso.with(getContext())
                        .load(posterPath)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder_error)
                        .into(mMoviePosterView);
            }

            // favorites star
            if(mActionFavoriteItem != null) {
                if (mMovieIsFavorite == 1)
                    mActionFavoriteItem.setIcon(android.R.drawable.btn_star_big_on);
                else
                    mActionFavoriteItem.setIcon(android.R.drawable.btn_star_big_off);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       //
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorites) {
            onClickAddToFavoritesButton();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onClickAddToFavoritesButton() {

        int icon;

        if(mMovieIsFavorite == 1) {
            // remove from favorites
            mMovieIsFavorite = 0;
            icon = android.R.drawable.btn_star_big_off;
        }
        else {
            // add to favorites
            mMovieIsFavorite = 1;
            icon = android.R.drawable.btn_star_big_on;
        }

        mActionFavoriteItem.setIcon(icon);
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE, mMovieIsFavorite);
        int update = getActivity().getContentResolver()
                .update(MovieContract.MovieEntry.CONTENT_URI,
                        cv,
                        MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID + " = ?",
                        new String[]{mMovieDbId});
    }

    private boolean hasTrailers() {
        boolean has = false;

        if(mMovieDbId == null)
            return false;

        Uri trailersUri = MovieContract.TrailerEntry
                .buildMovieTrailerUri(Long.parseLong(mMovieDbId));
        Cursor c = getActivity().getContentResolver().query(trailersUri,
                new String[]{MovieContract.TrailerEntry._ID},
                null, null, null);

        if(c != null && c.getCount() > 0)
            has = true;

        c.close();

        return has;
    }

    private boolean hasReviews() {
        boolean has = false;

        if(mMovieDbId == null)
            return false;

        Uri reviewsUri = MovieContract.ReviewEntry
                .buildMovieReviewUri(Long.parseLong(mMovieDbId));
        Cursor c = getActivity().getContentResolver().query(reviewsUri,
                new String[]{MovieContract.ReviewEntry._ID},
                null, null, null);

        if(c != null && c.getCount() > 0)
            has = true;

        c.close();

        return has;
    }

    public interface Callback {
        /**
         * Callbacks for watching trailers and reading reviews
         */
        void showTrailersList(Uri movieUri);
        void showReviewsFlipper(Uri movieUri);
    }
}
