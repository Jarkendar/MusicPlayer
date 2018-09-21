package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jaroslaw.musicplayer.TrackFragment.OnListFragmentInteractionListener;
import com.example.jaroslaw.musicplayer.player.Player;

import java.util.List;

public class MyTrackRecyclerViewAdapter extends RecyclerView.Adapter<MyTrackRecyclerViewAdapter.ViewHolder> {

    private List<Track> tracks;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private Player player;

    public MyTrackRecyclerViewAdapter(List<Track> items, OnListFragmentInteractionListener listener, Context context, Player player) {
        tracks = items;
        mListener = listener;
        this.context = context;
        this.player = player;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_track, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.track = tracks.get(position);
        holder.titleText.setText(prepareStringToDisplay(tracks.get(position).getTitle()));
        holder.artistText.setText(prepareStringToDisplay(tracks.get(position).getArtist()));
        holder.durationText.setText(tracks.get(position).getDuration());
    }

    private String prepareStringToDisplay(String string){
        int maxLength = 50;
        if (string.length() > maxLength){
            string = string.substring(0,maxLength)+"...";
        }
        return string;
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
        public final ImageButton favoriteButton;
        public Track track;
        public Context context;

        public ViewHolder(View view, Context context) {
            super(view);
            mView = view;
            titleText = view.findViewById(R.id.item_title);
            artistText = view.findViewById(R.id.item_artist);
            durationText = view.findViewById(R.id.item_duration);
            favoriteButton = view.findViewById(R.id.favorite_button);
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("****", "onClickFavorite: "+getAdapterPosition());
                }
            });
            this.context = context;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("*****", "onClick: "+getAdapterPosition());
                    player.chooseAndPlay(track.getData());
                    mListener.onListFragmentInteraction( Uri.EMPTY);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + durationText.getText() + "'";
        }
    }
}
