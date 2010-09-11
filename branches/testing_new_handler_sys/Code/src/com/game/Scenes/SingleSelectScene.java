package com.game.Scenes;

import com.game.MsgType;
import com.game.Preferences;
import com.game.R;
import com.game.DagActivity.SceneType;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * A specific scene for the "Single Player" screen.
 * @author NeoM
 *
 */

public class SingleSelectScene extends Scene {
	/**
	 * Initializes and sets the handler callback.
	 */
	public SingleSelectScene()
	{		
		super();
	
		Preferences.Get().multiplayerGame = false;
		
		this.handler = new Handler()
		{
			public void handleMessage (Message msg){
				// If a menu button is clicked, find out which and do something about it.
	        	if(msg.what == MsgType.BUTTON_CLICK.ordinal())
	        	{
	        		handleButtonClick(msg.arg1);
	        	}
	        	// If a checkbox is clicked, find out which and do something about it.
	        	else if(msg.what == MsgType.CHECKBOX_CLICK.ordinal()){
	        		handleCheckBoxClick(msg.arg1,msg.arg2);
	        	}
	        	// If a gallery item is clicked, find out which gallery and gallery item and do something about it.
	        	else if(msg.what == MsgType.GALLERY_ITEM_CLICK.ordinal()){
	        		handleGalleryItemClick(msg.arg1,msg.arg2);
	        	}
	        	// If a spinner item is clicked, find out which spinner and spinner item and do something about it.
	        	else if(msg.what == MsgType.SPINNER_ITEM_CLICK.ordinal()){
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

	/**
	 * Given which button has been pressed, do something
	 * @param which button to react to
	 */
	private void handleButtonClick(int which)
	{
		switch (which) 
    	{	
    	case R.id.ok_single_but:
			Log.i("SingleSelectScene", "Ok button handler called");
			actHandlerRef.sendMessage(actHandlerRef.obtainMessage(MsgType.ACTIVITY_CHANGE_SCENE.ordinal(), 
    				SceneType.PLAY_SCENE.ordinal(), 0));
    		break;
    	case R.id.back_single_but:
			Log.i("SingleSelectScene", "Back button handler called");
			actHandlerRef.sendMessage(actHandlerRef.obtainMessage(MsgType.ACTIVITY_CHANGE_SCENE.ordinal(), 
    				SceneType.MENU_SCENE.ordinal(), 0));
    		break;
    	default:
			Log.e("SingleSelectScene", "No handler options for that message!!");
			break;		
    	}
	}
	
	/**
	 * Given which checkbox has been pressed, do something
	 * @param which checkbox to react to
	 */
	private void handleCheckBoxClick(int which, int checked)
	{
		switch (which) 
    	{	
		case R.id.minimap_single_check:
			Log.i("SingleSelectScene", "Minimap checkbox handler called");
			Preferences.Get().singleShowMinmap = (checked != 0);
			break;
		case R.id.powerups_single_check:
			Log.i("SingleSelectScene", "Power-ups checkbox handler called");
			Preferences.Get().singlePowerups = (checked != 0);
			break;
    	default:
			Log.e("SingleSelectScene", "No handler options for that message!!");
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
		case R.id.maps_single_gal:
			Log.i("SingleSelectScene", "Maps gallery handler called");
			Preferences.Get().singleCurrentMap = position;
			break;
    	default:
			Log.e("SingleSelectScene", "No handler options for that message!!");
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
		case R.id.color_single_spin:
			Log.i("SingleSelectScene", "Color spinner handler called");
			Preferences.Get().singlePlayer1Color = position;
			break;
		case R.id.op_single_spin:
			Log.i("SingleSelectScene", "Opponents spinner handler called");
			Preferences.Get().singleNumberOpponents = position;
			break;
		case R.id.control_single_spin:
			Log.i("SingleSelectScene", "Control spinner handler called");
			Preferences.Get().singleControlMode = position;
			break;
		default:
			Log.e("SingleSelectScene", "No handler options for that message!!");
			break;	
    	}
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
	
	@Override
	public void End() 
	{
		// Notify the activity to save the preferences, in case they have changed.
		actHandlerRef.sendMessage(actHandlerRef.obtainMessage(MsgType.ACTIVITY_SAVE_PREFERENCES.ordinal()));
	}
}
