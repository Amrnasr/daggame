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
	private int maxCapacity;
	
	/**
	 * Amount of each player's units in the tile 
	 */
	private int[] density;
	/**
	 * Reference to the players with units in the tile
	 */
	private Player[] players;
	/**
	 * Tile's position in row/column
	 */
	private Vec2 position;
	
	/**
	 * Keeps track if the tile has been updated this cycle
	 */
	private boolean dirty;
	
	/**
	 * Reference to the parent map
	 */
	private Map mapRef;
	
	/**
	 * Initializes the tile
	 * @param x coordinates
	 * @param y coordinates
	 * @param maxCapacity Tile's maximum capacity
	 */
	public Tile(int x, int y, int numberWhitePixels, Map mapRef)
	{
		this.density = new int[Preferences.Get().GetNumberOfPlayers()];
		for(int i = 0; i < density.length; i++)
		{
			density[i] = 0;
		}
		
		this.players = new Player[Preferences.Get().GetNumberOfPlayers()];
		for(int i = 0; i < players.length; i++)
		{
			players[i] = null;
		}
		
		this.position = new Vec2(x, y);
		
		this.maxCapacity = numberWhitePixels; 
		
		this.dirty = false;
		
		this.mapRef = mapRef;
	}
	
	/**
	 * Updates the tile density flow.
	 */
	public void Update()
	{
		if(dirty) 
		{
			// Already updated this cycle. Must skip
			return;
		}
		
		for(int i = 0; i < players.length; i++)
		{
			// For each player
			Player curPlay = players[i];
			if(curPlay != null)
			{
				// Find where we have to move it's density
				int tilePosX = (int) (this.position.X()*Constants.TileWidth);
				int tilePosY = (int) (this.position.Y()*Constants.TileWidth);
				
				int dirX = 0;
				if(tilePosX > curPlay.GetCursor().GetPosition().X())
				{
					dirX = -1;
				}
				else {dirX = 1;}
				
				int dirY = 0;
				if(tilePosY > curPlay.GetCursor().GetPosition().Y())
				{
					dirY = -1;
				}
				else {dirY = 1;}
				
				// Try to move it
				int densityToMove = Math.min(density[i], GetMaxCapacity()/2);
				int leftovers = 0;
				
				if(density[i] < DivideThreshold())
				{
					// If it's a small quantity, just move it all in the closest distance
					leftovers = TryMoveDensity(curPlay, dirX, dirY, densityToMove, 1.0f);
					
					// TODO: if there's anything left, try to move it in diagonals
				}
				else
				{
					//Move in shortest direction + diagonals
					
					// If there is anything left, use the perpendiculars as well
				}
				
				
				// Remove total - leftovers
				
				// If density[player] = 0, unlink player and tile
				
			}
		}
	}
	
	private int TryMoveDensity(Player player, int dirX, int dirY, int totalDensity, float percent)
	{
		int leftovers = 0;
		
		Tile toMove = this.mapRef.AtTile((int)(this.position.X() + dirX), (int)(this.position.Y()+dirY));
		
		if(toMove == null)
		{
			// No available tile, return all density
			leftovers = totalDensity;
		}
		else
		{
			// Put as much density as we can, return the rest
			int maxCap = toMove.GetCurrentCapacity();
			int toAdd = Math.min(maxCap, totalDensity);
			
			toMove.AddDensity(player, toAdd);
			
			leftovers = Math.max(0, totalDensity-toAdd);
		}
		
		return leftovers;
	}
	
	/**
	 * Prepares the tile.
	 * Marks it as un-updated (not dirty) so the players can update it.
	 */
	public void Prepare()
	{
		this.dirty = false;
	}
	
	/**
	 * Returns the absolute maximum density ANY tile can have. 
	 * Does NOT return the maximum density of this particular tile
	 * see GetMaxCapacity() for that.
	 * 
	 * TODO: Precompute at the beginning
	 * 
	 * @return Absolute maximum density of ANY tile.
	 */
	public static int TileMaxCapacity() { return Constants.TileWidth * Constants.TileWidth;}
	
	/**
	 * The threshold above which is ok to divide the density
	 * TODO: Confirm tentative value. Set to 20% of the maximum
	 * TODO: Precompute at the beginning
	 * @return Divide threshold
	 */
	private int DivideThreshold() 
	{
		return (2*TileMaxCapacity())/10;
	}
	
	/**
	 * Gets the maxCapacity of this tile.
	 * @return maxCapacity
	 */
	public int GetMaxCapacity() { return this.maxCapacity; }
	
	/**
	 * Gets the total density on the tile, as the sum of all the 
	 * densities of all the players.
	 * @return Total current density
	 */
	public int GetCurrentDensity() 
	{
		int sum = 0;
		for(int i = 0; i < density.length; i++)
		{
			sum += density[i];
		}
		return sum;
	}
	
	/**
	 * Gets the current remaining capacity of the tile
	 * @return The current remaining capacity of the tile.
	 */
	public int GetCurrentCapacity()
	{
		return GetMaxCapacity() - GetCurrentDensity();
	}
	
	/**
	 * Sets some density in the tile for a specific player.
	 * Links the player if not linked previously.
	 * Does no limit checking for density overflow.
	 * 
	 * @param player Player that adds the capacity
	 * @param density Quantity of density to add
	 */
	public void AddDensity(Player player, int density)
	{
		int playerPos = player.GetID();
		if(this.players[playerPos] == null)
		{
			this.players[playerPos] = player;
		}
		
		this.density[playerPos] += density;
	}
	
	/**
	 * Gets the tile position in row/column format.
	 * @return Tile position
	 */
	public Vec2 GetPos() { return this.position; }
}
