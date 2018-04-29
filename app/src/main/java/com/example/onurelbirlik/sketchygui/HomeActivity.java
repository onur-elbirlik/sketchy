package com.example.onurelbirlik.sketchygui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.os.SystemClock;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    private long mLastClickTime = 0;
    public void goTo(View view)
    {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Intent intent = new Intent(HomeActivity.this, ImageToLine.class);
        startActivity(intent);
    }
    public void goToDisplayActivity(View view)
    {
        Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
        startActivity(intent);
    }
}
