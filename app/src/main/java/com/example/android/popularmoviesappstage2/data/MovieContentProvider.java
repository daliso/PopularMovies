package com.example.android.popularmoviesappstage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by dalisozuze on 05/03/2017.
 */


public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY,MovieContract.PATH_TASKS,MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY,MovieContract.PATH_TASKS + "/#",MOVIES_WITH_ID);

        return uriMatcher;
    }

    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch(match){
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if (id>0){
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }

                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor =  db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int moviesDeleted;

        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, "tmdbid=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of movies deleted
        return moviesDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int moviesUpdated;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesUpdated = mMovieDbHelper.getWritableDatabase().update(MovieContract.MovieEntry.TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesUpdated;
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}