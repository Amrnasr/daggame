package com.game;

import android.util.Log;


/**
 * Thread that takes care of all the logic of the game
 * @author Ying
 *
 */
public class DagLogicThread extends Thread 
{
	private boolean gameRuning;
	
	/**
	 * Creates the thread object and initializes default values
	 */
	public DagLogicThread()
	{
		this.gameRuning = true;
	}
	
	/**
	 * Called each update loop.
	 */
	@Override public void run()
	{
		while(gameRuning)
		{
			Log.i("Thread", "Runnin' baby!");
			
		}
	}
	
	/**
	 * Stops the game logic and therefore ends the thread.
	 */
	public void stopGame()
	{
		gameRuning = false;
	}
}
