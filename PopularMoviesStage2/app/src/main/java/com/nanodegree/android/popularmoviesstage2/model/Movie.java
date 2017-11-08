package com.nanodegree.android.popularmoviesstage2.model;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by marceloguerra on 15/07/2017.
 */

@org.parceler.Parcel
    public class Movie implements Parcelable {

    public static final String MOVIESLIST_ORDERBY_POPULAR = "1";
    public static final String MOVIESLIST_ORDERBY_TOPRATED = "2";
    public static final String MOVIESLIST_ORDERBY_FAVORITE = "3";

    public static final String MOVIE_BASEURL_IMAGEPOSTER = "http://image.tmdb.org/t/p/w185/";

    public static final String MOVIEDETAIL_EXTRA_KEY = "MovieDetailExtra";
    public static final String MOVIELIST_STATE_KEY = "movieliststatekey";
    public static final String ERRORMESSAGE_STATE_KEY = "errormessagestatekey";

    private int id;
    private String title;
    private String posterPath;
    private String synopsis; //called overview in the api
    private double userRating; //called vote_average in the api)
    private String releaseDate;

    public Movie (){

    }

    public Movie (Parcel in){
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        synopsis = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getTitle());
        dest.writeString(getPosterPath());
        dest.writeString(getSynopsis());
        dest.writeDouble(getUserRating());
        dest.writeString(getReleaseDate());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
