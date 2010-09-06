package com.game;

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
		
		this.cursor = new Cursor(this);
		// TODO: Set a real position for the cursor;
		this.cursor.SetPosition(20, 20);
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
