package com.game.battleofpixels.AI;

import java.util.Vector;

import com.game.battleofpixels.Cursor;
import com.game.battleofpixels.Map;
import com.game.battleofpixels.Player;
import com.game.battleofpixels.Tile;
import com.game.battleofpixels.Vec2;
import com.game.battleofpixels.PowerUp.PowerUpManager;

/**
 * Data class for the player AI in the game.
 * Has static members for shared data for all players,
 * and normal members for player-specific data.
 *  
 * @author Ying
 *
 */
public class Blackboard 
{
	/**
	 * Reference to the vector of players in the game
	 */
	public static Vector<Player> players;
	
	/**
	 * Reference to the game map
	 */
	public static Map map;
	
	/**
	 * Reference to the game PowerUpManager
	 */
	public static PowerUpManager powerUpManager;
	
	/**
	 * Closest enemy cursor
	 */
	public Cursor closestEnemyCursor;
	
	/**
	 * Direction vector to move in
	 */
	public Vec2 moveDirection;
	
	/**
	 * Destination point to arrive at
	 */
	public Vec2 destination;
	
	/**
	 * Path of positions to move to
	 */
	public Vector<Tile> path;
	
	/**
	 * Reference to the owner player
	 */
	public Player player;
	
	/**
	 * Data for the AStar search.
	 */
	public AStarData aStarData;
	
	/**
	 * Creates a new instance of the Blackboard class
	 */
	public Blackboard()
	{
		this.moveDirection = new Vec2();
		this.destination = new Vec2();
		this.path = new Vector<Tile>();
	}
	
	public void Log()
	{
		android.util.Log.i("Blackboard", "+-+-+-+-+-+-+-+-+-+-+");
		
		if(path != null)
		{
			String tileString = "";
			for(int i  = 0;  (i < path.size()); i++ )
			{
				tileString += (" (" + path.elementAt(i).GetPos().X() + ", " + path.elementAt(i).GetPos().Y() + ") ->");
			}
			android.util.Log.i("Blackboard", "Path {tiles: " + path.size() + "} :: {" + tileString + "}");
		}
		else
		{
			android.util.Log.i("Blackboard", "Path: NULL");
		}
		
		if(player != null)
		{
			String playerString = "Player: {ID: " + player.GetID() + "} :: {Cursor: ";
			if(player.GetCursor() != null)
			{
				playerString += ("" + player.GetCursor().GetPosition().X() + ", " + player.GetCursor().GetPosition().Y() + "}");
			}
			else
			{
				playerString += "NULL }";
			}
			android.util.Log.i("Blackboard", playerString);
		}
		
		if(destination != null)
		{
			destination.Print("Blackboard", "Destination Vec2");
		}
		else
		{
			android.util.Log.i("Blackboard", "Destination Vec2: NULL");
		}
		
		if(moveDirection != null)
		{
			moveDirection.Print("Blackboard", "MoveDirecti Vec2");
		}
		else
		{
			android.util.Log.i("Blackboard", "MoveDirecti Vec2: NULL");
		}
		
		if(aStarData != null)
		{
			String aStarString = "A*: ";
			Tile tile = aStarData.initialTile;
			if(tile != null)
			{
				aStarString += ("{Start: " + tile.GetRealPos().X() + ", " + tile.GetRealPos().Y() + " } :: ");
			}
			tile = aStarData.destinationTile;
			if(tile != null)
			{
				aStarString += ("{End: " + tile.GetRealPos().X() + ", " + tile.GetRealPos().Y() + " } ");
			}
			android.util.Log.i("Blackboard", aStarString);
		}
		
		android.util.Log.i("Blackboard", "");
		android.util.Log.i("Blackboard", "+-+-+-+-+-+-+-+-+-+-+");
	}
	
}
