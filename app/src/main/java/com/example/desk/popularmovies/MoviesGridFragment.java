package com.example.desk.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.desk.popularmovies.data.MovieContract;
import com.example.desk.popularmovies.sync.MovieSyncAdapter;


public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // indicates selected movie
    private int mPosition = GridView.INVALID_POSITION;

    // adapter for the grid view
    private ImageViewAdapter mAdapter;

    private static final int GRID_LOADER_ID = 100;

    public static final String[] GRID_MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVORITE
    };

    static final int COLUMN_MOVIE_ID            = 0;
    static final int COLUMN_MOVIE_DB_ID         = 1;
    static final int COLUMN_MOVIE_POSTER_PATH   = 2;
    static final int COLUMN_MOVIE_IS_FAVORITE   = 3;

    public MoviesGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isOnline()) {
            Toast.makeText(getActivity(), getString(R.string.not_online), Toast.LENGTH_LONG).show();
        }
        //setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // get the previously selected movie (if exists)
        if(savedInstanceState != null &&
                savedInstanceState.containsKey(getString(R.string.movie_position_key)))
            mPosition = savedInstanceState.getInt(getString(R.string.movie_position_key));

        // read the order setting
        Uri dataUri = getUriBySortOrderSettings();

        Cursor cursor = getActivity().getContentResolver().query(dataUri, null, null, null, null);
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Attach adapter
        GridView gv = (GridView) rootView.findViewById(R.id.grid_view_movies);
        mAdapter = new ImageViewAdapter(getContext(), cursor, 0);
        gv.setAdapter(mAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null) {
                    mPosition = position;

                    Uri uri = MovieContract.MovieEntry
                            .buildMovieUri(cursor.getLong(COLUMN_MOVIE_DB_ID));
                    ((Callback) getActivity()).onItemSelected(uri);
                }
            }
        });

        return rootView;
    }

    // Saves the state of the movie list.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.movie_position_key), mPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        getLoaderManager().initLoader(GRID_LOADER_ID, savedInstance, this);
    }

    // Reads sort order setting
    private Uri getUriBySortOrderSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String movieList = settings.getString(getString(R.string.pref_movie_list_key),
                getString(R.string.pref_movie_list_default));

        if(movieList.equals(getString(R.string.pref_movie_list_favorite)))
            return MovieContract.MovieEntry.CONTENT_MOVIE_FAVORITE_URI;
        else if(movieList.equals(getString(R.string.pref_movie_list_top_rated)))
            return MovieContract.MovieEntry.CONTENT_MOVIE_TOP_RATED_URI;
        else
            return MovieContract.MovieEntry.CONTENT_MOVIE_POPULAR_URI;
    }

    // Checks if the device is online
    public boolean isOnline() {
        ConnectivityManager c = (ConnectivityManager) getActivity()
                .getSystemService(getContext().CONNECTIVITY_SERVICE);
        NetworkInfo n = c.getActiveNetworkInfo();

        return n!= null && n.isConnected();
    }

    // Update DB
    private void updateMovies() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Data uri depends on movie list selected in the settings
        Uri dataUri = getUriBySortOrderSettings();

        return new CursorLoader(getActivity(), dataUri, GRID_MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.
        mAdapter.swapCursor(data);
        GridView gv = (GridView) getActivity().findViewById(R.id.grid_view_movies);
        if(mPosition != GridView.INVALID_POSITION) {
            gv.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.
        mAdapter.swapCursor(null);
    }

    public void onMovieListSettingChanged() {
        //updateMovies();
        getLoaderManager().restartLoader(GRID_LOADER_ID, null, this);
    }

    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(Uri movieUri);
    }
}
