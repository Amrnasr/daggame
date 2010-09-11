package com.example;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

class CamGLSurfaceView extends GLSurfaceView 
{
    public CamGLSurfaceView(Context context) 
    {
        super(context);
        
        // Turn on error-checking and logging ERROR. DO NOT USE.
        //setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
        
        mRenderer = new CamRenderer();
        setRenderer(mRenderer);
    }

    public boolean onTouchEvent(final MotionEvent event) 
    {
        
        mRenderer.setColor(event.getX() / getWidth(), event.getY() / getHeight(), 1.0f);
        
        return true;
    }

    CamRenderer mRenderer;
}

/*queueEvent(
new Runnable()
{
	public void run() 
	{
		mRenderer.setColor(event.getX() / getWidth(), event.getY() / getHeight(), 1.0f);
	}
}
);*/
