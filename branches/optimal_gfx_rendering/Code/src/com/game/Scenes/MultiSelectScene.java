package com.game.Scenes;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.game.MessageHandler;
import com.game.MsgType;
import com.game.Preferences;
import com.game.R;
import com.game.DagActivity.SceneType;
import com.game.MessageHandler.MsgReceiver;

/**
 * Specific scene for the "Multiplayer" screen.
 * @author NeoM
 *
 */

public class MultiSelectScene extends Scene {
	/**
	 * Initializes and sets the callback for the handler.
	 */
	public MultiSelectScene(){
		super();
		
		Preferences.Get().multiplayerGame = true;
		
		this.handler = new Handler()
		{
			public void handleMessage (Message msg){
				// If a menu button is clicked, find out which and do something about it.
	        	if(msg.what == MsgType.BUTTON_CLICK.ordinal())
	        	{
	        		//Log.i("Multi", "Handle button");
	        		handleButtonClick(msg.arg1);
	        	}
	        	// If a checkbox is clicked, find out which and do something about it.
	        	else if(msg.what == MsgType.CHECKBOX_CLICK.ordinal()){
	        		//Log.i("Multi", "Handle checkbox");
	        		handleCheckBoxClick(msg.arg1,msg.arg2);
	        	}
	        	// If a gallery item is clicked, find out which gallery and gallery item and do something about it.
	        	else if(msg.what == MsgType.GALLERY_ITEM_CLICK.ordinal()){
	        		//Log.i("Multi", "Handler gallery");
	        		handleGalleryItemClick(msg.arg1,msg.arg2);
	        	}
	        	// If a spinner item is clicked, find out which spinner and spinner item and do something about it.
	        	else if(msg.what == MsgType.SPINNER_ITEM_CLICK.ordinal()){
	        		//Log.i("Multi", "Handle spinner");
	        		handleSpinnerItemClick(msg.arg1,msg.arg2);
	        	}
	        	// If the activity tells us to stop, we stop.
	        	else if(msg.what == MsgType.STOP_SCENE.ordinal())
	        	{
	        		runScene = false;
	        	}
			}
		};
	}
	
	private void handleButtonClick(int which)
	{
		switch (which) 
    	{	
    	case R.id.ok_multi_but:
    		//Log.i("MultiSelectScene", "Ok button handler called");
			MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.ACTIVITY_CHANGE_SCENE, SceneType.PLAY_SCENE.ordinal());
    		break;
    	case R.id.back_multi_but:
    		//Log.i("MultiSelectScene", "Back button handler called");
			MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.ACTIVITY_CHANGE_SCENE, SceneType.MENU_SCENE.ordinal());
    		break;
    	default:
    		//Log.e("MultiSelectScene", "No handler options for that message!!");
			break;		
    	}
	}
	
	/**
	 * Given which checkbox has been pressed, do something
	 * @param which checkbox to react to
	 */
	private void handleCheckBoxClick(int which, int checked){
		switch (which) 
    	{	
		case R.id.minimap_multi_check:
			//Log.i("MultiSelectScene", "Minimap checkbox handler called");
			Preferences.Get().multiShowMinimap = (checked != 0);
			break;
		case R.id.powerups_multi_check:
			//Log.i("MultiSelectScene", "Power-ups checkbox handler called");
			Preferences.Get().multiPowerups = (checked != 0);
			break;
    	default:
    		//Log.e("MultiSelectScene", "No handler options for that message!!");
			break;		
    	}
	}
	
	/**
	 * Given which gallery item of which gallery has been pressed, do something
	 * @param which gallery to react to
	 * @param position position of the gallery item pressed
	 */
	private void handleGalleryItemClick(int which, int position){
		switch (which) 
    	{	
		case R.id.maps_multi_gal:
			//Log.i("MultiSelectScene", "Maps gallery handler called");
			Preferences.Get().multiCurrentMap = position;
			break;
    	default:
    		//Log.e("MultiSelectScene", "No handler options for that message!!");
			break;		
    	}
	}
	
	/**
	 * Given which spinner item of which spinner has been pressed, do something
	 * @param which spinner to react to
	 * @param position position of the spinner item pressed
	 */
	private void handleSpinnerItemClick(int which, int position){
		switch (which) 
    	{	
		case R.id.color1_multi_spin:
			//Log.i("MultiSelectScene", "Color 1 spinner handler called");
			Preferences.Get().multiPlayer1Color = position;
			break;
		case R.id.color2_multi_spin:
			//Log.i("MultiSelectScene", "Color 2 spinner handler called");
			Preferences.Get().multiPlayer2Color = position;
			break;
		case R.id.op_multi_spin:
			//Log.i("MultiSelectScene", "Opponents spinner handler called");
			Preferences.Get().multiNumberOpponents = position; // Because pos == 0 means numb oponents = 1
			break;
			/*
		case R.id.control_multi_spin:
			Log.i("MultiSelectScene", "Control spinner handler called");
			Preferences.Get().multiControlMode = position;
			break;
			*/
		default:
			//Log.e("MultiSelectScene", "No handler options for that message!!");
			break;	
    	}
	}
	
	@Override
	public void Start() {

	}

	@Override
	public void Update() {
		MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.UPDATE_LOGIC_PROFILER);
	}

	@Override
	public void End() 
	{
		// Notify the activity to save the preferences, in case they have changed.
		MessageHandler.Get().Send(MsgReceiver.ACTIVITY, MsgType.ACTIVITY_SAVE_PREFERENCES);		
	}

}
