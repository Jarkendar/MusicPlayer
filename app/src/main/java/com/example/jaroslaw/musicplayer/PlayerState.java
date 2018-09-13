package com.example.jaroslaw.musicplayer;

import java.util.LinkedList;

public class PlayerState {
    private LinkedList<Track> history;
    private Track current;
    private LinkedList<Track> next;

    public PlayerState() {
        history = new LinkedList<>();
        next = new LinkedList<>();
    }

    public PlayerState(LinkedList<Track> history, Track current, LinkedList<Track> next) {
        this.history = history;
        this.current = current;
        this.next = next;
    }

    public LinkedList<Track> getHistory() {
        return history;
    }

    public Track getCurrent() {
        return current;
    }

    public LinkedList<Track> getNext() {
        return next;
    }

    public void addLastHistory(Track track){
        history.addLast(track);
    }

    public void addLastNext(Track track){
        next.addLast(track);
    }

    public void setCurrent(Track track){
        current = track;
    }
}
