package com.example.android.popularmoviesappstage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

/**
 * Created by dalisozuze on 09/02/2017.
 */

public class TMDbVideoAdapter extends RecyclerView.Adapter<TMDbVideoAdapter.TMDbVideoAdapterViewHolder> {

    List<VideoTrailer> mVideoData;
    private ItemClickListener mClickListener;

    public TMDbVideoAdapter(){
    }

    public class TMDbVideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView videoTitleView;

        public TMDbVideoAdapterViewHolder(View view) {
            super(view);
            videoTitleView = (TextView) view.findViewById(R.id.tv_movie_video_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(mVideoData.get(getAdapterPosition()));
        }
    }

    @Override
    public int getItemCount() {
        if (null == mVideoData) return 0;
        return mVideoData.size();
    }

    @Override
    public TMDbVideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_video_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TMDbVideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TMDbVideoAdapterViewHolder holder, int position) {
        VideoTrailer videoForThisPosition = mVideoData.get(position);
        holder.videoTitleView.setText(videoForThisPosition.mName);
    }

    public void setVideoData(List<VideoTrailer> videoData) {
        mVideoData = videoData;
        notifyDataSetChanged();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(VideoTrailer video);
    }
}


