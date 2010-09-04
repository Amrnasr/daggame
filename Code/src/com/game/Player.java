package com.game;

import com.game.InputDevice.InputDevice;

/**
 * Stub class so I can create the Tiles
 * @author Ying
 *
 */
public class Player 
{
	private InputDevice inputDevice;
	
	private int playerNumber;
	
	public Player(int playerNumber, InputDevice inputDevice)
	{
		this.playerNumber = playerNumber;
		
		this.inputDevice = inputDevice;
		this.inputDevice.SetParent(this);
	}
	
	public void Start()
	{
		
	}
	
	public void Update()
	{
		
	}


}
