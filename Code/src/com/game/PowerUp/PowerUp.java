package com.game.PowerUp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.game.MessageHandler;
import com.game.MsgType;
import com.game.Player;
import com.game.Regulator;
import com.game.Vec2;
import com.game.MessageHandler.MsgReceiver;

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
	
	/** Buffer for the cursor square in ogl **/
	FloatBuffer cursorBuff;
	
	/**
	 * Creates a instance of the PoerUp class
	 */
	public PowerUp(Vec2 startingPos) 
	{
		this.parent = null;
		this.done = false;
		this.mapPos = startingPos;
		
		float[] debSquare = new float[] 
		                    	      { 30f, 30f, 1.0f,
		                    			0f, 30f, 1.0f,
		                    			30f, 0f, 1.0f,
		                    			0f, 0f, 1.0f };
		                    		
		this.cursorBuff = makeFloatBuffer(debSquare);
	}
	
	/**
	 * Makes a float buffer for ogl drawing
	 * @param arr of floats to turn into a buffer
	 * @return the float buffer asociated to arr
	 */
	protected static FloatBuffer makeFloatBuffer(float[] arr)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
	
	/**
	 * Assigns the regulator to a specific player and starts it with a 
	 * duration.
	 * 
	 * Applies the effect of the PowerUp to the player
	 * 
	 * @param parent The new owner of the PowerUp
	 * @param duration Time the PowerUp is supposed to be active.
	 */
	public void Assign(Player parent, float duration)
	{
		this.parent = parent;
		parent.LinkPowerUp(this);
		this.doneTimer = new Regulator(1/duration);
		MessageHandler.Get().Send(MsgReceiver.RENDERER, MsgType.STOP_DISPLAYING_POWERUP, this);
		MessageHandler.Get().Send(MsgReceiver.LOGIC, MsgType.STOP_DISPLAYING_POWERUP, this);
		
		ApplyEffect();
	}
	
	/**
	 * Updates lets the Player update the PowerUp.
	 * If it reaches the end of it's life, the effects are removed.
	 */
	public void PlayerUpdate()
	{
		if(done)
		{
			return;
		}
		
		if(doneTimer.IsReady())
		{
			done = true;
			RemoveEffect();
		}
		
		this.Update();
	}
	
	/**
	 * Gets whether the PowerUp has finished or not
	 * @return True if it's finished, false if it hasn't.
	 */
	public boolean Done() 
	{ 
		return this.done; 
	}
	
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
	
	/**
	 * Gets the position of the PowerUp in the map
	 * @return The map position.
	 */
	public Vec2 Pos() {return this.mapPos; }
	
	/**
	 * Sets a new map position for the PowerUp
	 * @param newPos New position of the PowerUp.
	 */
	public void SetPos(Vec2 newPos) { this.mapPos = newPos; }
	
	/**
	 * Gets the FloatBuffer to draw on
	 * @return The float buffer.
	 */
	public FloatBuffer GetBuffer() { return this.cursorBuff; }
}
