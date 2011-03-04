package com.game.battleofpixels.ViewData;

import com.game.battleofpixels.DagGLSurfaceView;

import android.app.Activity;
import android.util.Log;
import android.view.View;


/**
 * Specific view data class for the "Play" screen.
 * @author Ying
 *
 */
public class PlayViewData extends ViewData 
{
	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override
	public View createXMLView(Activity activity) 
	{
		//handlerRef
		//Log.i("PlayViewData", "createXMLView");
		
		// Create GLSurfaceView
		DagGLSurfaceView oglLayout = new DagGLSurfaceView(activity);  
        
		return oglLayout;
	}

}
