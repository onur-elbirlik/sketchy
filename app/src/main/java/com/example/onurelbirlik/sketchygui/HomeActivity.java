package com.example.onurelbirlik.sketchygui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

}