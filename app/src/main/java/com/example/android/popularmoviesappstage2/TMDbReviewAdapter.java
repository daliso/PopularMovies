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

public class TMDbReviewAdapter extends RecyclerView.Adapter<TMDbReviewAdapter.TMDbReviewAdapterViewHolder> {

    List<MovieReview> mReviewData;
    private ItemClickListener mClickListener;

    public TMDbReviewAdapter(){
    }

    public class TMDbReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView movieReviewContentView;
        public final TextView movieReviewAuthorView;

        public TMDbReviewAdapterViewHolder(View view) {
            super(view);
            movieReviewContentView = (TextView) view.findViewById(R.id.tv_movie_review);
            movieReviewAuthorView = (TextView) view.findViewById(R.id.tv_movie_review_author);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onReviewItemClick(mReviewData.get(getAdapterPosition()));
        }
    }

    @Override
    public int getItemCount() {
        if (null == mReviewData) return 0;
        return mReviewData.size();
    }

    @Override
    public TMDbReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TMDbReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TMDbReviewAdapterViewHolder holder, int position) {
        MovieReview reviewForThisPosition = mReviewData.get(position);
        holder.movieReviewAuthorView.setText(reviewForThisPosition.mAuthor);
        int clip = Math.min(300,reviewForThisPosition.mContent.length());
        holder.movieReviewContentView.setText(reviewForThisPosition.mContent.substring(0,clip)+"...");
    }

    public void setReviewData(List<MovieReview> reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onReviewItemClick(MovieReview video);
    }
}


