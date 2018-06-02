package com.example.adminuser.mytest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends Activity {

    public static Workbook workbook;
    GLRenderer glRenderer;
    MyGS glSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.distortion_data);
            Workbook book = Workbook.getWorkbook(inputStream);
            workbook=book;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        glSurfaceView=findViewById(R.id.gs);
        glSurfaceView.setEGLContextClientVersion(2);
        glRenderer=new GLRenderer(this, "/sdcard/cat.mp4");//"android.resource://"+context.getPackageName()+"/raw/cat"
        glSurfaceView.setRenderer(glRenderer);
    }

    @Override
    protected void onDestroy() {
        glRenderer.mediaPlayer.release();
        super.onDestroy();
    }
}
