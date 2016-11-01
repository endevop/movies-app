package com.example.desk.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ReviewAdapter extends CursorAdapter {

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // author
        int reviewByFormat = R.string.format_review_by;
        String rawAuthorStr = cursor.getString(MovieReviewsFragment.COLUMN_REVIEW_AUTHOR);
        String reviewByStr = String.format(context.getString(reviewByFormat), rawAuthorStr);
        TextView authorView = (TextView) view.findViewById(R.id.list_item_author_textview);
        authorView.setText(reviewByStr);

        // content
        TextView contentView = (TextView) view.findViewById(R.id.list_item_content_textview);
        contentView.setText(cursor.getString(MovieReviewsFragment.COLUMN_REVIEW_CONTENT));
    }
}