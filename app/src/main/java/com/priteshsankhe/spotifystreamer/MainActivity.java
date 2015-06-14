package com.priteshsankhe.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.priteshsankhe.spotifystreamer.search.SearchArtistsFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG_SEARCH_ARTIST_FRAGMENT = "SEARCH_FRAGMENT";
    private SearchArtistsFragment searchArtistsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        searchArtistsFragment = (SearchArtistsFragment) fragmentManager.findFragmentByTag(TAG_SEARCH_ARTIST_FRAGMENT);

        if(searchArtistsFragment == null){
            searchArtistsFragment = new SearchArtistsFragment();
            fragmentManager.beginTransaction().add(searchArtistsFragment, TAG_SEARCH_ARTIST_FRAGMENT).commit();
        }
    }
}
