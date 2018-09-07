package com.example.jaroslaw.musicplayer;

public class Track {
    private String name;
    private String path;
    private String length;

    public Track(String name, String path, long length) {
        this.name = name;
        this.path = path;
        this.length = length/(60*1000)+":"+length%(60*1000)/1000;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", length='" + length + '\'' +
                '}';
    }
}
