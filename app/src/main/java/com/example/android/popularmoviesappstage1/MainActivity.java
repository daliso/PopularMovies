package com.example.android.popularmoviesappstage1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesappstage1.networkutils.TMDbNetworkUtils;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TMDbAdapter.ItemClickListener{

    private RecyclerView mRecyclerView;
    private TMDbAdapter mTMDbAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private TMDbNetworkUtils.SortOrder mSortOrder;
    private static final String SORT_ORDER = "sort_order";


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

        loadMovieData();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SORT_ORDER, mSortOrder);
        super.onSaveInstanceState(outState);
    }

    private void loadMovieData() {
        if (isOnline()){
            showMovieGridView();
            new FetchMoviesTask().execute(mSortOrder);
        }
        else {
            showErrorMessage();
        }

    }

    @Override
    public void onItemClick(Movie movie) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("Movie", movie);
        startActivity(intentToStartDetailActivity);
    }


    public class FetchMoviesTask extends AsyncTask<TMDbNetworkUtils.SortOrder, Void, List<Map<String,String>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Map<String,String>> doInBackground(TMDbNetworkUtils.SortOrder... sortOrder) {
            return TMDbNetworkUtils.getMovies(sortOrder[0]);
        }

        @Override
        protected void onPostExecute(List<Map<String,String>> moviesCollection) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesCollection != null) {
                showMovieGridView();
                List<Movie> movieData = Movie.createMovies(moviesCollection);
                mTMDbAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }

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
            loadMovieData();
            return true;
        }
        else if (id == R.id.action_sort_rating) {
            mSortOrder = TMDbNetworkUtils.SortOrder.RATING;
            Context context = this;
            Toast.makeText(context, getString(R.string.sort_rating_toast), Toast.LENGTH_SHORT)
                    .show();
            loadMovieData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        Context context = this;

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
