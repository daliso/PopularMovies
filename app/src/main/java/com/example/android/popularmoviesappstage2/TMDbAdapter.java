package com.example.android.popularmoviesappstage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dalisozuze on 04/02/2017.
 */

public class TMDbAdapter extends RecyclerView.Adapter<TMDbAdapter.TMDbAdapterViewHolder> {

    List<Movie> mMovieData;
    private ItemClickListener mClickListener;

    public TMDbAdapter(){
    }

    public class TMDbAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView moviePosterView;

        public TMDbAdapterViewHolder(View view) {
            super(view);
            moviePosterView = (ImageView) view.findViewById(R.id.iv_movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(mMovieData.get(getAdapterPosition()));
        }
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }

    @Override
    public TMDbAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TMDbAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TMDbAdapterViewHolder holder, int position) {
        Movie movieForThisPosition = mMovieData.get(position);
        Picasso.with(holder.moviePosterView.getContext()).load(movieForThisPosition.mPosterURL).into(holder.moviePosterView);
    }

    public void setMovieData(List<Movie> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(Movie movie);
    }
}


