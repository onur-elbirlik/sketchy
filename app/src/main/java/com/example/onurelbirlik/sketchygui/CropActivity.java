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
import static android.graphics.PorterDuff.Mode.DST_OVER;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC4;


public class CropActivity extends AppCompatActivity
{

    int hE;
    int wI;
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
        wI = ImageToLine.w;
        hE = ImageToLine.h;
        dragView.widthHeight(wI,hE);

        if (null != dragView)
        {
            dragView.setOnUpCallback(new DragRectView.OnUpCallback()
            {
                @Override
                public void onRectFinished(final Rect rect)
                {
                    bm = layer(rect,bm,imageView,dragView,layerArr, wI, hE);
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

    public static Bitmap layer(Rect rect,Bitmap bm, ImageView imageView, DragRectView dragView, ArrayList<Bitmap> arr, int wI, int hE)
    {
        bm = Bitmap.createScaledBitmap(bm,wI,hE,true);
        PointF lT = new PointF();
        lT.set( rect.left, rect.top);
        //lT = convertPoint(lT,dragView,imageView);
        PointF rB = new PointF();
        rB.set(rect.right, rect.bottom);
        //rB = convertPoint(rB, dragView,imageView);
        Bitmap bm2 = Bitmap.createBitmap(wI,hE,bm.getConfig());
        Canvas cv = new Canvas();
        cv.setBitmap(bm2);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.FILL);
        cv.drawRect(lT.x,lT.y,rB.x,rB.y,p);
        Bitmap tempBm = bm2.copy(bm2.getConfig(),true);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        cv.drawBitmap(bm,0,0,p);
        arr.add(bm2);

        //Image - Layer
        Bitmap bm3 = bm;
        Canvas cv2 = new Canvas(bm3);
        Paint p2 = new Paint();
        p2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        cv2.drawRect(rect,p2);
        imageView.setImageBitmap(bm3);
        return bm3;
    }


    public void endLayer(View view)
    {
        layerArr.add(bm);
    }
}