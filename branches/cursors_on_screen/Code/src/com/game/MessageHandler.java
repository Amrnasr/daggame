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
	
	/**
	 * Singleton unique instance of the class.
	 * Active instantation to avoid threading issues.
	 */
	private static MessageHandler instance = new MessageHandler();
	
	/**
	 * Handler for messages for the activity
	 */
	private Handler activityHandler = null;
	
	/**
	 * Handler for messages for the renderer
	 */
	private Handler rendererHandler = null;
	
	/**
	 * Handler for messages for the logic 
	 */
	private Handler logicHandler = null;	
	
	/**
	 * Private constructor to avoid multiple copies.
	 */
	private MessageHandler() {}
	
	/**
	 * Gets the global instance of the MessageHandler
	 * @return instance
	 */
	public static MessageHandler Get()
	{
		return instance;
	}
	
	/**
	 * Sets the activity handler pointer.
	 * @param activityHandler to pass messages to.
	 */
	public void SetActivityHandler( Handler activityHandler) { this.activityHandler = activityHandler; }
	
	/**
	 * Sets the renderer handler pointer
	 * @param rendererHandler to pass messages to.
	 */
	public void SetRendererHandler( Handler rendererHandler) { this.rendererHandler = rendererHandler; }
	
	/**
	 * Sets the logic handler pointer
	 * @param logicHandler to pass messages to.
	 */
	public void SetLogicHandler( Handler logicHandler) { this.logicHandler = logicHandler;}
	
	/**
	 * Send a message to a handler.
	 * @param receiver the MsgReciever entity
	 * @param type MsgType of message
	 * @param object Object to send in the message.
	 */
	public void Send(MsgReceiver receiver, MsgType type, Object object)
	{
		Send(receiver, type, 0, 0, object);
	}
	
	/**
	 * Send a message to a handler.
	 * @param receiver of the MsgReceiver entity
	 * @param type of MsgType
	 * @param arg1 user defined argument
	 */
	public void Send(MsgReceiver receiver, MsgType type, int arg1)
	{
		Send(receiver, type, arg1, 0, null);
	}
	
	/**
	 * Send a message to a handler.
	 * @param receiver of the MsgReceiver entity
	 * @param type of MsgType
	 * @param arg1 user defined argument
	 * @param arg2 user defined argument
	 */
	public void Send (MsgReceiver receiver, MsgType type, int arg1, int arg2)
	{
		Send(receiver, type, arg1, arg2, null);
	}
	
	/**
	 * Send a message to a handler.
	 * @param receiver of the MsgReceiver entity
	 * @param type of MsgType
	 */
	public void Send (MsgReceiver receiver, MsgType type)
	{
		Send(receiver, type, 0, 0, null);
	}
	
	/**
	 * Sends a message to a handler
	 * @param receiver of the MsgReceiver entity
	 * @param type of MsgType
	 * @param arg1 user defined argument
	 * @param arg2 user defined argument
	 * @param object to send to the handler.
	 */
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
