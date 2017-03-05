package com.example.android.popularmoviesappstage1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

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

                Context context = this;
                Picasso.with(context).load(movie.mPosterURL).into(mPoster);
            }
        }
    }
}
