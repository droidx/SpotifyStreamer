package com.priteshsankhe.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.priteshsankhe.spotifystreamer.artist.TopTracksActivity;
import com.priteshsankhe.spotifystreamer.artist.TopTracksActivityFragment;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtist;
import com.priteshsankhe.spotifystreamer.search.SearchArtistsAdapter;
import com.priteshsankhe.spotifystreamer.search.SearchArtistsFragment;


public class MainActivity extends AppCompatActivity implements SearchArtistsAdapter.AdapterCallback {

    private static final String TAG_SEARCH_ARTIST_FRAGMENT = "SEARCH_FRAGMENT";
    private static final String TAG_TOP_TRACKS_FRAGMENT = "TOP_TRACKS_FRAGMENT";
    private SearchArtistsFragment searchArtistsFragment;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO handle orientation change on tablet
        //In tablet, on orientation change the behavior changes - from two pane to single

        FragmentManager fragmentManager = getSupportFragmentManager();
        searchArtistsFragment = (SearchArtistsFragment) fragmentManager.findFragmentByTag(TAG_SEARCH_ARTIST_FRAGMENT);

        if (searchArtistsFragment == null) {
            searchArtistsFragment = new SearchArtistsFragment();
            fragmentManager.beginTransaction().add(searchArtistsFragment, TAG_SEARCH_ARTIST_FRAGMENT).commit();
        }

        if (findViewById(R.id.top_tracks_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_tracks_container, new TopTracksActivityFragment(), TAG_TOP_TRACKS_FRAGMENT)
                        .commit();
            } else {
                mTwoPane = false;
            }
        }
    }

    @Override
    public void onItemSelected(SpotifyArtist artist) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable("SPOTIFY_ARTIST", artist);
            TopTracksActivityFragment fragment = new TopTracksActivityFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, fragment, TAG_TOP_TRACKS_FRAGMENT)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTracksActivity.class);
            intent.putExtra("SPOTIFY_ARTIST", artist);
            startActivity(intent);
        }


    }
}
