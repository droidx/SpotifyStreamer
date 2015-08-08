package com.priteshsankhe.spotifystreamer.artist;

import android.app.Activity;
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
import android.widget.TextView;

import com.priteshsankhe.spotifystreamer.MainActivity;
import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.listeners.TaskListener;
import com.priteshsankhe.spotifystreamer.listeners.TopTrackSelectedListener;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtist;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtistTrack;
import com.priteshsankhe.spotifystreamer.models.SpotifyTrackPlayer;
import com.priteshsankhe.spotifystreamer.utility.SpotifyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * This fragment dispays the top tracks for a selected artist.
 */
public class TopTracksActivityFragment extends Fragment implements TaskListener {

    private static final String TAG = TopTracksActivityFragment.class.getSimpleName();

    // Constants for Spotify API
    private static final String SPOTIFY_TOP_TRACKS_OPTION_COUNTRY = "country";
    private static final String SPOTIFY_TOP_TRACKS_OPTION_COUNTRY_VALUE = "SE";

    private static final int ALBUM_ART_THUMBNAIL_SMALL = 200;
    private static final int ALBUM_ART_THUMBNAIL_LARGE = 640;

    private static final String SPOTIFY_TOP_TRACKS_LIST = "SPOTIFY_TOP_TRACKS_LIST";

    @Bind(R.id.top_tracks_list_recycler_view)
    RecyclerView recyclerView;

    @Bind(R.id.result_progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.search_results_not_found_textview)
    TextView noResultsFoundTextView;

    private TopTracksAdapter topTracksAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<SpotifyArtistTrack> topTracksList;
    private SpotifyArtist spotifyArtist;
    private SpotifyTrackPlayer spotifyTrackPlayer;

    private FetchTopTracksTask fetchTopTracksTask;
    private TopTrackSelectedListener topTrackSelectedListener;
    private boolean isTablet = false;
    private boolean isTaskRunning = false;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            if (isTaskRunning && progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            topTracksList = savedInstanceState.getParcelableArrayList(SPOTIFY_TOP_TRACKS_LIST);
            if (null != fetchTopTracksTask && fetchTopTracksTask.getStatus() == AsyncTask.Status.PENDING && null != progressBar) {
                progressBar.setVisibility(View.VISIBLE);
            }
        } else {
            spotifyArtist = new SpotifyArtist();
            spotifyTrackPlayer = new SpotifyTrackPlayer();
            topTracksList = new ArrayList<SpotifyArtistTrack>();
            Bundle arguments = getArguments();
            if (arguments != null) {
                spotifyArtist = arguments.getParcelable("SPOTIFY_ARTIST");
                spotifyTrackPlayer.setArtist(spotifyArtist);
                final String artistID = spotifyArtist.getSpotifyArtistId();
                Log.d(TAG, "onCreateView artistId" + artistID);
                fetchTopTracksTask = new FetchTopTracksTask(TopTracksActivityFragment.this);
                fetchTopTracksTask.execute(artistID);
            }
            spotifyTrackPlayer.setSpotifyArtistTrackList(topTracksList);
        }

        topTracksAdapter = new TopTracksAdapter(getActivity(), spotifyTrackPlayer, topTrackSelectedListener);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(topTracksAdapter);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        final boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        try {
            if (isTablet) {
                topTrackSelectedListener = (MainActivity) activity;
            } else {
                topTrackSelectedListener = (TopTracksActivity) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTopTracksSelectedListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SPOTIFY_TOP_TRACKS_LIST, topTracksList);
    }

    @Override
    public void onTaskStarted() {
        isTaskRunning = true;
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTaskFinished() {
        isTaskRunning = false;
        if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fetchTopTracksTask != null && fetchTopTracksTask.getStatus() == AsyncTask.Status.PENDING) {
            fetchTopTracksTask.cancel(true);
            fetchTopTracksTask = null;
        }
    }

    /**
     * This AsyncTask requests track data via the Get an Artist's Top Tracks Spotify endpoint
     */
    public class FetchTopTracksTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchTopTracksTask.class.getSimpleName();
        private SpotifyError spotifyError = null;
        private final TaskListener listener;

        private FetchTopTracksTask(TaskListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listener.onTaskStarted();
        }

        protected Void doInBackground(String... params) {

            topTracksList.clear();
            final String artistId = params[0];
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            Map<String, Object> options = new HashMap<>(1);
            options.put(SPOTIFY_TOP_TRACKS_OPTION_COUNTRY, SPOTIFY_TOP_TRACKS_OPTION_COUNTRY_VALUE);
            Log.d(TAG, "doInBackground artist id" + artistId);
            try {
                final Tracks spotifyServiceArtistTopTrack = spotifyService.getArtistTopTrack(artistId, options);

                for (Track track : spotifyServiceArtistTopTrack.tracks) {
                    final String trackName = track.name;
                    final String albumName = track.album.name;
                    final String albumArtSmallThumbnailURL = SpotifyUtils.fetchOptimizedImageURL(track.album.images, ALBUM_ART_THUMBNAIL_SMALL);
                    final String albumArtLargeThumbnailURL = SpotifyUtils.fetchOptimizedImageURL(track.album.images, ALBUM_ART_THUMBNAIL_LARGE);
                    final String previewURL = track.preview_url;
                    topTracksList.add(new SpotifyArtistTrack(trackName, albumName, albumArtSmallThumbnailURL, albumArtLargeThumbnailURL, previewURL));
                }

            } catch (RetrofitError e) {
                Log.e(LOG_TAG, "Error : ", e);
                spotifyError = SpotifyError.fromRetrofitError(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listener.onTaskFinished();

            if (spotifyError != null) {
                Log.d(TAG, spotifyError.getMessage());
            } else {
                if (topTracksList.isEmpty()) {
                    noResultsFoundTextView.setVisibility(View.VISIBLE);
                    noResultsFoundTextView.setText(R.string.top_tracks_not_found);
                } else {
                    topTracksAdapter.notifyDataSetChanged();
                }
            }

        }
    }
}
