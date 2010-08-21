package com.game;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Renderer for the GLSurface
 * 
 * TODO: Set the correct device coordinates, right now it's messed up.
 * @author Ying
 *
 */
public class DagRenderer implements GLSurfaceView.Renderer 
{
	// To send messages to the logicThread, mainly status messages,
	// as practically no decisions are taken in the render thread.
	private Handler sendToLogic;
	
	// To receive messages from the logic thread.
	private Handler receiveFromLogic;
	
	//Debug info
	FloatBuffer floatBuff;
	float[] debSquare = new float[] 
	                              { 30f, 30f, 0.0f,
									-25f, 30f, 0.0f,
									30f, -25f, 0.0f,
									-25f, -25f, 0.0f };
	
	private int height;
	private int width;
	public DagRenderer()
	{
		super();
		
		// Link square to a float buffer
		floatBuff = FloatBuffer.wrap(debSquare);
		
		// Initialize handler
		this.receiveFromLogic = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {
	        	//TODO: Handle the messages that arrive from the logic thread here.
	        	
	        }
	    };
	    
	    height = 0;
	    width = 0;
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {        
    	
    }
	
	public void onSurfaceChanged(GL10 gl, int w, int h) 
	{        
		Log.i("DagRenderer","Surface changed: " + w + " / " + h );
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glViewport(0,0,w,h);
		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, 1f, 2000f);
		
		gl.glColor4f(1, 0, 0, 0.5f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		height = h;
		width = w;
	}
	 
	public void onDrawFrame(GL10 gl) 
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glTranslatef(-width/2,-height/2,-(2*height));
		
		// Draw debug square
		gl.glColor4f(1, 0, 0, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
				
    }

	public void setLogicHandler(Handler refHandler)
	{
		sendToLogic = refHandler;
		
    	// Now we've got a sender, lets pass the receiver to the logic thread.		
		sendToLogic.sendMessage(sendToLogic.obtainMessage(MsgType.RENDERER_LOGIC_HANDLER_LINK.ordinal(), receiveFromLogic));
	}
}
