package com.game.Scenes;

import android.os.Handler;
import android.os.Message;

import com.game.MessageHandler;
import com.game.MsgType;
import com.game.Preferences;
import com.game.R;
import com.game.DagActivity.SceneType;
import com.game.MessageHandler.MsgReceiver;


/**
 * A specific scene for the "Options" screen.
 * @author Ying
 *
 */
public class OptionsScene extends Scene 
{
	/**
	 * Initializes and sets the handler callback.
	 */
	public OptionsScene() 
	{
		super();
		
		this.handler = new Handler() 
		{
	        public void handleMessage(Message msg) 
	        {
	        	
	        	// If the "ok" button (which is the only button in the scene) is clicked, go back to the menu
	        	if(msg.what == MsgType.BUTTON_CLICK.ordinal())
	        	{
	        		MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.ACTIVITY_CHANGE_SCENE, SceneType.MENU_SCENE.ordinal());	        		
	        	}
	        	if(msg.what == MsgType.CHECKBOX_CLICK.ordinal())
	        	{
	        		// Some operator magic to turn a int into a boolean
	        		Preferences.Get().optionsSoundMute = (msg.arg2 != 0);
	        	}
	        	if(msg.what == MsgType.SPINNER_ITEM_CLICK.ordinal())
	        	{
	        		if(msg.arg1 == R.id.eat_speed_spin)
	        		{
	        			Preferences.Get().optionsUnitEatSpeed = msg.arg2;
	        		}
	        		if(msg.arg1 == R.id.move_speed_spin)
	        		{
	        			Preferences.Get().optionsUnitMoveSpeed = msg.arg2;
	        		}
	        		if(msg.arg1 == R.id.num_units_spin)
	        		{
	        			Preferences.Get().optionsUnitCuantity = msg.arg2;
	        		}
	        	}
	        	// If the activity tells us to stop, we stop.
	        	if(msg.what == MsgType.STOP_SCENE.ordinal())
	        	{
	        		runScene = false;
	        	}
	        	
	        }
	    };
	}

	@Override
	public void End() 
	{
		// Notify the activity to save the preferences, in case they have changed.
		MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.ACTIVITY_SAVE_PREFERENCES);	
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
		MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.UPDATE_PROFILER);
	}

}
