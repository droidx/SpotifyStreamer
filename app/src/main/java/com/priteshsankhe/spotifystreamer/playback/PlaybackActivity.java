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

        isTablet = getResources().getBoolean(R.bool.isTablet);
        final int spotifyTrackPosition = getIntent().getIntExtra("SPOTIFY_TRACK_POSITION", -1);
        final SpotifyTrackPlayer spotifyTrackPlayer = getIntent().getParcelableExtra("SPOTIFY_TRACK");

        if (isTablet) {
            Log.d(TAG, "onCreate : isTablet");
            showDialog(spotifyTrackPosition, spotifyTrackPlayer);
        } else {
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
    }

    private void showDialog(int position, SpotifyTrackPlayer spotifyTrackPlayer) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        playbackActivityFragment = (PlaybackActivityFragment) fragmentManager.findFragmentByTag(PLAYBACK_FRAGMENT);
        Bundle arguments = new Bundle();
        arguments.putInt("SPOTIFY_TRACK_POSITION", position);
        arguments.putParcelable("SPOTIFY_TRACK", spotifyTrackPlayer);

        if (playbackActivityFragment == null) {
            PlaybackActivityFragment playbackActivityFragment = PlaybackActivityFragment.newInstance();
            playbackActivityFragment.setArguments(arguments);
            playbackActivityFragment.show(getSupportFragmentManager(), PLAYBACK_FRAGMENT);
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
