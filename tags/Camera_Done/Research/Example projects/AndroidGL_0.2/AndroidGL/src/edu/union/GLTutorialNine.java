package edu.union;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.OpenGLContext;
import android.opengl.GLU;
import android.view.View;

public class GLTutorialNine extends View {
	private OpenGLContext glContext;
	
	float lightAmbient[] = new float[] { 0.2f, 0.3f, 0.6f, 1.0f };
	float lightDiffuse[] = new float[] { 0.2f, 0.3f, 0.6f, 1.0f };
	float[] pos = new float[] {0,0,3,0};
	
	float matAmbient[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float matDiffuse[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	
	int tex;
	Bitmap bmp;
	
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
	
	float texCoords[] = new float[] {
			// FRONT
			 0.0f, 0.0f,
			 1.0f, 0.0f,
			 0.0f, 1.0f,
			 1.0f, 1.0f,
			// BACK
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// LEFT
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// RIGHT
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f,
			// TOP
			 0.0f, 0.0f,
			 1.0f, 0.0f,
			 0.0f, 1.0f,
			 1.0f, 1.0f,
			// BOTTOM
			 1.0f, 0.0f,
			 1.0f, 1.0f,
			 0.0f, 0.0f,
			 0.0f, 1.0f
		};

	FloatBuffer cubeBuff;
	FloatBuffer texBuff;
	
	protected FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
	
	public GLTutorialNine(Context c) {
		super(c);
		
		bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.zeus);
		
		glContext = new OpenGLContext(0);
		GL11 gl = (GL11)glContext.getGL();
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, pos, 0);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepthf(1.0f);
		
		cubeBuff = makeFloatBuffer(box);
		texBuff = makeFloatBuffer(texCoords);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuff);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.height()*bmp.width()*4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer ib = bb.asIntBuffer();
		
		for (int y=0;y<bmp.height();y++)
			for (int x=0;x<bmp.width();x++) {
					ib.put(bmp.getPixel(x,y));
			}
		ib.position(0);
		bb.position(0);
		
		int[] tmp_tex = new int[1];
		
		gl.glGenTextures(1, tmp_tex, 0);
		this.tex = tmp_tex[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.tex);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bmp.width(), bmp.height(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		setFocusable(true);
	}
	
	float xrot = 0.0f;
	float yrot = 0.0f;
	
	protected void onDraw(Canvas canvas) {
		GL11 gl = (GL11)glContext.getGL();
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
	
		gl.glRotatef(30.0f, 1, 0, 0);
		gl.glRotatef(40.0f, 0, 1, 0);
	
		gl.glColor4f(1.0f, 1, 1, 1.0f);
		gl.glNormal3f(0,0,1);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glNormal3f(0,0,-1);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
	
		gl.glColor4f(1, 1.0f, 1, 1.0f);
		gl.glNormal3f(-1,0,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
		gl.glNormal3f(1,0,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
		
		gl.glColor4f(1, 1, 1.0f, 1.0f);
		gl.glNormal3f(0,1,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
		gl.glNormal3f(0,-1,0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
	
		glContext.waitGL();
	}
}