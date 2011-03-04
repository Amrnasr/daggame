package com.game.battleofpixels;

import javax.microedition.khronos.opengles.GL;

import com.game.battleofpixels.MessageHandler.MsgReceiver;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

/**
 * Surface view class for the PlayScene. 
 * 
 * @author Ying
 */
public class DagGLSurfaceView extends GLSurfaceView 
{
	/**
	 * OGL renderer object
	 */
	private DagRenderer mRenderer = null;
	
	/**
	 * Message handler for messages to the GLSurfaceView
	 */
	private Handler handler = null;
	
	/**
	 * Instantiates a new DagGLSurfaceView object. The new object creates it's own DagRenderer.
	 * @param context
	 */
	public DagGLSurfaceView(Context context) 
    {
		super(context);       
		
		setFocusable(true);
		
		// Wrapper set so the renderer can access the gl transformation matrixes.
		setGLWrapper(
				new GLSurfaceView.GLWrapper() 
				{
					@Override
					public GL wrap(GL gl) { return new MatrixTrackingGL(gl); }
				});  
		
		// Initialize handler
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {
	        	
	        }
	    };
	    
	    MessageHandler.Get().SetGLSurfaceHandler(this.handler);
	    
	    mRenderer = new DagRenderer();
        setRenderer(mRenderer);
    }

	/**
	 * Called when the user touches the screen.
	 * It notifies the logic of the touch and the position.
	 */
    public boolean onTouchEvent(final MotionEvent event) 
    {
    	// Send any touches in this view to the logic thread to be processed.
    	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.TOUCH_EVENT, event);
    	return true;
    }

    /**
     * Called when the user uses the trackball.
     * It notifies the logic of the trackball event.
     */
    public boolean onTrackballEvent(MotionEvent event)
    {
    	MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.TRACKBALL_EVENT, event);
    	return true;
    }    
}
