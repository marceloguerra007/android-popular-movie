package com.nanodegree.android.popularmoviesstage2.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.android.popularmoviesstage2.R;

import java.util.ArrayList;

/**
 * Created by marcelo on 29/08/2017.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListAdapterViewHolder> {

    private ArrayList<String> mVideoList;
    private TextView mTvName;

    private VideoListAdapterOnClickHandler mClickHandler;

    public interface VideoListAdapterOnClickHandler{
        void clickVideoListAdapter(String videoKey);
    }

    public VideoListAdapter(VideoListAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public void setVideoList(ArrayList<String> videoList){
        mVideoList = videoList;
        notifyDataSetChanged();
    }

    @Override
    public VideoListAdapter.VideoListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdListItem = R.layout.item_videolist;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIdListItem, viewGroup, false);

        return new VideoListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoListAdapterViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mVideoList == null)
            return 0;
        else
            return mVideoList.size();
    }

    public class VideoListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;

        public VideoListAdapterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvName = (TextView) itemView.findViewById(R.id.tv_nametrailer);

            mContext = itemView.getContext();
        }

        public void bind(int index){
            String videoName = mContext.getResources().getString(R.string.details_item_trailer_title);
            videoName+= " " + String.valueOf(index+1);
            mTvName.setText(videoName);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            // verifica se a posição do Adapter existe.
            if(position != RecyclerView.NO_POSITION){
                mClickHandler.clickVideoListAdapter(mVideoList.get(position));
            }
        }
    }
}
