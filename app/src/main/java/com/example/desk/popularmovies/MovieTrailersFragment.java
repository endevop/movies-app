package com.example.desk.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.desk.popularmovies.data.MovieContract;

public class MovieTrailersFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{
    static final String DETAIL_URI = "URI";
    static final String TAG = "MTF_TAG";
    private static final int TRAILER_LOADER_ID = 300;
    private Uri mUri;
    long mMovieId;
    private TrailerAdapter mTrailerAdapter;

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry.COLUMN_TRAILER_PATH
    };

    public static final int COLUMN_TRAILER_NAME = 1;
    public static final int COLUMN_TRAILER_PATH = 2;

    public MovieTrailersFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DETAIL_URI);
            mMovieId = Long.parseLong(MovieContract.getMovieIdFromUri(mUri));
        }
        Uri uriForTrailers = MovieContract.TrailerEntry.buildMovieTrailerUri(mMovieId);
        Cursor cur = getActivity().getContentResolver().query(uriForTrailers,
                null, null, null, null);
        mTrailerAdapter = new TrailerAdapter(getActivity(), cur, 0);
        View view = inflater.inflate(R.layout.fragment_trailers_listview, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listview_trailers);
        listView.setAdapter(mTrailerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null) {
                    String trailerUrl = cursor.getString(COLUMN_TRAILER_PATH);
                    openTrailerUrl(trailerUrl);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        getLoaderManager().initLoader(TRAILER_LOADER_ID, savedInstance, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri defaultUri = MovieContract.TrailerEntry.buildMovieTrailerUri(mMovieId);

        return new CursorLoader(getActivity(),
                defaultUri,
                TRAILER_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mTrailerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mTrailerAdapter.swapCursor(null);
    }

    private void openTrailerUrl(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
