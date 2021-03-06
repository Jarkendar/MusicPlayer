package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jaroslaw.musicplayer.HistoryFragment.OnListFragmentInteractionListener;
import com.example.jaroslaw.musicplayer.player.Player;

import java.util.List;


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
        double maxLength = holder.titleText.getMeasuredWidth()/Resources.getSystem().getDisplayMetrics().scaledDensity - 35.0;
        holder.titleText.setText(prepareStringToDisplay(tracks.get(position).getTitle(),maxLength));
        holder.artistText.setText(prepareStringToDisplay(tracks.get(position).getArtist(), maxLength));
        holder.durationText.setText(tracks.get(position).getDuration());
        Log.d("*****", "onBindViewHolder: "+position+" "+player.getCurrentPositionOnList()+" "+tracks.size()+" "+holder.mView.getBackground().toString());
        if (position == player.getCurrentPositionOnList() && player.getCurrentPositionOnList() == tracks.size() - 1) {
            holder.setBackground(R.drawable.end_current_history_background);
        } else if (position == player.getCurrentPositionOnList()) {
            holder.setBackground(R.drawable.current_history_background);
        } else if (position == 0 && player.getCurrentPositionOnList() != 1) {
            holder.setBackground(R.drawable.start_next_history_background);
        } else if (position == 0 && player.getCurrentPositionOnList() == 1) {
            holder.setBackground(R.drawable.start_end_next_history_background);
        } else if (position == player.getCurrentPositionOnList() - 1) {
            holder.setBackground(R.drawable.end_next_history_background);
        } else if (position < player.getCurrentPositionOnList() - 1) {
            holder.setBackground(R.drawable.next_history_background);
        } else if (position == tracks.size() - 1 && player.getCurrentPositionOnList() != tracks.size() - 2) {
            holder.setBackground(R.drawable.end_previous_history_background);
        } else if (position == tracks.size() - 1 && player.getCurrentPositionOnList() == tracks.size() - 2) {
            holder.setBackground(R.drawable.start_end_previous_history_background);
        } else if (position == player.getCurrentPositionOnList() + 1) {
            holder.setBackground(R.drawable.start_previous_history_background);
        } else if (position > player.getCurrentPositionOnList() + 1) {
            holder.setBackground(R.drawable.previous_history_background);
        }else {
            holder.setBackground(R.color.colorOrange);
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
        public final ConstraintLayout historyConstraint;
        public final TextView titleText;
        public final TextView artistText;
        public final TextView durationText;
        public Track track;
        public Context context;

        public ViewHolder(View view, Context context) {
            super(view);
            mView = view;
            historyConstraint = (ConstraintLayout) view.findViewById(R.id.history_constraint); 
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
        
        public void setBackground(int id){
            historyConstraint.setBackground(context.getDrawable(id));
        }

        @Override
        public String toString() {
            return super.toString() + " '" + durationText.getText() + "'";
        }
    }
}
