package com.priteshsankhe.spotifystreamer.utility;

import android.util.Log;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Pritesh on 6/14/2015.
 */
public class SpotifyUtils {

    private static final String TAG = SpotifyUtils.class.getSimpleName();


    public static String fetchOptimizedImageURL(final List<Image> artistImages, final int optimalImageSize){

        Log.d(TAG, "Images " + artistImages.size() + " optimal size : " + optimalImageSize);
        String optimizedThumbnailURL = null;
        int minDifference = Integer.MAX_VALUE;

        for (Image artistImageThumbnail : artistImages){
            if(null != artistImageThumbnail.url && !artistImageThumbnail.url.isEmpty()){

                Log.d(TAG, "Height" + artistImageThumbnail.height);
                final int dimensionDifference = Math.abs(artistImageThumbnail.height - optimalImageSize);
                if(dimensionDifference < minDifference){
                    minDifference = dimensionDifference;
                    optimizedThumbnailURL = artistImageThumbnail.url;
                }
            }
        }

        return optimizedThumbnailURL;
    }
}
