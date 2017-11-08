package com.nanodegree.android.popularmoviesstage2;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.android.popularmoviesstage2.data.PopularMoviesContract;
import com.nanodegree.android.popularmoviesstage2.data.PopularMoviesContract.MovieEntry;
import com.nanodegree.android.popularmoviesstage2.model.Movie;
import com.nanodegree.android.popularmoviesstage2.model.Review;
import com.nanodegree.android.popularmoviesstage2.utilities.JsonUtils;
import com.nanodegree.android.popularmoviesstage2.utilities.NetworkConnect;
import com.nanodegree.android.popularmoviesstage2.utilities.ReviewListAdapter;
import com.nanodegree.android.popularmoviesstage2.utilities.VideoListAdapter;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity implements VideoListAdapter.VideoListAdapterOnClickHandler {

    private ImageView mSmallPosterImage;
    private TextView mTitleText;
    private TextView mReleaseText;
    private TextView mRatingText;
    private TextView mSynopsis;
    private Movie movieDetail;
    private RecyclerView mVideoRecyclerView;
    private VideoListAdapter mVideoListAdapter;
    private RecyclerView mReviewRecyclerView;
    private ReviewListAdapter mReviewListAdapter;
    private Button mBtLoadReview;
    private TextView mReviewTitleText;
    private Button mBtFavorite;
    private ProgressBar mPbLoadingReview;

    private static final int FAVORITE_MARKED = 1;
    private static final int FAVORITE_UNMARKED = 0;
    private boolean isFavoriteMovie = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mSmallPosterImage = (ImageView) findViewById(R.id.iv_smallposter);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mReleaseText = (TextView) findViewById(R.id.tv_release);
        mRatingText = (TextView) findViewById(R.id.tv_rating);
        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);

        movieDetail = (Movie) getIntent().getParcelableExtra(Movie.MOVIEDETAIL_EXTRA_KEY);

        bindViews();

        mVideoRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_videolist);

        GridLayoutManager gridLayoutManager;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
            gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        }
        else {
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        }

        gridLayoutManager.setReverseLayout(false);
        mVideoRecyclerView.setLayoutManager(gridLayoutManager);

        mVideoRecyclerView.setHasFixedSize(true);
        mVideoListAdapter = new VideoListAdapter(this);
        mVideoRecyclerView.setAdapter(mVideoListAdapter);

        mBtFavorite = (Button) findViewById(R.id.bt_favorite);
        mBtFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavoriteMovie(movieDetail);
            }
        });
        checkFavoriteMovie();

        loadVideos();

        mPbLoadingReview = (ProgressBar) findViewById(R.id.pb_loading_review);
        mReviewTitleText = (TextView) findViewById(R.id.tv_review_title);
        mBtLoadReview = (Button) findViewById(R.id.bt_loadreview);
        mBtLoadReview.setVisibility(View.VISIBLE);
        mBtLoadReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadReviews();
            }
        });
    }

    private void bindViews() {
        if (movieDetail != null) {
            Picasso.with(getApplicationContext()).load(getImageURL()).into(mSmallPosterImage);
            mTitleText.setText(movieDetail.getTitle());
            mReleaseText.setText(movieDetail.getReleaseDate().substring(0, 4));
            mRatingText.setText(String.valueOf(movieDetail.getUserRating()));
            mSynopsis.setText(movieDetail.getSynopsis());
        }
    }

    private String getImageURL() {
        return Movie.MOVIE_BASEURL_IMAGEPOSTER + movieDetail.getPosterPath();
    }

    private void loadVideos() {
        mVideoListAdapter.setVideoList(null);

        if (NetworkConnect.existsInternetConnection(this))
            new FetchVideoListTask().execute(String.valueOf(movieDetail.getId()));
        else
            Toast.makeText(this, getResources().getString(R.string.error_internet_connection_message), Toast.LENGTH_SHORT).show();
    }

    private void checkFavoriteMovie(){
        int movieId = movieDetail.getId();
        Uri uriFavorite = MovieEntry.CONTENT_URI;
        String[] proj = {MovieEntry.COLUMN_MOVIE_ID};
        String where = MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] whereArgs = {String.valueOf(movieId)};
        Cursor cursor = getContentResolver().query(uriFavorite, proj, where, whereArgs, null);

        if ((cursor != null) && (cursor.getCount() > 0)){
            setStateFavoriteMovie(FAVORITE_MARKED);
        }else{
            setStateFavoriteMovie(FAVORITE_UNMARKED);
        }

        cursor.close();
    }

    private void setStateFavoriteMovie(int state){
        switch (state) {
            case FAVORITE_MARKED:
                mBtFavorite.setText(getResources().getString(R.string.details_unmark_favorite_button));
                isFavoriteMovie = true;
                break;
            case FAVORITE_UNMARKED:
                mBtFavorite.setText(getResources().getString(R.string.details_mark_favorite_button));
                isFavoriteMovie = false;
                break;
        }
    }

    private void setFavoriteMovie(Movie movie){
        Uri uri = PopularMoviesContract.MovieEntry.CONTENT_URI;
        String where = PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] whereArgs = {String.valueOf(movie.getId())};

        if (isFavoriteMovie){
            int rowsDeleted = getContentResolver().delete(uri, where, whereArgs);

            if (rowsDeleted > 0)
                setStateFavoriteMovie(FAVORITE_UNMARKED);
        }else{
            ContentValues values = new ContentValues();
            values.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MovieEntry.COLUMN_IMAGE_PATH, movie.getPosterPath());
            values.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieEntry.COLUMN_RATING, movie.getUserRating());
            values.put(MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());

            Uri uriInserted = getContentResolver().insert(uri, values);

            if (uriInserted != null)
                setStateFavoriteMovie(FAVORITE_MARKED);
        }
    }

    private void loadReviews(){
        mBtLoadReview.setVisibility(View.GONE);
        mReviewTitleText.setVisibility(View.VISIBLE);
        mPbLoadingReview.setVisibility(View.VISIBLE);

        if (NetworkConnect.existsInternetConnection(this)) {
            mReviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_review);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            gridLayoutManager.setReverseLayout(false);
            mReviewRecyclerView.setLayoutManager(gridLayoutManager);

            mReviewRecyclerView.setHasFixedSize(true);

            mReviewListAdapter = new ReviewListAdapter();
            mReviewListAdapter.setReviewDataList(null);

            mReviewRecyclerView.setAdapter(mReviewListAdapter);

            new FetchMovieReviewTask().execute(String.valueOf(movieDetail.getId()));
        }
        else {
            mPbLoadingReview.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.error_internet_connection_message), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void clickVideoListAdapter(String videoKey) {
        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
        videoIntent.setData(NetworkConnect.buildUriVideoIntent(videoKey));
        startActivity(videoIntent);
    }

    public class FetchVideoListTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> videoList = null;

            try {
                int movieId = Integer.parseInt(params[0]);

                URL url = NetworkConnect.buildUrlVideos(movieId);

                String jsonVideoList = NetworkConnect.getResponseFromHttpUrl(url);

                videoList = JsonUtils.getMovieVideosFromJson(jsonVideoList);

            } catch (Exception e) {
                e.printStackTrace();

                Log.i("MainActivity", "Error Data Access:" + e.getMessage());

                videoList = null;
            }

            return videoList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> videoList) {
            if (videoList != null)
                mVideoListAdapter.setVideoList(videoList);
        }
    }

    public class FetchMovieReviewTask extends AsyncTask<String, Void, ArrayList<Review>>{

        @Override
        protected ArrayList<Review> doInBackground(String... params) {
            ArrayList<Review> reviewList;

            try {
                int movieId = Integer.parseInt(params[0]);

                URL url = NetworkConnect.buildUrlReviews(movieId);

                String jsonMovieReview = NetworkConnect.getResponseFromHttpUrl(url);

                reviewList = JsonUtils.getReviewMovieFromJson(jsonMovieReview);
            } catch (Exception e) {
                e.printStackTrace();

                Log.i("DetailsActivity", "Error Data Access:" + e.getMessage());

                reviewList = null;
            }

            return reviewList;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews){
            if (reviews != null){
                if (reviews.size() == 0){
                    mReviewTitleText.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.details_message_noreviews),Toast.LENGTH_SHORT).show();
                }
                else {
                    mReviewRecyclerView.setVisibility(View.VISIBLE);
                    mReviewListAdapter.setReviewDataList(reviews);
                }
            }

            mPbLoadingReview.setVisibility(View.GONE);
        }
    }
}