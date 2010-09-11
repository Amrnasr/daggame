package edu.union;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.OpenGLContext;
import android.opengl.GLU;
import android.view.SurfaceHolder;
import android.view.View;

public class GLTutorialTwo extends View { 
	private OpenGLContext glContext;
	
	float[] square = new float[] { 	0.25f, 0.25f, 0.0f,
									0.75f, 0.25f, 0.0f,
									0.25f, 0.75f, 0.0f,
									0.75f, 0.75f, 0.0f };
	
	FloatBuffer squareBuff;
	
	public GLTutorialTwo(Context c) {
		super(c);
		glContext = new OpenGLContext(0);
		GL10 gl = (GL10)glContext.getGL();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		ByteBuffer bb = ByteBuffer.allocateDirect(square.length*4);
		bb.order(ByteOrder.nativeOrder());
		squareBuff = bb.asFloatBuffer();
		squareBuff.put(square);
		squareBuff.position(0);
				
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0.0f,1.2f,0.0f,1.0f);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, squareBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	protected void onDraw(Canvas canvas) {
		GL10 gl = (GL10)glContext.getGL();
		
		glContext.waitNative(canvas, this);
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glColor4f(1,1,1,1);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
        glContext.waitGL();
	}
}