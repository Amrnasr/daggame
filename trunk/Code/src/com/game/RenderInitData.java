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
	 * Map texture
	 */
	private Bitmap mapImage;
	
	/**
	 * Tilemap data
	 */
	private Vector<Tile> tileMap;
	
	/**
	 * Player cursor vector
	 */
	private Vector<Cursor> cursors;
	
	/**
	 * Creates a instance of the RenderInitData class
	 */
	public RenderInitData()
	{
		this.mapImage = null;
		this.tileMap = null;
		this.cursors = null;
	}
	
	/**
	 * Sets the cursors vector
	 * @param cursors
	 */
	public void SetCursors(Vector<Cursor> cursors) { this.cursors = cursors; }
	
	/**
	 * Sets the tile vector
	 * @param tileMap
	 */
	public void SetTileMap(Vector<Tile> tileMap) { this.tileMap = tileMap; }
	
	/**
	 * Sets the map texture
	 * @param mapImage
	 */
	public void SetMapImage(Bitmap mapImage) { this.mapImage = mapImage; }
	
	/**
	 * Gets the cursor vector.
	 * @return
	 */
	public Vector<Cursor> GetCursors() { return this.cursors; }
	
	/**
	 * Gets the vector of tiles
	 * @return
	 */
	public Vector<Tile> GetTileMap() { return this.tileMap; }
	
	/**
	 * Gets the map texture
	 * @return
	 */
	public Bitmap GetMapImage() { return this.mapImage; }
}
