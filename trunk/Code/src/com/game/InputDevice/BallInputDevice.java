package com.game.InputDevice;

import com.game.MsgType;
import com.game.Vec2;
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
	 * Modifies the ball input to give it a little more force.
	 */
	private static final int SCALE_FACTOR = 5;

	/**
	 * Creates the Handler and assigns it.
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
					
					// Inverted Y to go from android top left to opengl bottom left
					parent.GetCursor().MoveInDirection(new Vec2(event.getX() * SCALE_FACTOR, (-1)* event.getY() * SCALE_FACTOR));
				}
			}
		};
		
		if(playScene.trackballEvent != null)
		{
			Log.e("BallInputDevice", "Trackball handler is not null! Some other ID ahs already claimed it");
		}
		playScene.trackballEvent = this.deviceHandler;
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
