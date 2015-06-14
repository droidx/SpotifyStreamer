package com.priteshsankhe.spotifystreamer.artist;

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

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtistTrack;
import com.priteshsankhe.spotifystreamer.utility.SpotifyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private static final String TAG = TopTracksActivityFragment.class.getSimpleName();

    private static final String SPOTIFY_TOP_TRACKS_OPTION_COUNTRY = "country";
    private static final String SPOTIFY_TOP_TRACKS_OPTION_COUNTRY_VALUE = "SE";

    private static final int ALBUM_ART_THUMBNAIL_SMALL = 200;
    private static final int ALBUM_ART_THUMBNAIL_LARGE = 640;

    private static final String SPOTIFY_TOP_TRACKS_LIST = "SPOTIFY_TOP_TRACKS_LIST";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noResultsFoundTextView;

    private TopTracksAdapter topTracksAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<SpotifyArtistTrack> topTracksList;

    private FetchTopTracksTask fetchTopTracksTask;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.top_tracks_list_recycler_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.result_progress_bar);
        noResultsFoundTextView = (TextView) rootView.findViewById(R.id.search_results_not_found_textview);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        topTracksList = new ArrayList<SpotifyArtistTrack>();
        topTracksAdapter = new TopTracksAdapter(getActivity(), topTracksList);
        recyclerView.setAdapter(topTracksAdapter);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            topTracksList = savedInstanceState.getParcelableArrayList(SPOTIFY_TOP_TRACKS_LIST);
            if (topTracksAdapter != null) {
                topTracksAdapter.setSpotifyArtistTrackList(topTracksList);
                topTracksAdapter.notifyDataSetChanged();
            }
        } else {
            final String artistID = getActivity().getIntent().getStringExtra("artistId");
            fetchTopTracksTask = new FetchTopTracksTask();
            fetchTopTracksTask.execute(artistID);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SPOTIFY_TOP_TRACKS_LIST, topTracksList);
    }

    public class FetchTopTracksTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchTopTracksTask.class.getSimpleName();
        private SpotifyError spotifyError = null;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(String... params) {

            topTracksList.clear();
            final String artistId = params[0];
            SpotifyApi spotifyApi = new SpotifyApi();
            SpotifyService spotifyService = spotifyApi.getService();
            Map<String, Object> options = new HashMap<>(1);
            options.put(SPOTIFY_TOP_TRACKS_OPTION_COUNTRY, SPOTIFY_TOP_TRACKS_OPTION_COUNTRY_VALUE);
            try {
                final Tracks spotifyServiceArtistTopTrack = spotifyService.getArtistTopTrack(artistId, options);

                for (Track track : spotifyServiceArtistTopTrack.tracks) {
                    final String trackName = track.name;
                    final String albumName = track.album.name;
                    final String albumArtSmallThumbnailURL = SpotifyUtils.fetchOptimizedImageURL(track.album.images, ALBUM_ART_THUMBNAIL_SMALL);
                    final String albumArtLargeThumbnailURL = SpotifyUtils.fetchOptimizedImageURL(track.album.images, ALBUM_ART_THUMBNAIL_SMALL);
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
            progressBar.setVisibility(View.GONE);
            topTracksAdapter.notifyDataSetChanged();

        }
    }
}
