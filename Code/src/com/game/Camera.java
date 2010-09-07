package com.game;


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
 * @author Ying
 *
 */
public class Camera 
{
	private static Camera instance = null;
	private int x;
	private int y;
	private int z;
	
	private int screenH;
	private int screenW;
	
	/**
	 * Prevents the instantiation of an object of the Camera class
	 */
	protected Camera() 
	{
		x = 0;
		y = 0;
		z = 0;
		
		screenH = 0;
		screenW = 0;
	}
	
	/**
	 * Gets the singleton instance of the camera. Creates the camera if it's
	 * the first time it's called.
	 * 
	 * @return the instance of the camera
	 */
	public static Camera Get()
	{
		if(instance == null)
		{
			instance = new Camera();
		}
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
	}
	
	/**
	 * Given a point in the screen coordinates, it returns the point in
	 * map coordinates, taking in account the camera position.
	 * 
	 * @param touchPos is the point touched on the screen
	 * @return map coordinates for touchPoint
	 */
	public Vec2 ScreenToWorld(Vec2 touchPos)
	{
		Vec2 aux = new Vec2();
		int mapH = -1; 
		int mapW = -1;
		
		mapH = Preferences.Get().mapHeight;
		mapW = Preferences.Get().mapWidth;
		
		// Safety checks
		if(screenH == 0 || screenW == 0)
		{
			Log.e("Camera", "Screen size not initialized");
		}
		if(mapH == -1 || mapW == -1)
		{
			Log.e("Camera", "Map not initalized");
		}
		
		// Multiply the coordinates by this ratio to apply the camera perspective transform.
		// And add the displacement.
		double ratio = this.z / InitialZ();
		aux.Set((touchPos.X() + x)*ratio, (touchPos.Y() + y)*ratio);		
		
		return aux;
	}
	
	/**
	 * The initial Z of the camera, which is the distance from the camera 
	 * to the map at witch there is a 1:1 ratio of screen:map pixels.
	 * 
	 * The value 2*screen height is empirically obtained and true, even tough
	 * my trigonometry says it should be screen height / 2;
	 * 
	 * @return the initial Z of the camera.
	 */
	private int InitialZ()
	{
		return 2*screenH;
	}

}
