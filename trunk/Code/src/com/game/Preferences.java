package com.game;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Simple singleton for storing game options
 * @author Ying
 *
 */
public class Preferences 
{
	// Private instance
	private static Preferences instance;
	
	/**
	 * Defeats instanciation
	 */
	protected Preferences() {}
	
	/**
	 * Returns the singleton instance
	 * @return Preferences global instance 
	 */
	public static Preferences Get()
	{
		if(instance == null)
		{
			instance = new Preferences();
		}
		
		return instance;
	}
	
	public void Load(Activity activity)
	{
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
		
		// Options preferences
		optionsSoundMute = settings.getBoolean("optionsSoundMute", true);
		optionsUnitEatSpeed = settings.getInt("optionsUnitEatSpeed", 2);
		optionsUnitMoveSpeed = settings.getInt("optionsUnitMoveSpeed", 2);
		optionsUnitCuantity = settings.getInt("optionsUnitCuantity", 2);
		
		// Single preferences
		singleCurrentMap = settings.getInt("singleCurrentMap", 0);
		singlePlayer1Color = settings.getInt("singlePlayer1Color", 0);
		singleNumberOpponents = settings.getInt("singleNumberOpponents", 2);
		singleControlMode = settings.getInt("singleControlMode", 0);
		singleShowMinmap = settings.getBoolean("singleShowMinmap", true);
		singlePowerups = settings.getBoolean("singlePowerups", true);
		
		// Multi preferences
	}
	
	public void Save(Activity activity)
	{
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    
	    // Options preferences
	    editor.putBoolean("soundMute", optionsSoundMute);
	    editor.putInt("optionsUnitEatSpeed", optionsUnitEatSpeed);
	    editor.putInt("optionsUnitMoveSpeed", optionsUnitMoveSpeed);
	    editor.putInt("optionsUnitCuantity", optionsUnitCuantity);	    
	    
	    // Single player preferences
	    editor.putInt("singleCurrentMap", singleCurrentMap);	 
	    editor.putInt("singlePlayer1Color", singlePlayer1Color);	 
	    editor.putInt("singleNumberOpponents", singleNumberOpponents);	 
	    editor.putInt("singleControlMode", singleControlMode);	
	    editor.putBoolean("singleShowMinmap", singleShowMinmap);
	    editor.putBoolean("singlePowerups", singlePowerups);
	    
	    // Multiplayer preferences
	    
	    
	    // Commit the edits!
	    editor.commit();
	}
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
	// Options data
	public boolean optionsSoundMute;
	public int optionsUnitEatSpeed;
	public int optionsUnitMoveSpeed;
	public int optionsUnitCuantity;
	
	// Single player data
	public int singleCurrentMap;
	public int singlePlayer1Color;
	public int singleNumberOpponents;
	public int singleControlMode;
	public boolean singleShowMinmap;
	public boolean singlePowerups;
	
	// Multiplayer data

}
