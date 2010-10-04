package com.game;

import android.os.Handler;
import android.util.Log;

/**
 * Singleton message handler, to comunicate all the systems in the game.
 * @author Ying
 *
 */
public class MessageHandler 
{
	/**
	 * List of receivers. Update to add more.
	 * @author Ying
	 *
	 */
	public enum MsgReceiver
	{
		ACTIVITY,
		RENDERER,
		LOGIC
	}
	
	private static MessageHandler instance = new MessageHandler();
	
	private Handler activityHandler = null;
	private Handler rendererHandler = null;
	private Handler logicHandler = null;	
	
	private MessageHandler()	
	{
		
	}
	
	public static MessageHandler Get()
	{
		return instance;
	}
	
	public void SetActivityHandler( Handler activityHandler) { this.activityHandler = activityHandler; }
	
	public void SetRendererHandler( Handler rendererHandler) { this.rendererHandler = rendererHandler; }
	
	public void SetLogicHandler( Handler logicHandler) { this.logicHandler = logicHandler;}
	
	public void Send(MsgReceiver receiver, MsgType type, Object object)
	{
		Send(receiver, type, 0, 0, object);
	}
	
	public void Send(MsgReceiver receiver, MsgType type, int arg1)
	{
		Send(receiver, type, arg1, 0, null);
	}
	
	public void Send (MsgReceiver receiver, MsgType type, int arg1, int arg2)
	{
		Send(receiver, type, arg1, arg2, null);
	}
	
	public void Send (MsgReceiver receiver, MsgType type)
	{
		Send(receiver, type, 0, 0, null);
	}
	
	public void Send (MsgReceiver receiver, MsgType type, int arg1, int arg2, Object object)
	{
		switch (receiver) 
		{
		case ACTIVITY:
			if(this.activityHandler != null)
			{
				this.activityHandler.sendMessage(activityHandler.obtainMessage(type.ordinal(), arg1, arg2, object));
			}
			else
			{
				Log.e("MessageHandler", "Activity handler not initialized");
			}
			break;
		case RENDERER:
			if(this.rendererHandler != null)
			{
				this.rendererHandler.sendMessage(rendererHandler.obtainMessage(type.ordinal(), arg1, arg2, object));
			}
			else
			{
				Log.e("MessageHandler", "Renderer handler not initialized");
			}
			break;
		case LOGIC:
			if(this.logicHandler != null)
			{
				this.logicHandler.sendMessage(logicHandler.obtainMessage(type.ordinal(), arg1, arg2, object));
			}
			else
			{
				Log.e("MessageHandler", "Logic handler not initialized");
			}
			break;

		default:
			Log.e("MessageHandler", "Receiver not found");
			break;
		}
	}
}
