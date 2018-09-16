package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jaroslaw.musicplayer.HistoryFragment.OnListFragmentInteractionListener;
import com.example.jaroslaw.musicplayer.player.Player;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyHistoryRecyclerViewAdapter extends RecyclerView.Adapter<MyHistoryRecyclerViewAdapter.ViewHolder> {

    private List<Track> tracks;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private Player player;

    public MyHistoryRecyclerViewAdapter(List<Track> items, OnListFragmentInteractionListener listener, Context context, Player player) {
        tracks = items;
        mListener = listener;
        this.context = context;
        this.player = player;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.track = tracks.get(position);
        holder.titleText.setText(tracks.get(position).getTitle());
        holder.artistText.setText(tracks.get(position).getArtist());
        holder.durationText.setText(tracks.get(position).getDuration());
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
        public Track track;
        public Context context;

        public ViewHolder(View view, Context context) {
            super(view);
            mView = view;
            titleText = (TextView) view.findViewById(R.id.history_title);
            artistText = (TextView) view.findViewById(R.id.history_artist);
            durationText = (TextView) view.findViewById(R.id.history_duration);
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
