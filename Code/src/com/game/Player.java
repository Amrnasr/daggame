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
	
	private int totalDensity;
	
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
		
		// Tiles
		tiles = new Vector<Tile>();
		tileUpdateRegulator = new Regulator(5); // TODO: Put a decent update speed.
		totalDensity = 0;
		
		// TODO: Read from preferences!
		this.initialDensity = 3000;
		
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
	
	public void LinkTile(Tile tile)
	{
		this.tiles.add(tile);
	}
	
	/**
	 * Does naught
	 */
	public void Start()
	{
		
	}
	
	/**
	 * Updates the player logic. 
	 * Requires cursor and input decice active and working
	 */
	public void Update()
	{
		this.cursor.Update();
		this.inputDevice.Update();
		if(tileUpdateRegulator.IsReady())
		{
			UpdateTiles();
			if(GetID() == 0)
			{
				Log.i("Player" + GetID(), " Tiles: " + this.tiles.size() + ", density: " + this.totalDensity);
			}
		}
	}
	
	private void UpdateTiles()
	{
		// For every tile move the density
		for(int i = this.tiles.size()-1; i >= 0; i--)
		{
			this.tiles.elementAt(i).MoveDensity();
		}
		
		// Fight the density
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

		// DEBUG
		this.totalDensity = 0;
		for(int i = 0; i < this.tiles.size(); i++)
		{
			AddToTotalDensityCount( this.tiles.elementAt(i).GetDensityFrom(GetID()));
		}
	}
	
	public void AddToTotalDensityCount(int density)
	{
		this.totalDensity += density;
	}
	
	public void UnlinkTile(Tile tile)
	{
		this.tiles.remove(tile);
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
}
