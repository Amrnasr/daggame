package com.game.battleofpixels.PowerUp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.game.battleofpixels.Constants;
import com.game.battleofpixels.DagRenderer;
import com.game.battleofpixels.MessageHandler;
import com.game.battleofpixels.MsgType;
import com.game.battleofpixels.Player;
import com.game.battleofpixels.Regulator;
import com.game.battleofpixels.Vec2;
import com.game.battleofpixels.MessageHandler.MsgReceiver;
import com.game.battleofpixels.Preferences.TipName;
import com.game.battleofpixels.Scenes.PlayScene;

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
	 * Real position of the PowerUp while it's on the map
	 */
	private Vec2 mapPos;
	
	/**
	 * Indicates the type of PowerUp it is.
	 * From 0 to PowerUpManager.powerUpTypes
	 */
	private int type;
	
	/**
	 * Time this PowerUp should be active on a player, in seconds
	 */
	private float duration;
	
	/**
	 * Reference to the PlayScene
	 */
	protected PlayScene sceneRef;
	
	/**
	 * Type of tip to display
	 */
	public TipName tipType;
	
	/** 
	 * Buffer for the cursor square in ogl 
	 * **/
	public static final FloatBuffer cursorBuff = DagRenderer.makeFloatBuffer(new float[] 
	       { 16f, 16f, 1.0f,
			-16f, 16f, 1.0f,
			16f, -16f, 1.0f,
			-16f, -16f, 1.0f });                                                                            
	
	/**
	 * Current transparency of the PowerUp.
	 */
	private static float alpha = 0.1f;
	
	/**
	 * Flag to indicate if the alpha increases or decreases. Must be 1 or -1;
	 */
	private static float alphaDir = 1f;
	
	/**
	 * Lower bound for the alpha
	 */
	private static final float minAlpha = 0.1f;
	
	/**
	 * Upper bound for the alpha
	 */
	private static final float maxAlpha = 1;
	
	/**
	 * Creates a instance of the PowerUp class
	 */
	public PowerUp(Vec2 startingPos, int type, float duration, TipName tipType) 
	{
		this.parent = null;
		this.done = false;
		this.mapPos = startingPos;
		this.type = type;
		this.duration = duration;
		this.tipType = tipType;
	}
	
	/**
	 * Sets the reference to the PlayScene
	 * @param ref Reference to the PlayScene
	 */
	public void SetPlaySceneRef(PlayScene ref ) { this.sceneRef = ref; }
	
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
	public void SetPos(Vec2 newPos) 
	{ 
		this.mapPos = newPos;
	}
	
	/**
	 * Gets the FloatBuffer to draw on
	 * @return The float buffer.
	 */
	public FloatBuffer GetBuffer() { return PowerUp.cursorBuff; }
	
	/**
	 * Gets the alpha value
	 * @return
	 */
	public static float GetAlpha() { return alpha; }
	
	/**
	 * Updates the render image
	 */
	public static void AlphaRenderUpdate() 
	{
		alpha += alphaDir*Constants.PowerUpAlphaIncrease;
		
		if(alpha >= maxAlpha)
		{
			alpha = maxAlpha;
			alphaDir = -1;
		}
		else if (alpha <= minAlpha)
		{
			alpha = minAlpha;
			alphaDir = 1;
		}
	}
	
	/**
	 * Gets the type of PowerUp
	 * @return
	 */
	public int GetType() { return this.type; }

	/**
	 * Gets the duration of the PowerUp
	 * @return the duration
	 */
	public float GetDuration() { return this.duration; }
	
}
