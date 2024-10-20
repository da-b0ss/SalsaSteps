package com.example.salsasteps;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);
        String videoFileName = getIntent().getStringExtra("videoFileName");

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + videoFileName);
        videoView.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }
}