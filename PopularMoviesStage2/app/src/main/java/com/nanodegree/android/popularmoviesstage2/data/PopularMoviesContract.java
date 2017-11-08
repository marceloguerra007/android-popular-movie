package com.nanodegree.android.popularmoviesstage2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by marceloguerra on 02/09/2017.
 */
public class PopularMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.nanodegree.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE_MOVIES = "favoritemovies";

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favoritemovie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_PATH = "posterpath";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIES)
                .build();

        /**
         * O tipo MIME do {@link #CONTENT_URI} para uma lista de filmes favoritos.
         *  Resultado da constante: vnd.android.cursor.dir/com.nanodegree.android.popularmovies/favoritemovies
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;

        /**
         * O tipo MIME do {@link #CONTENT_URI} para um único filme favorito.
         * Resultado da constante: vnd.android.cursor.item/com.nanodegree.android.popularmovies/favoritemovies
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;
    }
}
