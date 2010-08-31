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
		
		optionsSoundMute = settings.getBoolean("optionsSoundMute", true);
		optionsUnitEatSpeed = settings.getInt("optionsUnitEatSpeed", 2);
		optionsUnitMoveSpeed = settings.getInt("optionsUnitMoveSpeed", 2);
		optionsUnitCuantity = settings.getInt("optionsUnitCuantity", 2);
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
	    
	    // Multiplayer preferences
	    
	    
	    // Commit the edits!
	    editor.commit();
	}
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
	public boolean optionsSoundMute;
	public int optionsUnitEatSpeed;
	public int optionsUnitMoveSpeed;
	public int optionsUnitCuantity;

}
