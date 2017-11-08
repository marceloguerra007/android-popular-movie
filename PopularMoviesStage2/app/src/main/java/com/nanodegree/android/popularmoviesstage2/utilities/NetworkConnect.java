package com.nanodegree.android.popularmoviesstage2.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.nanodegree.android.popularmoviesstage2.model.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by marceloguerra on 15/07/2017.
 */

public class NetworkConnect {

    private static final String MOVIESLIST_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String PATH_ORDERBY_POPULAR = "popular";
    private static final String PATH_ORDERBY_TOPRATED = "top_rated";
    private static final String PARAM_API_KEY_NAME = "api_key";
    private static final String PARAM_API_KEY_VALUE = "it's need has TMDb user account to request an API key. See more at https://www.themoviedb.org.";
    private static final String PATH_VIDEOS = "videos";
    private static final String PATH_REVIEWS = "reviews";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com";
    private static final String YOUTUBE_PATH_WATCH = "watch";
    private static final String YOUTUBE_PARAM_NAME = "v";

    public static URL buildUrlMovieList(String orderBy) {
        String orderByUrl;

        if (orderBy.equalsIgnoreCase(Movie.MOVIESLIST_ORDERBY_TOPRATED))
            orderByUrl = PATH_ORDERBY_TOPRATED;
        else
            orderByUrl = PATH_ORDERBY_POPULAR;

        Uri builtUri = Uri.parse(MOVIESLIST_BASE_URL).buildUpon()
                .appendPath(orderByUrl)
                .appendQueryParameter(PARAM_API_KEY_NAME, PARAM_API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlVideos(int movieId) {

        Uri builtUri = Uri.parse(MOVIESLIST_BASE_URL).buildUpon()
                .appendPath((String.valueOf(movieId)))
                .appendPath(PATH_VIDEOS)
                .appendQueryParameter(PARAM_API_KEY_NAME, PARAM_API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrlReviews(int movieId) {

        Uri builtUri = Uri.parse(MOVIESLIST_BASE_URL).buildUpon()
                .appendPath((String.valueOf(movieId)))
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(PARAM_API_KEY_NAME, PARAM_API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static Uri buildUriVideoIntent(String paramKey) {

        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendPath(YOUTUBE_PATH_WATCH)
                .appendQueryParameter(YOUTUBE_PARAM_NAME, paramKey)
                .build();

        return builtUri;
    }

    public static boolean existsInternetConnection(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
