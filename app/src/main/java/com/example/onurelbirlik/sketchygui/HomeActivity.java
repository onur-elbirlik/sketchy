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

    Button galleryButton,credits;
    ImageView imageView;
    private static final int PICK_IMAGE = 100;
    Uri imageURL;
    public static Bitmap bitmap;
    static int saveCounter=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        galleryButton = findViewById(R.id.startButton);
        credits = findViewById(R.id.creditsButton);
        imageView = findViewById(R.id.imageView);

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCredits();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGallery();
            }
        });


    }

    private void openCredits(){
        Intent credits = new Intent(this, Credits.class);
        startActivity(credits);
    }
    private void openGallery(){

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageURL = data.getData();
            imageView.setImageURI(imageURL);
            imageView.setVisibility(View.INVISIBLE);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            bitmap = drawable.getBitmap();
            Intent tempIntent = new Intent(HomeActivity.this, ImageToLine.class);
            startActivity(tempIntent);
        }
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
