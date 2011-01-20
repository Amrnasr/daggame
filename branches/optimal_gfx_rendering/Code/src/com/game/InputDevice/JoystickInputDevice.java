package com.game.InputDevice;

import java.nio.FloatBuffer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.game.Camera;
import com.game.DagRenderer;
import com.game.MsgType;
import com.game.Vec2;
import com.game.Scenes.PlayScene;

public class JoystickInputDevice extends InputDevice 
{
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	private int side;
	private Vec2 mainCirclePos;
	private Vec2 smallCirclePos;
	
	// Half of the big texture width
	private final int radius = 100;
	private final int radiusOffset = radius + 20;
	
	private int screenH;
	private int screenW; 
	
	/** Buffer for the big joystick texture in ogl **/
	private static final FloatBuffer mainJoystickBuff = DagRenderer.makeFloatBuffer(new float[] 
	      { 100f, 100f, 1.0f,
			-100f, 100f, 1.0f,
			100f, -100f, 1.0f,
			-100f, -100f, 1.0f });
	
	/** Buffer for the small joystick texture in ogl **/
	private static final FloatBuffer smallJoystickBuff = DagRenderer.makeFloatBuffer(new float[] 
	      { 20f, 20f, 1.0f,
			-20f, 20f, 1.0f,
			20f, -20f, 1.0f,
			-20f, -20f, 1.0f });
	
	private final int touchMargin = 300;
	
	private final int cursorSpeed = 10;
	
	public JoystickInputDevice(PlayScene playScene, int side) 
	{
		super(playScene);
		
		this.side = side;
		screenH = Camera.Get().GetScreenHeight();
		screenW = Camera.Get().GetScreenWidth();
		
		this.deviceHandler = new Handler()
		{			
			public void handleMessage(Message msg)
			{
				if(msg.what == MsgType.TOUCH_EVENT.ordinal())
				{
					MotionEvent event = (MotionEvent)msg.obj;
					for(int i = 0; i < event.getPointerCount(); i++)
					{
						int pointerID = event.getPointerId(i);
						Vec2 newPos = new Vec2(event.getX(pointerID),event.getY(pointerID));
						
						MoveCursor(newPos);
					}
				}
			}
		};
		
		switch (this.side) 
		{
		case LEFT:
			this.mainCirclePos = new Vec2(radiusOffset,screenH - radiusOffset);
			this.smallCirclePos = new Vec2(radiusOffset,screenH - radiusOffset);
			playScene.leftJoystickEvent = this.deviceHandler;
			break;

		case RIGHT:
			this.mainCirclePos = new Vec2(screenW -  radiusOffset,radiusOffset);
			this.smallCirclePos = new Vec2(screenW -  radiusOffset,radiusOffset);
			playScene.rightJoystickEvent = this.deviceHandler;
			break;
			
		default:
			Log.e("JoystickInputDevice", "ERROR! No side selected for the Joystick!");
			break;
		}
		
		
	}

	private void MoveCursor(Vec2 pos)
	{
		// TODO: use relative positions for stuff (careful of screen resolutions)
		if(	(side == LEFT && pos.X() < touchMargin && pos.Y() < touchMargin) ||
			(side == RIGHT && pos.X() > (screenW - touchMargin) && pos.Y() > (screenH - touchMargin) ))
		{
			Vec2 dir = new Vec2(mainCirclePos.X(), screenH- mainCirclePos.Y());
			dir = dir.GetVectorTo(pos);
			// Get the cursor moving
			dir.Normalize();
			dir.SetY(-dir.Y());
			dir.Scale(cursorSpeed);
			parent.GetCursor().MoveInDirection(dir.GetCopy());	
			
			// Relocate the small circle
			
			dir.Normalize();
			dir.Scale(radius);
			this.smallCirclePos.Set(dir.X()+mainCirclePos.X(), dir.Y()+mainCirclePos.Y());
		}
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
	public Vec2 GetSmallCirclePos() { return this.smallCirclePos; }
	
	/**
	 * Gets the main texture buffer for ogl rendering
	 * @return Main texture buffer
	 */
	public static FloatBuffer GetMainTextureBuffer() { return mainJoystickBuff;}
	
	/**
	 * Gets the small texture buffer for ogl rendering
	 * @return Small texture buffer
	 */
	public static FloatBuffer GetSmallTextureBuffer() { return smallJoystickBuff; }

}
