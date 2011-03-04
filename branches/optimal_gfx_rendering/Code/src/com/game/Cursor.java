package com.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.util.Log;

/**
 * Cursor the player moves around
 * @author Ying
 *
 */
public class Cursor 
{
	/** Reference to the parent player**/
	private Player parent;
	
	/** Cursor position**/
	private Vec2 pos;
	
	/** Cursor speed**/
	private double speed;	
	
	/** Unitary direction of the movement of the cursor**/
	private Vec2 direction;
	
	/** Distance to travel in the direction.**/
	private double distance;
	
	/** Normal speed constant. To turn into a valued enum if there is more than one**/
	private static final int NORMAL_SPEED  = 2;
	
	private float rotationAngle;
	private final float pi = 3.14f;
	private boolean rotating;
	private float currentRotationUpdate;
	private final float rotationUpdates = 60;
	private final float rotateIncrement = 10;
	private int rotationDirection;

	/** Buffer for the cursor square in ogl **/
	private static final FloatBuffer cursorBuff = DagRenderer.makeFloatBuffer(new float[] 
	      { 16f, 16f, 1.0f,
			-16f, 16f, 1.0f,
			16f, -16f, 1.0f,
			-16f, -16f, 1.0f });
	
	private float [] incrementTable;
	
	/**
	 * Current transparency of the powerup.
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
		this.rotationAngle = 0;
		this.rotating = false;
		this.currentRotationUpdate = 0;
		this.rotationDirection = 1;
		this.incrementTable = new float[(int) this.rotationUpdates];
		for(int i = 0; i < this.rotationUpdates; i++)
		{
			incrementTable[i] = this.rotateIncrement*(float) Math.sin((this.pi * i)/this.rotationUpdates);
		}
	}
	
	/**
	 * Sets the position of the Cursor
	 * @param x coordinates
	 * @param y coordinates
	 */
	public synchronized void SetPosition(float x, float y)
	{
		this.pos = new Vec2(x, y);
		
		//Log.i("Cursor", "Initial position: " + x + ", " + y);
	}
	
	/**
	 * Updates the cursor
	 */
	public void Update()
	{
		// If the cursor has been given a initial pos
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
	 * Updates the cursor so it rotates when asked to move.
	 */
	public void RenderUpdate()
	{
		if(rotating)
		{
			//float sinArg = ((float)this.rotationDirection)*(this.pi * this.currentRotationUpdate)/this.rotationUpdates;
			this.rotationAngle += ((float)this.rotationDirection)*incrementTable[(int) this.currentRotationUpdate];
			
			/*
			 * float sinArg = ((float)this.rotationDirection)*(this.pi * this.currentRotationUpdate)/this.rotationUpdates;
			this.rotationAngle += rotateIncrement*Math.sin(sinArg);
			*/
			this.currentRotationUpdate++;
			if( this.currentRotationUpdate >= this.rotationUpdates)
			{
				this.rotating = false;
				
			}
			if(parent.GetID()==0)
			{
				//Log.i("Cursor", "Rotating: " + sinArg +", " + Math.sin(sinArg)+ "," + rotateIncrement*Math.sin(sinArg) + ", " + currentRotationUpdate);
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
			//destination.Print("Cursor", "2 - Requesting move to ");
			MoveInDirection(this.pos.GetVectorTo(destination));
			
		}
	}
	
	public void StartRotating()
	{
		if(rotating)
		{
			return;
		}
		this.rotating = true;
		this.currentRotationUpdate = 0;
		this.rotationDirection *= -1;
	}
	
	/**
	 * Tells the cursor to move in a direction.
	 * @param direction Non-unitary vector of the direction we want to move. 
	 * The length of the vector is taken to be the length of the path we want
	 * to move along. 
	 */
	public void MoveInDirection(Vec2 direction)
	{
		StartRotating();
		this.distance = direction.Length();
		
		this.direction  = direction.GetCopy();
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
	 * 
	 * Syncronized so we don't change the position while the renderer is looking at it
	 * 
	 */
	private synchronized void Move()
	{
		double increment = Math.min(this.distance, this.speed);		
		
		this.pos.Offset(this.direction.X()*increment, this.direction.Y()*increment);
		this.distance -= increment;
		
		// Check if we have to truncate because we are outside the map
		if(pos.X() < 0)
		{
			pos.SetX(0);			
		}
		else if(pos.X() > Preferences.Get().mapWidth)
		{
			pos.SetX(Preferences.Get().mapWidth);
		}
		
		if(pos.Y() < 0)
		{
			pos.SetY(0);
		}
		else if(pos.Y() > Preferences.Get().mapHeight)
		{
			pos.SetY(Preferences.Get().mapHeight);
		}
		//Log.i("Cursor", "New position: " + this.pos.X() + ", " + this.pos.Y());
	}
	
	
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
	 * Gets the alpha value
	 * @return
	 */
	public static float GetAlpha() { return alpha; }
	
	/**
	 * Gets the cursor position
	 * @return a Vec2 with the x,y position in WCS.
	 */
	public synchronized Vec2 GetPosition()	{ return this.pos; }
	
	/**
	 * Indicates if the cursor is from a human player or not
	 * @return True if it's from a human, false if it's from AI
	 */
	public boolean IsFromHuman() { return this.parent.IsHuman(); }
	
	/**
	 * Gets the FloatBuffer for drawing the cursor
	 * @return The float buffer
	 */
	public FloatBuffer GetBuffer() { return Cursor.cursorBuff; }
	
	/**
	 * Gets the Parent of the cursor
	 * @return Parent of the cursor
	 */
	public Player GetPlayer() { return this.parent; }
	
	/**
	 * Gets the current rotation angle of the cursor
	 * @return The rotation angle in degrees
	 */
	public float GetRotationAngle() { return this.rotationAngle; }
	
	/**
	 * Indicates whether the cursor is rotating or not
	 * @return True if it is, false otherwise.
	 */
	public boolean Rotating() { return this.rotating; }
	
	/**
	 * Moddifies the speed of the cursor, by adding the increment.
	 * @param speedIncrement
	 */
	public void AddToSpeed(float speedIncrement) { this.speed += speedIncrement; }

}
