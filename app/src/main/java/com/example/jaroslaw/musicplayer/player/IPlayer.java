package com.example.jaroslaw.musicplayer.player;

import java.util.LinkedList;

public interface IPlayer {
    public void start();
    public void chooseAndPlay(String path);
    public void pause();
    public void stop();
    public void next();
    public void previous();
    public void refreshSong();
    public void changeMode();
    public LinkedList getShortListPlayed();
    public LinkedList getListPlayed();
}
