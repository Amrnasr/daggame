package com.game.Scenes;

import com.game.MsgType;
import android.app.Activity;
import android.os.Handler;

/**
 * Base class for any scene in the game. Scenes are owned and controlled by the 
 * scene manager.
 * 
 * @author Ying
 *
 */
public abstract class Scene 
{
	/// Handler for messages to the scene
	protected Handler handler = null;
	
	/// Reference handle for the Activity (to send messages to the activity)
	protected Handler actHandlerRef = null;
	
	/// Variables to ensure thread safety on scene shutdown
	protected boolean 	runScene;
	private boolean 	stopScene;
	
	// Reference to the activity to load resources.
	protected Activity refActivity = null;
	
	/**
	 * Initializes variables
	 */
	public Scene()
	{
		runScene = true;
		stopScene = false;
	}
	
	/**
	 * Override in the base class. Called on scene start.
	 */
	public abstract void Start();
	
	/**
	 * Override in the base class. Called each update cycle of the game logic.
	 */
	public abstract void Update();
	
	/**
	 * Override in the base class. Called on scene end.
	 */
	public abstract void End();
	
	/**
	 * Gets the message handler
	 * @return message handler.
	 */
	public Handler getHandler(){return handler;}
	
	/**
	 * Sets the activity handler reference.
	 * @param handleRef handle to set to
	 */
	public void setActivityHandlerRef(Handler handleRef)
	{ 
		this.actHandlerRef = handleRef;
	}
	
	/**
	 * Updates the scene only if the activity has not asked us to stop.
	 * If we have been asked to stop, notify the activity (only once, remember
	 * that until the scene is swapped this code is called each frame) when
	 * we are ready.
	 */
	public void safeUpdate()
	{
		if(runScene)
		{
			this.Update();
		}
		else
		{
			if(!stopScene)
			{
				stopScene = true;
				actHandlerRef.sendEmptyMessage(MsgType.SCENE_STOPED_READY_FOR_CHANGE.ordinal());
			}
		}
	}

	public void setRefActivity(Activity refActivity) 
	{
		this.refActivity = refActivity;
	}
	
}
