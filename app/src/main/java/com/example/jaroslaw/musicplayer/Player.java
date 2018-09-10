package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.LinkedList;
import java.util.logging.Handler;

public class Player implements IPlayer {

    private static final int NUMBER_OF_NEXT_SONGS = 10;

    private Context context;
    private LinkedList<Track> allTracks;//todo think about load this list in constructor
    private DataBaseLackey dataBaseLackey; //todo new table for list play
    private LinkedList<Track> willBePlayed;//todo 10 songs
    private Track currentPlay;
    private LinkedList<Track> history;// todo 50 songs
    private Mode mode = Mode.QUEUE;
    private MediaPlayer mediaPlayer;
    private Handler handler;//todo at the end

    public Player(Context context) {
        this.context = context;
        dataBaseLackey = new DataBaseLackey(context);
        prepareMediaPlayer();
    }

    private void prepareMediaPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    private synchronized void prepareQueueNextSongs() {
        switch (mode) {
            case QUEUE: {
                willBePlayed = createQueueSongsList();
                break;
            }
            case RANDOM: {

                break;
            }
            case INDEX_RANDOM: {
                //todo in future
                break;
            }
        }
    }

    private LinkedList<Track> createQueueSongsList() {
        int startIndex = allTracks.indexOf(currentPlay) + 1;//+1 because i not want to current song
        if (startIndex == 0) {
            return new LinkedList<>();
        } else if (allTracks.size() != 0 && allTracks.size() < NUMBER_OF_NEXT_SONGS) {
            LinkedList<Track> list = new LinkedList<>();
            for (int i = 0, currentIndex = startIndex; i < NUMBER_OF_NEXT_SONGS; ++i) {
                if (currentIndex == allTracks.size()) {
                    currentIndex = 0;
                    list.add(allTracks.get(currentIndex));
                } else {
                    list.add(allTracks.get(currentIndex));
                }
                currentIndex++;
            }
            return list;
        } else if (startIndex + NUMBER_OF_NEXT_SONGS < allTracks.size()) {
            return new LinkedList<>(allTracks.subList(startIndex, startIndex + NUMBER_OF_NEXT_SONGS));
        } else if (startIndex + NUMBER_OF_NEXT_SONGS >= allTracks.size()) {
            int onStart = NUMBER_OF_NEXT_SONGS - (allTracks.size() - startIndex);
            LinkedList<Track> list = new LinkedList<>(allTracks.subList(startIndex, allTracks.size()));
            list.addAll(new LinkedList<>(allTracks.subList(0, onStart)));
            return list;
        } else {
            return new LinkedList<>();
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
        switch (mode) {
            case QUEUE: {
                mode = Mode.RANDOM;
                break;
            }
            case RANDOM: {
                mode = Mode.INDEX_RANDOM;
                break;
            }
            case INDEX_RANDOM: {
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
