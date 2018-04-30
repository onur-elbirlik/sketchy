package com.example.onurelbirlik.sketchygui;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.VectorDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

public class ImageToLine extends AppCompatActivity {
    Bitmap bm = HomeActivity.bitmap;
    static{
        if(!OpenCVLoader.initDebug()){
            Log.i("opencv","init basarisiz");
        }else{
            Log.i("opencv","init basarili");
        }
    }

    private int threshHold =300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_line);

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
        //bm = BitmapFactory.decodeResource(getResources(), R.drawable.doge);
        Mat input = new Mat();
        Utils.bitmapToMat(bm,input);
        Mat outputGauss = new Mat();
        Mat outputCanny = new Mat();

        Imgproc.GaussianBlur(input, outputGauss, new Size(7, 7), 0);
        Imgproc.Canny(outputGauss, outputCanny, 0, th);
        Core.bitwise_not(outputCanny,outputCanny);
        Imgproc.erode(outputCanny, outputCanny, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4)));



        //green mask aplied
        //Mat mask = new Mat(dest2.size(),CvType.CV_8UC3);
        //Imgproc.threshold(dest2,mask,0,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);

        Imgproc.cvtColor(outputCanny,outputCanny,Imgproc.COLOR_GRAY2BGR);
        //dest2.setTo(new Scalar(0,0,255),mask);

        Mat inputRGBA = new Mat(outputCanny.size(),CvType.CV_8UC4);

        Imgproc.cvtColor(outputCanny,inputRGBA,Imgproc.COLOR_BGR2BGRA);
        int thres = 245;
        double [] pixels = new double[4];
        /*for(int i=0;i<inputRGBA.rows();i++){
            for(int k=0;k<inputRGBA.cols();k++){
                if(inputRGBA.get(i,k)[0]>=thres&&inputRGBA.get(i,k)[1]>=thres&&inputRGBA.get(i,k)[2]>=thres){
                    pixels[0]=inputRGBA.get(i,k)[0];
                    pixels[1]=inputRGBA.get(i,k)[1];
                    pixels[2]=inputRGBA.get(i,k)[2];
                    pixels[3]=0;

                    inputRGBA.put(i,k,pixels);


                }
                //System.out.println(inputRGBA.get(i,k)[0]+" "+inputRGBA.get(i,k)[1]+" "+inputRGBA.get(i,k)[2]+" "+inputRGBA.get(i,k)[3]);

            }
        }
        //System.out.println(inputRGBA.get(100,100)[0]+" "+inputRGBA.get(100,100)[1]+" "+inputRGBA.get(100,100)[3]);*/

        Utils.matToBitmap(outputCanny,bm);
        imageView.setImageBitmap(bm);
    }





    private void saveImage(Bitmap finalBitmap) {
        String folderPath = Environment.getExternalStorageDirectory() + "/SketchyPhoto";
        System.out.println(folderPath);
        File direct = new File(folderPath);

        if (!direct.exists()) {
            File wallpaperDirectory = new File(folderPath);
            wallpaperDirectory.mkdirs();
        }

        File file = new File(folderPath, "trial2.png");
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
            System.out.println("save success!!");
            saveImage(bm);
        }else{
            System.out.println("save fail!");
        }
    }
    public void aRButton(View view){
        String path = Environment.getExternalStorageDirectory()+ "/SketchyPhoto/trial2.png";
        File imgFile = new File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            //Bitmap bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.gh);

            BoxRenderer.setBitmap(myBitmap);
            Intent intent = new Intent(ImageToLine.this, DisplayActivity.class);
            startActivity(intent);
        } else {
            System.out.println("file does not exist!");
        }
    }

}
