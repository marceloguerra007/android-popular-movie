package com.nanodegree.android.popularmoviesstage2.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.android.popularmoviesstage2.R;
import com.nanodegree.android.popularmoviesstage2.model.Review;

import java.util.ArrayList;

/**
 * Created by marceloguerra on 02/09/2017.
 */
public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {
    private TextView mAuthorText;
    private TextView mReviewText;
    private ArrayList<Review> mReviewDataList;

    public void setReviewDataList(ArrayList<Review> reviewDataList){
        mReviewDataList = reviewDataList;
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdListItem = R.layout.item_reviewlist;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIdListItem, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mReviewDataList == null)
            return 0;
        else
            return mReviewDataList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public ReviewViewHolder(View itemView) {
            super(itemView);

            mAuthorText = (TextView) itemView.findViewById(R.id.tv_author_review);
            mReviewText = (TextView) itemView.findViewById(R.id.tv_content_review);
        }

        public void bind(int index){
            Review review = mReviewDataList.get(index);

            mAuthorText.setText(review.getAuthor());
            mReviewText.setText(review.getContent());
        }
    }
}