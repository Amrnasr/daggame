package edu.union;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.OpenGLContext;
import android.opengl.GLU;
import android.view.View;

public class GLTutorialSix extends View {
	private OpenGLContext glContext;
	
	float box[] = new float[] {
			// FRONT
			-0.5f, -0.5f,  0.5f,
			 0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			// BACK
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// LEFT
			-0.5f, -0.5f,  0.5f,
			-0.5f,  0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			-0.5f,  0.5f, -0.5f,
			// RIGHT
			 0.5f, -0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			// TOP
			-0.5f,  0.5f,  0.5f,
			 0.5f,  0.5f,  0.5f,
			 -0.5f,  0.5f, -0.5f,
			 0.5f,  0.5f, -0.5f,
			// BOTTOM
			-0.5f, -0.5f,  0.5f,
			-0.5f, -0.5f, -0.5f,
			 0.5f, -0.5f,  0.5f,
			 0.5f, -0.5f, -0.5f,
		};

	FloatBuffer cubeBuff;
	
	protected FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
	
	public GLTutorialSix(Context c) {
		super(c);
		glContext = new OpenGLContext(OpenGLContext.DEPTH_BUFFER);
		GL10 gl = (GL10)glContext.getGL();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		cubeBuff = makeFloatBuffer(box);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glClearDepthf(1.0f);
	}
	
	float xrot = 0.0f;
	float yrot = 0.0f;
	
	protected void onDraw(Canvas canvas) {
		GL10 gl = (GL10)glContext.getGL();
		int w = getWidth();
		int h = getHeight();
		
		glContext.waitNative(canvas, this);
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,w,h);
		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, 1f, 100f);
			
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);
	
		gl.glShadeModel(GL10.GL_SMOOTH);
		
 		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	
		gl.glRotatef(45.0f, 1, 0, 0);
		gl.glRotatef(45.0f, 0, 1, 0);
	
		gl.glColor4f(1.0f, 0, 0, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
	
		gl.glColor4f(0, 1.0f, 0, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
		
		gl.glColor4f(0, 0, 1.0f, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
	
		glContext.waitGL();
	}
}