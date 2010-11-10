package com.game;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * This class adds a fps and ram indicators to the view hirearcy once attached to the base 
 * layout of the scene. Needs the scene to be a relative layout as to appear on top of the actual scene.
 * 
 * To use, create a Profiler p = new Profiler(), then call Attach(RelativeLayout, Activity) and finally
 * call Update() in your main loop.
 * 
 *  @author Ying
 */
public class Profiler 
{
	/**
	 * Fps time from last update
	 */
	private long previousFpsTime;
	
	/**
	 * Ram time from last update
	 */
	private long previousRamTime;
	
	/**
	 * Current frames per second
	 */
	private int fps;
	
	/**
	 * Current RAM count in bytes
	 */
	private long bytesRam;
	
	/**
	 * FPS refresh speed
	 */
	private int fpsRefresh;
	
	/**
	 * RAM refresh speed
	 */
	private int ramRefresh;
	
	/**
	 * Text view for displaying FPS
	 */
	private TextView fpsView;
	
	/**
	 * Text view for displaying RAM
	 */
	private TextView ramView;

	/**
	 * Initializes data
	 */
	public Profiler()
	{
		// Initialize variables	
		previousFpsTime = 0;
		previousRamTime = 0;
		
		fps = 0;
		bytesRam = 0;
		
		fpsRefresh = 1000; // 1 second
		ramRefresh = 2000; // 2 seconds
	}
	
	/**
	 * Attaches the profiler to a relative layout. 
	 * @param rl is the RelativeLayout to attach to. Should be the root of the view tree to display properly.
	 * @param act the activity using the profiler. Needed to create auxiliary views.
	 */
	public void Attach(RelativeLayout rl, Activity act)
	{
		// Create a layout for storing our text in a horizontal manner
		LinearLayout containerLayout = new LinearLayout( act );
        containerLayout.setOrientation( LinearLayout.HORIZONTAL );
        
        // And it's params
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams( 
        		LayoutParams.FILL_PARENT, 
        		LayoutParams.WRAP_CONTENT );
        
        // Create the fps view
        fpsView = new TextView(act);
        fpsView.setText("Fps: ");
        fpsView.setPadding(0, 0, 10, 0);        
        containerLayout.addView(fpsView, 0); // Add to the layout
        
        // Create the ram view
        ramView = new TextView(act);
        ramView.setText("Mem: ");
        ramView.setPadding(0, 0, 10, 0);
        containerLayout.addView(ramView, 1); // Add to the layout
        
        // Add our view to the provided relative layout.
        rl.addView(containerLayout,tlp);
	}
	
	/**
	 * Updates the fps and ram values. Must be called in the main loop of the app. 
	 */
	public void Update()
	{
		// Update Fps and Mem
		calculateFPS();
		calculateRAM();
	}
	
	/**
	 * Calculates & updates the current fps if fpsRefresh time has been 
	 * reached (usually 1 sec), to avoid cpu overload.
	 */
	private void calculateFPS()
	{
		fps++;
		if(System.currentTimeMillis() - previousFpsTime >= fpsRefresh)
		{
			fpsView.setText("FPS: " + fps ); 
			
			fps = 0;
			previousFpsTime = System.currentTimeMillis();			
		}
	}
	
	/**
	 * Calculates and updates the current RAM ussage if the ramRefresh time
	 * has been reached (usually 2 secs), to avoid cpu overload.
	 */
	private void calculateRAM()
	{
		if(System.currentTimeMillis() - previousRamTime >= ramRefresh)
		{
			bytesRam = Runtime.getRuntime().freeMemory();			
			previousRamTime = System.currentTimeMillis();
			
			ramView.setText("Ram: " + bytesRam);
		}
	}
}
