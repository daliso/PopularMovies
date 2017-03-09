package com.example.android.popularmoviesappstage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.example.android.popularmoviesappstage2.data.MovieContract;
import com.example.android.popularmoviesappstage2.networkutils.TMDbNetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

/**
 * Created by dalisozuze on 11/02/2017.
 */
public class MovieDetailActivity extends AppCompatActivity implements TMDbVideoAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<List<VideoTrailer>>{

    private Movie movie;
    private TextView mOverviewDisplay;
    private TextView mTitle;
    private ImageView mPoster;
    private TextView mRelease;
    private TextView mRating;
    private boolean mIsFavorite;
    FloatingActionButton fabButton;

    private RecyclerView mRecyclerViewVideos;
    // private RecyclerView mRecyclerViewReviews;
    private TMDbVideoAdapter mTMDbVideoAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MOVIE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mRecyclerViewVideos = (RecyclerView) findViewById(R.id.recyclerview_movie_videos);
        // mRecyclerViewReviews = (RecyclerView) findViewById(R.id.recyclerview_movie_reviews);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mTMDbVideoAdapter = new TMDbVideoAdapter();
        mTMDbVideoAdapter.setClickListener(this);

        LinearLayoutManager layoutManagerVideo = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        LinearLayoutManager layoutManagerReview = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mRecyclerViewVideos.setLayoutManager(layoutManagerVideo);
        // mRecyclerViewVideos.setHasFixedSize(true);

        // mRecyclerViewReviews.setLayoutManager(layoutManagerReview);
        // mRecyclerViewReviews.setHasFixedSize(true);


        mTMDbVideoAdapter = new TMDbVideoAdapter();
        mTMDbVideoAdapter.setClickListener(this);
        mRecyclerViewVideos.setAdapter(mTMDbVideoAdapter);
        // mRecyclerViewReviews.setAdapter(mTMDbVideoAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mOverviewDisplay = (TextView) findViewById(R.id.tv_display_movie_detail);
        mTitle = (TextView) findViewById(R.id.tv_display_movie_title);
        mRelease = (TextView) findViewById(R.id.tv_release_date);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mPoster = (ImageView) findViewById(R.id.iv_detail_poster);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("Movie")) {
                movie = (Movie) intentThatStartedThisActivity.getSerializableExtra("Movie");
                mOverviewDisplay.setText(movie.mOverview);
                mTitle.setText(movie.mTitle);
                mRating.setText(movie.mUserRating);
                mRelease.setText(movie.mReleaseDate);

                final Context context = this;
                Picasso.with(context).load(movie.mPosterURL).into(mPoster);

                fabButton = (FloatingActionButton) findViewById(R.id.fab);
                updateFavButton();

                fabButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mIsFavorite != true) {

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.mPosterURL);
                            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.mUserRating);
                            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, movie.mReleaseDate);
                            contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.mOverview);
                            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.mTitle);
                            contentValues.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, movie.mTMDBId);

                            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                            if (uri != null) {
                                Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(context, "Unable to Add to Favorites", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                        else {

                            String[] args = {movie.mTMDBId};
                            int numDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movie.mTMDBId).build(),null,null);

                            if(numDeleted > 0){
                                Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }
                        updateFavButton();
                    }
                });

            }
        }


        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<VideoTrailer>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<VideoTrailer>>(this) {

            List<VideoTrailer> mMovieVideoData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieVideoData != null) {
                    deliverResult(mMovieVideoData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<VideoTrailer> loadInBackground() {

                try {

                    List<Map<String,String>> videosCollection = null;

                    videosCollection = TMDbNetworkUtils.getMovieVideos(movie.mTMDBId);

                    List<VideoTrailer> movieVideoData = null;

                    if (videosCollection != null) {
                        movieVideoData = VideoTrailer.createVideos(videosCollection);
                    }
                    else {
                        showErrorMessage();
                    }

                    return movieVideoData;

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }

            }

            public void deliverResult(List<VideoTrailer> data) {
                mMovieVideoData = data;
                super.deliverResult(data);
            }
        };

    }

    private void showVideoTrailerView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerViewVideos.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerViewVideos.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<List<VideoTrailer>> loader, List<VideoTrailer> data) {
        // Update the data that the adapter uses to create ViewHolders
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        showVideoTrailerView();
        mTMDbVideoAdapter.setVideoData(data);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<VideoTrailer>> loader) {
        mTMDbVideoAdapter.setVideoData(null);
    }


    @Override
    public void onItemClick(VideoTrailer video) {
        Context context = this;
//        Class destinationClass = MovieDetailActivity.class;
//        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
//        intentToStartDetailActivity.putExtra("Movie", movie);
//        startActivity(intentToStartDetailActivity);
        Toast.makeText(context, "Just clicked on video trailer: "+video.mName, Toast.LENGTH_SHORT)
                .show();
    }


    private void updateFavButton(){
        String[] args = {movie.mTMDBId};
        Cursor favC = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,"tmdbid=?",args,null);

        mIsFavorite = favC.moveToNext();

        if(mIsFavorite == true) {
            fabButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
        }
        else {
            fabButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorFavOff));
        }

        favC.close();
    }
}
