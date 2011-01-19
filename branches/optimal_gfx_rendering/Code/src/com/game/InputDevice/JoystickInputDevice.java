package com.game.InputDevice;

import java.nio.FloatBuffer;

import android.bluetooth.BluetoothClass.Device;
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
	private Vec2 dirCirclePos;
	
	// Half of the big texture width
	private final int radiusOffset = 32;
	
	/** Buffer for the big joystick texture in ogl **/
	private static final FloatBuffer mainJoystickBuff = DagRenderer.makeFloatBuffer(new float[] 
	      { 32f, 32f, 1.0f,
			-32f, 32f, 1.0f,
			32f, -32f, 1.0f,
			-32f, -32f, 1.0f });
	
	/** Buffer for the small joystick texture in ogl **/
	private static final FloatBuffer smallJoystickBuff = DagRenderer.makeFloatBuffer(new float[] 
	      { 15f, 15f, 1.0f,
			-15f, 15f, 1.0f,
			15f, -15f, 1.0f,
			-15f, -15f, 1.0f });
	
	public JoystickInputDevice(PlayScene playScene, int side) 
	{
		super(playScene);
		
		this.side = side;
		int screenH = Camera.Get().GetScreenHeight();
		int screenW = Camera.Get().GetScreenWidth();
		
		switch (this.side) 
		{
		case LEFT:
			this.mainCirclePos = new Vec2(radiusOffset,screenH - radiusOffset);
			this.dirCirclePos = new Vec2(radiusOffset,screenH - radiusOffset);
			break;

		case RIGHT:
			this.mainCirclePos = new Vec2(screenW -  radiusOffset,radiusOffset);
			this.dirCirclePos = new Vec2(screenW -  radiusOffset,radiusOffset);
			break;
			
		default:
			Log.e("JoystickInputDevice", "ERROR! No side selected for the Joystick!");
			break;
		}
		
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
	public Vec2 GetSmallCirclePos() { return this.dirCirclePos; }
	
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
