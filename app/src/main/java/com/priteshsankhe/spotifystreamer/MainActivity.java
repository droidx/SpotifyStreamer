package com.priteshsankhe.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.priteshsankhe.spotifystreamer.search.SearchArtistsFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
