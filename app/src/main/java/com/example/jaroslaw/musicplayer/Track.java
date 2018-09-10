package com.example.jaroslaw.musicplayer;

public class Track {
    private String artist;
    private String title;
    private String data;
    private String displayName;
    private String duration;
    private long durationTime;

    public Track(String artist, String title, String data, String displayName, long duration) {
        this.artist = artist;
        this.title = title;
        this.data = data;
        this.displayName = displayName;
        this.durationTime = duration;
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

    public long getDurationTime() {
        return durationTime;
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
