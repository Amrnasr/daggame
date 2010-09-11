package com.game.ViewData;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
	 
	 /**
	  * Sets the centerLinearLayout to 80% the screen width and centers it.
	  * 
	  * NOTES:
	  * - It crashes if LinearLayout.LayoutParams is used, instead of FrameLayout.LayoutParams
	  * no idea why it thinks the about_layout_inner_scroll is a FrameLayout.
	  * - Setting a LayoutParams object seems to override the default params. ALL of them, not
	  * just w & h, so gravity must be re-specified here, even tough it's ok in the xml
	  * - Had to change the AndroidManifest/Uses SDK /Target SDK & Min SDK cause it was emulating
	  * HVGA in all AVD  devices. This seems to be the default behavior if no Target & Min SDK
	  * are configured.
	  * - Confirmed it works ok in QVGA, HVGA and WVGA
	  */
	 public void Set80PercentWidth(Activity activity, LinearLayout centerLinerarLayout)
	 {
		 	Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
	        int width = display.getWidth(); 
	        
	        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)(width/1.3), FrameLayout.LayoutParams.FILL_PARENT);
	        lp.gravity = Gravity.CENTER_HORIZONTAL;
	        
	        centerLinerarLayout.setLayoutParams(lp);
	 }
}
