package com.game;


import java.util.Vector;

import com.game.MessageHandler.MsgReceiver;

import android.util.Log;

/**
 * Not sure what I'm doing here, camera transformations are like multithreading, that is
 * NOT my forte xD But I pulled off multithreading, so here's to a little hope this works 
 * as well.
 * 
 * The camera is a singleton for the LOGIC thread, keeps the position of the OGL camera, 
 * moves it in 3D space as required, and can transform screen to map coordinates.
 * 
 * It's a singleton because the data here is needed and modified in quite a few different places,
 * and honestly everyone has a right to know where the hell the camera is. It is NOT so the render
 * thread can access directly.
 * 
 * It's in the Logic thread because...
 * The render thread is sent a message if the camera changes.
 * The camera should not change much, just when the player cursors go out of it's field of view
 * or they are close enough to zoom in on them. That's why we can afford to message each position 
 * change. 
 * This also allows us to keep things consistent by doing logic calculations (where and when to move)
 * in the logic thread, while the render thread only get's told where to draw.
 * 
 * TODO: Camera is only gonna work correctly for maps where width > height. Fix this by using max/min(w,h)
 * where appropriate.
 * 
 * @author Ying
 *
 */
public class Camera 
{
	/**
	 * Static singleton instance
	 */
	private static Camera instance = new Camera();
	
	/**
	 * Position in ogl coords of the camera
	 */
	private Vec3 pos;
	
	/**
	 * Screen viewport height
	 */
	private int screenH;
	
	/**
	 * Screen viewport width
	 */
	private int screenW;
	
	/**
	 * Minimum camera z
	 */
	private int minZ;
	
	/**
	 * Maximum camera z
	 */
	private int maxZ;
	
	/**
	 * Minimum ratio for map/screen
	 */
	private float minRatio;
	
	/**
	 * Maximum ratio for map/screen
	 */
	private float maxRatio;
	
	/**
	 * Distance the camera must translate through if asked to move.
	 * Used for interpolation
	 */
	private double distance;
	
	/**
	 * Unitary direction vector for the camera if asked to move.
	 * Used for interpolation
	 */
	private Vec3 direction;
	
	/**
	 * Camera movement speed.
	 */
	private int speed;
	
	/**
	 * Speed constant.
	 */
	private static final int NORMAL_SPEED = 5;
	
	/**
	 * Distance to the border of the screen allowed for the cursors
	 * If the cursor(s) is closer to the edge of the screen than this, we move the camera.
	 */
	private int borderMargin;
	
	/**
	 * The width and height of the last camera viewport
	 */
	private Vec2 lastCameraSize;
	
	/**
	 * Prevents the instantiation of an object of the Camera class
	 */
	protected Camera() 
	{
		pos = new Vec3();

		screenH = 0;
		screenW = 0;
		
		minZ = 10;
		maxZ = 20;
		
		minRatio = 1;
		maxRatio = 0;
		
		direction = new Vec3();
		distance = 0;
		speed = NORMAL_SPEED;
		
		lastCameraSize = new Vec2();
	}
	
	/**
	 * Gets the singleton instance of the camera. Creates the camera if it's
	 * the first time it's called.
	 * 
	 * TODO: Check speed hit for having a sync
	 * 
	 * @return the instance of the camera
	 */
	public static synchronized Camera Get()
	{
		return instance;
	}
	
	/**
	 * It updates the camera. If necessary it interpolates it's movement in a straight line
	 * in the this.direction for this.distance.
	 */
	public void Update()
	{
		if(HasToMove())
		{
			double increment = Math.min(this.distance, this.speed);				
			this.pos.Offset(this.direction.X()*increment, this.direction.Y()*increment, this.direction.Z()*increment);
			
			this.distance -= increment;
		}
	}
	
	/**
	 * Initializes the screen size values
	 * @param w is the width of the screen
	 * @param h is the height of the screen
	 */
	public void SetScreenSize(int w, int h)
	{
		this.screenH = h;
		this.screenW = w;
		
		this.borderMargin = screenW / 5;
	}
	
	public void SetCameraZLimits()
	{
		this.minZ = 2* ((this.screenW * Preferences.Get().mapHeight)/this.screenH);
		this.maxZ = 2* Preferences.Get().mapWidth;		
		
		MessageHandler.Get().Send(MsgReceiver.RENDERER, MsgType.RENDERER_REQUEST_SURFACE_UPDATED);
	}
	
	/**
	 * Zooms the camera so all players are on screen.
	 * @param players
	 */
	public void ZoomOnPlayers(Vector<Player> players)
	{
		//Log.i("Camera", "Zoom on players -------------- ");
		
		if(players == null)
		{
			Log.e("Camera", "Zoom on all players: Players null");
			return;
		}
		// To make sure all player cursors are on screen
		float maxX, maxY, minX, minY;
		maxX = maxY = 0;
		minX = minY = Preferences.Get().mapWidth;
		
		// To calculate the center point of all players
		float centerX = 0, centerY = 0;
		int cursorCount = 0;
		
		for(int i = 0; i < players.size(); i++)
		{
			Player player = players.elementAt(i);
			if(player.IsHuman())
			{
				Cursor cursor = player.GetCursor();
				if(cursor == null)
				{
					Log.e("Camera", "Cursor in Zoom == null");
					return;
				}
					
				maxX = (float) Math.max(maxX, cursor.GetPosition().X());
				maxY = (float) Math.max(maxY, cursor.GetPosition().Y());
				minX = (float) Math.min(minX, cursor.GetPosition().X());
				minY = (float) Math.min(minY, cursor.GetPosition().Y());
				
				
				centerX += cursor.GetPosition().X();
				centerY += cursor.GetPosition().Y();
				cursorCount += 1;
			}
		}
		
		//Log.i("Camera", "3 - Original bounding box: (" + minX + ", " + minY + "),  (" + maxX + ", " + maxY + ")");
		
		// Give padding to multiplayer
		
		if(cursorCount > 1 && Preferences.Get().multiplayerGame)
		{
			minX -= borderMargin;
			maxX += borderMargin;
			minY -= borderMargin;
			maxY += borderMargin;
			
			Log.i("Camera", "4 - Extended bounding box: (" + minX + ", " + minY + "),  (" + maxX + ", " + maxY + ")");
		}
		
		
		if(cursorCount == 0)
		{
			// No human players, just watch the whole damn thing
			this.pos.Set(0, 0, this.maxZ);
		}
		else if (CursorsCloseToBorder(minX, maxX, minY, maxY))
		{
			//Log.i("Camera", "5 - Close to border!");
			
			// Point to go
			Vec3 destination = new Vec3();
			
			// Center position of all the cursors that must be shown on screen
			centerX /= cursorCount;
			centerY /= cursorCount;
			
			// Make it so the camera viewport is at least as big as the physical screen
			int xWidth = (int) (maxX - minX);
			int xHeight = (int) (maxY - minY);
			
			xHeight = Math.max(xHeight, Preferences.Get().mapHeight);
			xWidth = Math.max(xWidth, (Preferences.Get().mapHeight * this.screenW)/this.screenH);

			this.lastCameraSize.Set(xWidth, xHeight);
			
			//Log.i("Camera", "6 - Viewport: " + xWidth  + ", " + xHeight);
			//Log.i("Camera", "7 - Center: " + centerX  + ", " + centerY);
			
			// Set X and y
			destination.SetX(centerX);
			destination.SetY(centerY);
			
			// Set Z  (xDDD)			
			xWidth = Math.max(1, xWidth); // To avoid /0 errors while loading
			
			// Get the ratio map/screen
			float ratio = (float)((float)(xWidth) / (float)(this.screenW)); 
			//Log.i("Camera", "Ratio: " + ratio);
			
			this.minRatio = (float)((float)((float)((float)this.screenW/(float)this.screenH)*(float)Preferences.Get().mapHeight) / (float)(screenW));
			this.maxRatio = (float)((float)(Preferences.Get().mapWidth) / (float)(screenW));
			ratio = ratio - this.minRatio;
			ratio = Math.max(0f, ratio);
			float ratioRange = this.maxRatio - this.minRatio;
			//Log.i("Camera", "Ratio: [" + this.minRatio + ", " + this.maxRatio + "], range: " + ratioRange);
			
			
			ratio = ratio / ratioRange; // % of the total ratio range
			//Log.i("Camera", "Ratio %:" + ratio);
			
			//this.maxZ = 2*Preferences.Get().mapWidth;			
			float zRange = this.maxZ - this.minZ;
			//Log.i("Camera", "Z: [" + this.minZ + ", " + this.maxZ + "] range: " + zRange );
			
			// Now we've got the % of ratio, just apply it to the allowed Z interval.
			destination.SetZ(this.minZ + (zRange * ratio));
			//destination.Print("Camera", "Destination:");
			
			//destination.Print("Camera", "8 - Destination");
			
			// Once the destination point is calculated, request to move there.
			MoveTo(destination);			
		}
	}
	
	/**
	 * Checks to see if the bounding box defined by it's 4 sides
	 * is near to the edge of the screen
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @return True if is, false if it isn't.
	 */
	private boolean CursorsCloseToBorder(float minX, float maxX, float minY, float maxY)
	{
		if(minX < pos.X() + borderMargin - lastCameraSize.X()/2)
		{
			return true;
		}
		if( maxX > pos.X() + lastCameraSize.X()/2 - borderMargin)
		{
			return true;
		}
		if(minY < pos.Y() + borderMargin - lastCameraSize.Y()/2)
		{
			return true;
		}
		if(maxY > pos.Y() + lastCameraSize.Y()/2 - borderMargin)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Called to request the cursor to move to a specified position, in a straight line, to the max of it's speed.
	 * @param destination is the point we want the Cursor to translate to
	 */
	private void MoveTo(Vec3 destination)
	{
		if(! this.pos.Equals(destination))
		{
			// move in that direction	
			MoveInDirection(this.pos.GetVectorTo(destination));
		}
	}
	
	/**
	 * Tells the cursor to move in a direction.
	 * @param direction Non-unitary vector of the direction we want to move. 
	 * The length of the vector is taken to be the length of the path we want
	 * to move along. 
	 */
	private void MoveInDirection(Vec3 direction)
	{
		this.distance = direction.Length();
		
		this.direction  = direction;
		this.direction.Normalize();		
	}
	
	/**
	 * Checks if the Cursor has to move
	 * 
	 * @return True if it has to move, false if it doesn't.
	 */
	private boolean HasToMove()
	{
		if(this.direction == null)
		{
			return false;
		}
		if(this.distance == 0)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the screen width
	 * @return screen width
	 */
	public int GetScreenWidth() { return this.screenW; }
	
	/**
	 * Gets the screen height
	 * @return screen height
	 */
	public int GetScreenHeight() { return this.screenH; }
	
	/**
	 * Gets the camera x
	 * @return x
	 */
	public int X() { return (int) this.pos.X(); }
	
	/**
	 * Get the camera y
	 * @return y
	 */
	public int Y() { return (int) this.pos.Y(); }
	
	/**
	 * Get the camera z
	 * @return z
	 */
	public int Z() { return (int) this.pos.Z(); }	
	
	
	/**
	 * Gets the camera position vector
	 * @return the pos vector
	 */
	public Vec3 Position() {return this.pos; }
	
	/**
	 * Gets the minimum z
	 * @return minZ
	 */
	public int GetMinZ() { return this.minZ; }
	
	/**
	 * Gets the maximum z
	 */
	public int GetMaxZ() { return this.maxZ; }
	
	/**
	 * Gets the last camera size
	 * @return Camera size from the last update iteration
	 */
	public Vec2 LastCamSize() { return this.lastCameraSize; }

}
