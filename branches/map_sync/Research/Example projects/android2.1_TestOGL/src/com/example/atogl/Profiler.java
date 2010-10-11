package com.example.atogl;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/*
 * To use this class, create a Profiler p = new Profiler(), then call
 * 
 * Attach(relativeLayout, activity), where you can get the relativeLayout like this:
 *  LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    RelativeLayout layout = (RelativeLayout) li.inflate(R.layout.main, null); 
    
   and finally, call Update in your main loop.
   
   NOTE: Only works well with relative layouts.
 */

public class Profiler 
{
	private long previousFpsTime;
	private long previousRamTime;
	private int fps;
	private long bt;
	
	private int fpsRefresh;
	private int ramRefresh;
	
	private TextView fpsView;
	private TextView ramView;
	

	public Profiler()
	{
		// Initialize variables	
		previousFpsTime = 0;
		previousRamTime = 0;
		
		fps = 0;
		bt = 0;
		
		fpsRefresh = 1000; // 1 second
		ramRefresh = 2000; // 2 seconds
	}
	
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
	
	public void Update()
	{
		// Update Fps and Mem
		calculateFPS();
		calculateRAM();
	}
	
	/// If fpsRefresh time has elapsed, calculate fps
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
	
	/// If ramRefresh time has elapsed, check the current avialable ram
	private void calculateRAM()
	{
		if(System.currentTimeMillis() - previousRamTime >= ramRefresh)
		{
			bt = Runtime.getRuntime().freeMemory();			
			previousRamTime = System.currentTimeMillis();
			
			ramView.setText("Ram: " + bt);
		}
	}
}
