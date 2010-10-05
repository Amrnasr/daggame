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
	public int maxCapacity;
	private Vector<Integer> density;
	private Vector<Player> players;
	private Point position;
	
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
