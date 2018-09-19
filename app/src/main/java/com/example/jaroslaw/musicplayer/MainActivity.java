package com.example.jaroslaw.musicplayer;


import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.jaroslaw.musicplayer.player.Player;
import com.example.jaroslaw.musicplayer.player.PlayerMessages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements ActionBar.TabListener, PlayedFragment.OnFragmentInteractionListener, TrackFragment.OnListFragmentInteractionListener, HistoryFragment.OnListFragmentInteractionListener, Observer {

    private Player player;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final String TAG = "******";
    private TrackFragment trackFragment;
    private PlayedFragment playedFragment;
    private HistoryFragment historyFragment;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        player = new Player(getApplicationContext());
        player.addObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mayReadMusicFiles();
    }

    private void mayReadMusicFiles() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            refreshTrackFragment();
            setPlayerList();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //todo explanation to user

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            refreshTrackFragment();
            setPlayerList();
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshTrackFragment();
                    setPlayerList();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private LinkedList<Track> readMusicFiles() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION
            };

            Cursor cursor = getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);
            LinkedList<Track> songs = new LinkedList<>();
            while (cursor.moveToNext()) {
                songs.add(new Track(cursor.getString(1)
                        , cursor.getString(2)
                        , cursor.getString(3)
                        , cursor.getString(4)
                        , cursor.getLong(5)));
            }
            Log.d("*****", "readMusicFiles: " + Arrays.toString(songs.toArray()));
            cursor.close();
            return songs;
        }
        return new LinkedList<>();
    }

    private void refreshTrackFragment() {
        if (trackFragment != null) {
            LinkedList<Track> tracks = readMusicFiles();
            final BaseRefresher baseRefresher = new BaseRefresher();
            baseRefresher.execute(new ArrayList<Track>(tracks));
            trackFragment.refresh(tracks);
            refreshPlayerList();
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (baseRefresher.getStatus() == AsyncTask.Status.FINISHED){
                        refreshPlayerList();
                    }else {
                        handler.postDelayed(this, 500);
                    }
                }
            });
        }
    }

    private void refreshPlayerList() {
        player.setAllTracks();
        trackFragment.refresh(player.getListManager().getAllTracks());
    }

    private void setPlayerList() {
        player.setAllTracks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("URI********", "onFragmentInteraction: " + uri.toString());
    }

    @Override
    public void onListFragmentInteraction(Uri uri) {
        Log.d("URI********", "onFragmentInteraction: " + uri.toString());
        playedFragment.changeOnPlay();
        historyFragment.refresh(player.getListPlayed());
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    playedFragment = new PlayedFragment();
                    playedFragment.setPlayer(player);
                    return playedFragment;
                case 1:
                    trackFragment = new TrackFragment();
                    trackFragment.setPlayer(player);
                    return trackFragment;
                case 2:
                    historyFragment = new HistoryFragment();
                    historyFragment.setPlayer(player);
                    return historyFragment;
                default:
                    playedFragment = new PlayedFragment();
                    playedFragment.setPlayer(player);
                    return playedFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.played_tab);
                case 1:
                    return getString(R.string.tracks_list_tab);
                case 2:
                    return getString(R.string.history_list_tab);
            }
            return null;
        }
    }

    @Override
    public void update(Observable observable, Object object) {
        if (observable instanceof Player) {
            PlayerMessages message = (PlayerMessages) object;
            switch (message) {
                case PLAY_NEXT_SONG: {
                    playedFragment.refreshTimeTextAndSeekBar(0);
                    playedFragment.setShortList();
                    break;
                }
                case CHANGE_MODE: {
                    playedFragment.setShortList();
                    break;
                }
                case UPDATE_CURRENT_TIME: {
                    playedFragment.changeOnPlay();
                    playedFragment.refreshTimeTextAndSeekBar((int) player.getCurrentPlay().getCurrentDuration());
                }
            }
        }
    }

    private class BaseRefresher extends AsyncTask<List<Track>, Void, Void> {
        @Override
        protected Void doInBackground(List<Track>... linkedLists) {
            DataBaseLackey dataBaseLackey = new DataBaseLackey(getApplicationContext());
            synchronized (getApplicationContext()) {
                dataBaseLackey.updateTableTracks(dataBaseLackey.getWritableDatabase(), linkedLists[0]);
            }
            return null;
        }
    }
}
