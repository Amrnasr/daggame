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
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.game.InputDevice.JoystickInputDevice;
import com.game.MessageHandler.MsgReceiver;
import com.game.PowerUp.PowerUp;

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
	 * Reference to the cursors
	 */
	private Vector<Cursor> cursorsRef;
	
	/**
	 * Map to be rendered
	 */
	private Map map;
	
	/**
	 * Combat bitmap for rendering
	 */
	private Bitmap combatBitmap;
	
	/**
	 * Cursor bitmap for rendering
	 */
	private Bitmap cursorBitmap;
	
	/**
	 * Cursor shadow bitmap for rendering
	 */
	private Bitmap cursorShadowBitmap;
	
	/**
	 * PowerUp bitmap for rendering
	 */
	private Bitmap powerUpBitmap;
	
	/**
	 * PowerUp shadow bitmap for rendering
	 */
	private Bitmap powerUpShadowBitmap;
	
	/**
	 * Joystick main bitmap for rendering
	 */
	private Bitmap joystickMainBitmap;
	
	/**
	 * Joystick small bitmap for rendering
	 */
	private Bitmap joystickSmallBitmap;
	
	/**
	 * Map vertex buffer
	 */
	private FloatBuffer vertexMapBuffer;
	
	/**
	 * Map texture coordinates buffer
	 */
	private FloatBuffer textureMapBuffer;
	
	/**
	 * Combat effects vertex buffer
	 */
	private FloatBuffer vertexCombatBuffer;
	
	/**
	 * Combat effects texture coordinates buffer array
	 */
	private FloatBuffer[] textureCombatBuffer;
	
	/**
	 * Id of the texture of the map
	 */
	private int mapTextureId;
	
	/**
	 * Id of the texture of the combat bitmap
	 */
	private int combatTextureId;
	
	/**
	 * Id of the texture of the cursor
	 */
	private int cursorTextureId;
	
	/**
	 * Id of the texture of the cursor's shadow
	 */
	private int cursorShadowTextureId;
	
	/**
	 * Id of the texture of the PowerUp
	 */
	private int powerUpTextureId;
	
	/**
	 * Id of the texture of the PowerUp shadow
	 */
	private int powerUpShadowTextureId;
	
	/**
	 * Id of the texture of the cursor
	 */
	private int joystickMainTextureId;
	
	/**
	 * Id of the texture of the cursor
	 */
	private int joystickSmallTextureId;
	
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
	
	/**
	 * Indicates whether to show the minimap or not
	 */
	private boolean showMinimap;
	
	/**
	 * List of PowerUps to draw
	 */
	private Vector<PowerUp> powerUps;
	
	/**
	 * List of JoystickInputDevice in the renderer
	 */
	private Vector<JoystickInputDevice> joysticks;
	
	/**
	 * Initializes the renderer and sets the handler callbacks.
	 */
	public DagRenderer()
	{
		super();
		Log.i("DagRenderer", "Started constructor");
		
		this.map = null;
		this.cursorBitmap = null;
		this.cursorShadowBitmap = null;
		this.powerUpBitmap = null;
		this.powerUpShadowBitmap = null;
		this.joystickMainBitmap = null;
		this.joystickSmallBitmap = null;
		this.cursorsRef = new Vector<Cursor>();
		this.powerUps = new Vector<PowerUp>();
		this.joysticks = new Vector<JoystickInputDevice>();
		this.players = null;
		this.texReady = false;	
		this.lastWidth = 0;
		this.lastHeight = 0;
	    this.showMinimap = false;
	    
	    this.textureCombatBuffer = new FloatBuffer[Constants.CombatEffectImgNum];
		
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
	        	// Asked to draw a new PowerUp
	        	else if(msg.what == MsgType.DISPLAY_NEW_POWERUP.ordinal())
	        	{
	        		//Log.i("DagRenderer", "Add new PowerUp!");
	        		powerUps.add((PowerUp)msg.obj);
	        	}
	        	// Asked to stop drawing a specific PowerUp
	        	else if(msg.what == MsgType.STOP_DISPLAYING_POWERUP.ordinal())
	        	{
	        		Log.i("DagRenderer", "Remove powerup!");
	        		powerUps.remove((PowerUp)msg.obj);
	        	}
	        	else if(msg.what == MsgType.PAUSE_GAME.ordinal())
	        	{
	        		if(state == RenderState.RENDERING)
	        		{
	        			state = RenderState.PAUSED;
	        		}
	        	}
	        	else if(msg.what == MsgType.UNPAUSE_GAME.ordinal())
	        	{
	        		if(state == RenderState.PAUSED)
	        		{
	        			state = RenderState.RENDERING;
	        		}
	        	}
	        	else if(msg.what == MsgType.RENDERER_REQUEST_SURFACE_UPDATED.ordinal())
	        	{
	        		surfaceUpdatePending = true;
	        		minZ = Camera.Get().GetMinZ() - camZOffset;
	        		maxZ = 2*Camera.Get().GetMaxZ() + camZOffset;
	        		Log.i("DagRenderer", "zMin: " + minZ + " zMax: " + maxZ);
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

		// Store the combat bitmap
		this.combatBitmap = initData.GetCombatBitmap();
		
		// Store the cursor bitmap
		this.cursorBitmap = initData.GetCursorBitmap();
		this.cursorShadowBitmap = initData.GetCursorShadowBitmap();
		
		// Store the PowerUp bitmap
		this.powerUpBitmap = initData.GetPowerUpBitmap();
		this.powerUpShadowBitmap = initData.GetPowerUpShadowBitmap();
		
		// Store the joystick
		this.joystickMainBitmap = initData.GetJoystickMainBitmap();
		this.joystickSmallBitmap = initData.GetJoystickSmallBitmap();
		
		showMinimap = (!Preferences.Get().multiplayerGame && Preferences.Get().singleShowMinimap) || (Preferences.Get().multiplayerGame && Preferences.Get().multiShowMinimap);

		//Store the players vector
		this.players = initData.GetPlayers();
		
		// The camZOffset is because the min/max z of the camera is the limits we can see
		// at, so we need to give ourselves a little margin to see just at the limits, where our stuff is.
		this.minZ = Camera.Get().GetMinZ() - camZOffset;
		this.maxZ = 2*Camera.Get().GetMaxZ() + camZOffset;
		
		// Z's changed, so tell the update to update the surface perspective.
		this.surfaceUpdatePending = true;		
		
		// Check to see if we have to draw Joysticks
		for (Player player : players) 
		{
			if(player.GetInputDevice() instanceof JoystickInputDevice)
			{
				// If so, get the references to the Joystick
				joysticks.add((JoystickInputDevice)player.GetInputDevice());
			}
		}
	    
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
		gl.glEnable (GL10.GL_DEPTH_TEST);
		
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
		// Logic not dependent on game state
		MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.UPDATE_RENDER_PROFILER);
		
		// Update the view if needed.
		if(this.surfaceUpdatePending)
		{
			SurfaceShapeUpdate(gl);
			this.surfaceUpdatePending = false;
		}
		
		// If the bitmap hasn't been received don't do anything
		if(this.state != RenderState.RENDERING) return;	
		
		//Load the texture if it hasn't been loaded and it's necessary
		if(!Constants.DebugMode && !this.texReady) 
		{
			SetTextures(gl);
		}
		
		/// RENDERING
		
		// Initialize the buffers and matrices		
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// Camera transform
		gl.glTranslatef(-Camera.Get().X(),-Camera.Get().Y(),-Camera.Get().Z());	
		
		// Draw non-textured elements
		gl.glDisable(GL10.GL_TEXTURE_2D);
		DrawBackgroundRect(gl);
		
		synchronized (map) 
		{
			DrawMap(gl);
			map.notifyAll();
		}
		
		// Draw textured elements
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		// Draw the corresponding data, debug or not.
		
		if(!Constants.DebugMode )
		{
			//Log.i("DagRenderer", "Drawing map");
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			DrawTexturedMap(gl);
		}
		else
		{
			// Draw tile map	
			// TODO: @deprecated code here, fix or remove
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexMapBuffer);		
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, this.tileMapBufferLength/3);
		}	
		
		DrawCombatEffects(gl);
		
		DrawPowerUps(gl);
		
		DrawCursors(gl);
		
		
		
		
		getCurrentProjection(gl);
		getCurrentModelView(gl);
		
		
		DrawJoyStick(gl);
		/*
		if(showMinimap)
		{
			//DrawMinimap(gl);
		}
		*/
    }
	
	/**
	 * Draws the textured map image.
	 * @param gl Opengl context
	 */
	private void DrawTexturedMap(GL10 gl)
	{	
		//gl.glTranslatef(10f,0f,0f);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mapTextureId);
		
		//Set the vertices
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexMapBuffer);
		
		//Set the texture coordinates
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.textureMapBuffer);
		
		//Draw the bitmap
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		//gl.glTranslatef(-10f,0f,0f);
	}
	
	private void DrawCombatEffects(GL10 gl){
		Vector<Vec3> combatPosVector = map.getCombatPosVector();
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.combatTextureId);
		
		for(int i = 0; i < combatPosVector.size(); i++)
		{
			Vec3 curCombatPos = combatPosVector.elementAt(i);
			if(curCombatPos.Z() >= Constants.CombatEffectImgNum) continue;
				
			gl.glTranslatef((float) curCombatPos.X(), (float) curCombatPos.Y() - Constants.TileWidth, 0.3f);
			
			//Set the vertices
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexCombatBuffer);
			
			//Set the texture coordinates
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.textureCombatBuffer[(int) curCombatPos.Z()]);
			
			//Draw the bitmap
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			gl.glTranslatef((float) -curCombatPos.X(), (float) -curCombatPos.Y() + Constants.TileWidth, -0.3f);
			curCombatPos.SetZ(curCombatPos.Z()+1);
		}
		
	}
	
	/**
	 * Draw the player cursors.
	 * @param gl is the opengl context
	 */
	private void DrawCursors(GL10 gl)
	{			
		
		Cursor.AlphaRenderUpdate();
		for(int i = 0; i < this.cursorsRef.size(); i++ )
		{
			Cursor cursor = this.cursorsRef.elementAt(i);
			
			float oldX =(float)cursor.GetPosition().X();
			float oldY = (float)cursor.GetPosition().Y();
			
			cursor.RenderUpdate();
			
			float x =(float)cursor.GetPosition().X();
			float y = (float)cursor.GetPosition().Y();
			
			boolean rotating = cursor.Rotating();
			
				
			gl.glTranslatef(x,y,1);
			if(rotating)
			{
				gl.glRotatef(cursor.GetRotationAngle(), 0, 0, 1);
			}

			// --------
			//Set the vertices
			float shadowX = x-oldX;
			float shadowY = y-oldY;
			gl.glTranslatef(shadowX,shadowY,-0.2f);
			
			gl.glColor4f(
					Constants.CursorColorIntensity, 
					Constants.CursorColorIntensity, 
					Constants.CursorColorIntensity, 
					Cursor.GetAlpha());
			
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cursor.GetBuffer());
			
			//Set the texture coordinates
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureMapBuffer);
			
			//Set texture
			gl.glBindTexture(GL10.GL_TEXTURE_2D, cursorShadowTextureId);
			
			// Draw
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			gl.glTranslatef(-shadowX,-shadowY,0.2f);
			
			//---------
			SetCursorColor(gl,i);
			
			// Set the texture
			gl.glBindTexture(GL10.GL_TEXTURE_2D, cursorTextureId);
			
			// Draw
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			//----------
			
			//----------
			if(rotating)
			{
				gl.glRotatef(-cursor.GetRotationAngle(), 0, 0, 1);
			}
			gl.glTranslatef(-x,-y,-1);
		}
	}
	
	/**
	 * Draw the PowerUp images
	 * @param gl The openGL context
	 */
	private void DrawPowerUps(GL10 gl)
	{
		PowerUp.AlphaRenderUpdate();
		for(int i = 0; i < this.powerUps.size(); i++ )
		{			
			PowerUp powerUp = this.powerUps.elementAt(i);
			//powerUp.RenderUpdate();
			
			float x =(float)powerUp.Pos().X();
			float y = (float)powerUp.Pos().Y();
			
			gl.glTranslatef(x,y,0.5f);

			gl.glColor4f(
					Constants.CursorColorIntensity, 
					Constants.CursorColorIntensity, 
					Constants.CursorColorIntensity, 
					PowerUp.GetAlpha());

			
			gl.glTranslatef(0, 0, -0.2f);
			//Set the vertices
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, powerUp.GetBuffer());
			
			//Set the texture coordinates
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureMapBuffer);
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, powerUpShadowTextureId);
			
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			gl.glTranslatef(0, 0, 0.2f);
			
			// ------------
			gl.glColor4f(1, 1, 1, 1);
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, powerUpTextureId);
			
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			
			gl.glTranslatef(-x,-y,-0.5f);
		}
	}
	
	/**
	 * Draws the density polygons of the players.
	 * @param gl Opengl context
	 * @deprecated
	 */
	private void DrawPlayers(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		// Draw tile map			
		for(int i = 0; i < players.size(); i++){		
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.playersVertexBuffer[i]);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, this.playersColorBuffer[i]);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, this.playersBufferLength[i]/3);		
		}
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
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
	 * Draws the density map
	 * @param gl Opengl context to draw in.
	 */
	private void DrawMap(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, map.GetVertexBuffer());
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, map.GetColorBuffer());
		
		gl.glDrawElements(GL10.GL_TRIANGLES, map.GetIndexSize(),
                GL10.GL_UNSIGNED_SHORT, map.GetIndexBuffer());
		
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}
	
	private void DrawJoyStick(GL10 gl)
	{
		//Not call if there are no joysticks
		if(joysticks.size() == 0)
		{
			return;
		}
		
		//Change to orthogonal projection
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,this.lastWidth,this.lastHeight);

		//gl.glOrthof(-this.lastWidth/2f, this.lastWidth/2f, -this.lastHeight/2f, this.lastHeight/2f, this.minZ, this.maxZ);
		gl.glOrthof(0, this.lastWidth, 0, this.lastHeight, this.minZ, this.maxZ);
		
		//Log.i("DagRenderer", "lastWidth:" + lastWidth + " lastHeight: " + lastHeight);
		// Draw the joystick
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		for(int i = 0; i < joysticks.size(); i++)
		{
			JoystickInputDevice js = joysticks.elementAt(i);
			float mainX = (float)js.GetMainCirclePos().X();
			float mainY = (float)js.GetMainCirclePos().Y();
			float mainZ = -this.minZ-2f;
			
			float smallX = (float)js.GetSmallCirclePos().X() - mainX;
			float smallY = (float)js.GetSmallCirclePos().Y() - mainY;
			float smallZ = 1;
			
			gl.glTranslatef(mainX,mainY,mainZ);

			//---------------- Main texture
			gl.glColor4f(1,1,1,1);
			
			
			//Set the vertices
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, JoystickInputDevice.GetMainTextureBuffer());
			
			//Set the texture coordinates
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureMapBuffer);
			
			// Set the texture
			gl.glBindTexture(GL10.GL_TEXTURE_2D, joystickMainTextureId);
			
			// Draw
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			
			//-------------------- Small texture
			gl.glTranslatef(smallX, smallY, smallZ);
			//Set the vertices
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, JoystickInputDevice.GetSmallTextureBuffer());
			
			// Set the texture
			gl.glBindTexture(GL10.GL_TEXTURE_2D, joystickSmallTextureId);
			
			// Draw
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			gl.glTranslatef(-smallX, -smallY, -smallZ);
			
			
			
			gl.glTranslatef(-mainX,-mainY,-mainZ);
		}
		
		//Return to a perspective projection
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,this.lastWidth,this.lastHeight);

		GLU.gluPerspective(gl, 45.0f, ((float)this.lastWidth)/this.lastHeight, this.minZ, this.maxZ);
	}
	
	
	
	/**
	 * Draws a background map-sized rectangle
	 * @param gl OpenGL context
	 */
	private void DrawBackgroundRect(GL10 gl)
	{
		gl.glTranslatef(0f,0f,-3f);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexMapBuffer);		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glTranslatef(0f,0f,3f);
	}
	
	/**
	 * Sets the color used for rendering the cursor with the given player ID
	 * @param gl Opengl context
	 * @param playerID  The player ID
	 */
	private void SetCursorColor(GL10 gl,int playerID)
	{
		int colorIndex = players.elementAt(playerID).GetColorIndex();
		switch(colorIndex)
		{
			case 0: //Brown
				gl.glColor4f(0.65f*Constants.CursorColorIntensity, 0.32f*Constants.CursorColorIntensity, 0.13f*Constants.CursorColorIntensity, 0.8f);
				break;
			case 1: //Green
				gl.glColor4f(0f, Constants.CursorColorIntensity, 0f, 0.8f);
				break;
			case 2: //Blue
				gl.glColor4f(0f, 0f, Constants.CursorColorIntensity, 0.8f);
				break;	
			case 3: //Cyan
				gl.glColor4f(0f, Constants.CursorColorIntensity, Constants.CursorColorIntensity, 0.8f);
				break;
			case 4: //Purple
				gl.glColor4f(Constants.CursorColorIntensity, 0f, Constants.CursorColorIntensity, 0.8f);
				break;
			case 5: //Yellow
				gl.glColor4f(Constants.CursorColorIntensity, Constants.CursorColorIntensity, 0f, 0.8f);
				break;
		}
		
	}
	
	
	
	// Get a new texture id:
	private static int NewTextureID(GL10 gl) 
	{
	    int[] temp = new int[1];
	    gl.glGenTextures(1, temp, 0);
	    return temp[0];        
	}
	
	private int LoadTexture(GL10 gl, Bitmap bitmap)
	{
		int id = NewTextureID(gl);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
	    
	    // Set all of our texture parameters:
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
	    //gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,	GL10.GL_MODULATE); 
	    
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	    
	    return id;
	}
	
	/**
	 * Loads the textures needed into Opengl
	 * @param gl Opengl context.
	 */
	private void SetTextures(GL10 gl)
	{			
		this.mapTextureId = LoadTexture(gl, this.map.getBitmap());
		this.combatTextureId = LoadTexture(gl, this.combatBitmap);
		this.cursorTextureId = LoadTexture(gl, this.cursorBitmap);
		this.cursorShadowTextureId = LoadTexture(gl, this.cursorShadowBitmap);
		this.powerUpTextureId = LoadTexture(gl, this.powerUpBitmap);
		this.powerUpShadowTextureId = LoadTexture(gl, this.powerUpShadowBitmap);
		this.joystickMainTextureId = LoadTexture(gl, this.joystickMainBitmap);
		this.joystickSmallTextureId = LoadTexture(gl, this.joystickSmallBitmap);
		
		
		Log.i("DagRenderer", "Cursor: " + this.cursorBitmap.getWidth() + " Map: " + this.map.getBitmap().getWidth());
		
		//Log.i("DagRenderer", "DEBUG!! Map: " + this.mapTextureId +" Cursor: " + this.cursorTextureId + " PowerUp: " + this.powerUpTextureId);
		
		/*
		//Generate the texture vector
		int[] tmp_tex = new int[3];
		gl.glGenTextures(3, tmp_tex, 0); 
		
		/// MAP
		this.mapTextureId = tmp_tex[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.mapTextureId);
		
		//Set the texture parameters
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE); 
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,	GL10.GL_MODULATE); 
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, this.map.getBitmap(), 0);
		
		this.map.getBitmap().recycle();
		
		/// CURSOR
		this.cursorTextureId = tmp_tex[1];		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.cursorTextureId);
		
		//Set the texture parameters
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE); 
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,	GL10.GL_MODULATE); 
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, this.cursorBitmap, 0);
		this.cursorBitmap.recycle();
		
		/// POWERUP
		this.powerUpTextureId = tmp_tex[2];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.powerUpTextureId);
		
		//Set the texture parameters
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE); 
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,	GL10.GL_MODULATE); 

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, this.powerUpBitmap, 0);
		this.powerUpBitmap.recycle();
		*/
		
		//Set the rendering parameters
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_FLAT);
		
		//Create the float buffers of the vertices, texture coordinates and normals
		float VertexMapArray[] = {Preferences.Get().mapWidth,Preferences.Get().mapHeight,1.0f,
				0f,Preferences.Get().mapHeight,1.0f,
				Preferences.Get().mapWidth,0f,1.0f,
				0f,0f,1.0f};
		Log.i("DagRenderer","Map width: " + Preferences.Get().mapWidth + " height: " + Preferences.Get().mapHeight);
		float textureArray[] = {1.0f,0.0f,0.0f,0.0f,1.0f,1.0f,0.0f,1.0f};
		float VertexCombatArray[] = {Constants.TileWidth,Constants.TileWidth,1.0f,
				0f,Constants.TileWidth,1.0f,
				Constants.TileWidth,0f,1.0f,
				0f,0f,1.0f};
		
		//TODO: make this work for any number of images
		float textureCombatArray1[] = {0.25f,0.0f,0.0f,0.0f,0.25f,1.0f,0.0f,1.0f};
		float textureCombatArray2[] = {0.5f,0.0f,0.25f,0.0f,0.5f,1.0f,0.25f,1.0f};
		float textureCombatArray3[] = {0.75f,0.0f,0.5f,0.0f,0.75f,1.0f,0.5f,1.0f};
		float textureCombatArray4[] = {1.0f,0.0f,0.75f,0.0f,1.0f,1.0f,0.75f,1.0f};
		
		this.vertexMapBuffer = makeFloatBuffer(VertexMapArray);
		this.textureMapBuffer = makeFloatBuffer(textureArray);
		this.vertexCombatBuffer = makeFloatBuffer(VertexCombatArray);
		this.textureCombatBuffer[0]= makeFloatBuffer(textureCombatArray1);
		this.textureCombatBuffer[1]= makeFloatBuffer(textureCombatArray2);
		this.textureCombatBuffer[2]= makeFloatBuffer(textureCombatArray3);
		this.textureCombatBuffer[3]= makeFloatBuffer(textureCombatArray4);
		
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
		
		//Initialize the vertex array and other auxiliary variables
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
	 * @return the related FloatBuffer.
	 */
	public static FloatBuffer makeFloatBuffer(float[] arr)
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
		getMatrix(gl, GL10.GL_MODELVIEW, lastModelViewMat);
   }

   /**
    * Record the current projection matrix state. Has the side effect of
    * setting the current matrix state to GL_PROJECTION
    * @param gl context
    */
   public void getCurrentProjection(GL10 gl) 
   {
       getMatrix(gl, GL10.GL_PROJECTION, lastProjectionMat);
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
    * Calculates the transform from screen coordinate system to world coordinate system coordinates 
    * for a specific point, given a camera position.
    * 
    * @param touch Vec2 point of screen touch, the actual position on physical screen (ej: 160, 240)
    * @param cam camera object with x,y,z of the camera and screenWidth and screenHeight of the device.
    * @return position in WCS.
    */
   public Vec2 GetWorldCoords( Vec2 touch, Camera cam)
   {
	   // Initialize auxiliary variables.
	   Vec2 worldPos = new Vec2();
	   
	   // SCREEN height & width (ej: 320 x 480)
	   float screenW = cam.GetScreenWidth();
	   float screenH = cam.GetScreenHeight();
	   
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
	   
	   /* Obtain the transform matrix and then the inverse. */
	   Matrix.multiplyMM(transformMatrix, 0, lastProjectionMat, 0, lastModelViewMat, 0);
	   Matrix.invertM(invertedMatrix, 0, transformMatrix, 0);	   

	   /* Apply the inverse to the point in clip space */
	   Matrix.multiplyMV(outPoint, 0, invertedMatrix, 0, normalizedInPoint, 0);
	   
	   if (outPoint[3] == 0.0)
	   {
		   // Avoid /0 error.
		   Log.e("World coords", "Could not calculate world coordinates");
		   return worldPos;
	   }
	   
	   // Divide by the 3rd component to find out the real position.
	   worldPos.Set(outPoint[0] / outPoint[3], outPoint[1] / outPoint[3]);
	   
	   return worldPos;	   
   }
}

/**
 * Draw the player cursors.
 * @param gl is the opengl context
 
private void DrawCursors(GL10 gl)
{			
	gl.glBindTexture(GL10.GL_TEXTURE_2D, cursorTextureId);
	for(int i = 0; i < this.cursorsRef.size(); i++ )
	{
		Cursor cursor = this.cursorsRef.elementAt(i);
		
		cursor.RenderUpdate();
		boolean rotating = cursor.Rotating();
		
		float x =(float)cursor.GetPosition().X();
		float y = (float)cursor.GetPosition().Y();
		
			
		gl.glTranslatef(x,y,2);
		if(rotating)
		{
			gl.glRotatef(cursor.GetRotationAngle(), 0, 0, 1);
		}

		SetCursorColor(gl,i);
		
		//Set the vertices
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cursor.GetBuffer());
		
		//Set the texture coordinates
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureMapBuffer);
		
		// Draw
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		if(rotating)
		{
			gl.glRotatef(-cursor.GetRotationAngle(), 0, 0, 1);
		}
		gl.glTranslatef(-x,-y,-2);
	}
}

private void DrawCursorShadows(GL10 gl)
{			
	//gl.glBindTexture(GL10.GL_TEXTURE_2D, cursorTextureId);
	gl.glBindTexture(GL10.GL_TEXTURE_2D, cursorShadowTextureId);
	for(int i = 0; i < this.cursorsRef.size(); i++ )
	{
		Cursor cursor = this.cursorsRef.elementAt(i);
		
		boolean rotating = cursor.Rotating();
		
		float x =(float)cursor.GetPosition().X();
		float y = (float)cursor.GetPosition().Y();
		
		gl.glTranslatef(x,y,1);
		if(rotating)
		{
			gl.glRotatef(cursor.GetRotationAngle(), 0, 0, 1);
		}
		
		
		//Set the vertices
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cursor.GetBuffer());
		
		//Set the texture coordinates
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureMapBuffer);
		
		// Draw
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		
		if(rotating)
		{
			gl.glRotatef(-cursor.GetRotationAngle(), 0, 0, 1);
		}
		gl.glTranslatef(-x,-y,-1);
	}
}

/**
	 * Creates the players vertex buffer
	 * @deprecated
	 
	private void LoadPlayers(GL10 gl){
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
	 * Creates the color array used for rendering the players armies
	 * @param gl Opengl context
	 * @param tile The tile to be rendered
	 * @param count Initial position to store values in the array
	 * @param colorArray  The array to store the color values
	 * @deprecated
	 
	private void LoadPlayerColor(GL10 gl, Tile tile, int count, float[] colorArray)
	{
		float r = 0f, g = 0f, b = 0f, a=0f;
		
		int playersCount = 0;
		int playerWithDensity = -1;
		int totalDensity = 0;
		//calculate the intensity of the color
		for(int i=0; i < players.size(); i++){
			if(tile.GetDensityFrom(i) > 0){
				playersCount++;
				totalDensity += tile.GetDensityFrom(i);
				playerWithDensity = i;
			}
		}
		
		if(playersCount > 1){
			r += 0.25f;
			
			a += 0.25f;
		}
		else if(playersCount == 1){
			int colorIndex = players.elementAt(playerWithDensity).GetColorIndex();
			float intensityChange = tile.GetDensityFrom(playerWithDensity) * 0.25f / tile.GetMaxCapacity();
			if(intensityChange > 0f){
				switch(colorIndex){
					case 0: //Brown
						r += intensityChange + 0.1f;
						g += intensityChange + 0.43f;
						b += intensityChange + 0.62f;
						
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
					case 3: //Cyan
						g += intensityChange;
						b += intensityChange;
						
						a += intensityChange;
						break;
					case 4: //Purple
						r += intensityChange;
						b += intensityChange;
						
						a += intensityChange;
						break;
					case 5: //Yellow
						r += intensityChange;
						g += intensityChange;
						
						a += intensityChange;
						break;
				}		
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
*/
