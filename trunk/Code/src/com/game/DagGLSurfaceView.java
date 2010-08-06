package com.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class DagGLSurfaceView extends GLSurfaceView 
{
	private DagRenderer mRenderer;
	
	public DagGLSurfaceView(Context context) 
    {
		super(context);        
        // Turn on error-checking and logging ERROR. DO NOT USE.
        //setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
        
        mRenderer = new DagRenderer();
        setRenderer(mRenderer);
    }

    public boolean onTouchEvent(final MotionEvent event) 
    {
        return true;
    }

    
}
