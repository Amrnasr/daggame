package com.game.Scenes;

import android.app.Activity;
import android.util.Log;
import com.game.DagActivity.SceneType;


/**
 * Factory and manager for the Scenes. Keeps a pointer to the current scene 
 * to update and modify it.
 * 
 * @author Ying
 *
 */
public class SceneManager 
{
	/// Scene active right now, there can be one and only one.
	private Scene currentScene;
	
	/**
	 * Initializes
	 */
	public SceneManager()
	{
		this.currentScene = null;
	}
	
	/**
	 * Swaps the current scene for a new one. 
	 * Do not use directly, use ChangeScene(SceneType scene) instead, for safe creation
	 * of scenes.
	 * @param newScene Scene to change to.
	 */
	private void ChangeScene(Scene newScene, Activity refActivity)
    {
        if (currentScene!=null) 
        {
        	currentScene.End();
        }

        currentScene = newScene;
        currentScene.setRefActivity(refActivity);
        //TODO: Remove or un-comment: currentScene.Start();
    }
	
	/**
	 * Swaps a scene for the new one specified.
	 * @param scene To change to.
	 * @throws Exception If the new scene is not specified in the creation factory.
	 */
	public void ChangeScene(SceneType scene,  Activity refActivity) throws Exception
	{
		Scene newScene = null;
		
		switch (scene) 
		{
		case MENU_SCENE:
			newScene = new MenuScene();
			break;
		case SINGLE_SCENE:
			newScene = new SingleSelectScene();
			break;
		case MULTI_SCENE:
			newScene = new MultiSelectScene();
			break;
		case HOW_SCENE:
			newScene = new HowScene();
			break;
		case ABOUT_SCENE:
			newScene = new AboutScene();
			break;
		case GAMEOVER_SCENE:
			newScene = new GameOverScene();
			break;
		case OPTIONS_SCENE:
			newScene = new OptionsScene();
			break;
		case PLAY_SCENE:
			newScene = new PlayScene();
			break;
		default:
			throw new Exception("Scene to change to is not defined!");
		}
		
		ChangeScene(newScene, refActivity);		
	}
	
	/**
	 * Gets the current scene
	 * @return The current scene
	 */
	public Scene getCurrentScene()
	{
		return currentScene;
	}
	
	/**
	 * Called by the game logic thread each update loop.
	 */
	public void Update()
	{
		if(currentScene != null)
		{
			currentScene.safeUpdate();
		}
		else
		{
			Log.i("SceneManager", "No currentscene yet!");
		}
	}

}
