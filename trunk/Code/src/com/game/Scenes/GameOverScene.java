package com.game.Scenes;

import android.os.Handler;
import android.os.Message;

import com.game.MsgType;
import com.game.DagActivity.SceneType;

/**
 * A specific scene for the "Game Over" screen.
 * @author Ying
 *
 */
public class GameOverScene extends Scene 
{
	/**
	 * Initializes and sets the handler callback.
	 */
	public GameOverScene()
	{
		super();
		
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {
	        	// If the "ok" button (which is the only button in the scene) is clicked, go back to the menu
	        	if(msg.what == MsgType.BUTTON_CLICK.ordinal())
	        	{
	        		actHandlerRef.sendMessage(actHandlerRef.obtainMessage(MsgType.ACTIVITY_CHANGE_SCENE.ordinal(), 
	        				SceneType.MENU_SCENE.ordinal(), 0));
	        	}
	        	// If the activity tells us to stop, we stop.
	        	else if(msg.what == MsgType.STOP_SCENE.ordinal())
	        	{
	        		runScene = false;
	        	}
	        	
	        }
	    };
	}
	
	@Override
	public void End() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Start() {
		// TODO Auto-generated method stub

	}

	/**
	 * The only update done is to update the profiler in the activity.
	 */
	@Override
	public void Update() 
	{
		actHandlerRef.sendEmptyMessage(MsgType.UPDATE_PROFILER.ordinal());
	}

}
