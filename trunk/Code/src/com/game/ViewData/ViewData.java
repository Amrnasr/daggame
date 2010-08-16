package com.game.ViewData;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

/**
 * Derive from this class to create new data constructors for different game views.
 * Must have a different ViewData for each scene in the game.
 * 
 * @author Ying
 *
 */
public abstract class ViewData 
{
	protected Handler handlerRef;
	/**
	 * Override this function in the child class to get (or generate) the scene's 
	 * main view (usually, inflate from xml, but in some cases, create it, such as
	 * the playscene GLSurfaceView) and set the button callbacks and any other 
	 * logic dependant code.
	 * @param activity that uses this view. Needed to create a xml inflater
	 * @return the created view
	 */
	 public abstract View createXMLView(Activity activity);
	 
	 /**
	  * Sets the handler reference for the view data to the game logic
	  * @param handler From the game logic that receives our input messages
	  * @throws Exception If the handler we are given is null (hell could break loose if it were)
	  */
	 public void setHandlerReference(Handler handler) throws Exception
	 {
		 if(handler == null)
		 {
			 throw new Exception("Handler that we tried to set is null!!");
		 }
		 this.handlerRef = handler;
	 }
}
