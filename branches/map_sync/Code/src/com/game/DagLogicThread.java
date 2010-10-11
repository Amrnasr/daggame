package com.game;

import com.game.DagActivity.SceneType;
import com.game.Scenes.Scene;
import com.game.Scenes.SceneManager;

import android.app.Activity;
import android.util.Log;


/**
 * Thread that takes care of all the logic of the game
 * @author Ying
 *
 */
public class DagLogicThread extends Thread 
{
	/**
	 * Keeps the hearth beating! Jokes aside, it's the while() condition for the run function.
	 * volatile so it's aware that this value might be modified by another thread, so don't cache it
	 */
	private volatile boolean gameRuning;
	
	/**
	 * Controls all scenes in general, the current one in particular.
	 */
	private SceneManager sceneManager;
	
	/**
	 * Creates the thread object and initializes default values
	 */
	public DagLogicThread()
	{
		this.gameRuning = true;
		this.sceneManager = new SceneManager();
	}
	
	/**
	 * Called each update loop.
	 * Updates the logic thread.
	 */
	@Override public void run()
	{
		while(gameRuning)
		{
			if(sceneManager != null)
			{
				sceneManager.Update();
			}
			else
			{
				Log.i("DagLogicThread", "No scene manager yet!!");
			}			
		}
	}
	
	/**
	 * Stops the game logic and therefore ends the thread.
	 */
	public void stopGame()
	{
		gameRuning = false;
	}
	
	/**
	 * Sets the current scene. DO NOT CALL DIRECTLY from a scene!!!. 
	 * Let the Activity do it, use the ACTIVITY_CHANGE_SCENE message to change.
	 * 
	 * @param scene To change to.
	 * @throws Exception If the scene required is not available yet.  
	 */
	public void setScene(SceneType scene, Activity refActivity) throws Exception
	{
		sceneManager.ChangeScene(scene, refActivity);
	}
	
	/**
	 * Gets the current scene.
	 * @return currentScene.
	 */
	public Scene getCurrentScene()
	{
		if(sceneManager == null)
		{
			Log.i("DagLogicThread", "Scene manager not initialized yet! Call startWithScene()");
			return null;
		}
		else
		{
			return sceneManager.getCurrentScene();
		}
	}
}
