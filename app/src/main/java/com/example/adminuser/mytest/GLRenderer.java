package com.example.adminuser.mytest;

/**
 * Created by AdminUser on 3/14/2018.
 */

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Ads on 2016/11/13.
 */
public class GLRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private Grid grid;
    private int[] indiceData;
    private IntBuffer indiceBuffer;
    private FloatBuffer vertexBuffer;
    private  float[] vertexData;
    private  float[] vertexDataRight;
    private FloatBuffer vertexBufferRight;
    private  float[] textureVertexData;
    private FloatBuffer textureVertexBuffer;

    private static final String TAG = "GLRenderer";
    private Context context;
    private int aPositionHandle;
    private int programId;
    private final float[] projectionMatrix=new float[16];
    private int uMatrixHandle;
    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;
    private int textureId;
    private SurfaceTexture surfaceTexture;
    public MediaPlayer mediaPlayer;
    private float[] mSTMatrix = new float[16];
    private int uSTMMatrixHandle;
    private boolean updateSurface;
    private boolean playerPrepared;
    private int screenWidth,screenHeight;

    public GLRenderer(Context context,String videoPath) {
        grid=new Grid(13,13);
        vertexData=grid.getVertices();
        vertexDataRight=grid.getVerticesRight();
        textureVertexData=grid.getTexels();
        indiceData=grid.getIndices();
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);
        vertexBufferRight = ByteBuffer.allocateDirect(vertexDataRight.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexDataRight);
        vertexBufferRight.position(0);
        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);
        indiceBuffer=ByteBuffer.allocateDirect(indiceData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(indiceData);
        indiceBuffer.position(0);

        this.context = context;
        playerPrepared=false;
        synchronized(this) {
            updateSurface = false;
        }
        mediaPlayer=new MediaPlayer();
        try{
            mediaPlayer.setDataSource(context, Uri.fromFile(new File(videoPath)));
            //Uri uri=Uri.parse("rtsp://r2---sn-a5m7zu76.c.youtube.com/Ck0LENy73wIaRAnTmlo5oUgpQhMYESARFEgGUg5yZWNvbW1lbmRhdGlvbnIhAWL2kyn64K6aQtkZVJdTxRoO88HsQjpE1a8d1GxQnGDmDA==/0/0/0/video.3gp");
            //mediaPlayer.setDataSource("http://www.html5videoplayer.net/videos/toystory.mp4");

            /*
            https://ccs3.akamaized.net/cchanclips/a6164c61eddb455190330e05c6c91ca6/clip.mp4
            http://demos.webmproject.org/exoplayer/glass.mp4
            http://www.html5videoplayer.net/videos/toystory.mp4
            */
        }catch (IOException e){
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vertex_shader);
        String fragmentShader= ShaderUtils.readRawTextFile(context, R.raw.fragment_shader);
        programId=ShaderUtils.createProgram(vertexShader,fragmentShader);
        aPositionHandle= GLES20.glGetAttribLocation(programId,"aPosition");

        uMatrixHandle=GLES20.glGetUniformLocation(programId,"uMatrix");
        uSTMMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix");
        uTextureSamplerHandle=GLES20.glGetUniformLocation(programId,"sTexture");
        aTextureCoordHandle=GLES20.glGetAttribLocation(programId,"aTexCoord");


        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        ShaderUtils.checkGlError("glBindTexture mTextureID");

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);

        Surface surface = new Surface(surfaceTexture);
        mediaPlayer.setSurface(surface);
        surface.release();

        if (!playerPrepared){
            try {
                mediaPlayer.prepare();
                playerPrepared=true;
            } catch (IOException t) {
                Log.e(TAG, "media player prepare failed");
            }
            mediaPlayer.start();
            playerPrepared=true;
        }
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        synchronized (this){
            if (updateSurface){
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(mSTMatrix);
                updateSurface = false;
            }
        }
        //GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixHandle,1,false,projectionMatrix,0);
        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, mSTMatrix, 0);

        textureVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aTextureCoordHandle,2,GLES20.GL_FLOAT,false,8,textureVertexBuffer);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);

        GLES20.glUniform1i(uTextureSamplerHandle,0);
        GLES20.glViewport(0,0,screenWidth,screenHeight);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
////////////////////////////////////Left///////////////////////////////////////////////
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, grid.getIndicesCount(), GLES20.GL_UNSIGNED_INT, indiceBuffer);

////////////////////////////////////Right///////////////////////////////////////////////
        vertexBufferRight.position(0);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBufferRight);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, grid.getIndicesCount(), GLES20.GL_UNSIGNED_INT, indiceBuffer);
    }

    @Override
    synchronized public void onFrameAvailable(SurfaceTexture surface) {
        updateSurface = true;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        screenWidth=width; screenHeight=height;
        float ratio=width>height?
                (float)width/height:
                (float)height/width;
        if (width>height){
            Matrix.orthoM(projectionMatrix,0,-ratio,ratio,-1f,1f,-1f,1f);
        }else Matrix.orthoM(projectionMatrix,0,-1f,1f,-ratio,ratio,-1f,1f);
    }

}
