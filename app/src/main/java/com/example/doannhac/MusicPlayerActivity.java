package com.example.doannhac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MusicPlayerActivity extends AppCompatActivity{
    // views declaration
    private static final int REQUEST_PERMISSION = 99;
    Bundle songExtraData;
    TextView tvTime, tvTitle, tvArtist;
    TextView tvDuration;
    int position;
    ImageView nextBtn, previousBtn;
    SeekBar seekBarTime;
    SeekBar seekBarVolume;
    Button btnPlay;
    static MediaPlayer mMediaPlayer;
    ArrayList<Song> musicList;
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        NotificationChannel channel=new NotificationChannel("My notification","My notification",NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager=getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

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

//        tvTitle.setText(song.getTitle());
//        tvArtist.setText(song.getArtist());

        if(mMediaPlayer!=null)
        {
            mMediaPlayer.stop();
        }

        //getting values from previous activity
        Intent intent = getIntent();
        songExtraData = intent.getExtras();
        musicList = (ArrayList)songExtraData.getParcelableArrayList("musics");
        position = songExtraData.getInt("position", 0);

        initializeMusicPlayer(position);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<musicList.size()-1)
                {
                    position++;
                }
                else {
                    position=0;
                }
                initializeMusicPlayer(position);
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<=0){
                    position=musicList.size()-1;
                }
                else {
                    position--;
                }
                initializeMusicPlayer(position);
            }
        });
    }//end main


    private void initializeMusicPlayer(int position) {
        // if mediaplayer is not null and playing reset it at the launch of activity

        if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }

        // getting out the song name
        String name = musicList.get(position).getTitle();
        tvTitle.setText(name);
        String artist=musicList.get(position).getArtist();
        String duration = millisecondsToString(musicList.get(position).getDuration());
        tvDuration.setText(duration);
        // accessing the songs on storage

        Uri uri = Uri.parse(musicList.get(position).getPath());

        // creating a mediaplayer
        // passing the uri

        mMediaPlayer = MediaPlayer.create(this, uri);

        // SETTING ON PREPARED MEDIAPLAYER

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                // seekbar
                seekBarTime.setMax(mMediaPlayer.getDuration());

                // while mediaplayer is playing the play button should display pause
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                // start the mediaplayer
                mMediaPlayer.start();
            }
        });

        // setting the oncompletion listener
        // after song finishes what should happen // for now we will just set the pause button to play

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setBackgroundResource(R.drawable.ic_play);
            }
        });

        //volume bar
        seekBarVolume.setProgress(50);
        seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                // Handle progress change
                // You can use 'progress' to get the current progress value
                // tang giam am luong
                float volume = (float) progress / 100f;
                mMediaPlayer.setVolume(volume, volume);
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

        // if you want the the mediaplayer to go to next song after its finished playing one song its optional
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setBackgroundResource(R.drawable.ic_play);

                int currentPosition = position;
                if (currentPosition < musicList.size() -1) {
                    currentPosition++;
                } else {
                    currentPosition = 0;
                }
                initializeMusicPlayer(currentPosition);

            }
        });


        // working on seekbar

        // thanh thoi gian bai hat
        seekBarTime.setMax(mMediaPlayer.getDuration());
        Handler handler = new Handler();
        // Update seek bar and time TextView periodically
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer.isPlaying()) {
                    int currentPosition = mMediaPlayer.getCurrentPosition();
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
                    mMediaPlayer.seekTo(progress);
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
    }


    //chinh thoi gian
    public String millisecondsToString(int time) {
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }


    private void play() {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(MusicPlayerActivity.this,"My notification");
        builder.setContentTitle("My Title");
        builder.setContentText("Phat nhac di");
        builder.setSmallIcon(R.drawable.ic_music_note);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(MusicPlayerActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},REQUEST_PERMISSION);
            return;
        } else {
            managerCompat.notify(1,builder.build());
        }
        // if mediaplayer is not null and playing and if play button is pressed pause it
        if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            // change the image of playpause button to play when we pause it
            btnPlay.setBackgroundResource(R.drawable.ic_play);
        } else {
            mMediaPlayer.start();
            // if mediaplayer is playing // the image of play button should display pause
            btnPlay.setBackgroundResource(R.drawable.ic_pause);

        }
    }
}


