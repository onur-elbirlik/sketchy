package com.example.onurelbirlik.sketchygui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import cn.easyar.Engine;

public class DisplayActivity extends Activity {
    /*
     * Steps to create the key for this sample:
     *  1. login www.easyar.com
     *  2. create app with
     *      Name: sketchy
     *      Package Name: cn.easyar.samples.helloar
     *  3. find the created item in the list and show key
     *  4. set key string bellow
     */
    private static String key = "O4Z53NNFfzfsQHk3i3FS7Z3A2wPGMB6uQX5EHanFrwsurycyqL3hafMyW5vop8U5uNNN9fhh5AdyNd3l2h15CnMga3euHChQCc87N2LyMNMflLMVjEFls2QuMXPMmgaeFGuElhnrfIUlCMI0YQZOkpZFtOnBPt8NHybKNzmBQCfIKsBzYfx2cAA6O1lqMvAtNDBwb2va";
    private GLView glView;
    
    Spinner sp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        ArrayAdapter adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.colorNames, android.R.layout.simple_spinner_item);
        final Spinner sp = (Spinner)findViewById(R.id.color);
        sp.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                String text = new String(adapterView.getItemAtPosition(position).toString());
                System.out.println(text);
                if (text.equals("Red"))
                {
                    System.out.println("Image is red");
                    makeImageRed();
                }
                else if(text.equals("Blue")){
                    System.out.println("Image is blue");
                    makeImageBlue();
                }
                else if(text.equals("Black")){
                    System.out.println("Image is black");
                    makeImageBlack();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (!Engine.initialize(this, key)) {
            Log.e("ARModule", "Initialization Failed.");
        }

        glView = new GLView(this);
        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                ((ViewGroup) findViewById(R.id.frameLayout)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            @Override
            public void onFailure() {
            }

        });

    }

    private interface PermissionCallback
    {
        void onSuccess();
        void onFailure();
    }
    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
    private int permissionRequestCodeSerial = 0;
    @TargetApi(23)
    private void requestCameraPermission(PermissionCallback callback)
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                int requestCode = permissionRequestCodeSerial;
                permissionRequestCodeSerial += 1;
                permissionCallbacks.put(requestCode, callback);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            } else {
                callback.onSuccess();
            }
        } else {
            callback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (permissionCallbacks.containsKey(requestCode)) {
            PermissionCallback callback = permissionCallbacks.get(requestCode);
            permissionCallbacks.remove(requestCode);
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    callback.onFailure();
                }
            }
            if (!executed) {
                callback.onSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (glView != null) { glView.onResume(); }
    }

    @Override
    protected void onPause()
    {
        if (glView != null) { glView.onPause(); }
        super.onPause();
    }
    public void makeImageBlue()
    {
        Mat dest = new Mat();
        Bitmap bm = HomeActivity.bitmap;
        Utils.bitmapToMat(bm,dest);
        Imgproc.cvtColor(dest,dest,Imgproc.COLOR_BGRA2GRAY);
        Mat mask = new Mat(dest.size(), CvType.CV_8UC3);
        Imgproc.threshold(dest,mask,0,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);
        Imgproc.cvtColor(dest,dest,Imgproc.COLOR_GRAY2BGR);
        dest.setTo(new Scalar(0,0,255),mask);
        Utils.matToBitmap(dest,bm);
        bm=ImageToLine.createTransparentBitmapFromBitmap(bm,Color.WHITE);
        BoxRenderer.setBitmap(bm);
        Bundle x= new Bundle();
        this.onCreate(x);
    }
    public void makeImageBlack()
    {
        Mat dest = new Mat();
        Bitmap bm = HomeActivity.bitmap;
        Utils.bitmapToMat(bm,dest);
        Imgproc.cvtColor(dest,dest,Imgproc.COLOR_BGRA2GRAY);
        Mat mask = new Mat(dest.size(), CvType.CV_8UC3);
        Imgproc.threshold(dest,mask,0,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);
        Imgproc.cvtColor(dest,dest,Imgproc.COLOR_GRAY2BGR);
        dest.setTo(new Scalar(0,0,0),mask);
        Utils.matToBitmap(dest,bm);
        bm=ImageToLine.createTransparentBitmapFromBitmap(bm,Color.WHITE);
        BoxRenderer.setBitmap(bm);
        Bundle x= new Bundle();
        this.onCreate(x);
    }
    public void makeImageRed()
    {
        Mat dest = new Mat();
        Bitmap bm = HomeActivity.bitmap;
        Utils.bitmapToMat(bm,dest);
        Imgproc.cvtColor(dest,dest,Imgproc.COLOR_BGRA2GRAY);
        Mat mask = new Mat(dest.size(), CvType.CV_8UC3);
        Imgproc.threshold(dest,mask,0,255,Imgproc.THRESH_BINARY_INV|Imgproc.THRESH_OTSU);
        Imgproc.cvtColor(dest,dest,Imgproc.COLOR_GRAY2BGR);
        dest.setTo(new Scalar(255,0,0),mask);
        Utils.matToBitmap(dest,bm);
        bm=ImageToLine.createTransparentBitmapFromBitmap(bm,Color.WHITE);
        BoxRenderer.setBitmap(bm);
        Bundle x= new Bundle();
        this.onCreate(x);
    }
    /*private Bitmap getBitmapFromAssets(String fileName){
        AssetManager am = getAssets();
        InputStream is = null;
        try{

            is = am.open(fileName);
        }catch(IOException e){
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
    public void openGrid(){
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        Bitmap x = getBitmapFromAssets("grid.png");
        bm = x;
        imageView.setImageBitmap(bm);
    }*/
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
