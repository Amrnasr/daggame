package com.game.InputDevice;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.game.MsgType;
import com.game.Vec2;
import com.game.Scenes.PlayScene;

public class JoystickInputDevice extends InputDevice 
{

	Vec2 mainCirclePos;
	Vec2 dirCirclePos;
	
	public JoystickInputDevice(PlayScene playScene) 
	{
		super(playScene);
		
		// TODO: Set this positions relative to device size
		this.mainCirclePos = new Vec2(10,165);
		this.dirCirclePos = new Vec2(50, 200);
		
		this.deviceHandler = new Handler()
		{			
			public void handleMessage(Message msg)
			{
				
			}
		};
	}

	@Override
	public void Start() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void Update() 
	{
		// TODO Auto-generated method stub

	}
	
	/**
	 * Gets the main circle position
	 * @return The main circle position
	 */
	public Vec2 GetMainCirclePos() { return this.mainCirclePos; }
	
	/**
	 * Gets the direction circle position
	 * @return The direction circle position
	 */
	public Vec2 GetDirCirclePos() { return this.dirCirclePos; }

}
