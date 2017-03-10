package com.example.android.popularmoviesappstage2;

import android.database.Cursor;

import com.example.android.popularmoviesappstage2.data.MovieContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dalisozuze on 11/02/2017.
 */

public class Movie implements Serializable {

    public String mTitle;
    public String mPosterURL;
    public String mOverview;
    public String mUserRating;
    public String mReleaseDate;
    public String mTMDBId;

    public Movie(Map<String,String> movieData){
        mTitle = movieData.get("title");
        mPosterURL = movieData.get("poster");
        mOverview = movieData.get("overview");
        mUserRating = movieData.get("rating");
        mReleaseDate = movieData.get("releaseDate");
        mTMDBId = movieData.get("tmdbId");
    }
    public static List<Movie> createMovies(List<Map<String,String>> moviesCollection){

        List<Movie> movies = new ArrayList<>();

        for(Map<String,String> movieItem: moviesCollection){
            movies.add(new Movie(movieItem));
        }
        return movies;
    }


    public static List<Movie> createMovies(Cursor c){

        List<Movie> movies = new ArrayList<>();

        int posterIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
        int ratingIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
        int releaseIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE);
        int synopsisIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
        int titleIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int tmdbIdIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TMDB_ID);

        while(c.moveToNext()){
            Map<String,String> movieMap = new HashMap<>();
            movieMap.put("title",c.getString(titleIndex));
            movieMap.put("poster",c.getString(posterIndex));
            movieMap.put("overview",c.getString(synopsisIndex));
            movieMap.put("rating",c.getString(ratingIndex));
            movieMap.put("releaseDate",c.getString(releaseIndex));
            movieMap.put("tmdbId",c.getString(tmdbIdIndex));
            movies.add(new Movie(movieMap));

        }

        return movies;
    }

}
