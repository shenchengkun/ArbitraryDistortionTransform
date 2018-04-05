package com.example.adminuser.mytest;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLSurfaceView glSurfaceView=findViewById(R.id.gs);
        glSurfaceView.setEGLContextClientVersion(2);
        GLRenderer glRenderer=new GLRenderer(this, "/sdcard/cat.mp4");//"android.resource://"+context.getPackageName()+"/raw/cat"
        glSurfaceView.setRenderer(glRenderer);

        /*
        String youTubeVideoID = "pkIL61tPOLo";
        YouTubeVideoInfoRetriever retriever = new YouTubeVideoInfoRetriever();
        try
        {
            retriever.retrieve(youTubeVideoID);
            String res=retriever.getInfo(YouTubeVideoInfoRetriever.KEY_DASH_VIDEO);
            System.out.println();
            Toast.makeText(this,res,Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/


    }


}
