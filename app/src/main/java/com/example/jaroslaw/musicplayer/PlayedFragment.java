package com.example.jaroslaw.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;


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
    private static final String TAG = "*******";

    private OnFragmentInteractionListener mListener;

    private ImageButton playButton, nextButton, previousButton, repeatButton, modeButton;


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
        return rootView;
    }

    private void setUIVariables(View view) {
        playButton = view.findViewById(R.id.play_pause_button);
        nextButton = view.findViewById(R.id.next_button);
        previousButton = view.findViewById(R.id.previous_button);
        repeatButton = view.findViewById(R.id.repeat_button);
        modeButton = view.findViewById(R.id.mode_button);
    }

    private void setButtonListeners() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation click = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.play_pause_click_anim);
                view.startAnimation(click);

                if (view.getTag() == getString(R.string.false_tag)) {
                    ((ImageButton) view).setImageResource(R.drawable.pause_circle);
                    view.setTag(getString(R.string.true_tag));
                } else {
                    ((ImageButton) view).setImageResource(R.drawable.play_circle);
                    view.setTag(getString(R.string.false_tag));
                }

                //todo serve play/stop music

                Animation load = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.play_pause_load_anim);
                view.startAnimation(load);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(getClickAnimation());
                //todo serve next song
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(getClickAnimation());
                //todo serve previous song
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(getClickAnimation());
                //todo serve repeat
            }
        });
        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                //todo serve change mode
            }
        });
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
}
