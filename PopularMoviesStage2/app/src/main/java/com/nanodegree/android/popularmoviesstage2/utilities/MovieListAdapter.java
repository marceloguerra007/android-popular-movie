package com.nanodegree.android.popularmoviesstage2.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nanodegree.android.popularmoviesstage2.model.Movie;
import com.nanodegree.android.popularmoviesstage2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by marceloguerra on 17/07/2017.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ListMovieAdapterViewHolder> {

    private ArrayList<Movie> mMovieDataList;
    private ImageView mPosterImage;

    private MovieListAdapterOnClickHandler mClickHandler;

    public interface MovieListAdapterOnClickHandler{
        void clickMovieListAdapter(Movie movie);
    }

    public MovieListAdapter(MovieListAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public void setMovieDataList(ArrayList<Movie> movieDataList){
        mMovieDataList = movieDataList;
        notifyDataSetChanged();
    }

    @Override
    public MovieListAdapter.ListMovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdListItem = R.layout.item_movielist;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIdListItem, viewGroup, false);

        return new ListMovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListMovieAdapterViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mMovieDataList == null)
            return 0;
        else
            return mMovieDataList.size();
    }

    public class ListMovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;

        public ListMovieAdapterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mPosterImage = (ImageView) itemView.findViewById(R.id.iv_poster);

            mContext = itemView.getContext();
        }

        public void bind(int index){
            Picasso.with(mContext).load(getImageURL(mMovieDataList.get(index).getPosterPath())).into(mPosterImage);
        }

        private String getImageURL(String pathImage){
            return Movie.MOVIE_BASEURL_IMAGEPOSTER + pathImage;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            // verifica se a posição do Adapter existe.
            if(position != RecyclerView.NO_POSITION){
                mClickHandler.clickMovieListAdapter(mMovieDataList.get(position));
            }
        }
    }
}

