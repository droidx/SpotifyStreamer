package com.priteshsankhe.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Pritesh on 6/29/2015.
 * POJO for toptracks playback for a artist
 */
public class SpotifyTrackPlayer implements Parcelable {

    private SpotifyArtist artist;
    private List<SpotifyArtistTrack> spotifyArtistTrackList;

    public SpotifyTrackPlayer() {
    }

    public SpotifyArtist getArtist() {
        return artist;
    }

    public List<SpotifyArtistTrack> getSpotifyArtistTrackList() {
        return spotifyArtistTrackList;
    }

    public SpotifyTrackPlayer(SpotifyArtist artist, List<SpotifyArtistTrack> trackList){
        this.artist = artist;
        this.spotifyArtistTrackList = trackList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.artist, 0);
        dest.writeTypedList(spotifyArtistTrackList);
    }

    protected SpotifyTrackPlayer(Parcel in) {
        this.artist = in.readParcelable(SpotifyArtist.class.getClassLoader());
        this.spotifyArtistTrackList = in.createTypedArrayList(SpotifyArtistTrack.CREATOR);
    }

    public static final Parcelable.Creator<SpotifyTrackPlayer> CREATOR = new Parcelable.Creator<SpotifyTrackPlayer>() {
        public SpotifyTrackPlayer createFromParcel(Parcel source) {
            return new SpotifyTrackPlayer(source);
        }

        public SpotifyTrackPlayer[] newArray(int size) {
            return new SpotifyTrackPlayer[size];
        }
    };

    public void setArtist(SpotifyArtist artist) {
        this.artist = artist;
    }

    public void setSpotifyArtistTrackList(List<SpotifyArtistTrack> spotifyArtistTrackList) {
        this.spotifyArtistTrackList = spotifyArtistTrackList;
    }
}
