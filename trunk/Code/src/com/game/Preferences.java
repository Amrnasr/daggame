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
	 * Defeats instantiation
	 */
	protected Preferences() 
	{
		this.multiplayerGame = false;
		
		// Made up default map size
		this.mapHeight = 100;
		this.mapWidth = 100;
	}
	
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
		multiCurrentMap = settings.getInt("multiCurrentMap", 0);
		multiPlayer1Color = settings.getInt("multiPlayer1Color", 0);
		multiPlayer2Color = settings.getInt("multiPlayer2Color", 0);
		multiNumberOpponents = settings.getInt("multiNumberOpponents", 1);
		multiControlMode = settings.getInt("multiControlMode", 0);
		multiShowMinimap = settings.getBoolean("multiShowMinimap", true);
		multiPowerups = settings.getBoolean("multiPowerups", true);
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
	    editor.putInt("multiCurrentMap", multiCurrentMap);
	    editor.putInt("multiPlayer1Color", multiPlayer1Color);
	    editor.putInt("multiPlayer2Color", multiPlayer2Color);
	    editor.putInt("multiNumberOpponents", multiNumberOpponents);
	    editor.putInt("multiControlMode", multiControlMode);
	    editor.putBoolean("multiShowMinimap", multiShowMinimap);
	    editor.putBoolean("multiPowerups", multiPowerups);
	    
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
	public int multiCurrentMap;
	public int multiPlayer1Color;
	public int multiPlayer2Color;
	public int multiNumberOpponents;
	public int multiControlMode;
	public boolean multiShowMinimap;
	public boolean multiPowerups;
	
	// Other global data
	public boolean multiplayerGame;
	public int mapWidth;
	public int mapHeight;

}
