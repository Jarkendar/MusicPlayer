package com.example.jaroslaw.musicplayer;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jaroslaw.musicplayer.TrackFragment.OnListFragmentInteractionListener;

import java.util.List;

public class MyTrackRecyclerViewAdapter extends RecyclerView.Adapter<MyTrackRecyclerViewAdapter.ViewHolder> {

    private List<Track> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyTrackRecyclerViewAdapter(List<Track> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.titleText.setText(mValues.get(position).getTitle());
        holder.artistText.setText(mValues.get(position).getArtist());
        holder.durationText.setText(mValues.get(position).getDuration());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    Log.d("***********", "onClick: "+v);
                }
            }
        });
    }

    public List<Track> getmValues() {
        return mValues;
    }

    public void setmValues(List<Track> mValues) {
        this.mValues = mValues;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView titleText;
        public final TextView artistText;
        public final TextView durationText;
        public Track mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            titleText = (TextView) view.findViewById(R.id.item_title);
            artistText = (TextView) view.findViewById(R.id.item_artist);
            durationText = (TextView) view.findViewById(R.id.item_duration);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + durationText.getText() + "'";
        }
    }
}
