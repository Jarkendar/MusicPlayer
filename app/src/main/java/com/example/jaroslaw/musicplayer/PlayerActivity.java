package com.example.jaroslaw.musicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class PlayerActivity extends Activity {

    private ImageButton playButton, nextButton, previousButton, repeatButton, modeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUIVariables();
        setButtonListeners();
    }

    private void setUIVariables() {
        playButton = findViewById(R.id.play_pause_button);
        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.previous_button);
        repeatButton = findViewById(R.id.repeat_button);
        modeButton = findViewById(R.id.mode_button);
    }

    private void setButtonListeners() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation click = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.play_pause_click_anim);
                view.startAnimation(click);

                if (view.getTag() == getString(R.string.false_tag)) {
                    ((ImageButton) view).setImageResource(R.drawable.pause_circle);
                    view.setTag(getString(R.string.true_tag));
                } else {
                    ((ImageButton) view).setImageResource(R.drawable.play_circle);
                    view.setTag(getString(R.string.false_tag));
                }

                //todo serve play/stop music

                Animation load = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.play_pause_load_anim);
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

                //todo server change mode
            }
        });
    }

    private Animation getClickAnimation() {
        return AnimationUtils.loadAnimation(getApplicationContext(), R.anim.imagebutton_click_anim);
    }
}
