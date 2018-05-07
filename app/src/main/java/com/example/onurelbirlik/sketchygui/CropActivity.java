package com.example.onurelbirlik.sketchygui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC4;

public class CropActivity extends AppCompatActivity
{

    Bitmap bm;
    ArrayList<Bitmap> layerArr = new  ArrayList<Bitmap>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Bitmap tempBm = TakePictureCamera.bitmap;
        bm =tempBm;
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(bm);
        final DragRectView dragView = (DragRectView) findViewById(R.id.dragRect);
        dragView.widthHeight(1400,1600);

        if (null != dragView)
        {
            dragView.setOnUpCallback(new DragRectView.OnUpCallback()
            {
                @Override
                public void onRectFinished(final Rect rect)
                {
                    bm = layer(rect,bm,imageView,dragView,layerArr);
                }
            });
        }
    }


    public static PointF convertPoint(PointF fromPoint, View fromView, View toView)
    {
        int[] fromCoord = new int[2];
        int[] toCoord = new int[2];
        fromView.getLocationOnScreen(fromCoord);
        toView.getLocationOnScreen(toCoord);
        PointF toPoint = new PointF(toCoord[0] - fromCoord[0] + fromPoint.x,
                toCoord[1] - fromCoord[1]  + fromPoint.y);
        return toPoint;
    }

    public static Bitmap layer(Rect rect,Bitmap bm, ImageView imageView, DragRectView dragView, ArrayList<Bitmap> arr)
    {
        bm = Bitmap.createScaledBitmap(bm,1400,1600,true);
        PointF lT = new PointF();
        lT.set( rect.left, rect.top);
        //lT = convertPoint(lT,dragView,imageView);
        PointF rB = new PointF();
        rB.set(rect.right, rect.bottom);
        //rB = convertPoint(rB, dragView,imageView);
        Bitmap bm2 = Bitmap.createBitmap(1400,1600,bm.getConfig());
        Canvas cv = new Canvas();
        cv.setBitmap(bm2);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.FILL);
        cv.drawRect(lT.x,lT.y,rB.x,rB.y,p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        cv.drawBitmap(bm,0,0,p);
        arr.add(bm2);
        Bitmap bm3 = bm.copy(bm.getConfig(),true);
        bm3 = Bitmap.createScaledBitmap(bm3,1400,1600,true);
        Canvas cv2 = new Canvas();
        cv2.setBitmap(bm3);
        Paint p2 = new Paint();
        p2.setStyle(Paint.Style.FILL);
        p2.setColor(Color.WHITE);
        cv2.drawRect(lT.x,lT.y,rB.x,rB.y,p2);
        imageView.setImageBitmap(bm3);
        return bm3;
    }


    public void endLayer(View view)
    {
        layerArr.add(bm);
    }
}