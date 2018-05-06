package com.example.onurelbirlik.sketchygui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity {

    Button credits,start;
    static int saveCounter=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        credits = findViewById(R.id.creditsButton);
        start = findViewById(R.id.startButton);
        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCredits();
            }
        });
        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                openTakePictureCamera();
            }
        });
    }

    private void openCredits(){
        Intent Credits = new Intent(this, Credits.class);
        startActivity(Credits);
    }
    private void openTakePictureCamera(){
        Intent takePicture = new Intent(this, TakePictureCamera.class);
        startActivity(takePicture);
    }


    private long mLastClickTime = 0;
    public void goTo(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
