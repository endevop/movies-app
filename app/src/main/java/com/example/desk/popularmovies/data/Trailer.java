package com.example.desk.popularmovies.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;


public class Trailer implements Parcelable {
    final private String YOUTUBE_BASE_PATH = "https://www.youtube.com/watch?v=";
    final private String SITE_YOUTUBE = "youtube";
    private String mMovieId;
    private String mId;
    private String mName;
    private String mPath;

    public Trailer() {}

    public Trailer(Parcel in) {
        this.mMovieId        = in.readString();
        this.mId             = in.readString();
        this.mName           = in.readString();
        this.mPath           = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieId);
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static final Parcelable.Creator<Trailer> CREATOR
            = new Parcelable.Creator<Trailer>() {

        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public void setMovieId(String movieId) {mMovieId = movieId;}
    public void setId(String reviewId) {mId = reviewId;}
    public void setName(String author) {mName = author;}
    public void setPath(String path) {mPath = path;}
    public void setPath(String site, String key) {
        if(site.toLowerCase().equals(SITE_YOUTUBE))
            mPath = YOUTUBE_BASE_PATH + key;
    }

    public String getMovieId() {return mMovieId;}
    public String getId() {return mId;}
    public String getName() {return mName;}
    public String getPath() {return mPath;}

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_DB_ID, mId);
        cv.put(MovieContract.TrailerEntry.COLUMN_MOVIE_DB_ID, mMovieId);
        cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, mName);
        cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_PATH, mPath);

        return cv;
    }

    @Override
    public String toString() {
        return mMovieId + ", " + mId + " " + mName + " " + mPath;
    }
}
