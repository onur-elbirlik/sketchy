package com.example.onurelbirlik.sketchygui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class TakePictureCamera extends AppCompatActivity {

    ImageView imageView,imageView2;
    Button galleryButton;
    public static boolean imageFromCamera;
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
                imageFromCamera=true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }

        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageFromCamera=false;
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK)
        {
            imageURL = data.getData();
            imageView.setImageURI(imageURL);
            imageView.setVisibility(View.INVISIBLE);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor cursor = this.getContentResolver().query(imageURL, projection, null, null, null);

            int orientation = -1;
            if (cursor != null && cursor.moveToFirst()) {
                orientation = cursor.getInt(0);
                cursor.close();
            }

            Log.d("Orientation", "" + orientation);

            Matrix matrix = new Matrix();
            switch(orientation) {
                case 0:
                    matrix.postRotate(0);
                    break;
                default:
                    matrix.postRotate(90);
            }

            bitmap = drawable.getBitmap();
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            Intent tempIntent = new Intent(TakePictureCamera.this, ImageToLine.class);
            startActivity(tempIntent);

        }
        else if(resultCode == RESULT_OK) {
            imageURL = data.getData();
            bitmap = readBitmap(imageURL);
            Intent tempIntent = new Intent(TakePictureCamera.this, ImageToLine.class);
            startActivity(tempIntent);
        }

    }
    public Bitmap readBitmap(Uri selectedImage) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor =null;
        try {
            fileDescriptor = this.getContentResolver().openAssetFileDescriptor(selectedImage,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally{
            try {
                bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }

}
