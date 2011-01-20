package com.game;

import java.util.Vector;

import android.graphics.Bitmap;

/**
 * Data packet class for initializing the renderer.
 * @author Ying
 *
 */
public class RenderInitData 
{
	/**
	 * Map to render
	 */
	private Map map;
	
	/**
	 * Player cursor vector
	 */
	private Vector<Cursor> cursors;
	
	/**
	 * Combat bitmap for rendering
	 */
	private Bitmap combatBitmap;
	
	/**
	 * Cursor bitmap for rendering
	 */
	private Bitmap cursorBitmap;
	
	/**
	 * Cursor shadow bitmap for rendering
	 */
	private Bitmap cursorShadowBitmap;
	
	/**
	 * PowerUp bitmap for rendering
	 */
	private Bitmap powerUpBitmap;
	
	/**
	 * PowerUp shadow bitmap for rendering
	 */
	private Bitmap powerUpShadowBitmap;
	
	/**
	 * Joystick main bitmap for rendering
	 */
	private Bitmap joystickMainBitmap;
	
	/**
	 * Joystick small bitmap for rendering 
	 */
	private Bitmap joystickSmallBitmap;
	
	/**
	 * Vector of players to check for which tiles to render
	 */
	private Vector<Player> players;
	
	/**
	 * Creates a instance of the RenderInitData class
	 */
	public RenderInitData()
	{
		this.map = null;
		this.cursors = null;
		this.players = null;
	}
	
	/**
	 * Sets the player vector
	 * @param players
	 */
	public void SetPlayers(Vector<Player> players) { this.players = players; }
	
	/**
	 * Sets the cursors vector
	 * @param cursors
	 */
	public void SetCursors(Vector<Cursor> cursors) { this.cursors = cursors; }
	
	/**
	 * Sets the map 
	 * @param mapImage
	 */
	public void SetMap(Map map) { this.map = map; }
	
	/**
	 * Sets the combat bitmap
	 * @param combatBitmap
	 */
	public void SetCombatBitmap(Bitmap combatBitmap) { this.combatBitmap = combatBitmap; }
	
	/**
	 * Sets the cursor bitmap
	 * @param cursorBitmap
	 */
	public void SetCursorBitmap(Bitmap cursorBitmap) { this.cursorBitmap = cursorBitmap; }
	
	/**
	 * Sets the cursor shadow bitmap
	 * @param cursorBitmap
	 */
	public void SetCursorShadowBitmap(Bitmap cursorShadowBitmap) { this.cursorShadowBitmap = cursorShadowBitmap; }
	
	/**
	 * Sets the PowerUp bitmap
	 * @param powerUpBitmap
	 */
	public void SetPowerUpBitmap(Bitmap powerUpBitmap) { this.powerUpBitmap = powerUpBitmap; }
	
	/**
	 * Sets the PowerUp shadow bitmap
	 * @param powerUpShadowBitmap
	 */
	public void SetPowerUpShadowBitmap(Bitmap powerUpShadowBitmap) { this.powerUpShadowBitmap = powerUpShadowBitmap; }
	
	/**
	 * Sets the joystick main bitmap
	 * @param joystickMainBitmap
	 */
	public void SetJoysticMainBitmap(Bitmap joystickMainBitmap) { this.joystickMainBitmap = joystickMainBitmap; }
	
	/**
	 * Sets the joystick small bitmap
	 * @param joystickSmallBitmap
	 */
	public void SetJoystickSmallBitmap(Bitmap joystickSmallBitmap) { this.joystickSmallBitmap = joystickSmallBitmap; }
	
	/**
	 * Gets the player vector
	 * @return
	 */
	public Vector<Player> GetPlayers() { return this.players; }
	
	/**
	 * Gets the cursor vector.
	 * @return
	 */
	public Vector<Cursor> GetCursors() { return this.cursors; }
	
	/**
	 * Gets the map bitmap
	 * @return
	 */
	public Map GetMap() { return this.map; }
	
	/**
	 * Gets the combat bitmap
	 * @return
	 */
	public Bitmap GetCombatBitmap() { return this.combatBitmap; }
	
	/**
	 * Gets the cursor bitmap
	 * @return
	 */
	public Bitmap GetCursorBitmap() { return this.cursorBitmap; }
	
	/**
	 * Gets the cursor shadow bitmap
	 * @return
	 */
	public Bitmap GetCursorShadowBitmap() { return this.cursorShadowBitmap; }
	
	/**
	 * Gets the PowerUp bitmap
	 * @return
	 */
	public Bitmap GetPowerUpBitmap() { return this.powerUpBitmap; }
	
	/**
	 * Gets the PowerUp shadow bitmap
	 * @return
	 */
	public Bitmap GetPowerUpShadowBitmap() { return this.powerUpShadowBitmap; }
	
	/**
	 * Gets the Joystick main bitmap
	 * @return
	 */
	public Bitmap GetJoystickMainBitmap() { return this.joystickMainBitmap; }
	
	/**
	 * Gets the Joystick small bitmap
	 * @return
	 */
	public Bitmap GetJoystickSmallBitmap() { return this.joystickSmallBitmap; }
	
}
