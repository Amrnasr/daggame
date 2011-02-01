package com.game.InputDevice;

import com.game.MsgType;
import com.game.Preferences;
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
	private int scaleFactor = 50;

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
					
					// Inverted Y to go from android top left to opengl bottom left
					Vec2 dir = new Vec2(event.getX(),(-1)* event.getY());
					dir.Normalize();
					dir.Scale(scaleFactor);
					parent.GetCursor().MoveInDirection(dir);
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
		scaleFactor = Math.max(Preferences.Get().mapHeight/10, scaleFactor);
	}

	@Override
	public void Update() 
	{		

	}

}
