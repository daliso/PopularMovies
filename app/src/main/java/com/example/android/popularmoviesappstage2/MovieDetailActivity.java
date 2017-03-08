package com.example.android.popularmoviesappstage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.example.android.popularmoviesappstage2.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by dalisozuze on 11/02/2017.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private Movie movie;
    private TextView mOverviewDisplay;
    private TextView mTitle;
    private ImageView mPoster;
    private TextView mRelease;
    private TextView mRating;
    private boolean mIsFavorite;
    FloatingActionButton fabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

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
    }
}
