package com.priteshsankhe.spotifystreamer.playback;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtistTrack;
import com.priteshsankhe.spotifystreamer.models.SpotifyTrackPlayer;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaybackActivityFragment extends Fragment {

    private static final String TAG = PlaybackActivityFragment.class.getSimpleName();
    private MediaPlayer mediaPlayer;

    @Bind(R.id.album_artwork_image_view)
    ImageView albumArtWorkImageView;

    @Bind(R.id.track_name_text_view)
    TextView trackNameTextView;

    @Bind(R.id.artist_name_text_view)
    TextView artistNameTextView;

    @Bind(R.id.track_progress)
    SeekBar trackProgressSeekbar;

    @Bind(R.id.previous_track_button)
    ImageButton previousTrackButton;

    @Bind(R.id.pause_track_button)
    ImageButton pauseTrackButton;

    @Bind(R.id.next_track_button)
    ImageButton nextTrackButton;

    int spotifyTrackPosition = 0;

    public PlaybackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback, container, false);
        ButterKnife.bind(this, rootView);

        final SpotifyTrackPlayer spotifyTrackPlayer = getActivity().getIntent().getParcelableExtra("SPOTIFY_TRACK");
        spotifyTrackPosition = getActivity().getIntent().getIntExtra("SPOTIFY_TRACK_POSITION" , -1);
        final SpotifyArtistTrack spotifyArtistTrack = spotifyTrackPlayer.getSpotifyArtistTrackList().get(spotifyTrackPosition);
        final String PLAYBACK_URL = spotifyArtistTrack.getPreviewURL();
        final String ALBUM_ARTWORK_URL = spotifyArtistTrack.getAlbumArtLargeThumbnailURL();

        Picasso.with(getActivity()).load(ALBUM_ARTWORK_URL).into(albumArtWorkImageView);

        Log.d(TAG, "onCreateView " + PLAYBACK_URL);
        mediaPlayer = new MediaPlayer();
        playTrack(PLAYBACK_URL);

        nextTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++spotifyTrackPosition;
                playTrack(spotifyTrackPlayer.getSpotifyArtistTrackList().get(spotifyTrackPosition).getPreviewURL());
            }
        });

        return rootView;
    }

    private void playTrack(String PLAYBACK_URL) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(PLAYBACK_URL);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }
}
