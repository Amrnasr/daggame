package com.game.battleofpixels;

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
	 * Number of different maps
	 */
	public static final int MapsNum = 11;
	
	/**
	 * To show the profiler on screen
	 */
	public static final boolean OnScreenProfiler = false;
	
	/**
	 * Create a tracert trace of the game
	 */
	public static final boolean TracertProfiler = false;
	
	/**
	 * If set to true, pressing the menu key in the Android phone generates a 
	 * memory dump.
	 */
	public static final boolean MenuGenerateMemoryDump = false;

	/**
	 * When this flag is set to true the renderer takes all the extra cycles from the logic
	 */
	public static final boolean RenderTakeExtraLogicCycles = false;
	
	/**
	 * Debug counter for stuff
	 */
	public static long CurLogicCycle = 0;

}
