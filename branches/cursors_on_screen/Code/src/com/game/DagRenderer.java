package com.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.game.MessageHandler.MsgReceiver;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
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
	// To receive messages from the logic thread.
	private Handler handler;
	
	//Debug info
	FloatBuffer floatBuff;
	float[] debSquare = new float[] 
	                              { 30f, 30f, 1.0f,
									0f, 30f, 1.0f,
									30f, 0f, 1.0f,
									0f, 0f, 1.0f };
	
	// A square to be drawn as the cursor, at each position we have a cursor
	FloatBuffer cursorBuff;
	
	// Position for each player cursor
	private Vector<float[]> cursorPos;
	
	// Color for each player cursor
	private Vector<float[]> cursorColor;
	
	private int height;
	private int width;
	
	//The tilemap rendered in the debug mode
	private Vector<Tile> tileMap;	
	
	private int bufferLength;
	
	public DagRenderer()
	{
		super();
		
		tileMap = null;
		
		// Link square to a float buffer
		floatBuff = makeFloatBuffer(debSquare);
		cursorBuff = makeFloatBuffer(debSquare);
		
		cursorPos = new Vector<float[]>();
		cursorColor = new Vector<float[]>();
		
		// Create one cursor per player
		CreateCursors(Preferences.Get().GetNumberOfPlayers());
		
	    height = 0;
	    width = 0;
		
		// Initialize handler
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {
	        	if(msg.what == MsgType.NEW_TILEMAP.ordinal())
	        	{	        	
	        		tileMap = (Vector<Tile>) msg.obj;
	        		int rowTiles = msg.arg1;
	        		int columnTiles = msg.arg2;
	        		width=rowTiles*Constants.TileWidth;
	        		height=columnTiles*Constants.TileWidth;
	        		
	        		float[] floatArray= new float[rowTiles*columnTiles*6*3];
	        		
	        		Iterator<Tile> it = tileMap.listIterator();
	        		Tile tile = null;
	        		bufferLength = 0;
	        		for(int j = 0; j < columnTiles; j++){
	        			for(int i = 0; i < rowTiles; i++){			
	        				tile = it.next();
	        				if(tile.maxCapacity > 0){
	        					floatArray[bufferLength] = i*Constants.TileWidth; 
	        					floatArray[bufferLength+1] = j*Constants.TileWidth;
	        					floatArray[bufferLength+2] = 0.0f; 
	        					
	        					floatArray[bufferLength+3] = i*Constants.TileWidth+Constants.TileWidth; 
	        					floatArray[bufferLength+4] = j*Constants.TileWidth;
	        					floatArray[bufferLength+5] = 0.0f; 
	        					
	        					floatArray[bufferLength+6] = i*Constants.TileWidth; 
	        					floatArray[bufferLength+7] = j*Constants.TileWidth+Constants.TileWidth;
	        					floatArray[bufferLength+8] = 0.0f; 
	        					
	        					floatArray[bufferLength+9] = i*Constants.TileWidth+Constants.TileWidth; 
	        					floatArray[bufferLength+10] = j*Constants.TileWidth;
	        					floatArray[bufferLength+11] = 0.0f; 
	        					
	        					floatArray[bufferLength+12] = i*Constants.TileWidth+Constants.TileWidth; 
	        					floatArray[bufferLength+13] = j*Constants.TileWidth+Constants.TileWidth;
	        					floatArray[bufferLength+14] = 0.0f; 
	        					
	        					floatArray[bufferLength+15] = i*Constants.TileWidth; 
	        					floatArray[bufferLength+16] = j*Constants.TileWidth+Constants.TileWidth;
	        					floatArray[bufferLength+17] = 0.0f; 
	        					
	        					bufferLength += 18;
	        				}
	        			}			
	        		}
	        		
	        		floatBuff = makeFloatBuffer(floatArray); 
	        	}
	        	else if(msg.what == MsgType.NEW_BITMAP.ordinal()){
	        		
	        	}
	        	else if(msg.what == MsgType.UPDATE_CURSOR_POS.ordinal())
	        	{
	        		ChangeCursorPosition(msg.arg1, (float[]) msg.obj);
	        	}
	        }
	    };
	    
	    MessageHandler.Get().SetRendererHandler(this.handler);
	    MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.RENDERER_CONSTRUCTOR_DONE);
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
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		height = h;
		width = w;
	}
	 
	public void onDrawFrame(GL10 gl) 
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		//gl.glTranslatef(-Camera.Get().GetScreenWidth()/2.0f,-Camera.Get().GetScreenHeight()/2.0f,-(2.0f*Camera.Get().GetScreenWidth()));
		//gl.glTranslatef(-width/2.0f,-height/2.0f,-(2.0f*width));
		gl.glTranslatef(-Camera.Get().X(),-Camera.Get().Y(),-Camera.Get().Z());
		
		// Draw debug square
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);		
		if(tileMap != null){
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, bufferLength/3);
			DrawCursors(gl);
		}
		
				
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
	
	private void DrawCursors(GL10 gl)
	{		
		for(int i = 0; i < cursorPos.size(); i++)
		{		
			float[] cp = cursorPos.elementAt(i);
			float[] cc = cursorColor.elementAt(i);
			
			gl.glPushMatrix();
			
			gl.glTranslatef(cp[0],cp[1],cp[2]);		
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cursorBuff);
			gl.glColor4f(cc[0], cc[1], cc[2], cc[3]);		
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			gl.glPopMatrix();
		}
		
		/*
		gl.glColor4f(1, 0, 0, 1);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cursorBuff);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);*/

	}
	
	private void CreateCursors(int numbCursors)
	{
		for(int i=0; i < numbCursors; i++)
		{
			float[] pos = new float[3];
			pos[0] = 0;
			pos[1] = 0;
			pos[2] = 1;
			cursorPos.add(pos);
			
			float[] color = new float[4];
			color[0] = 90;
			color[1] = 0;
			color[2] = 0;
			color[3] = 1;
			cursorColor.add(color);
		}
	}
	
	private void ChangeCursorPosition(int cursor, float[] position)
	{
		cursorPos.elementAt(cursor)[0] = position[0];
		cursorPos.elementAt(cursor)[1] = position[1];
		cursorPos.elementAt(cursor)[2] = position[2];
		
		/*Log.i("Renderer", "New cursor pos: " + cursorPos.elementAt(cursor)[0] + 
				", " + cursorPos.elementAt(cursor)[1] +
				", " + cursorPos.elementAt(cursor)[2]);*/
	}
	
	private void ChangeCursorColor(int cursor, float[] color)
	{
		cursorColor.elementAt(cursor)[0] = color[0];
		cursorColor.elementAt(cursor)[1] = color[1];
		cursorColor.elementAt(cursor)[2] = color[2];
		cursorColor.elementAt(cursor)[3] = color[3];
	}
}
