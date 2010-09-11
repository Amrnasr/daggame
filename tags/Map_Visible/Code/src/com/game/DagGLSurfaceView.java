package com.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
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
	
	private Handler logicHandRef;
	
	public DagGLSurfaceView(Context context) 
    {
		super(context);       
		
		logicHandRef = null;
		
		setFocusable(true);
		
        mRenderer = new DagRenderer();
        setRenderer(mRenderer);
    }

    public boolean onTouchEvent(final MotionEvent event) 
    {
    	// Send any touches in this view to the logic thread to be processed.
    	if(logicHandRef == null)
    	{
    		return false;
    	}
    	else
    	{
    		logicHandRef.sendMessage(logicHandRef.obtainMessage(MsgType.TOUCH_EVENT.ordinal(), event));
    		return true;
    	}
    }
    
    public void setLogicHandlerRef( Handler refLogicHandler)
    {
    	logicHandRef = refLogicHandler;
    	mRenderer.setLogicHandler(refLogicHandler);
    }

    public boolean onTrackballEvent(MotionEvent event)
    {
    	
    	if(this.logicHandRef == null)
    	{
    		return false;
    	}
    	else
    	{
    		logicHandRef.sendMessage(logicHandRef.obtainMessage(MsgType.TRACKBALL_EVENT.ordinal(), event));
    		return true;
    	}
    }
    
}
