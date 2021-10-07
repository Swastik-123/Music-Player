package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    Button btnplay,btnnext,btnprev,btnfr,btnff;
    TextView textname,textstart,textstop;
    SeekBar seekBar;
    BarVisualizer visualizer;
    ImageView imageView;

    String sname;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    Thread updateseekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        if(visualizer != null)
        {
            visualizer.release();
        }

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnplay = (Button)findViewById(R.id.play);
        btnnext = (Button)findViewById(R.id.btnnext);
        btnprev = (Button)findViewById(R.id.btnprev);
        btnff = (Button)findViewById(R.id.btnff);
        btnfr = (Button)findViewById(R.id.btnfr);

        textname = (TextView)findViewById(R.id.txtsn);//*************************************
        textstart = (TextView)findViewById(R.id.txtsstart);
        textstop = (TextView)findViewById(R.id.txtsstop);


        imageView = (ImageView)findViewById(R.id.imageView);

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        visualizer = (BarVisualizer)findViewById(R.id.blast);




        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList)bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        textname.setSelected(true);

        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();

        textname.setText(sname);


        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();


        updateseekbar = new Thread()
        {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration)
                {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    }catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };



        seekBar.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.Red), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.Red),PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });


        String endTime = createTime(mediaPlayer.getDuration()); //showing stop time.
        textstop.setText(endTime);//showing stop time.



        final Handler handler = new Handler();//showing current time.
        final int delay = 1000;//showing current time.


        handler.postDelayed(new Runnable() {//showing current time.
            @Override//showing current time.
            public void run() {//showing current time.
                String currentTime = createTime(mediaPlayer.getCurrentPosition());//showing current time.
                textstart.setText(currentTime);//showing current time.
                handler.postDelayed(this,delay);//showing current time.

            }
        },delay);//showing current time.



        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying())
                {
                    //if button is playing i change the button icon here
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else
                {
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }

            }
        });


        int audiosessionId = mediaPlayer.getAudioSessionId();
        if(audiosessionId != -1)
        {
            visualizer.setAudioSessionId(audiosessionId);
        }



        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                position = ((position+1)%mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = mediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName();//here we take the song name in sname place.
                textname.setText(sname);
                mediaPlayer.start();

                btnplay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);

                int audiosessionId = mediaPlayer.getAudioSessionId();
                if(audiosessionId != -1)
                {
                    visualizer.setAudioSessionId(audiosessionId);
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // after complete a song a new son starting using this method
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });




        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mySongs.size()-1):(position-1);


                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = mediaPlayer.create(getApplicationContext(),u);

                sname = mySongs.get(position).getName();
                textname.setText(sname);
                mediaPlayer.start();


                btnplay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);

                int audiosessionId = mediaPlayer.getAudioSessionId();
                if(audiosessionId != -1)
                {
                    visualizer.setAudioSessionId(audiosessionId);
                }
            }
        });


        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

      btnfr.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              if(mediaPlayer.isPlaying())
              {
                  mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
              }
          }
      });


    }

    public void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration)
    {
        String time = " ";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time += min+":";
        if(sec<10)
        {
            time+="0";
        }
        time += sec;
        return time;
    }
}