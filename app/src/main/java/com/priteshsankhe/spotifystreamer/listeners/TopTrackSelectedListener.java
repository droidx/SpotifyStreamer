package com.priteshsankhe.spotifystreamer.listeners;

import com.priteshsankhe.spotifystreamer.models.SpotifyTrackPlayer;

/**
 * Created by Pritesh on 8/8/2015.
 */
public interface TopTrackSelectedListener {

    void onTrackSelected(int position, SpotifyTrackPlayer spotifyTrackPlayer);
}
