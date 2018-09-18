package com.example.jaroslaw.musicplayer.player;

import android.content.Context;
import android.util.Log;

import com.example.jaroslaw.musicplayer.DataBaseLackey;
import com.example.jaroslaw.musicplayer.PlayerState;
import com.example.jaroslaw.musicplayer.Track;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class ListManager {

    private static final String TAG = "*******";

    private static final int FIRST_FIFTH_SECONDS = 5 * 1000;
    private static final int NUMBER_OF_NEXT_SONGS = 10;
    private static final int NUMBER_OF_HISTORY_SONGS = 50;

    private DataBaseLackey dataBaseLackey;
    private LinkedList<Track> allTracks;
    private LinkedList<Track> willBePlayed = new LinkedList<>();
    private Track currentPlay;
    private LinkedList<Track> history = new LinkedList<>();
    private int currentPositionOnList = -1;


    public ListManager(Context context){
        dataBaseLackey = new DataBaseLackey(context);
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


    private LinkedList<Track> createRandomSongsList() {
        if (allTracks.size() == 0) {
            return new LinkedList<>();
        } else {
            Random random = new Random(System.currentTimeMillis());
            LinkedList<Track> list = new LinkedList<>();
            for (int i = 0; i < NUMBER_OF_NEXT_SONGS; ++i) {
                list.add(allTracks.get(random.nextInt(allTracks.size())));
            }
            return list;
        }
    }

    public Track getSongFromList(String path) {
        Log.d(TAG, "getSongFromList: " + allTracks.size() + " " + path);
        for (Track track : allTracks) {
            if (track.getData().equals(path)) {
                return track;
            }
        }
        return null;
    }

    public LinkedList<Track> getShortListPlayed() {//current song and 2-3 next
        LinkedList<Track> list = new LinkedList<>();
        if (history.size() != 0) {
            list.addLast(history.getFirst());
        }
        list.addLast(currentPlay);
        if (willBePlayed.size() > 0) {
            list.addLast(willBePlayed.get(0));
            list.addLast(willBePlayed.get(1));
            list.addLast(willBePlayed.get(2));
        }
        return list;
    }

    public LinkedList<Track> getListPlayed() {//full list {history.reverse,  current, next}
        LinkedList<Track> list = new LinkedList<>();
        list.addAll(willBePlayed);
        Collections.reverse(list);
        currentPositionOnList = list.size();
        list.addLast(currentPlay);
        list.addAll(list.size(), history);
        return list;
    }

    public synchronized void prepareQueueNextSongs(Mode mode) {
        willBePlayed.clear();
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
                willBePlayed = createRandomSongsList();
                //todo in future
                break;
            }
        }
        saveCurrentState();
    }

    private void generateNextSong(Mode mode) {
        if (willBePlayed.size() < NUMBER_OF_NEXT_SONGS) {
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
                    addNextLastTrackRandom();
                    //todo in future
                    break;
                }
            }
        }
    }


    private void addNextLastTrackQueue() {
        Track lastTrack = willBePlayed.getLast();
        int indexOfLast = allTracks.indexOf(lastTrack);
        if (allTracks.size() != 0) {
            if (indexOfLast == allTracks.size() - 1) {
                willBePlayed.addLast(allTracks.get(0));
            } else {
                willBePlayed.addLast(allTracks.get(indexOfLast + 1));
            }
        }
    }

    private void addNextLastTrackRandom() {
        Random random = new Random(System.currentTimeMillis());
        if (allTracks.size() != 0) {
            willBePlayed.addLast(allTracks.get(random.nextInt(allTracks.size())));
        }
    }

    private void addTrackToHistory(Track track) {
        history.addFirst(track);
        while (history.size() > NUMBER_OF_HISTORY_SONGS) {
            history.removeLast();
        }
    }

    public void saveCurrentState() {
        dataBaseLackey.saveState(dataBaseLackey.getWritableDatabase(), new PlayerState(history, currentPlay, willBePlayed));
    }

    public Track readCurrentState() {
        PlayerState state = dataBaseLackey.readStateFromDatabase(dataBaseLackey.getReadableDatabase());
        history = state.getHistory();
        currentPlay = state.getCurrent();
        willBePlayed = state.getNext();
        return currentPlay;
    }

    public void chooseSong(Track track, Mode mode){
        currentPlay = track;
        prepareQueueNextSongs(mode);
    }

    public Track getCurrentPlay() {
        return currentPlay;
    }

    public void setAllTracks(LinkedList<Track> allTracks) {
        this.allTracks = allTracks;
    }

    public void setDurationOnCurrentPlay(int duration){
        currentPlay.setCurrentDuration(duration);
    }

    public void setNext(Mode mode){
        addTrackToHistory(currentPlay);
        currentPlay = willBePlayed.getFirst();
        willBePlayed.removeFirst();
        generateNextSong(mode);
    }

    public void setPrevious(int actualDuration){
        if (actualDuration <= FIRST_FIFTH_SECONDS && history.size() != 0) {
            willBePlayed.addFirst(currentPlay);
            currentPlay = history.getFirst();
            history.removeFirst();
        }
    }

    public int getCurrentPositionOnList() {
        return currentPositionOnList;
    }

    public String getNextSongPath(){
        return willBePlayed.getFirst().getData();
    }
}