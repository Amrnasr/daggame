package com.game.battleofpixels;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Simple singleton for storing game options
 * @author Ying
 *
 */
public class Preferences 
{
	/**
	 * Private static sintleton instance, with active initialization.
	 */
	private static Preferences instance = new Preferences();
	
	/**
	 * Private constructor that defeats instantiation
	 */
	protected Preferences() 
	{
		this.multiplayerGame = false;
		
		// Made up default map size
		this.mapHeight = 0;
		this.mapWidth = 0;
		this.winnerPlayerColorIndex = -1;
	}
	
	/**
	 * Returns the singleton instance
	 * @return Preferences global instance 
	 */
	public static Preferences Get()
	{	
		return instance;
	}
	
	/**
	 * Loads preferences from the android shared preferences, or gets the default values if nothing is stored.
	 * @param activity from which to load.
	 */
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
		singleShowMinimap = settings.getBoolean("singleShowMinimap", true);
		singlePowerups = settings.getBoolean("singlePowerups", true);
		
		// Multi preferences
		multiCurrentMap = settings.getInt("multiCurrentMap", 0);
		multiPlayer1Color = settings.getInt("multiPlayer1Color", 0);
		multiPlayer2Color = settings.getInt("multiPlayer2Color", 1);
		multiNumberOpponents = settings.getInt("multiNumberOpponents", 1);
		multiControlMode = settings.getInt("multiControlMode", 0);
		multiShowMinimap = settings.getBoolean("multiShowMinimap", true);
		multiPowerups = settings.getBoolean("multiPowerups", true);
		
		/*if(singleCurrentMap >= Constants.MapsNum){
			singleCurrentMap = 0;
		}
		
		if(multiCurrentMap >= Constants.MapsNum){
			multiCurrentMap = 0;
		}*/
		// Color preferences
		
	}
	
	/**
	 * Saves the preferences to shared preferences memory.
	 * @param activity to save in.
	 */
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
	    editor.putBoolean("singleShowMinimap", singleShowMinimap);
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
	
	/**
	 * From the loaded preferences, calculates the number of players.
	 * @return number of players, including human.
	 */
	public int GetNumberOfPlayers()
	{
		int numb = 0;
		if(multiplayerGame == true)
		{
			// 2 Players and a number of opponents
			numb = 2 + multiNumberOpponents;
		}
		else
		{
			// 1 Player and a number of opponents
			numb = 1 + singleNumberOpponents;
		}
		return numb;
	}
	
	/**
	 * Name of the preference files to store in.
	 */
	public static final String PREFS_NAME = "MyPrefsFile";
	
	// Options data //
	/**
	 * Indicates whether the music is set to mute
	 */
	public boolean optionsSoundMute;
	
	/**
	 * Indicates the unit eating speed.
	 */
	public int optionsUnitEatSpeed;
	
	/**
	 * Indicates the unit move speed.
	 */
	public int optionsUnitMoveSpeed;
	
	/**
	 * Indicates the number of units a player can have.
	 */
	public int optionsUnitCuantity;
	
	// Single player data //
	/**
	 * The current map selection in singleplayer mode
	 */
	public int singleCurrentMap;
	
	/**
	 * Player 1 color in single player
	 */
	public int singlePlayer1Color;
	
	/**
	 * Number of opponents in singleplayer.
	 */
	public int singleNumberOpponents;
	
	/**
	 * Input mode for the human player ins singleplayer mode.
	 */
	public int singleControlMode;
	
	/**
	 * Indicates whether to show the minimap in the corner.
	 */
	public boolean singleShowMinimap;
	
	/**
	 * Indicates whether there will be powerups in this game.
	 */
	public boolean singlePowerups;
	
	// Multiplayer data //
	/**
	 * Current map in multiplayer
	 */
	public int multiCurrentMap;
	
	/**
	 * Player 1 color in multiplayer
	 */
	public int multiPlayer1Color;
	
	/**
	 * Player 2 color  in multiplayer
	 */
	public int multiPlayer2Color;
	
	/**
	 * Number of AI opponents in multiplayer mode
	 */
	public int multiNumberOpponents;
	
	/**
	 * Control mode for player 1 in multiplayer mode.
	 */
	public int multiControlMode;
	
	/**
	 * Indicates whether to show the minimap in multiplayer mode
	 */
	public boolean multiShowMinimap;
	
	/**
	 * Indicates whether to have powerups in multiplayer mode
	 */
	public boolean multiPowerups;
	
	// Other global data //
	/**
	 * Indicates whether the game is a multiplayer one or not (singleplayer)
	 */
	public boolean multiplayerGame;
	
	/**
	 * Map width in WCS
	 */
	public int mapWidth;
	
	/**
	 * Map height in WCS
	 */
	public int mapHeight;
	
	/**
	 * Player that has won this round
	 */
	public int winnerPlayerColorIndex;
	
	/**
	 * Size of a map tile
	 */
	public int tileWidth;
	
	public String playerColor(int colIndex)
	{
		//set the base color of the density
		switch(colIndex)
		{
			case 0: return "Brown";
			case 1: return "Green";
			case 2: return "Blue"; 
			case 3: return "Cyan"; 
			case 4: return "Purple"; 
			case 5: return "Yellow"; 
			default: return "ERROR!";
		}	
	}

}
