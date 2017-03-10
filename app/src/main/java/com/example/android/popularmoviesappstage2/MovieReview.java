package com.example.android.popularmoviesappstage2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dalisozuze on 09/03/2017.
 */

public class MovieReview {

    public String mID;
    public String mAuthor;
    public String mContent;
    public String mURL;


    public MovieReview(Map<String,String> reviewData){
        mID = reviewData.get("id");
        mAuthor = reviewData.get("author");
        mContent = reviewData.get("content");
        mURL = reviewData.get("url");

    }
    public static List<MovieReview> createMovieReviews(List<Map<String,String>> reviewCollection){

        List<MovieReview> reviews = new ArrayList<>();

        for(Map<String,String> reviewItem: reviewCollection){
            reviews.add(new MovieReview(reviewItem));
        }
        return reviews;
    }

}
