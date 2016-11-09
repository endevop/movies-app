package com.example.desk.popularmovies;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewAdapter extends CursorAdapter {
    public ImageView mPosterView;

    public ImageViewAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item_movie, parent, false);
        mPosterView = (ImageView) view.findViewById(R.id.movie_poster);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // get the movie poster path
        String posterPath = cursor.getString(MoviesGridFragment.COLUMN_MOVIE_POSTER_PATH);

        Picasso.with(context)
                .load(posterPath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into((ImageView) view);
        ViewCompat.setTransitionName(view, "poster" + cursor.getPosition());
    }
}
