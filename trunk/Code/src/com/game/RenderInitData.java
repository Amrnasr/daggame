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
	 * Creates a instance of the RenderInitData class
	 */
	public RenderInitData()
	{
		this.map = null;
		this.cursors = null;
	}
	
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
	 * Gets the cursor vector.
	 * @return
	 */
	public Vector<Cursor> GetCursors() { return this.cursors; }
	
	/**
	 * Gets the vector of tiles
	 * @return
	 */
	public Vector<Tile> GetTileMap() { return this.map.getTileMap(); }
	
	/**
	 * Gets the map texture
	 * @return
	 */
	public Bitmap GetBitmap() { return this.map.getBitmap(); }
}
