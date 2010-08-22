package com.game.Scenes;

import com.game.MsgType;
import com.game.R;
import com.game.DagActivity.SceneType;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Specific scene for the "Menu" screen.
 * @author Ying
 *
 */
public class MenuScene extends Scene 
{
	/**
	 * Initializes and sets the callback for the handler.
	 */
	public MenuScene()
	{
		super();
		
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {
	        	// If a menu button is clicked, find out which and do something about it.
	        	if(msg.what == MsgType.BUTTON_CLICK.ordinal())
	        	{
	        		handleButtonClick(msg.arg1);
	        	}
	        	// If the activity tells us to stop, we stop.
	        	else if(msg.what == MsgType.STOP_SCENE.ordinal())
	        	{
	        		runScene = false;
	        	}
	        	
	        }
	    };
	}
	
	/**
	 * Given which button has been pressed, do something
	 * @param which button to react to
	 */
	private void handleButtonClick(int which)
	{
		switch (which) 
    	{
		case R.id.single_but:
			Log.i("MenuScene", "Single button handler called");
			actHandlerRef.sendMessage(actHandlerRef.obtainMessage(
					MsgType.ACTIVITY_CHANGE_SCENE.ordinal(), 
    				SceneType.SINGLE_SCENE.ordinal(), 0));
			break;
		case R.id.multi_but:
			Log.i("MenuScene", "Multi button handler called");
			actHandlerRef.sendMessage(actHandlerRef.obtainMessage(
					MsgType.ACTIVITY_CHANGE_SCENE.ordinal(), 
    				SceneType.MULTI_SCENE.ordinal(), 0));
			break;
		case R.id.options_but:
			Log.i("MenuScene", "Options button handler called");
			actHandlerRef.sendMessage(actHandlerRef.obtainMessage(
					MsgType.ACTIVITY_CHANGE_SCENE.ordinal(), 
    				SceneType.OPTIONS_SCENE.ordinal(), 0));
			break;
		case R.id.about_but:
			Log.i("MenuScene", "About button handler called");
			actHandlerRef.sendMessage(actHandlerRef.obtainMessage(
					MsgType.ACTIVITY_CHANGE_SCENE.ordinal(), 
    				SceneType.ABOUT_SCENE.ordinal(), 0));
			break;
		case R.id.how_but:
			Log.i("MenuScene", "How button handler called");
			actHandlerRef.sendMessage(actHandlerRef.obtainMessage(
					MsgType.ACTIVITY_CHANGE_SCENE.ordinal(), 
    				SceneType.HOW_SCENE.ordinal(), 0));
			break;
		default:
			Log.e("MenuScene", "No handler options for that message!!");
			break;
		}
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
	 * It's a passive scene, so just update the profiler.
	 */
	@Override
	public void Update() 
	{
		actHandlerRef.sendEmptyMessage(MsgType.UPDATE_PROFILER.ordinal());
	}

}
