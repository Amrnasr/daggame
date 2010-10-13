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
	/**
	 * Specifies the state of the plays scene
	 * @author Ying
	 *
	 */
	public enum RenderState
	{
		/**
		 * When the state is created but not yet ready
		 */
		UNINITIALIZED,
		
		/**
		 * Game is ready to render, have fun kids!
		 */
		RENDERING,
		
		/**
		 * Game paused
		 */
		PAUSED
	}
	
	/**
	 * To receive messages from the logic thread.
	 */
	private Handler handler;
	
	/**
	 * OpenGl context copy
	 */
	GL10 gl = null;
	
	/**
	 * Vertex Array of the cursors
	 */
	float[] cursorSquare = new float[] 
	                              { 30f, 30f, 1.0f,
									0f, 30f, 1.0f,
									30f, 0f, 1.0f,
									0f, 0f, 1.0f };

	/**
	 * Reference to the cursors
	 */
	private Vector<Cursor> cursorsRef;
	
	/**
	 * Tile map rendered in the debug mode
	 */
	private Vector<Tile> tileMap;	
	
	/**
	 * Bitmap rendered in release mode
	 */
	private Bitmap bitmap;
	
	/**
	 * Map's vertex buffer
	 */
	private FloatBuffer vertexMapBuffer;
	
	/**
	 * Map's texture coordinates buffer
	 */
	private FloatBuffer textureMapBuffer;
	
	/**
	 * Map's normal coordinates buffer
	 */
	private FloatBuffer normalMapBuffer;
	
	/**
	 * Id of the texture of the map
	 */
	private int textureId;
	
	/**
	 * Length of the tile map array
	 */
	private int bufferLength;	
	
	/**
	 * Ambient light intensity
	 */
	private float LightAmbient[]= { 1.0f, 1.0f, 1.0f,1.0f};
	
	/**
	 * Ambient light material reflection
	 */
	private float matAmbient[] = { 1.0f, 1.0f, 1.0f,1.0f};

	/**
	 * Specifies the current renderer state.
	 */
	private RenderState state = RenderState.UNINITIALIZED;
	
	/**
	 * Texture loaded check
	 */
	private boolean texReady;
	
	/**
	 * Stores the last width provided by surface change callback
	 */
	private int lastWidth;
	
	/**
	 * Stores the last height provided by surface change callback
	 */
	private int lastHeight;
	
	/**
	 * Minimum z the camera can be at. Used for the perspective limits.
	 */
	private int minZ;
	
	/**
	 * Maximum z the camera can be at. Used for the perspective limits.
	 */
	private int maxZ;
	
	/**
	 * Is the amount of margin we have respecting the camera Z margins.
	 */
	private static final int camZOffset = 3;
	
	/**
	 * Notifies the draw function to update the surface if needed.
	 */
	private boolean surfaceUpdatePending;	
	
	private float[] lastProjectionMat = null;
	
	private float[] lastModelViewMat = null;
	
	/**
	 * Initializes the renderer and sets the handler callbacks.
	 */
	public DagRenderer()
	{
		super();
		Log.i("DagRenderer", "Started constructor");
		
		tileMap = null;
		bitmap = null;
		cursorsRef = null;
	    texReady = false;	
	    lastWidth = 0;
	    lastHeight = 0;
	    
	    lastProjectionMat = new float[16];
	    lastModelViewMat = new float[16];
	    
	    // Default frustrum cull planes so ogl doesn't go crazy on initializing.
		minZ = 10;
		maxZ = 100;
		surfaceUpdatePending = false;
		
		// Initialize handler
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {	
	        	// If asked to do a wcs to scs transform, do so and reply
	        	if(msg.what == MsgType.REQUEST_WCS_TRANSFORM.ordinal())
	        	{
	        		// TODO: Camera syncronization check 
	        		Vec2 reply = GetWorldCoords((Vec2)msg.obj, Camera.Get());
	        		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.REPLY_WCS_TRANSFORM_REQUEST, reply);
	        	}
	        	
	        	// Get the message to initialize the renderer. Do so.
	        	else if(msg.what == MsgType.INITIALIZE_RENDERER.ordinal())
	        	{
	        		Start((RenderInitData)msg.obj);
	        	}
	        }
	    };
	    
	    MessageHandler.Get().SetRendererHandler(this.handler);	   
	    Log.i("DagRenderer", "Renderer constructed");
	}
	
	/**
	 * Initializes the renderer data that is dependent on the logic data.
	 * It's called once Logic() and Logic.Start() have been called 
	 * @param initData A container of initialization data.
	 */
	public void Start(RenderInitData initData)
	{
		cursorsRef = initData.GetCursors();
		
		if(Constants.DebugMode)
		{
			// Create a debug tilemap
    		LoadTileMap(initData.GetTileMap());
		}
		else
		{
			//Store the bitmap and its dimensions in pixels
    		bitmap = initData.GetBitmap();
		}
		
		// The camZOffset is because the min/max z of the camera is the limits we can see
		// at, so we need to give ourselves a little margin to see just at the limits, where our stuff is.
		this.minZ = Camera.Get().GetMinZ() - camZOffset;
		this.maxZ = 2*Camera.Get().GetMaxZ() + camZOffset;
		
		// Z's changed, so tell the update to update the surface perspective.
		surfaceUpdatePending = true;		
	    
	    MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.ACTIVITY_DISMISS_LOAD_DIALOG);
	    MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.RENDERER_INITIALIZATION_DONE);		    
		state = RenderState.RENDERING;
		
	    Log.i("DagRenderer", "Initialization done");
	}
	
	/**
	 * Called when a Opengl surface is created.
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {   
		Log.i("DagRenderer","Surface Created" );
		
		//Enable blending
		gl.glEnable (GL10.GL_BLEND);
		gl.glBlendFunc (GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
    }
	
	/**
	 * Called when the surface changes size. 
	 * Might be called at any moment. 
	 */
	public void onSurfaceChanged(GL10 gl, int w, int h) 
	{        
		Log.i("DagRenderer","Surface changed: " + w + " / " + h );
		
		this.gl = gl;
		
		this.lastWidth = w;
		this.lastHeight = h;
		
		SurfaceShapeUpdate(gl);
	}
	
	/**
	 * Utility function that resets the surface shape.
	 * Called when the w/h or the minz/maxz change.
	 * @param gl
	 */
	public void SurfaceShapeUpdate(GL10 gl)
	{
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		
		int w = lastWidth;
		int h = lastHeight;
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,w,h);

		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, minZ, maxZ);	
	}
	 
	/**
	 * Called every draw step.
	 * Renders the scene and takes care of updating any logic inherent to the
	 * render thread
	 */
	public void onDrawFrame(GL10 gl) 
    {
		// Save context for matrix retrieval
		this.gl = gl;
		
		// Update the view if needed.
		if(surfaceUpdatePending)
		{
			SurfaceShapeUpdate(gl);
			surfaceUpdatePending = false;
		}
		
		// Initialize the buffers and matrices		
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// Camera transform
		gl.glTranslatef(-Camera.Get().X(),-Camera.Get().Y(),-Camera.Get().Z());	
		
		// If the bitmap hasn't been received don't do anything
		if(state != RenderState.RENDERING) return;		

		// Draw the corresponding data, debug or not.
		if(!Constants.DebugMode )
		{
			//Load the texture if it hasn't been loaded
			if(!texReady)
			{
        		SetTextures(gl);
			}
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			DrawTexturedMap(gl);
		}
		else
		{
			// Draw tile map			
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexMapBuffer);		
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, bufferLength/3);
		}	

		gl.glDisable(GL10.GL_LIGHTING);
		DrawCursors(gl);	
		
		getCurrentModelView(gl);
		getCurrentProjection(gl);
		
		DrawMinimap(gl);
    }
	
	/**
	 * Draws the textured map image.
	 * @param gl Opengl context
	 */
	private void DrawTexturedMap(GL10 gl)
	{
		
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
	
	/**
	 * Draws the minimap.
	 * @param gl Opengl context
	 */
	private void DrawMinimap(GL10 gl)
	{
		//Change to orthogonal projection
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,lastWidth,lastHeight);

		gl.glOrthof(-lastWidth/2f, lastWidth/2f, -lastHeight/2f, lastHeight/2f, minZ, maxZ);
		
		//Draw the minimap
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.3f);
		
		gl.glScalef(lastWidth/(Preferences.Get().mapWidth*3f), lastWidth/(Preferences.Get().mapWidth*3f), 1f);
		gl.glTranslatef((Preferences.Get().mapWidth*3f/lastWidth)*lastWidth/2f-Preferences.Get().mapWidth,(Preferences.Get().mapWidth*3f/lastWidth)*lastHeight/2f-Preferences.Get().mapHeight,-minZ-2f);	
		
		DrawTexturedMap(gl);
		
		DrawCursors(gl);
		
		//Return to a perspective projection
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,lastWidth,lastHeight);

		GLU.gluPerspective(gl, 45.0f, ((float)lastWidth)/lastHeight, minZ, maxZ);
	}
	
	/**
	 * Loads the textures needed into Opengl
	 * @param gl Opengl context.
	 */
	private void SetTextures(GL10 gl)
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
		float vertexArray[] = {Preferences.Get().mapWidth,Preferences.Get().mapHeight,0.0f,
				0f,Preferences.Get().mapHeight,0.0f,
				Preferences.Get().mapWidth,0f,0.0f,
				0f,0f,0.0f};
		Log.i("DagRenderer","width: " + Preferences.Get().mapWidth + " height: " + Preferences.Get().mapHeight);
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
	
	/**
	 * Creates the tilemap render from a list of tiles
	 * @param tilemap Vector of reference tiles.
	 */
	private void LoadTileMap(Vector<Tile> tilemap)
	{
		//Store the tilemap and its dimensions in pixels
		int rowTiles = Preferences.Get().mapWidth/Constants.TileWidth;
		int columnTiles = Preferences.Get().mapHeight/Constants.TileWidth;
		
		//Initialize the vertex array and other auxiliar variables
		float[] floatArray= new float[rowTiles*columnTiles*6*3];        		
		Iterator<Tile> it = tileMap.listIterator();
		Tile tile = null;
		bufferLength = 0;
		
		//Calculate the vertices of the tilemap
		for(int j = 0; j < columnTiles; j++){
			for(int i = 0; i < rowTiles; i++){			
				tile = it.next();
				if(tile.GetMaxCapacity() > 0){
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
		vertexMapBuffer = makeFloatBuffer(floatArray);
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
			Cursor cursor = cursorsRef.elementAt(i);
			//gl.glPushMatrix();
			float x =(float)cursor.GetPosition().X();
			float y = (float)cursor.GetPosition().Y();
			
			gl.glTranslatef(x,y,1);		
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cursor.GetBuffer());

			if(cursor.IsFromHuman())
			{
				gl.glColor4f(0, 0, 1, 1);
			}
			else
			{
				gl.glColor4f(1, 0, 0, 1);
			}
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			gl.glTranslatef(-x,-y,-1);		
			//gl.glPopMatrix();
			
			//cursorsRef.elementAt(i).DrawCursors(gl);
		}
	}
	
	/**
    * Record the current modelView matrix state. Has the side effect of
    * setting the current matrix state to GL_MODELVIEW
    * @param gl context
    */
   public void getCurrentModelView(GL10 gl) 
   {
		//float[] mModelView = new float[16];
		getMatrix(gl, GL10.GL_MODELVIEW, lastModelViewMat);
		//return lastModelViewMat;
   }

   /**
    * Record the current projection matrix state. Has the side effect of
    * setting the current matrix state to GL_PROJECTION
    * @param gl context
    */
   public void getCurrentProjection(GL10 gl) 
   {
	   //float[] mProjection = new float[16];
	   
       getMatrix(gl, GL10.GL_PROJECTION, lastProjectionMat);
       //return lastProjectionMat;
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
	   
	   //Print("In", normalizedInPoint);

	   /* Obtain the transform matrix and then the inverse. */
	   
	   Matrix.multiplyMM(transformMatrix, 0, lastProjectionMat, 0, lastModelViewMat, 0);
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