package com.priteshsankhe.spotifystreamer.playback;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtistTrack;
import com.priteshsankhe.spotifystreamer.models.SpotifyTrackPlayer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaybackActivityFragment extends Fragment {

    private static final String TAG = PlaybackActivityFragment.class.getSimpleName();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private static String playbackURL = null;
    private MediaPlayer mediaPlayer;
    private SpotifyTrackPlayer spotifyTrackPlayer;

    @Bind(R.id.album_artwork_image_view)
    ImageView albumArtWorkImageView;

    @Bind(R.id.track_name_text_view)
    TextView trackNameTextView;

    @Bind(R.id.artist_name_text_view)
    TextView artistNameTextView;

    @Bind(R.id.track_progress)
    SeekBar trackProgressSeekbar;

    @Bind(R.id.track_length_progress_text_view)
    TextView trackProgressLengthTextView;

    @Bind(R.id.track_total_length_text_view)
    TextView trackTotalLengthTextView;

    @Bind(R.id.previous_track_button)
    ImageButton previousTrackButton;

    @Bind(R.id.pause_track_button)
    ImageButton pauseTrackButton;

    @Bind(R.id.next_track_button)
    ImageButton nextTrackButton;

    int spotifyTrackPosition = 0;
    private final Handler handler = new Handler();
    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mScheduleFuture;

    public PlaybackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback, container, false);
        ButterKnife.bind(this, rootView);

        spotifyTrackPlayer = getActivity().getIntent().getParcelableExtra("SPOTIFY_TRACK");
        spotifyTrackPosition = getActivity().getIntent().getIntExtra("SPOTIFY_TRACK_POSITION", -1);
        mediaPlayer = new MediaPlayer();
        setUpPlaybackUI();
        playTrack(playbackURL);

        nextTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextTrack();
            }
        });

        previousTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousTrack();
            }
        });

        pauseTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });

        trackProgressSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress() * 1000);
                scheduleSeekbarUpdate();
            }
        });

        return rootView;
    }

    private void playNextTrack() {
        if (spotifyTrackPosition + 1 < spotifyTrackPlayer.getSpotifyArtistTrackList().size()) {
            ++spotifyTrackPosition;
            setUpPlaybackUI();
            playTrack(playbackURL);
        } else {
            Toast.makeText(getActivity(), "You have reached the end of the playlist", Toast.LENGTH_LONG).show();
        }
    }

    private void playPreviousTrack() {
        if (spotifyTrackPosition - 1 >= 0) {
            --spotifyTrackPosition;
            setUpPlaybackUI();
            playTrack(playbackURL);
        } else {
            Toast.makeText(getActivity(), "You have reached the beginning of the playlist", Toast.LENGTH_LONG).show();
        }
    }


    private void setUpPlaybackUI() {
        final SpotifyArtistTrack spotifyArtistTrack = spotifyTrackPlayer.getSpotifyArtistTrackList().get(spotifyTrackPosition);
        Log.d(TAG, "setUpPlaybackUI " + spotifyArtistTrack.toString());
        playbackURL = spotifyArtistTrack.getPreviewURL();
        final String albumArtworkUrl = spotifyArtistTrack.getAlbumArtLargeThumbnailURL();
        final String trackName = spotifyArtistTrack.getTrackName();
        final String albumName = spotifyArtistTrack.getAlbumName();

        Picasso.with(getActivity()).load(albumArtworkUrl).fit().centerCrop().into(albumArtWorkImageView);
        trackNameTextView.setText(trackName.toString().trim());
        artistNameTextView.setText(albumName.toString().trim());
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
                    mp.start();
                    trackProgressSeekbar.setMax((int) mp.getDuration() / 1000);
                    scheduleSeekbarUpdate();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void scheduleSeekbarUpdate() {
        if (!executorService.isShutdown()) {
            mScheduleFuture = executorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            handler.post(updateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void updateProgress() {
        if (mediaPlayer == null) {
            return;
        }

        final String currentProgress = formatMillis(mediaPlayer.getCurrentPosition());
        trackProgressLengthTextView.setText(currentProgress);
        trackTotalLengthTextView.setText(formatMillis(mediaPlayer.getDuration()));
        trackProgressSeekbar.setProgress(mediaPlayer.getCurrentPosition() / 1000);
    }

    public static String formatMillis(int millisec) {
        int seconds = millisec / 1000;
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;
        String time;
        if (hours > 0) {
            time = String.format("%d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)});
        } else {
            time = String.format("%d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)});
        }

        return time;
    }
}
