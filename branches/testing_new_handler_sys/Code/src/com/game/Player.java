package com.game;

import java.util.Random;

import android.util.Log;

import com.game.InputDevice.InputDevice;

/**
 * Player class, a player has a Input mode and a Cursor to controll it's army.
 * @author Ying
 *
 */
public class Player 
{
	// Input device for controlling this player
	private InputDevice inputDevice;
	
	// Player cursor for moving the army
	private Cursor cursor;
	
	// Unique identifier for the player. It will come handy eventually, I can assure you.
	private int playerNumber;
	
	/**
	 * Creates a new instance of the Player class.
	 * @param playerNumber Unique player identifier. If it's not unique you'll regret it later.
	 * @param inputDevice Input device used by this player.
	 */
	public Player(int playerNumber, InputDevice inputDevice)
	{
		this.playerNumber = playerNumber;
		
		// Cursor
		// TODO: This will have to change to wait for map load.
		int wMargin = Preferences.Get().mapWidth/10;
		int wArea = Preferences.Get().mapWidth*8/10;
		
		int hMargin = Preferences.Get().mapHeight /10;
		int hArea = Preferences.Get().mapHeight *8/10;
		
		this.cursor = new Cursor(this);
		Random gen = new Random();
		this.cursor.SetPosition(gen.nextInt(wArea) + wMargin, gen.nextInt(hArea) + hMargin);
		
		this.inputDevice = inputDevice;
		this.inputDevice.SetParent(this);		
	}
	
	public void Start()
	{
		
	}
	
	public void Update()
	{
		this.cursor.Update();
		this.inputDevice.Update();
	}

	/**
	 * Gets the current player cursor
	 * @return cursor
	 */
	public Cursor GetCursor() { return this.cursor; }

}
