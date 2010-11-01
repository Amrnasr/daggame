package com.game;

import java.util.Vector;


/**
 * Stub class for the logical map tile.
 * @author Ying
 *
 * (\_/)
 * (-.-)
 * c(")(")
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
	private boolean densityMoved;
	
	/**
	 * Keeps track if the tile has already fought this cycle
	 */
	private boolean densityFought;
	
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
		
		this.densityMoved = false;
		
		this.densityFought = false;
		
		this.mapRef = mapRef;
	}
	
	/**
	 * Updates the tile density flow.
	 * Uses the following schema for choosing the next tile 
	 * (Assuming the tile x wants to move density to top-right)
	 * 
	 * 000 001 021 321
	 * 0x0 0x0 0x2 0x2
	 * 000 000 000 003
	 */
	public void MoveDensity()
	{

		//Log.i("Tile", "Updating: " + position.X() + ", " + position.Y());
		if(densityMoved) 
		{
			// Already updated this cycle. Must skip
			return;
		}
		densityMoved = true;
		
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
				int densityToMove = density[i]; //Math.min(density[i], GetMaxCapacity());
				int leftovers = 0;
				
				// Try to send it in the diagonal (1)
				leftovers = TryMoveDensity(curPlay, dirX, dirY, densityToMove);
				
				// Density left? Send it to the sides (2)
				if(leftovers > 0)
				{
					int leftoverToMove = leftovers;
					leftovers = 0;
					leftovers += TryMoveDensity(curPlay, 0, dirY, leftoverToMove/2);
					leftovers += TryMoveDensity(curPlay, dirX, 0, (leftoverToMove/2) + (leftoverToMove%2));
				}
				
				// Density left? Send it to the perpendiculars (3)
				if(leftovers > 0)
				{
					int leftoverToMove = leftovers;
					leftovers = 0;
					leftovers += TryMoveDensity(curPlay, -dirX, dirY, leftoverToMove/2);
					leftovers += TryMoveDensity(curPlay, dirX, -dirY, (leftoverToMove/2) + (leftoverToMove%2));
				}
				
				// Remove total - leftovers
				int densityMoved = densityToMove - leftovers;
				density[i] -= densityMoved;
			}
		}
	}
	
	/**
	 * Makes the combat logic update in the tile.
	 */
	public void DensityFight()
	{
		if(densityFought)
		{
			return;
		}
		densityFought = true;

		DummyDensityFight();
		
	}
	
	/**
	 * Empty fight algorithm.
	 */
	private void DummyDensityFight()
	{
		
	}
	
	/**
	 * Trivial fight algorithim for densities
	 */
	private void BasicDensityFight()
	{
		int maxDensity = 0;
		int curMaxPlayer = -1;
		int numbPlayers = 0;
		for(int i = 0; i < density.length; i++)
		{
			if(density[i] > maxDensity )
			{
				maxDensity = density[i];
				curMaxPlayer = i;
			}
			numbPlayers++;
		}
		
		// If there is only one player, or the density he has is 0
		if(numbPlayers <= 1 || curMaxPlayer == -1 || maxDensity == 0)
		{
			return;
		}
		
		// For every player...
		for(int i = 0; i < density.length; i++)
		{
			// .. except the max density one 
			if(i != curMaxPlayer)
			{
				// ... if the player has any density
				if(density[i] > 0)
				{
					// .. take a percentage of it.
					int toTake = Math.min(DivideThreshold(), density[i]);
					density[i] -= toTake;
					density[curMaxPlayer] += toTake;
				}
			}
		}
	}
	
	
	/**
	 * TODO: Make it just for 2 players to optimize 
	 * How many densities do you think there are going to be in a tile normally anyhow?
	 * 
	 * TODO: Refactor this monster function. Damn it's ugly.
	 */
	private void MovementDensityFigth()
	{
		
		Vector<Integer> toFight = null;
		
		
		
		// Find all who wish to fight! SPARTA!
		for(int i = 0; i < density.length; i++)
		{
			if(density[i] > 0)
			{
				if(toFight == null) { toFight = new Vector<Integer>(); }
				toFight.add(i);
			}
		}
		
		// Remember, there is only lazy evaluation for &&, not for ||
		if(toFight == null ) { return; }
		if(toFight.size() <= 1) { return; }
		
		Vec2 tileToCur1 = new Vec2();
		Vec2 tileToCur2 = new Vec2(); 
		float tileX = (float) (this.position.X()*Constants.TileWidth);
		float tileY = (float) (this.position.Y()*Constants.TileWidth);
		int densityP1 = 0;
		int densityP2 = 0;
		
		// Fight among all the densities
		for(int i = 0; i< toFight.size(); i++)
		{
			for(int j = i; j < toFight.size(); j++)
			{
				tileToCur1.SetX(this.players[toFight.elementAt(i)].GetCursor().GetPosition().X() - tileX);
				tileToCur1.SetY(this.players[toFight.elementAt(i)].GetCursor().GetPosition().Y() - tileY);
				
				tileToCur2.SetX(this.players[toFight.elementAt(j)].GetCursor().GetPosition().X() - tileX);
				tileToCur2.SetY(this.players[toFight.elementAt(j)].GetCursor().GetPosition().Y() - tileY);
				
				// Both densities are moving in the same direction
				if(tileToCur1.Dot(tileToCur2) > 0)
				{
					// Get the direction of the sum of vectors, and size it to tile length
					tileToCur2.Add(tileToCur1);
					tileToCur2.Normalize();
					tileToCur2.Scale(Constants.TileWidth);
					
					// Get the next tile in the direction
					Tile next = this.mapRef.AtWorld((int)(tileX + tileToCur2.X()), (int)(tileY + tileToCur2.Y()));
					
					if(next != null)
					{
						// Next tile has {enemy density / mixed density / no density} ?
						densityP1 = next.GetDensityFrom(toFight.elementAt(i));
						densityP2 = next.GetDensityFrom(toFight.elementAt(j));
						
						if(densityP1 == 0 && densityP2 == 0)
						{
							// Check backwards
							Tile prev = this.mapRef.AtWorld((int)(tileX - tileToCur2.X()), (int)(tileY - tileToCur2.Y()));
							if(prev == null)
							{
								// Density fight
								SameDensityFight(toFight.elementAt(i), toFight.elementAt(j));
							}
							else
							{
								densityP1 = prev.GetDensityFrom(toFight.elementAt(i));
								densityP2 = prev.GetDensityFrom(toFight.elementAt(j));
								
								if(densityP1 == 0 && densityP2 == 0)
								{
									// Density fight!
									SameDensityFight(toFight.elementAt(i), toFight.elementAt(j));
								}
								else if(densityP1 > densityP2)
								{
									// P2 is bitch, P1 is rapist
									StealDensity(toFight.elementAt(j), toFight.elementAt(i), DivideThreshold());
								}
								else
								{
									// P1 is bitch, P2 is rapist
									StealDensity(toFight.elementAt(i), toFight.elementAt(j), DivideThreshold());
								}
							}
						}
						else if(densityP1 > densityP2)
						{
							// P1 is bitch, P2 is rapist
							StealDensity(toFight.elementAt(i), toFight.elementAt(j), DivideThreshold());
						}
						else
						{
							// P2 is bitch, P1 is rapist
							StealDensity(toFight.elementAt(j), toFight.elementAt(i), DivideThreshold());
						}
					}
					else
					{
						// Check backwards
						Tile prev = this.mapRef.AtWorld((int)(tileX - tileToCur2.X()), (int)(tileY - tileToCur2.Y()));
						if(prev == null)
						{
							// Density fight
							SameDensityFight(toFight.elementAt(i), toFight.elementAt(j));
						}
						else
						{
							densityP1 = prev.GetDensityFrom(toFight.elementAt(i));
							densityP2 = prev.GetDensityFrom(toFight.elementAt(j));
							
							if(densityP1 == 0 && densityP2 == 0)
							{
								// Density fight!
								SameDensityFight(toFight.elementAt(i), toFight.elementAt(j));
							}
							else if(densityP1 > densityP2)
							{
								// P2 is bitch, P1 is rapist
								StealDensity(toFight.elementAt(j), toFight.elementAt(i), DivideThreshold());
							}
							else
							{
								// P1 is bitch, P2 is rapist
								StealDensity(toFight.elementAt(i), toFight.elementAt(j), DivideThreshold());
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Steals density from the specified player to another player in the same tile.
	 * It only steals up to the total the attacked player has.
	 * 
	 * @param from Player to steal from
	 * @param to Player to give the stolen density
	 * @param cuantity How much density we want to try and steal
	 * @return How much we didn't manage to steal.
	 */
	private int StealDensity(int from, int to, int cuantity)
	{
		int realDensity = Math.min(cuantity, density[from]);
		density[from] -= realDensity;
		density[to] += realDensity;
		
		return cuantity - realDensity;
	}
	
	/**
	 * Trivial fight for two players. The one who has more density steals
	 * from the one who has less.
	 * 
	 * @param player1 The id of one of the players who fights
	 * @param player2 The id of the other player who fights
	 */
	private void SameDensityFight(int player1, int player2)
	{
		if(density[player1] > density[player2])
		{
			StealDensity(player2, player1, DivideThreshold());
		}
		else
		{
			StealDensity(player1, player2, DivideThreshold());
		}
	}
	
	/**
	 * Checks if the player has to unlink from this tile.
	 * 
	 * @param player ID of the player we are checking against
	 * @return True if it has to unlink, false otherwise.
	 */
	public boolean HasToUnlink(int player)
	{
		return this.density[player] <= 0;
	}
	
	/**
	 * Unlinks a player from this tile. Cuts connections on both pointers
	 * (player -> tile and tile -> player)
	 * @param player Player to unlink
	 */
	public void Unlink(int player)
	{
		players[player].UnlinkTile(this);
		players[player] = null;
	}
	
	/**
	 * Tries to move the specified density to an adjacent tile.
	 * 
	 * @param player Player who owns the density we are trying to move.
	 * @param dirX X direction regarding where to move the density. [1,0,-1]
	 * @param dirY Y direction regarding where to move the density. [1,0,-1]
	 * @param totalDensity We want to attempt to move
	 * @return The leftovers we weren't able to move to the specified tile.
	 */
	private int TryMoveDensity(Player player, int dirX, int dirY, int totalDensity)
	{
		return TryAgressiveMoveDensity(player, dirX, dirY, totalDensity);
	}
	
	/**
	 * Tries to move the density if it fits.
	 * 
	 * @param player Player who owns the density we are trying to move.
	 * @param dirX X direction regarding where to move the density. [1,0,-1]
	 * @param dirY Y direction regarding where to move the density. [1,0,-1]
	 * @param totalDensity We want to attempt to move
	 * @return The leftovers we weren't able to move to the specified tile.
	 * @return
	 */
	
	private int TryPassiveMoveDensity(Player player, int dirX, int dirY, int totalDensity)
	{
		int leftovers = 0;
		
		Tile toMove = this.mapRef.AtTile((int)(this.position.X() + dirX), (int)(this.position.Y()+dirY));
		
		if(toMove == null)
		{
			// No available tile, return all density
			leftovers = (int) totalDensity;
		}
		else
		{
			// Put as much density as we can, return the rest
			int maxCap = toMove.GetCurrentCapacity();
			maxCap = Math.max(0, maxCap);
			int toAdd = (int) Math.min(maxCap, totalDensity);
			
			if(toAdd > 0)
			{
				toMove.AddDensity(player, toAdd);
			}
			
			leftovers = (int) Math.max(0, totalDensity-toAdd);
			//leftovers = totalDensity-toAdd;
		}
		
		return leftovers;
	}
	
	/**
	 * Tries to move the density and steal density from the tile it's moving to, 
	 * if there is any enemy there.
	 * 
	 * @param player Player who owns the density we are trying to move.
	 * @param dirX X direction regarding where to move the density. [1,0,-1]
	 * @param dirY Y direction regarding where to move the density. [1,0,-1]
	 * @param totalDensity We want to attempt to move
	 * @return The leftovers we weren't able to move to the specified tile.
	 * @return
	 */
	private int TryAgressiveMoveDensity(Player player, int dirX, int dirY, int totalDensity)
	{
		int leftovers = 0;
		
		Tile toMove = this.mapRef.AtTile((int)(this.position.X() + dirX), (int)(this.position.Y()+dirY));
		
		if(toMove == null)
		{
			// No available tile, return all density
			leftovers = (int) totalDensity;
		}
		else
		{
			// Steal from the enemy first
			if(toMove.HasEnemyDensity(player.GetID()))
			{
				int enemyPlayer = toMove.GetFirstEnemy(player.GetID());
				
				toMove.Link(player);
				toMove.StealDensity(enemyPlayer, player.GetID(), totalDensity);	

			}

			// Put as much density as we can, return the rest
			int maxCap = toMove.GetCurrentCapacity();
			maxCap = Math.max(0, maxCap);
			int toAdd = (int) Math.min(maxCap, totalDensity);
			
			if(toAdd > 0)
			{
				toMove.AddDensity(player, toAdd);
			}
			
			leftovers = (int) Math.max(0, totalDensity-toAdd);

		}
		
		return leftovers;
	}
	

	/**
	 * Checks to see if the tile has any density from any other player
	 * @param player ID of the player who checks
	 * @return True if it does, false if it doesn't
	 */
	public boolean HasEnemyDensity(int player)
	{
		boolean enemyFound = false;
		
		for(int i = 0; i < density.length; i++)
		{
			if(density[i] > 0 && i != player)
			{
				enemyFound = true; 
				break;
			}
		}
		
		return enemyFound;
	}
	
	/**
	 * Gets the first enemy it finds for the specified player
	 * @param player ID of the player looking for enemies
	 * @return the ID of the enemy player
	 */
	private int GetFirstEnemy(int player)
	{
		int enemy = -1;
		for(int i = 0; i < density.length; i++)
		{
			if(density[i] > 0 && i != player)
			{
				enemy = i;
				break;
			}
		}
		return enemy;
	}
	
	/**
	 * Prepares the tile.
	 * Marks it as un-updated (not dirty) so the players can update it.
	 */
	public void Prepare()
	{
		this.densityMoved = false;
		this.densityFought = false;
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
	public synchronized int GetCurrentDensity() 
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
	public synchronized void AddDensity(Player player, int density)
	{
		int playerPos = player.GetID();
		Link(player);
		
		this.density[playerPos] += density;
	}
	
	/**
	 * Links the tile with a specified player if they weren't linked yet.
	 * @param player Player to link with.
	 */
	public void Link(Player player)
	{
		int playerPos = player.GetID();
		if(this.players[playerPos] == null)
		{
			this.players[playerPos] = player;
			this.densityMoved = true;
			
			player.LinkTile(this);
		}
	}
	
	/**
	 * Gets the tile position in row/column format.
	 * @return Tile position
	 */
	public Vec2 GetPos() { return this.position; }
	
	/**
	 * Returns whether the player has units in this tile or not
	 * @param player The player whose presence on the tile is going to be checked
	 * @return The player has units there or not
	 */
	public boolean IsPlayerThere(int player) 
	{ 
		return (density[player] > 0) ? true : false;
	}
	
	/**
	 * Gets the density that player has in the tile
	 * @param player From whom we want the density
	 * @return The density of player
	 */
	public int GetDensityFrom(int player)
	{
		return this.density[player];
	}
}

