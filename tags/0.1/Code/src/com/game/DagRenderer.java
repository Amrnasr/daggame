package com.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
	//To receive messages from the logic thread.
	private Handler handler;
	
	//Debug info
	FloatBuffer floatBuff;
	
	//Dimensions of the map
	private int mapWidth;
	private int mapHeight;
	
	//Tilemap rendered in the debug mode
	private Vector<Tile> tileMap;	
	
	//Bitmap rendered in release mode
	private Bitmap bitmap;
	private FloatBuffer vertexMapBuffer;
	private FloatBuffer textureMapBuffer;
	private FloatBuffer normalMapBuffer;
	
	//Id of the texture of the map
	private int textureId;
	
	//Length of the tilemap array
	private int bufferLength;	
	
	//Light parameters
	private float LightAmbient[]= { 1.0f, 1.0f, 1.0f,1.0f};
	
	//Map material parameters
	private float matAmbient[] = { 1.0f, 1.0f, 1.0f,1.0f};
	
	//Initialization checks
	private boolean ready;
	private boolean texReady;
	
	
	
	public DagRenderer()
	{
		super();
		Log.i("DagRenderer", "Started constructor");
		
		tileMap = null;
		bitmap = null;
		floatBuff = null;
		
		mapWidth = 0;
		mapHeight = 0;
	    
	    ready=false;
	    texReady=false;
		
		// Initialize handler
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {	//If a new tilemap is received
	        	if(msg.what == MsgType.NEW_TILEMAP.ordinal())
	        	{
	        		//Store the tilemap and its dimensions in pixels
	        		tileMap = (Vector<Tile>) msg.obj;
	        		int rowTiles = msg.arg1;
	        		int columnTiles = msg.arg2;
	        		mapWidth=rowTiles*Constants.TileWidth;
	        		mapHeight=columnTiles*Constants.TileWidth;
	        		
	        		//Initialize the vertex array and other auxiliar variables
	        		float[] floatArray= new float[rowTiles*columnTiles*6*3];        		
	        		Iterator<Tile> it = tileMap.listIterator();
	        		Tile tile = null;
	        		bufferLength = 0;
	        		
	        		//Calculate the vertices of the tilemap
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
	        		
	        		//store it in a float buffer
	        		floatBuff = makeFloatBuffer(floatArray);
	        		
	        		ready=true;
	        	}
	        	//if a new bitmap is received
	        	else if(msg.what == MsgType.NEW_BITMAP.ordinal()){
	        		//Store the bitmap and its dimensions in pixels
	        		bitmap = (Bitmap) msg.obj;
	        		
	        		mapWidth = bitmap.getWidth();
	        		mapHeight = bitmap.getHeight();

	        		ready=true;
	        	}
	        }
	    };
	    
	    MessageHandler.Get().SetRendererHandler(this.handler);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {   
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }
	
	public void onSurfaceChanged(GL10 gl, int w, int h) 
	{        
		Log.i("DagRenderer","Surface changed: " + w + " / " + h );
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glViewport(0,0,w,h);
		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, 1f, 4000f);
	}
	 
	public void onDrawFrame(GL10 gl) 
    {
		//Initialize the buffers and matrices
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		//If the bitmap hasn't been received don't do anything
		if(!ready) return;		

		if(!Constants.DebugMode){
			//Load the texture if it hasn't been loaded
			if(!texReady){
        		gl.glEnable(GL10.GL_LIGHTING);
        		
        		//Set the materials
        		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient,0);	
        		
        		//set the light
        		gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, LightAmbient,0);
        		
                gl.glEnable(GL10.GL_DEPTH_TEST);
                
                //Enable the use of textures and set the texture 0 as the current texture
        		gl.glEnable(GL10.GL_TEXTURE_2D);
        		gl.glClientActiveTexture(GL10.GL_TEXTURE0);
        		gl.glActiveTexture(GL10.GL_TEXTURE0); 
        		
        		//Generate the texture and bind it
        		int[] tmp_tex = new int[1];
        		gl.glGenTextures(1, tmp_tex, 0); 
        		textureId = tmp_tex[0];
        		
        		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        		
        		//Create the float buffers of the vertices, texture coordinates and normals
        		float vertexArray[] = {mapWidth/2.0f,mapHeight/2.0f,0.0f,
        				-mapWidth/2.0f,mapHeight/2.0f,0.0f,
        				mapWidth/2.0f,-mapHeight/2.0f,0.0f,
        				-mapWidth/2.0f,-mapHeight/2.0f,0.0f};
        		Log.i("DagRenderer","width: " + mapWidth + " height: " + mapHeight);
        		float textureArray[] = {1.0f,1.0f,0.0f,1.0f,1.0f,0.0f,0.0f,0.0f};
        		float normalArray[] = { 0.0f,0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,0.0f,-1.0f, 0.0f,0.0f,-1.0f };
        		
        		vertexMapBuffer = makeFloatBuffer(vertexArray);
        		textureMapBuffer = makeFloatBuffer(textureArray);
        		normalMapBuffer = makeFloatBuffer(normalArray);
        		
        		//Link the bitmap to the current texture
        		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        		
        		//Set the texture parameters
        		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
        				GL10.GL_CLAMP_TO_EDGE);
        		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
        				GL10.GL_CLAMP_TO_EDGE); 
        		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
        				GL10.GL_LINEAR);
        		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
        				GL10.GL_LINEAR);
        		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
        				GL10.GL_MODULATE); 
        		
        		//Set the rendering parameters
        		gl.glEnable(GL10.GL_CULL_FACE);
        		gl.glShadeModel(GL10.GL_FLAT);
        		
				texReady=true;
			}

			gl.glTranslatef(0.0f,-(mapHeight/2.0f),-(mapWidth*2.0f));
			
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			//Set the vertices
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexMapBuffer);
    		
    		//Set the texture coordinates
    		gl.glActiveTexture(GL10.GL_TEXTURE0); 
			gl.glClientActiveTexture(GL10.GL_TEXTURE0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId); 
			
    		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureMapBuffer);
    		
    		//Set the normals
    		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
    		gl.glNormalPointer(3, GL10.GL_FLOAT, normalMapBuffer);
			
    		//Draw the bitmap
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		}
		else{
			gl.glTranslatef(-mapWidth/2.0f,-mapHeight/2.0f,-(2.0f*mapWidth));

			// Draw tilemap
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);		
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, bufferLength/3);
		}	
    }
	
	/**
	 * Creates a float buffer from a float array
	 */
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
