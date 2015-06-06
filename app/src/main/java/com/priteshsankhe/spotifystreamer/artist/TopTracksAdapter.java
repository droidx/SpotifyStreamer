package com.priteshsankhe.spotifystreamer.artist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtistTrack;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Pritesh on 6/5/2015.
 */
public class TopTracksAdapter extends RecyclerView.Adapter<TopTracksAdapter.TopTracksViewHolder> {

    private static final String TAG = TopTracksAdapter.class.getSimpleName();

    private List<SpotifyArtistTrack> spotifyArtistTrackList;
    private Context context;

    public TopTracksAdapter(Context context, List<SpotifyArtistTrack> spotifyArtistTrackList) {
        this.context = context;
        this.spotifyArtistTrackList = spotifyArtistTrackList;
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
        Picasso.with(context).load(spotifyArtistTrack.getAlbumArtSmallThumbnailURL()).resize(200,200).centerCrop().into(topTracksViewHolder.getTopTrackThumbnail());
        topTracksViewHolder.getTopTrackTrackNameTextView().setText(spotifyArtistTrack.getTrackName().trim().toString());
        topTracksViewHolder.getTopTrackAlbumNameTextView().setText(spotifyArtistTrack.getAlbumName().trim().toString());
    }

    @Override
    public int getItemCount() {
        return spotifyArtistTrackList.size();
    }

    public static class TopTracksViewHolder extends RecyclerView.ViewHolder {

        private final ImageView topTrackThumbnail;
        private final TextView topTrackTrackNameTextView;
        private final TextView topTrackAlbumNameTextView;

        private SpotifyArtistTrack topTrack;
        private static Context context;

        public TopTracksViewHolder(View itemView) {
            super(itemView);
            topTrackThumbnail = (ImageView) itemView.findViewById(R.id.list_item_top_track_thumbnail);
            topTrackTrackNameTextView = (TextView) itemView.findViewById(R.id.list_item_top_track_textview);
            topTrackAlbumNameTextView = (TextView) itemView.findViewById(R.id.list_item_top_track_album_textview);
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

        public SpotifyArtistTrack getTopTrack() {
            return topTrack;
        }

        public void setTopTrack(SpotifyArtistTrack topTrack) {
            this.topTrack = topTrack;
        }
    }

    public void setSpotifyArtistTrackList(List<SpotifyArtistTrack> spotifyArtistTrackList) {
        this.spotifyArtistTrackList = spotifyArtistTrackList;
    }
}
