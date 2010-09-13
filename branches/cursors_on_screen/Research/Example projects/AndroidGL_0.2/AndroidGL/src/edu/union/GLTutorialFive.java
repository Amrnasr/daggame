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

public class GLTutorialFive extends View {
	private OpenGLContext glContext;
	
	float[] triangle = new float[] { 0.25f, 0.25f, 0.0f,
									 0.75f, 0.25f, 0.0f,
									 0.25f, 0.75f, 0.0f };
	
	float[] colors = new float[] { 	1, 0, 0, 1,
									0, 1, 0, 1,
									0, 0, 1, 1 };

	FloatBuffer triangleBuff;
	FloatBuffer colorBuff;
	
	protected FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
	
	public GLTutorialFive(Context c) {
		super(c);
		glContext = new OpenGLContext(OpenGLContext.DEPTH_BUFFER);
		GL10 gl = (GL10)glContext.getGL();
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, 0.0f,1.3f,0.0f,1.0f);
	
		triangleBuff = makeFloatBuffer(triangle);
		colorBuff = makeFloatBuffer(colors);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glClearDepthf(1.0f);
	
	}
	

	
	protected void onDraw(Canvas canvas) {
		GL10 gl = (GL10)glContext.getGL();
		
		glContext.waitNative(canvas, this);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuff);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glPushMatrix();
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(-0.2f,-.1f,-1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(0.1f,0.1f,-0.5f);
		gl.glRotatef(45.0f, 0, 0, 1);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
		gl.glPopMatrix();
		
		gl.glTranslatef(0.15f,0.05f,0.4f);
		gl.glRotatef(45,0,1,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
		gl.glPopMatrix();
		
		glContext.waitGL();
	}
}