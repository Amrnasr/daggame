package com.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Surface view class for the PlayScene. 
 * 
 * TODO: This is a stub class so I din't forget how it was done. Lots of work still.
 * 
 * @author Ying
 */
public class DagGLSurfaceView extends GLSurfaceView 
{
	private DagRenderer mRenderer;
	
	public DagGLSurfaceView(Context context) 
    {
		super(context);        
		
        mRenderer = new DagRenderer();
        setRenderer(mRenderer);
    }

    public boolean onTouchEvent(final MotionEvent event) 
    {
        return true;
    }

    
}
