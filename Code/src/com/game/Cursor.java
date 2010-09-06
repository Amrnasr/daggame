package com.game;

import android.util.Log;


/**
 * Cursor the player moves around
 * @author Ying
 *
 */
public class Cursor 
{
	// Reference to the parent player
	private Player parent;
	
	// Cursor position
	private Vec2 pos;
	
	// Cursor speed
	private double speed;
	
	
	// Unitary direction of the movement of the cursor
	private Vec2 direction;
	
	// Distance to travel in the direction.
	private double distance;
	
	// Normal speed constant. To turn into a valued enum if there is more than one
	private static final int NORMAL_SPEED  = 5;
	
	/**
	 * Creates an instance of the Cursor class
	 * @param parent to assign this cursor to
	 */
	public Cursor(Player parent)
	{
		this.parent = parent;		
		
		this.pos = null;
		this.speed = NORMAL_SPEED;
		this.direction = null;
		this.distance = 0;
	}
	
	/**
	 * Sets the position of the Cursor
	 * @param x coordinates
	 * @param y coordinates
	 */
	public void SetPosition(double x, double y)
	{
		this.pos = new Vec2(x, y);
	}
	
	/**
	 * Updates the cursor
	 */
	public void Update()
	{
		// If the cursor has been givven a initial pos
		if(InitialPositionSet())
		{
			// And it has to move
			if(HasToMove())
			{
				// Move it
				Move();
			}
		}
	}
	
	/**
	 * Called to request the cursor to move to a specified position, in a straight line, to the max of it's speed.
	 * @param destination is the point we want the Cursor to translate to
	 */
	public void MoveTo(Vec2 destination)
	{
		if(! this.pos.Equals(destination))
		{
			// move in that direction	
			MoveInDirection(this.pos.GetVectorTo(destination));
		}
	}
	
	/**
	 * Tells the cursor to move in a direction.
	 * @param direction Non-unitary vector of the direction we want to move. 
	 * The length of the vector is taken to be the length of the path we want
	 * to move along. 
	 */
	public void MoveInDirection(Vec2 direction)
	{
		this.distance = direction.Length();
		
		this.direction  = direction;
		this.direction.Normalize();		
	}
	
	/**
	 * Checks if the initial position has been set
	 * @return True if it has, false if it hasn't
	 */
	private boolean InitialPositionSet() { return this.pos != null; }
	
	/**
	 * Checks if the Cursor has to move
	 * 
	 * TODO: Check map bounds.
	 * @return True if it has to move, false if it doesn't.
	 */
	private boolean HasToMove()
	{
		if(this.direction == null)
		{
			return false;
		}
		if(this.distance == 0)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * If it needs to move, this function updates it's position one 
	 * speed step, or the remaining of the distance, whichever is shorter.
	 */
	private void Move()
	{
		double increment = Math.min(this.distance, this.speed);		
		
		this.pos.Offset(this.direction.X()*increment, this.direction.Y()*increment);
		this.distance -= increment;
		
		//Log.i("Cursor", "New position: " + this.pos.X() + ", " + this.pos.Y());
	}
}
