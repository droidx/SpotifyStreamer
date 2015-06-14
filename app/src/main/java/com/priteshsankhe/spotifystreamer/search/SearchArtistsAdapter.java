package com.priteshsankhe.spotifystreamer.search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.artist.TopTracksActivity;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtist;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Pritesh on 6/4/2015.
 */
public class SearchArtistsAdapter extends RecyclerView.Adapter<SearchArtistsAdapter.SpotifyArtistViewHolder> {

    private static final String TAG = SearchArtistsAdapter.class.getSimpleName();

    private Context context;
    private List<SpotifyArtist> spotifyArtistList;

    public SearchArtistsAdapter(Context context, List<SpotifyArtist> spotifyArtistList) {
        this.context = context;
        this.spotifyArtistList = spotifyArtistList;
        SpotifyArtistViewHolder.context = context;
    }

    @Override
    public SpotifyArtistViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_search_result, viewGroup, false);
        return new SpotifyArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SpotifyArtistViewHolder spotifyArtistViewHolder, int i) {
        SpotifyArtist spotifyArtist = spotifyArtistList.get(i);

        if (spotifyArtist.getArtistThumbnailImageURL() == null) {
            Picasso.with(context).load(R.drawable.ic_placeholder).into(spotifyArtistViewHolder.getSpotifyArtistThumbnail());
        } else {
            Picasso.with(context).load(spotifyArtist.getArtistThumbnailImageURL()).resize(200, 200).centerCrop().placeholder(R.drawable.ic_placeholder).into(spotifyArtistViewHolder.getSpotifyArtistThumbnail());
        }
        spotifyArtistViewHolder.getSpotifyArtistNameTextView().setText(spotifyArtist.getArtistName());
        spotifyArtistViewHolder.setArtist(spotifyArtistList.get(i));
    }

    @Override
    public int getItemCount() {
        return spotifyArtistList.size();
    }

    public static class SpotifyArtistViewHolder extends RecyclerView.ViewHolder {

        private final ImageView spotifyArtistThumbnail;
        private final TextView spotifyArtistNameTextView;

        private SpotifyArtist artist;
        private static Context context;

        public SpotifyArtistViewHolder(View itemView) {
            super(itemView);
            spotifyArtistThumbnail = (ImageView) itemView.findViewById(R.id.list_item_search_result_thumbnail);
            spotifyArtistNameTextView = (TextView) itemView.findViewById(R.id.list_item_search_result_textview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked " + artist.getArtistName());
                    Intent intent = new Intent(context, TopTracksActivity.class);
                    intent.putExtra("artistId", artist.getSpotifyArtistId());
                    intent.putExtra("artistName", artist.getArtistName());
                    context.startActivity(intent);
                }
            });
        }

        public ImageView getSpotifyArtistThumbnail() {
            return spotifyArtistThumbnail;
        }

        public TextView getSpotifyArtistNameTextView() {
            return spotifyArtistNameTextView;
        }

        public void setArtist(SpotifyArtist artist) {
            this.artist = artist;
        }
    }

    public void setSpotifyArtistList(List<SpotifyArtist> spotifyArtistList) {
        this.spotifyArtistList = spotifyArtistList;
    }
}
