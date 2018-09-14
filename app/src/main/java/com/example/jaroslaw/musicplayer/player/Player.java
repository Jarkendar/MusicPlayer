package com.example.jaroslaw.musicplayer.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.jaroslaw.musicplayer.DataBaseLackey;
import com.example.jaroslaw.musicplayer.PlayerState;
import com.example.jaroslaw.musicplayer.Track;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class Player extends Observable implements IPlayer {

    private static final String TAG = "*******";

    private static final int NUMBER_OF_NEXT_SONGS = 10;
    private static final int NUMBER_OF_HISTORY_SONGS = 50;
    private static final int FIRST_FIFTH_SECONDS = 5 * 1000;
    public static final String PLAY_NEXT_SONG = "PLAY_NEXT_SONG";
    public static final String CHANGE_MODE = "CHANGE_MODE";

    private LinkedList<Observer> observers;

    private LinkedList<Track> allTracks;//todo think about load this list in constructor
    private DataBaseLackey dataBaseLackey;
    private LinkedList<Track> willBePlayed = new LinkedList<>();
    private Track currentPlay;
    private LinkedList<Track> history = new LinkedList<>();
    private Mode mode = Mode.QUEUE;
    private MediaPlayer mediaPlayer;

    public Player(Context context) {
        dataBaseLackey = new DataBaseLackey(context);
        prepareMediaPlayer();
        readCurrentState();
        //todo check state, eventually prepare lists
    }

    private void prepareMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        setOnCompletionListener(mediaPlayer);
    }

    private synchronized void prepareQueueNextSongs() {
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

    @Override
    public void start() {
        try {
            Log.d(TAG, "start: isPlaying=" + mediaPlayer.isPlaying() + " position=" + mediaPlayer.getCurrentPosition());
            if (!mediaPlayer.isPlaying() && mediaPlayer.getCurrentPosition() > 1) {
                Log.d(TAG, "start: pause");
                mediaPlayer.start();
            } else {
                Log.d(TAG, "start: not pause");
                mediaPlayer.reset();
                mediaPlayer.setDataSource(currentPlay.getData());
                mediaPlayer.prepare();
                mediaPlayer.seekTo((int) currentPlay.getCurrentDuration());
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveCurrentState();
    }

    @Override
    public void chooseAndPlay(String path) {
        Track chosenTrack = getSongFromList(path);
        if (chosenTrack == null) {
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

    private Track getSongFromList(String path) {
        Log.d(TAG, "getSongFromList: " + allTracks.size() + " " + path);
        for (Track track : allTracks) {
            if (track.getData().equals(path)) {
                return track;
            }
        }
        return null;
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
        currentPlay.setCurrentDuration(mediaPlayer.getCurrentPosition());
        saveCurrentState();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
        currentPlay.setCurrentDuration(0);
        saveCurrentState();
    }

    @Override
    public void next() {
        addTrackToHistory(currentPlay);
        currentPlay = willBePlayed.getFirst();
        willBePlayed.removeFirst();
        generateNextSong();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentPlay.getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveCurrentState();
    }

    @Override
    public void previous() {
        int currentPosition = 0;
        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
        if (currentPosition > FIRST_FIFTH_SECONDS || history.size() == 0) {
            try {
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            willBePlayed.addFirst(currentPlay);
            currentPlay = history.getFirst();
            history.removeFirst();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(currentPlay.getData());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveCurrentState();
    }

    @Override
    public void setLoopingParam() {
        mediaPlayer.setLooping(!mediaPlayer.isLooping());
    }

    @Override
    public void changeMode() {
        if (!mediaPlayer.isLooping()) {
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
            Log.d(TAG, "changeMode: now = " + mode);
            prepareQueueNextSongs();
            prepareNextSong();
            notifyObservers(CHANGE_MODE);
        }
    }

    @Override
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

    @Override
    public LinkedList<Track> getListPlayed() {//full list {history.reverse,  current, next}
        LinkedList<Track> list = new LinkedList<>();
        list.addAll(history);
        Collections.reverse(list);
        list.addLast(currentPlay);
        list.addAll(list.size(), willBePlayed);
        return list;
    }

    private void generateNextSong() {
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

    private void prepareNextSong() {
        MediaPlayer next = new MediaPlayer();
        try {
            next.setDataSource(willBePlayed.getFirst().getData());
            next.prepare();
            setOnCompletionListener(next);
            mediaPlayer.setNextMediaPlayer(next);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setOnCompletionListener(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onCompletion: " + currentPlay.getTitle());
                prepareNextSong();
                notifyObservers(PLAY_NEXT_SONG);
            }
        });
    }

    private void addNextLastTrackQueue() {
        Track lastTrack = willBePlayed.getLast();
        int indextOfLast = allTracks.indexOf(lastTrack);
        if (allTracks.size() != 0) {
            if (indextOfLast == allTracks.size() - 1) {
                willBePlayed.addLast(allTracks.get(0));
            } else {
                willBePlayed.addLast(allTracks.get(indextOfLast + 1));
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

    private void saveCurrentState() {
        dataBaseLackey.saveState(dataBaseLackey.getWritableDatabase(), new PlayerState(history, currentPlay, willBePlayed));
    }

    private void readCurrentState() {
        PlayerState state = dataBaseLackey.readStateFromDatabase(dataBaseLackey.getReadableDatabase());
        history = state.getHistory();
        currentPlay = state.getCurrent();
        willBePlayed = state.getNext();
        if (currentPlay != null) {
            try {
                mediaPlayer.setDataSource(currentPlay.getData());
                mediaPlayer.prepare();
                mediaPlayer.seekTo((int) currentPlay.getCurrentDuration());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAllTracks(LinkedList<Track> allTracks) {
        this.allTracks = allTracks;
    }

    public Track getCurrentPlay() {
        return currentPlay;
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        super.addObserver(observer);
        if (observers == null) {
            observers = new LinkedList<>();
        }
        observers.addLast(observer);
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers(Object object) {
        super.notifyObservers(object);
        for (Observer observer : observers) {
            observer.update(this, object);
        }
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
        if (observers != null) {
            observers.clear();
        }
        observers = null;
    }
}
