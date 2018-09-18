package com.example.jaroslaw.musicplayer;

public class Track {
    private String artist;
    private String title;
    private String data;
    private String displayName;
    private String duration;
    private long durationTime;
    private long currentDuration;
    private int playedTimes;

    public Track(String artist, String title, String data, String displayName, long duration) {
        this.artist = artist;
        this.title = title;
        this.data = data;
        this.displayName = displayName;
        this.durationTime = duration;
        this.duration = duration / (60 * 1000) + ":" + (duration % (60 * 1000) / 1000 < 10 ? "0" + duration % (60 * 1000) / 1000 : duration % (60 * 1000) / 1000);
    }

    public Track(String artist, String title, String data, String displayName, long duration, long currentDuration) {
        this(artist, title, data, displayName, duration);
        this.currentDuration = currentDuration;
    }

    public Track(String artist, String title, String data, String displayName, long duration, long currentDuration, int playedTimes) {
        this(artist, title, data, displayName, duration, currentDuration);
        this.playedTimes = playedTimes;
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

    public long getCurrentDuration(){
        return currentDuration;
    }

    public void setCurrentDuration(long currentDuration) {
        this.currentDuration = currentDuration;
    }

    public int getPlayedTimes() {
        return playedTimes;
    }

    public void incrementPlayedTimes() {
        playedTimes++;
    }

    @Override
    public String toString() {
        return "Track{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", data='" + data + '\'' +
                ", displayName='" + displayName + '\'' +
                ", duration='" + duration + '\'' +
                ", durationTime=" + durationTime +
                ", currentDuration=" + currentDuration +
                ", playedTimes=" + playedTimes +
                '}';
    }
}
