package com.priteshsankhe.spotifystreamer.search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtist;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.RetrofitError;


/**
 * This fragment searches for an artist and displays results
 * It uses the Spotify-Web-Api-Wrapper library https://github.com/kaaes/spotify-web-api-android
 * to search for artists.
 */
public class SearchArtistsFragment extends Fragment {

    private static final String TAG = SearchArtistsFragment.class.getSimpleName();

    private static final String NO_RESULTS_TEXTVIEW_VISIBILITY = "NO_RESULTS_TEXTVIEW_VISIBILITY";
    private static final String SPOTIFY_ARTIST = "SPOTIFY_ARTIST";

    // UI elements
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView noResultsFoundTextView;

    private SearchArtistsAdapter searchArtistsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<SpotifyArtist> artistList;

    private FetchArtistsTask fetchArtistsTask;

    public SearchArtistsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_artists, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.artist_list_recycler_view);
        searchView = (SearchView) rootView.findViewById(R.id.artist_search_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.search_result_progress_bar);
        noResultsFoundTextView = (TextView) rootView.findViewById(R.id.search_results_not_found_textview);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        artistList = new ArrayList<SpotifyArtist>();
        searchArtistsAdapter = new SearchArtistsAdapter(getActivity(), artistList);
        recyclerView.setAdapter(searchArtistsAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "query is : " + query);
                if (!query.isEmpty()) {
                    fetchArtistsTask = new FetchArtistsTask();
                    fetchArtistsTask.execute(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NO_RESULTS_TEXTVIEW_VISIBILITY, noResultsFoundTextView.getVisibility());
        outState.putParcelableArrayList(SPOTIFY_ARTIST, artistList);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            if(savedInstanceState.getInt(NO_RESULTS_TEXTVIEW_VISIBILITY) == View.VISIBLE){
                noResultsFoundTextView.setVisibility(View.VISIBLE);
            }
            artistList = savedInstanceState.getParcelableArrayList(SPOTIFY_ARTIST);
            if (searchArtistsAdapter != null) {
                searchArtistsAdapter.setSpotifyArtistList(artistList);
                searchArtistsAdapter.notifyDataSetChanged();
            } else {
                searchArtistsAdapter = new SearchArtistsAdapter(getActivity(), artistList);
                searchArtistsAdapter.notifyDataSetChanged();
            }
        }
    }

    public class FetchArtistsTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();
        private SpotifyError spotifyError = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            noResultsFoundTextView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... params) {

            artistList.clear();
            final String artistQuery = params[0];
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();

            try {
                final ArtistsPager artistsPager = spotifyService.searchArtists(artistQuery);
                for (Artist artist : artistsPager.artists.items) {

                    String defaultImageURL = "https://i.scdn.co/image/18141db33353a7b84c311b7068e29ea53fad2326";
                    List<Image> images = artist.images;
                    for (Image image : images) {
                        if (image.url != null && !image.url.isEmpty()) {
                            if (image.height >= 200 && image.height <= 640) {
                                defaultImageURL = image.url;
                            }
                        }
                    }
                    artistList.add(new SpotifyArtist(artist.id, artist.name, defaultImageURL));
                }
            } catch (RetrofitError error) {
                Log.e(LOG_TAG, "Error ", error);
                spotifyError = SpotifyError.fromRetrofitError(error);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            if (spotifyError != null) {
                Toast.makeText(getActivity(), "Something went wrong! " + spotifyError.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                // if no artists are found, write a message to the textview
                if (artistList.isEmpty()) {
                    noResultsFoundTextView.setVisibility(View.VISIBLE);
                }
                searchArtistsAdapter.notifyDataSetChanged();
            }
        }
    }
}
