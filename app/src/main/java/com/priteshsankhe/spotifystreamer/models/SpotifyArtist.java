package com.priteshsankhe.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable; /**
 * Created by Pritesh on 6/4/2015.
 */

/**
 * POJO for SpotifyArtist
 */
public class SpotifyArtist implements Parcelable {

    private String artistName;
    private String spotifyArtistId;
    private String artistThumbnailImageURL;

    public SpotifyArtist() {
    }

    public SpotifyArtist(String spotifyArtistId, String artistName, String artistThumbnailImageURL){
        this.spotifyArtistId = spotifyArtistId;
        this.artistName = artistName;
        this.artistThumbnailImageURL = artistThumbnailImageURL;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getSpotifyArtistId() {
        return spotifyArtistId;
    }

    public String getArtistThumbnailImageURL() {
        return artistThumbnailImageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistName);
        dest.writeString(this.spotifyArtistId);
        dest.writeString(this.artistThumbnailImageURL);
    }

    protected SpotifyArtist(Parcel in) {
        this.artistName = in.readString();
        this.spotifyArtistId = in.readString();
        this.artistThumbnailImageURL = in.readString();
    }

    public static final Parcelable.Creator<SpotifyArtist> CREATOR = new Parcelable.Creator<SpotifyArtist>() {
        public SpotifyArtist createFromParcel(Parcel source) {
            return new SpotifyArtist(source);
        }

        public SpotifyArtist[] newArray(int size) {
            return new SpotifyArtist[size];
        }
    };
}
