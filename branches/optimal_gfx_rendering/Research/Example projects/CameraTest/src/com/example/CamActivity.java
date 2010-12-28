package com.example;

import android.app.Activity;
import android.os.Bundle;

import android.opengl.GLSurfaceView;

public class CamActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
         
        
        mGLView = new CamGLSurfaceView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onPause() 
    {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() 
    {
        super.onResume();
        mGLView.onResume();
    }

    private GLSurfaceView mGLView;
}