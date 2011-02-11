package com.game;

/**
 * Constant class.
 * All cross-system game constants go here.
 * 
 * @author Ying
 *
 */
public class Constants 
{
	private Constants() {}
	
	/**
	 * Debug mode check.
	 */
	//public static final boolean DebugMode = false;
	
	/**
	 * Color Intensity of the cursors
	 */
	public static final float CursorColorIntensity = 0.8f;
	
	/**
	 * Increment for the PowerUp glow
	 */
	public static final float PowerUpAlphaIncrease = 0.05f;
	
	/**
	 * Maximum number of players.
	 */
	public static final int MaxPlayers = 6;
	
	/**
	 * Size of a map tile for medium or high screen density.
	 */
	public static final int TileWidth = 16;
	
	/**
	 * Size of a map tile for low screen density.
	 */
	public static final int TileWidthLowDpi = 8;
	
	/**
	 * Probability of appearance of the combat effect
	 */
	public static final float CombatEffectChance = 0.03f;
	
	/**
	 * Number of images of the combat effect
	 */
	public static final int CombatEffectImgNum = 4;
	
	/**
	 * To show the profiler onscreen
	 */
	public static final boolean OnScreenProfiler = true;

}
