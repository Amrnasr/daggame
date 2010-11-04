package com.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.game.MessageHandler.MsgReceiver;

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
	 * Reference to the cursors
	 */
	private Vector<Cursor> cursorsRef;
	
	/**
	 * Map to be rendered
	 */
	private Map map;
	
	/**
	 * Cursor bitmap for rendering
	 */
	private Bitmap cursorBitmap;
	
	/**
	 * Map vertex buffer
	 */
	private FloatBuffer vertexMapBuffer;
	
	/**
	 * Map texture coordinates buffer
	 */
	private FloatBuffer textureMapBuffer;
	
	/**
	 * Id of the texture of the map
	 */
	private int mapTextureId;
	
	/**
	 * Id of the texture of the cursor
	 */
	private int cursorTextureId;
	
	/**
	 * Length of the tile map array
	 */
	private int tileMapBufferLength;	

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
	
	/**
	 * Keeps track of the Projection matrix calculated on the last draw frame
	 */
	private float[] lastProjectionMat = null;
	
	/**
	 * Keeps track of the model view matrix calculated on the last frame
	 */
	private float[] lastModelViewMat = null;
	
	/**
	 * Vector of players to check for which tiles to render
	 */
	private Vector<Player> players;
	
	/**
	 * Length of the player armies array
	 */
	private int[] playersBufferLength;
	/**
	 * Player armies vertex buffer
	 */
	private FloatBuffer[] playersVertexBuffer;
	
	/**
	 * Player armies color buffer
	 */
	private FloatBuffer[] playersColorBuffer;
	
	private boolean showMinimap;
	
	/**
	 * Initializes the renderer and sets the handler callbacks.
	 */
	public DagRenderer()
	{
		super();
		Log.i("DagRenderer", "Started constructor");
		
		this.map = null;
		this.cursorBitmap = null;
		this.cursorsRef = null;
		this.players=null;
		this.texReady = false;	
		this.lastWidth = 0;
		this.lastHeight = 0;
	    this.showMinimap = false;
		
		this.playersVertexBuffer = new FloatBuffer[Constants.MaxPlayers];
		this.playersColorBuffer = new FloatBuffer[Constants.MaxPlayers];
		this.playersBufferLength = new int[Constants.MaxPlayers];
	    
		this.lastProjectionMat = new float[16];
		this.lastModelViewMat = new float[16];
	    
	    // Default frustrum cull planes so ogl doesn't go crazy on initializing.
	    this.minZ = 10;
	    this.maxZ = 100;
	    this.surfaceUpdatePending = false;
		
		// Initialize handler
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {	
	        	// If asked to do a wcs to scs transform, do so and reply
	        	if(msg.what == MsgType.REQUEST_WCS_TRANSFORM.ordinal())
	        	{
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
		this.cursorsRef = initData.GetCursors();
		
		this.map= initData.GetMap();
		if(Constants.DebugMode)
		{
			// Create a debug tile map
    		LoadTileMap(map.getTileMap());
		}

		//Store the cursor bitmap
		this.cursorBitmap = initData.GetCursorBitmap();
		
		showMinimap = (!Preferences.Get().multiplayerGame && Preferences.Get().singleShowMinimap) || (Preferences.Get().multiplayerGame && Preferences.Get().multiShowMinimap);
		Log.i("DagRenderer","multiplayerGame: " + Preferences.Get().multiplayerGame + ", singleShowMinimap: " + Preferences.Get().singleShowMinimap + ", multiShowMinimap:" + Preferences.Get().multiShowMinimap);
		//Store the players vector
		this.players = initData.GetPlayers();
		
		// The camZOffset is because the min/max z of the camera is the limits we can see
		// at, so we need to give ourselves a little margin to see just at the limits, where our stuff is.
		this.minZ = Camera.Get().GetMinZ() - camZOffset;
		this.maxZ = 2*Camera.Get().GetMaxZ() + camZOffset;
		
		// Z's changed, so tell the update to update the surface perspective.
		this.surfaceUpdatePending = true;		
	    
	    MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.ACTIVITY_DISMISS_LOAD_DIALOG);
	    MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.RENDERER_INITIALIZATION_DONE);		    
	    this.state = RenderState.RENDERING;
		
		
		
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
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
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
		
		int w = this.lastWidth;
		int h = this.lastHeight;
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,w,h);

		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, this.minZ, this.maxZ);	
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
		if(this.surfaceUpdatePending)
		{
			SurfaceShapeUpdate(gl);
			this.surfaceUpdatePending = false;
		}
		
		// Initialize the buffers and matrices		
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// Camera transform
		gl.glTranslatef(-Camera.Get().X(),-Camera.Get().Y(),-Camera.Get().Z());	
		
		// If the bitmap hasn't been received don't do anything
		if(this.state != RenderState.RENDERING) return;	
		
		//Load the texture if it hasn't been loaded and it's necessary
		if(!Constants.DebugMode && !this.texReady) SetTextures(gl);
		
		// Draw background rectangle	
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glTranslatef(0f,0f,-1f);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexMapBuffer);		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glTranslatef(0f,0f,1f);
		
		LoadPlayers();
		
		DrawPlayers(gl);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		DrawCursors(gl);	
		
		// Draw the corresponding data, debug or not.
		if(!Constants.DebugMode )
		{
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			DrawTexturedMap(gl);
		}
		else
		{
			// Draw tile map			
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexMapBuffer);		
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, this.tileMapBufferLength/3);
		}	
		
		getCurrentProjection(gl);
		getCurrentModelView(gl);
		
		if(showMinimap){
			DrawMinimap(gl);
		}
    }
	
	/**
	 * Draws the textured map image.
	 * @param gl Opengl context
	 */
	private void DrawTexturedMap(GL10 gl)
	{	
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mapTextureId);
		
		//Set the vertices
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexMapBuffer);
		
		//Set the texture coordinates
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.textureMapBuffer);
		
		//Draw the bitmap
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}
	
	/**
	 * Draw the player cursors.
	 * @param gl is the opengl context
	 */
	private void DrawCursors(GL10 gl)
	{			
		gl.glBindTexture(GL10.GL_TEXTURE_2D, cursorTextureId);
		
		//Set the texture coordinates
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureMapBuffer);
		
		for(int i = 0; i < this.cursorsRef.size(); i++ )
		{
			Cursor cursor = this.cursorsRef.elementAt(i);
			//gl.glPushMatrix();
			float x =(float)cursor.GetPosition().X();
			float y = (float)cursor.GetPosition().Y();
			
			gl.glTranslatef(x,y,1);

			SetCursorColor(gl,i);
			
			//Set the vertices
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cursor.GetBuffer());
			
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			gl.glTranslatef(-x,-y,-1);		
			//gl.glPopMatrix();
			
			//cursorsRef.elementAt(i).DrawCursors(gl);
		}
	}
	
	/**
	 * Draws the density polygons of the players.
	 * @param gl Opengl context
	 */
	private void DrawPlayers(GL10 gl)
	{
		// Draw tile map			
		for(int i = 0; i < players.size(); i++){
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.playersVertexBuffer[i]);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, this.playersColorBuffer[i]);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, this.playersBufferLength[i]/3);
			
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
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
		gl.glViewport(0,0,this.lastWidth,this.lastHeight);

		gl.glOrthof(-this.lastWidth/2f, this.lastWidth/2f, -this.lastHeight/2f, this.lastHeight/2f, this.minZ, this.maxZ);
		
		//Draw the minimap
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glScalef(this.lastWidth/(Preferences.Get().mapWidth*3f), this.lastWidth/(Preferences.Get().mapWidth*3f), 1f);
		gl.glTranslatef((Preferences.Get().mapWidth*3f/this.lastWidth)*this.lastWidth/2f-Preferences.Get().mapWidth,(Preferences.Get().mapWidth*3f/this.lastWidth)*this.lastHeight/2f-Preferences.Get().mapHeight,-this.minZ-2f);	
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		DrawPlayers(gl);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		DrawCursors(gl);
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.3f);
		DrawTexturedMap(gl);
		
		//Return to a perspective projection
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,this.lastWidth,this.lastHeight);

		GLU.gluPerspective(gl, 45.0f, ((float)this.lastWidth)/this.lastHeight, this.minZ, this.maxZ);
	}
	
	/**
	 * Creates the players vertex buffer
	 */
	private void LoadPlayers(){
		//Calculate the size of the vertex array 
		float[] tilesPerPlayer = new float[Constants.MaxPlayers];
		for(int i = 0; i < players.size(); i++)
		{
			int size = this.players.elementAt(i).GetTiles().size();
			this.playersBufferLength[i] = size*18;
			tilesPerPlayer[i]=size;
			
		}
		
		// For each player	
		for(int i = 0; i < players.size(); i++)
		{	
			float[] floatVertexArray= new float[playersBufferLength[i]]; 
			float[] floatColorArray= new float[playersBufferLength[i] + playersBufferLength[i]/3]; 
			
			int count = 0;
			
			
			Vector<Tile> playerTiles = this.players.elementAt(i).GetTiles();
			// For each tile occupied by the player
 			for(int j = 0; j < tilesPerPlayer[i] && j < this.players.elementAt(i).GetTiles().size() && count < playersBufferLength[i]; j++)
			{
				Tile curTile = playerTiles.elementAt(j);
				
				float tileX = ((float)curTile.GetPos().X());
				float tileY = ((float)curTile.GetPos().Y());
				//calculate the position of the vertices
				floatVertexArray[count] = tileX*Constants.TileWidth; 
				floatVertexArray[count+1] = tileY*Constants.TileWidth;
				floatVertexArray[count+2] = 0.5f; 
							
				floatVertexArray[count+3] = tileX*Constants.TileWidth+Constants.TileWidth; 
				floatVertexArray[count+4] = tileY*Constants.TileWidth;
				floatVertexArray[count+5] = 0.5f; 
							
				floatVertexArray[count+6] = tileX*Constants.TileWidth; 
				floatVertexArray[count+7] = tileY*Constants.TileWidth+Constants.TileWidth;
				floatVertexArray[count+8] = 0.5f; 
							
				floatVertexArray[count+9] = tileX*Constants.TileWidth+Constants.TileWidth; 
				floatVertexArray[count+10] = tileY*Constants.TileWidth;
				floatVertexArray[count+11] = 0.5f; 
							
				floatVertexArray[count+12] = tileX*Constants.TileWidth+Constants.TileWidth; 
				floatVertexArray[count+13] = tileY*Constants.TileWidth+Constants.TileWidth;
				floatVertexArray[count+14] = 0.5f; 
							
				floatVertexArray[count+15] = tileX*Constants.TileWidth; 
				floatVertexArray[count+16] = tileY*Constants.TileWidth+Constants.TileWidth;
				floatVertexArray[count+17] = 0.5f; 
				
				LoadPlayerColor(gl, curTile, count + count/3, floatColorArray);
				
				count += 18;
			}
 			//Store it in a float buffer
 			this.playersVertexBuffer[i] = makeFloatBuffer(floatVertexArray);
 			this.playersColorBuffer[i] = makeFloatBuffer(floatColorArray);
		}	
	}
	
	/**
	 * Sets the color used for rendering the cursor with the given player ID
	 * @param gl Opengl context
	 * @param playerID  The player ID
	 */
	private void SetCursorColor(GL10 gl,int playerID){
		int colorIndex = players.elementAt(playerID).GetColorIndex();
		switch(colorIndex){
			case 0: //Red
				gl.glColor4f(Constants.CursorColorIntensity, 0f, 0f, 1f);
				break;
			case 1: //Green
				gl.glColor4f(0f, Constants.CursorColorIntensity, 0f, 1f);
				break;
			case 2: //Blue
				gl.glColor4f(0f, 0f, Constants.CursorColorIntensity, 1f);
				break;	
			case 3: //Yellow
				gl.glColor4f(0f, Constants.CursorColorIntensity, Constants.CursorColorIntensity, 1f);
				break;
			case 4: //Purple
				gl.glColor4f(Constants.CursorColorIntensity, 0f, Constants.CursorColorIntensity, 1f);
				break;
			case 5: //Orange
				gl.glColor4f(Constants.CursorColorIntensity, Constants.CursorColorIntensity, 0f, 1f);
				break;
		}
		
	}
	
	/**
	 * Creates the color array used for rendering the players armies
	 * @param gl Opengl context
	 * @param tile The tile to be rendered
	 * @param count Initial position to store values in the array
	 * @param colorArray  The array to store the color values
	 */
	private void LoadPlayerColor(GL10 gl, Tile tile, int count, float[] colorArray)
	{
		float r = 0f, g = 0f, b = 0f, a=0f;
		
		//calculate the intensity of the color
		for(int i=0; i < players.size(); i++){
			int colorIndex = players.elementAt(i).GetColorIndex();
			float intensityChange = tile.GetDensityFrom(i) * 0.25f / (Constants.TileWidth* Constants.TileWidth);
			switch(colorIndex){
				case 0: //Red
					r += intensityChange;
					
					a += intensityChange;
					break;
				case 1: //Green
					g += intensityChange;
					
					a += intensityChange;
					break;
				case 2: //Blue
					b += intensityChange;
					
					a += intensityChange;
					break;	
				case 3: //Yellow
					g += intensityChange;
					b += intensityChange;
					
					a += intensityChange;
					break;
				case 4: //Purple
					r += intensityChange;
					b += intensityChange;
					
					a += intensityChange;
					break;
				case 5: //Orange
					r += intensityChange;
					g += intensityChange;
					
					a += intensityChange;
					break;
				default:
					r += intensityChange; 
					g += intensityChange; 
					b += intensityChange;
					
					a += intensityChange;
			}		
		}
		//Add the base intensity if necessary
		r = (r > 0f) ? (0.75f - r) : 0f; 
		g = (g > 0f) ? (0.75f - g) : 0f;
		b = (b > 0f) ? (0.75f - b) : 0f;
		a = (r+g+b > 0f) ? 0.75f + a : 0f;
		
		//TODO: Check that the alpha doesn't accumulate because it's drawn one time per player with units in the tile
		//Store the color values
		colorArray[count] = r; 
		colorArray[count+1] = g;
		colorArray[count+2] = b; 
		colorArray[count+3] = a; 
		
		colorArray[count+4] = r;
		colorArray[count+5] = g; 
		colorArray[count+6] = b; 
		colorArray[count+7] = a;
		
		colorArray[count+8] = r; 
		colorArray[count+9] = g; 
		colorArray[count+10] = b;
		colorArray[count+11] = a; 
					
		colorArray[count+12] = r; 
		colorArray[count+13] = g;
		colorArray[count+14] = b; 
		colorArray[count+15] = a; 
		
		colorArray[count+16] = r;
		colorArray[count+17] = g; 
		colorArray[count+18] = b;
		colorArray[count+19] = a; 
		
		colorArray[count+20] = r;
		colorArray[count+21] = g; 
		colorArray[count+22] = b;
		colorArray[count+23] = a; 
	}
	
	/*private void LoadPlayerColor(GL10 gl, Tile tile, int count, float[] colorArray)
	{
		float r = 0f, g = 0f, b = 0f;
		
		
		//calculate the intensity of the color
		for(int i=0; i < players.size(); i++){
			int colorIndex = players.elementAt(i).GetColorIndex();
			float intensityChange = tile.GetDensityFrom(i) * 0.25f / (Constants.TileWidth* Constants.TileWidth);
			switch(colorIndex){
				case 0: //Red
					r += intensityChange;
					break;
				case 1: //Green
					g += intensityChange;
					break;
				case 2: //Blue
					b += intensityChange;
					break;	
				case 3: //Yellow
					g += intensityChange;
					b += intensityChange;
					break;
				case 4: //Purple
					r += intensityChange;
					b += intensityChange;
					break;
				case 5: //Orange
					r += intensityChange;
					g += intensityChange;
					break;
				default:
					r += intensityChange; 
					g += intensityChange; 
					b += intensityChange;
			}		
		}
		//Add the base intensity if necessary
		r = (r > 0f) ? (0.75f - r) : 0f; 
		g = (g > 0f) ? (0.75f - g) : 0f;
		b = (b > 0f) ? (0.75f - b) : 0f;
		
		//Store the color values
		colorArray[count] = r; 
		colorArray[count+1] = g;
		colorArray[count+2] = b; 
		colorArray[count+3] = 1f; 
		
		colorArray[count+4] = r;
		colorArray[count+5] = g; 
		colorArray[count+6] = b; 
		colorArray[count+7] = 1f;
		
		colorArray[count+8] = r; 
		colorArray[count+9] = g; 
		colorArray[count+10] = b;
		colorArray[count+11] = 1f; 
					
		colorArray[count+12] = r; 
		colorArray[count+13] = g;
		colorArray[count+14] = b; 
		colorArray[count+15] = 1f; 
		
		colorArray[count+16] = r;
		colorArray[count+17] = g; 
		colorArray[count+18] = b;
		colorArray[count+19] = 1f; 
		
		colorArray[count+20] = r;
		colorArray[count+21] = g; 
		colorArray[count+22] = b;
		colorArray[count+23] = 1f; 
	}*/

	/*private void LoadPlayerColor(GL10 gl, Tile tile, int count, float[] colorArray)
	{
		float r = 0f, g = 0f, b = 0f, a=0f;
		
		//calculate the intensity of the color
		for(int i=0; i < players.size(); i++){
			int colorIndex = players.elementAt(i).GetColorIndex();
			float intensityChange = tile.GetDensityFrom(i) * 0.25f / (Constants.TileWidth* Constants.TileWidth);
			switch(colorIndex){
				case 0: //Red
					r += intensityChange;
					
					a += intensityChange;
					break;
				case 1: //Green
					g += intensityChange;
					
					a += intensityChange;
					break;
				case 2: //Blue
					b += intensityChange;
					
					a += intensityChange;
					break;	
				case 3: //Yellow
					g += intensityChange;
					b += intensityChange;
					
					a += intensityChange;
					break;
				case 4: //Purple
					r += intensityChange;
					b += intensityChange;
					
					a += intensityChange;
					break;
				case 5: //Orange
					r += intensityChange;
					g += intensityChange;
					
					a += intensityChange;
					break;
				default:
					r += intensityChange; 
					g += intensityChange; 
					b += intensityChange;
					
					a += intensityChange;
			}		
		}
		//Add the base intensity if necessary
		r = (r < 0f) ? (0.5f + r) : 0f; 
		g = (g < 0f) ? (0.5f + g) : 0f;
		b = (b < 0f) ? (0.5f + b) : 0f;
		a = (r+g+b > 0f) ? 0.75f + a : 0f;
		
		//TODO: Check that the alpha doesn't accumulate because it's drawn one time per player with units in the tile
		//Store the color values
		colorArray[count] = r; 
		colorArray[count+1] = g;
		colorArray[count+2] = b; 
		colorArray[count+3] = a; 
		
		colorArray[count+4] = r;
		colorArray[count+5] = g; 
		colorArray[count+6] = b; 
		colorArray[count+7] = a;
		
		colorArray[count+8] = r; 
		colorArray[count+9] = g; 
		colorArray[count+10] = b;
		colorArray[count+11] = a; 
					
		colorArray[count+12] = r; 
		colorArray[count+13] = g;
		colorArray[count+14] = b; 
		colorArray[count+15] = a; 
		
		colorArray[count+16] = r;
		colorArray[count+17] = g; 
		colorArray[count+18] = b;
		colorArray[count+19] = a; 
		
		colorArray[count+20] = r;
		colorArray[count+21] = g; 
		colorArray[count+22] = b;
		colorArray[count+23] = a; 
	}*/
	
	/*private void LoadPlayerColor(GL10 gl, Tile tile, int count, float[] colorArray)
	{
		float r = 0f, g = 0f, b = 0f;
		
		
		//calculate the intensity of the color
		for(int i=0; i < players.size(); i++){
			int colorIndex = players.elementAt(i).GetColorIndex();
			float intensityChange = tile.GetDensityFrom(i) * 0.25f / (Constants.TileWidth* Constants.TileWidth);
			switch(colorIndex){
				case 0: //Red
					r += intensityChange;
					break;
				case 1: //Green
					g += intensityChange;
					break;
				case 2: //Blue
					b += intensityChange;
					break;	
				case 3: //Yellow
					g += intensityChange;
					b += intensityChange;
					break;
				case 4: //Purple
					r += intensityChange;
					b += intensityChange;
					break;
				case 5: //Orange
					r += intensityChange;
					g += intensityChange;
					break;
				default:
					r += intensityChange; 
					g += intensityChange; 
					b += intensityChange;
			}		
		}
		//Add the base intensity if necessary
		r = (r > 0f) ? (0.5f + r) : 0f; 
		g = (g > 0f) ? (0.5f + g) : 0f;
		b = (b > 0f) ? (0.5f + b) : 0f;
		
		//Store the color values
		colorArray[count] = r; 
		colorArray[count+1] = g;
		colorArray[count+2] = b; 
		colorArray[count+3] = 1f; 
		
		colorArray[count+4] = r;
		colorArray[count+5] = g; 
		colorArray[count+6] = b; 
		colorArray[count+7] = 1f;
		
		colorArray[count+8] = r; 
		colorArray[count+9] = g; 
		colorArray[count+10] = b;
		colorArray[count+11] = 1f; 
					
		colorArray[count+12] = r; 
		colorArray[count+13] = g;
		colorArray[count+14] = b; 
		colorArray[count+15] = 1f; 
		
		colorArray[count+16] = r;
		colorArray[count+17] = g; 
		colorArray[count+18] = b;
		colorArray[count+19] = 1f; 
		
		colorArray[count+20] = r;
		colorArray[count+21] = g; 
		colorArray[count+22] = b;
		colorArray[count+23] = 1f; 
	}*/
	
	/**
	 * Loads the textures needed into Opengl
	 * @param gl Opengl context.
	 */
	private void SetTextures(GL10 gl)
	{	
		gl.glEnable(GL10.GL_DEPTH_TEST);
        
        //Enable the use of textures and set the texture 0 as the current texture
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		//Generate the texture and bind it
		int[] tmp_tex = new int[2];
		gl.glGenTextures(2, tmp_tex, 0); 
		this.mapTextureId = tmp_tex[0];
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mapTextureId);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, this.map.getBitmap(), 0);
		
		this.map.getBitmap().recycle();
		
		//Create the float buffers of the vertices, texture coordinates and normals
		float VertexMapArray[] = {Preferences.Get().mapWidth,Preferences.Get().mapHeight,1.0f,
				0f,Preferences.Get().mapHeight,1.0f,
				Preferences.Get().mapWidth,0f,1.0f,
				0f,0f,1.0f};
		Log.i("DagRenderer","Map width: " + Preferences.Get().mapWidth + " height: " + Preferences.Get().mapHeight);
		float textureArray[] = {1.0f,1.0f,0.0f,1.0f,1.0f,0.0f,0.0f,0.0f};
		
		this.vertexMapBuffer = makeFloatBuffer(VertexMapArray);
		this.textureMapBuffer = makeFloatBuffer(textureArray);
		
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
		
		
		//Generate the texture and bind it
		this.cursorTextureId = tmp_tex[1];
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.cursorTextureId);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, this.cursorBitmap, 0);
		
		this.cursorBitmap.recycle();
		
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
	 * Creates the tile map render from a list of tiles
	 * @param tilemap Vector of reference tiles.
	 */
	private void LoadTileMap(Vector<Tile> tilemap)
	{
		//Store the tile map and its dimensions in pixels
		int rowTiles = Preferences.Get().mapWidth/Constants.TileWidth;
		int columnTiles = Preferences.Get().mapHeight/Constants.TileWidth;
		
		//Initialize the vertex array and other auxiliar variables
		float[] floatArray= new float[rowTiles*columnTiles*6*3];        		
		Iterator<Tile> it = map.getTileMap().listIterator();
		Tile tile = null;
		tileMapBufferLength = 0;
		
		//Calculate the vertices of the tile map
		for(int j = 0; j < columnTiles; j++){
			for(int i = 0; i < rowTiles; i++){			
				tile = it.next();
				if(tile.GetMaxCapacity() > 0){
					floatArray[this.tileMapBufferLength] = i*Constants.TileWidth; 
					floatArray[this.tileMapBufferLength+1] = j*Constants.TileWidth;
					floatArray[this.tileMapBufferLength+2] = 0.0f; 
					
					floatArray[this.tileMapBufferLength+3] = i*Constants.TileWidth+Constants.TileWidth; 
					floatArray[this.tileMapBufferLength+4] = j*Constants.TileWidth;
					floatArray[this.tileMapBufferLength+5] = 0.0f; 
					
					floatArray[this.tileMapBufferLength+6] = i*Constants.TileWidth; 
					floatArray[this.tileMapBufferLength+7] = j*Constants.TileWidth+Constants.TileWidth;
					floatArray[this.tileMapBufferLength+8] = 0.0f; 
					
					floatArray[this.tileMapBufferLength+9] = i*Constants.TileWidth+Constants.TileWidth; 
					floatArray[this.tileMapBufferLength+10] = j*Constants.TileWidth;
					floatArray[this.tileMapBufferLength+11] = 0.0f; 
					
					floatArray[this.tileMapBufferLength+12] = i*Constants.TileWidth+Constants.TileWidth; 
					floatArray[this.tileMapBufferLength+13] = j*Constants.TileWidth+Constants.TileWidth;
					floatArray[this.tileMapBufferLength+14] = 0.0f; 
					
					floatArray[this.tileMapBufferLength+15] = i*Constants.TileWidth; 
					floatArray[this.tileMapBufferLength+16] = j*Constants.TileWidth+Constants.TileWidth;
					floatArray[this.tileMapBufferLength+17] = 0.0f; 
					
					this.tileMapBufferLength += 18;
				}
			}			
		}
		
		//store it in a float buffer
		this.vertexMapBuffer = makeFloatBuffer(floatArray);
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
	  // Log.i("World Coords", "-------------- ");
	   
	   // Initialize auxiliary variables.
	   Vec2 worldPos = new Vec2();
	   
	   // SCREEN height & width (ej: 320 x 480)
	   float screenW = cam.GetScreenWidth();
	   float screenH = cam.GetScreenHeight();
	   
	   //Camera.Get().Position().Print("World Coords", "Camera");
	   //touch.Print("World Coords", "Screen touch");
	   //Log.i("World Coords", "Screen: " + screenW + ", " + screenH);
	   //Log.i("World Coords", "World: " + Preferences.Get().mapWidth + ", " + Preferences.Get().mapHeight);
	   
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
	   //Print("Out ", outPoint);
	   
	   if (outPoint[3] == 0.0)
	   {
		   // Avoid /0 error.
		   Log.e("World coords", "Could not calculate world coordinates");
		   return worldPos;
	   }
	   
	   // Divide by the 3rd component to find out the real position.
	   worldPos.Set(outPoint[0] / outPoint[3], outPoint[1] / outPoint[3]);
	   
	   // Unnecesary, but here for log purposes.
	   //float worldZ = outPoint[2] / outPoint[3];
	   
	   //Log.i("World Coords", "Move to point: " + worldPos.X() + ", " + worldPos.Y() + ", " + worldZ);			   
	   
	   return worldPos;	   
   }

}