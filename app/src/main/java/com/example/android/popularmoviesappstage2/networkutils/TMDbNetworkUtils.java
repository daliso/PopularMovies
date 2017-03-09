package com.example.android.popularmoviesappstage2.networkutils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by dalisozuze on 06/02/2017.
 */

public class TMDbNetworkUtils {

    private static final String TAG = TMDbNetworkUtils.class.getSimpleName();

    private static String IMAGE_URL_PREFIX = "https://image.tmdb.org/t/p/w185";

    private static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static String API_KEY = "6c74ffc062ba4bf4e9f30282fe2cceea"; // "INSERT_YOUR_API_KEY_HERE"; TODO: Insert API Key Here

    final static String LANG_PARAM = "language";
    final static String LANG = "en-US";
    final static String PAGE_PARAM = "page";
    final static String PAGE = "1";
    final static String KEY_PARAM = "api_key";

    final static String VIDEOS_PATH = "videos";
    final static String REVIEWS_PATH = "reviews";


    public enum SortOrder {
        POPULAR("popular"), RATING("top_rated"), FAVORITE("favorite");

        private String sortOrderString;

        SortOrder(String s) {
            sortOrderString = s;
        }

        public String getSortOrderString() {
            return sortOrderString;
        }
    }

    /**
     * Returns the collection of movie records obtained via the TMDb API
     * This is returned as a list of maps, each map is a key value pair.
     *
     * @param order The sort order for the results, either by rating or popularity.
     * @return The collection of movie records
     */
    public static List<Map<String,String>> getMovies(SortOrder order) {

        String jsonResponse = null;
        try {
            jsonResponse = getResponseFromHttpUrl(buildUrl(order));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("NetTalk","error in accessing network: "+ e.toString());
            return null;
        }

        return parseTMDbJSON(jsonResponse);
    }

    /**
     * Returns the collection of video trailer records obtained via the TMDb API
     * This is returned as a list of maps, each map is a key value pair.
     *
     * @param id The movie id for the videos
     * @return The collection of movie trailer records
     */
    public static List<Map<String,String>> getMovieVideos(String id) {

        String jsonResponse = null;
        try {
            jsonResponse = getResponseFromHttpUrl(buildMovieDetailUrl(id,VIDEOS_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("NetTalk","error in accessing network: "+ e.toString());
            return null;
        }

        return parseTMDbJSONVideos(jsonResponse);
    }


    /**
     * Parses the JSON string obtained via an http request
     *
     * @param jsonString The JSON from /movie
     * @return The collection of movie records
     */
    public static List<Map<String,String>> parseTMDbJSON(String jsonString) {

        List<Map<String,String>> movieCollection = new ArrayList<>();
        JSONObject jObject = null;

        try {
            Map<String, String> movieMap;
            jObject = new JSONObject(jsonString);
            JSONArray movieResultsList = jObject.getJSONArray("results");

            for(int i=0;i<movieResultsList.length();i++){
                JSONObject movieJSON = movieResultsList.getJSONObject(i);

                movieMap = new HashMap<>();
                movieMap.put("title",movieJSON.getString("original_title"));
                movieMap.put("poster",IMAGE_URL_PREFIX + movieJSON.getString("poster_path"));
                movieMap.put("overview",movieJSON.getString("overview"));
                movieMap.put("rating",String.valueOf(movieJSON.getDouble("vote_average")));
                movieMap.put("releaseDate",movieJSON.getString("release_date"));
                movieMap.put("tmdbId",String.valueOf(movieJSON.getInt("id")));

                movieCollection.add(movieMap);

            }

        }
        catch(Exception e){
            Log.d("NetTalk","something went wrong: "+ e.toString());
            return null;
        }

        return movieCollection;
    }


    /**
     * Parses the JSON string obtained via an http request
     *
     * @param jsonString The JSON from /movie/{movie_id}/videos
     * @return The collection of video trailer records
     */
    public static List<Map<String,String>> parseTMDbJSONVideos(String jsonString) {

        List<Map<String,String>> videoCollection = new ArrayList<>();
        JSONObject jObject = null;

        try {
            Map<String, String> videoMap;
            jObject = new JSONObject(jsonString);
            JSONArray videoResultsList = jObject.getJSONArray("results");

            for(int i=0;i<videoResultsList.length();i++){
                JSONObject videoJSON = videoResultsList.getJSONObject(i);
                videoMap = new HashMap<>();
                videoMap.put("site",videoJSON.getString("site"));
                videoMap.put("name",videoJSON.getString("name"));
                videoMap.put("key",videoJSON.getString("key"));
                videoCollection.add( videoMap);
            }
        }
        catch(Exception e){
            Log.d("NetTalk","something went wrong: "+ e.toString());
            return null;
        }

        return videoCollection;
    }



    /**
     * Builds the URL used to talk to the TMDb api.
     * @param order The sort order for the results, either by rating or popularity.
     * @return The URL to use to query TMDb.
     */
    public static URL buildUrl(SortOrder order) {

        Uri builtUri = Uri.parse(TMDB_BASE_URL+order.getSortOrderString()).buildUpon()
                .appendQueryParameter(KEY_PARAM, API_KEY)
                .appendQueryParameter(LANG_PARAM, LANG)
                .appendQueryParameter(PAGE_PARAM, PAGE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * Builds the URL used to talk to the TMDb api.
     * @param id The id of the movie for which videos are needed.
     * @return The URL to use to query TMDb.
     */
    public static URL buildMovieDetailUrl(String id, String detail) {

        Uri builtUri = Uri.parse(TMDB_BASE_URL+id).buildUpon()
                .appendPath(detail)
                .appendQueryParameter(KEY_PARAM, API_KEY)
                .appendQueryParameter(LANG_PARAM, LANG)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
