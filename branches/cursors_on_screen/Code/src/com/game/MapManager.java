package com.game;

import android.app.Activity;
import android.util.Log;

/**
 * Factory and manager for the Maps. Keeps a pointer to the current map 
 * to update and modify it.
 * 
 * @author NeoM
 *
 */
public class MapManager {
	/// Map active right now, there can be one and only one.
	private Map mCurrentMap;
	// Minimap active right now, there can be one and only one.
	private Minimap mCurrentMinimap;
	// Activity that created the map manager
	private Activity mCurrentActivity;
	// Stores whether the minimap must be shown or not
	private boolean mShowMinimap;
	
	/**
	 * Initializes the map manager
	 * @param activity Activity that created the map manager
	 */
	public MapManager(Activity activity)
	{
		mCurrentMap = null;
		mCurrentMinimap = null;
		mShowMinimap=false;
		mCurrentActivity = activity;
	}
	
	/**
	 * Swaps the current map for a new one. 
	 * @param mapRef Reference to the map to change to.
	 * @param showMinimap Whether the minimap must be drawn or not.
	 */
	public void Load(int mapRef, boolean showMinimap)
    {
		mShowMinimap=showMinimap;
		//Finish the previous map and minimap if it existed
        if (mCurrentMap!=null) 
        {
        	mCurrentMap.End();
        }
        
        if (mCurrentMinimap!=null) 
        {
        	mCurrentMinimap.End();
        	mCurrentMinimap=null;
        }
        //Initialize the new map
        mCurrentMap = new Map(mCurrentActivity,mapRef);
        mCurrentMap.Start();
        
        //Initialize the new minimap if needed
        if(mShowMinimap){
        	mCurrentMinimap = new Minimap(mCurrentMap);
        	mCurrentMinimap.Start();
        }
    }

	/**
	 * Gets the current map
	 * @return The current map
	 */
	public Map getCurrentMap()
	{
		return mCurrentMap;
	}
	
	/**
	 * Gets the current minimap
	 * @return The current minimap
	 */
	public Minimap getCurrentMinimap()
	{
		return mCurrentMinimap;
	}
	
	/**
	 * Called by the game logic thread each update loop.
	 */
	public void Update()
	{
		if(mCurrentMap != null)
		{
			mCurrentMap.Update();
			if(mShowMinimap){
				mCurrentMinimap.Update();
			}
		}
		else
		{
			Log.i("MapManager", "No current map yet!");
		}
	}
}
