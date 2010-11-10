package com.game.InputDevice;

import com.game.Player;
import com.game.Scenes.PlayScene;

import android.os.Handler;

/**
 * Abstract base class for all input devices in the game.
 * It's base job is to take a form of input and output Cursor movement
 * @author Ying
 *
 */
public abstract class InputDevice 
{
	/**
	 * Parent player for this InputDevice
	 */
	protected Player parent;
	
	/**
	 * Handler for messages to the input device
	 */
	protected Handler deviceHandler;
	
	/**
	 * Creates a new instance of the InputDevice class. 
	 * @param playScene is a forced param so all the base classes are forced to have the PlayScene in their constructor
	 */
	public InputDevice(PlayScene playScene)
	{
		this.parent = null;
		setDeviceHandler(null);
	}

	/**
	 * Sets the parent player
	 * @param parent player we assign this input to
	 */
	public void SetParent(Player parent)
	{
		this.parent = parent;
	}

	/**
	 * Sets the device handler of this input
	 * @param deviceHandler
	 */
	public void setDeviceHandler(Handler deviceHandler) 
	{
		this.deviceHandler = deviceHandler;
	}

	/**
	 * Returns the device handler for this input device
	 * @return device handler
	 */
	public Handler getDeviceHandler() 
	{
		return deviceHandler;
	}
	
	/**
	 * For the base class to override. Called after the creating of the InputDevice,
	 * before the Update. 
	 */
	public abstract void Start();
	
	/**
	 * For the base class to overrride. Called every update loop.
	 */
	public abstract void Update();
}
