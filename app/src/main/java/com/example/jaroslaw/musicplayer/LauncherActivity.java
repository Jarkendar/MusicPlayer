package com.example.jaroslaw.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends Activity {

    private static final long DISPLAY_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        this.getActionBar().hide();

        Intent intent = new Intent(this, PlayerActivity.class);
        waitAndRun(intent);
    }

    private void waitAndRun(final Intent intent){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(DISPLAY_TIME);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
}
