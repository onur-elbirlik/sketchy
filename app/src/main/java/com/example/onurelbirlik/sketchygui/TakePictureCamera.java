package com.example.onurelbirlik.sketchygui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TakePictureCamera extends AppCompatActivity {

    ImageView imageView,imageView2;
    Button galleryButton;

    private static final int PICK_IMAGE = 100;
    Uri imageURL;
    public static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture_camera);
        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        galleryButton = findViewById(R.id.selectImage);
        imageView = findViewById(R.id.imageView);

        imageView2 = findViewById(R.id.imageView1);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onBackPressed();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }


        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGallery();
            }
        });

    }
    private void openGallery(){

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
                imageURL = data.getData();
                imageView.setImageURI(imageURL);
                imageView.setVisibility(View.INVISIBLE);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                bitmap = drawable.getBitmap();
                Intent tempIntent = new Intent(TakePictureCamera.this, ImageToLine.class);
                startActivity(tempIntent);
        }
        else if(resultCode == RESULT_OK){
            imageURL = data.getData();
            imageView2.setImageURI(imageURL);
            imageView2.setVisibility(View.INVISIBLE);
            BitmapDrawable drawable = (BitmapDrawable) imageView2.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            TakePictureCamera.bitmap=bitmap;
            Intent tempIntent = new Intent(TakePictureCamera.this, ImageToLine.class);
            startActivity(tempIntent);
        }

    }
}

