package com.priteshsankhe.spotifystreamer.artist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.listeners.TopTrackSelectedListener;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtist;
import com.priteshsankhe.spotifystreamer.models.SpotifyTrackPlayer;
import com.priteshsankhe.spotifystreamer.playback.PlaybackActivity;

public class TopTracksActivity extends AppCompatActivity implements TopTrackSelectedListener {

    private static final String TAG = TopTracksActivity.class.getSimpleName();
    private static final String TAG_TOP_TRACKS_FRAGMENT = "TOP_TRACKS_FRAGMENT";

    private TopTracksActivityFragment topTracksActivityFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.top_ten_tracks));

        final SpotifyArtist spotifyArtist = getIntent().getParcelableExtra("SPOTIFY_ARTIST");
        getSupportActionBar().setSubtitle(spotifyArtist.getArtistName());

        FragmentManager fragmentManager = getSupportFragmentManager();
        topTracksActivityFragment = (TopTracksActivityFragment) fragmentManager.findFragmentByTag(TAG_TOP_TRACKS_FRAGMENT);
        Bundle arguments = new Bundle();
        arguments.putParcelable("SPOTIFY_ARTIST", spotifyArtist);

        if (topTracksActivityFragment == null) {
            topTracksActivityFragment = new TopTracksActivityFragment();
            topTracksActivityFragment.setArguments(arguments);
            fragmentManager.beginTransaction().replace(R.id.fragment, topTracksActivityFragment, TAG_TOP_TRACKS_FRAGMENT).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
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

    @Override
    public void onTrackSelected(final int position, final SpotifyTrackPlayer spotifyTrackPlayer) {

        Bundle bundle = new Bundle();
        bundle.putInt("SPOTIFY_TRACK_POSITION", position);
        bundle.putParcelable("SPOTIFY_TRACK", spotifyTrackPlayer);
        Intent intent = new Intent(this, PlaybackActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
