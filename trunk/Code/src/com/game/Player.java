package com.game;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

import android.util.Log;

import com.game.InputDevice.InputDevice;

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
	 * Indicates wherether the player is human controled.
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
	 * TODO: Read from preferences!!
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
	private final static int fightRecordLength = 60;
	
	/**
	 * Index of the color to be used to render this player's army
	 */
	private int colorIndex;
	
	/**
	 * Creates a new instance of the Player class.
	 * @param playerNumber Unique player identifier. If it's not unique you'll regret it later.
	 * @param inputDevice Input device used by this player.
	 * @param humanPlayer true if it's a human player, false if it's IA
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
		tileUpdateRegulator = new Regulator(5); // TODO: Put a decent update speed.
		
		// TODO: Read from preferences!
		this.initialDensity = 3000;
		
		this.totalDensity = 0;
		this.previousDensity = initialDensity;
		this.fightRecord = new CircularBuffer(fightRecordLength, 1);
		
		// Color
		this.colorIndex = colorIndex;
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
		
		Queue<Tile> toSearch = new LinkedList<Tile>();
		toSearch.add(mapRef.AtWorld((int)this.cursor.GetPosition().X(), (int)this.cursor.GetPosition().Y()));
		
		Tile initialTile = null;
		
		while(!toSearch.isEmpty())
		{
			Tile aux = toSearch.remove();
			
			// If it's a max capacity tile, and empty
			if(aux.GetCurrentCapacity() == Tile.TileMaxCapacity())
			{
				// Found our initial tile
				initialTile = aux;
				toSearch.clear();
			}
			else
			{
				// Keep looking, look at all the 8 tiles around
				int x = (int) aux.GetPos().X();
				int y = (int) aux.GetPos().Y();
				
				for(int i = -1; i < 2; i++)
				{
					for(int j = -1; j < 2; j++)
					{
						Tile newSuspect = mapRef.AtTile(x+i, y+j);
						if(newSuspect != null)
						{
							toSearch.add(newSuspect);
						}
					}
				}
			}
		}
		
		if(initialTile == null)
		{
			Log.e("Player " + GetID(), "NOT FOUND INITIAL TILE!");
		}
		
		initialTile.AddDensity(this, initialDensity);	
		//this.tiles.add(initialTile);
		
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
		this.cursor.Update();
		this.inputDevice.Update();
		if(tileUpdateRegulator.IsReady())
		{
			UpdateTiles();			
			//Log.i("Player" + GetID(), " Tiles: " + this.tiles.size() + ", density: " + this.totalDensity);
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
		
		// Fight the density
		// TODO: Remove if not needed in the final version (fighting in movement)
		for(int i = 0; i < this.tiles.size(); i++)
		{
			tiles.elementAt(i).DensityFight();
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
			fightRecord.Store((float)((float)(totalDensity)/(float)(previousDensity)));
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
}
