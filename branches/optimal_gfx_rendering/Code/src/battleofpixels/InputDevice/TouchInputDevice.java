package com.game.battleofpixels.InputDevice;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.game.battleofpixels.Camera;
import com.game.battleofpixels.MessageHandler;
import com.game.battleofpixels.MsgType;
import com.game.battleofpixels.Preferences;
import com.game.battleofpixels.Vec2;
import com.game.battleofpixels.MessageHandler.MsgReceiver;
import com.game.battleofpixels.Scenes.PlayScene;

/**
 * Handles the input of a player using the touch screen
 * @author Ying
 *
 */
public class TouchInputDevice extends InputDevice 
{
	
	private boolean borderTouch;
	private Vec2 offsetVec;
	private final int offset = 50;

	
	
	/**
	 * Creates the handler and links it to the appropriate one in PlayScene
	 * @param playScene to listen in
	 */
	public TouchInputDevice(PlayScene playScene) 
	{
		super(playScene);
		
		this.borderTouch = false;
		this.offsetVec = new Vec2();
		
		
		this.deviceHandler = new Handler()
		{			
			public void handleMessage(Message msg)
			{
				if(msg.what == MsgType.TOUCH_EVENT.ordinal())
				{
					MotionEvent event = (MotionEvent)msg.obj;
					Vec2 newPos = new Vec2(event.getX(),event.getY());
					//newPos.Print("TouchInputDevice", "TouchPos");
					
					if(Preferences.Get().multiplayerGame)
					{
						HandleBorderTouch(newPos);
						offsetVec.Print("TouchInputDevice", "OffsetPo");
					}
					
					MessageHandler.Get().Send(MsgReceiver.RENDERER, MsgType.REQUEST_WCS_TRANSFORM, newPos);

					
				}
				if(msg.what == MsgType.REPLY_WCS_TRANSFORM_REQUEST.ordinal())
				{
					// Add some padding
					Vec2 dest = (Vec2)msg.obj;
					//dest.Print("TouchInmputDevice", "1 - Requested coords");
					/*
					if(dest.X() > parent.GetCursor().GetPosition().X())
					{
						dest.Offset(movePadding, 0);
					}
					else if(dest.X() < parent.GetCursor().GetPosition().X())
					{
						dest.Offset(-movePadding, 0);
					}
					
					if(dest.Y() > parent.GetCursor().GetPosition().Y())
					{
						dest.Offset(0, movePadding);
					}
					else if(dest.Y() < parent.GetCursor().GetPosition().Y())
					{
						dest.Offset(0, -movePadding);
					}
					*/
					if(borderTouch)
					{
						dest.Offset(offsetVec.X(), offsetVec.Y());
						borderTouch = false;
					}
					
					parent.GetCursor().MoveTo(dest);
				}
			}
		};
		
		if(playScene.touchEvent != null)
		{
			Log.e("TouchInputDevice", "Touch handler is not null! Some other ID has already claimed it");
		}
		playScene.touchEvent = this.deviceHandler;
	}

	private void HandleBorderTouch(Vec2 pos)
	{
		int screenH = Camera.Get().GetScreenHeight();
		int screenW = Camera.Get().GetScreenWidth();
		int margin = screenW / 5;
		this.offsetVec.Set(0, 0);
		
		if(pos.X() < margin)
		{
			this.offsetVec.Offset(-offset, 0);
		}
		else if(pos.X() > (screenW -margin))
		{
			this.offsetVec.Offset(offset, 0);
		}
		if(pos.Y() < margin)
		{
			this.offsetVec.Offset(0, offset);
		}
		else if(pos.Y() > (screenH - margin))
		{
			this.offsetVec.Offset(0, -offset);
		}
		this.borderTouch = true;
	}
	
	@Override
	public void Start() {

	}

	@Override
	public void Update() {

	}

}
