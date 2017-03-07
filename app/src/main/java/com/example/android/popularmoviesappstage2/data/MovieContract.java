package com.example.android.popularmoviesappstage2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dalisozuze on 05/03/2017.
 */

public class MovieContract {


    public static final String AUTHORITY = "com.example.android.popularmoviesappstage2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TASKS = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "movies";


        public static final String COLUMN_TMDB_ID = "tmdbid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "user_rating";
        public static final String COLUMN_RELEASE = "release_date";

    }
}
