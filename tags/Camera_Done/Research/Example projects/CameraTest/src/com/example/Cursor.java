package com.example;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class Cursor {
	public int x;
	public int y;
	public int rx;
	public int by;
	public boolean bHasToBeOnScreen;
	
	float[] square = new float[] 
	                  	     { 	10f, 10f, 0.0f,
	                  			-10f, 10f, 0.0f,
	                  			10f, -10f, 0.0f,
	                  			-10f, -10f, 0.0f };
	FloatBuffer floatBuff;
	
	public Cursor(int x, int y, boolean onScreen)
	{
		this.x = x;
		this.y = y;		
		this.rx = x + 20;
		this.by = y + 20;
		this.bHasToBeOnScreen = onScreen;
		floatBuff = makeFloatBuffer(square);
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

	
	public void Draw(GL10 gl)
	{
		gl.glPushMatrix();
		
		if(bHasToBeOnScreen)
		{
			gl.glColor4f(0.5f, 0.5f, 0, 0.5f);
		}
		else
		{
			gl.glColor4f(0, 0.5f, 0.5f, 0.5f);
		}
		
		gl.glTranslatef(x,y,0);	
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);
		//gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glPopMatrix();
	}
}
