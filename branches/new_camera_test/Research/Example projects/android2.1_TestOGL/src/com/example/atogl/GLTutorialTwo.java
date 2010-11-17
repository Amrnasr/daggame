package com.example.atogl;

import java.nio.FloatBuffer;
import java.util.Random;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.view.MotionEvent;

/**
 * http://www.zeuscmd.com/tutorials/opengles/07-OrthographicProjection.php
 * @author bburns
 */
public class GLTutorialTwo extends GLTutorialBase { 
	// Coordinates for a 2D square
	float[] square2 = new float[] { 	-0.25f, -0.25f, 0.0f,
									0.25f, -0.25f, 0.0f,
									-0.25f, 0.25f, 0.0f,
									0.25f, 0.25f, 0.0f };
	float[] square = new float[] { 	30f, 30f, 0.0f,
									250f, 30f, 0.0f,
									30f, 250f, 0.0f,
									250f, 250f, 0.0f };
	float[] points;
	FloatBuffer floatBuff;
	
	// NIO Buffer for the square

	boolean pressing;
	float moveX, moveY;
	int stepSize;
	
	long previousTime;
	int fps;
	
	 Random rand;

	
	public GLTutorialTwo(Context c) {
		super(c);
		//squareBuff = makeFloatBuffer(square);
		rand = new Random();
		createPoints(1000);
		floatBuff = FloatBuffer.wrap(points); // Changes to square will modd floatBuff, and viceversa. COOL!
		pressing = false;
		moveY = 0f;
		moveX = 0f;
		stepSize = 100;
		fps = 0;
		previousTime = System.currentTimeMillis();
		
	}

	protected void init(GL10 gl) {		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
	}
	
	private void UpdatePoints()
	{
		if(!pressing) return;
		
		float vecX = 0, vecY = 0;
		
		for(int i = 0; i < points.length; i+=3)
		{
			vecX = (moveX - points[i])/stepSize;
			vecY = (moveY - points[i+1])/stepSize;
			
			points[i] += vecX;
			points[i+1] += vecY;
		}
	}
	
	
	private void createPoints(int number)
	{
		int numEle = number * 3;
		points = new float[numEle];
		
		for(int i = 0; i < numEle; i+=3)
		{
			points[i]= rand.nextFloat()*300+20;
			points[i+1]= rand.nextFloat()*300+20;
			points[i+2]= 0f;
		}
		
	}
	
	public void onDrawFrame(GL10 gl) {
		
		handler.sendEmptyMessage(0);
		this.UpdatePoints();
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0,0,-1);
		
		gl.glColor4f(1, 0, 0, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDrawArrays(GL10.GL_POINTS, 0, points.length/3);

	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		//Log.i("OnTouchEvent", "Reached function");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//Log.i("OnTouchEvent", "ACTION_DOWN");
			pressing = true;
			moveX = event.getX();
			moveY = event.getY();
			//Log.i("OnTouchEvent", "ACTION_DOWN "+ moveX + " " + moveY);
			break;
		case MotionEvent.ACTION_UP:
			//Log.i("OnTouchEvent", "ACTION_UP");
			pressing = false;
			break;
		case MotionEvent.ACTION_MOVE:
			//Log.i("OnTouchEvent", "ACTION_MOVE");
			moveX = event.getX();
			moveY = event.getY();
			break;
		default:
			break;
		}
		
		return true;
	}
}