package com.example.android.popularmoviesappstage2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dalisozuze on 09/03/2017.
 */

public class VideoTrailer {

    public String mSite;
    public String mName;
    public String mKey;


    public VideoTrailer(Map<String,String> videoData){
        mSite = videoData.get("site");
        mName = videoData.get("name");
        mKey = videoData.get("key");

    }
    public static List<VideoTrailer> createVideos(List<Map<String,String>> videoCollection){

        List<VideoTrailer> videos = new ArrayList<>();

        for(Map<String,String> videoItem: videoCollection){
            videos.add(new VideoTrailer(videoItem));
        }
        return videos;
    }

}
