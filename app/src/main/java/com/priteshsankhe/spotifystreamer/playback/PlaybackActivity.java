package com.priteshsankhe.spotifystreamer.playback;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.models.SpotifyTrackPlayer;

public class PlaybackActivity extends AppCompatActivity {

    private static final String TAG = PlaybackActivity.class.getSimpleName();
    private static final String PLAYBACK_FRAGMENT = "PLAYBACK_FRAGMENT";
    private PlaybackActivityFragment playbackActivityFragment;
    private boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        final int spotifyTrackPosition = getIntent().getIntExtra("SPOTIFY_TRACK_POSITION", -1);
        final SpotifyTrackPlayer spotifyTrackPlayer = getIntent().getParcelableExtra("SPOTIFY_TRACK");
        Log.d(TAG, "onCreate : isNotTablet");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.top_ten_tracks));
        FragmentManager fragmentManager = getSupportFragmentManager();
        playbackActivityFragment = (PlaybackActivityFragment) fragmentManager.findFragmentByTag(PLAYBACK_FRAGMENT);
        Bundle arguments = new Bundle();
        arguments.putInt("SPOTIFY_TRACK_POSITION", spotifyTrackPosition);
        arguments.putParcelable("SPOTIFY_TRACK", spotifyTrackPlayer);
        if (playbackActivityFragment == null) {
            playbackActivityFragment = new PlaybackActivityFragment();
            playbackActivityFragment.setArguments(arguments);
            fragmentManager.beginTransaction().replace(R.id.playback_fragment_container, playbackActivityFragment, PLAYBACK_FRAGMENT).commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
