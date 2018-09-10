package com.example.jaroslaw.musicplayer.player;

public interface IPlayer {
    public void start();
    public void chooseAndPlay(String path);
    public void stop();
    public void next();
    public void previous();
    public void refreshSong();
    public void changeMode();
    public void getShortListPlayed();
    public void getListPlayed();
}
