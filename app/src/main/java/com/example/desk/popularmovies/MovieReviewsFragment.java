package com.example.desk.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.desk.popularmovies.data.MovieContract;

public class MovieReviewsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{
    public static String DETAIL_URI;
    private static final int REVIEW_LOADER_ID = 400;
    private long mMovieId;
    private Uri mUri;
    private ReviewAdapter mReviewAdapter;
    private ListView mListView;
    private int mListPosition = ListView.INVALID_POSITION;
    private boolean mHideActionBar;

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT
    };

    public static final int COLUMN_REVIEW_AUTHOR = 1;
    public static final int COLUMN_REVIEW_CONTENT = 2;

    public MovieReviewsFragment() {}

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // hide the action bar
        if(mHideActionBar)
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DETAIL_URI);
            mMovieId = Long.parseLong(MovieContract.getMovieIdFromUri(mUri));
        }
        Uri uriForReviews = MovieContract.ReviewEntry.buildMovieReviewUri(mMovieId);
        Cursor cur = getActivity().getContentResolver().query(uriForReviews,
                null, null, null, null);
        mReviewAdapter = new ReviewAdapter(getActivity(), cur, 0);
        View view = inflater.inflate(R.layout.fragment_reviews_listview, container, false);

        mListView = (ListView) view.findViewById(R.id.listview_reviews);

        mListView.setAdapter(mReviewAdapter);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        getLoaderManager().initLoader(REVIEW_LOADER_ID, savedInstance, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri defaultUri = MovieContract.ReviewEntry.buildMovieReviewUri(mMovieId);

        return new CursorLoader(getActivity(),
                defaultUri,
                REVIEW_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mReviewAdapter.swapCursor(data);
        if(mListPosition != ListView.INVALID_POSITION) {
            ListView lv = (ListView) getActivity().findViewById(R.id.listview_reviews);
            lv.smoothScrollToPosition(mListPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mReviewAdapter.swapCursor(null);
    }

    // Action bar should be hidden in single pane mode
    public void setHideActionBar(boolean hide) {
        mHideActionBar = hide;
    }
}
