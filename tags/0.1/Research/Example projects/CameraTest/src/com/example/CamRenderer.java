package com.example;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import java.util.Vector;
import java.util.Random;

class CamRenderer implements GLSurfaceView.Renderer 
{
	float[] square = new float[] 
	     { 	30f, 30f, 0.0f,
			25f, 30f, 0.0f,
			30f, 25f, 0.0f,
			25f, 25f, 0.0f };
	float[] square2 = new float[] { 	-0.25f, -0.25f, 0.0f,
			0.25f, -0.25f, 0.0f,
			-0.25f, 0.25f, 0.0f,
			0.25f, 0.25f, 0.0f };
	float[] mapSquare = new float[] 
	                  	     { 	-720f, -400f, 0.0f,
	                  			720f, -400f, 0.0f,
	                  			-720f, 400f, 0.0f,
	                  			720f, 400f, 0.0f };
	float[] screenSquare = new float[] 
	    	                 { 	-240f, -400f, 0.0f,
	    	                  	240f, -400f, 0.0f,
	    	                  	-240f, 400f, 0.0f,
	    	                  	240f, 400f, 0.0f };
	
	FloatBuffer floatBuff;
	FloatBuffer mapBuff;
	Vector<Cursor> cursors;
	int cx, cy, cz;
	int minZ; 
	
    public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {        
    	floatBuff = makeFloatBuffer(screenSquare);
    	mapBuff = makeFloatBuffer(mapSquare);
    	cursors = new Vector<Cursor>();
    	
    	Random rnd = new Random();
    	boolean a;
    	
    	for(int i =0; i < 10; i++)
    	{    		
    		if (i%2== 0) a = true; else a = false;
    		Cursor auxCur = new Cursor(rnd.nextInt(1400)-680,rnd.nextInt(700)-330, a);
    		cursors.add(auxCur);
    	}
    	
    	cx = 0;
    	cy = 0;
    	cz = 980;
    	minZ = 980;
    	
    	AllCursorsOnScreen();
    }
    
    protected static FloatBuffer makeFloatBuffer(float[] arr) 
    {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
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
    
    public void AllCursorsOnScreen()
    {
    	// Calculate bounding box
    	int minx = 2000, miny = 2000, maxx = -2000, maxy = -2000;
    	for(int i = 0; i < cursors.size(); i++)
    	{
    		Cursor c = cursors.elementAt(i);
    		if(c.bHasToBeOnScreen)
    		{
    			if(c.x < minx) minx = c.x;
    			if(c.y < miny) miny = c.y;
    			if(c.rx > maxx) maxx = c.rx;
    			if(c.by > maxy) maxy = c.by;
    		}
    	}
    	
    	// Add a little padding just in case
    	minx -= 20; 
    	miny -= 20;
    	maxx += 20;
    	maxy += 20;
    	
    	// Size of the bb
    	int w = maxx - minx;
    	int h = maxy - miny;
    	
    	// If the bb is smaller than the screen, adjust to screen size
    	if( w < 480)
    	{
    		int aux = (480-w)/2;
    		minx-= aux;
    		maxx += aux;
    	}
    	if(h < 800)
    	{
    		int aux = (800-h)/2;
    		miny -= aux;
    		maxy += aux;
    	}
    	
    	// Center camera
    	this.cx = minx + w/2;
    	this.cy = miny + h/2;
    	
    	// Position on Z
    	if(w > h)
    	{
    		this.cz = 2*w;
    	}
    	else 
    	{
    		this.cz = 2*h;
    	}
    	
    	if (this.cz < 960) this.cz = 960;
    	
    	Log.i("Pos", "x: " + cx + " y: " + cy + " z: " + cz);
    	
    }

    public void onDrawFrame(GL10 gl) 
    {
    	
    	
        //gl.glClearColor(mRed, mGreen, mBlue, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glTranslatef(-cx,-cy,-cz);
		
		gl.glColor4f(0.3f, 0.4f, 0.8f, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mapBuff);		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glColor4f(1, 0, 0, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		for(int i = 0; i < cursors.size(); i++)
		{
			cursors.elementAt(i).Draw(gl);
		}
    }

    public void setColor(float r, float g, float b) {
        mRed = r;
        mGreen = g;
        mBlue = b;
    }

    private float mRed;
    private float mGreen;
    private float mBlue;
}
