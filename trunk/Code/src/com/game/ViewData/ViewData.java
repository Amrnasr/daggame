package com.game.ViewData;

import android.app.Activity;
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
	/**
	 * Override this function in the child class to get (or generate) the scene's 
	 * main view (usually, inflate from xml, but in some cases, create it, such as
	 * the playscene GLSurfaceView) and set the button callbacks and any other 
	 * logic dependant code.
	 * @param activity that uses this view. Needed to create a xml inflater
	 * @return the created view
	 */
	 public abstract View createXMLView(Activity activity);
}
