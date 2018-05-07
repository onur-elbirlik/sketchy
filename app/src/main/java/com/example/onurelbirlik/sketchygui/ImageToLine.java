package com.example.onurelbirlik.sketchygui;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;

public class ImageToLine extends AppCompatActivity {
    Bitmap bm = TakePictureCamera.bitmap;
    Bitmap bmCopy;
    static{
        if(!OpenCVLoader.initDebug()){
            Log.i("opencv","init fail");
        }else{
            Log.i("opencv","init success");
        }
    }
    private int threshHold =300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(bm.getWidth()>1200||bm.getHeight()>1200)
            bm=Bitmap.createScaledBitmap(bm,1000, 1000, true);
        setContentView(R.layout.activity_image_to_line);
        detectEdges(100);
        SeekBar s1 = (SeekBar) findViewById(R.id.seekBar3);
        s1.setProgress(100);
        threshHold=s1.getProgress();
        s1.setMax(500);
        s1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                threshHold=i;
                detectEdges(threshHold);
                System.out.println("test "+i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    public void detectEdges(int th) {

        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        bmCopy = bm.copy(bm.getConfig(),true);

        Mat input = new Mat();
        Utils.bitmapToMat(bmCopy,input);
        Mat outputGauss = new Mat();
        Mat outputCanny = new Mat();

        Imgproc.GaussianBlur(input, outputGauss, new Size(7, 7), 0);
        Imgproc.Canny(outputGauss, outputCanny, 0, th);
        Core.bitwise_not(outputCanny,outputCanny);
        Imgproc.erode(outputCanny, outputCanny, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
        Utils.matToBitmap(outputCanny,bmCopy);
        imageView.setImageBitmap(bmCopy);
        if(TakePictureCamera.imageFromCamera)
            imageView.setRotation(90);
    }
    public static Bitmap createTransparentBitmapFromBitmap(Bitmap bitmap,
                                                           int replaceThisColor) {
        if (bitmap != null) {
            int picw = bitmap.getWidth();
            int pich = bitmap.getHeight();
            int[] pix = new int[picw * pich];
            bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);

            for (int y = 0; y < pich; y++) {
                // from left to right
                for (int x = 0; x < picw; x++) {
                    int index = y * picw + x;

                    if (pix[index] == replaceThisColor) {
                        pix[index] = Color.TRANSPARENT;
                    }
                }
            }
            Bitmap bm = Bitmap.createBitmap(pix, picw, pich,
                    Bitmap.Config.ARGB_4444);
            return bm;
        }
        return null;
    }
    private void saveImage(Bitmap finalBitmap){
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/SketchyPhoto";
        System.out.println(folderPath);
        File direct = new File(folderPath);
        if (!direct.exists()) {
            File wallpaperDirectory = new File(folderPath);
            wallpaperDirectory.mkdirs();
        }
        File file = new File(folderPath, "SketchtPhoto"+ HomeActivity.saveCounter +".png");
        HomeActivity.saveCounter++;
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    public void saveButton(View view){

        if (isExternalStorageWritable()) {
            bm=bmCopy;
            saveImage(bm);

            //bm=createTransparentBitmapFromBitmap(bm,Color.WHITE);
        }else{
            Log.i("save","save fail");
        }
    }
    public void aRButton(View view){
        bm=bmCopy;
        TakePictureCamera.bitmap=bm;
        bm=createTransparentBitmapFromBitmap(bm,Color.WHITE);
        BoxRenderer.setBitmap(bm);
        Intent intent = new Intent(ImageToLine.this, DisplayActivity.class);
        startActivity(intent);
    }

}
