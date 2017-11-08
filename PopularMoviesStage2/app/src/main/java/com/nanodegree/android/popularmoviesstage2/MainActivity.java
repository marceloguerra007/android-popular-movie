package com.nanodegree.android.popularmoviesstage2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nanodegree.android.popularmoviesstage2.data.PopularMoviesContract;
import com.nanodegree.android.popularmoviesstage2.data.PopularMoviesContract.MovieEntry;
import com.nanodegree.android.popularmoviesstage2.model.Movie;
import com.nanodegree.android.popularmoviesstage2.utilities.JsonUtils;
import com.nanodegree.android.popularmoviesstage2.utilities.MovieListAdapter;
import com.nanodegree.android.popularmoviesstage2.utilities.NetworkConnect;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.MovieListAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieListAdapter mMovieListAdapter;
    private TextView mStatusMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private ArrayList<Movie> mMovieList;

    private String mLastErrorMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movielist);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mStatusMessageDisplay = (TextView) findViewById(R.id.tv_status_message_display);

        GridLayoutManager gridLayoutManager;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        else
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setReverseLayout(false);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieListAdapter = new MovieListAdapter(this);

        mRecyclerView.setAdapter(mMovieListAdapter);        

        if (savedInstanceState == null || !savedInstanceState.containsKey(Movie.MOVIELIST_STATE_KEY)) {
            loadMovieListFromInternet();
        }
        else {
            loadMovieListFromSavedInstance(savedInstanceState);
        }
    }

    private void loadMovieListFromInternet(){
        mMovieListAdapter.setMovieDataList(null);

        if (NetworkConnect.existsInternetConnection(this))
            new FetchMovieListTask().execute(getOrderByPreference());
        else
            showStatusMessage(getResources().getString(R.string.error_internet_connection_message));
    }

    private void loadMovieListFromSavedInstance(Bundle savedInstanceState) {
        mMovieList = savedInstanceState.getParcelableArrayList(Movie.MOVIELIST_STATE_KEY);
        String errorMessage = savedInstanceState.getString(Movie.ERRORMESSAGE_STATE_KEY);

        if (mMovieList != null) {
            mMovieListAdapter.setMovieDataList(mMovieList);
            showDataView();
        }
        else {
            if (errorMessage != null)
                showStatusMessage(errorMessage);
            else
                showStatusMessage(getResources().getString(R.string.error_data_access_message));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Movie.MOVIELIST_STATE_KEY, mMovieList);
        outState.putString(Movie.ERRORMESSAGE_STATE_KEY, mLastErrorMessage);
        super.onSaveInstanceState(outState);
    }

    private void showDataView(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mStatusMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showStatusMessage(String errorMessage){
        mRecyclerView.setVisibility(View.INVISIBLE);

        mStatusMessageDisplay.setText(errorMessage);
        mStatusMessageDisplay.setVisibility(View.VISIBLE);

        mLastErrorMessage = errorMessage;
    }

    private void openSettings(){
        Intent settingsIntent = new Intent(this, com.nanodegree.android.popularmoviesstage2.SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private String getOrderByPreference(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderByChosen = sharedPrefs.getString(
                getString(R.string.settings_orderby_key),
                getString(R.string.settings_orderby_defaultvalue));

        return orderByChosen;
    }

    private void openDetailsActivity(Movie movie){
        Intent DetailsIntent = new Intent(MainActivity.this, com.nanodegree.android.popularmoviesstage2.DetailsActivity.class);
        DetailsIntent.putExtra(Movie.MOVIEDETAIL_EXTRA_KEY, movie);

        startActivity(DetailsIntent);
    }

    public ArrayList<Movie> loadMovieListFromAPI(String orderBy){
        URL url = NetworkConnect.buildUrlMovieList(orderBy);
        ArrayList<Movie> movieList;

        try {
            String jsonMoviesList = NetworkConnect.getResponseFromHttpUrl(url);
            movieList = JsonUtils.getMovieListFromJson(jsonMoviesList);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("MainActivity","Error Data Access:" + e.getMessage());
            movieList = null;
        }

        return movieList;
    }

    public ArrayList<Movie> loadMovieListFromDB(){
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        try{
            Uri uriFavorite = PopularMoviesContract.MovieEntry.CONTENT_URI;
            Cursor cursor = getContentResolver().query(uriFavorite, null, null, null, null);

            if (cursor != null){
                if (cursor.moveToFirst()){
                    do {
                        Movie movie = new Movie();
                        int idIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
                        int titleIndex = cursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
                        int imagePathIndex = cursor.getColumnIndex(MovieEntry.COLUMN_IMAGE_PATH);
                        int synopsisIndex = cursor.getColumnIndex(MovieEntry.COLUMN_SYNOPSIS);
                        int ratingIndex = cursor.getColumnIndex(MovieEntry.COLUMN_RATING);
                        int releaseIndex = cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);

                        movie.setId(cursor.getInt(idIndex));
                        movie.setTitle(cursor.getString(titleIndex));
                        movie.setSynopsis(cursor.getString(synopsisIndex));
                        movie.setPosterPath(cursor.getString(imagePathIndex));
                        movie.setReleaseDate(cursor.getString(releaseIndex));
                        movie.setUserRating(cursor.getDouble(ratingIndex));

                        movieList.add(movie);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
            Log.i("MainActivity","Error Data Load:" + e.getMessage());
            movieList = null;
        }

        return movieList;
    }

    @Override
    public void clickMovieListAdapter(Movie movieSelected) {
        openDetailsActivity(movieSelected);
    }

    public class FetchMovieListTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            String orderBy = params[0];

            if (orderBy.equalsIgnoreCase(Movie.MOVIESLIST_ORDERBY_FAVORITE))
                return loadMovieListFromDB();
            else
                return loadMovieListFromAPI(orderBy);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movieList != null){
                if (movieList.size() > 0){
                    showDataView();
                    mMovieListAdapter.setMovieDataList(movieList);
                    mMovieList = movieList;
                }else{
                    showStatusMessage(getResources().getString(R.string.status_no_favorite_movies));
                }
            }
            else
                showStatusMessage(getResources().getString(R.string.error_data_access_message));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }
        else if (id == R.id.action_refresh){
            loadMovieListFromInternet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
