package com.nanodegree.android.popularmoviesstage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nanodegree.android.popularmoviesstage2.data.PopularMoviesContract.MovieEntry;

/**
 * Created by marceloguerra on 02/09/2017.
 */
public class PopularMoviesProvider extends ContentProvider {

    private static final int FAVORITE_MOVIES = 100;
    private static final int FAVORITE_MOVIE_BY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY, PopularMoviesContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        sUriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY, PopularMoviesContract.PATH_FAVORITE_MOVIES + "/#", FAVORITE_MOVIE_BY_ID);
    }

    private PopularMoviesDbHelper mDbHelper;

    public static final String LOG_TAG = PopularMoviesProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        mDbHelper = new PopularMoviesDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                cursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case FAVORITE_MOVIE_BY_ID:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = db.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                return MovieEntry.CONTENT_LIST_TYPE;
            case FAVORITE_MOVIE_BY_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                return insertFavoriteMovie(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertFavoriteMovie(Uri uri, ContentValues values){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long movieId = db.insert(MovieEntry.TABLE_NAME, null, values);

        if (movieId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, movieId);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                return updateFavoriteMovie(uri, values, selection, selectionArgs);
            case FAVORITE_MOVIE_BY_ID:
                String where = MovieEntry._ID + "=?";
                String[] whereArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                return updateFavoriteMovie(uri, values, where, whereArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateFavoriteMovie(Uri uri, ContentValues values, String where, String[] whereArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, where, whereArgs);

        if (rowsUpdated > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                return deleteFavoriteMovie(uri, selection, selectionArgs);
            case FAVORITE_MOVIE_BY_ID:
                String where = MovieEntry._ID + "=?";
                String[] whereArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                return deleteFavoriteMovie(uri, where, whereArgs);
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
    }

    private int deleteFavoriteMovie(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(MovieEntry.TABLE_NAME, where, whereArgs);

        if (rowsDeleted > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }
}
