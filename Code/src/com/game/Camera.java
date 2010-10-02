package com.game;


import java.util.Vector;

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
	private static Camera instance = new Camera();;
	private int x;
	private int y;
	private int z;
	
	private int screenH;
	private int screenW;
	
	private int minZ;
	private int maxZ;
	private float minRatio;
	private float maxRatio;
	
	private DagRenderer renderRef;
	
	/**
	 * Prevents the instantiation of an object of the Camera class
	 */
	protected Camera() 
	{
		// TODO: Change to default values, this is just debug
		x = 0;
		y = 0;
		z = 0;
		
		screenH = 0;
		screenW = 0;
		
		minZ = 0;
		maxZ = 0;
		
		minRatio = 1;
		maxRatio = 0;

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
		/*if(instance == null)
		{
			instance = new Camera();
		}*/
		return instance;
	}
	
	/**
	 * Keep all cursors on camera, and the camera just wide enough
	 * TODO: Keep cursors inside, move fluidly. 
	 * TODO: Notify the DagRender of any camera changes.
	 */
	public void Update()
	{
		
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
		
		// Set initial z then:
		this.minZ = 2* this.screenH;
		
	}
	
	/**
	 * Given a point in the screen coordinates, it returns the point in
	 * map coordinates, taking in account the camera position.
	 * 
	 * The logic here is:
	 * (screenPoint + cam(x,y))* cam.z/InitialZ
	 * The first sum is to displace, the multiplication is the ratio that results from 
	 * zooming the camera in/ out.
	 * 
	 * @param touchPos is the point touched on the screen
	 * @return map coordinates for touchPoint
	 */
	public Vec2 ScreenToWorld(Vec2 touchPos)
	{

		return this.renderRef.GetWorldCoords(touchPos, screenW, screenH, this);
		/*
		Vec2 aux = new Vec2();
		int mapH = -1; 
		int mapW = -1;
		
		mapH = Preferences.Get().mapHeight;
		mapW = Preferences.Get().mapWidth;
		
		this.maxZ = 2*mapW;
		this.maxRatio = mapW / screenW;
		Log.i("Camera", "--------------------------------------");
		Log.i("Camera", "Map: " + mapW + ", " + mapH);
		Log.i("Camera", "Screen: " + screenW + ", " + screenH);
		Log.i("Camera", "Touch pos: " + touchPos.X() + ", " + touchPos.Y());
		Log.i("Camera", "X: " + x + " Y: " + y + " Z: " + z);
		
		// Safety checks
		if(screenH == 0 || screenW == 0)
		{
			Log.e("Camera", "Screen size not initialized");
		}
		if(mapH == -1 || mapW == -1)
		{
			Log.e("Camera", "Map not initalized");
		}
		
		
		// The touch cc system is top left, instead of bottom left,only god knows why
		touchPos.SetY(screenH - touchPos.Y());;
		
		// Multiply the coordinates by this ratio to apply the camera perspective transform.
		// And add the displacement.
		float ratio = GetRatioFromZ();	
		Log.i("Camera", "Ratio: " + ratio);
		
		Log.i("Camera", " -- touchPos.X:" + touchPos.X() + ", x:" + x + ", ratio:" + ratio + ", screenW/2:" + (this.screenW/2));
		double xx = (touchPos.X() + x)*ratio -(this.screenW/2) ;
		double yy = (touchPos.Y() + y)*ratio - (this.screenH/2) ;		
		
		aux.Set(xx, yy);		
		Log.i("Camera", "New pos: " + xx + ", " + yy);
		
		return aux;
		*/
	}
	
	private float GetRatioFromZ()
	{
		float zDisplazament = this.z - this.minZ;
		float zRange = this.maxZ - this.minZ;
		
		float percentajeOfDisplazament = zDisplazament / zRange;
		
		float ratioRange = this.maxRatio - this.minRatio;
		float ratioDispl = ratioRange * percentajeOfDisplazament;
		
		float ratio = this.minRatio + ratioDispl;
		return ratio;
	}
	
	public void ZoomOnPlayers(Vector<Player> players)
	{
		if(players == null)
		{
			Log.e("Camera", "Zoom on all players: Players null");
			return;
		}
		// To make sure all player cursors are on screen
		float maxX, maxY, minX, minY;
		maxX = maxY = 0;
		minX = minY = Preferences.Get().mapWidth;
		
		// To calculate the center point
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
			this.x = 0;
			this.y = 0;
			this.z = this.maxZ;
		}
		else
		{
			// Center position of all the cursors that must be shown on screen
			centerX /= cursorCount;
			centerY /= cursorCount;
			
			// Make it so the camera viewport is at least as big as the screen
			int xWidth = (int) (maxX - minX);
			int xHeight = (int) (maxY - minY);
			xWidth = Math.max(xWidth, this.screenW);
			xHeight = Math.max(xHeight, this.screenH);
			
			// Set X and y
			this.x = (int) (centerX - xWidth/2) + this.screenW/2;
			this.y = (int) (centerY - xHeight/2) + this.screenH/2;
			
			// Set Z  (xDDD)			
			xWidth = Math.max(1, xWidth); // To avoid /0 errors while loading
			float ratio = xWidth / this.screenW;
			
			this.maxRatio = Preferences.Get().mapWidth / screenW;
			ratio = ratio - this.minRatio;
			float ratioRange = this.maxRatio - this.minRatio;
			
			ratio = ratio / ratioRange; // % of the total ratio range			
			this.maxZ = 2*Preferences.Get().mapWidth;			
			float zRange = this.maxZ - this.minZ;
			
			this.z = (int) (this.minZ + (zRange * ratio));
		}
		
	}
	
	public int GetScreenWidth() { return this.screenW; }
	public int GetScreenHeight() { return this.screenH; }
	public int X() { return this.x; }
	public int Y() { return this.y; }
	public int Z() { return this.z; }	
	public void SetRenderRef(DagRenderer ref) { this.renderRef = ref; }
}
