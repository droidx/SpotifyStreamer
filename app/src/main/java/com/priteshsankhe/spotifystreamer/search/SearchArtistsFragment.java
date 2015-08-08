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
import com.priteshsankhe.spotifystreamer.listeners.TaskListener;
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
 *
 * This project utilizes Picasso and Spotify-Web-Api-Wrapper third party libraries
 * Picasso : http://square.github.io/picasso/
 */
public class SearchArtistsFragment extends Fragment implements TaskListener {

    private static final String TAG = SearchArtistsFragment.class.getSimpleName();

    // Constants for key values for bundle during onSaveInstanceState and onRestoreInstanceState
    private static final String NO_RESULTS_TEXTVIEW_VISIBILITY = "NO_RESULTS_TEXTVIEW_VISIBILITY";
    private static final String SPOTIFY_ARTIST = "SPOTIFY_ARTIST";
    private static final String QUERY_TEXT = "QUERY_TEXT";

    // Optimal size of 200px for Artist Thumbnail Image
    private static final int ARTIST_THUMBNAIL_OPTIMIZED_IMAGE_SIZE = 200;

    public static final String INTENT_ARTIST_ID = "ARTIST_ID";
    public static final String INTENT_ARTIST_NAME = "ARTIST_NAME";

    // UI elements
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView noResultsFoundTextView;

    private SearchArtistsAdapter searchArtistsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<SpotifyArtist> artistList;

    // AsyncTask for fetching artist data using the Spotify Web-Api-Wrapper
    private FetchArtistsTask fetchArtistsTask;
    private static CharSequence queryText = null;
    private boolean isTaskRunning = false;

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

        // Restore state on rotation or if destroyed
        if (savedInstanceState != null) {

            // Check if asynctask is running
            if (isTaskRunning) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            // query text on the searchview
            if (savedInstanceState.getCharSequence(QUERY_TEXT) != null) {
                queryText = savedInstanceState.getCharSequence(QUERY_TEXT);
                searchView.setQuery(queryText, false);
            }

            if (savedInstanceState.getInt(NO_RESULTS_TEXTVIEW_VISIBILITY) == View.VISIBLE) {
                noResultsFoundTextView.setVisibility(View.VISIBLE);
            }
            artistList = savedInstanceState.getParcelableArrayList(SPOTIFY_ARTIST);

        } else {
            artistList = new ArrayList<SpotifyArtist>();
        }

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        searchArtistsAdapter = new SearchArtistsAdapter(getActivity(), artistList);
        recyclerView.setAdapter(searchArtistsAdapter);

        // SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                              @Override
                                              public boolean onQueryTextSubmit(String query) {
                                                  if (!query.isEmpty()) {
                                                      if (null != fetchArtistsTask && !fetchArtistsTask.isCancelled()) {
                                                          fetchArtistsTask.cancel(true);
                                                      }
                                                      fetchArtistsTask = new FetchArtistsTask(SearchArtistsFragment.this);
                                                      fetchArtistsTask.execute(query);
                                                  }
                                                  return false;
                                              }

                                              @Override
                                              public boolean onQueryTextChange(String newText) {
                                                  artistList.clear();
                                                  if (null != fetchArtistsTask && !fetchArtistsTask.isCancelled()) {
                                                      fetchArtistsTask.cancel(true);
                                                  }

                                                  // If no search query, don't execute asynctask just update the textview
                                                  if (newText.isEmpty()) {

                                                      if (null != progressBar && progressBar.isShown()) {
                                                          progressBar.setVisibility(View.GONE);
                                                      }
                                                      artistList.clear();
                                                      searchArtistsAdapter.notifyDataSetChanged();
                                                      noResultsFoundTextView.setVisibility(View.VISIBLE);
                                                      noResultsFoundTextView.setText(getActivity().getString(R.string.search_empty_search_text));
                                                      return false;
                                                  }

                                                  fetchArtistsTask = new FetchArtistsTask(SearchArtistsFragment.this);
                                                  fetchArtistsTask.execute(newText);
                                                  return false;
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
    public void onDetach() {
        if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
        super.onDetach();
    }

    @Override
    public void onTaskStarted() {
        isTaskRunning = true;
        if (progressBar != null && progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        }
        noResultsFoundTextView.setVisibility(View.GONE);
    }

    @Override
    public void onTaskFinished() {
        isTaskRunning = false;
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }

    }

    /**
     * This AsyncTask requests artist data via the Search for an Item endpoint.
     * It fetches the thumbnail url optimized for 200px imageview using SpotifyUtils class.
     * If no artists are found, the textview is updated.
     */
    public class FetchArtistsTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();
        private SpotifyError spotifyError = null;
        private final TaskListener listener;

        private FetchArtistsTask(TaskListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listener.onTaskStarted();
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
                isTaskRunning = false;
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

            listener.onTaskFinished();
            if (null != spotifyError) {

                // Handle common errors
                if (spotifyError.hasErrorDetails()) {
                    switch (spotifyError.getErrorDetails().status) {
                        case 400:
                            noResultsFoundTextView.setVisibility(View.VISIBLE);
                            noResultsFoundTextView.setText(getActivity().getString(R.string.search_bad_request));
                            break;
                        case 404:
                            noResultsFoundTextView.setVisibility(View.VISIBLE);
                            noResultsFoundTextView.setText(getActivity().getString(R.string.search_no_results_found));
                            break;
                    }
                }
                if (spotifyError.getMessage().contains("Unable to resolve host")) {
                    noResultsFoundTextView.setVisibility(View.VISIBLE);
                    noResultsFoundTextView.setText(getActivity().getString(R.string.search_no_internet_connection));
                }
            } else {
                // if no artists are found, write a message to the TextView
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
