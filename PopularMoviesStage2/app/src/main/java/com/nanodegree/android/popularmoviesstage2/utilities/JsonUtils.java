package com.nanodegree.android.popularmoviesstage2.utilities;

import com.nanodegree.android.popularmoviesstage2.model.Movie;
import com.nanodegree.android.popularmoviesstage2.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by marceloguerra on 18/07/2017.
 */
public final class JsonUtils {

    public static final String MockJson = "{\"page\":1,\"total_results\":6577,\"total_pages\":329,\"results\":[{\"vote_count\":398,\"id\":19404,\"video\":false,\"vote_average\":8.9,\"title\":\"Dilwale Dulhania Le Jayenge\",\"popularity\":3.781376,\"poster_path\":\"\\/2gvbZMtV1Zsl7FedJa5ysbpBx2G.jpg\",\"original_language\":\"hi\",\"original_title\":\"Dilwale Dulhania Le Jayenge\",\"genre_ids\":[35,18,10749],\"backdrop_path\":\"\\/nl79FQ8xWZkhL3rDr1v2RFFR6J0.jpg\",\"adult\":false,\"overview\":\"Raj is a rich, carefree, happy-go-lucky second generation NRI. Simran is the daughter of Chaudhary Baldev Singh, who in spite of being an NRI is very strict about adherence to Indian values. Simran has left for India to be married to her childhood fiancé. Raj leaves for India with a mission at his hands, to claim his lady love under the noses of her whole family. Thus begins a saga.\",\"release_date\":\"1995-10-20\"},{\"vote_count\":7570,\"id\":278,\"video\":false,\"vote_average\":8.5,\"title\":\"The Shawshank Redemption\",\"popularity\":10.308194,\"poster_path\":\"\\/9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg\",\"original_language\":\"en\",\"original_title\":\"The Shawshank Redemption\",\"genre_ids\":[18,80],\"backdrop_path\":\"\\/j9XKiZrVeViAixVRzCta7h1VU9W.jpg\",\"adult\":false,\"overview\":\"Framed in the 1940s for the double murder of his wife and her lover, upstanding banker Andy Dufresne begins a new life at the Shawshank prison, where he puts his accounting skills to work for an amoral warden. During his long stretch in prison, Dufresne comes to be admired by the other inmates -- including an older prisoner named Red -- for his integrity and unquenchable sense of hope.\",\"release_date\":\"1994-09-23\"}]}";
    private static final String RESULTS_ARRAY = "results";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String OVERVIEW = "overview";
    private static final String VIDEO_KEY = "key";
    private static final String REVIEW_ID = "id";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";

    public static ArrayList<Movie> getMovieListFromJson(String json) throws JSONException {

        ArrayList<Movie> movieList = new ArrayList<Movie>();

        JSONObject objJson = new JSONObject(json);

        JSONArray resultsList = objJson.getJSONArray(RESULTS_ARRAY);

        for (int i = 0; i < resultsList.length(); i++) {
            JSONObject objResults = resultsList.getJSONObject(i);

            Movie movie = new Movie();
            movie.setId(objResults.optInt(ID));
            movie.setTitle(objResults.optString(TITLE));
            movie.setPosterPath(objResults.optString(POSTER_PATH));
            movie.setReleaseDate(objResults.optString(RELEASE_DATE));
            movie.setUserRating(objResults.optDouble(VOTE_AVERAGE));
            movie.setSynopsis(objResults.optString(OVERVIEW));
            movieList.add(movie);
        }

        return movieList;
    }

    public static ArrayList<String> getMovieVideosFromJson(String json) throws JSONException {
        ArrayList<String> videoList = new ArrayList<String>();

        JSONObject objJson = new JSONObject(json);

        JSONArray resultsList = objJson.getJSONArray(RESULTS_ARRAY);

        for (int i = 0; i < resultsList.length(); i++) {
            JSONObject itemResults = resultsList.getJSONObject(i);

            videoList.add(itemResults.getString(VIDEO_KEY));
        }

        return videoList;
    }

    public static ArrayList<Review> getReviewMovieFromJson(String json) throws JSONException {
        ArrayList<Review> reviewList = new ArrayList<Review>();

        JSONObject objJson = new JSONObject(json);

        JSONArray resultsList = objJson.getJSONArray(RESULTS_ARRAY);

        for (int i = 0; i < resultsList.length(); i++) {
            JSONObject itemResults = resultsList.getJSONObject(i);

            Review review = new Review();
            review.setId(itemResults.getString(REVIEW_ID));
            review.setAuthor(itemResults.getString(REVIEW_AUTHOR));
            review.setContent(itemResults.getString(REVIEW_CONTENT).trim());

            reviewList.add(review);
        }

        return reviewList;
    }

}
