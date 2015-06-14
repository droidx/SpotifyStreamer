package com.priteshsankhe.spotifystreamer.search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtist;
import com.priteshsankhe.spotifystreamer.utility.SpotifyUtils;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
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
    private static final String QUERY_TEXT = "QUERY_TEXT";

    private static final int ARTIST_THUMBNAIL_OPTIMIZED_IMAGE_SIZE = 200;

    // UI elements
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView noResultsFoundTextView;

    private SearchArtistsAdapter searchArtistsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<SpotifyArtist> artistList;

    private FetchArtistsTask fetchArtistsTask;
    private static CharSequence queryText = null;

    public SearchArtistsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_artists, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.artist_list_recycler_view);
        searchView = (SearchView) rootView.findViewById(R.id.artist_search_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.result_progress_bar);
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
                                                  return true;
                                              }

                                              @Override
                                              public boolean onQueryTextChange(String newText) {
                                                  if (null != fetchArtistsTask && !fetchArtistsTask.isCancelled()) {
                                                      fetchArtistsTask.cancel(true);
                                                  }
                                                  fetchArtistsTask = new FetchArtistsTask();
                                                  fetchArtistsTask.execute(newText);
                                                  return true;
                                              }
                                          }

        );

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
        outState.putCharSequence(QUERY_TEXT, searchView.getQuery());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.getCharSequence(QUERY_TEXT) != null) {
                queryText = savedInstanceState.getCharSequence(QUERY_TEXT);
                searchView.setQuery(queryText, false);
            }

            if (savedInstanceState.getInt(NO_RESULTS_TEXTVIEW_VISIBILITY) == View.VISIBLE) {
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
            if (progressBar != null && progressBar.getVisibility() == View.GONE) {
                progressBar.setVisibility(View.VISIBLE);
            }
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

                    final String defaultImageURL = SpotifyUtils.fetchOptimizedImageURL(artist.images, ARTIST_THUMBNAIL_OPTIMIZED_IMAGE_SIZE);
                    artistList.add(new SpotifyArtist(artist.id, artist.name, defaultImageURL));
                }
            } catch (RetrofitError error) {
                Log.e(LOG_TAG, "Error ", error);
                spotifyError = SpotifyError.fromRetrofitError(error);
            }
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
            if (spotifyError != null) {
                if(spotifyError.hasErrorDetails()){
                    Log.d(TAG, "Spotify error code " + spotifyError.getErrorDetails().status);
                    switch (spotifyError.getErrorDetails().status){
                        case 400:
                            noResultsFoundTextView.setVisibility(View.VISIBLE);
                            noResultsFoundTextView.setText(getActivity().getString(R.string.empty_search_text));
                            break;
                        case 404:
                            noResultsFoundTextView.setVisibility(View.VISIBLE);
                            noResultsFoundTextView.setText(getActivity().getString(R.string.no_results_found));
                            break;
                    }
                }
                if(spotifyError.getMessage().contains("Unable to resolve host")){
                    noResultsFoundTextView.setVisibility(View.VISIBLE);
                    noResultsFoundTextView.setText(getActivity().getString(R.string.search_no_internet_connection));
                }
            } else {
                // if no artists are found, write a message to the textview
                if (artistList.isEmpty()) {
                    noResultsFoundTextView.setVisibility(View.VISIBLE);
                    noResultsFoundTextView.setText(R.string.no_artist_found);
                } else {
                    noResultsFoundTextView.setVisibility(View.GONE);
                    searchArtistsAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
