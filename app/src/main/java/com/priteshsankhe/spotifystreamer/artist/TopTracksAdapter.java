package com.priteshsankhe.spotifystreamer.artist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.listeners.TopTrackSelectedListener;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtistTrack;
import com.priteshsankhe.spotifystreamer.models.SpotifyTrackPlayer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Pritesh on 6/5/2015.
 * RecyclerView adapter for spotify artist tracks data.
 */
public class TopTracksAdapter extends RecyclerView.Adapter<TopTracksAdapter.TopTracksViewHolder> {

    private static final String TAG = TopTracksAdapter.class.getSimpleName();

    private SpotifyTrackPlayer spotifyTrackPlayer;
    private List<SpotifyArtistTrack> spotifyArtistTrackList;
    private Context context;

    public TopTracksAdapter(Context context, SpotifyTrackPlayer spotifyTrackPlayer, TopTrackSelectedListener listener) {
        this.context = context;
        this.spotifyTrackPlayer = spotifyTrackPlayer;
        this.spotifyArtistTrackList = spotifyTrackPlayer.getSpotifyArtistTrackList();
        TopTracksViewHolder.topTrackSelectedListener = listener;
        TopTracksViewHolder.context = context;
    }

    @Override
    public TopTracksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_top_track, parent, false);
        return new TopTracksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TopTracksViewHolder topTracksViewHolder, int position) {
        SpotifyArtistTrack spotifyArtistTrack = spotifyArtistTrackList.get(position);
        Picasso.with(context).load(spotifyArtistTrack.getAlbumArtSmallThumbnailURL()).resize(200, 200).centerCrop().placeholder(R.drawable.ic_placeholder).into(topTracksViewHolder.getTopTrackThumbnail());
        topTracksViewHolder.getTopTrackTrackNameTextView().setText(spotifyArtistTrack.getTrackName().trim().toString());
        topTracksViewHolder.getTopTrackAlbumNameTextView().setText(spotifyArtistTrack.getAlbumName().trim().toString());
        topTracksViewHolder.setSpotifyTrackPlayer(spotifyTrackPlayer);
    }

    @Override
    public int getItemCount() {
        return spotifyArtistTrackList.size();
    }

    public static class TopTracksViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.list_item_top_track_thumbnail)
        ImageView topTrackThumbnail;

        @Bind(R.id.list_item_top_track_textview)
        TextView topTrackTrackNameTextView;

        @Bind(R.id.list_item_top_track_album_textview)
        TextView topTrackAlbumNameTextView;

        private SpotifyTrackPlayer spotifyTrackPlayer;
        private static Context context;
        private static TopTrackSelectedListener topTrackSelectedListener;

        public TopTracksViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topTrackSelectedListener.onTrackSelected(getAdapterPosition(), spotifyTrackPlayer);

                }
            });
        }

        public ImageView getTopTrackThumbnail() {
            return topTrackThumbnail;
        }

        public TextView getTopTrackTrackNameTextView() {
            return topTrackTrackNameTextView;
        }

        public TextView getTopTrackAlbumNameTextView() {
            return topTrackAlbumNameTextView;
        }

        public SpotifyTrackPlayer getSpotifyTrackPlayer() {
            return spotifyTrackPlayer;
        }

        public void setSpotifyTrackPlayer(SpotifyTrackPlayer spotifyTrackPlayer) {
            this.spotifyTrackPlayer = spotifyTrackPlayer;
        }
    }

    public void setSpotifyArtistTrackList(List<SpotifyArtistTrack> spotifyArtistTrackList) {
        this.spotifyArtistTrackList = spotifyArtistTrackList;
    }
}
