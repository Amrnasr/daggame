package com.game.ViewData;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.game.R;

/**
 * ViewData for the Menu scene.
 * 
 * @author Ying
 *
 */
public class MenuViewData extends ViewData 
{
	
	@Override public View createXMLView(Activity activity) 
	{
		Log.i("MenuViewData", "Got here!");
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.menu, null);
        
        // Create button callbacks. 
        // It's important to create them from the xmlLayout object, not the activity, as
        // the activity has no layout assigned yet.
        // TODO: Add some real functionality to the buttons, probably with a event system.
        Button singleButton = (Button) xmlLayout.findViewById(R.id.single_but);
        singleButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("View Data", " Clicked Single button");
          }
        });
        
        Button multiButton = (Button) xmlLayout.findViewById(R.id.multi_but);
        multiButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("View Data", " Clicked Multiplayer button");
          }
        });
        
        Button optionsButton = (Button) xmlLayout.findViewById(R.id.options_but);
        optionsButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("View Data", " Clicked Options button");
          }
        });
        
        Button howButton = (Button) xmlLayout.findViewById(R.id.how_but);
        howButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("View Data", " Clicked How to play button");
          }
        });
        
        Button aboutButton = (Button) xmlLayout.findViewById(R.id.about_but);
        aboutButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("View Data", " Clicked About us button");
          }
        });
        
		return xmlLayout;
	}

}
