package com.example.desk.popularmovies.data;


import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    final String IMAGE_BASE_URL =  "http://image.tmdb.org/t/p/w185";
    private String mPosterPath;
    private String mOverview;
    private String mReleaseDate;
    private String mId;
    private String mTitle;
    private String mVoteAverage;

    public Movie() {}

    public Movie(Parcel in) {
        this.mPosterPath        = in.readString();
        this.mOverview          = in.readString();
        this.mReleaseDate       = in.readString();
        this.mId                = in.readString();
        this.mTitle             = in.readString();
        this.mVoteAverage       = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mVoteAverage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setPosterPath(String posterPath) {
        if(posterPath.startsWith("http")){
            mPosterPath = posterPath;
        } else {
            // construct absolute path
            mPosterPath = IMAGE_BASE_URL + posterPath;
        }
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getTitle() {return mTitle;}

    public String getReleaseDate() {return mReleaseDate;}

    public String getVoteAverage() {return mVoteAverage;}

    public String getId() {return mId;}

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_DB_ID, mId);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, mPosterPath);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, mOverview);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, mTitle);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, mReleaseDate);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, mVoteAverage);

        return cv;
    }

    @Override
    public String toString() {
        return mTitle + ", " + mReleaseDate + " " + mVoteAverage + " " + mPosterPath + " " +
                mOverview;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Movie)) return false;
        // two movies are equal if their IDs (MovieDB ID) are equal
        Movie m = (Movie) o;
        return m.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(this.getId()) + 7;
    }
}
