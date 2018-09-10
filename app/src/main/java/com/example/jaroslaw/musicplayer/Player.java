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
    private Mode mode = Mode.QUEUE;
    private MediaPlayer mediaPlayer;
    private Handler handler;

    public Player(Context context) {
        this.context = context;
        dataBaseLackey = new DataBaseLackey(context);
        prepareMediaPlayer();
    }

    private void prepareMediaPlayer(){
        mediaPlayer = new MediaPlayer();
    }

    private void prepareQueueNextSongs(){
        switch (mode){
            case QUEUE:{

                break;
            }
            case RANDOM:{

                break;
            }
            case INDEX_RANDOM:{

                break;
            }
        }
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
        switch (mode){
            case QUEUE:{
                mode = Mode.RANDOM;
                break;
            }
            case RANDOM:{
                mode = Mode.INDEX_RANDOM;
                break;
            }
            case INDEX_RANDOM:{
                mode = Mode.QUEUE;
                break;
            }
        }
        prepareQueueNextSongs();
    }

    @Override
    public void getShortListPlayed() {

    }

    @Override
    public void getListPlayed() {

    }
}
