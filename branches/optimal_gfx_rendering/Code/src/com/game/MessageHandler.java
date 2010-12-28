package com.game;

import java.util.Vector;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Singleton message handler, to comunicate all the systems in the game.
 * @author Ying
 * 
 * TODO: Instead of 2 switches (send, trysend), do a lookup table
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
		LOGIC,
		GLSURFACEVIEW
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
	 * Handler for the opengl surface view
	 */
	private Handler glSurfaceHandler = null;
	
	/**
	 * List of queued messages that could not be delivered because 
	 * the corresponding handler was null
	 */
	private Vector<QueuedMessage> queuedMessages;
	
	/**
	 * Private constructor to avoid multiple copies.
	 */
	private MessageHandler() 
	{
		queuedMessages = new Vector<QueuedMessage>();
	}
	
	/**
	 * Gets the global instance of the MessageHandler
	 * @return instance
	 */
	public static MessageHandler Get()
	{
		return instance;
	}
	
	/**
	 * Updates the list of queued messages, and sends any that it can.
	 */
	public void Update()
	{
		Vector<QueuedMessage> sentMsgs = null;
		for(int i = 0; i < queuedMessages.size(); i++)
		{
			boolean sent = TrySend(queuedMessages.elementAt(i));
			if(sent)
			{
				// Mark for removal
				
				// Lazy initialization
				if(sentMsgs == null) 
				{ 
					sentMsgs = new Vector<QueuedMessage>();
				}
				
				sentMsgs.add(queuedMessages.elementAt(i));				
			}
		}
		
		if(sentMsgs != null)
		{
			for(int i = 0; i < sentMsgs.size(); i++)
			{
				queuedMessages.remove(sentMsgs.elementAt(i));
			}
			
			sentMsgs.clear();
		}
	}
	
	/**
	 * Tries to send a queued message. Fails if the handler is still not set.
	 * @param msg Message we want to send
	 * @return True if it managed to send it, false otherwise
	 */
	private boolean TrySend(QueuedMessage msg) 
	{
		boolean sent = false;
		switch (msg.GetReceiver()) 
		{
		case ACTIVITY:
			if(this.activityHandler != null)
			{
				this.activityHandler.sendMessage(msg.GetMessage());
				sent = true;
			}
			break;
		case RENDERER:
			if(this.rendererHandler != null)
			{
				this.rendererHandler.sendMessage(msg.GetMessage());
				sent = true;
			}
			break;
			
		case LOGIC:
			if(this.logicHandler != null)
			{
				this.logicHandler.sendMessage(msg.GetMessage());
				sent = true;
			}
			break;
			
		case GLSURFACEVIEW:
			if(this.glSurfaceHandler != null)
			{
				this.glSurfaceHandler.sendMessage(msg.GetMessage());
				sent = true;
			}
			break;

		default:
			Log.e("MessageHandler", "Receiver not found");
			break;
		}
		return sent;
	}
	
	/**
	 * Queues a message that could not get sent because the handler wasn't ready.
	 * @param type Type of the message
	 * @param arg1 User defined arg1
	 * @param arg2 User defined arg2
	 * @param object User defined data object
	 * @param receiver MsgReceiver of who this message is for
	 */
	private void QueueMessage(MsgType type, int arg1, int arg2, Object object, MsgReceiver receiver)
	{
		// Queue the message
		Message aux = new Message();
		aux.what = type.ordinal();
		aux.arg1 = arg1;
		aux.arg2 = arg2;
		aux.obj = object;
		
		queuedMessages.add(new QueuedMessage(aux, receiver));
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
	 * Sets the gl surface handler pointer
	 * @param glSurfaceHandler to pass messages to.
	 */
	public void SetGLSurfaceHandler( Handler glSurfaceHandler) { this.glSurfaceHandler = glSurfaceHandler;}
	
	
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
				// Queue
				Log.e("MessageHandler", "Activity handler not initialized");
				QueueMessage(type, arg1, arg2, object, receiver);
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
				QueueMessage(type, arg1, arg2, object, receiver);
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
				QueueMessage(type, arg1, arg2, object, receiver);
			}
			break;
		case GLSURFACEVIEW:
			if(this.glSurfaceHandler != null)
			{
				this.glSurfaceHandler.sendMessage(glSurfaceHandler.obtainMessage(type.ordinal(), arg1, arg2, object));
			}
			else
			{
				Log.e("MessageHandler", "GL SurfaceHandler handler not initialized");
				QueueMessage(type, arg1, arg2, object, receiver);
			}
			break;

		default:
			Log.e("MessageHandler", "Receiver not found");
			break;
		}
	}
}
