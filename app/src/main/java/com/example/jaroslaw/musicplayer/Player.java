package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.LinkedList;
import java.util.logging.Handler;

public class Player implements IPlayer {

    private Context context;
    private DataBaseLackey dataBaseLackey; //todo new table for list play
    private LinkedList<Track> willBePlayed;//todo 10 songs
    private Track currentPlay;
    private LinkedList<Track> history;// todo 50 songs
    private Mode mode;
    private MediaPlayer mediaPlayer;
    private Handler handler;

    public Player(Context context) {
        this.context = context;
        dataBaseLackey = new DataBaseLackey(context);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void start() {

    }

    @Override
    public void chooseAndPlay(String path) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }

    @Override
    public void refreshSong() {

    }

    @Override
    public void changeMode() {

    }

    @Override
    public void getShortListPlayed() {

    }

    @Override
    public void getListPlayed() {

    }
}
