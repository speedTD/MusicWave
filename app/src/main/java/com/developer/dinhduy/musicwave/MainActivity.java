package com.developer.dinhduy.musicwave;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.taishi.library.Indicator;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="365" ;
    private Button mbtn;
    private TextView mTxt1,mTxt2,mTxt_name;
    private SeekBar mSeekbar;
    private Indicator mWave;
    private MediaPlayer mediaPlayer;
    private Handler mhander=new Handler();
    private boolean isplay=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mbtn=(Button) findViewById(R.id.id_btn);
        mTxt1=(TextView) findViewById(R.id.id_txtStart);
        mTxt2=(TextView) findViewById(R.id.id_txtEnd);
        mTxt_name=(TextView) findViewById(R.id.id_name);
        mSeekbar=(SeekBar) findViewById(R.id.id_seekbar);
        mWave=(Indicator) findViewById(R.id.id_indicator);
        mediaPlayer=new MediaPlayer();
        mediaPlayer=MediaPlayer.create(this,R.raw.nhac);
        int curent=mediaPlayer.getDuration();
        long phut=TimeUnit.MILLISECONDS.toMinutes(curent);
        long giay=TimeUnit.MILLISECONDS.toSeconds(curent)-TimeUnit.MINUTES.toSeconds(phut);
        mTxt2.setText(String.format("%2d:%2d",phut,giay));
        //sset click
        mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isplay=!isplay;
                Play(isplay);
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer!=null&&b){
                    mediaPlayer.seekTo(i);
                    int curent=mediaPlayer.getCurrentPosition();
                    long phut=TimeUnit.MILLISECONDS.toMinutes(curent);
                    long giay=TimeUnit.MILLISECONDS.toSeconds(curent)-TimeUnit.MINUTES.toSeconds(phut);
                    mTxt1.setText(String.format("%2d:%2d",phut,giay));
                }else if(mediaPlayer==null&&b){
                    RunSeek(i);
                    update();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer!=null){
                    mhander.removeCallbacks(runnable);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer!=null){
                    mhander.removeCallbacks(runnable);
                    mediaPlayer.seekTo(seekBar.getProgress());
                    int curent=mediaPlayer.getCurrentPosition();
                    long phut=TimeUnit.MILLISECONDS.toMinutes(curent);
                    long giay=TimeUnit.MILLISECONDS.toSeconds(curent)-TimeUnit.MINUTES.toSeconds(phut);
                    mTxt1.setText(String.format("%2d:%2d",phut,giay));
                    update();
                }

            }
        });


    }
     private void Play(boolean isplay){
        if(!isplay){
            if(mediaPlayer!=null){
                Log.d(TAG, "Playing");
                PlayMusic();
            }else {
                Log.d(TAG, "Pause");
                ResumeMusic();
            }
        }else {
            Log.d(TAG, "Resume");

            PauseMusic();
        }
     }
//method Stop
    private void StopMusic() {
        mbtn.setText("Play");
        mediaPlayer.release();
        mediaPlayer.reset();
        mediaPlayer.stop();
        mediaPlayer=null;
        mSeekbar.setProgress(mSeekbar.getProgress());
        mSeekbar.setProgress(mSeekbar.getMax());
    }
//method Pause
    private void PauseMusic() {
        mWave.setBarNum(0);
        mWave.setStepNum(0);
        mbtn.setText("Play");
        mediaPlayer.pause();
        mhander.removeCallbacks(runnable);
    }
//methos Play
    private void PlayMusic() {
       mbtn.setText("Resume");
       mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer=MediaPlayer.create(this,R.raw.nhac);
            mediaPlayer.prepare();
            mSeekbar.setMax(mediaPlayer.getDuration());

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        update();

    }
//method Resume
    private void ResumeMusic(){
        mbtn.setText("Resume");//1
        mSeekbar.setMax(mediaPlayer.getDuration());
        mWave.setBarNum(50);
        mWave.setStepNum(70);
        mWave.setDuration(18000);
        mhander.removeCallbacks(runnable);
        mediaPlayer.start();
        update();

    }
 //method Running Seekbar
    private void RunSeek(int progess){
        mediaPlayer=new MediaPlayer();

        try {
            Log.d(TAG, "Runsek");
            mediaPlayer=MediaPlayer.create(this,R.raw.nhac);
            mediaPlayer.prepare();
            mSeekbar.setMax(mediaPlayer.getDuration());
            mediaPlayer.seekTo(progess);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                   StopMusic();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            // method up date Time Seekbar and Textview
            if(mediaPlayer!=null) {
                int currentTime = mediaPlayer.getCurrentPosition();
                mSeekbar.setProgress(currentTime);
                long phut = TimeUnit.MILLISECONDS.toMinutes(currentTime);
                long giay = TimeUnit.MILLISECONDS.toSeconds(currentTime) - TimeUnit.MINUTES.toSeconds(phut);
                mTxt1.setText(String.format("%02d:%02d", phut, giay));
                update();
            }
        }
    };

    private void update() {
        mhander.postDelayed(runnable,1000);
    }
}
