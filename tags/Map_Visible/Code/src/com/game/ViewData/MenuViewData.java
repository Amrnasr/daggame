package com.game.ViewData;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.game.MsgType;
import com.game.R;

/**
 * ViewData for the Menu scene.
 * 
 * @author Ying
 *
 */
public class MenuViewData extends ViewData 
{
	/**
	 * @see ViewData createXMLView(Activity activity) 
	 */
	@Override public View createXMLView(Activity activity) 
	{
		Log.i("MenuViewData", "Got here!");
		// Access xml layout
		LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View xmlLayout = (View) li.inflate(R.layout.menu, null);
        
        // Create button callbacks. 
        // It's important to create them from the xmlLayout object, not the activity, as
        // the activity has no layout assigned yet.
        Button singleButton = (Button) xmlLayout.findViewById(R.id.single_but);
        singleButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", " Clicked Single button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.single_but, 0));
          }
        });
        
        Button multiButton = (Button) xmlLayout.findViewById(R.id.multi_but);
        multiButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", " Clicked Multiplayer button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.multi_but, 0));
          }
        });
        
        Button optionsButton = (Button) xmlLayout.findViewById(R.id.options_but);
        optionsButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", " Clicked Options button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.options_but, 0));
          }
        });
        
        Button howButton = (Button) xmlLayout.findViewById(R.id.how_but);
        howButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", " Clicked How to play button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.how_but, 0));
          }
        });
        
        Button aboutButton = (Button) xmlLayout.findViewById(R.id.about_but);
        aboutButton.setOnClickListener(new OnClickListener() 
        {
          @Override
          public void onClick(View v) {
            Log.i("ViewData", " Clicked About us button");
            handlerRef.sendMessage(handlerRef.obtainMessage(MsgType.BUTTON_CLICK.ordinal(), R.id.about_but, 0));
          }
        });
        
		return xmlLayout;
	}

}
