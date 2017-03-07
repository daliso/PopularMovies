package com.example.android.popularmoviesappstage2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesappstage2.data.MovieContract;
import com.example.android.popularmoviesappstage2.networkutils.TMDbNetworkUtils;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TMDbAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private RecyclerView mRecyclerView;
    private TMDbAdapter mTMDbAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private TMDbNetworkUtils.SortOrder mSortOrder;
    private static final String SORT_ORDER = "sort_order";

    private static final int MOVIE_LOADER_ID = 0;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.cols), GridLayout.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mTMDbAdapter = new TMDbAdapter();
        mTMDbAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mTMDbAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if (savedInstanceState == null) {
            mSortOrder = TMDbNetworkUtils.SortOrder.POPULAR;
        } else {
            mSortOrder = (TMDbNetworkUtils.SortOrder) savedInstanceState.getSerializable(SORT_ORDER);
        }

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SORT_ORDER, mSortOrder);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {

                try {

                    List<Map<String,String>> moviesCollection = null;
                    Cursor moviesCursor = null;

                    if(mSortOrder == TMDbNetworkUtils.SortOrder.FAVORITE){
                        moviesCursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);

                    }
                    else{
                        moviesCollection = TMDbNetworkUtils.getMovies(mSortOrder);
                    }

                    List<Movie> movieData = null;

                    if (moviesCollection != null) {
                        movieData = Movie.createMovies(moviesCollection);
                    } else if (moviesCursor != null) {
                        movieData = Movie.createMovies(moviesCursor);
                    }
                    else {
                        showErrorMessage();
                    }

                    return movieData;

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }

            }

            public void deliverResult(List<Movie> data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };

    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        // Update the data that the adapter uses to create ViewHolders
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        showMovieGridView();
        mTMDbAdapter.setMovieData(data);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mTMDbAdapter.setMovieData(null);
    }

    @Override
    public void onItemClick(Movie movie) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("Movie", movie);
        startActivity(intentToStartDetailActivity);
    }


    private void showMovieGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular) {
            mSortOrder = TMDbNetworkUtils.SortOrder.POPULAR;
            Context context = this;
            Toast.makeText(context, getString(R.string.sort_popular_toast), Toast.LENGTH_SHORT)
                    .show();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            return true;
        }
        else if (id == R.id.action_sort_rating) {
            mSortOrder = TMDbNetworkUtils.SortOrder.RATING;
            Context context = this;
            Toast.makeText(context, getString(R.string.sort_rating_toast), Toast.LENGTH_SHORT)
                    .show();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            return true;
        }
        else if (id == R.id.action_sort_favorite) {
            mSortOrder = TMDbNetworkUtils.SortOrder.FAVORITE;
            Context context = this;
            Toast.makeText(context, getString(R.string.sort_favorite_toast), Toast.LENGTH_SHORT)
                    .show();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Todo: put online protection code back
    public boolean isOnline() {
        Context context = this;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
