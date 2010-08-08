package com.game;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

/**
 * Renderer for the GLSurface
 * 
 * TODO: This is a stub class so I din't forget how it was done. Lots of work still.
 * 
 * @author Ying
 *
 */
public class DagRenderer implements GLSurfaceView.Renderer 
{
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {        
    	
    }
	
	 public void onSurfaceChanged(GL10 gl, int w, int h) 
    {        
    	gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glViewport(0,0,w,h);
		//GLU.gluOrtho2D(gl, 0, w, h, 0);
		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, 1f, 2000f);
		
		gl.glColor4f(1, 0, 0, 0.5f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }
	 public void onDrawFrame(GL10 gl) 
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glTranslatef(-0,-0,-1);
		
		/*		
		gl.glColor4f(1, 0, 0, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		*/
		
    }

}
