package com.priteshsankhe.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pritesh on 6/5/2015.
 */
public class SpotifyArtistTrack implements Parcelable{

    private String trackName;
    private String albumName;
    private String albumArtSmallThumbnailURL;
    private String albumArtLargeThumbnailURL;
    private String previewURL;

    public SpotifyArtistTrack(String trackName, String albumName, String albumArtSmallThumbnailURL, String albumArtLargeThumbnailURL, String previewURL) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.albumArtSmallThumbnailURL = albumArtSmallThumbnailURL;
        this.albumArtLargeThumbnailURL = albumArtLargeThumbnailURL;
        this.previewURL = previewURL;
    }

    public SpotifyArtistTrack(Parcel in){
        trackName = in.readString();
        albumName = in.readString();
        albumArtSmallThumbnailURL = in.readString();
        albumArtLargeThumbnailURL = in.readString();
        previewURL = in.readString();
    }

    public String getTrackName() {
        return trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumArtSmallThumbnailURL() {
        return albumArtSmallThumbnailURL;
    }

    public String getAlbumArtLargeThumbnailURL() {
        return albumArtLargeThumbnailURL;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    @Override
    public String toString() {
        return "trackName : " + getTrackName() + "\t" +
                "albumName : " + getAlbumName() + "\t" +
                "album Small Thumbnail URL : " + getAlbumArtSmallThumbnailURL() + "\t" +
                "album Large Thumbnail URL : " + getAlbumArtLargeThumbnailURL() + "\t" +
                "preview URL : " + getPreviewURL();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackName);
        dest.writeString(albumName);
        dest.writeString(albumArtSmallThumbnailURL);
        dest.writeString(albumArtLargeThumbnailURL);
        dest.writeString(previewURL);

    }

    public static final Parcelable.Creator<SpotifyArtistTrack> CREATOR = new Parcelable.ClassLoaderCreator<SpotifyArtistTrack>(){

        @Override
        public SpotifyArtistTrack createFromParcel(Parcel source) {
            return new SpotifyArtistTrack(source);
        }

        @Override
        public SpotifyArtistTrack[] newArray(int size) {
            return new SpotifyArtistTrack[size];
        }

        @Override
        public SpotifyArtistTrack createFromParcel(Parcel source, ClassLoader loader) {
            return new SpotifyArtistTrack(source);
        }
    };
}
