package com.idariam.justdictaphone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button pauseBtn;
    private Button startBtn;
    private Button stopBtn;
    private TextView recText;
    private Button playBtn;
    private Button stopPlayBtn;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String fileName;
    private Boolean recordingStopped = false;
    private String pathName;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pauseBtn=findViewById(R.id.button_pause_recording);
        startBtn=findViewById(R.id.button_start_recording);
        stopBtn=findViewById(R.id.button_stop_recording);
        recText=findViewById(R.id.text_recording);
        playBtn = findViewById(R.id.play_btn);
        stopPlayBtn=findViewById(R.id.stop_play_btn);
        playBtn.setEnabled(false);
        stopPlayBtn.setEnabled(false);
        stopBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/JustDictaphone");
        if (!folder.exists()){folder.mkdir();}
        pathName = folder.getAbsolutePath();
        startBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    startBtn.setBackground(getResources().getDrawable(R.drawable.btn_green));
                    String[]permissions = new String[]{android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(MainActivity.this,permissions,0);
                }

                else{

                    startRecording();
                }
            }
        });
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                pauseRecording();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord();

            }
        });
        stopPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
            }
        });


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void startRecording(){
        stopBtn.setEnabled(true);
        pauseBtn.setEnabled(true);
        startBtn.setEnabled(false);
        recText.setText("Запись идёт...");
        try{
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd.MM.HH.mm.ss");
            Date date = new Date();
            fileName = pathName + "/record"+dateFormat.format(date)+ ".mp3";
            releaseRecorder();
            File outFile = new File(fileName);
            mediaRecorder=new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(outFile);
            mediaRecorder.prepare();
            mediaRecorder.start();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    void pauseRecording(){
        if (!recordingStopped){
            mediaRecorder.pause();
            recordingStopped = true;
            pauseBtn.setText("RESUME");
            stopBtn.setEnabled(true);
            pauseBtn.setEnabled(true);
            startBtn.setEnabled(false);
            recText.setText("Приостановлено");}
        else resumeRecording();

    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    void resumeRecording(){
        recText.setText("Запись идёт...");
        mediaRecorder.resume();
        pauseBtn.setText("PAUSE");
        recordingStopped=false;

    }
    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
    @SuppressLint("SetTextI18n")
    void stopRecording(){
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
        playBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
        startBtn.setEnabled(true);
        recText.setText("Нажмите START");
        System.out.println(fileName);
    }
    void playRecord(){
        //playBtn.setEnabled(false);
        stopPlayBtn.setEnabled(true);

        try {
            releasePlayer();
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    void stopPlaying(){
        playBtn.setEnabled(true);
        //stopPlayBtn.setEnabled(false);
        if (mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        releaseRecorder();

    }

}
