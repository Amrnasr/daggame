package com.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
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
	// To receive messages from the logic thread.
	private Handler handler;
	
	//Debug info
	FloatBuffer floatBuff;
	float[] debSquare = new float[] 
	                              { 30f, 30f, 0.0f,
									-25f, 30f, 0.0f,
									30f, -25f, 0.0f,
									-25f, -25f, 0.0f };
	
	private int height;
	private int width;
	
	//Tilemap rendered in the debug mode
	private Vector<Tile> tileMap;	
	
	//Bitmap rendered in release mode
	private Bitmap bitmap;
	private FloatBuffer vertexMapBuffer;
	private FloatBuffer textureMapBuffer;
	//Bitmap resource
	private int mapFile;
	//Length of the tilemap array
	private int bufferLength;	
	
	public DagRenderer()
	{
		super();
		
		tileMap = null;
		bitmap = null;
		
		// Link square to a float buffer
		floatBuff = makeFloatBuffer(debSquare);
		
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
	        		bitmap = (Bitmap) msg.obj;
	        		
	        		mapFile = msg.arg1;
	        		
	        		width = bitmap.getWidth();
	        		height = bitmap.getHeight();
	        	}
	        }
	    };
	    
	    MessageHandler.Get().SetRendererHandler(this.handler);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {   
		
		/*gl.glClientActiveTexture(GL10.GL_TEXTURE0);
		gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		int[] tmp_tex = new int[1];
		gl.glGenTextures(1, tmp_tex, 0); 
		int tex = tmp_tex[0];
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);
		
		float[] vertexArray = {0.0f,0.0f,0.0f,width,0.0f,0.0f,width,height,0.0f,0.0f,height,0.0f};
		float[] textureArray = {0.0f,1.0f,1.0f,1.0f,0.0f,0.0f,1.0f,0.0f};;
		
		vertexMapBuffer = makeFloatBuffer(vertexArray);
		textureMapBuffer = makeFloatBuffer(textureArray);
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);*/

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
		if(!Constants.DebugMode){
			gl.glTranslatef(-width/2.0f,-height/2.0f,-(2.0f*width));
			
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			gl.glActiveTexture(GL10.GL_TEXTURE0);
			
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexMapBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureMapBuffer);
			
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		}
		else if(Constants.DebugMode && tileMap != null){
			gl.glTranslatef(-width/2.0f,-height/2.0f,-(2.0f*width));
			
			// Draw tilemap
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);		
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, bufferLength/3);
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
}
