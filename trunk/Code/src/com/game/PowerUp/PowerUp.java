package com.game.PowerUp;

import com.game.Player;
import com.game.Regulator;
import com.game.Vec2;

/**
 * A object that gives some kind of special property to a player
 * or a player's army.
 * 
 * If first exists in the map, until a Player picks it up, and the 
 * it's assigned to the player, it executes some effect and updates 
 * until the time has expired.
 * @author Ying
 *
 */
public  abstract class PowerUp 
{
	/**
	 * Reference to the parent Player that owns the PowerUp
	 */
	protected Player parent;
	
	/**
	 * Regulator to keep track of the time this PowerUp is in effect.
	 */
	private Regulator doneTimer;
	
	/**
	 * Indicates whether the PowerUp update time is still not done.
	 */
	private boolean done;
	
	/**
	 * Position of the PowerUp while it's on the map
	 */
	private Vec2 mapPos;
	
	/**
	 * Creates a instance of the PoerUp class
	 */
	public PowerUp(Vec2 startingPos) 
	{
		this.parent = null;
		this.done = false;
		this.mapPos = startingPos;
	}
	
	/**
	 * Assigns the regulator to a specific player and starts it with a 
	 * duration.
	 * 
	 * @param parent The new owner of the PowerUp
	 * @param duration Time the PowerUp is supposed to be active.
	 */
	public void Assign(Player parent, float duration)
	{
		this.parent = parent;
		this.doneTimer = new Regulator(1/duration);
	}
	
	/**
	 * Updates the PowerUp if applicable.
	 */
	public void SafeUpdate()
	{
		if(done)
		{
			return;
		}
		
		if(doneTimer.IsReady())
		{
			done = true;
		}
		
		this.Update();
	}
	
	/**
	 * Gets whether the PowerUp has finished or not
	 * @return True if it's finished, false if it hasn't.
	 */
	public boolean Done() { return this.done; }
	
	/**
	 * Updates the PowerUp
	 */
	public abstract void Update();
	
	/**
	 * Applies the effect it carries to the specific Player.
	 */
	public abstract void ApplyEffect();
	
	/**
	 * Removes the effect from the specific Player.
	 */
	public abstract void RemoveEffect();
}
