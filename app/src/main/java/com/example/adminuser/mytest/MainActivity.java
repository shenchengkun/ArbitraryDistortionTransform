package com.example.adminuser.mytest;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLSurfaceView glSurfaceView=findViewById(R.id.gs);
        glSurfaceView.setEGLContextClientVersion(2);
        GLRenderer glRenderer=new GLRenderer(this, "/sdcard/cat.mp4");//"android.resource://"+context.getPackageName()+"/raw/cat"
        glSurfaceView.setRenderer(glRenderer);

    }
}
