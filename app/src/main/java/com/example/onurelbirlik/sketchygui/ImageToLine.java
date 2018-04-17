package com.example.onurelbirlik.sketchygui;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageToLine extends AppCompatActivity {
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
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.doge);
        Mat img = new Mat();
        Utils.bitmapToMat(bm,img);
        Mat dest = new Mat();
        Mat dest2 = new Mat();
        Imgproc.GaussianBlur(img, dest, new Size(7, 7), 0);
        Imgproc.Canny(dest, dest2, 0, th);
        Core.bitwise_not(dest2,dest2);
        Imgproc.erode(dest2, dest2, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
        //BURDA KALDIN
        Utils.matToBitmap(dest2,bm);
        imageView.setImageBitmap(bm);
    }

}
