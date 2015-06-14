package com.priteshsankhe.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pritesh on 6/4/2015.
 */

/**
 * POJO for SpotifyArtist
 */
public class SpotifyArtist implements Parcelable{

    private String artistName;
    private String spotifyArtistId;
    private String artistThumbnailImageURL;

    public SpotifyArtist(String spotifyArtistId, String artistName, String artistThumbnailImageURL){
        this.spotifyArtistId = spotifyArtistId;
        this.artistName = artistName;
        this.artistThumbnailImageURL = artistThumbnailImageURL;
    }

    private SpotifyArtist(Parcel in){
        artistName = in.readString();
        spotifyArtistId = in.readString();
        artistThumbnailImageURL = in.readString();
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
        dest.writeString(spotifyArtistId);
        dest.writeString(artistName);
        dest.writeString(artistThumbnailImageURL);
    }

    public static final Parcelable.Creator<SpotifyArtist> CREATOR = new Parcelable.ClassLoaderCreator<SpotifyArtist>(){

        @Override
        public SpotifyArtist createFromParcel(Parcel source) {
            return new SpotifyArtist(source);
        }

        @Override
        public SpotifyArtist[] newArray(int size) {
            return new SpotifyArtist[size];
        }

        @Override
        public SpotifyArtist createFromParcel(Parcel source, ClassLoader loader) {
            return new SpotifyArtist(source);
        }
    };
}
