package com.game.AI;

import java.util.Vector;

import com.game.Cursor;
import com.game.Map;
import com.game.Player;
import com.game.Vec2;

public class Blackboard 
{
	public static Vector<Player> players;
	public static Map map;
	
	public Cursor closestEnemyCursor;
	public Vec2 moveDirection;
	public Vec2 destination;
	public Vector<Vec2> path;
	public Player player;
	
	public Blackboard()
	{
		this.moveDirection = new Vec2();
		this.destination = new Vec2();
		this.path = new Vector<Vec2>();
	}
}
