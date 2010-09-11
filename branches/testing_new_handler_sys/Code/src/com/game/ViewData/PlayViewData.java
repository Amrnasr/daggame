package com.game.ViewData;

import com.game.DagGLSurfaceView;
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
		Log.i("PlayViewData", "createXMLView");
		
		// Create GLSurfaceView
		DagGLSurfaceView oglLayout = new DagGLSurfaceView(activity);  
		oglLayout.setLogicHandlerRef(handlerRef);
        
		return oglLayout;
	}

}
