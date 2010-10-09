package com.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Vector;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.game.MessageHandler.MsgReceiver;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Renderer for the GLSurface
 * @author Ying
 *
 */

public class DagRenderer implements GLSurfaceView.Renderer 
{	
	//To receive messages from the logic thread.
	private Handler handler;
	GL10 gl = null;
	
	//Debug info
	FloatBuffer floatBuff;

	float[] debSquare = new float[] 
	                              { 30f, 30f, 1.0f,
									0f, 30f, 1.0f,
									30f, 0f, 1.0f,
									0f, 0f, 1.0f };

	//Dimensions of the map
	private int mapWidth;
	private int mapHeight;

	// A square to be drawn as the cursor, at each position we have a cursor
	FloatBuffer cursorBuff;
	
	// Reference to the cursors
	private Vector<Cursor> cursorsRef;
	
	private int height;
	private int width;
	
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
		cursorBuff = makeFloatBuffer(debSquare);
		

		cursorsRef = new Vector<Cursor>();
		
	    height = 0;
	    width = 0;
		
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
	        	else if(msg.what == MsgType.GET_CURSOR_VECTOR.ordinal())
	        	{
	        		Vector<Cursor> curs = (Vector<Cursor>) msg.obj;
	        		for(int i = 0; i < curs.size(); i++)
	        		{
	        			cursorsRef.add(curs.elementAt(i));
	        		}
	        	}
	        	else if(msg.what == MsgType.REQUEST_WCS_TRANSFORM.ordinal())
	        	{
	        		// TODO: Camera syncronization check 
	        		Vec2 reply = GetWorldCoords((Vec2)msg.obj, Camera.Get());
	        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.REPLY_WCS_TRANSFORM_REQUEST, reply);
	        	}
	        }
	    };
	    
	    MessageHandler.Get().SetRendererHandler(this.handler);
	    MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.RENDERER_CONSTRUCTOR_DONE);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {   
		gl.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
    }
	
	public void onSurfaceChanged(GL10 gl, int w, int h) 
	{        
		Log.i("DagRenderer","Surface changed: " + w + " / " + h );
		this.gl = gl;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,w,h);

		// TODO: IMPORTANT Make them dependant on map size
		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, 900f, 4000f);

		
		gl.glColor4f(1.0f, 0.0f, 1.0f, 0.5f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		height = h;
		width = w;

	}
	 
	public void onDrawFrame(GL10 gl) 
    {
		// Save context for matrix retrieval
		this.gl = gl;
		
		//Initialize the buffers and matrices		
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glTranslatef(-Camera.Get().X(),-Camera.Get().Y(),-Camera.Get().Z());	
		
		//If the bitmap hasn't been received don't do anything
		if(!ready) return;		

		if(!Constants.DebugMode )
		{
			//Load the texture if it hasn't been loaded
			if(!texReady)
			{
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
        		float vertexArray[] = {mapWidth,mapHeight,0.0f,
        				0f,mapHeight,0.0f,
        				mapWidth,0f,0.0f,
        				0f,0f,0.0f};
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

			//gl.glTranslatef(0.0f,-(mapHeight/2.0f),-(mapWidth*2.0f));
			
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			//Set the vertices
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexMapBuffer);
    		
    		//Set the texture coordinates
    		
    		gl.glEnable(GL10.GL_TEXTURE_2D);
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
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
		else
		{
			//gl.glTranslatef(-mapWidth/2.0f,-mapHeight/2.0f,-(2.0f*mapWidth));

			// Draw tilemap
			
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuff);		
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, bufferLength/3);
			
		}	

		
		gl.glDisable(GL10.GL_LIGHTING);
		DrawCursors(gl);
							
    }
	

	/**
	 * Transform a float array into a floatBuffer usable by ogl 1.0
	 * @param arr to transform
	 * @return the related floatbuffer.
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
	
	/**
	 * Draw the player cursors.
	 * @param gl is the opengl context
	 */
	private void DrawCursors(GL10 gl)
	{			
		for(int i = 0; i < cursorsRef.size(); i++ )
		{
			cursorsRef.elementAt(i).DrawCursors(gl);
		}
	}
	
	/**
    * Record the current modelView matrix state. Has the side effect of
    * setting the current matrix state to GL_MODELVIEW
    * @param gl context
    */
   public float[] getCurrentModelView(GL10 gl) 
   {
		float[] mModelView = new float[16];
		getMatrix(gl, GL10.GL_MODELVIEW, mModelView);
		return mModelView;
   }

   /**
    * Record the current projection matrix state. Has the side effect of
    * setting the current matrix state to GL_PROJECTION
    * @param gl context
    */
   public float[] getCurrentProjection(GL10 gl) 
   {
	   float[] mProjection = new float[16];
       getMatrix(gl, GL10.GL_PROJECTION, mProjection);
       return mProjection;
   }

   /**
    * Fetches a specific matrix from opengl
    * @param gl context
    * @param mode of the matrix
    * @param mat initialized float[16] array to fill with the matrix
    */
   private void getMatrix(GL10 gl, int mode, float[] mat) 
   {
       MatrixTrackingGL gl2 = (MatrixTrackingGL) gl;
       gl2.glMatrixMode(mode);
       gl2.getMatrix(mat, 0);
   }
   
   /**
    * Prints a float vector
    * @param name to display
    * @param vec to display
    */
   private void Print(String name, float[] vec)
   {
	   String text = name + ": ";
	   for(int i = 0; i < vec.length; i+=1)
	   {
		   text = text + vec[i] + ", ";
	   }
	   Log.i("World Coords", text);
   }
   
   /**
    * Prints a int vector
    * @param name to display
    * @param vec to display
    */
   private void Print(String name, int[] vec)
   {
	   String text = "";
	   for(int i = 0; i < vec.length; i+=1)
	   {
		   text = text + vec[i] + ", " ;
	   }
	   Log.i(name, text);
   }   

   /**
    * Calculates the transform from screen coordinate system to world coordinate system coordinates 
    * for a specific point, given a camera position.
    * 
    * @param touch Vec2 point of screen touch, the actual position on physical screen (ej: 160, 240)
    * @param cam camera object with x,y,z of the camera and screenWidth and screenHeight of the device.
    * @return position in WCS.
    */
   public Vec2 GetWorldCoords( Vec2 touch, Camera cam)
   {
	   Log.i("World Coords", "-------------- ");
	   
	   // Initialize auxiliary variables.
	   Vec2 worldPos = new Vec2();
	   
	   // SCREEN height & width (ej: 320 x 480)
	   float screenW = cam.GetScreenWidth();
	   float screenH = cam.GetScreenHeight();
	   
	   Camera.Get().Position().Print("World Coords", "Camera");
	   touch.Print("World Coords", "Screen touch");
	   Log.i("World Coords", "Screen: " + screenW + ", " + screenH);
	   Log.i("World Coords", "World: " + Preferences.Get().mapWidth + ", " + Preferences.Get().mapHeight);
	   
	   // Auxiliary matrix and vectors to deal with ogl.
	   float[] invertedMatrix, transformMatrix, normalizedInPoint, outPoint;
	   invertedMatrix = new float[16];
	   transformMatrix = new float[16];
	   normalizedInPoint = new float[4];
	   outPoint = new float[4];

	   // Invert y coordinate, as android uses top-left, and ogl bottom-left.
	   int oglTouchY = (int) (screenH - touch.Y());
	   
	   /* Transform the screen point to clip space in ogl (-1,1) */	   
	   normalizedInPoint[0] = (float) ((touch.X()) * 2.0f / screenW - 1.0);
	   normalizedInPoint[1] = (float) ((oglTouchY) * 2.0f / screenH - 1.0);
	   normalizedInPoint[2] = - 1.0f;
	   normalizedInPoint[3] = 1.0f;
	   
	   Print("In", normalizedInPoint);

	   /* Obtain the transform matrix and then the inverse. */
	   Print("Proj", getCurrentProjection(gl));
	   Print("Model", getCurrentModelView(gl));
	   Matrix.multiplyMM(transformMatrix, 0, getCurrentProjection(gl), 0, getCurrentModelView(gl), 0);
	   Matrix.invertM(invertedMatrix, 0, transformMatrix, 0);	   

	   /* Apply the inverse to the point in clip space */
	   Matrix.multiplyMV(outPoint, 0, invertedMatrix, 0, normalizedInPoint, 0);
	   Print("Out ", outPoint);
	   
	   if (outPoint[3] == 0.0)
	   {
		   // Avoid /0 error.
		   Log.e("World coords", "Could not calculate world coordinates");
		   return worldPos;
	   }
	   
	   // Divide by the 3rd component to find out the real position.
	   worldPos.Set(outPoint[0] / outPoint[3], outPoint[1] / outPoint[3]);
	   
	   // Unnecesary, but here for log purposes.
	   float worldZ = outPoint[2] / outPoint[3];
	   
	   Log.i("World Coords", "Move to point: " + worldPos.X() + ", " + worldPos.Y() + ", " + worldZ);			   
	   
	   return worldPos;	   
   }

}