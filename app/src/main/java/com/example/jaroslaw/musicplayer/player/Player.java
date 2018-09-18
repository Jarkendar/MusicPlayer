package com.example.jaroslaw.musicplayer.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.example.jaroslaw.musicplayer.Track;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class Player extends Observable implements IPlayer {

    private static final String TAG = "*******";

    private LinkedList<Observer> observers;

    private ListManager listManager;
    private Mode mode = Mode.QUEUE;
    private MediaPlayer mediaPlayer;
    private Handler refresher;
    private Runnable refreshSeekBarRun;

    public Player(Context context) {
        listManager = new ListManager(context);
        prepareMediaPlayer();
        readCurrentState();
        setRefreshSeekBar();
    }

    private void setRefreshSeekBar() {
        refresher = new Handler();
        refreshSeekBarRun = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    Log.d(TAG, "run: media = " + mediaPlayer.getCurrentPosition() + " current = " + listManager.getCurrentPlay().getCurrentDuration());
                    listManager.getCurrentPlay().setCurrentDuration(mediaPlayer.getCurrentPosition());
                    notifyObservers(PlayerMessages.UPDATE_CURRENT_TIME);
                    refresher.postDelayed(this, 1000);
                }
            }
        };
    }

    private void prepareMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        setOnCompletionListener(mediaPlayer);
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
                mediaPlayer.setDataSource(listManager.getCurrentPlay().getData());
                mediaPlayer.prepare();
                mediaPlayer.seekTo((int) listManager.getCurrentPlay().getCurrentDuration());
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        startRefresher();
        listManager.saveCurrentState();
    }

    @Override
    public void chooseAndPlay(String path) {
        Track chosenTrack = listManager.getSongFromList(path);
        if (chosenTrack == null) {
            return;
        }
        mediaPlayer.stop();
        listManager.chooseSong(chosenTrack, mode);
        prepareAndStart(listManager.getCurrentPlay().getData());
        startRefresher();
        listManager.saveCurrentState();
    }

    private void startRefresher() {
        refresher.postDelayed(refreshSeekBarRun, 1000);
    }

    private void stopRefresher() {
        refresher.removeCallbacks(refreshSeekBarRun);
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
        listManager.setDurationOnCurrentPlay(mediaPlayer.getCurrentPosition());
        stopRefresher();
        listManager.saveCurrentState();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
        listManager.setDurationOnCurrentPlay(0);
        stopRefresher();
        listManager.saveCurrentState();
    }

    @Override
    public void next() {
        listManager.setNext(mode);
        prepareAndStart(listManager.getCurrentPlay().getData());
        startRefresher();
        listManager.saveCurrentState();
    }

    @Override
    public void previous() {
        int currentPosition = 0;
        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
        listManager.setPrevious(currentPosition);
        prepareAndStart(listManager.getCurrentPlay().getData());
        startRefresher();
        listManager.saveCurrentState();
    }

    private void prepareAndStart(String path) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeLooping() {
        mediaPlayer.setLooping(!mediaPlayer.isLooping());
    }

    public boolean isLooping() {
        return mediaPlayer.isLooping();
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
            listManager.prepareQueueNextSongs(mode);
            notifyObservers(PlayerMessages.CHANGE_MODE);
        }
    }

    @Override
    public LinkedList getShortListPlayed() {
        return listManager.getShortListPlayed();
    }

    @Override
    public LinkedList getListPlayed() {
        return listManager.getListPlayed();
    }

    public int getCurrentPositionOnList() {
        return listManager.getCurrentPositionOnList();
    }

    private void setOnCompletionListener(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onCompletion: " + listManager.getCurrentPlay().getTitle());
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(listManager.getNextSongPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                notifyObservers(PlayerMessages.PLAY_NEXT_SONG);
            }
        });
    }

    public void seekSongTo(int progress) {
        mediaPlayer.seekTo(progress);
    }

    public void setAllTracks() {
        listManager.refreshTracksFromDatabase();
    }

    public Track getCurrentPlay() {
        return listManager.getCurrentPlay();
    }

    private void readCurrentState() {
        prepareSong(listManager.readCurrentState());
    }

    private void prepareSong(Track track) {
        if (track != null) {
            try {
                mediaPlayer.setDataSource(track.getData());
                mediaPlayer.prepare();
                mediaPlayer.seekTo((int) track.getCurrentDuration());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public ListManager getListManager() {
        return listManager;
    }
}
