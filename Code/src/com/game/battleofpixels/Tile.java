package com.game.battleofpixels;

import java.util.Collections;
import java.util.Vector;

import android.util.Log;

import com.game.battleofpixels.AI.Blackboard;
import com.game.battleofpixels.MessageHandler.MsgReceiver;
import com.game.battleofpixels.PowerUp.PowerUp;


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
	 * Keeps track if the tile has been color-updated this round
	 */
	private boolean densityColorUpdated;
	
	/**
	 * Reference to the parent map
	 */
	private Map mapRef;
	
	/**
	 * Reference to a PowerUp if any.
	 */
	private PowerUp powerUpRef;
	
	/**
	 * Bonus multiplier for AI to make it more challenging
	 */
	private static final float AIBonusDensityMovement = 2f;
	
	/**
	 * Player index vector to randomize the order of the players update every cycle.
	 */
	private static Vector<Integer> playerIndex = new Vector<Integer>();
	
	/**
	 * Maximum density any tile can have
	 */
	private static final int TILEMAXCAPACITY = Preferences.Get().tileWidth * Preferences.Get().tileWidth;
	
	/**
	 * Index of the tile in the map vector of tiles
	 */
	private int index;
	
	/**
	 * Initializes the tile
	 * @param x coordinates
	 * @param y coordinates
	 * @param numberWhitePixels The capacity the tile has
	 * @param mapRef Reference to the Map that owns the Tile
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
		
		this.powerUpRef = null;
		
		this.index = (int) (position.Y() * Preferences.Get().mapWidth/Constants.TileWidth + position.X());
	}
	
	/**
	 * Initializes the static index vector of the Tile class 
	 * @param numberPlayers Number of players in the PlayScene
	 */
	public static void InitIndexVector(int numberPlayers)
	{
		Tile.playerIndex.clear();
		for(int i = 0; i < numberPlayers; i++)
		{
			Tile.playerIndex.add(i);
		}
	}
	
	/**
	 * Shuffles the order of the index vector
	 */
	public static void ShuffleIndexVector()
	{
		Collections.shuffle(Tile.playerIndex);
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
			int playPos = Tile.playerIndex.elementAt(i);
			Player curPlay = players[playPos];
			if(curPlay != null)
			{
				// Find where we have to move it's density
				int tilePosX = (int) (this.position.X()*Preferences.Get().tileWidth);
				int tilePosY = (int) (this.position.Y()*Preferences.Get().tileWidth);
				
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
				float requestedDensityToMove = density[playPos]* players[playPos].GetDensitySpeed();
				
				if(!curPlay.IsHuman())
				{
					// Give the AI a little edge
					requestedDensityToMove *= AIBonusDensityMovement;
				}
				
				int densityToMove = 0;
				if(requestedDensityToMove < 1.0f && requestedDensityToMove > 0.0f )
				{
					densityToMove = 1;
				}
				else
				{
					densityToMove = Math.min(density[playPos], (int)requestedDensityToMove);
				}
				
				//int densityToMove = density[i]; 
				int leftovers = 0;
				
				// Try to send it in the diagonal (1)
				leftovers = TryMoveDensity(curPlay, dirX, dirY, densityToMove, true);
				
				// Density left? Send it to the sides (2)
				if(leftovers > 0)
				{
					int leftoverToMove = leftovers;
					leftovers = 0;
					leftovers += TryMoveDensity(curPlay, 0, dirY, leftoverToMove/2,false);
					leftovers += TryMoveDensity(curPlay, dirX, 0, (leftoverToMove/2) + (leftoverToMove%2),false);
				}
				
				// Density left? Send it to the perpendiculars (3)
				if(leftovers > 0)
				{
					int leftoverToMove = leftovers;
					leftovers = 0;
					leftovers += TryMoveDensity(curPlay, -dirX, dirY, leftoverToMove/2,false);
					leftovers += TryMoveDensity(curPlay, dirX, -dirY, (leftoverToMove/2) + (leftoverToMove%2),false);
				}
				
				// Remove total - leftovers
				int densityMoved = densityToMove - leftovers;
				density[playPos] -= densityMoved;
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
		
		if(this.GetCurrentCapacity() == this.maxCapacity)
		{
			this.mapRef.SetColor(this.GetRealPos(), 1, 1, 1, 1);
		}
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
	private int TryMoveDensity(Player player, int dirX, int dirY, int totalDensity, boolean isAttackDir)
	{
		return TryAgressiveMoveDensity(player, dirX, dirY, totalDensity, isAttackDir);
	}
	
	
	
	/**
	 * Tries to move the density and steal density from the tile it's moving to, 
	 * if there is any enemy there.
	 * 
	 * 
	 * @param player Player who owns the density we are trying to move.
	 * @param dirX X direction regarding where to move the density. [1,0,-1]
	 * @param dirY Y direction regarding where to move the density. [1,0,-1]
	 * @param totalDensity We want to attempt to move
	 * @param isAttackDir Indicates whether we are attacking in the straight line. 
	 * 	If so we eat enemies, if not we don't
	 * @return The leftovers we weren't able to move to the specified tile.
	 */
	private int TryAgressiveMoveDensity(Player player, int dirX, int dirY, int totalDensity, boolean isAttackDir)
	{
		int leftovers = 0;
		
		// To avoid it moving so much in a straight line, and more to the sides.
		if(isAttackDir && totalDensity > 10)
		{
			int rest = totalDensity /4; // Make it 75% only
			leftovers += rest;
			totalDensity -= rest;
		}
		
		Tile toMove = this.mapRef.AtTile((int)(this.position.X() + dirX), (int)(this.position.Y()+dirY));
		
		if(toMove == null)
		{
			// No available tile, return all density
			leftovers += (int) totalDensity;
		}
		else
		{
			// Steal from the enemy first
			if(toMove.HasEnemyDensity(player.GetID()) && isAttackDir)
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
			
			leftovers += (int) Math.max(0, totalDensity-toAdd);

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
		this.densityColorUpdated = false;
	}
	
	/**
	 * Returns the absolute maximum density ANY tile can have. 
	 * Does NOT return the maximum density of this particular tile
	 * see GetMaxCapacity() for that.
	 * 
	 * @return Absolute maximum density of ANY tile.
	 */
	public static int TileMaxCapacity() { return TILEMAXCAPACITY;}
	
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
			
			this.PowerUpLink(player);
		}
	}
	
	/**
	 * If the tile has a PowerUp it links it to a player.
	 */
	private void PowerUpLink(Player player)
	{
		if(HasPowerUp())
		{
			//Log.i("Tile", "Giving powerup to Player " + player.GetID() +" !");
			this.powerUpRef.Assign(player, this.powerUpRef.GetDuration());
			
			if(!Preferences.Get().multiplayerGame && player.GetID() == 0)
			{
				if(Preferences.Get().IsTipActive(powerUpRef.tipType))
				{
					MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.DISPLAY_TIP, powerUpRef.tipType.ordinal());
				}
			}
			this.RemovePowerUp();
		}
	}
	
	/**
	 * Gets the tile position in row/column format.
	 * @return Tile position
	 */
	public Vec2 GetPos() { return this.position; }
	
	/**
	 * Gets the tile position in map x/y pixels.
	 * @return Tile position
	 */
	public Vec2 GetRealPos() {return new Vec2(position.X()*Preferences.Get().tileWidth, position.Y()*Preferences.Get().tileWidth); }
	
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
	
	/**
	 * Adds a powerup to the tile, if it had one before it 
	 * discards it.
	 * 
	 * @param newPowerUp PowerUp to add
	 */
	public void AddPowerUp(PowerUp newPowerUp)
	{
		//Log.i("Tile", "Add new powerup!");
		this.powerUpRef = newPowerUp;
	}
	
	/**
	 * Removes the current PowerUp from the tile.
	 */
	public void RemovePowerUp() 
	{
		this.powerUpRef = null;
	}
	
	/**
	 * Gets whether the tile has a PowerUp
	 * @return True if it has a PowerUp, false if it doesn't.
	 */
	public boolean HasPowerUp()
	{
		return this.powerUpRef != null;
	}
	
	/**
	 * Gets whether the tile has been color updated this cycle
	 * @return True if it has, false if it doesn't
	 */
	public boolean HasBeenColorUpdated() { return this.densityColorUpdated; }
	
	/**
	 * Marks the tile as color updated this cycle
	 */
	public void FlagAsColorUpdated()  { this.densityColorUpdated = true; }
	
	/**
	 * Gets the index of the tile in the map
	 * @return The index in the Tile Vector in the Map
	 */
	public int Index() { return index;}
}

