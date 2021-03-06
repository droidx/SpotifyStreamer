package com.priteshsankhe.spotifystreamer.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.priteshsankhe.spotifystreamer.MainActivity;
import com.priteshsankhe.spotifystreamer.R;
import com.priteshsankhe.spotifystreamer.models.SpotifyArtist;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Pritesh on 6/4/2015.
 * RecyclerView adapter for spotify artist data.
 */
public class SearchArtistsAdapter extends RecyclerView.Adapter<SearchArtistsAdapter.SpotifyArtistViewHolder> {

    private static final String TAG = SearchArtistsAdapter.class.getSimpleName();

    private Context context;
    private List<SpotifyArtist> spotifyArtistList;
    private AdapterCallback adapterCallback;

    public SearchArtistsAdapter(Context context, final List<SpotifyArtist> spotifyArtistList) {
        this.context = context;
        this.spotifyArtistList = spotifyArtistList;
        SpotifyArtistViewHolder.context = context;
        this.adapterCallback = (MainActivity) context;
        SpotifyArtistViewHolder.adapterCallback = this.adapterCallback;
    }

    @Override
    public SpotifyArtistViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_search_result, viewGroup, false);
        return new SpotifyArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SpotifyArtistViewHolder spotifyArtistViewHolder, int i) {
        final SpotifyArtist spotifyArtist = spotifyArtistList.get(i);
        Picasso.with(context).load(spotifyArtist.getArtistThumbnailImageURL()).resize(200, 200).centerCrop().placeholder(R.drawable.ic_placeholder).into(spotifyArtistViewHolder.getSpotifyArtistThumbnail());
        spotifyArtistViewHolder.getSpotifyArtistNameTextView().setText(spotifyArtist.getArtistName());
        spotifyArtistViewHolder.setArtist(spotifyArtistList.get(i));
    }

    @Override
    public int getItemCount() {
        return spotifyArtistList.size();
    }

    public static class SpotifyArtistViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.list_item_search_result_thumbnail)
        ImageView spotifyArtistThumbnail;

        @Bind(R.id.list_item_search_result_textview)
        TextView spotifyArtistNameTextView;

        private SpotifyArtist artist;
        private static AdapterCallback adapterCallback;
        private static Context context;

        public SpotifyArtistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked " + artist.getArtistName());
                    adapterCallback.onItemSelected(artist);
                }
            });
        }

        public ImageView getSpotifyArtistThumbnail() {
            return spotifyArtistThumbnail;
        }

        public TextView getSpotifyArtistNameTextView() {
            return spotifyArtistNameTextView;
        }

        public void setArtist(final SpotifyArtist artist) {
            this.artist = artist;
        }
    }

    public void setSpotifyArtistList(final List<SpotifyArtist> spotifyArtistList) {
        this.spotifyArtistList = spotifyArtistList;
    }

    public static interface AdapterCallback {
        void onItemSelected(SpotifyArtist artist);
    }
}
