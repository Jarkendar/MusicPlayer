package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
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
        double maxLength = holder.titleText.getMeasuredWidth()/Resources.getSystem().getDisplayMetrics().scaledDensity - 35.0;
        holder.titleText.setText(prepareStringToDisplay(tracks.get(position).getTitle(), maxLength));
        holder.artistText.setText(prepareStringToDisplay(tracks.get(position).getArtist(), maxLength));
        holder.durationText.setText(tracks.get(position).getDuration());
        if (holder.track.isFavorite()){
            holder.favoriteButton.setImageResource(R.drawable.ic_music_note_quarter_red_24dp);
        }else {
            holder.favoriteButton.setImageResource(R.drawable.ic_music_note_half_white_24dp);
        }
    }

    private String prepareStringToDisplay(String sequence, double maxLength) {
        int cutLetter = -1;
        String text;
        do {
            ++cutLetter;
            text = sequence.substring(0, sequence.length() - cutLetter);
        } while (calculateWidth(text) > maxLength);
        return cutLetter == 0 ? text : text.trim() + "...";
    }


    private double calculateWidth (String text) {
        Rect bounds = new Rect();
        TextView textView = new TextView(context);
        textView.getPaint().getTextBounds(text, 0, text.length(), bounds);
        double length = bounds.width();
        return length / Resources.getSystem().getDisplayMetrics().scaledDensity;
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
                    player.getListManager().changeFavoriteForTrack(track);
                    if (track.isFavorite()){
                        favoriteButton.setImageResource(R.drawable.ic_music_note_quarter_red_24dp);
                    }else {
                        favoriteButton.setImageResource(R.drawable.ic_music_note_half_white_24dp);
                    }
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
