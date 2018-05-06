package com.example.onurelbirlik.sketchygui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TakePictureCamera extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture_camera);
        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        imageView = (ImageView)findViewById(R.id.imageView);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onBackPressed();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }


        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            HomeActivity.bitmap = bitmap;
            Intent tempIntent = new Intent(TakePictureCamera.this, ImageToLine.class);
            startActivity(tempIntent);
        }
        //imageView.setImageBitmap(bitmap);
    }
}
