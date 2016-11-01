package com.example.desk.popularmovies.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private String mMovieId;
    private String mId;
    private String mAuthor;
    private String mContent;

    public Review() {}

    public Review(Parcel in) {
        this.mMovieId        = in.readString();
        this.mId             = in.readString();
        this.mAuthor         = in.readString();
        this.mContent        = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieId);
        dest.writeString(mId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static final Parcelable.Creator<Review> CREATOR
            = new Parcelable.Creator<Review>() {

        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public void setMovieId(String movieId) {mMovieId = movieId;}
    public void setId(String reviewId) {mId = reviewId;}
    public void setAuthor(String author) {mAuthor = author;}
    public void setContent(String content) {mContent = content;}

    public String getMovieId() {return mMovieId;}
    public String getId() {return mId;}
    public String getAuthor() {return mAuthor;}
    public String getContent() {return mContent;}

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_DB_ID, mId);
        cv.put(MovieContract.ReviewEntry.COLUMN_MOVIE_DB_ID, mMovieId);
        cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, mAuthor);
        cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, mContent);

        return cv;
    }

    @Override
    public String toString() {
        return mMovieId + ", " + mId + " " + mAuthor + " " + mContent;
    }
}
