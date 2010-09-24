package com.game;

import javax.microedition.khronos.opengles.GL;

import com.game.MessageHandler.MsgReceiver;

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
		
		setFocusable(true);
		
		setGLWrapper(
				new GLSurfaceView.GLWrapper() 
				{
					@Override
					public GL wrap(GL gl) { return new MatrixTrackingGL(gl); }
				});  
		
        mRenderer = new DagRenderer();
        setRenderer(mRenderer);
    }

    public boolean onTouchEvent(final MotionEvent event) 
    {
    	// Send any touches in this view to the logic thread to be processed.
    	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.TOUCH_EVENT, event);
    	return true;
    }

    public boolean onTrackballEvent(MotionEvent event)
    {
    	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.TRACKBALL_EVENT, event);
    	return true;
    }
    
}
