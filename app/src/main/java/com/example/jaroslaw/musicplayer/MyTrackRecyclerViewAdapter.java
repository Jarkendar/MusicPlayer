package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jaroslaw.musicplayer.TrackFragment.OnListFragmentInteractionListener;

import java.io.IOException;
import java.util.List;

public class MyTrackRecyclerViewAdapter extends RecyclerView.Adapter<MyTrackRecyclerViewAdapter.ViewHolder> {

    private List<Track> tracks;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private MediaPlayer mediaPlayer;

    public MyTrackRecyclerViewAdapter(List<Track> items, OnListFragmentInteractionListener listener, Context context) {
        tracks = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_track, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = tracks.get(position);
        holder.titleText.setText(tracks.get(position).getTitle());
        holder.artistText.setText(tracks.get(position).getArtist());
        holder.durationText.setText(tracks.get(position).getDuration());
    }

    private void audioPlayer(Track track){
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(track.getData());
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView titleText;
        public final TextView artistText;
        public final TextView durationText;
        public Track mItem;
        public Context context;

        public ViewHolder(View view, Context context) {
            super(view);
            mView = view;
            titleText = (TextView) view.findViewById(R.id.item_title);
            artistText = (TextView) view.findViewById(R.id.item_artist);
            durationText = (TextView) view.findViewById(R.id.item_duration);
            this.context = context;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("*****", "onClick: "+getAdapterPosition());
                    audioPlayer(mItem);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + durationText.getText() + "'";
        }
    }
}
