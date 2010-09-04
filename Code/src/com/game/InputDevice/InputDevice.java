package com.game.InputDevice;

import com.game.Player;
import com.game.Scenes.PlayScene;

import android.os.Handler;

public abstract class InputDevice {
	
	protected Player parent;
	protected Handler deviceHandler;
	
	public InputDevice(PlayScene playScene)
	{
		this.parent = null;
		setDeviceHandler(null);
	}

	public void SetParent(Player parent)
	{
		this.parent = parent;
	}

	public void setDeviceHandler(Handler deviceHandler) 
	{
		this.deviceHandler = deviceHandler;
	}

	public Handler getDeviceHandler() 
	{
		return deviceHandler;
	}
	
	public abstract void Start();
	public abstract void Update();
}
