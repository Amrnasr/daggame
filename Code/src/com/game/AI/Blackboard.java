package com.game.AI;

import java.util.Vector;

import com.game.Cursor;
import com.game.Map;
import com.game.Player;
import com.game.Vec2;

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
	public Vector<Vec2> path;
	
	/**
	 * Reference to the owner player
	 */
	public Player player;
	
	/**
	 * Creates a new instance of the Blackboard class
	 */
	public Blackboard()
	{
		this.moveDirection = new Vec2();
		this.destination = new Vec2();
		this.path = new Vector<Vec2>();
	}
}
