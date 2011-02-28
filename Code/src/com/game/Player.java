package com.game;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import android.os.SystemClock;
import android.util.Log;

import com.game.InputDevice.InputDevice;
import com.game.PowerUp.PowerUp;

/**
 * Player class, a player has a Input mode and a Cursor to controll it's army.
 * @author Ying
 *
 */
public class Player 
{
	/**
	 * Input device for controlling this player
	 */
	private InputDevice inputDevice;
	
	/**
	 * Player cursor for moving the army
	 */
	private Cursor cursor;
	
	/**
	 * Unique identifier for the player. It will come handy eventually, I can assure you.
	 */
	private int playerNumber;
	
	/**
	 * Indicates whether the player is human controlled.
	 */
	private boolean humanPlayer;
	
	/**
	 * Actual tiles the player has density in.
	 */
	private Vector<Tile> tiles;
	
	/**
	 * Reference to the game map
	 */
	private Map mapRef; 
	
	/**
	 * The initial density for the player
	 */
	private int initialDensity;
	
	/**
	 * Regulates the update speed of the tiles
	 */
	private Regulator tileUpdateRegulator;
	
	/**
	 * Total density owned by the player at this point
	 */
	private int totalDensity;
	
	/**
	 * Density the player had in the previous update. 
	 * Used to calculate the "improvement" of density
	 */
	private int previousDensity;
	
	/**
	 * Keeps a record of the latest rounds wins/losses
	 */
	private CircularBuffer fightRecord;
	
	/**
	 * Length of the record of fightRecord
	 */
	private final static int fightRecordLength = 5;
	
	/**
	 * Index of the color to be used to render this player's army
	 */
	private int colorIndex;
	
	/**
	 * The speed of the density movement of this player.
	 * From 0 to 1.
	 */
	private float densitySpeed;
	
	/**
	 * The starting speed of the density movement of this player.
	 * From 0 to 1.
	 */
	private float startingDensitySpeed;
	
	/**
	 * List of PowerUps the player has
	 */
	private Vector<PowerUp> powerUps;
	
	/**
	 * Last time when the amount of density the player has changed.
	 */
	private long lastTimeDensityUpdated;
	
	/**
	 * Whether the player is slowed or not
	 */
	private boolean isSlowed;
	
	/**
	 * Whether the player is faster or not
	 */
	private boolean isFaster;
	
	/**
	 * Whether a slow PowerUp is applied or not
	 */
	private boolean isSlowPowerUpApplied;
	
	/**
	 * Time when the PowerUp started being rendered
	 */
	private long powerUpRenderingStartTimeMillis;
	
	/**
	 * position in the vector of the PowerUp being rendered.
	 */
	private int powerUpBeingRenderedIndex;
	
	/**
	 * Creates a new instance of the Player class.
	 * @param playerNumber Unique player identifier. If it's not unique you'll regret it later.
	 * @param inputDevice Input device used by this player.
	 * @param humanPlayer true if it's a human player, false if it's IA
	 * @param colorIndex is the color for the player
	 */
	public Player(int playerNumber, InputDevice inputDevice, boolean humanPlayer , int colorIndex)
	{
		this.playerNumber = playerNumber;
		this.humanPlayer = humanPlayer;
		
		// Cursor	
		this.cursor = new Cursor(this);		
		
		this.inputDevice = inputDevice;
		this.inputDevice.SetParent(this);
		this.inputDevice.Start();
		
		// Tiles
		tiles = new Vector<Tile>();
		tileUpdateRegulator = new Regulator(10);
		
		this.initialDensity = 2000 + 200*Preferences.Get().optionsUnitCuantity; 
		this.startingDensitySpeed = this.densitySpeed = (float) (0.2f + 0.1*Preferences.Get().optionsUnitEatSpeed);
		
		this.totalDensity = 0;
		this.previousDensity = initialDensity;
		this.fightRecord = new CircularBuffer(fightRecordLength, 1);
		
		// Color
		this.colorIndex = colorIndex;
		this.powerUpBeingRenderedIndex = 0;
		this.powerUpRenderingStartTimeMillis = SystemClock.elapsedRealtime();
		
		// PowerUps
		this.powerUps = new Vector<PowerUp>();
		this.isSlowed = false;
		this.isFaster = false;
		this.isSlowPowerUpApplied = false;
		
		// Density update
		this.lastTimeDensityUpdated = 0;
		
	}
	
	/**
	 * Sets a random initial position for the player, inside map bounds.
	 */
	public void SetCursorInitialPos()
	{
		int wMargin = Preferences.Get().mapWidth/10;
		int wArea = Preferences.Get().mapWidth*8/10;
		
		int hMargin = Preferences.Get().mapHeight /10;
		int hArea = Preferences.Get().mapHeight *8/10;
		
		Random gen = new Random();
		int x = gen.nextInt(wArea) + wMargin;
		int y = gen.nextInt(hArea) + hMargin;
		
		this.cursor.SetPosition( x, y);		
	}
	
	/**
	 * Finds the closest, empty, full capacity tile to select as a starting tile.
	 * Once found, fill it as the initial tile.
	 */
	public void SetInitialTile(Map mapRef)
	{
		this.mapRef = mapRef;
		
		Tile initialTile = null;
		while(initialTile == null)
		{
			initialTile = this.mapRef.GetClosestEmptyTile((int)this.cursor.GetPosition().X(), (int)this.cursor.GetPosition().Y(),10);
			if(initialTile == null)
			{
				// We give it a new random start position and try again
				SetCursorInitialPos();
			}
		}
		
		initialTile.AddDensity(this, initialDensity);	
		
		Log.i("Player" + GetID(), "Initial tile: " + initialTile.GetPos().X() + ", " + initialTile.GetPos().Y());
	}
	
	/**
	 * Links the player with the provided tile by adding it to it's tile vector
	 * @param tile Tile to link
	 */
	public void LinkTile(Tile tile)
	{
		this.tiles.add(tile);
	}
	
	/**
	 * Unlinks the player with the provided tile by removing it from its tile vector
	 * @param tile Tile to unlink
	 */
	public void UnlinkTile(Tile tile)
	{
		this.tiles.remove(tile);
	}
	
	/**
	 * Links the player with the provided PowerUp by adding it to it's PowerUp vector
	 * @param powerUp PowerUp to link
	 */
	public void LinkPowerUp(PowerUp powerUp)
	{
		//if it's the only PowerUp applied set the rendering start time
		/*if(this.powerUps.isEmpty() && !IsSlowed()){
			this.powerUpRenderingStartTimeMillis = SystemClock.elapsedRealtime();
		}*/
		if(powerUp.GetType() == 0) //if it's a speed powerup
		{
			this.isFaster = true;
		}
		else if(powerUp.GetType() == 2){
			this.isSlowPowerUpApplied = true;
		}
		this.powerUps.add(powerUp);
	}
	
	/**
	 * Unlinks the player with the provided PowerUp by removing it from it's PowerUp vector.
	 * @param powerUp
	 */
	public void UnlinkPowerUp(PowerUp powerUp)
	{
		this.powerUps.remove(powerUp);
		
		this.isFaster = false;
		this.isSlowPowerUpApplied = false;
		for(int i=0; i < this.powerUps.size(); i++){
			if(this.powerUps.elementAt(i).GetType() == 0){
				this.isFaster = true;
			}
			else if(this.powerUps.elementAt(i).GetType() == 2){
				this.isSlowPowerUpApplied = true;
			}
		}
		
		//if there are no PowerUps applied reset the rendering start time
		/*if(powerUps.isEmpty() && !IsSlowed()){
			this.powerUpRenderingStartTimeMillis = 0;
		}*/
	}
	
	/**
	 * Does naught
	 */
	public void Start()
	{
	}
	
	/**
	 * Updates the player logic. 
	 * Requires cursor and input decide active and working
	 */
	public void Update()
	{
		int prevDensity = this.previousDensity;
		
		this.cursor.Update();
		this.inputDevice.Update();
		if(tileUpdateRegulator.IsReady())
		{
			UpdateTiles();			
			//Log.i("Player" + GetID(), " Tiles: " + this.tiles.size() + ", density: " + this.totalDensity);
		}
		UpdatePowerUps();
		
		if(prevDensity != this.totalDensity)
		{
			this.lastTimeDensityUpdated = System.currentTimeMillis();
		}
	}
	
	/**
	 * Updates the PowerUps if the player has any
	 */
	private void UpdatePowerUps() 
	{
		for(int i= 0; i < powerUps.size(); i++)
		{
			powerUps.elementAt(i).PlayerUpdate();
		}		
	}

	/**
	 * Updates all the tiles the player has.
	 */
	private void UpdateTiles()
	{
		// For every tile move the density
		for(int i = this.tiles.size()-1; i >= 0; i--)
		{
			this.tiles.elementAt(i).MoveDensity();
		}
		
		// Once updated, eliminate those with no density
		for(int i = 0; i < this.tiles.size(); i++)
		{
			if(tiles.elementAt(i).HasToUnlink(GetID()))
			{
				tiles.elementAt(i).Unlink(GetID());
			}
		}

		this.previousDensity = this.totalDensity;
		this.totalDensity = 0;
		for(int i = 0; i < this.tiles.size(); i++)
		{
			AddToTotalDensityCount( this.tiles.elementAt(i).GetDensityFrom(GetID()));
		}
		
		if(previousDensity > 0)
		{
			float difference = (previousDensity - totalDensity);
			
			// Empirical numbers. Put it on the [-330, 300] line, move it to 0, normalize between 0 and 1
			difference += 300;
			difference /= 600;
			
			float ratio = Math.min(1, difference);
			ratio = Math.max(0, ratio);
			
			fightRecord.Store(ratio);
		}
	}
	
	/**
	 * Adds the specified density to the total density the player has
	 * @param density
	 */
	public void AddToTotalDensityCount(int density)
	{
		this.totalDensity += density;
	}
	
	/**
	 * Prepares the tiles for the next update step
	 */
	public void Prepare()
	{
		for(int i = 0; i < this.tiles.size(); i++)
		{
			this.tiles.elementAt(i).Prepare();
		}
	}

	/**
	 * Gets the current player cursor
	 * @return cursor
	 */
	public Cursor GetCursor() { return this.cursor; }
	
	/**
	 * Gets the player ID relative to the PlayScene.
	 * @return the id
	 */
	public int GetID() { return this.playerNumber; }
	
	/**
	 * Gets the tiles occupied by the player.
	 * @return the tiles occupied
	 */
	public Vector<Tile> GetTiles() { return this.tiles; }
	
	/**
	 * Gets color index of the player.
	 * @return the color index
	 */
	public int GetColorIndex() { return this.colorIndex; }
	
	/**
	 * Gets powerUps of the player.
	 * @return the powerUps
	 */
	public Vector<PowerUp> GetPowerUps() { return powerUps; }

	/**
	 * Gets a value indicating whether the player is human.
	 * @return if is human.
	 */
	public boolean IsHuman() { return this.humanPlayer; }
	
	/**
	 * Gets the total density the player has
	 * @return The total density.
	 */
	public int GetTotalDensity() {return this.totalDensity; }
	
	/**
	 * Gets the average of the fight record
	 * @return The average fight record of the player
	 */
	public float GetAverageFightRecord() {return this.fightRecord.GetAverage(); }
	
	/**
	 * Gets a value indicating whether the player is slowed.
	 * @return if is slowed.
	 */
	public boolean IsSlowed() { return this.isSlowed; }
	
	/**
	 * Gets the time when the PowerUp started being rendered
	 * @return time when the PowerUp started being rendered
	 */
	public long GetPowerUpRenderingStartTimeMillis() { return this.powerUpRenderingStartTimeMillis; }
	
	/**
	 * Gets the index of the PowerUp being rendered
	 * @return index of the PowerUp being rendered
	 */
	public int GetPowerUpBeingRenderedIndex() { return this.powerUpBeingRenderedIndex; }
	
	/**
	 * Gets a value indicating whether a slow PowerUp is applied.
	 * @return if a slow PowerUp is applied.
	 */
	public boolean IsSlowPowerUpApplied() { return this.isSlowPowerUpApplied; }
	
	/**
	 * Sets the index of the PowerUp being rendered
	 * @param powerUpBeingRenderedIndex index of the PowerUp being rendered
	 */
	public void SetPowerUpBeingRenderedIndex(int powerUpBeingRenderedIndex) 
	{ 
		this.powerUpBeingRenderedIndex = powerUpBeingRenderedIndex; 
		this.powerUpRenderingStartTimeMillis = SystemClock.elapsedRealtime();
	}
	
	/**
	 * Gets the density speed of the player, clamped between 1.0 and 0.0
	 * @return The clamped density speed of the player
	 */
	public float GetDensitySpeed() 
	{ 
		float top = (float) Math.min(this.densitySpeed, 1.0);
		float bottom = (float) Math.max(top, 0.0f);
		return bottom; 
	}
	
	/**
	 * Adds a quantity to the current density speed and checks if the player as been slowed.
	 * @param quantity
	 */
	public void EditDensitySpeed(float quantity)
	{
		this.densitySpeed += quantity;
		
		//keep record of whether the player has been slowed or not
		if (this.densitySpeed < this.startingDensitySpeed || (this.densitySpeed == this.startingDensitySpeed && this.isFaster)){
			this.isSlowed = true;
			
			//if it's the only PowerUp applied set the rendering start time
			/*if(this.powerUps.isEmpty()){
				this.powerUpRenderingStartTimeMillis = SystemClock.elapsedRealtime();
			}*/
		}
		else{
			this.isSlowed = false;
			
			//if there are no PowerUps applied reset the rendering start time
			/*if (this.powerUps.isEmpty()){
				this.powerUpRenderingStartTimeMillis = 0;
			}*/
		}
		Log.i("Player","slowed: " + this.isSlowed + ", Faster: " + this.isFaster);
	}
	
	/**
	 * Gets the InputDevice reference of the player.
	 * @return The players InputDevice
	 */
	public InputDevice GetInputDevice() { return this.inputDevice; }

	/**
	 * Gets the last time the amount of density had any changes
	 * @return The last time the amount of density had any changes.
	 */
	public long LastTimeDensityChanged() 
	{
		return this.lastTimeDensityUpdated;
	}
}
