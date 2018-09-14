package com.example.jaroslaw.musicplayer;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jaroslaw.musicplayer.player.Player;

import java.util.LinkedList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Player player;
    private static final String TAG = "*******";

    private OnFragmentInteractionListener mListener;

    private ImageButton playButton, nextButton, previousButton, repeatButton, modeButton;
    private TextView[] titles = new TextView[5];
    private TextView[] artists = new TextView[5];
    private TextView[] durations = new TextView[5];
    private ConstraintLayout next3, next2, next, current, previous;


    public PlayedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayedFragment newInstance(String param1, String param2) {
        PlayedFragment fragment = new PlayedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_played, container, false);
        setUIVariables(rootView);
        setButtonListeners();
        setShortList();
        return rootView;
    }

    private void setUIVariables(View view) {
        playButton = view.findViewById(R.id.play_pause_button);
        nextButton = view.findViewById(R.id.next_button);
        previousButton = view.findViewById(R.id.previous_button);
        repeatButton = view.findViewById(R.id.repeat_button);
        modeButton = view.findViewById(R.id.mode_button);
        titles[0] = view.findViewById(R.id.item_title_previous);
        titles[1] = view.findViewById(R.id.item_title_current);
        titles[2] = view.findViewById(R.id.item_title_next);
        titles[3] = view.findViewById(R.id.item_title_next2);
        titles[4] = view.findViewById(R.id.item_title_next3);
        artists[0] = view.findViewById(R.id.item_artist_previous);
        artists[1] = view.findViewById(R.id.item_artist_current);
        artists[2] = view.findViewById(R.id.item_artist_next);
        artists[3] = view.findViewById(R.id.item_artist_next2);
        artists[4] = view.findViewById(R.id.item_artist_next3);
        durations[0] = view.findViewById(R.id.item_duration_previous);
        durations[1] = view.findViewById(R.id.item_duration_current);
        durations[2] = view.findViewById(R.id.item_duration_next);
        durations[3] = view.findViewById(R.id.item_duration_next2);
        durations[4] = view.findViewById(R.id.item_duration_next3);
        next3 = view.findViewById(R.id.constraintNext3);
        next2 = view.findViewById(R.id.constraintNext2);
        next = view.findViewById(R.id.constraintNext);
        current = view.findViewById(R.id.constraintCurrent);
        previous = view.findViewById(R.id.constraintPrevious);
    }

    private void setButtonListeners() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerIsAvailable() && player.getCurrentPlay() != null) {

                    Animation click = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.play_pause_click_anim);
                    view.startAnimation(click);

                    if (view.getTag() == getString(R.string.false_tag)) {
                        ((ImageButton) view).setImageResource(R.drawable.pause_circle);
                        view.setTag(getString(R.string.true_tag));
                        player.start();
                    } else {
                        ((ImageButton) view).setImageResource(R.drawable.play_circle);
                        view.setTag(getString(R.string.false_tag));
                        player.pause();
                    }

                    Animation load = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.play_pause_load_anim);
                    view.startAnimation(load);
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerIsAvailable()) {
                    view.startAnimation(getClickAnimation());
                    player.next();
                    changeOnPlay();
                }
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerIsAvailable()) {
                    view.startAnimation(getClickAnimation());
                    player.previous();
                    changeOnPlay();
                }
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerIsAvailable()) {
                    view.startAnimation(getClickAnimation());
                    player.setLoopingParam();
                    //todo change color or image
                }
            }
        });
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerIsAvailable()) {
                    view.startAnimation(getClickAnimation());
                    int imgId = R.drawable.ic_music_note_half_white_48dp;
                    if (view.getTag() == getString(R.string.queue_tag)) {
                        imgId = R.drawable.ic_music_note_quarter_white_48dp;
                        view.setTag(getString(R.string.random_tag));
                    } else if (view.getTag() == getString(R.string.random_tag)) {
                        imgId = R.drawable.ic_music_note_eighth_white_48dp;
                        view.setTag(getString(R.string.index_tag));
                    } else if (view.getTag() == getString(R.string.index_tag)) {
                        imgId = R.drawable.ic_music_note_half_white_48dp;
                        view.setTag(getString(R.string.queue_tag));
                    }
                    ((ImageButton) view).setImageResource(imgId);
                    player.changeMode();
                }
            }
        });
    }

    private boolean playerIsAvailable() {
        return player != null;
    }

    private Animation getClickAnimation() {
        return AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.imagebutton_click_anim);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void changeOnPlay(){
            playButton.setImageResource(R.drawable.pause_circle);
            playButton.setTag(getString(R.string.true_tag));
            setShortList();
    }

    public void setShortList() {
        LinkedList<Track> shortList = player.getShortListPlayed();
        if (shortList.size() >= titles.length-1) {
            int shift = shortList.size() < titles.length ? 1 : 0;
            for (int i = shift, j = 0; i < titles.length; ++i, ++j) {
                titles[i].setText(shortList.get(j).getTitle());
            }
            for (int i = shift, j = 0; i < artists.length; ++i, ++j) {
                artists[i].setText(shortList.get(j).getArtist());
            }
            for (int i = shift, j = 0; i < durations.length; ++i, ++j) {
                durations[i].setText(shortList.get(j).getDuration());
            }
            next3.setBackground(getActivity().getDrawable(R.drawable.next3_background));
            next2.setBackground(getActivity().getDrawable(R.drawable.next2_background));
            next.setBackground(getActivity().getDrawable(R.drawable.next_background));
            if (shortList.size() == titles.length){
                current.setBackground(getActivity().getDrawable(R.drawable.current_background));
                previous.setBackground(getActivity().getDrawable(R.drawable.previous_background));
            }else {
                current.setBackground(getActivity().getDrawable(R.drawable.current_background_end));
                previous.setBackground(null);
            }
        }else {
            next3.setBackground(null);
            next2.setBackground(null);
            next.setBackground(null);
            current.setBackground(null);
            previous.setBackground(null);
        }
    }
}
