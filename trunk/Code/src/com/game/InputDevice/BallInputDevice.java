package com.game.InputDevice;

import com.game.MsgType;
import com.game.Scenes.PlayScene;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;


/**
 * Class to handle the input of a player using the trackball
 * @author Ying
 *
 */
public class BallInputDevice extends InputDevice 
{

	/**
	 * Creates the Handler and asigns it.
	 * @param playScene
	 */
	public BallInputDevice(PlayScene playScene)
	{
		super(playScene);
		
		this.deviceHandler = new Handler()
		{			
			public void handleMessage(Message msg)
			{
				if(msg.what == MsgType.TRACKBALL_EVENT.ordinal())
				{
					MotionEvent event = (MotionEvent)msg.obj;
					Log.i("BallInputDevice", "Trackball event: " + event.getX() + ", " + event.getY());
					
					// TODO: Transform trackball input into cursor movement
				}
			}
		};
		
		if(playScene.trackballEvent != null)
		{
			Log.e("BallInputDevice", "Trackball handler is not null! Some other ID ahs already claimed it");
		}
		playScene.trackballEvent = this.deviceHandler;
		Log.i("BallInputDevice","Called constructor");
	}
	
	@Override
	public void Start() 
	{
		
	}

	@Override
	public void Update() 
	{		

	}

}
