package com.game;


import java.util.Vector;

import com.game.MessageHandler.MsgReceiver;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Test for an orthogonal camera system.
 * @author Ying
 *
 */
public class OrthoCamera 
{
	/**
	 * Static singleton instance
	 */
	private static OrthoCamera instance = new OrthoCamera();
	
	/**
	 * Position in ogl coords of the camera
	 */
	private Vec3 pos;
	
	/**
	 * Size of the camera viewport
	 */
	private Vec2 size;
	
	/**
	 * Screen viewport height
	 */
	private int screenH;
	
	/**
	 * Screen viewport width
	 */
	private int screenW;
	
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
	 * Used for interpolation.
	 */
	private int speed;
	
	/**
	 * Speed constant.
	 */
	private static final int NORMAL_SPEED = 5;
	
	/**
	 * Distance the camera is at.
	 */
	private static final float zDistance = 800;
	
	/**
	 * Width of the view frustrum so that the map image covers the whole screen
	 * without distortion.
	 */
	private int fillScreenWidth;
	
	/**
	 * Height of the view frustrum so that the map image covers the whole screen
	 * without distortion.
	 */
	private int fillScreenHeight;
	
	private Handler handler;
	
	/**
	 * Prevents the instantiation of an object of the Camera class
	 */
	protected OrthoCamera() 
	{
		pos = new Vec3();
		size = new Vec2();

		screenH = 0;
		screenW = 0;
		
		direction = new Vec3();
		distance = 0;
		speed = NORMAL_SPEED;
		
		fillScreenWidth = 1;
		fillScreenHeight = 1;
		
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {	
	        	if(msg.what == MsgType.CAMERA_CALCULATE_MAP_RELIANT_DATA.ordinal())
	        	{
	        		SetMapReliantData(Preferences.Get().mapWidth, Preferences.Get().mapHeight);
	        	}
	        }
		};
		
		MessageHandler.Get().SetCameraHandler(this.handler);	
	}
	
	/**
	 * Gets the singleton instance of the camera. Creates the camera if it's
	 * the first time it's called.
	 * 
	 * TODO: Check speed hit for having a sync
	 * 
	 * @return the instance of the camera
	 */
	public static synchronized OrthoCamera Get()
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
	 * NOTE: Called on ACTIVITY CREATION, before any Scene creation or anything,
	 * so it's not dangerous to assume the values are initialized when doing map
	 * initialization.
	 * 
	 * @param w is the width of the screen
	 * @param h is the height of the screen
	 */
	public void SetScreenSize(int w, int h)
	{
		this.screenH = h;
		this.screenW = w;		
	}
	
	/**
	 * Initializes any members that rely on the map size.
	 * @param w Width of the map
	 * @param h Height of the map
	 */
	public void SetMapReliantData(int mapW, int mapH)
	{		
		// Initial size is the map size
		this.size.Set(mapW, mapH);
		
		// map width divided by the size ratio (sH/mH) is the "full screen" size
		// The weird calculation for the W is to avoid distortion
		this.fillScreenWidth = 665; //(int)(mapW / ((float)((float)this.screenH / (float)mapH)));
		this.fillScreenHeight = mapH;
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
		
		if(cursorCount == 0)
		{
			// No human players, just watch the whole damm thing
			this.pos.Set(0, 0, zDistance);
		}
		else
		{
			// Point to go
			Vec3 destination = new Vec3();
			
			// Center position of all the cursors that must be shown on screen
			centerX /= cursorCount;
			centerY /= cursorCount;
			
			// Make it so the camera viewport is at least as big as the map
			int xWidth = (int) (maxX - minX);
			this.size.SetX( Math.max(xWidth, this.fillScreenWidth) );
			this.size.SetY((Preferences.Get().mapHeight / ((float)((float)(Preferences.Get().mapWidth) / (float)(this.size.X())))));
			//Log.i("Camera", "Viewport: " + xWidth  + ", " + xHeight);
			
			// Set destination
			destination.SetX((centerX - this.size.X()/2) + this.screenW/2);
			destination.SetY((centerY - this.size.Y()/2) + this.screenH/2);
			destination.SetZ(zDistance);
			
			// Once the destination point is calculated, request to move there.
			MessageHandler.Get().Send(MsgReceiver.RENDERER, MsgType.RENDERER_CHANGE_VIEWPORT_SIZE, (int)this.size.X(), (int)this.size.Y());
			MoveTo(destination);			
		}
		
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
	public Vec3 Pos() {return this.pos; }
	
	/**
	 * Gets the maximum z
	 */
	//public int GetMaxZ() { return 2*Preferences.Get().mapWidth; }
	
	public Vec2 GetWorldCoords( Vec2 touch )
	{
		   //Log.i("World Coords", "-------------- ");
		   Log.i("Camera", "Cam info --------------- ");
		   this.pos.Print("Camera", "Position");
		   this.size.Print("Camera", "Size");
		   Log.i("Camera", "FillScreen: " + fillScreenWidth + ", " + fillScreenHeight);
		   
		   // Initialize auxiliary variables.
		   Vec2 worldPos = new Vec2();

		   // Invert y coordinate, as android uses top-left, and ogl bottom-left.
		   touch.SetY((screenH - touch.Y()));
		   
		   worldPos.Set(this.fillScreenWidth/this.size.X(), this.fillScreenHeight/this.size.Y());
		   worldPos.Offset(this.pos.X(), this.pos.Y());
		   
		   //Log.i("World Coords", "Move to point: " + worldPos.X() + ", " + worldPos.Y() + ", " + worldZ);			   
		   
		   return worldPos;	   
	   }
	
	

}
