package com.example.doannhac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Button;
import android.media.MediaPlayer;
import android.view.View;
import android.os.Handler;
import android.widget.SeekBar.OnSeekBarChangeListener;
import androidx.appcompat.app.ActionBar;

import java.io.IOException;


public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    // views declaration
    TextView tvTime, tvTitle, tvArtist;
    TextView tvDuration;

    ImageView nextBtn, previousBtn;
    SeekBar seekBarTime;
    SeekBar seekBarVolume;
    Button btnPlay;
    MediaPlayer musicplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Song song = (Song) getIntent().getSerializableExtra("song");

        tvTime = findViewById(R.id.tvTime);
        tvDuration = findViewById(R.id.tvDuration);
        seekBarTime = findViewById(R.id.seekBarTime);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        btnPlay = findViewById(R.id.btnPlay);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());

        musicplayer = new MediaPlayer();
        try {
            musicplayer.setDataSource(song.getPath());
            musicplayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        musicplayer.setLooping(true); // lap lai
        musicplayer.seekTo(0);
        musicplayer.setVolume(0.5f, 0.5f); // am luong
        String duration = millisecondsToString(musicplayer.getDuration());
        tvDuration.setText(duration);
        btnPlay.setOnClickListener(this);
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());
        // thanh am luong
        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                // Handle progress change
                // You can use 'progress' to get the current progress value
                // tang giam am luong
                float volume = (float) progress / 100f;
                musicplayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle the start of tracking touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle the stop of tracking touch
            }
        });
        // thanh thoi gian bai hat
        seekBarTime.setMax(musicplayer.getDuration());
        Handler handler = new Handler();
        // Update seek bar and time TextView periodically
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (musicplayer.isPlaying()) {
                    int currentPosition = musicplayer.getCurrentPosition();
                    seekBarTime.setProgress(currentPosition);
                    tvTime.setText(millisecondsToString(currentPosition));
                }
                handler.postDelayed(this, 1000); // Update every 1 second
            }
        }, 0);
        seekBarTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicplayer.seekTo(progress);
                    tvTime.setText(millisecondsToString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle the start of tracking touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle the stop of tracking touch
            }
        });
    }//end main

    //chinh thoi gian
    public String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    private void playNextSong() {

    }

    private void playPreviousSong() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPlay) {
            if (musicplayer.isPlaying()) {
                // dang nghe nhac
                musicplayer.pause();
                btnPlay.setBackgroundResource(R.drawable.ic_play);
            } else {
                // dang tam dung
                musicplayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            if(musicplayer.isPlaying()) {
                musicplayer.stop();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}


