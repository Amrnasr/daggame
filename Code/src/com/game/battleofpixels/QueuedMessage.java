package com.game.battleofpixels;

import com.game.battleofpixels.MessageHandler.MsgReceiver;

import android.os.Message;

/**
 * Wrapper class for android.os.Messages that get queued in the MessageManager
 * @author Ying
 *
 */
public class QueuedMessage 
{
	/**
	 * Message to queue
	 */
	private Message message;
	
	/**
	 * Receiver for the message
	 */
	private MsgReceiver receiver;
	
	/**
	 * Creates a new instance of the QueuedMessage clas
	 * @param message Message to queue
	 * @param reciever Receiver of the msg message
	 */
	public QueuedMessage(Message message, MsgReceiver reciever)
	{
		this.message = message;
		this.receiver = reciever;
	}
	
	/**
	 * Gets the message
	 * @return the message stored
	 */
	public Message GetMessage() { return this.message; }
	
	/**
	 * Gets the receiver
	 * @return the receiver of the stored message
	 */
	public MsgReceiver GetReceiver() { return this.receiver; }

}
