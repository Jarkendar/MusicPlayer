package com.example.jaroslaw.musicplayer.player;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.jaroslaw.musicplayer.DataBaseLackey;
import com.example.jaroslaw.musicplayer.Track;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Handler;

public class Player implements IPlayer {

    private static final int NUMBER_OF_NEXT_SONGS = 10;
    private static final int NUMBER_OF_HISTORY_SONGS = 50;

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
                willBePlayed = createRandomSongsList();
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

    private LinkedList<Track> createRandomSongsList(){
        if (allTracks.size() == 0){
            return new LinkedList<>();
        }else {
            Random random = new Random(System.currentTimeMillis());
            LinkedList<Track> list = new LinkedList<>();
            for (int i = 0; i<NUMBER_OF_NEXT_SONGS; ++i){
                list.add(allTracks.get(random.nextInt(allTracks.size())));
            }
            return list;
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void chooseAndPlay(String path) {
        Track chosenTrack = getSongFromList(path);
        if (chosenTrack == null){
            return;
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        currentPlay = chosenTrack;
        try {
            mediaPlayer.setDataSource(currentPlay.getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
            prepareQueueNextSongs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveCurrentState();
        prepareNextSong();
    }

    private Track getSongFromList(String path){
        for (Track track: allTracks){
            if (track.getData().equals(path)){
                return track;
            }
        }
        return null;
    }

    @Override
    public void pause() {

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
    public void setLoopingParam() {
        mediaPlayer.setLooping(!mediaPlayer.isLooping());
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
        prepareNextSong();
    }

    @Override
    public LinkedList<Track> getShortListPlayed() {//current song and 2-3 next
        LinkedList<Track> list = new LinkedList<>();
        list.addLast(currentPlay);
        list.addLast(willBePlayed.get(0));
        list.addLast(willBePlayed.get(1));
        list.addLast(willBePlayed.get(2));
        return list;
    }

    @Override
    public LinkedList<Track> getListPlayed() {//full list {history.reverse,  current, next}
        LinkedList<Track> list = new LinkedList<>();
        list.addAll(history);
        Collections.reverse(list);
        list.addLast(currentPlay);
        list.addAll(list.size(), willBePlayed);
        return list;
    }

    private void generateNextSong(){
        switch (mode) {
            case QUEUE: {
                addNextLastTrackQueue();
                break;
            }
            case RANDOM: {
                addNextLastTrackRandom();
                break;
            }
            case INDEX_RANDOM: {
                //todo in future
                break;
            }
        }
    }

    private void prepareNextSong(){
        MediaPlayer next = new MediaPlayer();
        try {
            next.setDataSource(willBePlayed.getFirst().getData());
            next.prepare();
            mediaPlayer.setNextMediaPlayer(next);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNextLastTrackQueue(){
        Track lastTrack = willBePlayed.getLast();
        int indextOfLast = allTracks.indexOf(lastTrack);
        if (allTracks.size() != 0) {
            if (indextOfLast == allTracks.size() -1){
                willBePlayed.addLast(allTracks.get(0));
            }else {
                willBePlayed.addLast(allTracks.get(indextOfLast+1));
            }
        }
    }

    private void addNextLastTrackRandom() {
        Random random = new Random(System.currentTimeMillis());
        if (allTracks.size() != 0) {
            willBePlayed.addLast(allTracks.get(random.nextInt(allTracks.size())));
        }
    }



    private void addTrackToHistory(Track track){
        history.addFirst(track);
        while (history.size() > NUMBER_OF_HISTORY_SONGS){
            history.removeLast();
        }
    }

    private void saveCurrentState(){

    }
}
