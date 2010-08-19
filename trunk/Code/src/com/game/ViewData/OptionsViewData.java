package com.game.ViewData;


import com.game.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Specific view data class for the "Options" screen.
 * @author Ying
 *
 */
public class OptionsViewData extends ViewData 
{

	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override
	public View createXMLView(Activity activity) 
	{
		Log.i("OptionsViewData", "createXMLView");
		
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.options, null);
        
        // Callback for the buttons & views
       
        
		return xmlLayout;
	}

}
