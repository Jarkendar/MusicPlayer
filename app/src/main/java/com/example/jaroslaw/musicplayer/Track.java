package com.example.jaroslaw.musicplayer;

public class Track {
    private String artist;
    private String title;
    private String data;
    private String displayName;
    private String duration;
    private long lDuration;

    public Track(String artist, String title, String data, String displayName, long duration) {
        this.artist = artist;
        this.title = title;
        this.data = data;
        this.displayName = displayName;
        this.lDuration = duration;
        this.duration = duration /(60*1000)+":"+ (duration %(60*1000)/1000 < 10 ? "0"+duration %(60*1000)/1000 : duration %(60*1000)/1000);
    }

    public String getArtist() {
        return artist;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public String getDuration() {
        return duration;
    }

    public long getlDuration() {
        return lDuration;
    }

    @Override
    public String toString() {
        return "Track{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", data='" + data + '\'' +
                ", displayName='" + displayName + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
