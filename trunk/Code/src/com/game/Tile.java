package com.game;

import java.util.Vector;

import android.graphics.Point;

/**
 * Stub class for the logical map tile.
 * @author Ying
 *
 */
public class Tile 
{
	/**
	 * Tile's Maximum capacity
	 */
	public int maxCapacity;
	/**
	 * Amount of each player's units in the tile 
	 */
	private Vector<Integer> density;
	/**
	 * Reference to the players with units in the tile
	 */
	private Vector<Player> players;
	/**
	 * Tile's position
	 */
	private Point position;
	/**
	 * Initializes the tile
	 * @param x coordinates
	 * @param y coordinates
	 * @param maxCapacity Tile's maximum capacity
	 */
	public Tile(int x, int y, int maxCapacity)
	{
		this.density = new Vector<Integer>(Constants.MaxPlayers);
		this.players = new Vector<Player>(Constants.MaxPlayers);
		this.position = new Point(x, y);
		this.maxCapacity = maxCapacity;
	}
	
	public void Update()
	{
		
	}
}
